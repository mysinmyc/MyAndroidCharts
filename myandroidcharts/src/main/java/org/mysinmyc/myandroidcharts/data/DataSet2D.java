package org.mysinmyc.myandroidcharts.data;

/**
 * This class is a container of points 2D
 * Created by ace on 31/10/2016.
 */
public class DataSet2D {

    DataSet _AxisX = new DataSet();
    DataSet _AxisY = new DataSet();


    /**
     * Add a point
     * @param pX = x coordinates
     * @param pY = y coordinats
     */
    public void add(Float pX, Float pY) {
        _AxisX.add(pX);
        _AxisY.add(pY);
    }

    /**
     * AXIS X
     * @return container of axis X values
     */
    public DataSet getAxisX() {
        return _AxisX;
    }

    /**
     * AXIS Y
     * @return container of axis Y values
     */
    public DataSet getAxisY() {
        return _AxisY;
    }

    /**
     * return an array of coordinates
     * @return [x,y, x+1, y+1, ...,...,xn,yn]
     */
    public float[] getValues() {

        float[] vRis =new float[_AxisX.count()*2];
        for (int vCur =0; vCur < _AxisX.count();vCur++) {

            vRis[vCur] = _AxisX.getItemAt(vCur);
            vRis[vCur+1] =_AxisY.getItemAt(vCur);
        }

        return  vRis;
    }

    /**
     * Convert values in coordinates (to draw it in a canvas)
     * @param pWidth = canvas width
     * @param pHeight = canvas height
     * @param pMinX = X min value shown
     * @param pMinY = Y min value shown
     * @param pScaleX = X scaling factor
     * @param pScaleY = Y scaling factor
     * @return converted [x,y, x+1, y+1, ...,...,xn,yn]
     */
    public float[] getChartPoints(int pWidth, int pHeight,float pMinX, float pMinY, float pScaleX,float pScaleY) {

        float[] vRis =new float[_AxisX.count()*2];
        for (int vCur =0; vCur < _AxisX.count();vCur++) {

            vRis[vCur] = (_AxisX.getItemAt(vCur)-pMinX) *pScaleX ;
            vRis[vCur+1] = pHeight- (_AxisY.getItemAt(vCur)-pMinY)*pScaleY;
        }

        return  vRis;
    }

    /**
     * Count the number of values
     * @return number of values contained
     */
    public int count() {
        return getAxisX().count();
    }


    /**
     * Search the first value near the given
     * @param pX = X value to search for
     * @param pY = Y value to search for
     * @param pDistanceMaxX = max distance of X value
     * @param pDistanceMaxY = max distance of Y value
     * @return [x,y] of the first value in the range specified  pX-pDistanceMaxX -> point -> pX+pDistanceX, pY-pDistanceMaxY -> point -> pY+pDistanceY. NULL if not found
     */
    public float[] getFirstValueNearThan(float pX, float pY, float pDistanceMaxX,float  pDistanceMaxY) {

        float[] vRis=null;

        for (int vCnt=0;vCnt< _AxisX.count();vCnt++){
            float vCurX = _AxisX.getItemAt(vCnt);
            if (Math.abs(vCurX-pX) >pDistanceMaxX) {
                continue;
            }

            float vCurY = _AxisY.getItemAt(vCnt);
            if (Math.abs(vCurY-pY) >pDistanceMaxY) {
                continue;
            }

            vRis= new float[]{vCurX,vCurY};
            break;
        }

        return vRis;
    }
}
