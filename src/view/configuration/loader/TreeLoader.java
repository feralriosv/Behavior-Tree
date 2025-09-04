package view.configuration.loader;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Node;
import model.decisiontree.node.Naming;
import view.NodeFabric;
import view.NodeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Loads a behavior/decision tree from Mermaid "flowchart TD" text.
 *
 * @author ubpst
 */
public class TreeLoader implements Loader<DecisionTree> {

    private static final String ARROW_SYMBOL = "-->";

    private static final String ERROR_CONFLICTING_LABELS = "node '%s' has conflicting labels: '%s' vs '%s'";
    private static final String ERROR_UNDEFINED_NODE_IN_EDGE = "undefined node in edge";
    private static final String ERROR_CANNOT_ADD_CHILD = "cannot add child to leaf '%s'";
    private static final String ERROR_MULTIPLE_ROOTS = "multiple roots detected";
    private static final String ERROR_NO_ROOT = "no root found";
    private static final String ERROR_INVALID_BLOCK_LINE = "blockline %s is not valid";
    private static final String ERROR_NO_INFORMATION = "there is no information";
    private static final String ERROR_MISSING_HEADER = "missing mermaid syntax header";
    private static final String ERROR_EMPTY_TREE = "empty behavior-tree block";
    private static final String ERROR_NODE_WITHOUT_DEFINITION = "node '%s' referenced without definition";
    private static final String ERROR_NO_ACTION_NODE = "tree must contain at least one action node";

    private static final String MERMAID_SYNTAX_HEADER = "flowchart TD";

    private final Map<Naming, String> nodeDefinitions;
    private final List<Naming[]> edges;
    private final Set<Naming> referencedIds;
    private final Map<Naming, Node<?>> createdNodes;
    private final Set<Naming> childrenNodeIds;
    private boolean actionCreated;
    private Naming rootNaming;

    /**
     * Creates a new {@code TreeLoader} with default factories.
     */
    public TreeLoader() {
        this.nodeDefinitions = new HashMap<>();
        this.edges = new ArrayList<>();
        this.referencedIds = new HashSet<>();
        this.createdNodes = new HashMap<>();
        this.childrenNodeIds = new HashSet<>();
        this.actionCreated = false;
        this.rootNaming = null;
    }

    @Override
    public DecisionTree load(List<String> lines) throws LoadingException {
        if (lines == null || lines.isEmpty()) {
            throw new LoadingException(ERROR_NO_INFORMATION);
        }

        if (!lines.getFirst().trim().equals(MERMAID_SYNTAX_HEADER)) {
            throw new LoadingException(ERROR_MISSING_HEADER);
        }

        this.resetState();

        this.processLinesContent(lines);
        return new DecisionTree(createdNodes.get(this.rootNaming));
    }

    /**
     * Marks that at least one action node was created during the current load cycle.
     */
    public void markCreatedAction() {
        this.actionCreated = true;
    }

    private void processLinesContent(List<String> lines) throws LoadingException {
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = extractLineParts(line);
            if (parts.length == 2) {
                processPair(parts);
            } else if (parts.length == 1) {
                processSingle(parts[0]);
            } else {
                throw new LoadingException(ERROR_INVALID_BLOCK_LINE.formatted(line));
            }
        }

        if (this.nodeDefinitions.isEmpty()) {
            throw new LoadingException(ERROR_EMPTY_TREE);
        }

        this.arrangeNodes();
    }

    private void arrangeNodes() throws LoadingException {
        for (Map.Entry<Naming, String> entry : nodeDefinitions.entrySet()) {
            Naming nodeName = entry.getKey();
            String label = entry.getValue();

            NodeFabric nodeFabric = new NodeFabric(this);
            Node<?> node = nodeFabric.createNode(nodeName, label);

            createdNodes.put(nodeName, node);
        }

        for (Naming nodeName : this.referencedIds) {
            if (!createdNodes.containsKey(nodeName)) {
                throw new LoadingException(ERROR_NODE_WITHOUT_DEFINITION.formatted(nodeName));
            }
        }

        for (Naming[] edge : this.edges) {
            this.childrenNodeIds.add(edge[1]);
        }

        this.rootNaming = findRootName();

        for (Naming[] edge : this.edges) {
            Naming parentId = edge[0];
            Naming childId  = edge[1];

            Node<?> parent = createdNodes.get(parentId);
            Node<?> child  = createdNodes.get(childId);

            if (parent == null || child == null) {
                throw new LoadingException(ERROR_UNDEFINED_NODE_IN_EDGE);
            }

            boolean success = parent.addChild(child);
            if (!success) {
                throw new LoadingException(ERROR_CANNOT_ADD_CHILD.formatted(parent.getNaming()));
            }
        }

        if (!actionCreated) {
            throw new LoadingException(ERROR_NO_ACTION_NODE);
        }
    }

    private Naming findRootName() throws LoadingException {
        for (Naming naming : this.createdNodes.keySet()) {
            if (!this.childrenNodeIds.contains(naming)) {
                if (rootNaming != null) {
                    throw new LoadingException(ERROR_MULTIPLE_ROOTS);
                }
                rootNaming = naming;
            }
        }

        if (rootNaming == null) {
            throw new LoadingException(ERROR_NO_ROOT);
        }

        return rootNaming;
    }

    private void processPair(String[] pair) throws LoadingException {
        Optional<NodeToken> leftTokenOpt  = NodeToken.fromLine(pair[0].trim());
        Optional<NodeToken> rightTokenOpt = NodeToken.fromLine(pair[1].trim());

        if (leftTokenOpt.isEmpty() || rightTokenOpt.isEmpty()) {
            throw new LoadingException("invalid tokens" + pair[0] + " " + ARROW_SYMBOL + " " + pair[1]);
        }

        Naming leftId  = new Naming(leftTokenOpt.get().name());
        Naming rightId = new Naming(rightTokenOpt.get().name());

        processToken(leftTokenOpt.get());
        processToken(rightTokenOpt.get());

        this.edges.add(new Naming[]{ leftId, rightId });
    }

    private void processSingle(String rawToken) throws LoadingException {
        Optional<NodeToken> tokenOpt = NodeToken.fromLine(rawToken);
        if (tokenOpt.isEmpty()) {
            throw new LoadingException("invalid token: " + rawToken);
        }

        this.processToken(tokenOpt.get());
    }

    private void processToken(NodeToken token) throws LoadingException {
        Naming nodeName = new Naming(token.name());
        this.referencedIds.add(nodeName);

        String label = token.label().trim();
        if (!label.isEmpty()) {
            String prev = nodeDefinitions.putIfAbsent(nodeName, label);
            if (prev != null && !prev.equals(label)) {
                throw new LoadingException(ERROR_CONFLICTING_LABELS.formatted(token.name(), prev, label));
            }
        }
    }

    private String[] extractLineParts(String line) throws LoadingException {
        String trimmed = line.isEmpty() ? "" : line.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }

        if (trimmed.contains(ARROW_SYMBOL)) {
            String[] parts = trimmed.split(ARROW_SYMBOL);
            if (parts.length != 2) {
                throw new LoadingException(ERROR_INVALID_BLOCK_LINE.formatted(line));
            }

            parts[0] = parts[0].trim();
            parts[1] = parts[1].trim();
            return parts;
        }
        return new String[]{ trimmed };
    }

    private void resetState() {
        this.nodeDefinitions.clear();
        this.edges.clear();
        this.referencedIds.clear();
        this.createdNodes.clear();
        this.childrenNodeIds.clear();
        this.actionCreated = false;
        this.rootNaming = null;
    }
}
