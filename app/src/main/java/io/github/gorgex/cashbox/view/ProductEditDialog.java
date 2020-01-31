package io.github.gorgex.cashbox.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;

import io.github.gorgex.cashbox.R;

public class ProductEditDialog extends AppCompatDialogFragment {

    private TextInputLayout nameInputLayout;
    private EditText productName;
    private EditText productPrice;
    private EditText productQuantity;
    private Button save;
    private ProductEditDialogListener listener;
    private InputMethodManager inputMethodManager;

    private String name;
    private double price;
    private double quantity;

    ProductEditDialog(String name, double price, double quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_product, null);

        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        productName = view.findViewById(R.id.product_name);
        productPrice = view.findViewById(R.id.product_price);
        productQuantity = view.findViewById(R.id.product_quantity);
        inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        productName.setText(name);
        productPrice.setText(NumberFormat.getInstance().format(price));
        productQuantity.setText(NumberFormat.getInstance().format(quantity));

        builder.setView(view)
                .setTitle("Edit Product")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

        builder.setPositiveButton("Save", null);
        productName.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        if (Build.VERSION.SDK_INT > 26) {
            productName.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        save = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        Button cancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        cancel.setTextColor(getResources().getColor(R.color.colorPrimary));
//        cancel.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        productName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    nameInputLayout.setError("Invalid name");
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = productName.getText().toString();
                String p = productPrice.getText().toString();
                String q = productQuantity.getText().toString();
                if (p.isEmpty()) {
                    price = 0;
                } else {
                    price = Double.parseDouble(p);
                }
                if (q.isEmpty()) {
                    quantity = 0;
                } else {
                    quantity = Double.parseDouble(q);
                }
                if (n.isEmpty()) {
                    nameInputLayout.setError("Invalid name");
                } else {
                    listener.editProduct(n, price, quantity);
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
            listener = (ProductEditDialog.ProductEditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ProductEditDialogListener");
        }
    }

    public interface ProductEditDialogListener {
        void editProduct(String name, double price, double quantity);
    }
}