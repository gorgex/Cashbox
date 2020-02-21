package io.github.gorgex.cashbox.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.Objects;

import io.github.gorgex.cashbox.R;

public class ProductEditDialog extends AppCompatDialogFragment {

    private TextInputLayout nameInputLayout;
    private TextInputLayout priceInputLayout;
    private TextInputLayout quantityInputLayout;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private RadioGroup productTypeRadioGroup;
    private RadioButton productTypeRadioButton;
    private Button save;
    private ProductEditDialogListener listener;
    private InputMethodManager inputMethodManager;

    private String name;
    private double price;
    private double quantity;
    private String type;

    ProductEditDialog(String name, double price, double quantity, String type) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_create_product, null);

        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        priceInputLayout = view.findViewById(R.id.priceInputLayout);
        quantityInputLayout = view.findViewById(R.id.quantityInputLayout);

        productNameEditText = view.findViewById(R.id.productName);
        productNameEditText.setLongClickable(false);
        productNameEditText.setTextIsSelectable(false);
        productNameEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        productNameEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
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

        productPriceEditText = view.findViewById(R.id.productPrice);
        productPriceEditText.setLongClickable(false);
        productPriceEditText.setTextIsSelectable(false);
        productPriceEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        productPriceEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
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

        productTypeRadioGroup = view.findViewById(R.id.productType);

        inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        DecimalFormat priceFormat = new DecimalFormat();
        priceFormat.setMinimumFractionDigits(2);

        productNameEditText.setText(name);
        productPriceEditText.setText(priceFormat.format(price).replaceAll(",", ""));
        productQuantityEditText.setText(DecimalFormat.getInstance().format(quantity).replaceAll(",", ""));

        String p = productPriceEditText.getText().toString();
        if (p.contains(".")) {
            productPriceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        }

        String q = productQuantityEditText.getText().toString();
        if (q.contains(".")) {
            productQuantityEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        }

        if (type.equals("kg")) {
            productTypeRadioGroup.check(productTypeRadioGroup.findViewById(R.id.kg).getId());
            if (productQuantityEditText.getText().toString().contains(".")) {
                productTypeRadioGroup.findViewById(R.id.psc).setEnabled(false);
            }
        } else {
            productTypeRadioGroup.check(productTypeRadioGroup.findViewById(R.id.psc).getId());
        }

        int checkedId = productTypeRadioGroup.getCheckedRadioButtonId();
        productTypeRadioButton = productTypeRadioGroup.findViewById(checkedId);

        builder.setView(view)
                .setTitle("Edit Product")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

        productNameEditText.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        if (Build.VERSION.SDK_INT > 26) {
            productNameEditText.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        save = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        productNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    nameInputLayout.setError("Invalid name");
                    save.setEnabled(false);
                } else if (productPriceEditText.getText().toString().trim().isEmpty() || Double.parseDouble(productPriceEditText.getText().toString()) == 0
                        || productQuantityEditText.getText().toString().trim().isEmpty() || Double.parseDouble(productQuantityEditText.getText().toString()) == 0) {
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }
            }
        });

        productPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productPriceEditText.setFilters(new InputFilter[]{new ProductEditDialog.DecimalDigitsInputFilter()});
                priceInputLayout.setError(null);

                if (!s.toString().isEmpty() && s.toString().contains(".")) {
                    if (s.toString().indexOf(".") == 0 || (s.toString().substring(s.toString().indexOf(".")).length() > 3)) {
                        int dotPos = s.toString().indexOf(".");
                        productPriceEditText.removeTextChangedListener(this);
                        productPriceEditText.setText(s.toString().replace(".", ""));
                        productPriceEditText.setSelection(dotPos);
                        productPriceEditText.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().isEmpty() || (editable.length() == 1 && editable.charAt(0) == '.')) {
                        productPriceEditText.removeTextChangedListener(this);
                        productPriceEditText.setText("");
                        productPriceEditText.addTextChangedListener(this);
                        priceInputLayout.setError("Invalid price");
                        save.setEnabled(false);
                    } else if (Double.parseDouble(editable.toString()) == 0) {
                        priceInputLayout.setError("Price can't be 0");
                        save.setEnabled(false);
                    } else if (productNameEditText.getText().toString().trim().isEmpty() || productQuantityEditText.getText().toString().trim().isEmpty()
                            || Double.parseDouble(productQuantityEditText.getText().toString()) == 0) {
                        save.setEnabled(false);
                    } else {
                        save.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    productPriceEditText.removeTextChangedListener(this);
                    productPriceEditText.setText("");
                    productPriceEditText.addTextChangedListener(this);
                    priceInputLayout.setError("Invalid price");
                    save.setEnabled(false);
                }
            }
        });

        productQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productQuantityEditText.setFilters(new InputFilter[]{new ProductEditDialog.DecimalDigitsInputFilter()});
                quantityInputLayout.setError(null);

                if (!s.toString().isEmpty() && s.toString().contains(".")) {
                    if (s.toString().indexOf(".") == 0 || (s.toString().substring(s.toString().indexOf(".")).length() > 3)) {
                        int dotPos = s.toString().indexOf(".");
                        productQuantityEditText.removeTextChangedListener(this);
                        productQuantityEditText.setText(s.toString().replace(".", ""));
                        productQuantityEditText.setSelection(dotPos);
                        productQuantityEditText.addTextChangedListener(this);
                    } else if (s.toString().indexOf(".") != 0) {
                        productTypeRadioGroup.check(productTypeRadioGroup.findViewById(R.id.kg).getId());
                        productTypeRadioGroup.findViewById(R.id.psc).setEnabled(false);
                    }
                } else {
                    productTypeRadioGroup.findViewById(R.id.psc).setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.toString().isEmpty() || (editable.length() == 1 && editable.charAt(0) == '.')) {
                        productQuantityEditText.removeTextChangedListener(this);
                        productQuantityEditText.setText("");
                        productQuantityEditText.addTextChangedListener(this);
                        quantityInputLayout.setError("Invalid quantity");
                        save.setEnabled(false);
                    } else if (Double.parseDouble(editable.toString()) == 0) {
                        quantityInputLayout.setError("Quantity can't be 0");
                        save.setEnabled(false);
                    } else if (productNameEditText.getText().toString().trim().isEmpty() || productPriceEditText.getText().toString().trim().isEmpty()
                            || Double.parseDouble(productPriceEditText.getText().toString()) == 0) {
                        save.setEnabled(false);
                    } else {
                        save.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    productQuantityEditText.removeTextChangedListener(this);
                    productQuantityEditText.setText("");
                    productQuantityEditText.addTextChangedListener(this);
                    quantityInputLayout.setError("Invalid quantity");
                    save.setEnabled(false);
                }
            }
        });

        productTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                productTypeRadioButton = productTypeRadioGroup.findViewById(checkedId);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = productNameEditText.getText().toString().trim();
                String productPrice = productPriceEditText.getText().toString().trim();
                String productQuantity = productQuantityEditText.getText().toString().trim();
                String productType = productTypeRadioButton.getText().toString();

                price = Double.parseDouble(productPrice);
                quantity = Double.parseDouble(productQuantity);
                listener.editProduct(productName, price, quantity, productType);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ProductEditDialog.ProductEditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ProductEditDialogListener");
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

    public interface ProductEditDialogListener {
        void editProduct(String name, double price, double quantity, String type);
    }
}
