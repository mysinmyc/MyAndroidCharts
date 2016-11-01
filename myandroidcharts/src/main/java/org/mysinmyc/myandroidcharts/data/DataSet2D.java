package org.mysinmyc.myandroidcharts.data;

/**
 * Created by ace on 31/10/2016.
 */

public class DataSet2D {

    DataSet _AxisX = new DataSet();
    DataSet _AxisY = new DataSet();


    public void add(Float pX, Float pY) {
        _AxisX.add(pX);
        _AxisY.add(pY);
    }

    public DataSet getAxisX() {
        return _AxisX;
    }

    public DataSet getAxisY() {
        return _AxisY;
    }

    public float[] getValues() {

        float[] vRis =new float[_AxisX.count()*2];
        for (int vCur =0; vCur < _AxisX.count();vCur++) {

            vRis[vCur] = _AxisX.getItemAt(vCur);
            vRis[vCur+1] =_AxisY.getItemAt(vCur);
        }

        return  vRis;
    }

    public float[] getChartPoints(int pWidth, int pHeight,float pMinX, float pMinY, float pScaleX,float pScaleY) {

        float[] vRis =new float[_AxisX.count()*2];
        for (int vCur =0; vCur < _AxisX.count();vCur++) {

            vRis[vCur] = (_AxisX.getItemAt(vCur)-pMinX) *pScaleX ;
            vRis[vCur+1] = pHeight- (_AxisY.getItemAt(vCur)-pMinY)*pScaleY;
        }

        return  vRis;
    }


    public int count() {
        return getAxisX().count();
    }
}
