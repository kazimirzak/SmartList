package com.example.smartlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlist.Database.Product;
import com.example.smartlist.Database.Repository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter {

    /* Adapter used to show products in a recycler view */

    private final List<ProductData> products;
    public final int layoutResource;
    private final Context context;
    private ProductListAdapter otherAdapter;

    /**
     * Constructor. Context is the Context where the recyclerview is. The layout resource
     * is the ID of the xml file that should be used to render each item in the view.
     */

    ProductListAdapter(Context context, int layoutResource) {
        this.products = new ArrayList<>();
        this.layoutResource = layoutResource;
        this.context = context;
    }

    /**
     * Initializes the otherAdapter attribute. This was used such that i could have a complete
     * and incomplete adapter, and when removed from one could instantly be added to the other.
     */

    void initAdapter(ProductListAdapter otherAdapter) {
        this.otherAdapter = otherAdapter;
    }

    /**
     * Adds a product to the adapter. searchType and search must be given, as they are needed later.
     */

    void addProduct(Product product, SearchType searchType, String search) {
        addProduct(new ProductData(product, searchType, search));
    }

    /**
     * Adds a product to the list and sorts the list. The sorting order is: store > productCategory > description.
     */

    void addProduct(ProductData productData) {
        products.add(productData);
        products.sort((o1, o2) -> {
            if(o1.getProduct().store.compareTo(o2.getProduct().store) != 0) {
                return o1.getProduct().store.compareTo(o2.getProduct().store);
            } else if(o1.getProduct().productCategory.compareTo(o2.getProduct().productCategory) != 0) {
                return o1.getProduct().productCategory.compareTo(o2.getProduct().productCategory);
            } else {
                return o1.getProduct().description.compareTo(o2.getProduct().description);
            }
        });
        notifyDataSetChanged();
    }

    /**
     * Removed the item from the list at the given position.
     */

    ProductData removeProduct(int position) {
        ProductData result = products.remove(position);
        if(products.size() <= 0 && otherAdapter.getItemCount() <= 0) {
            ((Activity) context).findViewById(R.id.clearList).setVisibility(View.GONE);
        }
        notifyDataSetChanged();
        return result;
    }

    /**
     * Self-explanatory
     */

    void clearList() {
        products.clear();
        notifyDataSetChanged();
    }

    /* Called when the recyclerView needs to create a new item on teh list. */

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new ProductViewHolder(view);
    }

    /* method used to bind data to a item in the list. Will add an on click listener
    *  to the view where it increments or decrements the hits of the product depending
    *  on which list it is in. It will then remove it from this adapter and add it to otherAdapter.
    *  If the adapter is the incomplete kind, it will also add a long click that starts the swap
    *  item activity on long click.
    */

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductData product = products.get(position);
        holder.itemView.setOnClickListener(v -> {
            if(layoutResource == R.layout.product_bought_view) {
                product.getProduct().hits--;
            } else {
                product.getProduct().hits++;
            }
            Repository.updateProduct(product.getProduct());
            otherAdapter.addProduct(removeProduct(position));
        });
        if(layoutResource == R.layout.product_view) {
            holder.itemView.setOnLongClickListener(v -> {
                products.remove(position);
                Intent intent = new Intent(context, SwapItem.class);
                intent.putExtra(MainActivity.PRODUCT_ID_CODE, product.getProduct().id);
                intent.putExtra(MainActivity.SEARCH_CODE, product.getSearch());
                intent.putExtra(MainActivity.SEARCH_TYPE_CODE, product.getSearchType());
                System.out.println(product.getSearch());
                context.startActivity(intent);
                return true;
            });
        }
        ProductViewHolder productViewHolder = (ProductViewHolder) holder;
        productViewHolder.bind(product);
    }

    /**
     * Self-explanatory
     */

    @Override
    public int getItemCount() {
        return products.size();
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder {

        /** Handles binding data to an item in the list. */

        TextView description, category, store, packetPrice, packetSize, unitPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.productDescription);
            category = itemView.findViewById(R.id.productCategory);
            store = itemView.findViewById(R.id.store);
            packetPrice = itemView.findViewById(R.id.packetPrice);
            packetSize = itemView.findViewById(R.id.packetSize);
            unitPrice = itemView.findViewById(R.id.unitPrice);
        }

        void bind(ProductData productData) {
            Product product = productData.getProduct();
            DecimalFormat numberFormat = new DecimalFormat("0.##");
            String info = product.description;
            if(info.length() > 32) {
                info = info.substring(0, 30) + "..";
            }
            this.description.setText(info);
            this.category.setText(product.productCategory);
            this.store.setText(product.store);
            String packetPrice = numberFormat.format(product.packetPrice) + " kr.";
            this.packetPrice.setText(packetPrice);
            String packetPriceAndSize = numberFormat.format(product.packetSize) + product.packetSizeType;
            this.packetSize.setText(packetPriceAndSize);
            String unitPriceAndSize = numberFormat.format(product.unitPrice) + "/" +product.unitType;
            this.unitPrice.setText(unitPriceAndSize);
        }
    }
}
