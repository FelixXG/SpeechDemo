package com.felix.speechdemo;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class AsrBasicActivity extends AppCompatActivity {

    private static String TAG="AsrBasicActivity";
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    //语音识别对象
    private SpeechRecognizer mAsr;
    private Toast mToast;

    private EditText mContent;

    //缓存
    private SharedPreferences mSharedPreferences;

    //云端语法文件
    private String mCloudGrammar=null;

    private static final String KEY_GRAMMAR_ABNF_ID="grammar_abnf_id";
    private static final String GRAMMAR_TYPE_ABNF="abnf";

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    int ret = 0; // 函数调用返回值

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58d9cd0a");
        initAsr();
        //初始化组件   后期修改为，程序一进入的时候进行初始化
        mToast = Toast.makeText(AsrBasicActivity.this,"",Toast.LENGTH_SHORT);
        mCloudGrammar = FucUtil.readFile(this,"grammar_sample.abnf","utf-8");
        initGrammar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mAsr){
            //退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }


    public void initIatData(EditText mContent){
        this.mContent=mContent;
    }

    /**
     * 初始化语音识别中的对象
     */
    private void initAsr(){
        mAsr=SpeechRecognizer.createRecognizer(this, mInitListener);
        mIatDialog = new RecognizerDialog(this, mInitListener);
        mSharedPreferences = getSharedPreferences(getPackageName(),
                Activity.MODE_PRIVATE);
    }

    /**
     * 初始化在线语法
     */
    private void initGrammar(){
        //构建语法文件
        String mTempGrammar=null;
        mTempGrammar=new String(mCloudGrammar);
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE,mEngineType);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
        ret=mAsr.buildGrammar(GRAMMAR_TYPE_ABNF,mTempGrammar,mCloudGrammarListener);
        if(ret!=ErrorCode.SUCCESS){
            showTip("语法构建失败,错误码:"+ret);
        }

    }


    /**
     * 云端构建语法监听器
     */
    private GrammarListener mCloudGrammarListener=new GrammarListener() {
        @Override
        public void onBuildFinish(String s, SpeechError speechError) {
            if(speechError==null){
                String grammarID=new String(s);
                SharedPreferences.Editor editor=mSharedPreferences.edit();
                if(!TextUtils.isEmpty(s)){
                    editor.putString(KEY_GRAMMAR_ABNF_ID,grammarID);
                    editor.commit();
                    showTip("语法构建成功:"+grammarID);
                }else{
                    showTip("语法构建失败,错误码:"+speechError.getErrorCode());
                }
            }
        }
    };
    /**
     * 初始化监听器
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if (null != results) {
                Log.d(TAG, "recognizer result：" + results.getResultString());
                String text ;
                text = JsonParser.parseGrammarResult(results.getResultString());
                Log.d("xgf121Dialog","text="+text);
                // 显示
                mContent.setText(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }
    };

    /**
     * 识别监听器
     */

    private RecognizerListener mRecognizerListener=new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
            Log.d(TAG, "进入RecoginzerListener,开始说话");
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.d(TAG, "进入RecoginzerListener,返回结果");
            if (null != recognizerResult) {
                Log.d(TAG, "recognizer result：" + recognizerResult.getResultString());
                String text ;
                    text = JsonParser.parseGrammarResult(recognizerResult.getResultString());
                Log.d("xgf121","text="+text);
                // 显示
                mContent.setText(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    /**
     * 参数设置
     */
    public boolean setParam(){
        boolean result=false;
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE,mEngineType);
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE,"json");

        String grammarId=mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID,null);//这里获取之前保存的grammarId    用于识别
        if(TextUtils.isEmpty(grammarId)){
            result=false;
        }else{
            //设置云端识别使用的语法id
            mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR,grammarId);
            result=true;
            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
            mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
            mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");

        }
        return result;

    }

    /**
     * 触发点击事件时的方法
     */
    public void clickMethod(){
        mContent.setText(null);
        //设置参数
        if(!setParam()){
            showTip("请先构建语法");
            return;
        }
//        boolean isShowDialog = mSharedPreferences.getBoolean(getString(R.string.pref_key_iat_show), true);
//        if (isShowDialog) {
//            // 显示听写对话框
//            mIatDialog.setListener(mRecognizerDialogListener);
//            mIatDialog.show();
//            showTip(getString(R.string.text_begin));
//        }else{
//            ret=mAsr.startListening(mRecognizerListener);
//        }
        ret=mAsr.startListening(mRecognizerListener);

    }


}
