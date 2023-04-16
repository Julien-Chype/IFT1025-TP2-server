// test commit

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
     * Le constructeur instancie un ServerSocket pouvant désservir un seul client à la fois
     * @param port "Port" est le port sur lequel l'application va desservir les clients
     * @throws IOException  Envoie une exception si le port fournit n'est pas un integer
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Cette méthode ajout un EventHandler au server
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
     * Cette méthode gère le stream de données provenant du client. Elle extrait les requêtes
     * et ensuite alerte tous les Handlers.
     * @throws IOException Cette exception survient lorsque la ligne de requête n'est pas dans un format pouvant être
     * traité par le programme
     * @throws ClassNotFoundException Cette exception survient lorsque le format de donnée reçu n'est pas un Objet.
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
     * Cette méthode est le "handler" principal de notre server. Elle trie les <cmd> reçu du client
     * et active la réponse appropriée.
     * @param cmd "cmd" est le String de la commande compris dans une requête
     * @param arg "arg" est le String des arguments compris dans une requête
     */
    public void handleEvents(String cmd, String arg) throws IOException {
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

        //debug #todo , nécessité de tester la session ? car l'appel à cette méthode est fait par
        // une méthode interne, donc pas sensé recevoir autres choses

        //Vérifie que l'argument est valide
        boolean test = false ;
        String[] session = {"Ete", "Hiver", "Automne"} ;
        for (int i = 0 ; i < session.length ; i++) { if(arg == session[i]) { test = true; break ; } }
        if (!test){ throw new IllegalArgumentException("L'argument n'est pas une session valide") ;}

        //ouverture du document texte contenu les cours
        try {
            reader = new BufferedReader( new FileReader("../data/cours.txt") ) ;
        } catch (IOException e) {
            throw new IOException("Erreur à l'ouverture du fichier 'cours.txt'");
        }

        //Extrait seulement les lignes  contenant la bonne session et construit la liste
        //à remettre au client
        ArrayList<String> cours = new ArrayList<>() ;
        String ligne ;
        while ( (ligne = reader.readLine()) != null ){
           String[] coupe = ligne.split("\t");
           if (coupe[2] == arg){
               String ligneRecuParClient = coupe[0] + "\t" + coupe[1] ;
               cours.add(ligneRecuParClient) ;
           }
        }
        try {
            reader.close();
        } catch(IOException e){
            throw new IOException("Erreur lors de la fermeture de 'cours.txt'") ;
        }

        //remise du ArrayList<String> au client
        try {
            objectOutputStream.writeObject(cours);
        } catch(IOException e){
            throw new IOException("Erreur d'output lors de l'envoie de la liste de cours filtré") ;
        }
    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en
     * utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet,
     * l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() throws IOException {
        //premier Objet est déjà lu, cest la requête, la on lit le 2e objet, le registratioForm
        String forme ;
        String reponse ;
        Boolean estConforme = true;
        try {
            forme = objectInputStream.readObject().toString();
        }catch(IOException | ClassNotFoundException e){
           throw new IOException("Erreur à la réception du registre d'inscription") ;
        }
        //la forme est sensé ressembler à cela:
        //Automne IFT2255 87654321 Lanuze Charlotte charlotte@umontreal.ca

        String[] parts = forme.split("\t");
        String sigle = parts[1];
        String matricule = parts[2];
        String nom = parts[2];
        String prenom = parts[3] ;
        reponse = "Félicitation! Incription réussi au réussi de " + prenom + " au cours " + sigle + "\n" ;


        //vérifier que le cours existe
        BufferedReader reader ;
        try {
            reader = new BufferedReader( new FileReader("../data/cours.txt") ) ;
        } catch (IOException e) {
            throw new IOException("Erreur à l'ouverture du fichier 'cours.txt'");
        }

        //Parcours tous les cours pour vérifier si le sigle correspond
        String ligne ;
        while ( (ligne = reader.readLine()) != null ){
            String[] coupe = ligne.split(" ");
            if (coupe[1] == sigle) {
                estConforme = false;
                reponse = "Échec de l'inscription, le cours n'existe pas";
                break;
            }
        }
        try {
            reader.close();
        } catch(IOException e){
            throw new IOException("Erreur lors de la fermeture de 'cours.txt'") ;
        }

        //iscription dans le fichier, et vérificatio que pas déjà inscrit
        BufferedWriter writer ;
        try {
            writer = new BufferedWriter(new FileWriter("../data/inscription.txt"));
            reader = new BufferedReader(new FileReader("../data/inscription.txt"));
        }catch (IOException e){
            estConforme = false ;
            throw new IOException("Erreur à l'ouverture du document 'inscription.txt") ;
        }
        //Automne IFT2255 87654321 Lanuze Charlotte charlotte@umontreal.ca
        try {
            //vérification de doublon
            while ( ( ligne = reader.readLine()) != null ){
                parts = ligne.split("\t") ;
                if (parts[2] == matricule){
                   if (parts[1] == sigle){
                       estConforme = false ;
                       reponse = "Échec de l'inscription, l'étudiant est déjà inscrit" ;
                   }
                }
            }
            //fait l'inscription dans le document
            if (! estConforme) { writer.write(forme); }

        }catch (IOException e){
            throw new IOException("Erreur à l'écriture dans le fichier \"inscription.txt\"\n") ;
        }

        //envoie de la réponse de réussi ou non au client
       try {
           objectOutputStream.writeObject(reponse);
       } catch(IOException e){
           throw new IOException("Envoie de la réponse au client a échoué" );
       }
    }
}

