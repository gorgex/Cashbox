package io.github.gorgex.cashbox.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.gorgex.cashbox.R;
import io.github.gorgex.cashbox.adapters.ProductsRecyclerAdapter;
import io.github.gorgex.cashbox.model.Product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProductsRecyclerAdapter.OnProductClickListener, ProductsRecyclerAdapter.OnProductLongClickListener, ProductCreateDialog.ProductCreateDialogListener, ProductEditDialog.ProductEditDialogListener {

    ArrayList<Product> products = new ArrayList<>();
    int selected;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ProductsRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;
    ExtendedFloatingActionButton fab;
    ActionMode actionMode;
    int actionModeState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new ProductsRecyclerAdapter(products, this, this);
        recyclerView.setAdapter(adapter);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCreateDialog dialog = new ProductCreateDialog();
                dialog.show(getSupportFragmentManager(), "Create Product");
                if(actionModeState == 1) {
                    actionMode.finish();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    fab.shrink();
                } else if(dy < 0) {
                    fab.extend();
                }
            }
        });
    }

    @Override
    public void onProductClick(int position) {
        if(actionModeState == 1) {
            actionMode.finish();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
            intent.putExtra("product_name", products.get(position).getName());
            intent.putExtra("product_quantity", products.get(position).getQuantity());
            startActivity(intent);
        }
    }

    @Override
    public void onProductLongClick(int position) {
        selected = position;
        layoutManager.findViewByPosition(position).setBackgroundColor(getResources().getColor(R.color.colorSelected));
        actionMode = startSupportActionMode(callback);
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionModeState = 1;
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            mode.setTitle("Choose your option");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit:
                    ProductEditDialog dialog = new ProductEditDialog(products.get(selected).getName(), products.get(selected).getPrice(), products.get(selected).getQuantity());
                    dialog.show(getSupportFragmentManager(), "Edit Product");
                    mode.finish();
                    return true;
                case R.id.delete:
                    products.remove(selected);
                    adapter.notifyItemRemoved(selected);
                    if(!fab.isExtended()) {
                        fab.extend();
                    }
                    saveData();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            layoutManager.findViewByPosition(selected).setBackgroundColor(getResources().getColor(R.color.colorDeselected));
            actionModeState = 0;
            actionMode = null;
        }
    };

    @Override
    public void createProduct(String name, double price, double quantity) {
        products.add(new Product(name, price, quantity));
        adapter.notifyDataSetChanged();
        saveData();
    }

    @Override
    public void editProduct(String name, double price, double quantity) {
        products.get(selected).setName(name);
        products.get(selected).setPrice(price);
        products.get(selected).setQuantity(quantity);
        adapter.notifyItemChanged(selected);
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(products);
        editor.putString("products", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("products", null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        products = gson.fromJson(json, type);

        if(products == null) {
            products = new ArrayList<>();
        }
    }
}
