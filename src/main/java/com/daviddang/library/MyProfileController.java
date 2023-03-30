package com.daviddang.library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class MyProfileController extends LoginController {
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSurname;
    @FXML
    private Label lblEmail;


    public void setUsernameMP(String INPUTusername) {
        lblUsername.setText(INPUTusername);
    }

    public void setNameMP(String name) {
        lblName.setText(name);
    }

    public void setSurnameMP(String surname) {
        lblSurname.setText(surname);
    }

    public void setEmailMP(String email) {
        lblEmail.setText(email);
    }

    @FXML
    private TableView<Book> tableViewMP;

    public void initialize() {
        TableColumn<Book, Integer> idCol = (TableColumn<Book, Integer>) tableViewMP.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> nameCol = (TableColumn<Book, String>) tableViewMP.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Book, String> daysCol = (TableColumn<Book, String>) tableViewMP.getColumns().get(2);
        daysCol.setCellValueFactory(new PropertyValueFactory<>("book_return_date"));

        loadData();
    }

    private void loadData() {
        ObservableList<Book> books = FXCollections.observableArrayList();

        try {
            Connection conn = MyConnection.getConnection();
            String query = "SELECT * FROM books WHERE username =?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, loggedUserUsername);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("book_name");
                String author = rs.getString("author");
                int publication = rs.getInt("publication");
                String genres = rs.getString("genres");
                String availability = rs.getString("availability");
                String book_return_dateDB = rs.getString("book_return_date");

                // ziskani aktualniho datumu a datumu z DB a zjisteni rozdilu a nasledne vlozeni do controcteru
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                LocalDate futureDate = LocalDate.parse(book_return_dateDB, formatter);
                long daysBetweenLong = ChronoUnit.DAYS.between(today, futureDate);
                String book_return_date = String.valueOf(daysBetweenLong);

                Book book = new Book(id, name, author, publication, genres, availability, book_return_date);
                books.add(book);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableViewMP.setItems(books);
    }

    @FXML
    private void returnBookBtn(ActionEvent event) {
        SelectionModel<Book> selectionModel = tableViewMP.getSelectionModel();
        Book selectedRow = selectionModel.getSelectedItem();

        if (selectedRow != null) {
            //nacteni ID a jmena z oznaceneho radku
            int id_book = selectedRow.getId();

            // vyskoceni upozorneni o potvrzeni vyberu
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setHeaderText(null);
            alert.setContentText("Do you really want to return the book with ID: " + id_book);

            // zeptani se na ANO/NE potvrzeni
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // zde bude kod pro vraceni knihy knihy

                Connection conn = MyConnection.getConnection();
                ResultSet rs = null;
                PreparedStatement ps = null;

                try {

                    String SQL_return = "UPDATE books SET username = NULL, book_return_date= NULL WHERE id =?;";
                    ps = conn.prepareStatement(SQL_return);
                    ps.setString(1, String.valueOf(id_book));
                    ps.execute();

                    ps.close();
                    conn.close();

                    tableViewMP.getItems().clear();
                    loadData();
                    tableViewMP.getSelectionModel().clearSelection();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                tableViewMP.getSelectionModel().clearSelection();
            } else {
                // zde zpracujte storno akce
                tableViewMP.getSelectionModel().clearSelection();
            }
        } else {
            // Pokud uzivatel neoznacil zadny radek, vyskoci ERROR tabulka
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You have to mark a row. Try it again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        tableViewMP.getItems().clear();
        loadData();
    }

    @FXML
    private void editButton(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Password confirmation");
        alert.setHeaderText("Enter your password:");
        alert.setContentText(null);

        // Přidání pole pro zadání hesla
        PasswordField passwordField = new PasswordField();
        alert.getDialogPane().setContent(passwordField);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Heslo bylo zadáno a uživatel stiskl tlačítko OK
            String password = passwordField.getText();
            if (password.equals(loggedUserPassword)) {
                // Heslo je správné, otevřít nové okno pro editaci profilu
                try {
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("editProfile.fxml"));
                    Parent root = loader.load();
                    Scene editScene = new Scene(root);
                    Stage editStage = new Stage();
                    editStage.setScene(editScene);
                    editStage.setTitle("Edit profile");
                    editStage.show();
                    stage.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Heslo je nesprávné, zobrazit chybové hlášení
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("The password you entered is incorrect!");
                errorAlert.showAndWait();
            }
        }
    }
}
