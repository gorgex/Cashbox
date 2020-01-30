package io.github.gorgex.cashbox.view;

import androidx.appcompat.app.AlertDialog;
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
import io.github.gorgex.cashbox.R;

public class ProductCreateDialog extends AppCompatDialogFragment {

    private EditText productName;
    private EditText productQuantity;
    private ProductCreateDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_product, null);

        builder.setView(view)
                .setTitle("Create a Product")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = productName.getText().toString();
                        String quantity = productQuantity.getText().toString();
                        if(!name.isEmpty() && !quantity.isEmpty()) {
                            listener.createProduct(name, quantity);
                        } else {
                            dialog.dismiss();
                        }
                    }
                });

        productName = view.findViewById(R.id.product_name);
        productQuantity = view.findViewById(R.id.product_quantity);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ProductCreateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                "Must implement ProductCreateDialogListener");
        }
    }

    public interface ProductCreateDialogListener {
        void createProduct(String name, String quantity);
    }
}
