package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SearchRecentSuggestionsProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.glowingsoft.carplaterecognizer.api.WebRequest;
import com.loopj.android.http.AsyncHttpClient;

import static com.glowingsoft.carplaterecognizer.api.WebRequest.client;

public class SettingActivity extends AppCompatActivity {
    EditText apitoken,regionCodeEdit;
    Button applychanged;
    String SHARED_PREF_NAME ="user_pref";
    SharedPreferences pref;
    ImageButton imageButton;
    TextView currenttoken;
    SharedPreferences.Editor editor;
    ConstraintLayout currentlayout,editlayout;
    String token = "ddecd03711e795147f3feb345ec198eff5d957b6";
    String regioncode="";
    String resize_image="";
    CheckBox checkBox;
    EditText resizeImageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        currentlayout=findViewById(R.id.current_token_layout);
        editlayout=findViewById(R.id.edit_current_token_layout);
        imageButton=findViewById(R.id.setting_edit_btn);
        currenttoken=findViewById(R.id.current_token);
        checkBox=findViewById(R.id.checkBox);
        resizeImageEdit=findViewById(R.id.resize_edit);
        currenttoken.setText("**********"+token.substring(token.length()-6));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentlayout.setVisibility(View.GONE);
                editlayout.setVisibility(View.VISIBLE);
            }
        });
        pref =getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();;
        ImageButton back =findViewById(R.id.backT_btn_type);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        apitoken=findViewById(R.id.token_Code);
        regionCodeEdit=findViewById(R.id.editText2);
        regioncode=pref.getString("RegionCode","");
        regionCodeEdit.setText(regioncode);
        applychanged=findViewById(R.id.button);
        apitoken.setText(token);
        applychanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apitoken.equals("") || apitoken.length()<1){
                    apitoken.setError("Add Token");
                }else {
                    String currentToken = apitoken.getText().toString();
                    editor.putString("CarToken", currentToken).apply();
                    String regioncodes =regionCodeEdit.getText().toString();
                    editor.putString("RegionCode",regioncodes).apply();
                    String lastDigits = token.substring(token.length()-6);
                    editor.putString("LastDigits",lastDigits).apply();
                    resize_image=resizeImageEdit.getText().toString();
                    editor.putString("Resized",resize_image).apply();
                    currentlayout.setVisibility(View.VISIBLE);
                    editlayout.setVisibility(View.GONE);
                    finish();
                    Toast.makeText(SettingActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                }
                if(checkBox.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                }else{
                    editor.putBoolean("checked", false);
                    editor.apply();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pref.contains("checked") && pref.getBoolean("checked",false) == true) {
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        String resized=pref.getString("Resized","");
        resizeImageEdit.setText(resized);
//        String edittedtoken ="";
//        edittedtoken=pref.getString("CarToken", "");
//        apitoken.setText(edittedtoken);
//        currenttoken.setText("**********"+edittedtoken.substring(edittedtoken.length()-6));

    }
}
