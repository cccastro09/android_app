package com.c4castro.microredes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private static final String API_URL = "https://android-prueba42.herokuapp.com/login";
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mLoginbtn;
    private CoordinatorLayout mRoot;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private Button mRegisterbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRoot = findViewById(R.id.root);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginbtn = findViewById(R.id.loginbtn);
        mTextView = findViewById(R.id.textView);
        mProgressBar = findViewById(R.id.progressBar);
        mRegisterbtn = findViewById(R.id.registerbtn);

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginbtn.setEnabled(false);
                LoginTask loginTask = new LoginTask();
                loginTask.execute(mEmail.getEditText().getText().toString(), mPassword.getEditText().getText().toString());
            }
        });
        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });
        if (checkUser()) {
            // user logged in
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmail.setVisibility(View.VISIBLE);
            mPassword.setVisibility(View.VISIBLE);
            mLoginbtn.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            mRegisterbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean saveUser(String id, String jwt) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("jwt", jwt);
        try {
            getContentResolver().insert(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), values);
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    boolean checkUser() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://com.c4castro.microredes.data.provider.UserProvider/users"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;

    }


    class LoginTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject s) {
            mLoginbtn.setEnabled(true);
            if (s == null) {
                Snackbar.make(mRoot, "No se pudo iniciar sesión", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        saveUser(String.valueOf(s.getInt("id")), s.getString("jwt"));
                        Snackbar.make(mRoot, "Login exitoso", Snackbar.LENGTH_LONG)
                                .show();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
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
                params.put("email", strings[0]);
                params.put("password", strings[1]);

                String json = NetworkUtils.sendPost(API_URL, params, null);
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
