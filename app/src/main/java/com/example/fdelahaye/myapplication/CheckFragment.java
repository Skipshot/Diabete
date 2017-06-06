package com.example.fdelahaye.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fdelahaye.myapplication.Objects.Glycaemia;
import com.example.fdelahaye.myapplication.Objects.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CheckFragment extends Fragment {

    private Settings settings;
    private ArrayList<Glycaemia> glycaemiaList;

    boolean isTimeOfTheDayAlreadyUsed = false;

    private String currentDate;
    private String timeOfTheDay;

    private Button btnGlucoseValidation;
    private EditText edtGlucoseCheck, edtGlucoseFood, edtComment;
    private TextView tvResultBolus, tvResultBolusDetail, tvResultDetailCalcul;
    private LinearLayout linlayGlucoseFood;
    private Spinner spinTimeOfTheDay;

    private OnFragmentInteractionListener mListener;

    public CheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_check, container, false);

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Fragment Check");
        }

        btnGlucoseValidation = (Button)view.findViewById(R.id.btnGlucoseValidation);
        edtGlucoseCheck = (EditText)view.findViewById(R.id.edtGlucoseCheck);
        edtGlucoseFood = (EditText)view.findViewById(R.id.edtGlucoseFood);
        edtComment = (EditText)view.findViewById(R.id.edtComment);
        tvResultBolus = (TextView)view.findViewById(R.id.tvResultBolus);
        tvResultBolusDetail = (TextView)view.findViewById(R.id.tvResultBolusDetail);
        tvResultDetailCalcul = (TextView)view.findViewById(R.id.tvResultDetailCalcul);
        linlayGlucoseFood = (LinearLayout)view.findViewById(R.id.linlayGlucoseFood);
        spinTimeOfTheDay = (Spinner)view.findViewById(R.id.spinTimeOfTheDay);

        //get settings datas
        settings = new Settings(getActivity(), getString(R.string.SettingsJsonFilename));
        //get glycaemia datas
        glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), getString(R.string.GlycaemiaJsonFilename));
        //define current date
        currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //define time of the day (breakfast, lunch or dinner)
        setTimeOfTheDay(view);

        if (!TextUtils.isEmpty(timeOfTheDay)) {
            //spinTimeOfTheDay
            spinTimeOfTheDay.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Glycaemia.timeOfTheDayEnum.values()));
            spinTimeOfTheDay.setSelection(Glycaemia.timeOfTheDayEnum.valueOf(timeOfTheDay).getPosition());
            spinTimeOfTheDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    timeOfTheDay = spinTimeOfTheDay.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            //have to hide some elements when it is morning, afternoon, sleep or night
            if (!isMeal(timeOfTheDay)) {
                linlayGlucoseFood.setVisibility(View.INVISIBLE);
            }
            //Validate button click
            ValidateButton(view);

            //TODO : display recap last 7 check.
        }

        return view;
    }

    private void setTimeOfTheDay(View view) {
        //bind data with enum timeOfTheDay


        ArrayList<Glycaemia> todayGlycaemiaList = Glycaemia.getTodayGlycaemiaList(glycaemiaList);

        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        Date currentDate = parseDate(currentHour + ":" + currentMinute);
        Date breakfastDate = parseDate(settings.getHourBreakfast());
        Date lunchDate = parseDate(settings.getHourLunch());
        Date dinnerDate = parseDate(settings.getHourDinner());

        //define timeOfTheDay
        timeOfTheDay = Glycaemia.timeOfTheDayEnum.SLEEP.name();
        if(currentDate.after(breakfastDate)) {
            timeOfTheDay = Glycaemia.timeOfTheDayEnum.BREAKFAST.name();
        }
        if(currentDate.after(lunchDate)) {
            timeOfTheDay = Glycaemia.timeOfTheDayEnum.LUNCH.name();
        }
        if(currentDate.after(dinnerDate)) {
            timeOfTheDay = Glycaemia.timeOfTheDayEnum.DINNER.name();
        }

        //define if user already check something at this time of the day (ex : if user have already checked his glycaemia on breakfast)
        if (todayGlycaemiaList != null) {
            for (Glycaemia glycaemia : todayGlycaemiaList) {
                if (glycaemia.getTimeOfTheDay().equals(timeOfTheDay)) {
                    isTimeOfTheDayAlreadyUsed = true;
                    break;
                }
            }
        }

        //if timeOfTheDay is already used, we have to take the next one (ex : breakfast already used, we have to use morning
        if (isTimeOfTheDayAlreadyUsed) {
            //get index of timeOfTheDay ...
            int position =  Glycaemia.timeOfTheDayEnum.valueOf(timeOfTheDay).getPosition();
            //set new timeOfTheDay, and add +1 to get the next one
            timeOfTheDay = Glycaemia.timeOfTheDayEnum.values()[ position + 1 ].name();
        }
    }

    private void ValidateButton(View view) {
        btnGlucoseValidation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                short check = Short.parseShort(edtGlucoseCheck.getText().toString());
                short food = Short.parseShort(edtGlucoseFood.getText().toString());
                short goal = settings.getGoalBeforeMeal();
                float index = settings.getBreakfastIndex();
                float ratio = settings.getBreakfastRatio();
                if (timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.BREAKFAST.name())) {
                    index = settings.getBreakfastIndex();
                    ratio = settings.getBreakfastRatio();
                }
                if (timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.LUNCH.name())) {
                    index = settings.getLunchIndex();
                    ratio = settings.getLunchRatio();
                }
                if (timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.DINNER.name())) {
                    index = settings.getDinnerIndex();
                    ratio = settings.getDinnerRatio();
                }

                float insulineMeal = 0.0f;
                float insulineTreat = 0.0f;
                float bolus = 0.0f;
                //calculs
                if (!isMeal(timeOfTheDay)) {
                    insulineMeal = (food * ratio) / 10;
                    insulineTreat = check < goal ? 0 : (check - goal) / (100 * index);
                    bolus = insulineTreat + insulineMeal;

                    //display text
                    tvResultBolus.setText(String.format("Insuline Bolus : %.2f.", bolus));
                    tvResultBolusDetail.setText(String.format("Dont %.2f pour l'alimentaire et %.2f pour le soin.", insulineMeal, insulineTreat));
                    tvResultDetailCalcul.setText(String.format("Calcul alimentaire : ( %.2f * %d ) / 10 = %.2f \r\nCalcul soin : ( %d - %d ) / ( 100 * %.2f ) = %.2f ", ratio, food, insulineMeal, check, goal, index, insulineTreat));
                }
                //create and add new Glycaemia object
                glycaemiaList.add(new Glycaemia(currentDate,
                        spinTimeOfTheDay.getSelectedItem().toString(),
                        check,
                        food,
                        insulineMeal,
                        insulineTreat,
                        bolus,
                        ratio,
                        index,
                        goal,
                        null,
                        "ventre",
                        edtComment.getText().toString()
                ));
                //write to file
                Glycaemia.WriteGlycaemiaFile(glycaemiaList, getActivity(), getString(R.string.GlycaemiaJsonFilename));

                //TODO : update mode.
            }
        });
    }



    private Date parseDate(String date) {

        try {
            return new SimpleDateFormat("HH:mm").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private boolean isMeal(String timeOfTheDay) {
        return timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.BREAKFAST.name()) || timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.LUNCH.name()) || timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.DINNER.name());
    }



    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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
