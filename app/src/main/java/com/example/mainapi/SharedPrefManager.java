package com.example.mainapi;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by fariz ramadhan.
 * website : www.farizdotid.com
 * github : https://github.com/farizdotid
 * linkedin : https://www.linkedin.com/in/farizramadhan/
 */


public class SharedPrefManager {

    public static final String SP_MAHASISWA_APP = "MainApi";
    public static final String SP_ID = "spId";
    public static final String SP_NAMA = "spNama";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_ROLE = "spRole";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_MAHASISWA_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void CreateSession(String id, String nama, Integer role){
        spEditor.putString(SP_ID, id);
        spEditor.putString(SP_NAMA, nama);
        spEditor.putInt(SP_ROLE, role);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }
    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();
        user.put("spId",sp.getString(SP_ID, null));
        user.put("spNama",sp.getString(SP_NAMA, null));

        return user;
    }

    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }

    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }
    public Integer getSPRole(){
        return sp.getInt(SP_ROLE, 0);
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
