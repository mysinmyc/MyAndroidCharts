package org.mysinmyc.myandroidcharts;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mysinmyc.myandroidcharts.data.DataLabel;
import org.mysinmyc.myandroidcharts.data.DataSet2D;
import org.mysinmyc.myandroidcharts.utils.MyViewUtils;
import org.mysinmyc.myandroidcharts.utils.TouchEventHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ace on 31/10/2016.
 */

public class LineChart extends RelativeLayout {


    public static final String DATALABEL_TAG="DataLabel";

    List<Integer> _Colors =new ArrayList<>();
    List<String> _Labels =new ArrayList<>();
    List<DataSet2D> _Data =new ArrayList<>();

    DataLabel _AxisXDataLabelFunction;

    public LineChart(Context context) {
        super(context);
        setWillNotDraw(false);

    }

    public LineChart(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setWillNotDraw(false);

    }


    /**
     * Specify a data label function for X values
     * @param pAxisXDataLabelFunction
     */
    public void setAxisXDataLabelFunction(DataLabel pAxisXDataLabelFunction) {
        _AxisXDataLabelFunction = pAxisXDataLabelFunction;
    }

    DataLabel _AxisYDataLabelFunction;

    /**
     * Specify a data label function for Y values
     * @param pAxisYDataLabelFunction
     */
    public void setAxisYDataLabelFunction(DataLabel pAxisYDataLabelFunction) {
        _AxisYDataLabelFunction = pAxisYDataLabelFunction;
    }

    /**
     * Add a series of data
     * @param pColor = Color of line
     * @param pLabel = Label for the legend
     * @param pData = container of X,Y data values
     */
    public void addDataSeries(int pColor, String pLabel, DataSet2D pData) {
        _Colors.add(pColor);
        _Labels.add(pLabel);
        _Data.add(pData);
    }

    /**
     * Clear the data contained
     */
    public void clear() {
        _Colors.clear();
        _Labels.clear();
        _Data.clear();
        _SelectedValue=null;
        refresh();
    }


    static class DrawChartContext  {
        Canvas canvas;
        float _originalDataLimits[];
        float dataLimits[];
        float marginLeft=10;
        float marginRight=10;
        float marginTop=10;
        float marginBottom=30;
        float chartWidth;
        float chartHeight;
        float scaleX;
        float scaleY;




        public static final int DEFAULT_GRIDLINES=5;
        public static final int DEFAULT_GRIDLINES_MAX=20;

        public static final float DATA_MARGIN=0.2f;
        int gridLines=DEFAULT_GRIDLINES;


        public DrawChartContext(Canvas pCanvas, float[] pDatalimits) {
            canvas=pCanvas;

            float vDataMarginX= pDatalimits[2]==pDatalimits[0] ? 1 : (pDatalimits[2]-pDatalimits[0])*DATA_MARGIN;
            float vDataMarginY= pDatalimits[3]==pDatalimits[1]  ? 1: (pDatalimits[3]-pDatalimits[1])*DATA_MARGIN;
            _originalDataLimits=new float[]{pDatalimits[0]-vDataMarginX,pDatalimits[1]-vDataMarginY,pDatalimits[2]+vDataMarginX,pDatalimits[3]+vDataMarginY};
            dataLimits=_originalDataLimits.clone();

            refresh();
        }

        protected void refresh() {
            chartWidth=canvas.getWidth() -marginLeft-marginRight;
            chartHeight=canvas.getHeight() -marginTop-marginBottom;
            scaleX = chartWidth / (dataLimits[2]-dataLimits[0]);
            scaleY = chartHeight / (dataLimits[3]-dataLimits[1]);
        }
        public void moveByPoints(float pX, float pY) {
            float vValueX = pX / scaleX;
            dataLimits[0] += vValueX;
            dataLimits[2] += vValueX;

            float vValueY = pY / scaleY;
            dataLimits[1] += vValueY;
            dataLimits[3] += vValueY;
        }

        public float getPointForValueX(float pValueX) {
            return marginLeft+(pValueX-dataLimits[0])*scaleX;
        }

        public float getValueForPointX(float pPointX) {
            return (pPointX-marginLeft)/scaleX+dataLimits[0];
        }

        public float getPointForValueY(float pValueY) {
            return canvas.getHeight()-marginBottom-(pValueY-dataLimits[1])*scaleY;
        }

        public float getValueForPointY(float pValueY) {
            return (canvas.getHeight()-marginBottom-pValueY)/scaleY+dataLimits[1];
        }

        public boolean isValueInRange(float pValueX, float pValueY) {
            return pValueX >= dataLimits[0] && pValueX<= dataLimits[2]
                    && pValueY >= dataLimits[1] && pValueY <= dataLimits[3];
        }


        public float pixelToValueX(float pPixel) {
            return pPixel/scaleX;
        }

        public float pixelToValueY(float pPixel) {
            return pPixel/scaleY;
        }

        float[] _DataLimitsToScale;

        float _ScalingFactor;
        float _ScalingFocusValueX;
        float _ScalingFocusValueY;
        float _ScalingGridLines;

        public void beginScale(float pScalingFocusX, float pScalingFocusY) {
            _DataLimitsToScale=_originalDataLimits.clone();
            _ScalingFactor=1f;
            _ScalingFocusValueX=getValueForPointX(pScalingFocusX);
            _ScalingFocusValueY=getValueForPointY(pScalingFocusY);
            _ScalingGridLines=DEFAULT_GRIDLINES;
        }

        public void scaleBy(float pFocusX, float pFocusY, float pFactor) {
            setScale(pFocusX,pFocusY,_ScalingFactor * pFactor);
        }

        public void setScale(float pFocusX, float pFocusY, float pFactor) {


            _ScalingFactor =pFactor;
            gridLines = ((Double)Math.ceil(_ScalingGridLines/_ScalingFactor)).intValue();
            if (gridLines> DEFAULT_GRIDLINES_MAX) {
                gridLines= DEFAULT_GRIDLINES_MAX;;
            }
            float vSizeX = (_DataLimitsToScale[2]-_DataLimitsToScale[0])/_ScalingFactor;
            float vSizeY = (_DataLimitsToScale[3]-_DataLimitsToScale[1])/_ScalingFactor;

            dataLimits[0]= _ScalingFocusValueX - vSizeX/2;
            dataLimits[1]= _ScalingFocusValueY - vSizeY/2;
            dataLimits[2]= _ScalingFocusValueX + vSizeX/2;
            dataLimits[3]= _ScalingFocusValueY + vSizeY/2;
            refresh();
        }
    }


    /**
     * Reset the canvas
     */
    public void reset() {
        _DrawChartContext=null;
        refresh();
    }

    /**
     * Ask canvas redraw
     */
    public void refresh() {
        LineChart.this.requestLayout();
        LineChart.this.invalidate();
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

    DrawChartContext _DrawChartContext;

    @Override
    protected void onDraw(Canvas pCanvas) {


        if (_Data.size()==0) {
            return;
        }

        if (_DrawChartContext==null) {
            _DrawChartContext = new DrawChartContext(pCanvas, getDataLimits());
        }

        //pCanvas.drawColor( new Random().nextInt(0xFFFFFF) |0xFF000000);


        initPaints();
        drawGrid(_DrawChartContext);
        drawAxes(_DrawChartContext);
        drawDataLines(_DrawChartContext);
        drawSelectedValue(_DrawChartContext);

    }
    Paint _PaintDataLine;
    Paint _PaintAxes;
    Paint _PaintGrid;
    Paint _PaintSelectedValue;

    private void initPaints() {

        if (_PaintDataLine ==null) {
            _PaintDataLine= new Paint();
            _PaintDataLine.setStyle(Paint.Style.FILL_AND_STROKE);
            _PaintDataLine.setStrokeWidth(4);
        }

        if (_PaintAxes ==null) {
            _PaintAxes = new Paint();
            _PaintAxes.setStyle(Paint.Style.STROKE);
            _PaintAxes.setStrokeWidth(4);
            _PaintAxes.setColor(Color.DKGRAY);
        }

        if (_PaintGrid==null) {
            _PaintGrid = new Paint();
            _PaintGrid.setStyle(Paint.Style.STROKE);
            _PaintGrid.setStrokeWidth(2);
            _PaintGrid.setColor(Color.LTGRAY);
            _PaintGrid.setTextSize(26);
        }

        if (_PaintSelectedValue==null) {
            _PaintSelectedValue = new Paint();
            _PaintSelectedValue.setStyle(Paint.Style.STROKE);
            _PaintSelectedValue.setStrokeWidth(2);
            _PaintSelectedValue.setColor(Color.BLUE);
        }
    }
    protected void drawDataLines(DrawChartContext pContext) {

        for (int vCntSeries=0 ;vCntSeries < _Data.size();vCntSeries++){
            DataSet2D vCurSeries = _Data.get(vCntSeries);

            Path  vCurPath= new Path();
            boolean vFirstPoint=true;
            for (int vCntPoints=0 ;vCntPoints < vCurSeries.count();vCntPoints++){
                float vCurX = pContext.getPointForValueX(vCurSeries.getAxisX().getItemAt(vCntPoints));
                float vCurY =  pContext.getPointForValueY(vCurSeries.getAxisY().getItemAt(vCntPoints));

/*
                if (!pContext.isValueInRange(vCurSeries.getAxisX().getItemAt(vCntPoints), vCurSeries.getAxisY().getItemAt(vCntPoints))) {
                    continue;
                }
*/
                if (vFirstPoint==false) {
                    vCurPath.lineTo(vCurX,vCurY);
                }

                vCurPath.addCircle(vCurX,vCurY,5, Path.Direction.CW);
                vCurPath.moveTo(vCurX,vCurY);
                vFirstPoint=false;
            }

            _PaintDataLine.setColor(_Colors.get(vCntSeries));
            pContext.canvas.drawPath(vCurPath,_PaintDataLine);
        }


    }


    boolean _ShowAxes=true;

    /**
     * Show axes
     * @param pShowAxes = true to show axes in the chart, otherwise false
     */
    public void setShowAxes(boolean pShowAxes) {
        _ShowAxes=pShowAxes;
        refresh();
    }
    protected void drawAxes(DrawChartContext pContext) {


        if (_ShowAxes==false) {
            return;
        }

        float vAxisX=pContext.getPointForValueY(pContext.dataLimits[1] < 0 ? 0:pContext.dataLimits[1]);
        pContext.canvas.drawLine(pContext.marginLeft,vAxisX,pContext.canvas.getWidth()-pContext.marginRight,vAxisX,_PaintAxes);

        float vAxisY=pContext.getPointForValueX(pContext.dataLimits[0] < 0 ? 0:pContext.dataLimits[0]);
        pContext.canvas.drawLine(vAxisY,pContext.marginTop,vAxisY,pContext.canvas.getHeight()-pContext.marginBottom,_PaintAxes);
    }


    boolean _ShowGrid=true;

    /**
     * Change grid visibility in the chart
     * @param pShowGrid = true to show the grid, otherwise false
     */
    public void setShowGrid(boolean pShowGrid) {
        _ShowGrid=pShowGrid;
        refresh();
    }

    protected void drawGrid(DrawChartContext pContext) {



        float vYFactor = (0f+pContext.canvas.getHeight()) / pContext.canvas.getWidth();
        int vYLines;
        int vXLines;
        if (vYFactor >= 1) {
            vXLines = pContext.gridLines;
            vYLines =  ((Double)Math.ceil(vXLines * vYFactor)).intValue();
        } else {
            vYLines =  pContext.gridLines;
            vXLines = ((Double) Math.ceil( pContext.gridLines / vYFactor)).intValue();
        }



        Rect vBounds = new Rect();

        float vXSize = (pContext.canvas.getWidth() - pContext.marginLeft-pContext.marginRight) / vXLines;
        float vAxisX=pContext.getPointForValueY(pContext.dataLimits[1] < 0 ? 0:pContext.dataLimits[1]);
        int vNextXLabel=0;


        String vPreviousLabelX="";
        for (int vCntX = 0; vCntX < vXLines; vCntX++) {

            float vCurPointX= pContext.marginLeft+vCntX*vXSize;
            float vCurValueX=pContext.getValueForPointX(vCurPointX);


            if (_ShowGrid) {
                pContext.canvas.drawLine(vCurPointX, pContext.marginTop, vCurPointX, pContext.canvas.getHeight() - pContext.marginBottom, _PaintGrid);
            }

            if (_ShowAxes && Math.ceil(vCurValueX)!=0 && vCntX > vNextXLabel) {

                String vCurText = _AxisXDataLabelFunction == null
                        ? "" + Math.ceil(vCurValueX*10)/10
                        : _AxisXDataLabelFunction.getLabelFor(vCurValueX);

                if (vCurText.equals(vPreviousLabelX)) {
                  //  continue;
                }
                vPreviousLabelX=vCurText;
                pContext.canvas.drawLine(vCurPointX,vAxisX,vCurPointX,vAxisX-10,_PaintAxes);

                _PaintGrid.getTextBounds(vCurText, 0, vCurText.length(), vBounds);

                vNextXLabel = vCntX + vBounds.width()  / ((Double)Math.ceil(vXSize)).intValue();

                pContext.canvas.drawText(vCurText, vCurPointX- vBounds.width()/2, (pContext.dataLimits[1] < 0
                        ? pContext.getPointForValueY(0) + 26 :
                        pContext.canvas.getHeight() + -pContext.marginBottom + 26), _PaintGrid);
            }
        }

        float vYSize = (pContext.canvas.getHeight() - pContext.marginTop-pContext.marginBottom) / vYLines;
        int vNextYLabel=0;

        float vAxisY=pContext.getPointForValueX(pContext.dataLimits[0] < 0 ? 0:pContext.dataLimits[0]);
        _PaintGrid.getTextBounds("Z",0,1,vBounds);

        String vPreviousLabelY="";
        for (int vCntY = vYLines-1; vCntY >-1; vCntY--) {

            float vCurPointY= pContext.marginLeft+vCntY+vYSize*vCntY;
            float vCurValueY=pContext.getValueForPointY(vCurPointY);

            if (_ShowGrid) {
                pContext.canvas.drawLine(pContext.marginLeft, vCurPointY, pContext.canvas.getWidth() - pContext.marginRight, vCurPointY, _PaintGrid);
            }

            //if (_ShowAxes && Math.ceil(vCurValueY)!=0 && vCntY > vNextYLabel) {
            if (_ShowAxes && Math.ceil(vCurValueY)!=0) {

                String vCurText=_AxisYDataLabelFunction == null
                        ? "" + Math.ceil(vCurValueY*10)/10
                        : _AxisYDataLabelFunction.getLabelFor(vCurValueY);

                if (vCurText.equals(vPreviousLabelY)) {
                    continue;
                }
                vPreviousLabelY=vCurText;

                pContext.canvas.drawLine(vAxisY,vCurPointY,vAxisY+10,vCurPointY,_PaintAxes);

                vNextYLabel = vCntY- (vBounds.height()+10) / ((Double)Math.ceil(vYSize)).intValue();

                pContext.canvas.drawText(vCurText,
                        ( pContext.dataLimits[0] < 0? pContext.getPointForValueX(0):0+pContext.marginLeft) + 14, pContext.marginTop+vCurPointY, _PaintGrid);
            }
        }

    }

    TouchEventHelper _TouchEventHelper;

    @Override
    public boolean onTouchEvent(MotionEvent pEvent) {



        if (_TouchEventHelper==null) {
            _TouchEventHelper = new TouchEventHelper(this);

            _TouchEventHelper.setContinuousMoveListener(new TouchEventHelper.MoveListener() {
                @Override
                public void onMove(float pX, float pY) {


                    if (LineChart.this._DrawChartContext !=null ){
                        LineChart.this._DrawChartContext.moveByPoints(pX*-1,pY);
                    }

                    LineChart.this.refresh();

                }
            });

            _TouchEventHelper.setTapListener(new TouchEventHelper.TapListener() {
                @Override
                public void onDoubleTap(float pX, float pY) {
                    if (LineChart.this._DrawChartContext !=null ){

                        float vOldFactor=LineChart.this._DrawChartContext._ScalingFactor;
                        LineChart.this._DrawChartContext.beginScale(pX,pY);
                        LineChart.this._DrawChartContext.setScale(pX,pY,vOldFactor == 1?4:1);
                        LineChart.this.refresh();
                    }
                }
            });
            _TouchEventHelper.setOnScaleGestureListener(new OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {

                    if (LineChart.this._DrawChartContext !=null ){
                        LineChart.this._DrawChartContext.scaleBy(detector.getFocusX(),detector.getFocusY(),
                                detector.getScaleFactor());
                    }
                    LineChart.this.refresh();
                    return true;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    if (LineChart.this._DrawChartContext !=null ){
                        LineChart.this._DrawChartContext.beginScale(detector.getFocusX(),detector.getFocusY());
                    }
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {

                }
            });

            _TouchEventHelper.setClickListener(new TouchEventHelper.ClickListener() {
                @Override
                public void onClick(float pX, float pY) {

                    clickPoint(pX,pY);
                }
            });
        }

        _TouchEventHelper.processEvent(pEvent);
        return true;

    }



    boolean _AllowSelectValue=true;

    /**
     * Choose to allow to select value by clicking on chart points
     * @param pAllowSelectValue = true to allow value selection, otherwise false
     */
    public void setAllowSelectValue(boolean pAllowSelectValue) {
        _AllowSelectValue =pAllowSelectValue;
    }

    SelectedValue _SelectedValue;

    public static final int CLICK_SIZE=50;

    protected void clickPoint(float pX, float pY) {


        if (_AllowSelectValue==false) {
            return;
        }

        if (_DrawChartContext ==null ){
            return;
        }
        float vCurValueX=_DrawChartContext.getValueForPointX(pX);
        float vCurValueY=_DrawChartContext.getValueForPointY(pY);

        _SelectedValue = getFirstValueNearThan(vCurValueX,vCurValueY,_DrawChartContext.pixelToValueX(CLICK_SIZE),_DrawChartContext.pixelToValueY(CLICK_SIZE));



        TextView vDataValueText= (TextView) MyViewUtils.getChildByTag(LineChart.this, DATALABEL_TAG);

        if (vDataValueText!=null) {

            if (_SelectedValue==null) {
                vDataValueText.setText("");
                vDataValueText.setVisibility(GONE);
            } else {
                vDataValueText.setVisibility(VISIBLE);
                vDataValueText.setText(
                        _SelectedValue.label+" "+
                        ( _AxisXDataLabelFunction == null ? "" + Math.ceil(_SelectedValue.x) : _AxisXDataLabelFunction.getLabelFor(_SelectedValue.x))
                                + ": " +
                                (_AxisYDataLabelFunction == null ? "" + Math.ceil(_SelectedValue.y) : _AxisYDataLabelFunction.getLabelFor(_SelectedValue.y))
                );
            }
        }

        refresh();

    }

    protected void drawSelectedValue(DrawChartContext pContext) {

        if (_SelectedValue==null ){
            return;
        }

        float vSelectedValueX= pContext.getPointForValueX(_SelectedValue.x);
        float vSelectedValueY= pContext.getPointForValueY(_SelectedValue.y);

        pContext.canvas.drawLine(vSelectedValueX,pContext.marginTop,vSelectedValueX,pContext.canvas.getHeight()-pContext.marginBottom,_PaintSelectedValue);
        pContext.canvas.drawLine(pContext.marginLeft,vSelectedValueY,pContext.canvas.getWidth()-pContext.marginRight,vSelectedValueY,_PaintSelectedValue);

    }

    static class SelectedValue {
        float x;
        float y;
        float distance;
        DataSet2D dataset;
        String label;
        int color;
    }

    protected SelectedValue getFirstValueNearThan(float pX, float pY, float pDistanceMaxX,float pDistanceMaxY) {

        SelectedValue vRis=null;
        for (int vCnt=0;vCnt<_Data.size();vCnt++) {
            DataSet2D vCurDataSet = _Data.get(vCnt);
            float [] vCur = vCurDataSet.getFirstValueNearThan(pX,pY,pDistanceMaxX, pDistanceMaxY);

            if (vCur==null) {
                continue;
            }

            if (vRis!=null&& vCur[2] > vRis.distance) {
                    continue;
            }
            vRis = new SelectedValue();
            vRis.x = vCur[0];
            vRis.y= vCur[1];
            vRis.distance = vCur[2];
            vRis.dataset = vCurDataSet;
            vRis.label = _Labels.get(vCnt);
            vRis.color=_Colors.get(vCnt);


        }

        return vRis;
    }
}
