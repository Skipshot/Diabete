package com.example.fdelahaye.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fdelahaye.myapplication.Objects.Glycaemia;
import com.example.fdelahaye.myapplication.Objects.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.BubbleChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GlycaemiaGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GlycaemiaGraphFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TextView tvDateStart, tvDateStop;

    private ArrayList<Glycaemia> glycaemiaList;

    private DatePickerDialog.OnDateSetListener mDataSetListenerStart, mDataSetListenerStop;
    private Calendar cal;
    private Date dStart, dStop;
    int year, month, day;

    private BubbleChartView chart;
    private BubbleChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;


    public GlycaemiaGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //hide all elements in OptionsMenu
        menu.findItem(R.id.action_pdf).setVisible(false);
        menu.findItem(R.id.action_excel).setVisible(false);
        menu.findItem(R.id.action_csv).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pdf : {
                return true;
            }
            case R.id.action_excel : {
                return true;
            }
            case R.id.action_csv : {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //hide OptionsMenu if no item is show
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_glycaemia_graph, container, false);
        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Graphique des glycémies");
        }

        tvDateStart = (TextView) view.findViewById(R.id.tvDateStart);
        tvDateStop = (TextView) view.findViewById(R.id.tvDateStop);
        chart = (BubbleChartView) view.findViewById(R.id.chart);
        //chart.setOnValueTouchListener(new ValueTouchListener());
        //add listener
        chart.setOnValueTouchListener(new BubbleChartOnValueSelectListener() {
            @Override public void onValueSelected(int bubbleIndex, BubbleValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }
            @Override public void onValueDeselected() {}
        });

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;    //month start with 0
        day = cal.get(Calendar.DAY_OF_MONTH);

        BindDate();

        glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), year, month));

        if (glycaemiaList != null && glycaemiaList.size() > 0) {
            generateGraph();
        }


        return view;
    }

    private void BindDate() {

        dStart = Utils.parseDate(year + "-" + (month <= 10 ? "0" + (month-1) : (month-1)) + "-" + day); //display from a month ago ...
        dStop = Utils.parseDate(year + "-" + (month <= 9 ? "0" + month : month) + "-" + day);           //to today

        tvDateStart.setText("Du : " + android.text.format.DateFormat.format("yyyy-MM-dd", dStart));
        tvDateStop.setText(" Au : " + android.text.format.DateFormat.format("yyyy-MM-dd", dStop));

        mDataSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dStart = Utils.parseDate(year + "-" + month + "-" + dayOfMonth);
                tvDateStart.setText("Du : " + android.text.format.DateFormat.format("yyyy-MM-dd", dStart));

                generateGraph();
            }
        };

        mDataSetListenerStop = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dStop = Utils.parseDate(year + "-" + month + "-" + dayOfMonth);
                tvDateStop.setText(" Au : " + android.text.format.DateFormat.format("yyyy-MM-dd", dStop));

                generateGraph();
            }
        };

        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cStart = Calendar.getInstance();
                cStart.setTime(dStart);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, mDataSetListenerStart, cStart.get(Calendar.YEAR), cStart.get(Calendar.MONTH),cStart.get(Calendar.DAY_OF_MONTH));  //current date
                dialog.show();
            }
        });
        tvDateStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cStop = Calendar.getInstance();
                cStop.setTime(dStop);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, mDataSetListenerStop, cStop.get(Calendar.YEAR), cStop.get(Calendar.MONTH),cStop.get(Calendar.DAY_OF_MONTH));   //current date a month ago
                dialog.show();
            }
        });


    }

    private void generateGraph() {
        List<BubbleValue> values = new ArrayList<BubbleValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        glycaemiaList = Glycaemia.getDateStartStopGlycaemiaList(getActivity(), getString(R.string.GlycaemiaJsonFilename), dStart, dStop);
        Calendar currentCalendar = Calendar.getInstance();

        if ( glycaemiaList != null && glycaemiaList.size() > 0 ) {
            //must have one bubble with a big "Z" value to have small circle after that
            currentCalendar.setTime(Utils.parseDateTime(glycaemiaList.get(0).getDateCreate()));
            BubbleValue value = new BubbleValue(currentCalendar.getTimeInMillis(), glycaemiaList.get(0).getGlucoseCheck(), (float) 1000);
            value.setColor(Color.TRANSPARENT);  //color transparent to hide him
            value.setShape(ValueShape.CIRCLE);
            values.add(value);

            int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            for (Glycaemia g : glycaemiaList) {
                currentCalendar.setTime(Utils.parseDateTime(g.getDateCreate()));

                value = new BubbleValue(currentCalendar.getTimeInMillis(), g.getGlucoseCheck(), (float) 1);

                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.BREAKFAST.name())
                    value.setColor(R.color.colorGraphBreakfast);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.MORNING.name())
                    value.setColor(R.color.colorGraphMorning);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.LUNCH.name())
                    value.setColor(R.color.colorGraphLunch);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.AFTERNOON.name())
                    value.setColor(R.color.colorGraphAfternoon);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.DINNER.name())
                    value.setColor(R.color.colorGraphDinner);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.SLEEP.name())
                    value.setColor(R.color.colorGraphSleep);
                if (g.getTimeOfTheDay() == Glycaemia.timeOfTheDayEnum.NIGHT.name())
                    value.setColor(R.color.colorGraphNight);

                value.setColor(ChartUtils.pickColor());
                value.setShape(ValueShape.CIRCLE);
                values.add(value);

                //add axis X values
                AxisValue axisValue = new AxisValue(currentCalendar.getTimeInMillis());
                if (currentCalendar.get(Calendar.DAY_OF_MONTH) != date)
                    axisValue.setLabel(g.getDateCreate().substring(0, g.getDateCreate().indexOf(" ")));
                else
                    axisValue.setLabel("");
                axisValues.add(axisValue);

                date = currentCalendar.get(Calendar.DAY_OF_MONTH);
            }

            data = new BubbleChartData(values);
            data.setHasLabels(hasLabels);
            data.setHasLabelsOnlyForSelected(hasLabelForSelected);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Date");
                    axisY.setName("Glycémie");
                    axisX.setValues(axisValues);
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            chart.setBubbleChartData(data);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String string);
    }
}
