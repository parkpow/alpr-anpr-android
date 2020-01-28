package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.loopj.android.http.AsyncHttpClient;

import static com.glowingsoft.carplaterecognizer.api.WebRequest.client;

public class SettingActivity extends AppCompatActivity {
    EditText ApiToken,regionCode;
    Button applychanged;
    String SHARED_PREF_NAME ="user_pref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token = "ddecd03711e795147f3feb345ec198eff5d957b6";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pref =getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();;
        ApiToken=findViewById(R.id.token_Code);
        regionCode=findViewById(R.id.editText2);
        applychanged=findViewById(R.id.button);
        //to show enable home back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        setTitle("Settings");
        ApiToken.setText(token);
        applychanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiToken.equals("") || ApiToken.length()<1){
                    ApiToken.setError("Add Token");
                }else {
                    String currentToken = ApiToken.getText().toString();
                    editor.putString("CarToken", currentToken).apply();
                    String regioncodes =regionCode.getText().toString();
                    editor.putString("RegionCode",regioncodes).apply();

                    Toast.makeText(SettingActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
