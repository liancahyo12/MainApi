package com.example.mainapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mainapi.apihelper.ApiInterface;
import com.example.mainapi.apihelper.ApiUrl;
import com.example.mainapi.model.BukuData;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView tvResultNama;
    String resultNama,role;
    ApiInterface mApiService;
    Button tambahbuku, logoutbtn;
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swiperef;
    private CustomAdapter adapter;
    SharedPrefManager sharedPrefManager;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiService = ApiUrl.getAPIService();
        recyclerView = findViewById(R.id.customRecyclerView);
        initComponents();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mContext = this;
        sharedPrefManager = new SharedPrefManager(this);
        tambahbuku=findViewById(R.id.tambahbtn);
        logoutbtn=findViewById(R.id.logoutbtn);
        swiperef = findViewById(R.id.swipeContainer);
        refresha();
        // untuk mendapatkan data dari activity sebelumnya, yaitu activity login.
        Bundle extras = getIntent().getExtras();
        cekuser();

        tambahbuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, TambahBuku.class));
            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });

        swiperef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresha();
            }
        });

        // Configure the refreshing colors
       swiperef.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initComponents() {
        tvResultNama = (TextView) findViewById(R.id.tvResultNama);
    }
    private void generateDataList(List<BukuData> bukuDataList) {
        recyclerView = findViewById(R.id.customRecyclerView);
        adapter = new CustomAdapter(this,bukuDataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    public void refresha (){
        Call<List<BukuData>> call = mApiService.getBuku();
        call.enqueue(new Callback<List<BukuData>>() {
            @Override
            public void onResponse(Call<List<BukuData>> call, Response<List<BukuData>> response) {
                progressDialog.dismiss();
                Log.d("a","OnResponse: "+ response.code());

                if(response.isSuccessful()){
                    //    Users = new ArrayList<User>();
                    generateDataList(response.body());
                    swiperef.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<BukuData>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("a","OnFail: "+ t.toString());
            }

        });
    }
    public void cekuser(){
        HashMap<String, String> user = sharedPrefManager.getUserDetails();
        String spId = user.get("spId").toString();
        String spNama = user.get("spNama").toString();
        Integer spRole = sharedPrefManager.getSPRole();
        Boolean login = sharedPrefManager.getSPSudahLogin();
        if(login==true){
            if(spRole==0){
                tambahbuku.setVisibility(View.VISIBLE);
                tvResultNama.setText(spNama);
            }
            else if(spRole==1){
                tambahbuku.setVisibility(View.GONE);
                tvResultNama.setText(sharedPrefManager.getSPNama());
            }
        }else{
            startActivity(new Intent(mContext, LoginActivity.class));
        }


    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
           finish();

        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
