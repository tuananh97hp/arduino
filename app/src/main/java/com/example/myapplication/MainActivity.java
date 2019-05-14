package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    Switch tb1;
    Switch tb2;
    Switch tb3;
    Switch tb4;
    TextView setText;
    TextView temp;
    TextView hum;
    TextView noti;
    String MES = "messenge";
    String TB1 = "TB1";
    String TB2 = "TB2";
    String TB3 = "TB3";
    String TB4 = "TB4";

    String HUM = "Humidity";
    String TEMP = "Temperature";
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb1 = findViewById(R.id.tb1);
        tb2 = findViewById(R.id.tb2);
        tb3 = findViewById(R.id.tb3);
        tb4 = findViewById(R.id.tb4);
        img = findViewById(R.id.imageView);
        setText = findViewById(R.id.textView);
        temp = findViewById(R.id.text_temp);
        hum = findViewById(R.id.text_hum);
        noti = findViewById(R.id.noti);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        deviceControl(database, TB1, tb1);
        deviceControl(database, TB2, tb2);
        deviceControl(database, TB3, tb3);
        deviceControl(database, TB4, tb4);
        deviceControl(database, TEMP, tb4);
        deviceControl(database, HUM, null);

        getControl(database, TB1, tb1);
        getControl(database, TB2, tb2);
        getControl(database, TB3, tb3);
        getControl(database, TB4, tb4);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    public void deviceControl(FirebaseDatabase database, final String TB, final Switch tb) {
        DatabaseReference databaseReference = database.getReference(TB);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (TB.equals("Humidity")) {
                    hum.setText(value+"%");
                } else if (TB.equals("Temperature")) {
                    temp.setText(value+"'C");
                    float tempratuer = Float.parseFloat(value);
                    if (tempratuer >= 35) {
                        if (!tb.isChecked()){
                            noti.setText("Thời tiết nóng, bạn nên bật quạt cho mát");
                        }else{
                            noti.setText("");
                        }
                    }
                    if (20 <=tempratuer && tempratuer <= 35) {
                        if (tb.isChecked()){
                            noti.setText("Thời tiết mát, bạn nên tắt quạt để tiết kiệm điện");
                        }else{
                            noti.setText("");
                        }

                    }
                    if (tempratuer <=10){
                        noti.setText("Thời tiết lạnh, bạn nên mặc áo ấm");
                    }
                } else {
                    if (value.equals("1")) {
                        tb.setChecked(true);
                    } else if (value.equals("0")) {
                        tb.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getControl(FirebaseDatabase database, String TB, final Switch tb) {
        final DatabaseReference databaseReference = database.getReference(TB);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databaseReference.setValue("1");
                } else {
                    databaseReference.setValue("0");
                }
            }
        });
    }

    //
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    setText.setText(result.get(0));
                    AIcontrol(result.get(0));
                }
                break;
            }

        }
    }


    protected void AIcontrol(String val) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseMes = database.getReference(MES);
        DatabaseReference databaseTb1 = database.getReference(TB1);
        DatabaseReference databaseTb2 = database.getReference(TB2);
        DatabaseReference databaseTb3 = database.getReference(TB3);
        DatabaseReference databaseTb4 = database.getReference(TB4);
        databaseMes.setValue(val);

        if (val.contains("Bật tất cả")) {
            databaseTb1.setValue("1");
            databaseTb2.setValue("1");
            databaseTb3.setValue("1");
            databaseTb4.setValue("1");
        } else if (val.contains("Tắt tất cả")) {
            databaseTb1.setValue("0");
            databaseTb2.setValue("0");
            databaseTb3.setValue("0");
            databaseTb4.setValue("0");
        } else if (val.contains("bật") || val.contains("bậc")) {
            if (val.contains("một") || val.contains("1")) {
                databaseTb1.setValue("1");
            }
            if (val.contains("hai") || val.contains("hay") || val.contains("hài") || val.contains("2")) {
                databaseTb2.setValue("1");
            }
            if (val.contains("ba") || val.contains("3")) {
                databaseTb3.setValue("1");
            }
            if (val.contains("quạt") || val.contains("4")) {
                databaseTb4.setValue("1");
            }
        } else if (val.contains("tắt")) {
            if (val.contains("một") || val.contains("1")) {
                databaseTb1.setValue("0");
            }
            if (val.contains("hai") || val.contains("2")) {
                databaseTb2.setValue("0");
            }
            if (val.contains("ba") || val.contains("3")) {
                databaseTb3.setValue("0");
            }
            if (val.contains("quạt") || val.contains("4")) {
                databaseTb4.setValue("0");
            }
        }
    }

}
