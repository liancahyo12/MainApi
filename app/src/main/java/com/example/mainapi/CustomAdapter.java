package com.example.mainapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.mainapi.model.BukuData;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private List<BukuData> mdataList;
    private Context context;

    public CustomAdapter(Context context, List<BukuData> result) { mdataList = result;
        this.context = context;
    }


    class CustomViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        private TextView id,nama,kota,company;
        private ImageView gbuku;


        CustomViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            id=mView.findViewById(R.id.idt);
            nama=mView.findViewById(R.id.namat);
            kota=mView.findViewById(R.id.kotat);
            gbuku=mView.findViewById(R.id.gbbuku);
        }
    }


    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.CustomViewHolder holder, final int position) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load("http://192.168.137.1/mainapibuku/src/uploads/"+mdataList.get(position).getCover())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(holder.gbuku);
        holder.id.setText("ID ="+mdataList.get(position).getId());
        holder.nama.setText("Judul ="+mdataList.get(position).getTitle());
        holder.kota.setText("Author ="+mdataList.get(position).getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), EditBuku.class);
                mIntent.putExtra("id", mdataList.get(position).getId());
                mIntent.putExtra("title",mdataList.get(position).getTitle());
                mIntent.putExtra("author", mdataList.get(position).getAuthor());
                mIntent.putExtra("sinopsis", mdataList.get(position).getSinopsis());
                mIntent.putExtra("cover", mdataList.get(position).getCover());
                mIntent.putExtra("pdf", mdataList.get(position).getPdf());
                view.getContext().startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdataList.size();
    }
}