package com.example.smartlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlist.Database.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter {

    /* Pretty much the same as ProductListAdapter, just uses a slightly different XML file and very
    *  minor changes to the code. See ProductListAdapter for documentation (Dont have time)
    */

    private final List<Product> products;
    private final Context context;
    private final int pickedProductId;
    private final SearchType searchType;
    private final String search;

    ItemListAdapter(Context context, int productId, SearchType searchType, String search) {
        this.products = new ArrayList<>();
        this.context = context;
        this.pickedProductId = productId;
        this.searchType = searchType;
        this.search = search;
        System.out.println("THE PRODUCT ID:" + productId);
    }

    void addProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_item_view, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = products.get(position);
        System.out.println("Position: " + position + " Product id: " + product.id);
        if(product.id == this.pickedProductId) {
            holder.itemView.findViewById(R.id.shroud).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.shroud).setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            MainActivity.incompleteAdapter.addProduct(product, searchType, search);
            ((Activity) context).finish();
        });
        ProductViewHolder productViewHolder = (ProductViewHolder) holder;
        productViewHolder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder {

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

        void bind(Product product) {
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
