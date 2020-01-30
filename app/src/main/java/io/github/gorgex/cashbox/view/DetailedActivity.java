package io.github.gorgex.cashbox.view;

import androidx.appcompat.app.AppCompatActivity;
import io.github.gorgex.cashbox.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class DetailedActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    TextView inStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        toolbar = findViewById(R.id.toolbar_detailed);
        intent = getIntent();
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
        inStock.setText(intent.getStringExtra("in_stock"));
    }
}
