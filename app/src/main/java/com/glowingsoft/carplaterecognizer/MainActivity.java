package com.glowingsoft.carplaterecognizer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity  implements IPickResult {
    ImageView imageView;
    TextView textView;
    Context context;

    //dialoag box setup
    @SuppressLint("WrongConstant")
    PickSetup setup = new PickSetup()
            .setTitle("Choose")
            .setCancelText("Cancel")
            .setFlip(true)
            .setProgressText("Loading Image")
            .setMaxSize(500)
            .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
            .setCameraButtonText("Camera")
            .setGalleryButtonText("Gallery")
            .setIconGravity(Gravity.TOP)
            .setButtonOrientation(LinearLayoutCompat.HORIZONTAL)
            .setSystemDialog(false)
            .setGalleryIcon(R.drawable.photo)
            .setCameraIcon(R.drawable.cam);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        context=this;
        textView = findViewById(R.id.response);
    }
    //pick result method to get image after getting image form gallary or camera
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams params=new RequestParams();
            String file=r.getPath();
            Log.d("response", "filepath: "+file+" ");
            try {
                params.put("upload", new File(file));
                Log.d("response", "image to upload: "+params+" ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            WebRequest.post(context,"v1/plate-reader/",params,new JsonHttpResponseHandler()
            {
                @Override
                public void onStart() {
                    Log.d("response", "onStart: ");
                    super.onStart();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("response ",response.toString()+" ");
                    try {
                        Toast.makeText(context,response.getString("filename"),Toast.LENGTH_SHORT).show();
                        String imagepath="https://app.platerecognizer.com/media/uploads/01/27/"+response.getString("filename");
                        JSONArray Jsresults = response.getJSONArray("results");
                        if (Jsresults.length()>0)
                        {
                            for (int i = 0; i < Jsresults.length(); i++) {
                                JSONObject tabObj = Jsresults.getJSONObject(i);
                                textView.setText(response.getString("filename"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response", "onFailure: "+errorResponse+" ");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response", "onFailure: "+errorResponse+" ");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("response", "onFailure: "+responseString+" ");
                }
            });
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.takePhoto) {
            PickImageDialog.build(setup).show(MainActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
