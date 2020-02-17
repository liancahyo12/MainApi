package com.example.mainapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mainapi.apihelper.ApiInterface;
import com.example.mainapi.apihelper.ApiUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;
    ProgressDialog loading;
    String id, nama, jurusan, email;
    Context mContext;
    SharedPrefManager sharedPrefManager;
    ApiInterface mApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mApiService = ApiUrl.getAPIService(); // meng-init yang ada di package apihelper
        sharedPrefManager = new SharedPrefManager(this);
        cekuser();
        initComponents();

    }

    private void initComponents() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
    }
    private void requestLogin(){
        mApiService.loginRequest(etEmail.getText().toString(), etPassword.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("status").equals("true")){
                                    // Jika login berhasil maka data nama yang ada di response API
                                    // akan diparsing ke activity selanjutnya.
                                    Toast.makeText(mContext, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();
                                    //JSONArray data = jsonRESULTS.getJSONArray("data");

                                    //for(int i = 0; i < data.length(); i++)
                                    //{
                                    //    JSONObject object3 = data.getJSONObject(i);
                                    //    id = object3.getString("id");
                                    //    nama = object3.getString("nama");
                                   //     email = object3.getString("email");
                                   // }
                                    String id = jsonRESULTS.getJSONObject("data").getString("id");
                                    String nama = jsonRESULTS.getJSONObject("data").getString("nama");
                                    Integer role = jsonRESULTS.getJSONObject("data").getInt("role");
                                    sharedPrefManager.CreateSession(id, nama, role);
                                    sharedPrefManager.saveSPBoolean(sharedPrefManager.SP_SUDAH_LOGIN, true);
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    Intent intent1 = new Intent(mContext, EditBuku.class);
                                    intent.putExtra("result_nama", nama);
                                    intent.putExtra("role", role);
                                    intent1.putExtra("role", role);
                                    finish();
                                    startActivity(intent);
                                } else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });
    }
    public void cekuser(){
        HashMap<String, String> user = sharedPrefManager.getUserDetails();
        Boolean login = sharedPrefManager.getSPSudahLogin();
        if(login==true){
            startActivity(new Intent(mContext, MainActivity.class));
            }
        }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
        finish();
    }


}
