package com.c4castro.microredes.ui.notifications;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.c4castro.microredes.LoginActivity;
import com.c4castro.microredes.MainActivity;
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

public class NotificationsFragment extends Fragment {


    private static final String API_URL = "https://android-prueba42.herokuapp.com/api/redes";
    private RecyclerView mRecyclerView;

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
        if (cursor != null && cursor.moveToFirst()) {
            new LoadDataTask().execute(cursor.getString(cursor.getColumnIndex("jwt")));
            cursor.close();
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
                                    "",
                                    item.getString("familia"),
                                    item.getString("n_de_paredes_de_la_espora"),
                                    item.getString("pais"),
                                    item.getString("tamanio_um"),
                                    item.getString("textura_de_la_espora"),
                                    item.getString("tombre_cientifico"),
                                    item.getString("informacion_de_la_especie")));
                        }
                        Log.i(TAG, "onPostExecute: " + redes.size());
                        RedesAdapter adapter = new RedesAdapter(redes);
                        mRecyclerView.setAdapter(adapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        if (s.getString("error") == "UNAUTH") {
                            getActivity().getContentResolver().delete(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users/*"),null,null);
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
                result.put("error",null);
                result.put("data",array);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (NetworkUtils.UnAuthException e) {
                JSONObject result = new JSONObject();
                try {
                    result.put("error","UNAUTH");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return result;
            }
            return null;
        }

    }
}