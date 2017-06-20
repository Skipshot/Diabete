package com.example.fdelahaye.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.fdelahaye.myapplication.Objects.Glycaemia;
import com.example.fdelahaye.myapplication.Objects.Settings;
import com.example.fdelahaye.myapplication.Objects.Validation;

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

    private boolean isTimeOfTheDayAlreadyUsed = false;
    private boolean isUpdate = false;

    private Calendar calendar;
    private String timeOfTheDay;

    private Button btnGlucoseValidation;
    private EditText edtGlucoseCheck, edtGlucoseFood, edtComment, edtBolusCorrection;
    private TextView tvResultBolus, tvResultBolusDetail, tvResultDetailCalcul;
    private LinearLayout linlayGlucoseFood, linlayBolusCorrection, linlayRemind;
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
        edtBolusCorrection = (EditText)view.findViewById(R.id.edtBolusCorrection);
        edtComment = (EditText)view.findViewById(R.id.edtComment);
        tvResultBolus = (TextView)view.findViewById(R.id.tvResultBolus);
        tvResultBolusDetail = (TextView)view.findViewById(R.id.tvResultBolusDetail);
        tvResultDetailCalcul = (TextView)view.findViewById(R.id.tvResultDetailCalcul);
        linlayGlucoseFood = (LinearLayout)view.findViewById(R.id.linlayGlucoseFood);
        linlayBolusCorrection = (LinearLayout)view.findViewById(R.id.linlayBolusCorrection);
        linlayRemind = (LinearLayout)view.findViewById(R.id.linlayRemind);
        spinTimeOfTheDay = (Spinner)view.findViewById(R.id.spinTimeOfTheDay);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //get settings datas
        settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));

        if (settings != null && !TextUtils.isEmpty(settings.getDate())) {

            calendar = Calendar.getInstance();      //initialize calendar
            //get all glycaemia List (monthly)
            glycaemiaList = Glycaemia.getGlycaemiaList(getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1));

            setTimeOfTheDay();      //define time of the day (breakfast, lunch or dinner)

            if (!TextUtils.isEmpty(timeOfTheDay)) {

                spinTimeOfTheDay.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Glycaemia.timeOfTheDayEnum.values()));

                UpdateScreen();
                //bind TableLayout with 7 last check in the same time of the day
                BindRemind();
                //Initialize user validation
                EventsInit();

                //set spinner select listener
                spinTimeOfTheDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        timeOfTheDay = spinTimeOfTheDay.getSelectedItem().toString();
                        isUpdate = false;
                        UpdateScreen();
                        BindRemind();
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });

                //Validate button click
                ValidateButton();
            }
        }
    }



    private void setTimeOfTheDay() {
        //get Glycaemia List of today
        ArrayList<Glycaemia> todayGlycaemiaList = Glycaemia.getTodayGlycaemiaList(glycaemiaList);

        //set current time
        Date currentTime = parseTime(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

        //breakfast
        if(currentTime.after(parseTime(settings.getHourBreakfast())))    timeOfTheDay = Glycaemia.timeOfTheDayEnum.BREAKFAST.name();
        //lunch
        if(currentTime.after(parseTime(settings.getHourLunch())))        timeOfTheDay = Glycaemia.timeOfTheDayEnum.LUNCH.name();
        //dinner
        if(currentTime.after(parseTime(settings.getHourDinner())))       timeOfTheDay = Glycaemia.timeOfTheDayEnum.DINNER.name();

        //define if user already check something at this moment of the day (ex : if user have already checked his glycaemia on breakfast)
        if (todayGlycaemiaList != null && todayGlycaemiaList.size() > 0) {
            for (Glycaemia glycaemia : todayGlycaemiaList) {
                if (glycaemia.getTimeOfTheDay().equals(timeOfTheDay)) {
                    isTimeOfTheDayAlreadyUsed = true;
                    break;
                }
            }
        }

        //if timeOfTheDay is already used, we have to take the next one (ex : breakfast already used, we have to use morning
        if (isTimeOfTheDayAlreadyUsed) {
            //get "index" of timeOfTheDay and set the new timeOfTheDay : add index+1 to get the next one (ex : breakfast -> morning)
            int index =  Glycaemia.timeOfTheDayEnum.valueOf(timeOfTheDay).getPosition();
            timeOfTheDay = Glycaemia.timeOfTheDayEnum.values()[ index + 1 ].name();
        }
    }

    private void EventsInit() {
        edtGlucoseCheck.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtGlucoseCheck.addTextChangedListener(new Validation(edtGlucoseCheck) {
            @Override public void validate() {
                if(isNumber(edtGlucoseCheck, true) && isFieldsValid()) {
                    btnGlucoseValidation.setEnabled(true);
                } else btnGlucoseValidation.setEnabled(false);
            }
        });

        edtGlucoseFood.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtGlucoseFood.addTextChangedListener(new Validation(edtGlucoseFood) {
            @Override public void validate() {
                if(isNumber(edtGlucoseFood, true) && isFieldsValid()) {
                    btnGlucoseValidation.setEnabled(true);
                } else btnGlucoseValidation.setEnabled(false);
            }
        });

        edtBolusCorrection.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtBolusCorrection.addTextChangedListener(new Validation(edtBolusCorrection) {
            @Override public void validate() {
                if(isNumber(edtBolusCorrection, false) && isFieldsValid()) {
                    btnGlucoseValidation.setEnabled(true);
                } else btnGlucoseValidation.setEnabled(false);
            }
        });
    }

    private void ValidateButton() {
        btnGlucoseValidation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                short check = Short.parseShort(edtGlucoseCheck.getText().toString());
                float insulineMeal = 0.0f;
                float insulineTreat = 0.0f;
                float bolus = 0.0f;
                short food = 0;
                short goal = settings.getGoalOutMeal();
                float index = 0.0f;
                float ratio = 0.0f;
                //calculs
                if (isMeal()) {
                    food = Short.parseShort(edtGlucoseFood.getText().toString());
                    goal = settings.getGoalBeforeMeal();
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

                    //calcul
                    insulineMeal = (food * ratio) / 10;
                    insulineTreat = check < goal ? 0 : (check - goal) / (100 * index);
                    bolus = insulineTreat + insulineMeal;
                } else {
                    bolus = !TextUtils.isEmpty(edtBolusCorrection.getText().toString()) ? Float.parseFloat(edtBolusCorrection.getText().toString()) : 0.0f;
                }

                //set new glycaemia
                Glycaemia newGlycaemia = new Glycaemia(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
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
                );

                //update/create Glycaemia object and rewrite file
                if(isUpdate) {
                    //find the glycamia to update
                    for(Glycaemia g : glycaemiaList) {
                        if(g.getDateCreate() != null &&
                                parseDate(g.getDateCreate()).compareTo(parseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) == 0 &&  //compare with current date
                                g.getTimeOfTheDay().equals(timeOfTheDay)) { //compare timeOfTheDay

                            newGlycaemia.setDateCreate(g.getDateCreate());      //we don't want to change dateCreate
                            glycaemiaList.set(glycaemiaList.indexOf(g), newGlycaemia);      //update
                        }
                    }
                } else {
                    glycaemiaList.add(newGlycaemia);    //add new glycaemia
                }
                //write to file
                Glycaemia.WriteGlycaemiaFile(glycaemiaList, getActivity(), String.format("%s%s-%s.json", getString(R.string.GlycaemiaJsonFilename), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1));

                UpdateScreen();
                BindRemind();

                //display text after UpdateScreen
                if(isMeal()) {
                    tvResultBolus.setText(String.format("Insuline Bolus : %.2f.", bolus));
                    tvResultBolusDetail.setText(String.format("Dont %.2f pour l'alimentaire et %.2f pour le soin.", insulineMeal, insulineTreat));
                    tvResultDetailCalcul.setText(String.format("Calcul alimentaire : ( %.2f * %d ) / 10 = %.2f \r\nCalcul soin : ( %d - %d ) / ( 100 * %.2f ) = %.2f ", ratio, food, insulineMeal, check, goal, index, insulineTreat));
                } else {
                    tvResultBolus.setText(R.string.strNouveauControleEnregistre);
                }
                //TODO : update mode.
                //TODO : add ID ?
            }
        });
    }

    private void UpdateScreen() {
        //------ reset output
        edtGlucoseCheck.setText(null);
        edtGlucoseFood.setText(null);
        edtBolusCorrection.setText(null);
        edtComment.setText(null);
        tvResultBolus.setText(null);
        tvResultBolusDetail.setText(null);
        tvResultDetailCalcul.setText(null);

        //------ Update elements
        //have to show/hide some elements when it is morning, afternoon, sleep or night
        if (!isMeal()) {
            linlayGlucoseFood.setVisibility(View.INVISIBLE);
            linlayBolusCorrection.setVisibility(View.VISIBLE);
        } else {
            linlayGlucoseFood.setVisibility(View.VISIBLE);
            linlayBolusCorrection.setVisibility(View.INVISIBLE);
        }

        //update spinner
        spinTimeOfTheDay.setSelection(Glycaemia.timeOfTheDayEnum.valueOf(timeOfTheDay).getPosition());
        //if we have update spinner on time of the day who is a meal
        if(isMeal()) {
            //get Glycaemia List of today
            ArrayList<Glycaemia> todayGlycaemiaList = Glycaemia.getTodayGlycaemiaList(glycaemiaList);

            if (todayGlycaemiaList != null && todayGlycaemiaList.size() > 0) {
                //check all glycaemia's check of the day
                for (Glycaemia glycaemia : todayGlycaemiaList) {
                    //find THE glycaemia check if the time of the day
                    if (glycaemia.getTimeOfTheDay().equals(timeOfTheDay)) {
                        isTimeOfTheDayAlreadyUsed = true;
                        //set text
                        if (glycaemia.getGlucoseCheck() > 0 && glycaemia.getGlucoseFood() > 0) {
                            isUpdate = true;
                            edtGlucoseCheck.setText(String.valueOf(glycaemia.getGlucoseCheck()));
                            edtGlucoseFood.setText(String.valueOf(glycaemia.getGlucoseFood()));
                            edtComment.setText(glycaemia.getComment());
                        }
                        break;
                    }
                }
            }
        }
    }

    private void BindRemind() {
        ArrayList<Glycaemia> remingGlycaemiaList = Glycaemia.getRemindGlycaemiaList(glycaemiaList, timeOfTheDay);

        //reset
        linlayRemind.removeAllViews();

        if(remingGlycaemiaList != null && remingGlycaemiaList.size() > 0) {

            //horizontal divider
            View dividerHeader = new View(getActivity());
            LinearLayout.LayoutParams lpDivider = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);
            dividerHeader.setLayoutParams(lpDivider);
            dividerHeader.setBackgroundColor(Color.BLACK);

            //header
            LinearLayout llHead = new LinearLayout(getActivity());
            llHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llHead.setOrientation(LinearLayout.HORIZONTAL);
            llHead.setWeightSum(15);

            TextView tvHead1 = AddRow("Date");
            tvHead1.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead1);

            TextView tvHead2 = AddRow("Contrôle\r\nglycémique");
            tvHead2.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead2);

            TextView tvHead3 = AddRow("Glucide\r\nrepas");
            tvHead3.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead3);

            TextView tvHead4 = AddRow("Insuline\r\nalimentation");
            tvHead4.setLayoutParams(new LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead4);

            TextView tvHead5 = AddRow("Insuline\r\nsoin");
            tvHead5.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead5);

            TextView tvHead6 = AddRow("Bolus");
            tvHead6.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
            llHead.addView(tvHead6);

            linlayRemind.addView(llHead);
            linlayRemind.addView(dividerHeader);

            //datas
            for(Glycaemia g : remingGlycaemiaList) {

                //lines
                LinearLayout ligne = new LinearLayout(getActivity());
                ligne.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ligne.setOrientation(LinearLayout.HORIZONTAL);
                //ligne.setTag(g.getDateCreate());    //set identifier

                //add Click listener
                ligne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO : add Button Click on LinearLayout from Remind, and make update mode
                        //v.getId();
                        //Date gDate = parseDate(getView().getTag().toString().replace("\r\n"," "));     //get identifier.
                    }
                });


                TextView tv1 = AddRow( g.getDateCreate().replace(" ", "\r\n") );                                        //Date
                tv1.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv1);

                TextView tv2 = AddRow( g.getGlucoseCheck() > 0 ? String.valueOf( g.getGlucoseCheck() ) : "-" );         //Glucose Check
                tv2.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv2);

                TextView tv3 = AddRow( g.getGlucoseFood() > 0 ? String.valueOf( g.getGlucoseFood() ) : "-" );           //Glucose Food
                tv3.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv3);

                TextView tv4 = AddRow( g.getInsulineMeal() > 0 ? String.format( "%.2f", g.getInsulineMeal() ) : "-" );  //Insuline Meal
                tv4.setLayoutParams(new LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv4);

                TextView tv5 = AddRow( g.getInsulineTreat() > 0 ? String.format( "%.2f", g.getInsulineTreat() ) : "-" );//Insuline Treat
                tv5.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv5);

                TextView tv6 = AddRow( g.getBolus() > 0 ? String.format( "%.2f", g.getBolus() ) : "-" );                //Bolus
                tv6.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
                ligne.addView(tv6);

                //horizontal divider
                View divider = new View(getActivity());
                divider.setLayoutParams(lpDivider);
                divider.setBackgroundColor(Color.BLACK);

                linlayRemind.addView(ligne);
                linlayRemind.addView(divider);
            }
        }
    }


    private TextView AddRow(String text) {
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private Date parseTime(String date) {
        try {
            return new SimpleDateFormat("HH:mm").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private boolean isMeal() {
        if(!TextUtils.isEmpty(this.timeOfTheDay))
            return this.timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.BREAKFAST.name()) ||
                    this.timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.LUNCH.name()) ||
                    this.timeOfTheDay.equals(Glycaemia.timeOfTheDayEnum.DINNER.name());
        else
            return false;
    }

    private boolean isFieldsValid() {
        if(isMeal())
            return !TextUtils.isEmpty(edtGlucoseCheck.getText().toString()) &&
                    !TextUtils.isEmpty(edtGlucoseFood.getText().toString());
        else
            return !TextUtils.isEmpty(edtGlucoseCheck.getText().toString());
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
