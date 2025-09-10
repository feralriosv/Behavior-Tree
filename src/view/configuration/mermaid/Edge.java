package view.configuration.mermaid;

import model.node.NodeNaming;

/**
 * Represents a directed edge in a Mermaid-style decision tree diagram.
 *
 * @param from the starting node of the edge
 * @param to the ending node of the edge
 * @author ubpst
 */
public record Edge(NodeNaming from, NodeNaming to) {
}
