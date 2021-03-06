package org.mysinmyc.myandroidcharts.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Host a series of data
 * Created by ace on 30/10/2016.
 */
public class DataSet implements  Iterable<Float> {

    List<Float> _data = new ArrayList<Float>();

    int _Count=0;

    Float _Min=Float.MAX_VALUE;
    Float _Max=Float.MIN_VALUE;
    Float _Sum=0f;



    /**
     * Add a value to the series
     * @param pValue = value to add1
     */
    public void add(Float pValue) {
        _data.add(pValue);
        _Count++;
        _Sum+=pValue;
        if (pValue<_Min) {
            _Min=pValue;
        }
        if (pValue>_Max) {
            _Max=pValue;
        }
    }

    /**
     * Count the number of items
     * @return number of items
     */
    public int count() {
        return _Count;
    }

    /**
     * Max value
     * @return max value
     */
    public Float max() {
        return _Max;
    }

    /**
     * Min value
     * @return min value
     */
    public Float min() {
        return _Min;
    }

    /**
     * Sum of data contained
     * @return  sum of data contained
     */
    public Float sum() {
        return _Sum;
    }


    public Float getItemAt(int pItemIndex) {
        return _data.get(pItemIndex);
    }


    @Override
    public Iterator<Float> iterator() {
        return _data.iterator();
    }


}
