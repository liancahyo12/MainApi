package com.example.mainapi.apihelper;

public class ApiUrl {

    // 10.0.2.2 ini adalah localhost.
    public static final String BASE_URL_API = "http://192.168.137.1/";

    // Mendeklarasikan Interface BaseApiService
    public static ApiInterface getAPIService(){
        return ApiClient.getClient(BASE_URL_API).create(ApiInterface.class);
    }
}
