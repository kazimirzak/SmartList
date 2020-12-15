package com.example.smartlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlist.Database.Product;
import com.example.smartlist.Database.Repository;

import java.util.List;

public class SwapItem extends AppCompatActivity {

    /* Activity used to swap an item on the MainActivity List.*/

    private int productId;
    private String search;
    private SearchType searchType;
    private ItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_item);
        // Get the contents of the intent and initialize everything.
        Intent intent = getIntent();
        productId = intent.getIntExtra(MainActivity.PRODUCT_ID_CODE, -1);
        search = intent.getStringExtra(MainActivity.SEARCH_CODE);
        searchType = (SearchType) intent.getSerializableExtra(MainActivity.SEARCH_TYPE_CODE);
        initRecyclerView();
        populateAdapter();
    }

    /**
     * Initializes the activities recycler view. Sets the adapter to be the ItemList Adapter.
     * Uses a Linear Layout Manager.
     */

    private void initRecyclerView() {
        adapter = new ItemListAdapter(this, productId, searchType, search);
        RecyclerView itemView = findViewById(R.id.itemView);
        itemView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemView.setLayoutManager(linearLayoutManager);
    }

    /**
     * Depending on the searchType, it will populate the adapter, and thereby the recycler view.
     */

    private void populateAdapter() {
        if(searchType == SearchType.CUSTOM) {
            List<Product> products = Repository.getProductsByRawQuery(Utility.stringSplitRawQuery(search));
            products.sort(Utility.bestMatchAndPriceComparator(search));
            adapter.addProducts(products);
        } else { // must be SPECIFIC
            adapter.addProducts(Repository.getCheapestProductsByDescription(search));
        }
    }
}