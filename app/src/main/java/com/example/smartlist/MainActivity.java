package com.example.smartlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.example.smartlist.Database.Product;
import com.example.smartlist.Database.Repository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /* Main activity. */

    public static final int SEARCH_ACTIVITY_CODE = 1; // Request code for return on result
    public static final String SEARCH_TYPE_CODE = "b07f25ac5fd0c68e42bdc5d566b737d6"; // Code for where enum is.
    public static final String SEARCH_CODE = "faf25d7bd1f6dc041f1eb0a4ea303fca"; // Code for where data is.
    public static final String PRODUCT_ID_CODE = "79415fcbff2e5b96f8a95888178247fd"; // Code for where product data is.
    private ProductListAdapter completeAdapter;
    public static ProductListAdapter incompleteAdapter;

    /**
     * Since this is the first activity that is run when the app opens, we initialize the database
     * here.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init
        Context context = getApplicationContext();
        Repository.init(context);
        initProductViews();
    }

    /**
     * Initializes the two recyclerViews in this activity. Uses LinearLayoutManagers. The adapter is
     * ProductListAdapter. Also adds a swipe detector to them.
     */

    private void initProductViews() {
        completeAdapter = new ProductListAdapter(this, R.layout.product_bought_view);
        incompleteAdapter = new ProductListAdapter(this, R.layout.product_view);
        completeAdapter.initAdapter(incompleteAdapter);
        incompleteAdapter.initAdapter(completeAdapter);

        RecyclerView completeView = findViewById(R.id.completedItems);
        completeView.setAdapter(completeAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        completeView.setLayoutManager(linearLayoutManager);
        initSwipeDetector(completeView);

        RecyclerView incompleteView = findViewById(R.id.incompleteItems);
        incompleteView.setAdapter(incompleteAdapter);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        incompleteView.setLayoutManager(linearLayoutManager1);
        initSwipeDetector(incompleteView);
    }

    /**
     * Initializes a swipe detector for both left and right swipe on the recyclerView.
     */

    private static void initSwipeDetector(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                ProductListAdapter adapter = (ProductListAdapter) recyclerView.getAdapter();
                int position = viewHolder.getAdapterPosition();
                adapter.removeProduct(position);
            }
        };
        ItemTouchHelper swipeDetector = new ItemTouchHelper(callback);
        swipeDetector.attachToRecyclerView(recyclerView);
    }


    /**
     * Called when an activity returns that this activity is waiting for a result from.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_CODE && resultCode == RESULT_OK && data != null) {
            SearchType searchType = (SearchType) data.getSerializableExtra(SEARCH_TYPE_CODE);
            String search = data.getStringExtra(SEARCH_CODE);
            Product product;
            if(searchType == SearchType.CUSTOM) {
                List<Product> products = Repository.getProductsByRawQuery(Utility.stringSplitRawQuery(search));
                products.sort(Utility.bestMatchAndPriceComparator(search));
                product = products.get(0);
            } else { // must be SearchType.SPECIFIC
                product = Repository.getCheapestProductByDescription(search);
            }
            incompleteAdapter.addProduct(product, searchType, search);
            Button clearListButton = findViewById(R.id.clearList);
            clearListButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when button or searchView is pressed.
     */

    public void search(View view) {
        Intent search = new Intent(this, Search.class);
        startActivityForResult(search, SEARCH_ACTIVITY_CODE);
    }

    /**
     * Called when clearList button is presed.
     */

    public void clearList(View view) {
        completeAdapter.clearList();
        incompleteAdapter.clearList();
        Button clearListButton = findViewById(R.id.clearList);
        clearListButton.setVisibility(View.GONE);
    }

}