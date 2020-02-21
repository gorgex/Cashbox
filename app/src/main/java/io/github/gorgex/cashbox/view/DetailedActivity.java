package io.github.gorgex.cashbox.view;

import androidx.appcompat.app.AppCompatActivity;

import io.github.gorgex.cashbox.R;
import io.github.gorgex.cashbox.model.Product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;

public class DetailedActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    TextView inStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        intent = getIntent();
        Product product = intent.getParcelableExtra("product");

        toolbar = findViewById(R.id.toolbar_detailed);
        assert product != null;
        toolbar.setTitle(product.getName());
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inStock = findViewById(R.id.inStock);
        double stock = product.getQuantity();
        String type = product.getType();
        if (stock > 0) {
            inStock.setText(String.format(getResources().getString(R.string.in_stock), NumberFormat.getInstance().format(stock), type));
            inStock.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPositive));
        } else {
            inStock.setText(getResources().getText(R.string.out_of_stock));
            inStock.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNegative));
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
