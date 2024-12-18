package com.example.applocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LockScreenActivity extends AppCompatActivity {

    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        pass = SharedPrefUtil.getInstance(this).getString(MainActivity.KEY);
        String lockedApp = getIntent().getStringExtra("locked_app");

        EditText passwordInput = findViewById(R.id.password_input);
        Button unlockButton = findViewById(R.id.unlock_button);

        unlockButton.setOnClickListener(v -> {
            String input = passwordInput.getText().toString();
            if (input.equals(pass)) {
                finish(); // Allow the user to continue to the locked app
            } else {
                Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
            }
        });

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Optional: Prevent back press
        setFinishOnTouchOutside(false);
    }





    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Prevent going back to the locked app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
