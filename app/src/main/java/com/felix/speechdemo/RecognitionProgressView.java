package com.felix.speechdemo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * 柱形动画,随着音量的大小变化,可以动态这只柱形的颜色和个数
 */
public class RecognitionProgressView extends View {
    public static final int BARS_COUNT = 5;

    private static final int CIRCLE_RADIUS_DP = 5;
    private static final int CIRCLE_SPACING_DP = 11;
    private static final int ROTATION_RADIUS_DP = 25;
    private static final int IDLE_FLOATING_AMPLITUDE_DP = 3;

    private static final int[] DEFAULT_BARS_HEIGHT_DP = {60, 46, 70, 54, 64};

    private static final float MDPI_DENSITY = 1.5f;

    private final List<RecognitionBar> recognitionBars = new ArrayList<>();
    private Paint paint;
    private BarParamsAnimator animator;

    private int radius;
    private int spacing;


    private float density;

    private boolean animating;

    private SpeechRecognizer speechRecognizer;
    private RecognitionListener recognitionListener;
    private int barColor = -1;
    private int[] barColors;
    private int[] barMaxHeights;
    private int[] barInitHeights;

    public RecognitionProgressView(Context context) {
        super(context);
        init();
    }
    public RecognitionProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RecognitionProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(recognitionBars.isEmpty()){
            initBars();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(recognitionBars.isEmpty()){
            return;
        }

        if(animating){
            animator.animate();
            Log.v("ondraw animating","animating="+animating);
        }

        for(int i=0;i<recognitionBars.size();i++){
            RecognitionBar bar=recognitionBars.get(i);
            if(barColors!=null){
                paint.setColor(barColors[i]);
            }else if(barColor!=-1){
                paint.setColor(barColor);
            }
            canvas.drawRoundRect(bar.getRect(), radius, radius, paint);
            Log.v("ondraw","radius="+radius);
            if(animating){
                invalidate();
            }
        }
    }

    private void init(){
        paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);

        density=getResources().getDisplayMetrics().density;

        radius=(int)(CIRCLE_RADIUS_DP*density);
        spacing=(int)(CIRCLE_SPACING_DP*density);

    }

    public void play(){
        startRmsInterpolation();
        animating=true;
        invalidate();
    }

    private void initBars(){
        final List<Integer>heights=initBarHeight();
        int firstCirclePosition1=(int)this.getX()+2*spacing;
//        int firstCirclePosition=getMeasuredWidth()/2-
//                2*spacing-
//                4*radius;
        for(int i=0;i<BARS_COUNT;i++){
            int x=firstCirclePosition1+(2*radius+spacing)*i;
            RecognitionBar bar=new RecognitionBar(x,getMeasuredHeight()/2,barInitHeights[i],heights.get(i),radius);
            Log.v("xgfmeasureheight","measureheight="+getMeasuredHeight());
            recognitionBars.add(bar);
        }

    }

    private List<Integer> initBarHeight(){
        final List<Integer>barHeights=new ArrayList<>();
        if(barMaxHeights==null){
            for(int i=0;i<BARS_COUNT;i++){
                barHeights.add((int)(barMaxHeights[i]*density));
            }
        }else{
            for(int i=0;i<BARS_COUNT;i++){
                barHeights.add((int)(barMaxHeights[i]*density));
            }
        }
        return barHeights;
    }

    /**
     * 为bar设置颜色
     * @param colors
     */
    public void setColors(int[] colors){
        if(colors==null) return;

        barColors=new int[BARS_COUNT];
        if(colors.length<BARS_COUNT){
            System.arraycopy(colors,0,barColors,0,colors.length);
            for(int i=colors.length;i<BARS_COUNT;i++){
                barColors[i]=colors[0];
            }
        }else{
            System.arraycopy(colors,0,barColors,0,BARS_COUNT);
        }
    }


    public void setBarInitHeights(int [] heights){
        if(heights==null) return;

        barInitHeights=new int[BARS_COUNT];
        if(heights.length<BARS_COUNT){
            System.arraycopy(heights,0,barInitHeights,0,heights.length);//全部赋值为0
            for(int i=heights.length;i<BARS_COUNT;i++){
                barInitHeights[i]=heights[0];
            }
        }else{
            System.arraycopy(heights,0,barInitHeights,0,BARS_COUNT);
        }
    }

    public void setBarMaxHeightsInDp(int[] heights){
        if(heights==null) return;

        barMaxHeights=new int[BARS_COUNT];
        if(heights.length<BARS_COUNT){
            System.arraycopy(heights,0,barMaxHeights,0,heights.length);//全部赋值为0
            for(int i=heights.length;i<BARS_COUNT;i++){
                barMaxHeights[i]=heights[0];
            }
        }else{
            System.arraycopy(heights,0,barMaxHeights,0,BARS_COUNT);
        }
    }

    private void startRmsInterpolation(){
        animator=new RmsAnimator(recognitionBars);
        animator.start();
    }

    public void onRmsChanged(float rmsDB){
        ((RmsAnimator)animator).onRmsChanged(rmsDB);

    }
}
