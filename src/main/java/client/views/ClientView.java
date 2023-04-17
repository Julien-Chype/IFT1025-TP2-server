package client.views;

import server.models.Course;
import server.models.RegistrationForm;

import java.util.ArrayList;

public abstract class ClientView {

    public abstract String waitForNextCommand();
    public abstract RegistrationForm getRegistrationInfo();
    public abstract String getCourseListSessionInfo();
    public abstract void processRegistrationResponse(String response);
    public abstract void processCourseListResponse(String session, ArrayList<Course> cours);

}
