package client.views;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.System ;

/**
 * Classe qui definie une View pour l'interface sur terminal du client
 */
public class CLIClientView{

    private final static Scanner scanner = new Scanner(System.in);
    private static String choixSession ;

    private String getUserString(String demande){

        // ================ Lecture du terminal d'un string ================

        String input = "";
        System.out.println("Veuiller saisir " + demande + ": ") ;
        try {input = scanner.nextLine() ;}
        catch(Exception e){ System.out.println("Svp entrez un choix valide\nVeuiller saisir " + demande + ": ") ; getUserString(demande) ; }
        return input ;
    }

    private int getUserInt(int maxIntAllowed){

        // ================ Lecture du terminal d'un nombre entre 1 et maxIntAllowed ================

        int choix = -1 ;
        try { choix = Integer.parseInt(scanner.nextLine()); }
        catch(Exception e){ System.out.println("Svp entrez un choix valide\n") ; getUserInt(maxIntAllowed) ; }

        if (choix < 1 || choix > maxIntAllowed){
            System.out.println("Svp entrez un choix valide\n") ; choix = getUserInt(maxIntAllowed) ;
        }
        return choix ;
    }

    public String waitForNextCommand(){

        // ================ attente pour la prochaine commande du terminal ================

        System.out.println("\n1. Consulter les cours offerts pour une session\n2. Inscription à un cours\n> Choix: ") ;
        int decision  = getUserInt(2) ;

        if (decision == 1){
            return "CHARGER";
        }else{
            return "INSCRIRE";
        }
    }
    public RegistrationForm getRegistrationInfo(){

        // ================ Lecture du terminal de tout les champs de la forme d'inscription ================

        String prenom = getUserString("votre prénom");
        String nom = getUserString("votre nom");
        String email = getUserString("votre email");
        String matricule = getUserString("votre matricule");
        String sigle = getUserString("le code du cours");

        Course course = new Course("unknown", sigle, "unknown");

        return new RegistrationForm(prenom, nom, email, matricule, course);
    }
    public String getCourseListSessionInfo(){

        // ================ Lecture du terminal de la session de cours desiree ================

        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n1. Automne\n2. Hiver\n3. Ete\n") ;

        int choix = getUserInt(3) ;

        String[] sessions = {"Automne", "Hiver", "Ete"};

        return sessions[choix-1];
    }
    public void processRegistrationResponse(String response){

        // ================ impression au terminal de la reponse ================

        System.out.println(response);
    }
    public void processCourseListResponse(String session, ArrayList<Course> cours){

        // ================ impression au terminal des cours disponibles ================

        System.out.println("Les cours offerts pendant la session d'" + session + " sont:");
        for(int i = 0; i<cours.size(); i++){
            Course course = cours.get(i);
            System.out.println(String.valueOf(i) + "." + course.getCode() + "\t" + course.getName() );
        }
    }
}
