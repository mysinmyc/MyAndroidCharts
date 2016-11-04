package org.mysinmyc.myandroidcharts.data;

/**
 * Created by ace on 31/10/2016.
 */
public interface DataLabel {

    /**
     * Return the label for a specific value
     * @param pValue = pValue
     * @return Label
     */
    public String getLabelFor(float pValue);
}
