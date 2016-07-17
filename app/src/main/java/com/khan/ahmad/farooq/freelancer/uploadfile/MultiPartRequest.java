package com.khan.ahmad.farooq.freelancer.uploadfile;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Template.EndPointAPI;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import Template.Template;

/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public class MultiPartRequest extends Request<String> {

    private Response.Listener<String> mListener;
    private HttpEntity mHttpEntity;

    public MultiPartRequest(Response.ErrorListener errorListener,Response.Listener listener, ArrayList<File> file, int numberOfFiles) {
        super(Method.POST, EndPointAPI.PATH, errorListener);
        mListener = listener;
        mHttpEntity = buildMultipartEntity(file, numberOfFiles);

    }

    private HttpEntity buildMultipartEntity(ArrayList<File> file, int numberOffiles) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for(int i=0; i < file.size();i++){
            FileBody fileBody = new FileBody(file.get(i));
            builder.addPart(Template.Query.KEY_IMAGE.concat(String.valueOf(i)), fileBody);
        }

        builder.addTextBody(Template.Query.KEY_DIRECTORY, Template.Query.VALUE_DIRECTORY);
        builder.addTextBody("numberOfFiles", String.valueOf(numberOffiles));
        return builder.build();
    }




    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            mHttpEntity.writeTo(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            VolleyLog.e("" + e);
            return null;
        } catch (OutOfMemoryError e){
            VolleyLog.e("" + e);
            return null;
        }

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, "UTF-8"),
                    getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.success(new String(response.data),
                    getCacheEntry());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}