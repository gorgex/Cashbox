package io.github.gorgex.cashbox.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        String quantity = products.get(position).getQuantity();

        holder.name.setText(name);
        holder.quantity.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name, quantity;
        OnProductClickListener onProductClickListener;
        OnProductLongClickListener onProductLongClickListener;

        ViewHolder(@NonNull View itemView, OnProductClickListener onProductClickListener, OnProductLongClickListener onProductLongClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
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
