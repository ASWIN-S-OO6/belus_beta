package com.example.applocker;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

public class AppLockerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            List<String> lockedApps = SharedPrefUtil.getInstance(this).getListString("locked_apps");

            if (lockedApps.contains(packageName)) {
                // Launch lock screen activity
                Intent intent = new Intent(this, LockScreenActivity.class);
                intent.putExtra("locked_app", packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Handle interruption
    }
}
