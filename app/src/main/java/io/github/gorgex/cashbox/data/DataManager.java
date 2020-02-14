package io.github.gorgex.cashbox.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.github.gorgex.cashbox.model.Product;

import static android.content.Context.MODE_PRIVATE;

public class DataManager {

    private Context context;
    private ArrayList<Product> products;
    private SharedPreferences sharedPreferences;

    public DataManager(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void saveData() {
        sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(products);
        editor.putString("products", json);
        editor.apply();
    }

    public ArrayList<Product> loadData() {
        sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("products", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        products = gson.fromJson(json, type);

        if (products == null) {
            products = new ArrayList<>();
        }

        return products;
    }
}
