package client;

import client.controllers.*;
import client.views.*;

/**
 * The type Cli client launcher.
 */
public class CLIClientLauncher {

    /**
     * The constant PORT.
     */
    public final static int PORT = 1337;
    /**
     * The constant HOST.
     */
    public final static String HOST = "127.0.0.1";

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Client client;
            try {

                CLIClientView view = new CLIClientView();
                client = new Client(PORT, view, HOST);
                System.out.println("client is running...");
                System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
                client.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
