package com.daviddang.library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class BooksController {
    @FXML
    private TableView<Book> tableView;

    public void initialize() {
        TableColumn<Book, Integer> idCol = (TableColumn<Book, Integer>) tableView.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> nameCol = (TableColumn<Book, String>) tableView.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Book, String> authorCol = (TableColumn<Book, String>) tableView.getColumns().get(2);
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, Integer> publicationCol = (TableColumn<Book, Integer>) tableView.getColumns().get(3);
        publicationCol.setCellValueFactory(new PropertyValueFactory<>("publication"));

        TableColumn<Book, String> genresCol = (TableColumn<Book, String>) tableView.getColumns().get(4);
        genresCol.setCellValueFactory(new PropertyValueFactory<>("genres"));

        TableColumn<Book, String> availabilityCol = (TableColumn<Book, String>) tableView.getColumns().get(5);
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        loadData(); // načtení dat z databáze a vložení do TableView
    }

    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        tableView.getItems().clear();
        loadData();
    }

    @FXML
    private void borrowBtn(ActionEvent event) {

        SelectionModel<Book> selectionModel = tableView.getSelectionModel();
        Book selectedRow = selectionModel.getSelectedItem();

        if (selectedRow != null){
            //nacteni ID a jmena z oznaceneho radku
            int id = selectedRow.getId();
            String name = selectedRow.getName();

            // vyskoceni upozorneni o potvrzeni vyberu
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setHeaderText(null);
            alert.setContentText("Do you really want to borrow the book: " + name + " with ID: " + id);

            // zeptani se na ANO/NE potvrzeni
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // zde bude kod pro pujceni knihy

                Connection conn = MyConnection.getConnection();
                ResultSet rs = null;
                PreparedStatement ps = null;
                LocalDate currentDate = LocalDate.now(); // ziskani aktualniho datumu
                LocalDate book_return_date = currentDate.plusMonths(1); //pricteni 1 mesic k aktualnimu datumu = datum kdyz uzivatel musi vratit knihu

                try {

                    String SQL_return = "UPDATE books SET username =?, book_return_date =? WHERE ID =?;";
                    ps = conn.prepareStatement(SQL_return);
                    ps.setString(1, readFromFile());
                    ps.setString(2, String.valueOf(book_return_date));
                    ps.setString(3, String.valueOf(id));
                    ps.execute();

                    ps.close();
                    conn.close();

                    tableView.getItems().clear();
                    loadData();
                    tableView.getSelectionModel().clearSelection();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                // zde zpracujte storno akce
                tableView.getSelectionModel().clearSelection();
            }
        }else{
            // Pokud uzivatel neoznacil zadny radek, vyskoci ERROR tabulka
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You have to mark a row. Try it again.");
            alert.showAndWait();
        }
    }

    public String readFromFile() {
        String userLogged = "";
        try {
            File file = new File("userLogged.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                userLogged += line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userLogged;
    }

    private void loadData() {
        ObservableList<Book> books = FXCollections.observableArrayList();

        try {
            Connection conn = MyConnection.getConnection();
            String query = "SELECT * FROM books WHERE username IS NULL AND availability =?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "yes");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("book_name");
                String author = rs.getString("author");
                int publication = rs.getInt("publication");
                String genres = rs.getString("genres");
                String availability = rs.getString("availability");
                String book_return_date = rs.getString("book_return_date");

                Book book = new Book(id, name, author, publication, genres, availability, book_return_date);
                books.add(book);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(books);
    }
}
