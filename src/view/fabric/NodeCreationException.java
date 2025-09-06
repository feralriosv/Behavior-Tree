package view.fabric;

import model.decisiontree.Naming;

/**
 * Exception thrown when a node with a specific naming and label cannot be created.
 *
 * @author ubpst
 */
public class NodeCreationException extends Exception {

    private static final String ERROR_MESSAGE_FORMAT = "node %s %s could not be created";

  /**
   * Constructs a new NodeCreationException with a detailed error message.
   *
   * @param naming the naming of the node that could not be created
   * @param label the label of the node that could not be created
   */
    public NodeCreationException(Naming naming, String label) {
        super(ERROR_MESSAGE_FORMAT.formatted(naming, label));
    }
}
