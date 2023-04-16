package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.System ;

public class Client_simple {
    private final static Scanner scanner = new Scanner(System.in);
    private static Socket client ;
    private static String choixSession ;
    private static ObjectInputStream input ;
    private static ObjectOutputStream output ;
    public static void main(String[] args) throws IOException {

        try { client = new Socket("127.0.0.1", 1337); }
        catch(Exception e) {
            System.out.println("Échec de la connection au serveur de l'UDEM");
            System.exit(0) ;
        }

        System.out.println("connection faites"); //debug
        try {
            System.out.println("juste avant de get les streams"); //debug
            input = new ObjectInputStream(client.getInputStream());
            System.out.println("input good "); //debug
            output = new ObjectOutputStream(client.getOutputStream());
            System.out.println("juste après de les streams"); //debug
        }catch(Exception e){
            System.out.println("Échec d'établissement de passerelle client/serveur");
            System.exit(0) ;
        }

        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***") ;

        choixSession() ;
        deconnection() ;
    }

    private static void choixSession() {
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n1. Automne\n2. Hiver\n3. Ete\n") ;

        choixCours(getUserInt(3)) ;

    }
    private static void choixCours(int choix) {
        ArrayList<String> listeDeCours = null;

        String[] session = {"Automne", "Ete", "Hiver"} ;
        String cmd = "CHARGER" ;
        String arg = session[choix-1] ;
        choixSession = arg ;
        String requete = cmd + " " + arg ;

        //requête des cours offerts à la session sélectionné au serveur
        try{
            output.writeObject(requete) ;

            Object object = input.readObject() ;
            listeDeCours = (ArrayList<String>) object ;

        } catch(Exception e) {
            System.out.println("Échec du chargement des cours");
            System.exit(0);
        }
        String messageDeOuverture = "Les cours offerts pendant la session d'automne sont:\n " ;
        int chiffre = 1 ;
        if (listeDeCours.isEmpty()) { messageDeOuverture+= "*** Aucun cours disponible ***\n";}

        while ( !listeDeCours.isEmpty()){
           messageDeOuverture += chiffre + ". " + listeDeCours.remove(0) + "\n"   ;
           chiffre++ ;
        }
        System.out.println(messageDeOuverture + "Choix;\n1. Consulter les cours offerts pour une autre session\n2. Inscription à un cours\nChoix: ") ;

        int decision  = getUserInt(chiffre-1) ;
        switch(decision){
            case 1:
                choixSession(); break;
            case 2:
                inscription(); break ;
            default:
                System.out.println("May god have mercy on you soul, you are not supposed to be here");
        }
    }
    private static int getUserInt(int quantite){
        int choix = -1 ;
        try { choix = Integer.parseInt(scanner.nextLine()); }
        catch(Exception e){ System.out.println("Svp entré un choix valide\n") ; getUserInt(quantite) ; }

        if (choix < 1 || choix > quantite){
            System.out.println("Svp entré un choix valide\n") ; choix = getUserInt(quantite) ;
        }
        return choix ;

    }
    private static String getUserString(String demande){
        String input = "";
        System.out.println("Veuiller saisir " + demande + ": ") ;
        try {input = scanner.nextLine() ;}
        catch(Exception e){ System.out.println("Svp entré un choix valide\nVeuiller saisir " + demande + ": ") ; getUserString(demande) ; }
        return input ;
    }
    private static void inscription(){
        String prenom = getUserString("votre prénom");
        String nom = getUserString("votre nom");
        String email = getUserString("votre email");
        String matricule = getUserString("votre matricule");
        String sigle = getUserString("le code du cours");

        String forme = choixSession + "\t" + sigle + "\t" + matricule + "\t" + nom + "\t" + prenom + "\t" + email + "\n" ;
        try{
            output.writeObject(forme);
            String message = input.readObject().toString();
            System.out.println(message) ; //todo attendre la réponse comment ? si le serveur est lent
        }catch(Exception e){
            System.out.println("erreur lors de l'envoie de la forme");
            System.exit(0) ;
        }

    }
    private static void deconnection() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("erreur lors de la déconnection au serveur");
            throw new RuntimeException(e);
        }
    }
}
