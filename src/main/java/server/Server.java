package server;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    /**
     *"REGISTER_COMMAND" est une constante d'un String permettant d'identifier une
     * certaine commande.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     *"LOAD_COMMAND" est une constante d'un String permettant d'identifier une
     * certaine commande.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     *
     * @param port "Port" est le port sur lequel l'application va desservir le client
     * @throws IOException  Envoie une exception si le port fournit n'est pas un integer
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler((cmd, arg) -> handleEvents(cmd, arg));
    }

    /**
     * Cette méthode enfile un Event destiné à se faire traiter
     * @param h "h" est l'évenement enfilé
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Cette méthode est la fonction principale de la classe Server. Elle établie
     * la connection client-serveur afin d'écouter les requêtes, les traiter et
     * d'envoyer la réponse au client.
     *
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cette méthode gère le stream de réception du serveur, afin d'en extraire les requêtes et de lancer les méthodes
     * appropriées.
     * @throws IOException Cette exception survient lorsque la ligne de requête n'est pas dans un format pouvant être
     * traité par le programme
     * @throws ClassNotFoundException Cette exception survient lorsque #todo
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Cette méthode reçoit un String d'une ligne de requête reçu par le server et les décomposes en <commande> et <argument>
     * @param line "line" est le String de la requêtre reçu par le server
     * @return La méthode retourne une classe Pair (<String commande>, <String arguments>)
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Cette méthode déconnecte le client du serveur proprement en fermant les streams de données et la connection.
     * @throws IOException Envoie un exception s'il y a une erreur d'entré/sortie
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Cette méthode trie les commandes aves ses arguments et lance la méthode approprié
     * @param cmd "cmd" est le String de la commande compris dans une requête
     * @param arg "arg" est le String des arguments compris dans une requête
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) throws IOException, IllegalArgumentException {

        BufferedReader reader ;

        //debug , nécessité de tester la session ? car l'appel à cette méthode est fait par
        // une méthode interne, donc pas sensé recevoir autres choses
        //Vérifie que l'argument est valide
        boolean test = false ;
        String[] session = {"Ete", "Hiver", "Automne"} ;
        for (int i = 0 ; i < 3 ; i++) { if(arg == session[i]) { test = true; } }
        if (!test){ throw new IllegalArgumentException("L'argument n'est pas une session valide") ;}

        try {
            reader = new BufferedReader( new FileReader("../data/cours.txt") ) ;
        } catch (IOException e) {
            throw new IOException("Erreur à l'ouverture du fichier 'cours.txt'");
        }

        //Extrait seulement les lignes  contenant la bonne session à liste<Object> cours
        ArrayList<Object> cours = new ArrayList<>() ;
        String cour ;
        while ( (cour = reader.readLine()) != null ){
           String[] coupe = cour.split(" ");
           if (coupe[2] == arg){ cours.add(cour) ; }
        }
        try {
            reader.close();
        } catch(IOException e){
            throw new IOException("Erreur lors de la fermeture de 'cours.txt'") ;
        }

        //construit et envoie le message contenant la liste de cours triée.
        String res = ""    ;
        for (int i = 0; i < cours.size() ; i++){
           String[] coupe =  ( (String) cours.get(i) ).split(" ") ;
           res += coupe[0] + " " + coupe[1] + "\n" ;
        }
        try {
            //noinspection ReassignedVariable
            objectOutputStream.writeBytes(res);
        } catch(IOException e){
            throw new IOException("Erreur de output lors de l'envoie de la liste de cours filtré") ;
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode
    }
}

