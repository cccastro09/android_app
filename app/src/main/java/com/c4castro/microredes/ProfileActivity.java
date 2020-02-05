package com.c4castro.microredes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.c4castro.microredes.ui.home.HomeFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private static final String API_URL = "https://android-prueba42.herokuapp.com/api/me";
    private static final String API_URL_PASSWORD = "https://android-prueba42.herokuapp.com/api/me/updatePassword";

    private TextView mName;
    private TextView mEmail;
    private TextInputLayout mNewPassword;
    private TextInputLayout mRepeatNewPassword;
    private Button mChangePasswordBtn;
    private Button mButton2;
    private CoordinatorLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mNewPassword = findViewById(R.id.new_password);
        mRepeatNewPassword = findViewById(R.id.repeat_new_password);
        mChangePasswordBtn = findViewById(R.id.change_password_btn);
        mButton2 = findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton2.setEnabled(false);

                getContentResolver().delete(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users/*"), null, null);
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });
        Cursor cursor =
                getContentResolver().query(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            new MeInfoTask().execute(cursor.getString(cursor.getColumnIndex("jwt")));
            cursor.close();
        } else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }
        mRoot = findViewById(R.id.root);
    }


    class MeInfoTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject s) {
            if (s == null) {
                Snackbar.make(mRoot, "No se pudo obtener la información.", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        mName.setText(s.getJSONObject("user").getString("name"));
                        mEmail.setText(s.getJSONObject("user").getString("email"));


                        mChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Cursor cursor =
                                        getContentResolver().query(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    if (mNewPassword.getEditText().getText().toString().equals(mRepeatNewPassword.getEditText().getText().toString())) {
                                        mChangePasswordBtn.setEnabled(false);
                                        new UpdatePasswordTask().execute(cursor.getString(cursor.getColumnIndex("jwt")), mNewPassword.getEditText().getText().toString());
                                    } else {
                                        Snackbar.make(mRoot, "Las contraseñas no coinciden.", Snackbar.LENGTH_LONG)
                                                .show();
                                    }

                                    cursor.close();
                                } else {
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });
                    } else {
                        if (s.getString("error") == "UNAUTH") {
                            getContentResolver().delete(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users/*"), null, null);
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            finish();
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


    class UpdatePasswordTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject s) {
            mChangePasswordBtn.setEnabled(true);
            if (s == null) {
                Snackbar.make(mRoot, "No se pudo cambiar la contraseña", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        Snackbar.make(mRoot, "Contraseña acutalizada.", Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Snackbar.make(mRoot, s.getJSONObject("error").getString("message"), Snackbar.LENGTH_LONG)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                JSONObject params = new JSONObject();
                params.put("password", strings[1]);

                String json = NetworkUtils.sendPost(API_URL_PASSWORD, params, strings[0]);
                System.out.println(json);
                if (json != null) {
                    JSONObject responseJson = new JSONObject(json);
                    if (responseJson.has("id"))
                        System.out.println(responseJson.getString("jwt"));
                    return responseJson;
                }
                return null;
            } catch (Exception | NetworkUtils.UnAuthException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
