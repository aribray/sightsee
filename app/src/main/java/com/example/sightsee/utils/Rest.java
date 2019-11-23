package com.example.sightsee.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Rest {
    private static int READ_TIMEOUT = 10000;
    private static int CONNECT_TIMEOUT = 15000;
    private boolean useSSL = false;
    private URL url = null;
    private URLConnection urlConnection;
    private String error;
    private String responseString = null;
    private int responseCode;
    private String responseMessage;
    private InputStream inputStream;
    private HashMap<String, String> params;
    private ArrayList<Parameter> parameters;

    public enum SSLType {
        SSL,
        TLS
    }

    public Rest(String url) {
        this(url, SSLType.SSL);
    }

    public Rest(String url, SSLType sslType){

        try {
            this.url = new URL(url);
            if(this.url.getProtocol().toLowerCase().equals("https")) {
                TrustManager[] trustAllCerts = new TrustManager[]
                        {
                                new X509TrustManager()
                                {
                                    public java.security.cert.X509Certificate[] getAcceptedIssuers()  { return null; }
                                    public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType)  {}
                                    public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType)  {}
                                }
                        };

                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                String sslContextType = "SSL";
                if(sslType == SSLType.TLS)
                    sslContextType = "TLS";
                SSLContext sslContext = SSLContext.getInstance(sslContextType); // "TLS" "SSL"
                sslContext.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                this.useSSL = true;
            }
            this.urlConnection = this.url.openConnection();
            this.urlConnection.setReadTimeout(READ_TIMEOUT);
            this.urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            this.urlConnection.setDoInput(true);
            this.urlConnection.setDoOutput(true);
        } catch(Exception e){}

    }

    public static Rest newInstance(String url) {
        return new Rest(url);
    }

    public Rest setConnectTimeout(int timeout){
        this.CONNECT_TIMEOUT = timeout;
        return this;
    }

    public Rest setReadTimeout(int timeout) {
        this.READ_TIMEOUT = timeout;
        return this;
    }

    public static Rest get(String url) {
        return new Rest(url).get();
    }

    public Rest get(){
        try {
            if(this.useSSL) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) this.urlConnection;
                httpsURLConnection.setRequestMethod("GET");
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) this.urlConnection;
                httpURLConnection.setRequestMethod("GET");
            }
            this.request();
        } catch (IOException e) {
            Log.e("Rest", e.getMessage());
        }
        return this;
    }

    public static Rest post(String url) {
        return new Rest(url).post();
    }

    public static Rest post(String url, HashMap<String, String> params) {
        return new Rest(url).setParameter(params).post();
    }

    public static Rest post(String url, ArrayList<Parameter> params) {
        return new Rest(url).setParameter(params).post();
    }

    public Rest post(){
        try {
            if(this.useSSL) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) this.urlConnection;
                httpsURLConnection.setRequestMethod("POST");
            } else {
                HttpURLConnection httpURLConnection = (HttpURLConnection) this.urlConnection;
                httpURLConnection.setRequestMethod("POST");
            }
            this.request();
        } catch (IOException e) {
            Log.e("Rest", e.getMessage());
        }
        return this;
    }

    public Rest setParameter(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public Rest setParameter(ArrayList<Parameter> params) {
        this.parameters = params;
        return this;
    }

    private void request() throws IOException {
        this.prepareParams();
        this.urlConnection.connect();
        if(this.useSSL) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) this.urlConnection;
            this.responseCode = httpsURLConnection.getResponseCode();
            this.responseMessage = httpsURLConnection.getResponseMessage();
        } else {
            HttpURLConnection httpURLConnection = (HttpURLConnection) this.urlConnection;
            this.responseCode = httpURLConnection.getResponseCode();
            this.responseMessage = httpURLConnection.getResponseMessage();
        }
        this.inputStream = this.urlConnection.getInputStream();
    }

    private void prepareParams() throws IOException {
        if((this.params != null && this.params.size() > 0) ||
                (this.parameters != null && this.parameters.size() > 0)) {
            OutputStream outputStream = this.urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            ArrayList<String> params = new ArrayList<>();
            String paramString = null;
            if(this.params != null && this.params.size() > 0) {
                Set<String> keys = this.params.keySet();
                for (String key : keys) {
                    String value = this.params.get(key);
                    String param = key + "=" + URLEncoder.encode(value, "UTF-8");
                    params.add(param);
                }
                paramString = TextUtils.join("&", params);
                params = new ArrayList<>();
            }
            if(this.parameters != null && this.parameters.size() > 0) {
                for (Parameter param : this.parameters) {
                    params.add(param.keyValueString());
                }
                if(paramString != null)
                    paramString = paramString + "&" + TextUtils.join("&", params);
                else paramString = TextUtils.join("&", params);
            }
            writer.write(paramString);
            writer.flush();
            writer.close();
            outputStream.close();
        }
    }

    public String getString(){
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.inputStream, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            this.inputStream.close();
            this.responseString = sb.toString();
            return this.responseString;
        } catch (Exception e) {
            this.error = e.getMessage();
            Log.e("Rest", "Response string buffer error. " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmap() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.inputStream);
            if(this.inputStream != null)
                this.inputStream.close();
            return bitmap;
        } catch (Exception e) {
            this.error = e.getMessage();
            Log.e("Rest", e.getMessage());
        }
        return null;
    }

    public JSONObject getJSONObject(){
        if(this.responseString != null) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(this.responseString);
                return jsonObject;
            } catch (JSONException e) {
                this.error = e.getMessage();
                Log.e("Rest", this.error);
            }
        }
        return null;
    }

    public JSONArray getJSONArray() {
        if(this.responseString != null) {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(this.responseString);
                return jsonArray;
            } catch (JSONException e) {
                this.error = e.getMessage();
                Log.e("Rest", this.error);
            }
        }
        return null;
    }

    public String getResponseText(){
        if(this.responseString != null)
            return this.responseString;
        else return this.getString();
    }

    public String getError() {
        return this.error;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public static class Parameter {
        String key;
        String value;
        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public String keyValueString() {
            try {
                return key + "=" + URLEncoder.encode(this.value, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
