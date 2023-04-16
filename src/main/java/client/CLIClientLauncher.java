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
            System.out.println("Server is running...");
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
