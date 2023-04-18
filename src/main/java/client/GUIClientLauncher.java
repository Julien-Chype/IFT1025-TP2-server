package client;

import client.controllers.*;
import client.views.*;

public class GUIClientLauncher {

    public final static int PORT = 1337;

    public static void main(String[] args) {
        Client client;
        try {

//            GUIClientView view = new GUIClientView(args);
//            client = new Client(PORT, view);
//            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
