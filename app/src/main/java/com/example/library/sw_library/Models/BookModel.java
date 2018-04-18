package com.example.library.sw_library.Models;

/*
model for book
 */

public class BookModel {

    private int bookID ;
    private String name;
    private String []authors;
    private String category;

    public BookModel(int bookID, String name, String []authors, String category) {
        this.bookID = bookID;
        this.name = name;
        this.authors = authors;
        this.category = category;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
