package io.github.gorgex.cashbox.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import io.github.gorgex.cashbox.R;

public class ProductSellDialog extends AppCompatDialogFragment {

    private TextInputLayout quantityInputLayout;
    private EditText productQuantityEditText;
    private Button sell;
    private ProductSellDialogListener listener;
    private InputMethodManager inputMethodManager;

    private String type;
    private double inStock;

    ProductSellDialog(String type, double inStock) {
        this.type = type;
        this.inStock = inStock;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_buy_product, null);

        quantityInputLayout = view.findViewById(R.id.quantityInputLayout);
        productQuantityEditText = view.findViewById(R.id.productQuantity);
        productQuantityEditText.setLongClickable(false);
        productQuantityEditText.setTextIsSelectable(false);
        productQuantityEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        productQuantityEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        builder.setView(view)
                .setTitle("Sell a Product")
                .setPositiveButton("Sell", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        dialog.dismiss();
                    }
                });

        productQuantityEditText.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        sell = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        sell.setEnabled(false);

        productQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productQuantityEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
                quantityInputLayout.setError(null);

                if (!s.toString().isEmpty() && s.toString().contains(".")) {
                    if (s.toString().indexOf(".") == 0 || (s.toString().substring(s.toString().indexOf(".")).length() > 3) || type.equals("psc")) {
                        int dotPos = s.toString().indexOf(".");
                        productQuantityEditText.removeTextChangedListener(this);
                        productQuantityEditText.setText(s.toString().replace(".", ""));
                        productQuantityEditText.setSelection(dotPos);
                        productQuantityEditText.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().isEmpty() || (editable.toString().length() == 1 && editable.charAt(0) == '.')) {
                        productQuantityEditText.removeTextChangedListener(this);
                        productQuantityEditText.setText("");
                        productQuantityEditText.addTextChangedListener(this);
                        quantityInputLayout.setError("Invalid quantity");
                        sell.setEnabled(false);
                    } else if (Double.parseDouble(editable.toString()) == 0) {
                        quantityInputLayout.setError("Quantity can't be 0");
                        sell.setEnabled(false);
                    } else if (Double.parseDouble(productQuantityEditText.getText().toString()) > inStock) {
                        if (type.equals("psc")) {
                            quantityInputLayout.setError(String.format(getResources().getString(R.string.left_in_stock), Integer.toString((int) inStock), type));
                        } else {
                            quantityInputLayout.setError(String.format(getResources().getString(R.string.left_in_stock), Double.toString(inStock), type));
                        }
                        sell.setEnabled(false);
                    } else {
                        sell.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    productQuantityEditText.removeTextChangedListener(this);
                    productQuantityEditText.setText("");
                    productQuantityEditText.addTextChangedListener(this);
                    quantityInputLayout.setError("Invalid quantity");
                    sell.setEnabled(false);
                }
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productQuantity = productQuantityEditText.getText().toString().trim();
                double quantity = Double.parseDouble(productQuantity);

                if (quantity > inStock) {
                    if (type.equals("psc")) {
                        quantityInputLayout.setError(String.format(getResources().getString(R.string.left_in_stock), Integer.toString((int) inStock), type));
                    } else {
                        quantityInputLayout.setError(String.format(getResources().getString(R.string.left_in_stock), Double.toString(inStock), type));
                    }
                    sell.setEnabled(false);
                } else {
                    listener.sellProduct(quantity);
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ProductSellDialog.ProductSellDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ProductBuyDialogListener");
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String value = dest.toString();
            if (value.contains(".") && value.indexOf(".") == value.length() - 3) {
                return "";
            }
            return null;
        }
    }

    public interface ProductSellDialogListener {
        void sellProduct(double quantity);
    }
}
