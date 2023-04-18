package client.views;

import client.GUIClientLauncher;
import server.models.Course;
import server.models.RegistrationForm;

import java.util.ArrayList;

public class GUIClientView extends ClientView{
    // this is gonna maintain an Application instance?

    private GUIclientApp app;

    public GUIClientView(String[] args){
        app = new GUIclientApp();
        GUIclientApp.launch(args);
    }

    public String waitForNextCommand(){
        // this sets up the button waiters
        return "";
    }
    public RegistrationForm getRegistrationInfo(){
        // reads the registration info from the forms in the GUI and sends them to controller
        return new RegistrationForm("", "", "", "", new Course("","",""));
    }
    public String getCourseListSessionInfo(){
        // reads the session label from the stopdown menu button and sends it back
        return "";
    }
    public void processRegistrationResponse(String response){
        // prints the confirmation message below the "enovyer" button

    }
    public void processCourseListResponse(ArrayList<Course> cours){
        // prints the courses in the content table
    }
    public void processCourseListResponse(String session, ArrayList<Course> cours){

    }

}
