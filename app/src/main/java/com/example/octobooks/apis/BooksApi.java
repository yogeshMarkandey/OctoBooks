package com.example.octobooks.apis;

import com.example.octobooks.data.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BooksApi {

    @GET("test/prod/books?subject=Hindi")
    Call<ApiResponse> getBooks();

    @GET("test/prod/books")
    Call<ApiResponse> getBooks(@Query("subject") String subject);
}
