package com.daviddang.library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class EditProfileController extends Application{

    @FXML
    private TextField fieldUsername;

    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldSurname;
    @FXML
    private TextField fieldEmail;
    @FXML
    private TextField fieldCurrentPassword;

    @FXML
    private TextField fieldNewPassword;

    public void initialize() {
        fieldUsername.setPromptText(loggedUserUsername);
        fieldName.setPromptText(loggedUserName);
        fieldSurname.setPromptText(loggedUserSurname);
        fieldEmail.setPromptText(loggedUserEmail);
        fieldCurrentPassword.setPromptText(loggedUserPassword);
    }

    @FXML
    private void submitButton(ActionEvent event){

        String edittedUsername, edittedName, edittedSurname, edittedEmail, newPassword;

        if (Objects.equals(fieldUsername.getText(), "")){
            edittedUsername = fieldUsername.getPromptText();
        }else {
            edittedUsername = fieldUsername.getText();
        }

        if (Objects.equals(fieldName.getText(), "")){
            edittedName = fieldName.getPromptText();
        }else {
            edittedName = fieldName.getText();
        }

        if (Objects.equals(fieldSurname.getText(), "")){
            edittedSurname = fieldSurname.getPromptText();
        }else {
            edittedSurname = fieldSurname.getText();
        }

        if (Objects.equals(fieldEmail.getText(), "")){
            edittedEmail = fieldEmail.getPromptText();
        }else {
            edittedEmail = fieldEmail.getText();
        }

        if (Objects.equals(fieldNewPassword.getText(), "")){
            newPassword = fieldCurrentPassword.getPromptText();
        }else {
            newPassword = fieldNewPassword.getText();
        }

        //zde bude zapis do DB
        Connection conn = MyConnection.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String SQL = "UPDATE user SET username = ?, password = ?, email = ?, name = ?, surename = ? WHERE username = ?";
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, edittedUsername);
            ps.setString(2, newPassword);
            ps.setString(3, edittedEmail);
            ps.setString(4, edittedName);
            ps.setString(5, edittedSurname);
            ps.setString(6, loggedUserUsername);
            ps.execute();
            ps.close();
            conn.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account editted");
            alert.setHeaderText("Your account has been editted. Please login again.");
            alert.showAndWait();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Login");

            // zavření aktuálního okna a zobrazení nového
            stage.close();
            newStage.show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
