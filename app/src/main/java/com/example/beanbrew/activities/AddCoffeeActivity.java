package com.example.beanbrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.beanbrew.R;
import com.example.beanbrew.database.CoffeeDBHelper;

public class AddCoffeeActivity extends AppCompatActivity {

    private TextView titleText;
    private EditText coffeeNameEditText;
    private EditText coffeePriceEditText;
    private EditText coffeeDescriptionEditText;
    private RadioGroup categoryRadioGroup;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button backButton;
    private CoffeeDBHelper dbHelper;
    private int coffeeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coffee);

        dbHelper = new CoffeeDBHelper(this);

        titleText = findViewById(R.id.titleText);
        coffeeNameEditText = findViewById(R.id.coffeeName);
        coffeePriceEditText = findViewById(R.id.coffeePrice);
        coffeeDescriptionEditText = findViewById(R.id.coffeeDescription);
        categoryRadioGroup = findViewById(R.id.categoryRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        if (intent.hasExtra("COFFEE_ID")) {
            coffeeId = intent.getIntExtra("COFFEE_ID", -1);
            String name = intent.getStringExtra("COFFEE_NAME");
            double price = intent.getDoubleExtra("COFFEE_PRICE", 0.0);
            String category = intent.getStringExtra("COFFEE_CATEGORY");
            String description = intent.getStringExtra("COFFEE_DESCRIPTION");

            titleText.setText("Update Coffee");
            coffeeNameEditText.setText(name);
            coffeePriceEditText.setText(String.valueOf(price));
            coffeeDescriptionEditText.setText(description);

            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);

            if (category != null) {
                if (category.equals("Hot")) {
                    categoryRadioGroup.check(R.id.btnHot);
                } else if (category.equals("Iced")) {
                    categoryRadioGroup.check(R.id.btnIced);
                } else if (category.equals("Special")) {
                    categoryRadioGroup.check(R.id.btnSpecial);
                }
            }
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            categoryRadioGroup.check(R.id.btnHot);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoffee();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCoffee();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCoffee();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getSelectedCategory() {
        int selectedId = categoryRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.btnHot) {
            return "Hot";
        } else if (selectedId == R.id.btnIced) {
            return "Iced";
        } else if (selectedId == R.id.btnSpecial) {
            return "Special";
        }
        return "Hot";
    }

    private void saveCoffee() {
        String name = coffeeNameEditText.getText().toString().trim();
        String priceStr = coffeePriceEditText.getText().toString().trim();
        String description = coffeeDescriptionEditText.getText().toString().trim();
        String category = getSelectedCategory();

        if (TextUtils.isEmpty(name)) {
            coffeeNameEditText.setError("Coffee name is required");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            coffeePriceEditText.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                coffeePriceEditText.setError("Price must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            coffeePriceEditText.setError("Invalid price format");
            return;
        }

        long id = dbHelper.insertCoffee(name, price, category, description);
        if (id != -1) {
            Toast.makeText(this, "Coffee added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add coffee", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCoffee() {
        String name = coffeeNameEditText.getText().toString().trim();
        String priceStr = coffeePriceEditText.getText().toString().trim();
        String description = coffeeDescriptionEditText.getText().toString().trim();
        String category = getSelectedCategory();

        if (TextUtils.isEmpty(name)) {
            coffeeNameEditText.setError("Coffee name is required");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            coffeePriceEditText.setError("Price is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                coffeePriceEditText.setError("Price must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            coffeePriceEditText.setError("Invalid price format");
            return;
        }

        boolean updated = dbHelper.updateCoffee(coffeeId, name, price, category, description);
        if (updated) {
            Toast.makeText(this, "Coffee updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update coffee", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCoffee() {
        boolean deleted = dbHelper.deleteCoffee(coffeeId);
        if (deleted) {
            Toast.makeText(this, "Coffee deleted successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to delete coffee", Toast.LENGTH_SHORT).show();
        }
    }
}