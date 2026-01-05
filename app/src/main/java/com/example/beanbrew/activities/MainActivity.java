package com.example.beanbrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beanbrew.adapters.CoffeeAdapter;
import com.example.beanbrew.R;
import com.example.beanbrew.api.CoffeeApiSimulator;
import com.example.beanbrew.database.CoffeeDBHelper;
import com.example.beanbrew.models.Coffee;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView yourCoffeeRecyclerView;
    private RecyclerView dealsRecyclerView;
    private CoffeeAdapter yourCoffeeAdapter;
    private CoffeeAdapter dealsAdapter;
    private CoffeeDBHelper dbHelper;
    private FirebaseAuth mAuth;
    private LinearLayout emptyYourCoffee;
    private LinearLayout emptyDeals;
    private LinearLayout factsSection;
    private TextView coffeeFactText;
    private CoffeeApiSimulator apiSimulator;

    private final String[] COFFEE_FACTS = {
            "Did you know? The most expensive coffee in the world is Kopi Luwak at $600 per pound!",
            "Tip: Store coffee beans in an airtight container away from light and moisture.",
            "Fact: Coffee was discovered by goats in Ethiopia around 800 AD.",
            "Recommendation: Try adding a pinch of salt to reduce bitterness in your coffee.",
            "Health Tip: Drinking 3-4 cups of coffee daily may reduce risk of heart disease.",
            "Fun Fact: Brazil produces about 40% of the world's coffee supply.",
            "Brewing Tip: Use 1-2 tablespoons of coffee per 6 ounces of water for perfect strength.",
            "Did you know? The world's largest cup of coffee was 9,000 liters!",
            "Recommendation: Arabica beans have smoother taste than Robusta beans.",
            "Storage Tip: Freeze coffee beans to keep them fresh for up to a month."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = new CoffeeDBHelper(this);
        apiSimulator = new CoffeeApiSimulator();

        // Initialize views
        Button profileButton = findViewById(R.id.profileButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        factsSection = findViewById(R.id.factsSection);
        coffeeFactText = findViewById(R.id.coffeeFactText);
        emptyYourCoffee = findViewById(R.id.emptyYourCoffee);
        emptyDeals = findViewById(R.id.emptyDeals);

        // Show facts section
        showRandomCoffeeFact();

        // Setup Your Coffee RecyclerView
        yourCoffeeRecyclerView = findViewById(R.id.yourCoffeeRecyclerView);
        yourCoffeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        yourCoffeeAdapter = new CoffeeAdapter(new ArrayList<>());
        yourCoffeeRecyclerView.setAdapter(yourCoffeeAdapter);

        // Setup Deals RecyclerView
        dealsRecyclerView = findViewById(R.id.dealsRecyclerView);
        dealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dealsAdapter = new CoffeeAdapter(new ArrayList<>());
        dealsRecyclerView.setAdapter(dealsAdapter);

        // Disable edit/delete for deals
        dealsAdapter.setDealsMode(true);

        // Load data
        loadYourCoffee();
        loadCoffeeDeals();

        // Button click listeners
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // FAB click listener
        findViewById(R.id.fabAddCoffee).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCoffeeActivity.class);
            startActivity(intent);
        });

        // Facts section click listener
        factsSection.setOnClickListener(v -> showRandomCoffeeFact());

        // Your Coffee item click listeners
        yourCoffeeAdapter.setOnItemClickListener(new CoffeeAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Coffee coffee = yourCoffeeAdapter.getCoffeeList().get(position);
                Intent intent = new Intent(MainActivity.this, AddCoffeeActivity.class);
                intent.putExtra("COFFEE_ID", coffee.getId());
                intent.putExtra("COFFEE_NAME", coffee.getName());
                intent.putExtra("COFFEE_PRICE", coffee.getPrice());
                intent.putExtra("COFFEE_CATEGORY", coffee.getCategory());
                intent.putExtra("COFFEE_DESCRIPTION", coffee.getDescription());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                Coffee coffee = yourCoffeeAdapter.getCoffeeList().get(position);
                dbHelper.deleteCoffee(coffee.getId());
                loadYourCoffee();
                Toast.makeText(MainActivity.this, "Coffee deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // Deals item click listeners (read-only, no edit/delete)
        dealsAdapter.setOnItemClickListener(new CoffeeAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Toast.makeText(MainActivity.this,
                        "Cannot edit coffee deals - these are special offers!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(MainActivity.this,
                        "Cannot delete coffee deals - these are special offers!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadYourCoffee();
        showRandomCoffeeFact();
    }

    private void loadYourCoffee() {
        List<Coffee> yourCoffeeList = dbHelper.getAllCoffee();
        yourCoffeeAdapter.setCoffeeList(yourCoffeeList);

        // Show/hide empty state
        if (yourCoffeeList.isEmpty()) {
            emptyYourCoffee.setVisibility(View.VISIBLE);
            yourCoffeeRecyclerView.setVisibility(View.GONE);
        } else {
            emptyYourCoffee.setVisibility(View.GONE);
            yourCoffeeRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadCoffeeDeals() {
        emptyDeals.setVisibility(View.VISIBLE);
        dealsRecyclerView.setVisibility(View.GONE);

        apiSimulator.getCoffeeDeals(new CoffeeApiSimulator.CoffeeApiCallback() {
            @Override
            public void onSuccess(List<Coffee> dealsList) {
                runOnUiThread(() -> {
                    dealsAdapter.setCoffeeList(dealsList);

                    // Show/hide empty state
                    if (dealsList.isEmpty()) {
                        emptyDeals.setVisibility(View.VISIBLE);
                        emptyDeals.findViewById(android.R.id.progress).setVisibility(View.GONE);
                        ((TextView) emptyDeals.findViewById(android.R.id.text1)).setText("No deals available");
                        dealsRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptyDeals.setVisibility(View.GONE);
                        dealsRecyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Found " + dealsList.size() + " coffee deals!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    emptyDeals.setVisibility(View.VISIBLE);
                    emptyDeals.findViewById(android.R.id.progress).setVisibility(View.GONE);
                    ((TextView) emptyDeals.findViewById(android.R.id.text1)).setText("Failed to load deals");
                    dealsRecyclerView.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,
                            "Coffee deals: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showRandomCoffeeFact() {
        Random random = new Random();
        int index = random.nextInt(COFFEE_FACTS.length);
        coffeeFactText.setText(COFFEE_FACTS[index]);

        // Change background color for variety
        int[] colors = {0xFF4CAF50, 0xFF2196F3, 0xFFFF9800, 0xFF9C27B0};
        factsSection.setBackgroundColor(colors[random.nextInt(colors.length)]);
    }
}