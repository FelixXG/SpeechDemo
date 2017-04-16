package com.felix.speechdemo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


/**
 * 这个波浪自定动画暂时还没做好
 */
public class RecognitionWaveView extends View {

    private static final int DEFAULT_MINWIDTH=500;

    private Paint mPaint;

    private long lasttime=0;

    private int lineSpeed=100;

    private float translateX=0;

    private float amplitude=1;//振幅

    /**
     * 音量
     */
    private float volume=10;

    private int fineness=1;

    private float targetVolume=1;

    private float maxVolume=100;

    private boolean isSet=false;

    private int voiceLineColor;//颜色

    private int sensibility=4;//灵敏度

    private ArrayList<Path> paths;


    public RecognitionWaveView(Context context) {
        super(context);
    }

    public RecognitionWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public RecognitionWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec),measure(heightMeasureSpec));
    }


    //当布局为wrap_content时设置默认宽
    private int measure(int demin){
        int result=DEFAULT_MINWIDTH;
        int specMode=MeasureSpec.getMode(demin);
        int specSize=MeasureSpec.getSize(demin);
        if(specMode==MeasureSpec.EXACTLY){
            result=specSize;
        }else{
            if(specMode==MeasureSpec.AT_MOST){
                result=Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lineChange();
        canvas.save();
        int moveY=getHeight()*3/4;
        for(int i=0;i<paths.size();i++){
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth()*5/6,getHeight()*3/4);
        }
        for(float j =getWidth()*5/6-1;j>=getWidth()/6;j-=fineness){
            float i=j-getWidth()/6;
            //这边必须保证起始点和终点时amplitede为0
            amplitude=4*volume*i/getWidth()-4*volume*i/getWidth()*i/getWidth()*12/10;
            for(int n=1;n<=paths.size();n++){
                float sin=amplitude*(float)Math.sin((i-Math.pow(1.22,n))*Math.PI/180-translateX);
                paths.get(n-1).lineTo(j,(2*n*sin/paths.size()-15*sin/paths.size()+moveY));
            }
        }
        for(int n=0;n<paths.size();n++){
            if(n==paths.size()-1){
                mPaint.setAlpha(255);
            }else{
                mPaint.setAlpha(n*130/paths.size());
            }
            if(mPaint.getAlpha()>0){
                canvas.drawPath(paths.get(n),mPaint);
            }
        }
        canvas.restore();
        postInvalidate();
    }




    private void initPaint(){
        mPaint=new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.color4));
        mPaint.setStrokeWidth(2);
        paths=new ArrayList<>(20);
        for(int i=0;i<20;i++){
            paths.add(new Path());
        }
    }

    private void lineChange(){
        if(lasttime==0){
            lasttime=System.currentTimeMillis();
            translateX+=5;
        }else{
            if(System.currentTimeMillis()-lasttime>lineSpeed){
                lasttime=System.currentTimeMillis();
                translateX+=5;
            }else{
                return;
            }
        }
        if(volume<targetVolume&&isSet){
            volume+=getHeight()/30;
        }else{
            isSet=false;
            if(volume<=10){
                volume=10;
            }else{
                if(volume<getHeight()/30){
                    volume-=getHeight()/60;
                }else{
                    volume-=getHeight()/30;
                }
            }
        }
    }

    public void setVolume(int volume){
        if(volume>100){
            volume=volume/100;
            volume=volume*2/5;
            if(volume>maxVolume*sensibility/30){
                isSet=true;
                this.targetVolume=getHeight()*volume/3/maxVolume;
            }
        }
    }



}
