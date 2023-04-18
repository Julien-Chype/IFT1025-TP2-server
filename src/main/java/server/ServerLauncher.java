package server;

/**
 * Classe qui lance le serveur
 */
public class ServerLauncher {
    /**
     * The constant PORT.
     */
    public final static int PORT = 1337;

    /**
     * Point d'entree de l'application
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Server server;
        try {
            // ================ creation et lancement d'un nouveau serveur ================
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}