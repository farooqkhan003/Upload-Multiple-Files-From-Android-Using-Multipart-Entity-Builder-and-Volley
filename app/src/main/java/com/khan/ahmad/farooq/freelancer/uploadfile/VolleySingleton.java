package com.khan.ahmad.farooq.freelancer.uploadfile;

import android.app.Activity;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import Application.AppContext;

/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;
    private Context context;
    static Context con;

    protected VolleySingleton(Context c) {
        context = c;
        con = c;
        mRequestQueue = Volley.newRequestQueue(context);
    }


    public static VolleySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleton(con);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


}