package io.github.gorgex.cashbox.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

public class ProductBuyDialog extends AppCompatDialogFragment {

    private TextInputLayout quantityInputLayout;
    private EditText buyQuantity;
    private Button add;
    private ProductBuyDialogListener listener;
    private InputMethodManager inputMethodManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_buy_product, null);

        quantityInputLayout = view.findViewById(R.id.quantityInputLayout);
        buyQuantity = view.findViewById(R.id.productQuantity);
        inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        builder.setView(view)
                .setTitle("Add to stock")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        dialog.dismiss();
                    }
                });

        buyQuantity.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        add = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        add.setEnabled(false);

        buyQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buyQuantity.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty() || (s.toString().length() == 1 && s.toString().charAt(0) == '.') || Double.parseDouble(s.toString()) <= 0) {
                    buyQuantity.setError("Quantity should be a positive number");
                    add.setEnabled(false);
                } else {
                    add.setEnabled(true);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = buyQuantity.getText().toString().trim();
                double q;
                if (quantity.isEmpty()) {
                    buyQuantity.setError("Quantity should be a positive number");
                } else {
                    q = Double.parseDouble(quantity);
                    listener.buyProduct(q);
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
            listener = (ProductBuyDialog.ProductBuyDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ProductBuyDialogListener");
        }
    }

    public interface ProductBuyDialogListener {
        void buyProduct(double quantity);
    }
}
