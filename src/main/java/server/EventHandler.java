package server;

import java.io.IOException;

/**
 * The interface Event handler.
 */
@FunctionalInterface
public interface EventHandler {
    /**
     * Handle.
     *
     * @param cmd the cmd
     * @param arg the arg
     * @throws IOException the io exception
     */
    void handle(String cmd, String arg) throws IOException;
}
