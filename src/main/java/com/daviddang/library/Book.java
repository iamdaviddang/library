package com.daviddang.library;

public class Book {
    private int id;
    private final String name;
    private final String author;
    private final int publication;
    private final String genres;
    private final String availability;



    private final String book_return_date;

//    public Book(int id, String name, String author, int publication, String genres, String availability) {
//        this.id = id;
//        this.name = name;
//        this.author = author;
//        this.publication = publication;
//        this.genres = genres;
//        this.availability = availability;
//    }

    public Book(int id, String name, String author, int publication, String genres, String availability, String book_return_date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publication = publication;
        this.genres = genres;
        this.availability = availability;
        this.book_return_date = book_return_date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublication() {
        return publication;
    }

    public String getGenres() {
        return genres;
    }

    public String getAvailability() {
        return availability;
    }

    public String getBook_return_date() {
        return book_return_date;
    }
}
