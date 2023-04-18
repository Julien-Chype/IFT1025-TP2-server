package client;

import client.controllers.GUIClient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import java.util.ArrayList;

public class GUIClientApp extends Application {

    public final static int PORT = 1337;
    public final static String HOST = "127.0.0.1";
    static void main(String[] args){
        GUIClient client;
        client = new GUIClient(PORT, HOST);
        client.run();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        HBox root = new HBox();

        VBox leftSide = new VBox();
        VBox rightSide = new VBox();

        //populate left side ========================

        Text courseListText = new Text("Liste des cours");
        courseListText.setFont(Font.font("serif", 20));

        HBox buttonBox = new HBox();
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("Automne");
        comboBox.getItems().add("Hiver");
        comboBox.getItems().add("Ete");
        Button charger = new Button("charger");
        buttonBox.getChildren().addAll(comboBox, charger);

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        TableView table = new TableView();
        table.setEditable(true);
        TableColumn codeCol = new TableColumn("Code");
        TableColumn coursCol = new TableColumn("Cours");
        table.getColumns().addAll(codeCol, coursCol);

        leftSide.getChildren().addAll(courseListText, table, buttonBox, new Separator());

        leftSide.setAlignment(Pos.CENTER);
        leftSide.setSpacing(10);


        //populate right side =======================

        Text registerText = new Text("Formulaire d'inscription");
        registerText.setFont(Font.font("serif", 20));
        rightSide.getChildren().add(registerText);

        Text prenom = new Text("Prenom");
        TextField prenomTextField = new TextField();
        HBox prenomBox = new HBox();
        prenomBox.getChildren().addAll(prenom, prenomTextField);
        prenomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(prenomBox);

        Text nom = new Text("Nom");
        TextField nomTextField = new TextField();
        HBox nomBox = new HBox();
        nomBox.getChildren().addAll(nom, nomTextField);
        nomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(nomBox);

        Text email = new Text("Email");
        TextField emailTextField = new TextField();
        HBox emailBox = new HBox();
        emailBox.getChildren().addAll(email, emailTextField);
        emailBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(emailBox);

        Text matricule = new Text("Matricule");
        TextField matriculeTextField = new TextField();
        HBox matriculeBox = new HBox();
        matriculeBox.getChildren().addAll(matricule, matriculeTextField);
        matriculeBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(matriculeBox);

        Button envoyer = new Button("envoyer");

        rightSide.getChildren().add(envoyer);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setSpacing(10);

        //Merge together ========================

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        root.getChildren().addAll(sep1, leftSide, sep2, rightSide);
        root.setSpacing(20);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setTitle("Inscription UdeM");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static String waitForNextCommand(){
        // this sets up the button waiters
        return "";
    }
    static RegistrationForm getRegistrationInfo(){
        // reads the registration info from the forms in the GUI and sends them to controller
        return new RegistrationForm("", "", "", "", new Course("","",""));
    }
    static String getCourseListSessionInfo(){
        // reads the session label from the stopdown menu button and sends it back
        return "";
    }
    static void processRegistrationResponse(String response){
        // prints the confirmation message below the "enovyer" button

    }
    static void processCourseListResponse(ArrayList<Course> cours){
        // prints the courses in the content table
    }
    static void processCourseListResponse(String session, ArrayList<Course> cours){

    }
}
