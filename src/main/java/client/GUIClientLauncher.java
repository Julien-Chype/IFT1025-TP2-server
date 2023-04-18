package client;

import client.controllers.*;
import client.views.*;

public class GUIClientLauncher {

    public final static int PORT = 1337;
    public final static String HOST = "127.0.0.7" ;

    public static void main(String[] args) {
        Client client;
        try {

            GUIClientView view = new GUIClientView(args);
            client = new Client(PORT, view, HOST);
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
