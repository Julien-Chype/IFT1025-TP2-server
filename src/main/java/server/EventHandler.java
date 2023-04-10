package server;

import java.io.IOException;

@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg) throws IOException;
}
