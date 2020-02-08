package com.c4castro.microredes.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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

import static java.lang.String.format;

public class RedesAdapter extends RecyclerView.Adapter<RedesAdapter.ViewHolder> {

    private List<RedModel> redes;

    public void setLocation(Location location) {
        this.location = location;
        this.notifyDataSetChanged();
    }

    private Location location;

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
        if (this.location != null) {
            Log.d(TAG, "onBindViewHolder: " + red.getLat() + ',' + red.getLng());
            double distance = distance(location.getLatitude(), location.getLongitude(), red.getLat(), red.getLng(), "K");
            holder.mLocation.setText("Distancia: " + format("%.2f", distance) + "Km");
        } else {
            holder.mLocation.setText("Distancia: -");
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
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
        private TextView mLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhoto = itemView.findViewById(R.id.photo);
            mName = itemView.findViewById(R.id.name);
            mDescription = itemView.findViewById(R.id.description);
            mLocation = itemView.findViewById(R.id.location);
        }
    }
}
