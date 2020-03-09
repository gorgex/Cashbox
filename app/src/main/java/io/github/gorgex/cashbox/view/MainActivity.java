package io.github.gorgex.cashbox.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class MainActivity extends AppCompatActivity implements ProductsRecyclerAdapter.OnProductClickListener, ProductsRecyclerAdapter.OnProductLongClickListener, ProductCreateDialog.ProductCreateDialogListener, ProductEditDialog.ProductEditDialogListener, ProductBuyDialog.ProductBuyDialogListener, ProductSellDialog.ProductSellDialogListener {

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
                if (actionModeState == 1) {
                    actionMode.finish();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.shrink();
                } else if (dy < 0) {
                    fab.extend();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onProductClick(int position) {
        if (actionModeState == 1) {
            actionMode.finish();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
            intent.putExtra("product", products.get(position));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onProductLongClick(int position) {
        if (actionModeState == 0) {
            selected = position;
            Objects.requireNonNull(layoutManager.findViewByPosition(position)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelected));
            actionMode = startSupportActionMode(callback);
        }
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

            if (swipeBack) {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (actionModeState == 1) {
                return 0;
            }
            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final double third = c.getWidth() / 3.0;
            if (actionState == ACTION_STATE_SWIPE) {
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                        if (swipeBack) {
                            if (dX < -third) {
                                selected = viewHolder.getAdapterPosition();
                                if (products.get(selected).getQuantity() <= 0) {
                                    Snackbar snackbar = Snackbar.make(recyclerView, "Out of stock!", Snackbar.LENGTH_SHORT);
                                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
                                    snackbar.setAnchorView(fab);
                                    snackbar.setAction("Add", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ProductBuyDialog dialog = new ProductBuyDialog(products.get(selected).getType());
                                            dialog.show(getSupportFragmentManager(), "Buy a Product");
                                        }
                                    });
                                    snackbar.show();
                                } else {
                                    ProductSellDialog dialog = new ProductSellDialog(products.get(selected).getType(), products.get(selected).getQuantity());
                                    dialog.show(getSupportFragmentManager(), "Sell a Product");
                                }
                            } else if (dX > third) {
                                selected = viewHolder.getAdapterPosition();
                                ProductBuyDialog dialog = new ProductBuyDialog(products.get(selected).getType());
                                dialog.show(getSupportFragmentManager(), "Buy a Product");
                            }
                        }
                        return false;
                    }
                });
            }

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNegative))
                    .addSwipeLeftLabel("Sell").setSwipeLeftLabelColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPositive))
                    .addSwipeRightLabel("Buy").setSwipeRightLabelColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white))
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
                    ProductEditDialog dialog = new ProductEditDialog(products.get(selected).getName(), products.get(selected).getPrice(), products.get(selected).getQuantity(), products.get(selected).getType());
                    dialog.show(getSupportFragmentManager(), "Edit Product");
                    mode.finish();
                    return true;
                case R.id.delete:
                    products.remove(selected);
                    adapter.notifyItemRemoved(selected);
                    if (!fab.isExtended()) {
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
            Objects.requireNonNull(layoutManager.findViewByPosition(selected)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorDeselected));
            actionModeState = 0;
            actionMode = null;
        }
    };

    @Override
    public void createProduct(String name, double price, double quantity, String type) {
        products.add(new Product(name, price, quantity, type));
        adapter.notifyDataSetChanged();
        dataManager.saveData();
    }

    @Override
    public void editProduct(String name, double price, double quantity, String type) {
        products.get(selected).setName(name);
        products.get(selected).setPrice(price);
        products.get(selected).setQuantity(quantity);
        products.get(selected).setType(type);
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

    @Override
    public void sellProduct(double quantity) {
        double inStock = products.get(selected).getQuantity();
        products.get(selected).setQuantity(inStock - quantity);
        adapter.notifyItemChanged(selected);
        dataManager.saveData();
    }
}
