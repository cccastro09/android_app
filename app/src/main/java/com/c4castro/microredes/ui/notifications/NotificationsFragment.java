package com.c4castro.microredes.ui.notifications;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c4castro.microredes.LoginActivity;
import com.c4castro.microredes.NetworkUtils;
import com.c4castro.microredes.R;
import com.c4castro.microredes.data.RedModel;
import com.c4castro.microredes.ui.RedesAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class NotificationsFragment extends Fragment implements LocationListener {

    private static final String TAG = "NotificationsFragment";
    private static final String API_URL = "https://android-prueba42.herokuapp.com/api/redes";
    private static final int REQUEST_LOCATION_CODE = 42;
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 24;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 242;
    private RecyclerView mRecyclerView;
    private LocationManager locationManager;
    private RedesAdapter redesAdapter;
    private Location lastLocation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        mRecyclerView = root.findViewById(R.id.recyclerView);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), null, null, null, null);
        Context context = getContext();
        if (cursor != null && cursor.moveToFirst()) {
            new LoadDataTask().execute(cursor.getString(cursor.getColumnIndex("jwt")));
            cursor.close();
        } else {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().finish();
        }

        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showExplanation("Need Location permission", "To show the distance to the bacteria", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation("Need fine location permission", "To show the accurate distance to the bacteria", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);

                } else {
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
                }
                return;
            }
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        Context context = getContext();
        if (context != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 30, this);
        }
    }


    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        Context context = getContext();
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestPermission(permission, permissionRequestCode);
                        }
                    });
            builder.create().show();
        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permissionName}, permissionRequestCode);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        if (redesAdapter != null)
            redesAdapter.setLocation(location);
        Log.d(TAG, "onLocationChanged: " + location.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + provider + " \nStatus: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                break;
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationUpdates();
                break;
            default:
                break;
        }
    }

    class LoadDataTask extends AsyncTask<String, String, JSONObject> {

        private static final String TAG = "LoadDataTask";

        @Override
        protected void onPostExecute(JSONObject s) {

            if (s == null) {
                Snackbar.make(getView().getRootView(), "No se pudo cargar las redes", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        JSONArray data = s.getJSONArray("data");
                        List<RedModel> redes = new LinkedList<>();
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject item = data.getJSONObject(i);
                            redes.add(new RedModel(item.getLong("id"),
                                    item.getString("color"),
                                    item.getString("foto"),
                                    item.getString("familia"),
                                    item.getString("n_de_paredes_de_la_espora"),
                                    item.getString("pais"),
                                    item.getString("tamanio_um"),
                                    item.getString("textura_de_la_espora"),
                                    item.getString("tombre_cientifico"),
                                    item.getString("informacion_de_la_especie"),
                                    item.getDouble("lat"),
                                    item.getDouble("lng")));
                        }
                        Log.i(TAG, "onPostExecute: " + redes.size());
                        redesAdapter = new RedesAdapter(redes);
                        mRecyclerView.setAdapter(redesAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        if (lastLocation != null)
                            redesAdapter.setLocation(lastLocation);
                        else
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        if (s.getString("error") == "UNAUTH") {
                            getActivity().getContentResolver().delete(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users/*"), null, null);
                            Intent i = new Intent(getContext(), LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            getActivity().finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(getView().getRootView(), "No se pudo cargar las redes", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                Log.i(TAG, "doInBackground: " + strings[0]);
                String json = NetworkUtils.sendGet(API_URL, strings[0]);
                JSONArray array = new JSONArray(json);
                JSONObject result = new JSONObject();
                result.put("error", null);
                result.put("data", array);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (NetworkUtils.UnAuthException e) {
                JSONObject result = new JSONObject();
                try {
                    result.put("error", "UNAUTH");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return result;
            }
            return null;
        }

    }
}