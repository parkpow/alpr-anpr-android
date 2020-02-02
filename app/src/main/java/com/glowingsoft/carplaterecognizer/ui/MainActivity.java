package com.glowingsoft.carplaterecognizer.ui;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
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
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity  implements IPickResult,View.OnClickListener {
    ImageView imageView,emptyImage;
    TextView plate_txt,region_txt,vihical_txt;
    Context context;
    ImageButton editResult;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String SHARED_PREF_NAME ="user_pref";
    String token = "";
    String countrycode="";
    Date date;
    DateFormat df;
    String plate_type="",region_type="",car_type="";
    CardView plateCard,regionCard,vihicalCard;


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
        context=this;
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        date = new Date();
        df = new SimpleDateFormat("MM/dd/");
        // Use London time zone to format the date in
        df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        progressBar=findViewById(R.id.homeprogress);
        plate_txt = findViewById(R.id.car_plate);
        region_txt = findViewById(R.id.region_code);
        vihical_txt = findViewById(R.id.vihicle_type);
        emptyImage=findViewById(R.id.empty_image);
        plateCard=findViewById(R.id.cardView);
        vihicalCard=findViewById(R.id.cardView3);
        regionCard=findViewById(R.id.cardView2);
        editResult=findViewById(R.id.edit_btn);
        editResult.setOnClickListener(this);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);

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
                    region_txt.setText(null);
                    plate_txt.setText(null);
                    vihical_txt.setText(null);
                    imageView.setImageResource(R.drawable.upload);

                    Log.d("response", "onStart: ");
                    super.onStart();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    Log.d("response ",response.toString()+" ");
                    try {
                        //image path
                        String imagepath="https://app.platerecognizer.com/media/uploads/"+df.format(date)+response.getString("filename");
                        //json array or results
                        JSONArray Jsresults = response.getJSONArray("results");
                        if (Jsresults.length()>0)
                        {
                            for (int i = 0; i < Jsresults.length(); i++) {
                                JSONObject tabObj = Jsresults.getJSONObject(i);
                                plate_txt.setText(tabObj.getString("plate"));
                                region_txt.setText(tabObj.getJSONObject("region").getString("code"));
                                vihical_txt.setText(tabObj.getJSONObject("vehicle").getString("type"));
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
                                regionCard.setVisibility(View.VISIBLE);
                                plateCard.setVisibility(View.VISIBLE);
                                vihicalCard.setVisibility(View.VISIBLE);
                                editResult.setVisibility(View.VISIBLE);
                                emptyImage.setVisibility(View.GONE);

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
    public void onClick(View v) {
        if (v.getId()==R.id.imageView)
        {
            PickImageDialog.build(setup).show(MainActivity.this);
        }
        if (v.getId()==R.id.edit_btn)
        {
            plate_type = plate_txt.getText().toString();
            region_type=region_txt.getText().toString();
            car_type=vihical_txt.getText().toString();

            Intent intent =new Intent(MainActivity.this,EditActivity.class);
            intent.putExtra("car_plate",plate_type );
            intent.putExtra("region_code", region_type);
            intent.putExtra("car_type",car_type );
            startActivityForResult(intent, 123);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check if request code is for inserting new list then perform insertion
        if (requestCode == 123 && resultCode == RESULT_OK) {
            String plate = data.getStringExtra("car_plate");
            String region = data.getStringExtra("region_code");
            String car = data.getStringExtra("car_type");
            Log.d("response", "onActivityResult: "+plate+" ");
            plate_txt.setText(plate);
            region_txt.setText(region);
            vihical_txt.setText(car);
            Toast.makeText(this, "Results saved", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId()==R.id.next_image)
        {
            PickImageDialog.build(setup).show(MainActivity.this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
