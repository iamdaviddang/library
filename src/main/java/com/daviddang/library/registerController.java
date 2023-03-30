package com.daviddang.library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class registerController {
    Connection conn = MyConnection.getConnection();
    ResultSet rs = null;
    PreparedStatement ps = null;


    Stage stage = new Stage();

    @FXML
    private Label errorMessage;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSurename;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtPassword;

    @FXML
    public void RegisterButtonPressed(ActionEvent event) throws SQLException {

        String sql = "INSERT INTO `user`(`username`, `password`, `email`, `name`, `surename`, `Address`) VALUES (?,?,?,?,?,?)";

        String username = txtUsername.getText();
        String name = txtName.getText();
        String surename = txtSurename.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String password = txtPassword.getText();

        // Kontrola jména
        PreparedStatement statementJmeno = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE name = ?");
        statementJmeno.setString(1, username);
        ResultSet resultJmeno = statementJmeno.executeQuery();

        // Kontrola e-mailu
        PreparedStatement statementEmail = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE email = ?");
        statementEmail.setString(1, email);
        ResultSet resultEmail = statementEmail.executeQuery();

        if (resultJmeno.next() && resultEmail.next()) {
            int pocetJmen = resultJmeno.getInt(1);
            int pocetEmailu = resultEmail.getInt(1);

            if (pocetJmen > 0 && pocetEmailu > 0) { //kontrola jmena a zaroven emailu
                Alert alert = new Alert(Alert.AlertType.ERROR);
                txtUsername.setText("");
                alert.setTitle("Error");
                alert.setHeaderText("username and email already exist!");
                alert.showAndWait();
            } else if (pocetJmen > 0) { //kontrola jmena
                txtUsername.setText("");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("username already exists!");
                alert.showAndWait();
            } else if (pocetEmailu > 0) { //kontrola emailu
                Alert alert = new Alert(Alert.AlertType.ERROR);
                txtEmail.setText("");
                alert.setTitle("Error");
                alert.setHeaderText("email already exists!");
                alert.showAndWait();
            } else if (username.equals("") || name.equals("") || surename.equals("") || email.equals("") || address.equals("") || password.equals("")) { //kontrola prazdnych poli
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Some of the field is empty!");
                alert.showAndWait();
            }else {
                    // Registrace a zapis dat do databaze
                    try {
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, username);
                        ps.setString(2, password);
                        ps.setString(3, email);
                        ps.setString(4, name);
                        ps.setString(5, surename);
                        ps.setString(6, address);
                        ps.execute();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Account created");
                        alert.setHeaderText("New account successfully created");
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


                        resultJmeno.close();
                        resultEmail.close();
                        ps.close();
                        statementJmeno.close();
                        statementEmail.close();
                        conn.close();

                    } catch (SQLException ex) {
                        ex.getStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Make sure that every field is not empty!");
                        alert.showAndWait();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

            }
        }
    }


        @FXML
    public void backButtonPressed(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));

        // zavření aktuálního okna a zobrazení nového
        stage.close();
        newStage.show();
        newStage.setTitle("Login");
    }
}



