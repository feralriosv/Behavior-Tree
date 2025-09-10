/*
 * Copyright (c) 2025, KASTEL. All rights reserved.
 */
package view;

import model.Game;
import view.configuration.Configuration;
import view.configuration.SetupExecuter;

/**
 * This class provides the setup for the game and its final creation.
 *
 * @author Programmieren-Team
 * @author ubpst
 */
public class GameFactory {

    private final ConsoleIORessources ioRessources;

    /**
     * Creates a new factory instance.
     *
     * @param ioRessources an executer containing the communication ressources to be used
     */
    public GameFactory(ConsoleIORessources ioRessources) {
        this.ioRessources = ioRessources;
    }

    /**
     * Provides the players with the dialog for the setup of the game.
     *
     * @return a game instance when the game could be constructed, {@code null} if not.
     */
    protected Game createGame() {
        SetupExecuter<Configuration, ?> setupHandler = SetupExecuter.createSetup(this.ioRessources);
        setupHandler.handleUserInput();

        if (!setupHandler.isRunning()) {
            return null;
        }

        Configuration gameConfig = setupHandler.getConfig();

        return new Game(gameConfig);
    }
}
