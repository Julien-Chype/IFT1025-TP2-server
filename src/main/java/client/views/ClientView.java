package client.views;

import server.models.Course;

import java.util.ArrayList;

public abstract class ClientView {

    public abstract String waitForNextCommand();
    public abstract String getInscriptionInfo();
    public abstract String getCourseListSessionInfo();
    public abstract void processInscriptionResponse(String response);
    public abstract void processCourseListResponse(ArrayList<Course> cours);

}
