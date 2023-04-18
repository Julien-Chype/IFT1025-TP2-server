

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

public class Client {
    /*
    This handles waiting around for the server to respond, maintains a client View to display, along with

    - the views need to be able to
    1. wait for user to choose Session, Course, input their names, etc.
    2. provide output messages that show the server output

    This class calls the views to make this happen, and waits around for the server to

     */
    private Socket client ;
    private ObjectInputStream input ;
    private ObjectOutputStream output ;
    private ClientView view;
    private int PORT ;
    private String HOST ;
    public Client(int port, ClientView view, String host){

        this.HOST = host ;
        this.PORT = port ;




        this.view = view;

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

            InputStream x = client.getInputStream();

            input = new ObjectInputStream(client.getInputStream());
            output = new ObjectOutputStream(client.getOutputStream());
        }catch(Exception e){
            System.out.println("Échec d'établissement de passerelle client/serveur");
            System.exit(0) ;
        }
    }

    public void run(){
        // this is the function that calls the view to get the registration form
        // that sends back the responses from the server to the view for visualization
        // need to setup events here, the view would callback to tell us whether we register or request courses.

        // while: wait for command type from view
        // then send back data to view depending on command to visualize it.
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
                    stop = true;
                    disconnect();
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
