package com.example.octobooks.repository;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.octobooks.apis.BooksApi;
import com.example.octobooks.data.ApiResponse;
import com.example.octobooks.data.Books;
import com.example.octobooks.networking.RetrofitClient;
import com.example.octobooks.room.BooksDao;
import com.example.octobooks.room.BooksDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRepository {
    private static final String TAG = "MainRepository";
    private BooksDao booksDao;
    private RetrofitClient client;
    private BooksApi booksApi;


    private Application application;
    private MutableLiveData<Boolean> networkStateLiveData = new MutableLiveData<>();


    private LiveData<List<Books>> listOfBooksLiveData;
    private List<Books> booksList;
    private List<Books> allBooks;
    private MutableLiveData<Boolean> progressLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Books>> queryList = new MutableLiveData<>();
    private MutableLiveData<Boolean> errorLiveData   = new MutableLiveData<>();
    private MutableLiveData<Boolean> noResultFoundLiveData = new MutableLiveData<>();

    // Constructor
    public MainRepository(Application application) throws ExecutionException, InterruptedException {
        BooksDatabase database = BooksDatabase.getInstance(application);

        this.application = application;

        booksDao = database.booksDao();

        listOfBooksLiveData = booksDao.getAllBooks();

        client = RetrofitClient.getInstance();
        booksApi = client.retrofit.create(BooksApi.class);

        allBooks = allBooksInDatabase();

    }




    //Driver Method to insert
    public void insertBook(List<Books> book){
        Log.d(TAG, "insertBook: Insert Book Called");
        new InsertBookAsynkTask(booksDao).execute(book);
    }



    // Inner Class for AsyncTask
    private static class InsertBookAsynkTask extends AsyncTask<List<Books>, Void, Void>{
        private static final String TAG = "insertBookAsynkTask";
        private  BooksDao dao;
        public InsertBookAsynkTask(BooksDao dao){
            this.dao = dao;
        }


        @Override
        protected Void doInBackground(List<Books>... lists) {
            Log.d(TAG, "doInBackground: initializing insert");

            int count=0;
            for (Books b :
                    lists[0]) {
                dao.insertBook(b);
                count++;
            }
            Log.d(TAG, "doInBackground: books added = " + count);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



        }
    }


    public List<Books> allBooksInDatabase() throws ExecutionException, InterruptedException {

        return new GetDataAsyncTask(booksDao).execute().get();
    }

    private static class GetDataAsyncTask extends AsyncTask<Void, Void, List<Books>>{
        private BooksDao dao;
        public GetDataAsyncTask(BooksDao dao){
            this.dao = dao;
        }


        @Override
        protected List<Books> doInBackground(Void... voids) {
            return dao.getBooks();
        }
    }

    // Getter for LiveData
    public LiveData<List<Books>> getListOfBooksLiveData() {
        return listOfBooksLiveData;
    }

    public MutableLiveData<List<Books>> getQueryList() {
        return queryList;
    }

    public List<Books> getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(List<Books> allBooks) {
        this.allBooks = allBooks;
    }

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public MutableLiveData<Boolean> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public MutableLiveData<Boolean> getNoResultFoundLiveData() {
        return noResultFoundLiveData;
    }

    public MutableLiveData<Boolean> getErrorLiveData() {
        return errorLiveData;
    }


    // Method to Initialize Search
    public void initializeSearch(String query){
        noResultFoundLiveData.postValue(false);
        errorLiveData.postValue(false);
        progressLiveData.setValue(true);


        if(allBooks == null || allBooks.isEmpty()){
            // If no books is DataBase

            checkNetworkState();    // Check Network State
            searchApi(query);       // Search Online
            return;

        }
        else if(!allBooks.isEmpty()){
            // If DataBase is not Empty
            // Search for Books

            List<Books> list = new ArrayList<>();

            for (Books b : allBooks) {
                if(b.getSubject().equals(query)){
                    list.add(b);
                }
            }

            if(list.isEmpty()){
                // if DataBase doesn't contains the searched Book
                // Search Online

                queryList.postValue(new ArrayList<Books>());
                checkNetworkState();    //  Check Network State
                searchApi(query);       // Search online

            }else {
                // if Database contains Books
                // then show it on RecyclerView

                queryList.postValue(list);
                progressLiveData.postValue(false);
            }

        }
    }




    // Method to Search Api for Books
    private void searchApi(String query){

        progressLiveData.postValue(true);   //  start progress bar

        Call<ApiResponse> responseCall =  booksApi.getBooks(query);

        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {


                if(response.isSuccessful()){

                    booksList  = response.body().getBooksList();

                    if(booksList != null){

                        queryList.postValue(booksList);
                        insertBook(booksList);          // Insert Books in Database
                        progressLiveData.postValue(false);

                        if (booksList.size()<=0){
                            // if Response doesn't contains any books
                            // "no result found"

                            noResultFoundLiveData.postValue(true);
                        }else {
                            noResultFoundLiveData.postValue(false);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                errorLiveData.postValue(true);
            }
        });
    }


    // Method For Checking Network State
    public boolean checkNetworkState(){
        ConnectivityManager conMgr = (ConnectivityManager)application.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

            // notify user you are online
            networkStateLiveData.postValue(true);
            return true;

        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

            // notify user you are not online
            //progressLiveData.postValue(false);
            networkStateLiveData.postValue(false);
            return false;
        }

        return false;
    }
}
