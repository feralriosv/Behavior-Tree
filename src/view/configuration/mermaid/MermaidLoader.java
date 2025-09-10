package view.configuration.mermaid;

import model.decisiontree.NodeNaming;
import view.configuration.loader.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Loader for Mermaid flowchart definitions.
 * <p>
 * Parses lines of a Mermaid diagram into a {@link MermaidData} object
 * containing node definitions, edges, and references.
 *
 * @author ubpst
 */
public class MermaidLoader implements Loader<MermaidData> {

    private static final String MERMAID_EDGE_SYMBOL = "-->";
    private static final String MERMAID_HEADER = "flowchart TD";

    private final Map<NodeNaming, String> nodeDefinitions = new HashMap<>();
    private final List<Edge> edgesFound = new ArrayList<>();
    private final Set<NodeNaming> references = new HashSet<>();

    @Override
    public MermaidData load(List<String> lines) {
        this.resetLoader();

        if (lines == null || lines.isEmpty()) {
            return MermaidData.invalidData();
        }

        if (!lines.getFirst().trim().equals(MERMAID_HEADER)) {
            return MermaidData.invalidData();
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = splitLine(line);
            if (parts == null) {
                return MermaidData.invalidData();
            }

            boolean ok = parts.length == 2
                    ? processPair(parts)
                    : processSingle(parts[0]);

            if (!ok) {
                return MermaidData.invalidData();
            }
        }

        return new MermaidData(nodeDefinitions, edgesFound, references);
    }

    private boolean processPair(String[] parts) {
        Optional<NodeToken> leftTokenOpt  = NodeToken.fromLine(parts[0].trim());
        Optional<NodeToken> rightTokenOpt = NodeToken.fromLine(parts[1].trim());

        if (leftTokenOpt.isEmpty() || rightTokenOpt.isEmpty()) {
            return false;
        }

        NodeToken[] tokens = { leftTokenOpt.get(), rightTokenOpt.get() };

        for (NodeToken token : tokens) {
            NodeNaming nodeNaming = new NodeNaming(token.name());
            this.references.add(nodeNaming);

            if (!putLabel(nodeNaming, token.label())) {
                return false;
            }
        }

        Edge edge = new Edge(new NodeNaming(tokens[0].name()), new NodeNaming(tokens[1].name()));
        this.edgesFound.add(edge);
        return true;
    }

    private boolean processSingle(String part) {
        Optional<NodeToken> tokenOpt = NodeToken.fromLine(part.trim());
        if (tokenOpt.isEmpty()) {
            return false;
        }

        NodeNaming id = new NodeNaming(tokenOpt.get().name());
        this.references.add(id);
        return putLabel(id, tokenOpt.get().label());
    }

    private boolean putLabel(NodeNaming id, String rawLabel) {
        String label = rawLabel == null ? "" : rawLabel.trim();
        if (label.isEmpty()) {
            return true;
        }

        String prev = nodeDefinitions.putIfAbsent(id, label);
        return prev == null || prev.equals(label);
    }

    private static String[] splitLine(String line) {
        if (line.isEmpty()) {
            return new String[0];
        }
        if (!line.contains(MERMAID_EDGE_SYMBOL)) {
            return new String[]{ line };
        }
        String[] parts = line.split(MERMAID_EDGE_SYMBOL);

        if (parts.length != 2) {
            return null;
        }

        return new String[]{ parts[0].trim(), parts[1].trim() };
    }

    private void resetLoader() {
        this.nodeDefinitions.clear();
        this.edgesFound.clear();
        this.references.clear();
    }
}
