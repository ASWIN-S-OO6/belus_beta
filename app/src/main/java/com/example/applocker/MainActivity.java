package com.example.applocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.provider.Settings;


import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    String pass;
    static final String KEY = "pass";
    final Context con = this;

    Button btn, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (checkSelfPermission("android.permission.FOREGROUND_SERVICE_SPECIAL_USE")
                    == PackageManager.PERMISSION_GRANTED) {
                startForegroundService(new Intent(this, ForegroundAppService.class));
            } else {
                requestSpecialUsePermission();
            }
        } else {
            startForegroundService(new Intent(this, ForegroundAppService.class));
        }


        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        }

        pass = SharedPrefUtil.getInstance(this).getString(KEY);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccessGranted()) {
                    if (pass != null && !pass.isEmpty()) {
                        startActivity(new Intent(MainActivity.this, ShowAll_InstalledApps.class));
                    } else {
                        Toast.makeText(con, "Set password to continue", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "We need permission", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        });

        btn3 = findViewById(R.id.btn3);
        if (pass == null || pass.isEmpty()) {
            btn3.setText("Set password");
        } else {
            btn3.setText("Update password");
        }
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass == null || pass.isEmpty()) {
                    setPassword(con);
                } else {
                    updatePassword(con);
                }
            }
        });

        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Enable access,", Toast.LENGTH_LONG).show();
            }
        });

        requestSpecialUsePermission();
    }

    private void requestSpecialUsePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
            if (checkSelfPermission("android.permission.FOREGROUND_SERVICE_SPECIAL_USE")
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.FOREGROUND_SERVICE_SPECIAL_USE"},
                        1001);
            }
        }
    }





    private void requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    private void launchLockScreen(String lockedApp) {
        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.putExtra("locked_app", lockedApp);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }



    private void setPassword(Context con) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);

        LinearLayout ll = new LinearLayout(con);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView t1 = new TextView(con);
        t1.setText("Enter the password");
        ll.addView(t1);
        EditText input = new EditText(con);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(input);
        dialog.setView(ll);

        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                SharedPrefUtil.getInstance(con).putString(KEY, input.getText().toString());
                Toast.makeText(con, "Password set successfully", Toast.LENGTH_LONG).show();
                pass = input.getText().toString();
                btn3.setText("Update password");
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void updatePassword(final Context con) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);

        LinearLayout ll = new LinearLayout(con);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView t1 = new TextView(con);
        t1.setText("Enter previous password");
        ll.addView(t1);
        EditText input = new EditText(con);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(input);

        TextView t2 = new TextView(con);
        t2.setText("Enter new password");
        ll.addView(t2);
        EditText input1 = new EditText(con);
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(input1);

        dialog.setView(ll);

        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (pass.equals(input.getText().toString())) {
                    SharedPrefUtil.getInstance(con).putString(KEY, input1.getText().toString());
                    Toast.makeText(con, "Password updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(con, "Sorry wrong password", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Application not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking access: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, ForegroundAppService.class);
        startService(serviceIntent);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "FOREGROUND_SERVICE_SPECIAL_USE granted");
            } else {
                Log.e("Permission", "FOREGROUND_SERVICE_SPECIAL_USE denied");
            }
        }
    }

}
