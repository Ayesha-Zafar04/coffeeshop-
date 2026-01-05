package com.example.beanbrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.beanbrew.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.beanbrew.R;
public class ProfileActivity extends AppCompatActivity {

    private TextView userEmail;
    private TextView userName;
    private TextView userStatus;
    private Button changePasswordButton;
    private Button editProfileButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.userEmail);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userEmail.setText(currentUser.getEmail());
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                userName.setText(currentUser.getDisplayName());
            } else {
                userName.setText("User");
            }
            userStatus.setText("Active");
        }

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}