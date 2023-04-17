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

public class CLIClientView extends ClientView{

    private final static Scanner scanner = new Scanner(System.in);
    private static String choixSession ;


    public String waitForNextCommand(){
        // prompts the user for either sending a registration request or a course list request

    }
    public RegistrationForm getRegistrationInfo(){
        // prompts the user to enter registration info
    }
    public String getCourseListSessionInfo(){
        // prompts the user to select the session for the requested course list
    }
    public void processRegistrationResponse(String response){
        // just prints the registration response from the server
    }
    public void processCourseListResponse(ArrayList<Course> cours){
        // takes the arraylist of courses and prints it to screen
    }
}
