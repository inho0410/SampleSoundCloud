package com.example.tacademy.samplesoundcloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final String CLIENT_ID = "855fe8df184bf720b9d8e3a4bfb05caf";
    private static final String CLIENT_SECRET = "aede1edc37a86ede4606326274d1172c";
    private static final String LOGIN_URL = "https://soundcloud.com/connect";
    private static final String SCOPE = "*";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";

    private static final String TOKEN_URL ="https://api.soundcloud.com/oauth2/token";
    private static final int RC_LOGIN = 100;

    OkHttpClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = new OkHttpClient.Builder().build();

        Button btn = (Button)findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = makeConnectionURL();
                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.setData(Uri.parse(url));
                startActivityForResult(intent, RC_LOGIN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN) {
            String code = data.getStringExtra(BrowserActivity.PARAM_CODE);
            if (!TextUtils.isEmpty(code)) {
                getAccessToken(code);
            }
        }
    }

    public void getAccessToken(String code) {
        RequestBody body = new FormBody.Builder()
                .add("client_id",CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("redirect_uri", BrowserActivity.CALLBACK_URL)
                .add("grant_type", GRANT_TYPE)
                .add("code",code)
                .build();

        Request request = new Request.Builder().url(TOKEN_URL)
                .post(body)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                final AccessToken token = gson.fromJson(response.body().string(), AccessToken.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMeInfo(token);
                    }
                });
            }
        });
    }

    private static final String ME_URL = "https://api.soundcloud.com/me";
    private static final String PARAM_OAUTH_TOKEN = "oauth_token";

    private void getMeInfo(AccessToken token) {
        StringBuilder sb = new StringBuilder();
        sb.append(ME_URL).append("?").append(PARAM_OAUTH_TOKEN).append("=").append(token.accessToken);
        String url = sb.toString();
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                final MeInfo info = gson.fromJson(response.body().string(), MeInfo.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "info : " + info.username, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String makeConnectionURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(LOGIN_URL).append("?");
        sb.append("client_id").append("=").append(CLIENT_ID).append("&");
        sb.append("redirect_uri").append("=").append(BrowserActivity.CALLBACK_URL).append("&");
        sb.append("response_type").append("=").append(RESPONSE_TYPE);
//        .append("&");
//        sb.append("scope").append("=").append(SCOPE);
        return sb.toString();
    }

    private String makeTokenURL(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_URL).append("?");
        sb.append("client_id").append("=").append(CLIENT_ID).append("&");
        sb.append("client_secret").append("=").append(CLIENT_SECRET).append("&");
        sb.append("redirect_uri").append("=").append(BrowserActivity.CALLBACK_URL).append("&");
        sb.append("grant_type").append("=").append(GRANT_TYPE).append("&");
        sb.append("code").append("=").append(code).append("&");
        return sb.toString();
    }
}
