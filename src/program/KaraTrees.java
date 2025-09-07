package program;

import model.GameRunner;
import view.Arguments;
import view.CLILadybugClient;
import view.ConsoleIORessources;
import view.IORessources;

/**
 * The class offering the entry point for the application.
 *
 * @author ubpst
 * @author Programmieren-Team
 */
public final class KaraTrees {

    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_TOO_MANY_ARGUMENTS = ERROR_PREFIX + "too many arguments";

    private KaraTrees() {
        // Utility Class
    }

    /**
     * The entry point for the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Arguments argumentHolder = new Arguments(args);

        if (!argumentHolder.isExhausted()) {
            System.err.println(ERROR_TOO_MANY_ARGUMENTS);
            return;
        }

        IORessources ioRessources = new ConsoleIORessources(System.in, System.out, System.err);

        try (CLILadybugClient client = new CLILadybugClient(ioRessources)) {
            GameRunner runner = new GameRunner(client);
            runner.start();
        }
    }
}
