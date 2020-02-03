package com.c4castro.microredes;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    public static String sendGet(String url, String token) throws IOException, UnAuthException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", token);
        con.setRequestProperty("Accept", "application/json");
        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);
        if (responseCode == HttpsURLConnection.HTTP_OK) { // connection ok
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            if (responseCode == 401) {
                throw new UnAuthException();
            }
            return null;
        }
    }

    public static String sendPost(String r_url, JSONObject postDataParams, @Nullable String token) throws Exception, UnAuthException {
        URL url = new URL(r_url);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(70000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        if (token != null) {
            Log.i(TAG, "sendPost: Sending Token");
            conn.setRequestProperty("Authorization", token);
        }
        conn.setRequestProperty("Accept", "application/json");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream(); OutputStreamWriter ow = new OutputStreamWriter(os)) {
            ow.write(postDataParams.toString());
        }

        int responseCode = conn.getResponseCode();
        BufferedReader in;
        if (responseCode == HttpsURLConnection.HTTP_OK)
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        else
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        if (responseCode == 401) {
            throw new UnAuthException();
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        conn.disconnect();
        return sb.toString();

    }

    private static String encodeParams(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    public static class UnAuthException extends Throwable {
    }
}
