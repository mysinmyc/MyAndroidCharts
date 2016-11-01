package org.mysinmyc.myandroidchartsapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import org.mysinmyc.myandroidcharts.LineChart;
import org.mysinmyc.myandroidcharts.data.DataLabel;
import org.mysinmyc.myandroidcharts.data.DataSet2D;

public class LineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);


        LineChart vLineChart = (LineChart) findViewById(R.id.lineChart);

        DataSet2D vData = new DataSet2D();

        for (float vCnt=-5;vCnt<10;vCnt++) {
            vData.add(vCnt,0f+ vCnt%2 == 0 ? vCnt:vCnt*2);
        }
        vLineChart.addDataSerie(Color.BLUE,"ciao",vData);

        DataSet2D vData2 = new DataSet2D();

        for (float vCnt=-10;vCnt<10;vCnt++) {
            //vData2.add(vCnt,0f+vCnt *vCnt);
            vData2.add(vCnt,0f+vCnt);
        }
        vLineChart.addDataSerie(Color.RED,"miao",vData2);

        vLineChart.setAxisXDataLabelFunction(new DataLabel() {
            @Override
            public String getLabelFor(float pValue) {
                return " [ valore di "+Math.round(pValue)+"] ";
            }
        });
    }


}
