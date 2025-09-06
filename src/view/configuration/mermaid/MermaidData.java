package view.configuration.mermaid;

import model.decisiontree.Naming;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable container for parsed Mermaid tree data.
 * <p>
 * This class represents the definitions of nodes, their edges, and all referenced IDs
 * extracted from a Mermaid flowchart input.
 * @author ubpst
 */
public final class MermaidData {
    private final Map<Naming, String> nodeDefinitions;
    private final List<Edge> edges;
    private final Set<Naming> referencedIds;
    private final boolean invalid;

    /**
     * Creates a new {@code MermaidData} instance representing valid parsed data.
     *
     * @param nodeDefinitions map of node IDs ({@link Naming}) to their labels
     * @param edges           list of directed edges between nodes
     * @param referencedIds   set of all node IDs referenced in the tree
     */
    public MermaidData(Map<Naming, String> nodeDefinitions, List<Edge> edges, Set<Naming> referencedIds) {
        this.edges = Collections.unmodifiableList(edges);
        this.nodeDefinitions = Collections.unmodifiableMap(nodeDefinitions);
        this.referencedIds = Collections.unmodifiableSet(referencedIds);
        this.invalid = false;
    }

    private MermaidData(Map<Naming, String> nodeDefinitions, List<Edge> edges, Set<Naming> referencedIds, boolean invalid) {
        this.edges = Collections.unmodifiableList(edges);
        this.nodeDefinitions = Collections.unmodifiableMap(nodeDefinitions);
        this.referencedIds = Collections.unmodifiableSet(referencedIds);
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
     * Returns the mapping of node IDs to labels.
     *
     * @return unmodifiable map from {@link Naming} to node label
     */
    public Map<Naming, String> getNodeDefinitions() {
        return this.nodeDefinitions;
    }

    /**
     * Returns all node IDs that were referenced in the Mermaid diagram.
     *
     * @return unmodifiable set of {@link Naming}
     */
    public Set<Naming> getReferencedIds() {
        return this.referencedIds;
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
