package com.c4castro.microredes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRoot = findViewById(R.id.root);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginbtn = findViewById(R.id.loginbtn);

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginbtn.setEnabled(false);
                LoginTask loginTask = new LoginTask();
                loginTask.execute(mEmail.getEditText().getText().toString(), mPassword.getEditText().getText().toString());


            }
        });
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
                        Snackbar.make(mRoot, "Login exitoso", Snackbar.LENGTH_LONG)
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
                params.put("email", strings[0]);
                params.put("password", strings[1]);

                String json = NetworkUtils.sendPost(API_URL, params);
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
            }

            return null;
        }

    }
}
