package org.mysinmyc.myandroidcharts.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ace on 03/11/2016.
 */

public class MyViewUtils {

    public static View getChildByTag(ViewGroup pParent, String pTag){

        for (int vCnt=0;vCnt<pParent.getChildCount();vCnt++) {
            View vCurView = pParent.getChildAt(vCnt);

            if (vCurView instanceof ViewGroup) {

                View vFound = getChildByTag((ViewGroup) vCurView, pTag);

                if (vFound != null) {
                    return vFound;
                }
            } else {

                if (pTag.equals(vCurView.getTag())) {
                    return vCurView;
                }
            }
        }

        return null;
    }
}
