package com.example.octobooks.viewmodel;

import android.app.Application;


import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.octobooks.activies.MainActivity;
import com.example.octobooks.data.Books;
import com.example.octobooks.repository.MainRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {


    private MainRepository repository;
    private LiveData<List<Books>> listBooksLiveData ;

    private MutableLiveData<List<Books>> queryList;
    private MutableLiveData<Boolean> progressLiveData;
    private MutableLiveData<Boolean> networkLiveData;
    private MutableLiveData<Boolean> errorLiveData;
    private MutableLiveData<Boolean> noResultLiveData;

    private List<Books> originalList;

    private String searchTerm;


    public MainViewModel(Application application) throws ExecutionException, InterruptedException {
        super(application);


        repository = new MainRepository(application);

        listBooksLiveData = repository.getListOfBooksLiveData();
        queryList = repository.getQueryList();
        progressLiveData = repository.getProgressLiveData();
        networkLiveData = repository.getNetworkStateLiveData();
        errorLiveData = repository.getErrorLiveData();
        noResultLiveData = repository.getNoResultFoundLiveData();

        searchTerm = MainActivity.DEFAULT_SEARCH_TERM;
    }

    //Getters
    public LiveData<List<Books>> getListBooksLiveData() {
        return listBooksLiveData;
    }

    public MainRepository getRepository() {
        return repository;
    }

    public MutableLiveData<List<Books>> getQueryList() {
        return queryList;
    }

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public MutableLiveData<Boolean> getNetworkLiveData() {
        return networkLiveData;
    }

    public void setOriginalList(List<Books> originalList) {
        this.originalList = originalList;
    }

    public MutableLiveData<Boolean> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getNoResultLiveData() {
        return noResultLiveData;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    //Method to searchBooks
    public void searchBooks(String query){
        repository.initializeSearch(query);
    }

    // Method to filter list of books
    public List<Books> filterClass(String cls, List<Books> list){

        if(originalList == null){
            originalList = new ArrayList<>(list);
        }
        List<Books> returnList = new ArrayList<>() ;

        switch (cls){
            case "Select class":
                returnList = new ArrayList<>(originalList);
                break;

            case "Class 10":
                for (Books b :
                        originalList) {
                    if(b.getClass_name().equals("10th")){
                        returnList.add(b);
                    }
                }
                break;
            case "Class 9":
                for (Books b :
                        originalList) {
                    if(b.getClass_name().equals("9th")){
                        returnList.add(b);
                    }
                }
                break;

            case "Class 8":
                for (Books b :
                        originalList) {
                    if(b.getClass_name().equals("8th")){
                        returnList.add(b);
                    }
                }
                break;

            case "Class 7":
                for (Books b :
                        originalList) {
                    if(b.getClass_name().equals("7th")){
                        returnList.add(b);
                    }
                }
                break;

            case "Class 6":
                for (Books b :
                        originalList) {
                    if(b.getClass_name().equals("6th")){
                        returnList.add(b);
                    }
                }
                break;

        }

        return returnList;
    }
}
