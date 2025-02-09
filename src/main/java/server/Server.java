
package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe Server principale
 */
public class Server {

    /**
     * "REGISTER_COMMAND" est une constante d'un String permettant d'identifier une
     * certaine commande.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * "LOAD_COMMAND" est une constante d'un String permettant d'identifier une
     * certaine commande.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Le constructeur instancie un ServerSocket pouvant désservir un seul client à la fois
     *
     * @param port "Port" est le port sur lequel l'application va desservir les clients
     * @throws IOException Envoie une exception si le port fournit n'est pas un integer
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Cette méthode ajout un EventHandler au server
     *
     * @param h "h" est l'évenement enfilé
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) throws IOException {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Cette méthode est la fonction principale de la classe Server. Elle attend une connection d'un client.
     * puis traite ses commandes convenablement. Chaque connection est ensuite close lorsque qu'il n'y pas
     * plus de commande reçu.
     */
    public void run() {
        try {
            while (true){
            client = server.accept();
            System.out.println("Connecté au client: " + client);
            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectInputStream = new ObjectInputStream(client.getInputStream());
            listen();
            disconnect();
            System.out.println("Client déconnecté!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Cette méthode gère le stream de données provenant du client. Elle extrait les requêtes
     * et ensuite alerte tous les Handlers.
     *
     * @throws IOException            Cette exception survient lorsque la ligne de requête n'est pas dans un format pouvant être traité par le programme
     * @throws ClassNotFoundException Cette exception survient lorsque le format de donnée reçu n'est pas un Objet.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            System.out.println(line); //debug
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            System.out.println(cmd + " " + arg); //debug
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Cette méthode reçoit un String d'une ligne de requête reçu par le server et les décomposes en <commande> et <argument>
     *
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
     *
     * @throws IOException Envoie un exception s'il y a une erreur d'entré/sortie
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Cette méthode est le "handler" principal de notre server. Elle trie les <cmd> reçu du client
     * et active la réponse appropriée.
     *
     * @param cmd "cmd" est le String de la commande compris dans une requête
     * @param arg "arg" est le String des arguments compris dans une requête
     * @throws IOException the io exception
     */
    public void handleEvents(String cmd, String arg) throws IOException {
        if (cmd.equals(REGISTER_COMMAND)) {
            System.out.println("Starting registration handling");
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            System.out.println("Starting course load handling");
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     *
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     * @throws IOException              the io exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    public void handleLoadCourses(String arg) throws IOException, IllegalArgumentException {

        BufferedReader reader ;

        // ================   Vérifie que l'argument est une session valide ================

        boolean test = false ;
        String[] session = {"Ete", "Hiver", "Automne"} ;

        for (String s : session) {
            if (arg.equals(s)) {
                test = true;
                break;
            }
        }
        if (!test){ throw new IllegalArgumentException("L'argument n'est pas une session valide") ;}

        // ================ ouverture du document cours.txt ================

        try {
            reader = new BufferedReader( new FileReader("src/main/java/server/data/cours.txt")) ;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Erreur à l'ouverture du fichier 'cours.txt'");
        }

        // ================ construction de la liste des cours a remettre au client ================

        ArrayList<Course> cours = new ArrayList<Course>() ;

        String ligne ;

        while ( (ligne = reader.readLine()) != null ){
           String[] coupe = ligne.split("\t");
           if (coupe[2].equals(arg)){
               cours.add(new Course(coupe[1], coupe[0], coupe[2])) ;
           }
        }
        try {
            reader.close();
        } catch(IOException e){
            throw new IOException("Erreur lors de la fermeture de 'cours.txt'") ;
        }

        // ================ remise du ArrayList<Course> au client ================
        try {
            objectOutputStream.writeObject(cours);
        } catch(IOException e){
            throw new IOException("Erreur d'output lors de l'envoie de la liste de cours filtrée") ;
        }
    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en
     * utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet,
     * l'écriture dans un fichier ou dans le flux de sortie.
     *
     * @throws IOException the io exception
     */
    public void handleRegistration() throws IOException {

        // ================ lecture de l'objet RegistrationForm du Stream ================

        RegistrationForm forme ;
        String reponse ;
        boolean estConforme = true;
        try {
            forme = (RegistrationForm) objectInputStream.readObject();
        }catch(IOException | ClassNotFoundException e){
           throw new IOException("Erreur à la réception du registre d'inscription") ;
        }

        // ================ lecture des champs de la forme recue ================

        String sigle = forme.getCourse().getCode();
        String matricule = forme.getMatricule();
        String nom = forme.getNom();
        String email = forme.getEmail();
        String prenom = forme.getPrenom() ;
        String session = "";

        // reponse de base a renvoyer au client si aucun problem n'es trouve par la suite
        reponse = "Félicitation! Incription réussi au réussi de " + prenom + " au cours " + sigle + "\n" ;

        // ================ verification de la comformite des champs ================

        if (email.split("@").length != 2){
            reponse = "veuillez entrer une addresse email valide";
            estConforme = false;
        }
        if (matricule.length() == 0){
            reponse = "veuillez entrer un matricule valide";
            estConforme = false;
        }
        if (nom.length() == 0 || prenom.length() == 0){
            reponse = "veuillez entrer un nom et prenom valide";
            estConforme = false;
        }

        // ================ verification que le cours existe ================

        BufferedReader reader ;
        try {
            reader = new BufferedReader( new FileReader("src/main/java/server/data/cours.txt") ) ;
        } catch (IOException e) {
            throw new IOException("Erreur à l'ouverture du fichier 'cours.txt'");
        }

        String ligne ;
        boolean coursExiste = false;
        while ( (ligne = reader.readLine()) != null ){
            String[] coupe = ligne.split("\t");

            if (coupe[0].equals(sigle)) {
                coursExiste = true;
                session = coupe[2];
                break;
            }
        }
        if (!coursExiste){
            estConforme = false;
            reponse = "Échec de l'inscription, le cours n'existe pas";
        }

        try {
            reader.close();
        } catch(IOException e){
            throw new IOException("Erreur lors de la fermeture de 'cours.txt'") ;
        }


        // ================ ouverture du fichier d'inscription ================

        try {
            reader = new BufferedReader(new FileReader("src/main/java/server/data/inscription.txt"));
        }catch (IOException e){
            throw new IOException("Erreur à l'ouverture du document 'inscription.txt") ;
        }

        // ================ verification que l'etudient n'est pas deja inscrit ================

        try {
            //vérification de doublon
            while ( ( ligne = reader.readLine()) != null ){
                String[] parts = ligne.split("\t") ;
                if (parts.length > 2) {
                    if (parts[2].equals(matricule) && parts[1].equals(sigle)){
                        estConforme = false ;
                        reponse = "Échec de l'inscription, l'étudiant est déjà inscrit" ;
                    }
                }
            }
        }catch (IOException e){
            throw new IOException("Erreur à l'écriture dans le fichier \"inscription.txt\"\n") ;
        }
        reader.close();

        // ================ ouverture du fichier d'inscription pour ecrire ================
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("src/main/java/server/data/inscription.txt", true));
        }catch (IOException e){
            throw new IOException("Erreur à l'ouverture du document 'inscription.txt") ;
        }

        // ================ ecriture au fichier ================
        try{
            if (estConforme) {
                writer.write(session + "\t" + sigle + '\t' + matricule + '\t' + nom + '\t' + prenom + '\t' + email + '\n'); }
        }catch (IOException e){
            throw new IOException("Erreur à l'ouverture du document 'inscription.txt") ;
        }
        writer.close();

        // ================ envoi de la reponse au client ================
        try {
            objectOutputStream.writeObject(reponse);
        } catch(IOException e){
            throw new IOException("Envoie de la réponse au client a échoué" );
        }
    }
}

