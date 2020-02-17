package com.example.mainapi.apihelper;

import com.example.mainapi.model.BukuData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    BukuData buku = new BukuData();
    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/login.php
    @FormUrlEncoded
    @POST("mainapibuku/index.php/user/login")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);

    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/register.php
    @FormUrlEncoded
    @POST("mainapibuku/index.php/user/register")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);
    @Multipart
    @POST("mainapibuku/index.php/books/")
    Call<ResponseBody> booksBuat(@Part("title") RequestBody title,
                                       @Part("author") RequestBody author,
                                       @Part("sinopsis") RequestBody sinopsis,
                                       @Part MultipartBody.Part cover,
                                       @Part MultipartBody.Part pdf);
    @FormUrlEncoded
    @PUT("mainapibuku/index.php/books/{id}")
    Call<ResponseBody> booksEdit(@Path("id") String id,
                                 @Field("title") String title,
                                 @Field("author") String author,
                                 @Field("sinopsis") String sinopsis);
    @GET("mainapibuku/index.php/books/")
    Call<List<BukuData>> getBuku();
    @DELETE("matkul/{idmatkul}")
    Call<ResponseBody> deteleMatkul(@Path("idmatkul") String idmatkul);
    @DELETE("mainapibuku/index.php/books/{id}")
    Call<ResponseBody> booksHapus(@Path("id") String id);
}
