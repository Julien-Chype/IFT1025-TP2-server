

package client.controllers;

import client.views.*;
import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe client qui interface avec le serveur pour l'application sur ligne de commande
 */
public class Client {
    private Socket client ;
    private ObjectInputStream input ;
    private ObjectOutputStream output ;
    private CLIClientView view;
    private int PORT ;
    private String HOST ;

    /**
     * creation d'un nouveau Client
     *
     * @param port le port
     * @param view la view
     * @param host le host
     */
    public Client(int port, CLIClientView view, String host){

        this.HOST = host ;
        this.PORT = port ;
        this.view = view;
    }

    /**
     * etablir une connection avec le serveur
     *
     * @param port the port
     * @param host the host
     */
    public void establishConnection(int port, String host){

        // ================ creation du socket ================

        try {
            client = new Socket(host, port);
        }
        catch(Exception e) {
            System.out.println("Échec de la connection au serveur de l'UDEM");
            System.exit(0) ;
        }

        // ================ creation des input et output stream ================

        try {
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
        }catch(Exception e){
            System.out.println("Échec d'établissement de passerelle client/serveur");
            System.exit(0) ;
        }
    }

    /**
     * method principale de la logique du client de ligne de commande
     */
    public void run(){
        boolean stop = false;

        while(!stop){
            establishConnection(PORT, HOST);

            String command = view.waitForNextCommand();

            switch (command) {

                case "INSCRIRE":
                    // here we need to get the inscription info from the current view
                    RegistrationForm forme = view.getRegistrationInfo();
                    String response = sendRegistrationRequest(forme);
                    view.processRegistrationResponse(response);
                    break;

                case "CHARGER":
                    String session = view.getCourseListSessionInfo();
                    ArrayList<Course> cours = sendCourseListRequest(session);
                    view.processCourseListResponse(session, cours);
                    break;
            }
        }
    }

    private String waitForRegistrationResponse(){

        String response = "";

        try {
            response = (String) input.readObject();
        }
        catch (Exception e){
            System.out.println("erreur lors de la reception de la reponse du serveur pour l'inscription");
            System.exit(0) ;
        }

        return response;
    }

    private ArrayList<Course> waitForClassListRequestResponse(){
        ArrayList<Course> cours = new ArrayList<>();

        try {
            cours = (ArrayList<Course>) input.readObject();
        }
        catch (Exception e){
            System.out.println("erreur lors de la reception de la reponse du serveur pour l'inscription");
            e.printStackTrace();
            System.exit(0) ;
        }
        return cours;
    }

    private String sendRegistrationRequest(RegistrationForm forme){
        String message = "";
        try{
            output.writeObject("INSCRIRE");
            output.writeObject(forme);
            message = waitForRegistrationResponse();
        }catch(Exception e){
            System.out.println("erreur lors de l'envoie de la forme pour l'inscription");
            System.exit(0) ;
        }
        return message;
    }

    private ArrayList<Course> sendCourseListRequest(String session){
        ArrayList<Course> cours = new ArrayList<Course>();
        try{
            output.writeObject("CHARGER " + session);
            cours = waitForClassListRequestResponse();
        }catch(Exception e){
            System.out.println("erreur lors de l'envoie de la forme pour la requete de cours");
            System.exit(0) ;
        }
        return cours;
    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("erreur lors de la déconnection au serveur");
            throw new RuntimeException(e);
        }
    }
}
