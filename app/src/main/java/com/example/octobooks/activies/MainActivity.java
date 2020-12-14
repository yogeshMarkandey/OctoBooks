package com.example.octobooks.activies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.octobooks.R;
import com.example.octobooks.adapters.RVAdapter;
import com.example.octobooks.data.Books;
import com.example.octobooks.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    public static final String DEFAULT_SEARCH_TERM = "Maths";



    // widgets
    private RecyclerView recyclerView;
    private TextView searchText;
    private Spinner spinner;
    private ProgressBar progressBar;
    private View errorView, errorNotFound;
    private Button retryButton;
    private CoordinatorLayout constraintLayout;
    private Toolbar toolbar;

    // variables

    private MainViewModel model;
    private RVAdapter adapter;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        constraintLayout = findViewById(R.id.main_coordinator_layout);

        progressBar = findViewById(R.id.progress_bar);
        errorView = findViewById(R.id.error_box);
        retryButton = errorView.findViewById(R.id.button_retry);
        errorView.setVisibility(View.GONE);
        errorNotFound = findViewById(R.id.error_box_not_found);
        spinner = findViewById(R.id.spinner_class);
        searchText = findViewById(R.id.textView_search);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBooks(model.getSearchTerm());
            }
        });



        setupSpinner();     // Setting up Spinner


        // RecyclerView Adapter
        adapter = new RVAdapter();
        recyclerView.setAdapter(adapter);


        // Initializing ViewModel
        model = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        setupObservers();   // Setting up Observers


        searchText.setText("Books For " + model.getSearchTerm());
        model.searchBooks(model.getSearchTerm());
    }

    // Method For Initialising Observers
    private void setupObservers(){
        model.getListBooksLiveData().observe(MainActivity.this, new Observer<List<Books>>() {
            @Override
            public void onChanged(List<Books> books) {
                try {
                    model.getRepository().setAllBooks(model.getRepository().allBooksInDatabase());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        model.getRepository().getProgressLiveData().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //  For Showing ProgressBar

                if(aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        model.getQueryList().observe(MainActivity.this, new Observer<List<Books>>() {
            @Override
            public void onChanged(List<Books> books) {
                // For observing Books on RecyclerView

                adapter.submitList(books);
                model.setOriginalList(books);
            }
        });

        model.getNetworkLiveData().observe(MainActivity.this, new Observer<Boolean>() {
            // For observing Network Connectivity

            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean){
                    // if  not connected Show Snackbar.

                    Snackbar.make(constraintLayout, "You are Offline.Check Your Internet Connection.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        model.getErrorLiveData().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //   For observing Error Message

                if(aBoolean){
                    recyclerView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                }
            }
        });

        model.getNoResultLiveData().observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if(aBoolean){
                    recyclerView.setVisibility(View.GONE);
                    errorNotFound.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    errorNotFound.setVisibility(View.GONE);
                }
            }
        });

    }


    //  Method to setup Spinner
    private void setupSpinner(){
        ArrayAdapter<CharSequence> adapterClassName = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapterClassName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterClassName);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String std = adapterView.getItemAtPosition(i).toString();
                List<Books> filterList =  model.filterClass(std, adapter.getCurrentList());
                adapter.submitList(filterList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }



    // Method to Search Books
    private void searchBooks(String query){

        model.setSearchTerm(query);
        searchText.setText("Books for " + model.getSearchTerm());
        model.searchBooks(model.getSearchTerm());
        spinner.setSelection(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerView.scrollToPosition(0);
                searchBooks(query);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;

    }
}