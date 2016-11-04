# MyAndroidCharts
Charts for android


# LineChart

It's a basic line chart graph. Allow scrolling, zoom and value selection

The underline object is a relative layout so it can contains children

In the initial implementation there is no databinding, data are statically put once

##Usage

The following example configure a LineChart at the inizialization of an activity
LineChart has been defined in the activity layout with id *@+id/lineChart*

````java
    ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ...


        //find the linechart view
        final LineChart vLineChart = (LineChart) findViewById(R.id.lineChart);

        //Declare the data serire
        DataSet2D vData = new DataSet2D();

        //Populate data serie with dummy data (x^3)
        for (float vCnt=-10;vCnt<10;vCnt+=1) {
                vData.add(vCnt, vCnt*vCnt*vCnt);
        }

        //Add data series to chart with color blue and label ciao
        vLineChart.addDataSeries(Color.BLUE,"ciao",vData);

        //Set a custom label function for X
        vLineChart.setAxisXDataLabelFunction(new DataLabel() {
            @Override
            public String getLabelFor(float pValue) {
                return " [ value of "+Math.round(pValue)+"] ";
            }
        });

        ...
        }

    ...
````