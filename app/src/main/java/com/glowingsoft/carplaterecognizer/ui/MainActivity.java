package com.glowingsoft.carplaterecognizer.ui;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import cz.msebera.android.httpclient.Header;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
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
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements IPickResult {
    ImageView imageView;
    TextView response_txt,plate_txt,region_txt,timestamp_txt;
    Context context;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String SHARED_PREF_NAME ="user_pref";
    String token = "";
    String countrycode="";


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
        sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        progressBar=findViewById(R.id.homeprogress);
        context=this;
        response_txt = findViewById(R.id.responseTime);
        plate_txt = findViewById(R.id.carPlate);
        region_txt = findViewById(R.id.regioin);
        timestamp_txt = findViewById(R.id.timestamp);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(setup).show(MainActivity.this);
                region_txt.setText(null);
                plate_txt.setText(null);
                response_txt.setText(null);
                timestamp_txt.setText(null);
                imageView.setImageResource(R.drawable.upload);
            }
        });

    }
    //to change token value
    @Override
    protected void onResume() {
        super.onResume();
        token=sharedPreferences.getString("CarToken", "currentToken");
        if (token.equals("")){
            Toast.makeText(context, "Token Not Found", Toast.LENGTH_SHORT).show();
        }else {
            WebRequest.client.addHeader("Authorization","Token "+token);
        }
    }
    //pick result method to get image after getting image form gallary or camera
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            RequestParams params=new RequestParams();
            String file=r.getPath();
            countrycode=sharedPreferences.getString("RegionCode","regioncodes");
            Log.d("response", "filepath: "+file+" ");
            try {
                params.put("upload", new File(file));
                params.put("regions",countrycode);
                Log.d("response", "image to upload: "+params+" ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            WebRequest.post(context,"v1/plate-reader/",params,new JsonHttpResponseHandler()
            {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d("response", "onStart: ");
                    super.onStart();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("response ",response.toString()+" ");
                    try {
                        //set it as current date.
                        String date_n = new SimpleDateFormat("MM/dd/", Locale.getDefault()).format(new Date());
                        //image path
                        String imagepath="https://app.platerecognizer.com/media/uploads/"+date_n+response.getString("filename");
                        //json array or results
                        JSONArray Jsresults = response.getJSONArray("results");
                        if (Jsresults.length()>0)
                        {
                            for (int i = 0; i < Jsresults.length(); i++) {
                                JSONObject tabObj = Jsresults.getJSONObject(i);
                                response_txt.setText("Processing Time: "+response.getString("processing_time"));
                                plate_txt.setText("Car Plate: "+tabObj.getString("plate"));
                                region_txt.setText("Region Code: "+tabObj.getString("region"));
                                timestamp_txt.setText("TimeStamp: "+response.getString("timestamp"));

                                Picasso.with(context)
                                        .load(imagepath)
                                        .into(imageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                              progressBar.setVisibility(View.GONE);
                                            }
                                            @Override
                                            public void onError() {

                                            }
                                        });
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
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Invalid Image or Token Code",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("response", "onFailure: "+errorResponse+" ");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Invalid Image or Token Code",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("response", "onFailure: "+responseString+" ");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Invalid Image or Token Code",Toast.LENGTH_SHORT).show();
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
        if (item.getItemId()==R.id.settings)
        {
            Intent intent =new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
