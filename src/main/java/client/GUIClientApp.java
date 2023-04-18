package client;

import client.controllers.GUIClient;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    public static GUIClient client;

    private static TextField prenomTextField;
    private static TextField nomTextField;
    private static TextField emailTextField;
    private static TextField matriculeTextField;

    private static ComboBox<String> comboBox;

    private static TableView<Course> table;

    private static ArrayList<Course> activeCourses = new ArrayList<Course>();

    private static Course selectedCourse;

    public static void main(String[] args){
        client = new GUIClient(PORT, HOST);
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
        comboBox = new ComboBox<String>();
        comboBox.getItems().add("Automne");
        comboBox.getItems().add("Hiver");
        comboBox.getItems().add("Ete");
        Button charger = new Button("charger");
        buttonBox.getChildren().addAll(comboBox, charger);

        // event handler for charger button press
        charger.setOnMouseClicked((event) -> {
            chargerEvent();
        });

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        table = new TableView<Course>();
        table.setEditable(true);
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        TableColumn<Course, String> coursCol = new TableColumn<>("Cours");

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        coursCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codeCol, coursCol);

        table.setItems(FXCollections.observableArrayList(activeCourses));

        table.setOnMouseClicked((event) -> {
            if (event.getClickCount() > 0) {
                selectedCourse = table.getSelectionModel().getSelectedItem();
            }
        });

        leftSide.getChildren().addAll(courseListText, table, buttonBox, new Separator());

        leftSide.setAlignment(Pos.CENTER);
        leftSide.setSpacing(10);


        //populate right side =======================

        Text registerText = new Text("Formulaire d'inscription");
        registerText.setFont(Font.font("serif", 20));
        rightSide.getChildren().add(registerText);

        Text prenom = new Text("Prenom");
        prenomTextField = new TextField();
        HBox prenomBox = new HBox();
        prenomBox.getChildren().addAll(prenom, prenomTextField);
        prenomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(prenomBox);

        Text nom = new Text("Nom");
        nomTextField = new TextField();
        HBox nomBox = new HBox();
        nomBox.getChildren().addAll(nom, nomTextField);
        nomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(nomBox);

        Text email = new Text("Email");
        emailTextField = new TextField();
        HBox emailBox = new HBox();
        emailBox.getChildren().addAll(email, emailTextField);
        emailBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(emailBox);

        Text matricule = new Text("Matricule");
        matriculeTextField = new TextField();
        HBox matriculeBox = new HBox();
        matriculeBox.getChildren().addAll(matricule, matriculeTextField);
        matriculeBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(matriculeBox);

        Button envoyer = new Button("envoyer");

        // event handler for envoyer button press
        envoyer.setOnMouseClicked((event) -> {
            inscrireEvent();
        });

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

    static public void inscrireEvent(){
        client.establishConnection(PORT, HOST);
        RegistrationForm forme = getRegistrationInfo();
        String response = client.sendRegistrationRequest(forme);
        processRegistrationResponse(response);
    }

    static public void chargerEvent(){
        client.establishConnection(PORT, HOST);
        String session = getCourseListSessionInfo();
        ArrayList<Course> cours = client.sendCourseListRequest(session);
        processCourseListResponse(session, cours);
    }

    static public RegistrationForm getRegistrationInfo(){
        // read the fields of each Textfield and return a registration form
        // reads the class from the "currently pressed" value on the table

        String prenom = prenomTextField.getText();
        String nom = nomTextField.getText();
        String email = emailTextField.getText();
        String matricule = matriculeTextField.getText();

        return new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
    }
    static public String getCourseListSessionInfo(){
        // reads the session label from the stopdown menu button and sends it back
        String session = (String) comboBox.getValue();
        System.out.println("asked session is " + session);
        return session;
    }
    static public void processRegistrationResponse(String response){
        // opens a new message window with the response
        System.out.println(response);

    }
    static public void processCourseListResponse(String session, ArrayList<Course> cours){
        // modifies the table (erasing all previous entries) to display the course list
        activeCourses = cours;
        table.setItems(FXCollections.observableArrayList(activeCourses));
        System.out.println(cours.toString());
    }
}
