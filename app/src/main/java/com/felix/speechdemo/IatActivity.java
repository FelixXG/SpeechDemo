package com.felix.speechdemo;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IatActivity extends IatBasicActivity implements View.OnClickListener {
    private EditText mContent;

    private Button mBtmVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iat);
        initView();
    }

    private void initView(){
        mContent=(EditText)findViewById(R.id.et_content1);
        mBtmVoice=(Button)findViewById(R.id.btn_voice1);
        mBtmVoice.setOnClickListener(this);

        initIatData(mContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_voice1:
                clickMethod();
        }
    }
}
