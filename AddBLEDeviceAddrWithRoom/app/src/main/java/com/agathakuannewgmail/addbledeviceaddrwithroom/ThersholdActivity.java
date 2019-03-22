package com.agathakuannewgmail.addbledeviceaddrwithroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ThersholdActivity extends AppCompatActivity {
    private ImageButton mSendBackMain;
    private SeekBar mSpeedThers,mFreqThers;
    private CheckBox mDirApart, mDirDepart, mDirAll;
    private TextView mSpeedThersPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thershold);

        final GlobalVariables gv = (GlobalVariables)getApplicationContext();

        mSpeedThersPrint = (TextView)findViewById(R.id.textView3);

        mSpeedThers = (SeekBar)findViewById(R.id.speedThers);
        mSpeedThers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpeedThersPrint.setText(String.valueOf(progress));
                gv.setSpeedThershold((double)progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSpeedThersPrint.setText(" ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpeedThersPrint.setText(" "+gv.getSpeedThershold()+" ok");
            }
        });


        mDirAll = (CheckBox)findViewById(R.id.chk1);
        mDirAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    gv.setDirectionThershold("all");
            }
        });
        mDirApart =(CheckBox)findViewById(R.id.chk2);
        mDirApart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    gv.setDirectionThershold("apart");
            }
        });
        mDirDepart =(CheckBox)findViewById(R.id.chk3);
        mDirDepart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    gv.setDirectionThershold("depart");
            }
        });

        mSendBackMain = (ImageButton)findViewById(R.id.sendBackMain);
        mSendBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThersholdActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
