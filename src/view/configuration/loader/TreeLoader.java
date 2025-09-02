package view.configuration.loader;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Node;
import model.decisiontree.node.Naming;
import view.configuration.factory.CompositeNodeFactory;
import view.configuration.factory.LeafNodeFactory;
import view.configuration.factory.NodeFactory;

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
    private static final String BRACKET_OPEN_SYMBOL = "[";
    private static final String BRACKET_CLOSE_SYMBOL = "]";
    private static final String PAREN_OPEN_SYMBOL = "(";
    private static final String PAREN_CLOSE_SYMBOL = ")";

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
    private static final String ERROR_UNKNOWN_LABEL = "unknown node label: %s";

    private static final String MERMAID_SYNTAX_HEADER = "flowchart TD";

    private final List<NodeFactory> factories;
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
        this.factories = List.of(new CompositeNodeFactory(), new LeafNodeFactory(this));
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
                processPairs(parts);
            } else if (parts.length == 1) {
                processToken(parts[0]);
            } else {
                throw new LoadingException(ERROR_INVALID_BLOCK_LINE.formatted(line));
            }
        }

        if (this.nodeDefinitions.isEmpty()) {
            throw new LoadingException(ERROR_EMPTY_TREE);
        }

        arrangeNodes();
    }

    private void arrangeNodes() throws LoadingException {
        for (Map.Entry<Naming, String> entry : nodeDefinitions.entrySet()) {
            Naming nodeName = entry.getKey();
            String label = entry.getValue();
            createdNodes.put(nodeName, createNode(nodeName, label));
        }

        for (Naming nodeName : this.referencedIds) {
            if (!createdNodes.containsKey(nodeName)) {
                throw new LoadingException(ERROR_NODE_WITHOUT_DEFINITION.formatted(nodeName));
            }
        }

        for (Naming[] edge : this.edges) {
            childrenNodeIds.add(edge[1]);
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
        for (Naming id : this.createdNodes.keySet()) {
            if (!this.childrenNodeIds.contains(id)) {
                if (rootNaming != null) {
                    throw new LoadingException(ERROR_MULTIPLE_ROOTS);
                }
                rootNaming = id;
            }
        }

        if (rootNaming == null) {
            throw new LoadingException(ERROR_NO_ROOT);
        }

        return rootNaming;
    }

    private void processPairs(String[] parts) throws LoadingException {
        Token leftTok  = parseToken(parts[0].trim());
        Token rightTok = parseToken(parts[1].trim());

        Naming leftId  = new Naming(leftTok.name);
        Naming rightId = new Naming(rightTok.name);

        processParsedToken(leftTok);
        processParsedToken(rightTok);

        this.edges.add(new Naming[]{ leftId, rightId });
    }

    private void processToken(String rawToken) throws LoadingException {
        Token token = parseToken(rawToken);
        processParsedToken(token);
    }

    private void processParsedToken(Token token) throws LoadingException {
        Naming nodeName = new Naming(token.name);
        this.referencedIds.add(nodeName);

        String label = token.label;
        if (label != null) {
            String prev = nodeDefinitions.putIfAbsent(nodeName, label);
            if (prev != null && !prev.equals(label)) {
                throw new LoadingException(ERROR_CONFLICTING_LABELS.formatted(token.name, prev, label));
            }
        }
    }

    private Node<?> createNode(Naming nodeName, String label) throws LoadingException {
        for (NodeFactory factory : factories) {
            Optional<? extends Node<?>> maybe = factory.create(nodeName, label);
            if (maybe.isPresent()) {
                return maybe.get();
            }
        }
        throw new LoadingException(ERROR_UNKNOWN_LABEL.formatted(label));
    }

    private String[] extractLineParts(String line) throws LoadingException {
        String trimmed = line == null ? "" : line.trim();
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

    private Token parseToken(String line) throws LoadingException {
        String cleaned = line.trim().replace(PAREN_OPEN_SYMBOL, "").replace(PAREN_CLOSE_SYMBOL, "");
        int openIndex = cleaned.indexOf(BRACKET_OPEN_SYMBOL);
        int closeIndex = cleaned.lastIndexOf(BRACKET_CLOSE_SYMBOL);

        if (openIndex == -1 || closeIndex == -1) {
            if (cleaned.isEmpty()) {
                throw new LoadingException("empty node value in token: " + line);
            }
            return new Token(cleaned, null);
        }

        String id = cleaned.substring(0, openIndex).trim();
        String label = cleaned.substring(openIndex + 1, closeIndex).trim();

        if (id.isEmpty() || label.isEmpty()) {
            throw new LoadingException("invalid node token: " + line);
        }

        return new Token(id, label);
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

    private record Token(String name, String label) {

    }
}
