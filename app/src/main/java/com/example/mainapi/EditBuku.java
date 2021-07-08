package com.example.mainapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mainapi.apihelper.ApiInterface;
import com.example.mainapi.apihelper.ApiUrl;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBuku extends AppCompatActivity {
    EditText titled, authord, sinopsisd;
    TextView idd, pdfd;
    String role;
    Button editbt, hapusb, unduhb;
    ImageView imgd;
    private Context context;
    ApiInterface mApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_buku);
        titled=findViewById(R.id.titled);
        pdfd=findViewById(R.id.pdfd);
        authord=findViewById(R.id.authored);
        sinopsisd=findViewById(R.id.sinopsisd);
        unduhb=findViewById(R.id.unduhbtn);
        idd=findViewById(R.id.idd);
        imgd=findViewById(R.id.imageVd);
        editbt=findViewById(R.id.editbtn);
        hapusb=findViewById(R.id.hapusbtn);
        mApiService = ApiUrl.getAPIService();
        context = this;
        Intent mIntent = getIntent();
        idd.setTag(idd.getKeyListener());
        idd.setKeyListener(null);
        titled.setText(mIntent.getStringExtra("title"));
        authord.setText(mIntent.getStringExtra("author"));
        sinopsisd.setText(mIntent.getStringExtra("sinopsis"));
        pdfd.setText(mIntent.getStringExtra("pdf"));
        sharedPrefManager = new SharedPrefManager(this);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        idd.setVisibility(View.GONE);
        cekrole();
        builder.build().load("http://192.168.137.1/mainapibuku/src/uploads/a"+mIntent.getStringExtra("cover"))
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(imgd);
        unduhb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.137.1/mainapibuku/src/uploads/"+mIntent.getStringExtra("pdf")));
                startActivity(i);
            }
        });
        editbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:
                                loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
                                mApiService.booksEdit(mIntent.getStringExtra("id"), titled.getText().toString(), authord.getText().toString(), sinopsisd.getText().toString()).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()){
                                            loading.dismiss();
                                            Toast.makeText(context, "Data Berhasil Update", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(context, MainActivity.class));
                                        } else {
                                            loading.dismiss();
                                            Toast.makeText(context, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.e("Edit buku error", t.toString());
                                    }
                                });

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Benarkah anda ingin mengubah buku ini?").setPositiveButton(Html.fromHtml("<font color='#008577'>Ya</font>"), dialogClickListener)
                        .setNegativeButton(Html.fromHtml("<font color='#008577'>Tidak</font>"), dialogClickListener).show();
                // TODO Auto-generated method stub
            }
        });
        hapusb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:
                                loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
                                Log.d("id", idd.getText().toString());
                                mApiService.booksHapus(mIntent.getStringExtra("id")).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()){
                                            loading.dismiss();
                                            Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(context, MainActivity.class));
                                        } else {
                                            loading.dismiss();
                                            Toast.makeText(context, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.e("Hapus Error", t.toString());
                                    }
                                });

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Benarkah anda ingin menghapus buku ini dari daftar?").setPositiveButton(Html.fromHtml("<font color='#008577'>Ya</font>"), dialogClickListener)
                        .setNegativeButton(Html.fromHtml("<font color='#008577'>Tidak</font>"), dialogClickListener).show();
                // TODO Auto-generated method stub

            }

        });
    }
        public void cekrole(){
            HashMap<String, String> user = sharedPrefManager.getUserDetails();
            String spId = user.get("spId").toString();
            String spNama = user.get("spNama").toString();
            Integer spRole = sharedPrefManager.getSPRole();
            Bundle extras = getIntent().getExtras();
            if(spRole == 1){
                editbt.setVisibility(View.GONE);
                hapusb.setVisibility(View.GONE);
                titled.setEnabled(false);
                titled.setTextColor(Color.BLACK);
                authord.setEnabled(false);
                authord.setTextColor(Color.BLACK);
                sinopsisd.setEnabled(false);
                sinopsisd.setTextColor(Color.BLACK);
            }else if(spRole == 0){
                editbt.setVisibility(View.VISIBLE);
                hapusb.setVisibility(View.VISIBLE);
                titled.setEnabled(true);
                titled.setTextColor(Color.BLACK);
                authord.setEnabled(true);
                authord.setTextColor(Color.BLACK);
                sinopsisd.setEnabled(true);
                sinopsisd.setTextColor(Color.BLACK);
            }

        }

    public void onBackPressed() {
        finish();
    }
}
