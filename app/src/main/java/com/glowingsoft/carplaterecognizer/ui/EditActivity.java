package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.glowingsoft.carplaterecognizer.R;

import javax.sql.StatementEvent;

public class EditActivity extends AppCompatActivity {
    EditText plate,region,vihical;
    Button saveResult;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().hide();
        back=findViewById(R.id.back_btn_type);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        plate = findViewById(R.id.car_plate_edit);
        region = findViewById(R.id.region_code_edit);
        vihical = findViewById(R.id.vihical_type_edit);
        Intent intent = getIntent();
        plate.setText(intent.getStringExtra("car_plate"));
        region.setText(intent.getStringExtra("region_code"));
        vihical.setText(intent.getStringExtra("car_type"));
        saveResult = findViewById(R.id.save_btn);
        saveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDate();
            }
        });
    }

    private void saveDate() {
        String plate_edit=plate.getText().toString();
        String region_edit=region.getText().toString();
        String car_edit=vihical.getText().toString();
        Intent data = new Intent();
        data.putExtra("car_plate",plate_edit );
        data.putExtra("region_code",region_edit );
        data.putExtra( "car_type",car_edit);
        setResult(RESULT_OK, data);
        finish();

    }

}
