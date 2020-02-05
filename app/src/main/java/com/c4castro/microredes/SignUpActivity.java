package com.c4castro.microredes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    private static final String API_URL = "https://android-prueba42.herokuapp.com/signup";

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mPassword2;
    private Button mLoginbtn;
    private CoordinatorLayout mRoot;
    private TextInputLayout mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassword2 = findViewById(R.id.password2);
        mLoginbtn = findViewById(R.id.loginbtn);
        mRoot = findViewById(R.id.root);
        mName = findViewById(R.id.name);

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassword.getEditText().getText().toString().equals(mPassword2.getEditText().getText().toString()) &&
                        !mEmail.getEditText().getText().toString().isEmpty() &&
                        !mName.getEditText().getText().toString().isEmpty()
                ) {
                    new SignUpTask().execute(mName.getEditText().getText().toString(),mEmail.getEditText().getText().toString(),mPassword.getEditText().getText().toString());
                }else{
                    Snackbar.make(mRoot, "Completa todos los campos", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    class SignUpTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPostExecute(JSONObject s) {
            mLoginbtn.setEnabled(true);
            if (s == null) {
                Snackbar.make(mRoot, "No se pudo registrar", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    if (s.isNull("error")) {
                        Snackbar.make(mRoot, "Usuario registrado con éxito", Snackbar.LENGTH_LONG)
                                .show();
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
                params.put("name", strings[0]);
                params.put("email", strings[1]);
                params.put("password", strings[2]);

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
