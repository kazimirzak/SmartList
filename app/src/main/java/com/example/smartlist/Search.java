package com.example.smartlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.smartlist.Database.Product;
import com.example.smartlist.Database.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Search extends AppCompatActivity {

    /* Activity used to search for products */

    private List<Product> currentProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize everything
        currentProducts = Repository.getProductsWithHits();
        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentProducts);
        ListView listView = findViewById(R.id.searchView);
        listView.setAdapter(adapter);

        initSearchView(adapter);
        initListView(listView);
    }

    /**
     * Initializes the searchView. On query change it will query the database with the new query
     * and update the ListViews adapter with the results.
     * If the searchView get a submit we called buttonSubmit.
     */
    private void initSearchView(ArrayAdapter<Product> adapter) {
        SearchView searchView = findViewById(R.id.itemSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String input) {
                buttonSubmit(null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                if(input.length() >= 2) {
                    List<Product> products = Repository.getProductsByRawQuery(Utility.stringSplitRawQueryGroupByDescription(input));
                    products.sort(Utility.bestMatchComparator(input));
                    currentProducts.clear();
                    currentProducts.addAll(products);
                } else {
                    currentProducts.clear();
                    currentProducts.addAll(Repository.getProductsWithHits());
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    /**
     * Firsts checks whether the searchView has more than 1 letter. If it does, it goes onto
     * creating an Intent and inserting the result from the query that the user typed.
     */

    public void buttonSubmit(View v) {
        SearchView searchView = findViewById(R.id.itemSearch);
        if(searchView.getQuery().length() >= 2) {
            Intent returnValue = new Intent();
            returnValue.putExtra(MainActivity.SEARCH_TYPE_CODE, SearchType.CUSTOM);
            returnValue.putExtra(MainActivity.SEARCH_CODE, searchView.getQuery().toString());
            submit(returnValue);
        }
    }

    /**
     * Takes the intent and returns it to the calling activity. (Shuts the current activity down)
     */
    private void submit(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Initializes a click listener on the items of the listview, such that when one is pressed it
     * will call submit with the correct intent.
     */

    private void initListView(ListView listView) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent returnValue = new Intent();
            returnValue.putExtra(MainActivity.SEARCH_TYPE_CODE, SearchType.SPECIFIC);
            returnValue.putExtra(MainActivity.SEARCH_CODE, currentProducts.get(position).description);
            submit(returnValue);
        });
    }
}