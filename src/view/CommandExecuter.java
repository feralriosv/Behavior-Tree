/*
 * Copyright (c) 2025, KASTEL. All rights reserved.
 */
package view;

import view.command.ViewKeyword;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.Set;

/**
 * <p> The class initiates and handles command executions with the user. It is responsible for delegating the input to the
 * corresponding command implementations. The command and its arguments are expected to be separated by {@value #COMMAND_SEPARATOR}.</p>
 *
 * <p> An interaction is started by calling {@link #handleUserInput()}. It is possible to stop the current interaction prematurely
 * with {@link #stop()}.</p>
 *
 * @param <M> the type of the model on which model commands are invoked
 * @param <K> the type of the model commands provider
 *
 * @author Programmieren-Team
 */
public class CommandExecuter<M, K extends Enum<K> & Keyword<M>> {

    private static final String COMMAND_SEPARATOR = " ";
    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_UNKNOWN_COMMAND = "unknown command";
    private static final String ERROR_TOO_MANY_ARGUMENTS = "too many arguments provided.";
    private static final String ERROR_INVALID_PRECONDITION = "command cannot be used right now.";

    private final Set<? extends Keyword<M>> modelKeywords;
    private final Set<ViewKeyword> viewKeywords = EnumSet.allOf(ViewKeyword.class);
    private final ConsoleIORessources ioRessources;
    private M model;
    private boolean running;

    /**
     * Constructs a new command executer using the provided input source and output streams when interacting.
     *
     * @param ioRessources
     * @param keywordClass the class of the command provider to look up possible commands
     */
    public CommandExecuter(ConsoleIORessources ioRessources, Class<K> keywordClass) {
        this.ioRessources = ioRessources;
        this.modelKeywords = EnumSet.allOf(keywordClass);
        this.running = true;
    }

    /**
     * Stops this instance from reading further input from the source.
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Sets the model of this command executer on which its commands should get invoked.
     *
     * @param model the model to be handled by the commands
     */
    protected void setModel(M model) {
        this.model = model;
    }

    /**
     * Returns whether this executer has been terminated.
     *
     * @return whether this executer has been terminated
     */
    protected boolean isRunning() {
        return this.running;
    }

    /**
     * Retrieves a command from the provided input stream and executes it. This method will block while waiting for an input.
     *
     * @see Scanner#hasNextLine()
     */
    protected void handleUserInput() {
        if (this.running && this.ioRessources.getInputSource().hasNextLine()) {
            handleLine(this.ioRessources.getInputSource().nextLine());
        }
    }

    private void handleLine(String line) {
        String[] splittedLine = line.split(COMMAND_SEPARATOR, -1);
        if (!findAndHandleCommand(this.viewKeywords, this, splittedLine)
                && !findAndHandleCommand(this.modelKeywords, this.model, splittedLine)) {
            printError(ERROR_UNKNOWN_COMMAND);
        }
    }

    private <S, T extends Keyword<S>> boolean findAndHandleCommand(Set<T> keywords, S value, String[] command) {
        T keyword = retrieveKeyword(keywords, command);
        if (keyword != null) {
            String[] arguments = Arrays.copyOfRange(command, keyword.words(), command.length);
            handleCommand(value, arguments, keyword);
            return true;
        }
        return false;
    }

    private <S, T extends Keyword<S>> void handleCommand(S value, String[] arguments, T keyword) {
        if (value == null) {
            printError(ERROR_INVALID_PRECONDITION);
            return;
        }

        Arguments argumentsHolder = new Arguments(arguments);
        Command<S> providedCommand;
        try {
            providedCommand = keyword.provide(argumentsHolder);
        } catch (InvalidArgumentException e) {
            printError(e.getMessage());
            return;
        }

        if (!argumentsHolder.isExhausted()) {
            printError(ERROR_TOO_MANY_ARGUMENTS);
            return;
        }

        handleResult(providedCommand.execute(value));
    }

    private void handleResult(Result result) {
        if (result == null || result.getMessage() == null) {
            return;
        }

        PrintStream outputStream = switch (result.getType()) {
            case SUCCESS -> this.ioRessources.getDefaultStream();
            case FAILURE -> this.ioRessources.getErrorStream();
        };
        outputStream.println((result.getType().equals(ResultType.FAILURE) ? ERROR_PREFIX : "") + result.getMessage());
    }

    private static <T extends Keyword<?>> T retrieveKeyword(Collection<T> keywords, String[] command) {
        for (T keyword : keywords) {
            if (keyword.matches(command)) {
                return keyword;
            }
        }
        return null;
    }

    /**
     * Prints a message to the default output stream of this executer.
     *
     * @param message the message to print
     */
    public void printOnDefault(String message) {
        this.ioRessources.getDefaultStream().println(message);
    }

    /**
     * Returns the I/O resources used by this command executer.
     *
     * @return the {@link IORessources} instance backing this executer
     */
    public ConsoleIORessources getIoRessources() {
        return this.ioRessources;
    }

    /**
     * Prints an error message to the error output stream of this executer.
     * <p>
     * The message is prefixed with the error prefix before being printed to the error stream.
     *
     * @param errorMessage the error message to print
     */
    private void printError(String errorMessage) {
        this.ioRessources.getErrorStream().println(ERROR_PREFIX + errorMessage);
    }
}
