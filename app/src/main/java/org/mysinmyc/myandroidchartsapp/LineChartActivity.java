package org.mysinmyc.myandroidchartsapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.mysinmyc.myandroidcharts.LineChart;
import org.mysinmyc.myandroidcharts.data.DataLabel;
import org.mysinmyc.myandroidcharts.data.DataSet2D;

public class LineChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_line_chart);


        final LineChart vLineChart = (LineChart) findViewById(R.id.lineChart);


        CheckBox vChkShowAxes = (CheckBox) findViewById(R.id.chkShowAxes);
        vChkShowAxes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vLineChart.setShowAxes(isChecked);
            }
        });



        CheckBox vChkShowGrid = (CheckBox) findViewById(R.id.chkShowGrid);
        vChkShowGrid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vLineChart.setShowGrid(isChecked);
            }
        });


        DataSet2D vData = new DataSet2D();

        for (float vCnt=-10;vCnt<10;vCnt+=1) {
            vData.add(vCnt, vCnt*vCnt*vCnt);
        }
        vLineChart.addDataSeries(Color.BLUE,"ciao",vData);

        DataSet2D vData2 = new DataSet2D();

        for (float vCnt=-20;vCnt<20;vCnt+=1) {
            vData2.add(vCnt,0f+vCnt *vCnt);
            //vData2.add(vCnt,10f+vCnt);
        }
        vLineChart.addDataSeries(Color.RED,"miao",vData2);

        vLineChart.setAxisXDataLabelFunction(new DataLabel() {
            @Override
            public String getLabelFor(float pValue) {
                return " [ value of "+Math.round(pValue)+"] ";
            }
        });

        Button vBtnReset = (Button) findViewById(R.id.btnReset);
        vBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vLineChart.reset();
            }
        });

        Button vBtnBack= (Button) findViewById(R.id.btnBack);
        vBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LineChartActivity.this.finish();
            }
        });

    }


}
