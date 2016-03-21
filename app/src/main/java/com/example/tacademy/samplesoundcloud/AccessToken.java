package com.example.tacademy.samplesoundcloud;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tacademy on 2016-03-21.
 */
public class AccessToken {
    @SerializedName("access_token")
    String accessToken;

    String scope;
}
