package com.example.applocker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundAppService extends android.app.Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        startMonitoringForegroundApps();

        Notification notification = new NotificationCompat.Builder(this, "lock_service")
                .setContentTitle("App Locker Service")
                .setContentText("Monitoring locked apps...")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        startForeground(1, notification);
        startMonitoringForegroundApps();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "lock_service",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startMonitoringForegroundApps() {
        runnable = new Runnable() {
            @Override
            public void run() {
                String currentApp = getForegroundApp();
                List<String> lockedApps = SharedPrefUtil.getInstance(ForegroundAppService.this).getListString("locked_apps");

                if (lockedApps.contains(currentApp)) {
                    Log.d("ForegroundAppService", "Locked App Launched: " + currentApp);
                    launchLockScreen(currentApp);
                }
                handler.postDelayed(this, 1000); // Check every 1 second
            }
        };
        handler.post(runnable);
    }

    private String getForegroundApp() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 10, currentTime);

        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!sortedMap.isEmpty()) {
                return sortedMap.get(sortedMap.lastKey()).getPackageName();
            }
        }
        return null;
    }

    private void launchLockScreen(String lockedApp) {
        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.putExtra("locked_app", lockedApp);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }
}
