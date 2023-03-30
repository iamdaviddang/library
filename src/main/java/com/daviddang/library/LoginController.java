package com.daviddang.library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class LoginController extends Application{

    Connection conn = MyConnection.getConnection();
    ResultSet rs = null;
    PreparedStatement ps = null;

    Stage stage = new Stage();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;
    @FXML
    private Button loginButton;

    @FXML
    public void loginButtonPressed(ActionEvent event) {



        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equalsIgnoreCase("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Username or Password field is empty");
            alert.showAndWait();
        } else if (password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Username or Password field is empty");
            alert.showAndWait();
        } else {
            try {
                // připojení k databázi a ověření jména a hesla
                PreparedStatement stmt = conn.prepareStatement("SELECT password FROM user WHERE username = ?");
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword.equals(password)) {
                        // uživatel úspěšně přihlášen
                        try {
                            String query = "SELECT * FROM `user` WHERE `username`=? AND `password`=?";
                            ps = conn.prepareStatement(query);
                            ps.setString(1, username);
                            ps.setString(2, password);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                int idDB = rs.getInt(1);
                                String usernameDB = rs.getString(2);
                                String emailDB = rs.getString(4);
                                String surnameDB = rs.getString(6);
                                String nameDB = rs.getString(5);
                                String passwordDB = rs.getString(3);
                                String addressDB = rs.getString(7);


                                // načtení nového FXML souboru a vytvoření nového okna
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("Library.fxml"));
                                Parent root = loader.load();
                                Stage newStage = new Stage();
                                newStage.setScene(new Scene(root));
                                newStage.setTitle("Library");

                                LibraryController controller = loader.getController();
                                controller.setUser(usernameDB);

                                updateUsername(usernameDB);
                                loggedUserId = idDB;
                                loggedUserUsername = usernameDB;
                                loggedUserPassword = passwordDB;
                                loggedUserEmail = emailDB;
                                loggedUserName = nameDB;
                                loggedUserSurname = surnameDB;
                                loggedUserAddress = addressDB;



                                // zavření aktuálního okna a zobrazení nového
                                stage.close();
                                newStage.show();

                                rs.close();
                                ps.close();
                                conn.close();
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Login Failed! Username or Password is wrong!");
                                alert.showAndWait();
                            }
                        } catch (SQLException ex) {
                            ex.getStackTrace();
                            errorMessage.setText("Invalid username or password");
                            System.out.println("Login failed");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // zobrazení upozornění o nesprávném hesle
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Invalid password!");
                        alert.showAndWait();
                    }
                } else {
                    // zobrazení upozornění o nesprávném jménu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("User with this username does not exist!");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateUsername(String username) {
        try {
            File file = new File("userLogged.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();

            FileWriter writer2 = new FileWriter(file);
            writer2.write(username);
            writer2.flush();
            writer2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registerButtonPressed(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Register");

        // zavření aktuálního okna a zobrazení nového
        stage.close();
        newStage.show();

    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        loginButton.fire();
    }
}
