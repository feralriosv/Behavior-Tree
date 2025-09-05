package view.configuration.mermaid;

import model.decisiontree.node.Naming;
import view.NodeToken;
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
 *
 * <p>Parses lines of a Mermaid diagram into a {@link MermaidData} object
 * containing node definitions, edges, and references.
 *
 * @author ubpst
 */
public class MermaidLoader implements Loader<MermaidData> {

    private static final String MERMAID_EDGE_SYMBOL = "-->";
    private static final String MERMAID_HEADER = "flowchart TD";

    private final Map<Naming, String> nodeDefinitions = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Set<Naming> references = new HashSet<>();

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

        return new MermaidData(nodeDefinitions, edges, references);
    }

    private boolean processPair(String[] parts) {
        Optional<NodeToken> leftTokenOpt  = NodeToken.fromLine(parts[0].trim());
        Optional<NodeToken> rightTokenOpt = NodeToken.fromLine(parts[1].trim());

        if (leftTokenOpt.isEmpty() || rightTokenOpt.isEmpty()) {
            return false;
        }

        NodeToken[] tokens = { leftTokenOpt.get(), rightTokenOpt.get() };

        for (NodeToken token : tokens) {
            Naming naming = new Naming(token.name());
            references.add(naming);

            if (!putLabel(nodeDefinitions, naming, token.label())) {
                return false;
            }
        }

        edges.add(new Edge(new Naming(tokens[0].name()), new Naming(tokens[1].name())));
        return true;
    }

    private boolean processSingle(String part) {
        Optional<NodeToken> tokenOpt = NodeToken.fromLine(part.trim());
        if (tokenOpt.isEmpty()) {
            return false;
        }

        Naming id = new Naming(tokenOpt.get().name());
        this.references.add(id);
        return putLabel(nodeDefinitions, id, tokenOpt.get().label());
    }

    private static boolean putLabel(Map<Naming, String> defs, Naming id, String rawLabel) {
        String label = rawLabel == null ? "" : rawLabel.trim();
        if (label.isEmpty()) {
            return true;
        }

        String prev = defs.putIfAbsent(id, label);
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
        this.edges.clear();
        this.references.clear();
    }
}
