package com.example.beanbrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.beanbrew.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("New password is required");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        resetButton.setEnabled(false);
        resetButton.setText("Updating...");

        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            resetButton.setEnabled(true);
                            resetButton.setText("Reset Password");
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in. Please login first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}