package com.glowingsoft.carplaterecognizer.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.Header;

public class WebRequest {
    private static AsyncHttpClient client;

    static{
        String token = "ddecd03711e795147f3feb345ec198eff5d957b6";
        //create object of loopj client
        //443 will save you from ssl exception
        client = new AsyncHttpClient(true,80,443);
        client.addHeader("Authorization", "Token "+token);
    }
    //concatenation of base url and file name
    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("response URL: ", GlobleClass.BASE_URL + relativeUrl+" ");
        return GlobleClass.BASE_URL + relativeUrl;
    }
    public static void post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.post(context, getAbsoluteUrl(url),params, responseHandler);
        Log.d("response", "post: request sent");
    }

}
