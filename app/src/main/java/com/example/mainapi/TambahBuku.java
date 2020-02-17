package com.example.mainapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mainapi.apihelper.ApiInterface;
import com.example.mainapi.apihelper.ApiUrl;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahBuku extends AppCompatActivity {
    EditText judule, authore,sinopsis;
    TextView selfilet;
    Button tambahbtn, selgbrbt, selfilebtn;
    ImageView img;
    ApiInterface mApiService;
    Context mContext;
    ProgressDialog loading;
    Uri uriimg;
    String pdfPath;
    File imgFile,pdfFile;
    private static final int GALLERY_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_buku);
        judule=findViewById(R.id.judule);
        authore=findViewById(R.id.authore);
        sinopsis=findViewById(R.id.sinopsise);
        tambahbtn=findViewById(R.id.btnSimpanBuku);
        img=findViewById(R.id.selimg);
        selgbrbt=findViewById(R.id.selimgbtn);
        selfilebtn=findViewById(R.id.selfilebtn);
        selfilet=findViewById(R.id.selfile);

        Permission();
        mContext = this;

        mApiService = ApiUrl.getAPIService();
        selgbrbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(TambahBuku.this,
                        "Pilih Gambar",
                        3);
            }
        });
        tambahbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){

                            case DialogInterface.BUTTON_POSITIVE:
                                tambahbuku();

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Benarkah anda ingin menambah buku?").setPositiveButton(Html.fromHtml("<font color='#008577'>Ya</font>"), dialogClickListener)
                        .setNegativeButton(Html.fromHtml("<font color='#008577'>Tidak</font>"), dialogClickListener).show();
                // TODO Auto-generated method stub
            }
        });
        selfilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    private void tambahbuku() {
        if(judule.getText()==null || authore.getText()==null||sinopsis.getText()==null || imgFile==null || pdfFile==null)
            Toast.makeText(mContext, "Masukan data terlebih dahulu", Toast.LENGTH_LONG).show();
        else {
            loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/from-data"), imgFile);
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/from-data"), pdfFile);
            MultipartBody.Part bodya = MultipartBody.Part.createFormData("cover", imgFile.getName(), requestFile);
            MultipartBody.Part bodyb = MultipartBody.Part.createFormData("pdf",pdfFile.getName(),requestFile1);

            RequestBody judula = RequestBody.create(MediaType.parse("text/plain"), judule.getText().toString());
            RequestBody authora = RequestBody.create(MediaType.parse("text/plain"), authore.getText().toString());
            RequestBody sinopsisa = RequestBody.create(MediaType.parse("text/plain"), sinopsis.getText().toString());

            //Create request body with text description and text media type
            mApiService.booksBuat(judula, authora, sinopsisa, bodya, bodyb)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                loading.dismiss();
                                Toast.makeText(mContext, "Data Berhasil Ditambahkan", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(mContext, MainActivity.class));
                            } else {
                                loading.dismiss();
                                Toast.makeText(mContext, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            loading.dismiss();
                            Log.e("error", t.toString());
                            Toast.makeText(mContext, "Server tidak merespon", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }
    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent .setType("*/*");
        String[] mimeTypes = {"application/pdf"};
        intent .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                CropImage.activity(Uri.fromFile(imageFile))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .start(TambahBuku.this);
                //Uri uri = data.getData();
                //selfilet.setText(uri.getPath());
                //imgFile = new File(uri.getPath());
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
                Toast.makeText(TambahBuku.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
            }
        });

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uri = result.getUri();

                Glide.with(getApplicationContext())
                        .load(new File(uri.getPath()))
                        .into(img);

                imgFile = new File(uri.getPath());
                //selfilet.setText(uri.getPath());
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception exception = result.getError();
                Toast.makeText(this, ""+exception.toString(), Toast.LENGTH_SHORT).show();
            }

        }
        if(requestCode == READ_REQUEST_CODE)
        {
            Uri uri = data.getData();
            pdfPath = uri.getPath();
            //pdfFile = new File(uri.getPath());
            File file = new File(RealPathUtil.getRealPath(mContext, uri));
            pdfFile = file;
            selfilet.setText(pdfFile.getName());
        }
        if (resultCode != RESULT_OK) return;
        String path     = "";


    }
    private void Permission() {
        if (ContextCompat.checkSelfPermission(TambahBuku.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(TambahBuku.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(TambahBuku.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    public void onBackPressed() {
        finish();

    }
}
