package com.daviddang.library;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static int loggedUserId;
    public static String loggedUserUsername;
    public static String loggedUserPassword;
    public static String loggedUserEmail;
    public static String loggedUserName;
    public static String loggedUserSurname;
    public static String loggedUserAddress;

    public static void main(String[] args) {
        launch();
    }
}
