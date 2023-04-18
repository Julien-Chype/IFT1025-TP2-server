package client;

import client.controllers.GUIClient;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import java.util.ArrayList;

/**
 * The type Gui client app.
 */
public class GUIClientApp extends Application {

    /**
     * The constant PORT.
     */
    public final static int PORT = 1337;
    /**
     * The constant HOST.
     */
    public final static String HOST = "127.0.0.1";

    /**
     * The constant client.
     */
    public static GUIClient client;

    private static TextField prenomTextField;
    private static TextField nomTextField;
    private static TextField emailTextField;
    private static TextField matriculeTextField;

    private static ComboBox<String> comboBox;

    private static TableView<Course> table;

    private static ArrayList<Course> activeCourses = new ArrayList<Course>();

    private static Course selectedCourse = new Course("", "", "");

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String[] args){
        client = new GUIClient(PORT, HOST);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // ================ definition de l'element root ================
        HBox root = new HBox();

        VBox leftSide = new VBox();
        VBox rightSide = new VBox();

        // ===================================================================
        // ================ addition d'element au cote gauche ================
        // ===================================================================

        // texte en haut du cote gauche
        Text courseListText = new Text("Liste des cours");
        courseListText.setFont(Font.font("serif", 20));

        // ================ definition de la combobox de session et du bouton de chargement ================

        HBox buttonBox = new HBox();
        comboBox = new ComboBox<String>();
        comboBox.getItems().add("Automne");
        comboBox.getItems().add("Hiver");
        comboBox.getItems().add("Ete");
        comboBox.getSelectionModel().selectFirst();
        Button charger = new Button("charger");
        buttonBox.getChildren().addAll(comboBox, charger);

        // ================ event-handler pour le bouton de chargement ================

        charger.setOnMouseClicked((event) -> {
            chargerEvent();
        });
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // ================ definition de la table de cours ================

        table = new TableView<Course>();
        table.setEditable(true);
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        TableColumn<Course, String> coursCol = new TableColumn<>("Cours");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        coursCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codeCol, coursCol);

        // ici on specifie que la table contient toujours les cours dans le arrayList activeCourses
        table.setItems(FXCollections.observableArrayList(activeCourses));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ================ event-handler pour la selection d'un cours ================

        table.setOnMouseClicked((event) -> {
            if (event.getClickCount() > 0) {
                selectedCourse = table.getSelectionModel().getSelectedItem();
            }
        });


        // ================ on additionne tout les elements au HBox gauche ================

        leftSide.getChildren().addAll(courseListText, table, buttonBox, new Separator());
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setSpacing(10);


        // ===================================================================
        // ================ addition d'element au cote droit ================
        // ===================================================================

        // texte en haut du cote droit
        Text registerText = new Text("Formulaire d'inscription");
        registerText.setFont(Font.font("serif", 20));
        rightSide.getChildren().add(registerText);

        // ================ Elements pour le champs Prenom ================

        Text prenom = new Text("Prenom");
        prenomTextField = new TextField();
        HBox prenomBox = new HBox();
        prenomBox.getChildren().addAll(prenom, prenomTextField);
        prenomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(prenomBox);

        // ================ Elements pour le champs Nom ================

        Text nom = new Text("Nom");
        nomTextField = new TextField();
        HBox nomBox = new HBox();
        nomBox.getChildren().addAll(nom, nomTextField);
        nomBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(nomBox);

        // ================ Elements pour le champs Email ================

        Text email = new Text("Email");
        emailTextField = new TextField();
        HBox emailBox = new HBox();
        emailBox.getChildren().addAll(email, emailTextField);
        emailBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(emailBox);

        // ================ Elements pour le champs Matricule ================

        Text matricule = new Text("Matricule");
        matriculeTextField = new TextField();
        HBox matriculeBox = new HBox();
        matriculeBox.getChildren().addAll(matricule, matriculeTextField);
        matriculeBox.setAlignment(Pos.CENTER);
        rightSide.getChildren().add(matriculeBox);

        // ================ Elements et event-handler pour le bouton envoyer ================

        Button envoyer = new Button("envoyer");
        envoyer.setOnMouseClicked((event) -> {
            inscrireEvent(primaryStage);
        });

        // ================ on additionne tout les elements au HBox droit ================

        rightSide.getChildren().add(envoyer);
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setSpacing(10);

        // ===================================================================
        // ================ on combine le cote gauche et droit ================
        // ===================================================================

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        root.getChildren().addAll(sep1, leftSide, sep2, rightSide);
        root.setSpacing(20);
        root.setAlignment(Pos.CENTER);

        // ================ definition finale de la scene ================

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Inscription UdeM");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * methode apellée lorsque le bouton d'envoie d'inscription est clique
     *
     * @param stage le State de l'application
     */
    static public void inscrireEvent(Stage stage){
        client.establishConnection(PORT, HOST);
        RegistrationForm forme = getRegistrationInfo();
        String response = client.sendRegistrationRequest(forme);
        processRegistrationResponse(response, stage);
    }

    /**
     * methode apellée lorsque le bouton de chargement de cours est clique
     */
    static public void chargerEvent(){
        client.establishConnection(PORT, HOST);
        String session = getCourseListSessionInfo();
        ArrayList<Course> cours = client.sendCourseListRequest(session);
        processCourseListResponse(session, cours);
    }

    /**
     * prend les informations entree par l'utilisateur et retourne la forme d'inscription
     *
     * @return un objet RegistrationForm
     */
    static public RegistrationForm getRegistrationInfo(){

        // ================ lecture de tout les TextFields ================

        String prenom = prenomTextField.getText();
        String nom = nomTextField.getText();
        String email = emailTextField.getText();
        String matricule = matriculeTextField.getText();

        return new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
    }

    /**
     * Trouve la session active en ce moment sur le GUI
     *
     * @return the string
     */
    static public String getCourseListSessionInfo(){
        // ================ Lecture de la Combobox pour trouver la session ================
        String session = (String) comboBox.getValue();
        return session;
    }

    /**
     * prise en charge de la reponse d'inscription du serveur
     *
     * @param response la reponse du serveur
     * @param stage    la Stage actuel
     */
    static public void processRegistrationResponse(String response, Stage stage){
        // ================ on ouvre une nouvelle fenetre avec le message ================
        System.out.println(response);
        showPopupWindow(response, stage);
    }

    /**
     * Process course list response.
     *
     * @param session the session
     * @param cours   the cours
     */
    static public void processCourseListResponse(String session, ArrayList<Course> cours){
        // ================ on dit a la table d'utiliser l'array cours ================
        activeCourses = cours;
        table.setItems(FXCollections.observableArrayList(activeCourses));
    }

    /**
     * methode qui ouvre une nouvelle fenetre et ecrit un message
     *
     * @param message le message a imprimer
     * @param owner   le Stage principal
     */
    static public void showPopupWindow(String message, Stage owner) {

        // ================ creation d'un nouveau stage ================
        Stage popupWindow = new Stage();
        popupWindow.initModality(Modality.WINDOW_MODAL);
        popupWindow.initOwner(owner);
        popupWindow.setTitle("Message");

        // ================ creation de l'element du message ================
        Label messageLabel = new Label(message);
        VBox root = new VBox(messageLabel);
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        // ================ on cree une nouvelle scene ================
        Scene popupScene = new Scene(root, 500, 60);
        popupWindow.setScene(popupScene);
        popupWindow.showAndWait();
    }
}
