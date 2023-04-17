package client;

import client.controllers.*;
import client.views.*;

public class CLIClientLauncher {

    public final static int PORT = 1337;

    public static void main(String[] args) {
        Client client;
        try {

            CLIClientView view = new CLIClientView();
            client = new Client(PORT, view);
            System.out.println("client is running...");
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***") ;
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
