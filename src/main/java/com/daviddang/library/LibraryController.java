package com.daviddang.library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibraryController {



    Stage stage = new Stage();

    @FXML
    private Label lblusername;

    @FXML
    private BorderPane bp;

    @FXML
    private AnchorPane ap;

    public void setUser(String username) {
        lblusername.setText(username);
    }





    @FXML
    private void myProfile(ActionEvent event) throws IOException {

        try {
            Connection conn = MyConnection.getConnection();
            ResultSet rs = null;
            PreparedStatement ps = null;

            String query = "SELECT * FROM `user` WHERE `username`=?";
            ps = conn.prepareStatement(query);
            ps.setString(1, lblusername.getText());
            rs = ps.executeQuery();

            // ziskani dat z databaze
            if (rs.next()) {
                String username = rs.getString(2);
                String email = rs.getString(4);
                String surname = rs.getString(6);
                String name = rs.getString(5);

                // nastaveni hodnot pomoci metod z MyProfileController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MyProfile.fxml"));
                Parent root = loader.load();
                bp.setCenter(root);

                MyProfileController controller = loader.getController();
                controller.setUsernameMP(username);
                controller.setNameMP(name);
                controller.setSurnameMP(surname);
                controller.setEmailMP(email);

            }else {
                System.out.println("data load failed");
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    @FXML
    private void books(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Books.fxml"));
        bp.setCenter(root);

    }


    @FXML
    public void logOut(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Login");

        // zavření aktuálního okna a zobrazení nového
        stage.close();
        newStage.show();

    }

    public String getUser(){
        return lblusername.getText();
    }

}
