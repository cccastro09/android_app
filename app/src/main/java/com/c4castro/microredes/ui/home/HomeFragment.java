package com.c4castro.microredes.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.c4castro.microredes.LoginActivity;
import com.c4castro.microredes.NetworkUtils;
import com.c4castro.microredes.PhotoActivity;
import com.c4castro.microredes.ProfileActivity;
import com.c4castro.microredes.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String API_URL = "https://android-prueba42.herokuapp.com/api/me";
    private Button mGotoProfile;
    private Button mGotoComparator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mGotoProfile = root.findViewById(R.id.gotoProfile);
        mGotoComparator = root.findViewById(R.id.gotoComparator);
        mGotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                startActivity(i);
            }
        });
        mGotoComparator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PhotoActivity.class);
                startActivity(i);
            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            new MeInfoTask().execute(cursor.getString(cursor.getColumnIndex("jwt")));
            cursor.close();
        } else {
            Intent i = new Intent(getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().finish();
        }

    }


    class MeInfoTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject s) {
            if (s == null) {
                Snackbar.make(getView().getRootView(), "No se pudo obtener la información.", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s.getJSONObject("user").getString("name"));
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
                }
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                String json = NetworkUtils.sendGet(API_URL, strings[0]);
                System.out.println(json);
                if (json != null) {
                    JSONObject responseJson = new JSONObject(json);
                    if (responseJson.has("id"))
                        System.out.println(responseJson.getString("jwt"));
                    return responseJson;
                }
                return null;
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