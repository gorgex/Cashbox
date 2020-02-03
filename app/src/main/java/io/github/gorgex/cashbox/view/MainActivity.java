package io.github.gorgex.cashbox.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.gorgex.cashbox.R;
import io.github.gorgex.cashbox.adapters.ProductsRecyclerAdapter;
import io.github.gorgex.cashbox.data.DataManager;
import io.github.gorgex.cashbox.model.Product;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class MainActivity extends AppCompatActivity implements ProductsRecyclerAdapter.OnProductClickListener, ProductsRecyclerAdapter.OnProductLongClickListener, ProductCreateDialog.ProductCreateDialogListener, ProductEditDialog.ProductEditDialogListener, ProductBuyDialog.ProductBuyDialogListener {

    private ArrayList<Product> products = new ArrayList<>();
    private int selected;
    private ProductsRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ExtendedFloatingActionButton fab;
    private ActionMode actionMode;
    private DataManager dataManager;
    private int actionModeState = 0;
    private boolean swipeBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = new DataManager(this, products);
        products = dataManager.loadData();
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        Objects.requireNonNull(layoutManager.findViewByPosition(position)).setBackgroundColor(getResources().getColor(R.color.colorSelected));
        actionMode = startSupportActionMode(callback);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {

            if(swipeBack) {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final double third = c.getWidth() / 3;
            if(actionState == ACTION_STATE_SWIPE) {
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                        if(swipeBack) {
                            if(dX < -third) {
                                selected = viewHolder.getAdapterPosition();
                                Snackbar.make(recyclerView, "Sell", Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                            } else if(dX > third) {
                                selected = viewHolder.getAdapterPosition();
                                ProductBuyDialog dialog = new ProductBuyDialog();
                                dialog.show(getSupportFragmentManager(), "Buy a Product");
                            }
                        }
                        return false;
                    }
                });
            }

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.colorNegative))
                    .addSwipeLeftLabel("Sell").setSwipeLeftLabelColor(getResources().getColor(android.R.color.white))
                    .addSwipeRightBackgroundColor(getResources().getColor(R.color.colorPositive))
                    .addSwipeRightLabel("Buy").setSwipeRightLabelColor(getResources().getColor(android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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
                    dataManager.saveData();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Objects.requireNonNull(layoutManager.findViewByPosition(selected)).setBackgroundColor(getResources().getColor(R.color.colorDeselected));
            actionModeState = 0;
            actionMode = null;
        }
    };

    @Override
    public void createProduct(String name, double price, double quantity) {
        products.add(new Product(name, price, quantity));
        adapter.notifyDataSetChanged();
        dataManager.saveData();
    }

    @Override
    public void editProduct(String name, double price, double quantity) {
        products.get(selected).setName(name);
        products.get(selected).setPrice(price);
        products.get(selected).setQuantity(quantity);
        adapter.notifyItemChanged(selected);
        dataManager.saveData();
    }

    @Override
    public void buyProduct(double quantity) {
        double inStock = products.get(selected).getQuantity();
        products.get(selected).setQuantity(inStock + quantity);
        adapter.notifyItemChanged(selected);
        dataManager.saveData();
    }
}
