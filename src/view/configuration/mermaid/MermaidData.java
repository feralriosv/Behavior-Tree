package view.configuration.mermaid;

import model.decisiontree.NodeNaming;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable container for parsed Mermaid diagram data.
 * <p>
 * This class encapsulates the structural information extracted from a Mermaid flowchart,
 * including node definitions, directed edges, and all referenced node identifiers.
 *
 * @author ubpst
 */
public final class MermaidData {
    private final Map<NodeNaming, String> nodeDefinitions;
    private final List<Edge> edges;
    private final Set<NodeNaming> namingsFound;
    private final boolean invalid;

    /**
     * Creates a new {@code MermaidData} instance representing valid parsed data.
     *
     * @param nodeDefinitions map of node IDs ({@link NodeNaming}) to their labels
     * @param edges           list of directed edges between nodes
     * @param nodeNamings   set of all node namings referenced in the tree
     */
    public MermaidData(Map<NodeNaming, String> nodeDefinitions, List<Edge> edges, Set<NodeNaming> nodeNamings) {
        this.edges = Collections.unmodifiableList(edges);
        this.nodeDefinitions = Collections.unmodifiableMap(nodeDefinitions);
        this.namingsFound = Collections.unmodifiableSet(nodeNamings);
        this.invalid = false;
    }

    private MermaidData(Map<NodeNaming, String> nodeDefinitions, List<Edge> edges, Set<NodeNaming> referencedIds, boolean invalid) {
        this.edges = Collections.unmodifiableList(edges);
        this.nodeDefinitions = Collections.unmodifiableMap(nodeDefinitions);
        this.namingsFound = Collections.unmodifiableSet(referencedIds);
        this.invalid = invalid;
    }

    /**
     * Returns all edges parsed from the Mermaid diagram.
     *
     * @return unmodifiable list of {@link Edge} objects
     */
    public List<Edge> getEdges() {
        return this.edges;
    }

    /**
     * Returns a read-only view of the node definitions in this Mermaid diagram.
     *
     * @return an unmodifiable set of map entries representing node IDs and their labels
     */
    public Set<Map.Entry<NodeNaming, String>> getNodeDefinitions() {
        return this.nodeDefinitions.entrySet();
    }

    /**
     * Returns all node IDs that were referenced in the Mermaid diagram.
     *
     * @return unmodifiable set of {@link NodeNaming}
     */
    public Set<NodeNaming> getNamingsFound() {
        return this.namingsFound;
    }

    /**
     * Factory method for representing invalid tree information.
     *
     * @return an empty {@code TreeInformation} instance indicating invalid state
     */
    public static MermaidData invalidData() {
        return new MermaidData(Map.of(), List.of(), Set.of(), true);
    }

    /**
     * Indicates whether this {@code MermaidData} instance represents invalid data.
     *
     * @return {@code true} if the data is invalid, {@code false} otherwise
     */
    public boolean isInvalid() {
        return this.invalid;
    }
}
