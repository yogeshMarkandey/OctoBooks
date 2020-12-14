package com.example.octobooks.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.octobooks.data.Books;

import java.util.List;


@Dao
public interface BooksDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBook(Books book);

    /*@Delete
    void deleteBooks();*/

    /*@Query("SELECT * FROM Books_List WHERE subject LIKE '% :sub%'")
    List<Books> searchBooks(String sub);*/


    @Query("SELECT * FROM books_list ")
    LiveData<List<Books>> getAllBooks();

    @Query("SELECT * FROM books_list ")
    List<Books> getBooks();
}
