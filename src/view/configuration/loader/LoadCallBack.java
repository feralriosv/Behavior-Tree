package view.configuration.loader;

/**
 * Interface for callback methods used during the loading process.
 *
 * @author ubpst
 */
public interface LoadCallBack {
    /**
     * Marks that at least one action node was created during the current load cycle.
     */
    void markCreatedAction();
}
