package com.example.habittrackerapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private HabitDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = HabitDatabase.getInstance(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> register());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = db.userDao().login(username, password);
            if (user != null) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, MainActivity.class); // ✅ Redirect to MainActivity
                    intent.putExtra("username", user.getUsername());      // ✅ Send username
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            if (db.userDao().getUserByUsername(username) == null) {
                db.userDao().insertUser(new UserEntity(username, password, 0));
                runOnUiThread(() ->
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                );
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
