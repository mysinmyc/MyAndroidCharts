package org.mysinmyc.myandroidcharts.utils;

import android.os.Build;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by ace on 02/11/2016.
 */

public class TouchEventHelper implements ScaleGestureDetector.OnScaleGestureListener {


    public static final long TIMEOUTMS= 100;
    float _SelectedX=Float.NaN;
    float _SelectedY=Float.NaN;

    View _Parent;
    GestureDetector _GestureDetector;
    ScaleGestureDetector _ScaleGestureDetector;

    public TouchEventHelper(View pParent) {
        _Parent=pParent;
        _GestureDetector = new GestureDetector(_Parent.getContext(), new  GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (_TapListener!=null) {
                    _TapListener.onDoubleTap(e.getX(),e.getY());
                }
                return true;
            }


        });
        _ScaleGestureDetector=new ScaleGestureDetector(_Parent.getContext(),this);

    }

    ScaleGestureDetector.OnScaleGestureListener _OnScaleGestureListener;

    public ScaleGestureDetector.OnScaleGestureListener getOnScaleGestureListener() {
        return _OnScaleGestureListener;
    }

    public void setOnScaleGestureListener(ScaleGestureDetector.OnScaleGestureListener _OnScaleGestureListener) {
        this._OnScaleGestureListener = _OnScaleGestureListener;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {


        if (detector.getTimeDelta() < TIMEOUTMS ) {
            return false;
        }

        if (_OnScaleGestureListener!=null) {
            return _OnScaleGestureListener.onScale(detector);
        }

        return true;
    }

    
    
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        if (_OnScaleGestureListener!=null) {
            return _OnScaleGestureListener.onScaleBegin(detector);
        }

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (_OnScaleGestureListener!=null) {
             _OnScaleGestureListener.onScaleEnd(detector);
        }
    }

    public interface TapListener {
        void onDoubleTap(float pX, float pY);
    }

    TapListener _TapListener;

    public void setTapListener(TapListener pTapListener) {
        _TapListener=pTapListener;
    }

    public interface MoveListener {
        void onMove(float pX, float pY);
    }

    MoveListener _ContinuousMoveListener;

    public void setContinuousMoveListener(MoveListener pContinuousMoveListener) {
        _ContinuousMoveListener = pContinuousMoveListener;
    }

    MoveListener _MoveListener;

    public void setMoveListener(MoveListener pMoveListener) {
        _MoveListener = pMoveListener;
    }


    public interface ClickListener {
        void onClick(float pX, float pY);
    }

    ClickListener _ClickListener;

    public void setClickListener(ClickListener pClickListener) {
        _ClickListener=pClickListener;
    }


    float _StartX=Float.NaN;
    float _StartY=Float.NaN;
    boolean _Down=false;
    long _LastMoveTime = System.currentTimeMillis();


    public void processEvent(MotionEvent pEvent) {
        if (_GestureDetector.onTouchEvent(pEvent)) {
            return;
        }
        _ScaleGestureDetector.onTouchEvent(pEvent);
        if (_ScaleGestureDetector.isInProgress()) {
            _Down=false;
            return;
        }
        switch (pEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                _Down=true;
                _StartX = pEvent.getX();
                _StartY = pEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
                _Down=false;


                if (pEvent.getX() ==_StartX && pEvent.getY()==_StartY) {
                    if (_ClickListener != null) {
                        _ClickListener.onClick(pEvent.getX(), pEvent.getY());
                    }
                } else {
                    if (_MoveListener != null) {
                        _MoveListener.onMove(pEvent.getX()-_StartX, pEvent.getY()-_StartY);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:


                if (_Down==false) {
                    break;
                }

                long vNow = System.currentTimeMillis();

                if (vNow - _LastMoveTime < TIMEOUTMS) {
                    return;
                }
                _LastMoveTime=vNow;

                if (_ContinuousMoveListener!=null) {
                    _ContinuousMoveListener.onMove(pEvent.getX()-_StartX, pEvent.getY()-_StartY);
                    _StartX=pEvent.getX();
                    _StartY=pEvent.getY();
                }


                break;

        }


    }
}
