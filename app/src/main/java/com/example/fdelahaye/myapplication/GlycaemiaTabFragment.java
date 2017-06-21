package com.example.fdelahaye.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fdelahaye.myapplication.Objects.Glycaemia;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GlycaemiaTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GlycaemiaTabFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private LinearLayout content;
    private TextView tvDateStart, tvDateStop;

    private ArrayList<Glycaemia> glycaemiaList;

    private DatePickerDialog.OnDateSetListener mDataSetListenerStart, mDataSetListenerStop;
    private Calendar cal;
    private Date dStart, dStop;
    int year, month, day;

    public GlycaemiaTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_glycaemia_tab, container, false);
        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Tableau Glycémique");
        }

        content = (LinearLayout) view.findViewById(R.id.content);
        tvDateStart = (TextView) view.findViewById(R.id.tvDateStart);
        tvDateStop = (TextView) view.findViewById(R.id.tvDateStop);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;    //month start with 0
        day = cal.get(Calendar.DAY_OF_MONTH);

        BindDate();

        glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), year, month));

        if(glycaemiaList != null && glycaemiaList.size() > 0) {
            BindContent();
        }
    }

    private void BindDate() {

        dStart = parseDate(year + "/" + (month-1) + "/" + day);
        dStop = parseDate(year + "/" + month + "/" + day);

        tvDateStart.setText("Du : " + android.text.format.DateFormat.format("yyyy/MM/dd", dStart));
        tvDateStop.setText(" Au : " + android.text.format.DateFormat.format("yyyy/MM/dd", dStop));

        mDataSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dStart = parseDate(year + "/" + month + "/" + dayOfMonth);
                tvDateStart.setText("Du : " + android.text.format.DateFormat.format("yyyy/MM/dd", dStart));

                glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), year, month));
                if(glycaemiaList != null && glycaemiaList.size() > 0) {
                    BindContent();
                }
            }
        };

        mDataSetListenerStop = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dStop = parseDate(year + "/" + month + "/" + dayOfMonth);
                tvDateStop.setText(" Au : " + android.text.format.DateFormat.format("yyyy/MM/dd", dStop));

                glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), year, month));
                if(glycaemiaList != null && glycaemiaList.size() > 0) {
                    BindContent();
                }
            }
        };

        tvDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, mDataSetListenerStart, year,month-2,day);  //current date
                dialog.show();
            }
        });
        tvDateStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, mDataSetListenerStop, year,month-1,day);   //current date a month ago
                dialog.show();
            }
        });


    }

    private void BindContent() {
        glycaemiaList = Glycaemia.getDateStartStopGlycaemiaList(getActivity(), getString(R.string.GlycaemiaJsonFilename), dStart, dStop);

        content.removeAllViews();

        //horizontal divider
        View dividerHeader = new View(getActivity());
        LinearLayout.LayoutParams lpDivider = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
        dividerHeader.setLayoutParams(lpDivider);
        dividerHeader.setBackgroundColor(Color.BLACK);

        //header
        LinearLayout llHead = new LinearLayout(getActivity());
        llHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llHead.setOrientation(LinearLayout.HORIZONTAL);
        llHead.setGravity(Gravity.CENTER_VERTICAL);
        llHead.setWeightSum(15);

        //header initialize
        ArrayList<String> titreList = new ArrayList<>(Arrays.asList("Date", "Zone", "Moment\r\nde la\r\njournée", "Contrôle\r\nglycémique", "Glucide\r\nrepas", "Insuline\r\nalimentation", "Insuline\r\nsoin", "Bolus", "Ratio", "Index\r\nsoin", "Objectif", "Commentaire"));
        ArrayList<Integer> widthList = new ArrayList<>(Arrays.asList(250,100,200,200,150,220,150,120,120,120,200,500));

        for (String t : titreList) {
            TextView tv = AddCell(t);
            tv.setLayoutParams(new LinearLayout.LayoutParams(widthList.get(titreList.indexOf(t)), LinearLayout.LayoutParams.WRAP_CONTENT));

            if(t.equals("Commentaire")) tv.setGravity(Gravity.LEFT);

            llHead.addView(tv);
        }

        content.addView(llHead);
        content.addView(dividerHeader);

        if(glycaemiaList != null && glycaemiaList.size() > 0) {
            //initialize previousDate
            Date previousDate = new Date(0);
            Calendar previousCalendar = Calendar.getInstance();
            previousCalendar.setTime(previousDate);

            int indexColor = 0;

            for (Glycaemia g : glycaemiaList) {
                //lines
                LinearLayout ligne = new LinearLayout(getActivity());
                ligne.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ligne.setOrientation(LinearLayout.HORIZONTAL);
                //ligne.setTag(g.getDateCreate());    //set identifier

                //current date
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTime(parseDateTime( g.getDateCreate()));
                Date currentDate = parseDate(currentCalendar.get(Calendar.YEAR) + "/" + currentCalendar.get(Calendar.MONTH) + "/" + currentCalendar.get(Calendar.DAY_OF_MONTH));

                //manage color.
                if (!previousDate.equals(currentDate))    indexColor ++;
                if (indexColor % 2 == 1)                  ligne.setBackgroundColor( Color.LTGRAY );

                //add Click listener
                /*ligne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO : add Button Click on LinearLayout from Remind, and make update mode
                        //v.getId();
                        //Date gDate = parseDate(getView().getTag().toString().replace("\r\n"," "));     //get identifier.
                    }
                });*/


                //content
                ArrayList<String> valueList = new ArrayList<>(Arrays.asList(
                        g.getDateCreate().replace(" ", "\r\n"),
                        g.getInjectionBody(),
                        g.getTimeOfTheDay().replace("BREAKFAST", "Petit déjeuner").replace("MORNING", "Matinée").replace("LUNCH", "Déjeuner").replace("AFTERNOON", "Après-midi").replace("DINNER", "Diner").replace("SLEEP", "Coucher").replace("NIGHT", "Nuit"),
                        g.getGlucoseCheck() > 0 ? String.valueOf(g.getGlucoseCheck()) : "-",
                        g.getGlucoseFood() > 0 ? String.valueOf(g.getGlucoseFood()) : "-",
                        g.getInsulineMeal() > 0 ? String.format("%.2f", g.getInsulineMeal()) : "-",
                        g.getInsulineTreat() > 0 ? String.format("%.2f", g.getInsulineTreat()) : "-",
                        g.getBolus() > 0 ? String.format("%.2f", g.getBolus()) : "-",
                        g.getRatio() > 0 ? String.format("%.2f", g.getRatio()) : "-",
                        g.getIndexTreat() > 0 ? String.format("%.2f", g.getIndexTreat()) : "-",
                        g.getObjective() > 0 ? String.format("%.0f", g.getObjective()) : "-",
                        g.getComment()));

                //add all content
                for (String t : titreList) {
                    int index = titreList.indexOf(t);   //get index

                    TextView tv = AddCell( valueList.get(index) );
                    tv.setLayoutParams(new LinearLayout.LayoutParams( widthList.get( index ), LinearLayout.LayoutParams.WRAP_CONTENT) );

                    if(t.equals("Commentaire")) tv.setGravity(Gravity.LEFT);

                    ligne.addView(tv);
                }

                //horizontal divider
                View divider = new View(getActivity());
                divider.setLayoutParams(lpDivider);
                divider.setBackgroundColor(Color.BLACK);

                content.addView(ligne);
                content.addView(divider);

                //set previousDate
                previousCalendar.setTime(parseDateTime(g.getDateCreate()));
                previousDate = parseDate(previousCalendar.get(Calendar.YEAR) + "/" + previousCalendar.get(Calendar.MONTH) + "/" + previousCalendar.get(Calendar.DAY_OF_MONTH));

            }
        }
    }


    //region Utils
    private TextView AddCell(String text) {
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private Date parseDateTime(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
    //endregion

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
