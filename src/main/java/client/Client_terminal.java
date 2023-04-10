package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.System ;

public class Client_terminal {
    private final static Scanner scanner = new Scanner(System.in);
    private static String userInput = scanner.nextLine();
    private static Socket client ;
    private static ObjectInputStream input ;
    private static ObjectOutputStream output ;
    public static void main(String[] args) throws IOException {

        try { client = new Socket("127.0.0.1", 1337); }
        catch(Exception e) {
            System.out.println("Échec de la connection au serveur de l'UDEM");
            System.exit(0) ;
        }

        try {
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
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

        int choix = -1;
        try { choix = Integer.parseInt(userInput); }
        catch(Exception e){ System.out.println("Svp entré un choix valide\n") ; choixSession() ; }

        if (choix < 1 || choix > 3){
        System.out.println("Svp entré un choix valide entre 1, 2 et 3\n") ; choixSession() ;
        }

        choixCours(choix) ;

    }
    private static void choixCours(int choix) {
        ArrayList<String> listeDeCours = null;
        try{
            String cmd = "CHARGER" ;
            output.writeObject(cmd) ;

            Object object = input.readObject() ;
            listeDeCours = (ArrayList<String>) object ;

        } catch(Exception e) {
            System.out.println("Échec du chargement des cours");
            System.exit(0);
        }
        String messageDeOuverture = "Les cours offerts pendant la session d'automne sont:\n " ;
        int chiffre = 1 ;
        while ( !listeDeCours.isEmpty()){
           messageDeOuverture += chiffre + " " + listeDeCours.get(chiffre - 1) + "\n"   ;
           chiffre++ ;
        }
        System.out.println(messageDeOuverture + "Choix;\n1. Consulter les cours offerts pour une autre session\n2. Inscription à un cours\nChoix: ") ;
        // eheh monsieur, chu rendu ici #todo rendu ici
        try
        int choixDeux = Integer.parseInt(userInput) ;
        if (choixDeux)





        System.out.println(messageDeOuverture) ;

    }
}
