package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btn_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_query = findViewById(R.id.btn_query);

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String url = "http://140.124.73.33:3001/station";
                String url = "https://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b";
                Request req = new Request.Builder().url(url).build();

                new OkHttpClient().newCall(req).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.code() == 200){
                            if(response.body() == null) return;

                            Data data = new Gson().fromJson(response.body().string(), Data.class);

                            final String[] items = new String[data.result.results.length];

                            for(int i = 0;i < items.length;i++)
                                items[i] = "\n列車即將進入 : "+data.result.results[i].Station +
                                        "\n列車行駛目的地 : "+data.result.results[i].Destination;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("台北捷運列車進站站名")
                                            .setItems(items, null)
                                            .show();
                                }
                            });
                        } else if (! response.isSuccessful())
                            Log.e("伺服器錯誤",response.code()+""+response.message());
                        else
                            Log.e("其他錯誤",response.code()+""+response.message());
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e){
                        Log.e("其他錯誤",e.toString());
                    }
                });
            }
        });

    }
    class Data{
        Result result;

        class Result{
            Results[] results;

            class Results{
                String Station;
                String Destination;
            }
        }
    }
}