package io.github.gorgex.cashbox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.github.gorgex.cashbox.R;
import io.github.gorgex.cashbox.model.Product;

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private OnProductClickListener onProductClickListener;
    private OnProductLongClickListener onProductLongClickListener;

    public ProductsRecyclerAdapter(ArrayList<Product> products, OnProductClickListener onProductClickListener, OnProductLongClickListener onProductLongClickListener) {
        this.products = products;
        this.onProductClickListener = onProductClickListener;
        this.onProductLongClickListener = onProductLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ViewHolder(view, onProductClickListener, onProductLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = products.get(position).getName();
        double price = products.get(position).getPrice();
        double quantity = products.get(position).getQuantity();
        String type = products.get(position).getType();

        holder.name.setText(name);
        holder.price.setText(String.format(holder.itemView.getResources().getString(R.string.gel), NumberFormat.getInstance().format(price)));
        holder.quantity.setText(NumberFormat.getInstance().format(quantity));
        holder.type.setText(type);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView name, price, quantity, type;
        private OnProductClickListener onProductClickListener;
        private OnProductLongClickListener onProductLongClickListener;

        ViewHolder(@NonNull View itemView, OnProductClickListener onProductClickListener, OnProductLongClickListener onProductLongClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            type = itemView.findViewById(R.id.type);
            this.onProductClickListener = onProductClickListener;
            this.onProductLongClickListener = onProductLongClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProductClickListener.onProductClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onProductLongClickListener.onProductLongClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnProductClickListener {
        void onProductClick(int position);
    }

    public interface OnProductLongClickListener {
        void onProductLongClick(int position);
    }
}
