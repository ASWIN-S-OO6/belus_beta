package com.example.applocker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.AsyncTask;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class ShowAll_InstalledApps extends AppCompatActivity {

    RecyclerView recyclerView;

    List<AppModel> appModelList = new ArrayList<>();
    AppAdapter adapter;

     Context con = this;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_installed_apps);
        recyclerView = findViewById(R.id.recycleview);

        adapter = new AppAdapter(appModelList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps();
                
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("Fetching apps");

        progressDialog.setMessage("Loading");

        progressDialog.show();
    }

    public void getInstalledApps(){

        List<String> list = SharedPrefUtil.getInstance(con).getListString("locked_apps");

        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);

        for (int i =0 ; i<packageInfos.size();i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packname = packageInfos.get(i).packageName;
            appModelList.add(new AppModel(name,icon,0,packname));


            if(!list.isEmpty()){
                if (list.contains(packname)){
                    appModelList.add(new AppModel(name,icon,1,packname));

                }else {
                    appModelList.add(new AppModel(name,icon,0,packname));

                }
            }else {
                appModelList.add(new AppModel(name,icon,0,packname));

            }
        }

        adapter.notifyDataSetChanged();

        progressDialog.dismiss();
    }
}