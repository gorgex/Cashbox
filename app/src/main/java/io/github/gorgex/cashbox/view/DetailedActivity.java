package io.github.gorgex.cashbox.view;

import androidx.appcompat.app.AppCompatActivity;
import io.github.gorgex.cashbox.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

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

        toolbar = findViewById(R.id.toolbar_detailed);
        toolbar.setTitle(intent.getStringExtra("product_name"));
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inStock = findViewById(R.id.inStock);

        double stock = intent.getDoubleExtra("product_quantity", 0);
        String type = intent.getStringExtra("product_type");
        if(stock > 0) {
            assert type != null;
            if(type.equals("psc")) {
                inStock.setText(String.format(getResources().getString(R.string.in_stock), NumberFormat.getInstance().format(stock), "psc"));
            } else {
                inStock.setText(String.format(getResources().getString(R.string.in_stock), NumberFormat.getInstance().format(stock), "kg"));
            }
            inStock.setTextColor(getResources().getColor(R.color.colorPositive));
        } else {
            inStock.setText(getResources().getText(R.string.out_of_stock));
            inStock.setTextColor(getResources().getColor(R.color.colorNegative));
        }
    }
}
