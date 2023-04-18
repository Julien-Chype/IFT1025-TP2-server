package server;

import java.net.BindException;

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
        while (true) {
            try {
                // ================ creation et lancement d'un nouveau serveur ================
                server = new Server(PORT);
                System.out.println("Server is running...");
                server.run();
            }
            catch( BindException e){
                ; // ne fait rien, sert à éviter une impression répétitive de la tentative de réouverture du serveur
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}