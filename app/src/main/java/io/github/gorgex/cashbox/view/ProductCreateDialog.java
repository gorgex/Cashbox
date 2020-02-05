package io.github.gorgex.cashbox.view;

import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.gorgex.cashbox.R;

public class ProductCreateDialog extends AppCompatDialogFragment {

    private TextInputLayout nameInputLayout;
    private TextInputLayout priceInputLayout;
    private TextInputLayout quantityInputLayout;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private RadioGroup productTypeRadioGroup;
    private RadioButton productTypeRadioButton;
    private Button save;
    private ProductCreateDialogListener listener;
    private InputMethodManager inputMethodManager;

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_create_product, null);

        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        priceInputLayout = view.findViewById(R.id.priceInputLayout);
        quantityInputLayout = view.findViewById(R.id.quantityInputLayout);

        productNameEditText = view.findViewById(R.id.productName);
        productPriceEditText = view.findViewById(R.id.productPrice);
        productQuantityEditText = view.findViewById(R.id.productQuantity);
        productTypeRadioGroup = view.findViewById(R.id.productType);

        int checkedId = productTypeRadioGroup.getCheckedRadioButtonId();
        productTypeRadioButton = productTypeRadioGroup.findViewById(checkedId);

        inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        builder.setView(view)
                .setTitle("Create a Product")
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

        productPriceEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (productNameEditText.getText().toString().isEmpty()) {
                    nameInputLayout.setError("Invalid name");
                }
                return false;
            }
        });

        productQuantityEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (productNameEditText.getText().toString().isEmpty()) {
                    nameInputLayout.setError("Invalid name");
                }
                return false;
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        save = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        save.setEnabled(false);

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
                } else if ((productPriceEditText.getText().toString().length() == 1 && productPriceEditText.getText().toString().charAt(0) == '.') ||
                        (productQuantityEditText.getText().toString().length() == 1 && productQuantityEditText.getText().toString().charAt(0) == '.')) {
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
                productPriceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
                priceInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1 && editable.charAt(0) == '.') {
                    priceInputLayout.setError("Invalid price");
                    save.setEnabled(false);
                } else if (productNameEditText.getText().toString().isEmpty() ||
                        (productQuantityEditText.getText().toString().length() == 1 && productQuantityEditText.getText().toString().charAt(0) == '.')) {
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }
            }
        });

        productQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().contains(".")) {
                    productTypeRadioGroup.check(productTypeRadioGroup.findViewById(R.id.kg).getId());
                } else {
                    productTypeRadioGroup.check(productTypeRadioGroup.findViewById(R.id.psc).getId());
                }
                productQuantityEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
                quantityInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1 && editable.charAt(0) == '.') {
                    quantityInputLayout.setError("Invalid quantity");
                    save.setEnabled(false);
                } else if (productNameEditText.getText().toString().isEmpty() ||
                        (productPriceEditText.getText().toString().length() == 1 && productPriceEditText.getText().toString().charAt(0) == '.')) {
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
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
                String productPrice = productPriceEditText.getText().toString();
                String productQuantity = productQuantityEditText.getText().toString();
                String productType = productTypeRadioButton.getText().toString();

                double price = productPrice.isEmpty() ? 0 : Double.parseDouble(productPrice);
                double quantity = productQuantity.isEmpty() ? 0 : Double.parseDouble(productQuantity);
                listener.createProduct(productName, price, quantity, productType);
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
            listener = (ProductCreateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Must implement ProductCreateDialogListener");
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern pattern;

        DecimalDigitsInputFilter() {
            this.pattern = Pattern.compile("^[0-9]+([.][0-9]{0,2})?$");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source.equals("")) {
                return null;
            }
            StringBuilder builder = new StringBuilder(dest);
            builder.replace(dstart, dend, source.subSequence(start, end).toString());
            Matcher matcher = pattern.matcher(builder);
            if (!matcher.matches()) {
                return "";
            }
            return null;
        }
    }

    public interface ProductCreateDialogListener {
        void createProduct(String name, double price, double quantity, String type);
    }
}
