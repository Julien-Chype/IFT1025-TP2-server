package client.controllers;

import client.GUIClientApp;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GUIClient {

    private Socket client ;
    private ObjectInputStream input ;
    private ObjectOutputStream output ;
    private int PORT ;
    private String HOST ;
    public GUIClient(int port, String host){

        this.HOST = host ;
        this.PORT = port ;
    }
    public void establishConnection(int port, String host){
        // creation du socket
        try {
            client = new Socket(host, port);
        }
        catch(Exception e) {
            System.out.println("Échec de la connection au serveur de l'UDEM");
            System.exit(0) ;
        }
        // creation des input et output stream
        try {
            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
        }catch(Exception e){
            System.out.println("Échec d'établissement de passerelle client/serveur");
            System.exit(0) ;
        }
    }

    public String waitForRegistrationResponse(){

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

    public ArrayList<Course> waitForClassListRequestResponse(){
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

    public String sendRegistrationRequest(RegistrationForm forme){
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

    public ArrayList<Course> sendCourseListRequest(String session){
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
