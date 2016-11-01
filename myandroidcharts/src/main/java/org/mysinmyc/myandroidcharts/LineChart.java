package org.mysinmyc.myandroidcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.mysinmyc.myandroidcharts.data.DataLabel;
import org.mysinmyc.myandroidcharts.data.DataSet2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ace on 31/10/2016.
 */

public class LineChart extends View {

    int _GridLines=5;



    List<Integer> _Colors =new ArrayList<>();
    List<String> _Labels =new ArrayList<>();
    List<DataSet2D> _Data =new ArrayList<>();

    DataLabel _AxisXDataLabelFunction;

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setAxisXDataLabelFunction(DataLabel pAxisXDataLabelFunction) {
        _AxisXDataLabelFunction = pAxisXDataLabelFunction;
    }

    DataLabel _AxisYDataLabelFunction;

    public void setAxisYDataLabelFunction(DataLabel pAxisYDataLabelFunction) {
        _AxisYDataLabelFunction = pAxisYDataLabelFunction;
    }

    public void addDataSerie(int pColor, String pLabel, DataSet2D pData) {
        _Colors.add(pColor);
        _Labels.add(pLabel);
        _Data.add(pData);
    }


    static class DrawChartContext  {
        Canvas canvas;
        float dataLimits[];
        float marginLeft=0;
        float marginRight=0;
        float marginTop=0;
        float marginBottom=0;

        float scaleX;
        float scaleY;

        public DrawChartContext(Canvas pCanvas, float[] pDatalimits) {
            canvas=pCanvas;
            dataLimits=pDatalimits;
            scaleX = (canvas.getWidth() -marginLeft-marginRight) / (dataLimits[2]-dataLimits[0]);
            scaleY = (canvas.getHeight() -marginTop-marginBottom) / (dataLimits[3]-dataLimits[1]);
        }

        public float getPointForValueX(float pValueX) {
            return marginLeft+(pValueX-dataLimits[0])*scaleX+1;
        }

        public float getValueForPointX(float pPointX) {
            return (pPointX-1-marginLeft)/scaleX+dataLimits[0];
        }

        public float getPointForValueY(float pValueY) {
            return canvas.getHeight()+marginTop-(pValueY-dataLimits[1])*scaleY-1;
        }

        public float getValueForPointY(float pValueY) {
            return (canvas.getHeight()+marginTop-1-pValueY)/scaleY+dataLimits[1];
        }
    }



    /**
     *
     * @return [minx, miny, maxx,maxy]
     */
    protected  float[] getDataLimits() {

        float[] vLimits = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};

        for (DataSet2D vCurSerie : _Data) {
            float vCurMinX = vCurSerie.getAxisX().min();

            if (vCurMinX < vLimits[0]) {
                vLimits[0]=vCurMinX;
            }
            float vCurMaxX = vCurSerie.getAxisX().max();
            if (vCurMaxX > vLimits[2]) {
                vLimits[2]=vCurMaxX;
            }
            float vCurMinY = vCurSerie.getAxisY().min();

            if (vCurMinY < vLimits[1]) {
                vLimits[1]=vCurMinY;
            }
            float vCurMaxY = vCurSerie.getAxisY().max();
            if (vCurMaxY > vLimits[3]) {
                vLimits[3]=vCurMaxY;
            }
        }
        return vLimits;
    }

    @Override
    protected void onDraw(Canvas pCanvas) {
        DrawChartContext vDrawChartContext =new DrawChartContext(pCanvas,getDataLimits());

        drawGrid(vDrawChartContext);
        drawAxes(vDrawChartContext);
        drawDataLines(vDrawChartContext);

    }


    protected void drawDataLines(DrawChartContext pContext) {

        for (int vCntSeries=0 ;vCntSeries < _Data.size();vCntSeries++){
            DataSet2D vCurSeries = _Data.get(vCntSeries);

            Path  vCurPath= new Path();
            for (int vCntPoints=0 ;vCntPoints < vCurSeries.count();vCntPoints++){
                float vCurX = pContext.getPointForValueX(vCurSeries.getAxisX().getItemAt(vCntPoints));
                float vCurY =  pContext.getPointForValueY(vCurSeries.getAxisY().getItemAt(vCntPoints));

                if (vCntPoints==0) {
                    vCurPath.moveTo(vCurX,vCurY);
                } else {
                    vCurPath.lineTo(vCurX,vCurY);
                }
            }

            Paint vCurPaint =new Paint();
            vCurPaint.setStyle(Paint.Style.STROKE);
            vCurPaint.setStrokeWidth(4);
            vCurPaint.setColor(_Colors.get(vCntSeries));
            pContext.canvas.drawPath(vCurPath,vCurPaint);
        }


    }


    protected void drawAxes(DrawChartContext pContext) {

        Paint vCurPaint =new Paint();
        vCurPaint.setStyle(Paint.Style.STROKE);
        vCurPaint.setStrokeWidth(4);
        vCurPaint.setColor(Color.DKGRAY);

        float vAxisX=pContext.getPointForValueY(pContext.dataLimits[1] < 0 ? 0:pContext.dataLimits[1]);
        pContext.canvas.drawLine(0,vAxisX,pContext.canvas.getWidth(),vAxisX,vCurPaint);

        float vAxisY=pContext.getPointForValueX(pContext.dataLimits[0] < 0 ? 0:pContext.dataLimits[0]);
        pContext.canvas.drawLine(vAxisY,0,vAxisY,pContext.canvas.getHeight(),vCurPaint);
    }




    protected void drawGrid(DrawChartContext pContext) {


        float vYFactor = (0f+pContext.canvas.getHeight()) / pContext.canvas.getWidth();
        int vYLines;
        int vXLines;
        if (vYFactor >= 1) {
            vXLines = _GridLines;
            vYLines =  Math.round(vXLines * vYFactor);
        } else {
            vYLines = _GridLines;
            vXLines = Math.round(_GridLines / vYFactor);
        }

        Paint vCurPaint = new Paint();
        vCurPaint.setStyle(Paint.Style.STROKE);
        vCurPaint.setStrokeWidth(1);
        vCurPaint.setColor(Color.LTGRAY);
        vCurPaint.setTextSize(16);
        float vXSize = pContext.canvas.getWidth() / vXLines;
        float vXoffset = pContext.getPointForValueX(0) % vXSize;
        for (int vCntX = 0; vCntX < vXLines; vCntX++) {

            float vCurX= vXSize * vCntX+vXoffset;
            pContext.canvas.drawLine(vCurX, 0, vCurX, pContext.canvas.getHeight(), vCurPaint);

            float vCurValueX=pContext.getValueForPointX(vCurX);
            pContext.canvas.drawText(
                    _AxisXDataLabelFunction == null
                            ? "" + Math.round(vCurValueX)
                            : _AxisXDataLabelFunction.getLabelFor(vCurValueX)
                    ,
                    vCurX + 6, pContext.getPointForValueY(0) + vCurPaint.getTextSize() + 2, vCurPaint);
        }
        float vYSize = pContext.canvas.getHeight() / vYLines;
        float vYoffset = pContext.getPointForValueY(0) % vYSize;
        for (int vCntY = 0; vCntY < vYLines; vCntY++) {

            float vCurY=vYSize*vCntY+vYoffset;
            pContext.canvas.drawLine(0,vCurY, pContext.canvas.getWidth(),vCurY, vCurPaint);

            float vCurValueY=pContext.getValueForPointY(vCurY);
            if (Math.round(vCurValueY)!=0) {
                pContext.canvas.drawText(
                        _AxisYDataLabelFunction == null
                                ? "" + Math.round(vCurValueY)
                                : _AxisYDataLabelFunction.getLabelFor(vCurValueY)
                        ,
                        pContext.getPointForValueX(0) + 6, vCurY + vCurPaint.getTextSize() + 2, vCurPaint);
            }
        }

    }

    float _SelectedX=Float.NaN;
    float _SelectedY=Float.NaN;
    @Override
    public boolean onTouchEvent(MotionEvent pEvent) {

        _SelectedX = pEvent.getX();
        _SelectedY = pEvent.getY();

        invalidate();
        return true;

    }


}
