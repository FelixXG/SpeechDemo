package com.felix.speechdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * 一组bar的动画
 */
public class RmsAnimator implements BarParamsAnimator {

    final private List<BarRmsAnimator> barRmsAnimators;

    public RmsAnimator(List<RecognitionBar> recognitionBars) {
        this.barRmsAnimators = new ArrayList<>();
        for(RecognitionBar bar:recognitionBars){
            barRmsAnimators.add(new BarRmsAnimator(bar));
        }
    }

    @Override
    public void start() {
        for(BarRmsAnimator barRmsAnimator: barRmsAnimators){
            barRmsAnimator.start();
        }
    }

    @Override
    public void stop() {
        for(BarRmsAnimator barRmsAnimator:barRmsAnimators){
            barRmsAnimator.stop();

        }

    }

    @Override
    public void animate() {
        for(BarRmsAnimator barRmsAnimator:barRmsAnimators){
            barRmsAnimator.animate();
        }
    }

    public void onRmsChanged(float rmsDB){
        for(BarRmsAnimator barRmsAnimator:barRmsAnimators){
           barRmsAnimator.onRmsChanged(rmsDB);
        }

    }
}
