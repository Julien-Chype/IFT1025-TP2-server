module client {
    requires javafx.controls;
    requires javafx.fxml;
    exports client;
    exports server.models;
    opens client to javafx.graphics;
    opens server.models to javafx.graphics;
}