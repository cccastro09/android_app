package com.c4castro.microredes.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.c4castro.microredes.R;
import com.c4castro.microredes.data.RedModel;

import java.util.List;

public class RedesAdapter extends RecyclerView.Adapter<RedesAdapter.ViewHolder> {

    List<RedModel> redes;

    private static final String TAG = "Redesadapter";

    public RedesAdapter(List<RedModel> redes) {
        this.redes = redes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_red, parent, false);
        return new ViewHolder(vi);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RedModel red = redes.get(position);
        holder.mName.setText(red.getTombreCientifico());
        holder.mDescription.setText(red.getInformacionDeLaEspecie());
        byte[] decodedString = Base64.decode(red.getFoto().substring(21), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.mPhoto.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + redes.size());
        return redes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhoto;
        private TextView mName;
        private TextView mDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.photo);
            mName = itemView.findViewById(R.id.name);
            mDescription = itemView.findViewById(R.id.description);
        }
    }
}
