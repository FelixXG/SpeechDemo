package com.felix.speechdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felix.speechdemo.Model.SpeechFlightInfo;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_speech;

    private TextView textView_speech;

    private  RecognizerDialog mDialog;

    private Button mBtnVoice;

    private Button mBtnSkip;


    private RecognitionProgressView mRecognitionProgressView;

    public static final String PRIVATE_SETTING="com.iflytek.setting";           //缓存数据的名称
    private static final String TAG = "BasicIatActivity";
    private EditText mContent;                          //显示内容

    //语义理解对象(语音到语义)
    private SpeechUnderstander mSpeechUnderstander;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SharedPreferences mSharedPreferences;

    private Toast mToast;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    int ret = 0; // 函数调用返回值

    private boolean isSpeaking;

    private SpeechFlightInfo speechFlightInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58d9cd0a");
        initIat();
        //初始化组件   后期修改为，程序一进入的时候进行初始化
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != mSpeechUnderstander ){
            // 退出时释放连接
            mSpeechUnderstander.cancel();
            mSpeechUnderstander.destroy();
        }
    }


    private void initView(){
        mContent=(EditText)findViewById(R.id.et_content);
        mBtnVoice=(Button)findViewById(R.id.btn_voice);
        mBtnSkip=(Button)findViewById(R.id.btn_skip);
        mBtnVoice.setOnClickListener(this);
        mBtnSkip.setOnClickListener(this);
        mRecognitionProgressView=(RecognitionProgressView)findViewById(R.id.recognition_view);
        int[] colors={
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        };

        int[] heights={60,76,58,80,55};

        mRecognitionProgressView.setColors(colors);
        mRecognitionProgressView.setBarMaxHeightsInDp(heights);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_voice:
                clickMethod();
                break;
            case R.id.btn_skip:
                clickSkip();
                break;

        }

    }

    /**
     * 显示toast
     * @param content  待显示的内容
     */
    public void showTip(String content){
        mToast.setText(content);
        mToast.show();
    }



    /**
     * 初始化语音识别中的对象
     */
    private void initIat() {
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
       // mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mSpeechUnderstander=SpeechUnderstander.createUnderstander(this,mSpeechUnderInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
       // mIatDialog = new RecognizerDialog(this, mInitListener);
        mSharedPreferences = getSharedPreferences(PRIVATE_SETTING,
                Activity.MODE_PRIVATE);

    }

    /**
     * 初始化监听器
     */
    private InitListener mSpeechUnderInitListener=new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + i);
            if (i != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+i);
            }
        }
    };


    /**
     * 听写UI监听器
     */

    /**
     * 语义理解回调
     */
        private SpeechUnderstanderListener mSpeechUnderstanderListener=new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            showTip("当前正在说话，音量大小：" + i);
            Log.d(TAG, bytes.length+"");
            mRecognitionProgressView.onRmsChanged(i);
        }

        @Override
        public void onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            mRecognitionProgressView.play();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");

        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            if(null!=understanderResult){
                Log.d("understander", understanderResult.getResultString());
            }

            //显示
            PraseResult(understanderResult,mContent);
           // Log.v("understander","text="+text);
//            if(!TextUtils.isEmpty(understanderResult.getResultString())){
//                mContent.setText(understanderResult.getResultString());
//            }
        }

        @Override
        public void onError(SpeechError speechError) {
            showTip(speechError.getPlainDescription(true));
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 参数设置
     * @param
     * @return
     */
    public void setParam() {
        String lang=mSharedPreferences.getString("understander_language_preference","mandarin");
        if(lang.equals("en_us")){
            //设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE,"en_us");
        }else{
            //设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
            //设置语言区域
            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT,lang);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("understander_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("understander_vadeos_preference", "1000"));

        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
    }


    /**
     * 触发点击事件时的方法
     */
    private void clickMethod() {
        mContent.setText(null);// 清空显示内容
        setParam();
        if(mSpeechUnderstander.isUnderstanding()){//开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            showTip("停止录音");
        }else{
            ret=mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
        }

    }

    private void clickSkip(){
        Intent intent=new Intent(MainActivity.this,IatActivity.class);
        startActivity(intent);
    }

    private void PraseResult(UnderstanderResult understanderResult,EditText mContent){
        Log.v("understand","json="+understanderResult.getResultString());
        speechFlightInfo=new Gson().fromJson(understanderResult.getResultString(),SpeechFlightInfo.class);
        String rc=speechFlightInfo.getRc();
        String temp=speechFlightInfo.getText();
        if ("0".equals(rc)) {

            String date=speechFlightInfo.getSemantic().getSlots().getStartDate().getDate();
            String flightno=speechFlightInfo.getSemantic().getSlots().getFlightno();

//            String startPoi=speechFlightInfo.getSemantic().getSlots().getStartLoc().getPoi();
//            String endPoi=speechFlightInfo.getSemantic().getSlots().getEndLoc().getPoi();
            Log.v("parsexgf","temp="+temp);
            Log.v("parsexgf","date="+date);
            Log.v("parsexgf","flightno="+flightno);
//            Log.v("startPoi","startPoi="+startPoi);
//            Log.v("endPoi","endPoi="+endPoi);
        }
        mContent.setText(temp);

    }



}
