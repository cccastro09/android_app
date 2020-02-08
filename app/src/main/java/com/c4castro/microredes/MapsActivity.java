package com.c4castro.microredes;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.c4castro.microredes.data.RedModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CoordinatorLayout mRoot;
    private static final String API_URL = "https://android-prueba42.herokuapp.com/api/redes";
    private List<RedModel> redes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mRoot = findViewById(R.id.root);
    }

    private void loadMarkers() {
        if (this.mMap != null) {
            LatLng sydney = null;
            for (RedModel rede : this.redes) {

                sydney = new LatLng(rede.getLat(), rede.getLng());
                mMap.addMarker(new MarkerOptions().position(sydney).title(rede.getTombreCientifico()));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (this.redes != null && !this.redes.isEmpty()) {
            this.loadMarkers();
        }
    }


    class LoadDataTask extends AsyncTask<String, String, JSONObject> {

        private static final String TAG = "LoadDataTask";

        @Override
        protected void onPostExecute(JSONObject s) {

            if (s == null) {
                Snackbar.make(mRoot, "No se pudo cargar las redes", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        JSONArray data = s.getJSONArray("data");
                        redes = new LinkedList<>();
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

                        if (mMap != null) {
                            loadMarkers();
                        }

                    } else {
                        if (s.getString("error").equals("UNAUTH")) {
                            getContentResolver().delete(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users/*"), null, null);
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(mRoot, "No se pudo cargar las redes", Snackbar.LENGTH_LONG)
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
