package io.github.gorgex.cashbox.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.NumberFormat;

import io.github.gorgex.cashbox.R;

public class ProductEditDialog extends AppCompatDialogFragment {

    private EditText productName;
    private EditText productPrice;
    private EditText productQuantity;
    private ProductEditDialogListener listener;

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

        builder.setView(view)
                .setTitle("Edit Product")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                        if (!n.isEmpty()) {
                            listener.editProduct(n, price, quantity);
                        } else {
                            dialogInterface.dismiss();
                        }
                    }
                });

        productName = view.findViewById(R.id.product_name);
        productPrice = view.findViewById(R.id.product_price);
        productQuantity = view.findViewById(R.id.product_quantity);

        productName.setText(name);
        productPrice.setText(NumberFormat.getInstance().format(price));
        productQuantity.setText(NumberFormat.getInstance().format(quantity));

        return builder.create();
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
