package com.example.fdelahaye.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.example.fdelahaye.myapplication.Objects.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment {

    private EditText edtBreakfastRatio;
    private EditText edtBreakfastIndex;
    private EditText edtLunchRatio;
    private EditText edtLunchIndex;
    private EditText edtDinnerRatio;
    private EditText edtDinnerIndex;
    private EditText edtGoalOutMeal;
    private EditText edtGoalBeforeMeal;
    private ToggleButton tbNotifHbA1c;
    private EditText edtHourBreakfast;
    private EditText edtHourLunch;
    private EditText edtHourDinner;
    private Spinner spinDelaisNotifHbA1c;
    private ArrayAdapter<String> spinAdapter;
    private LinearLayout linlayDelaisNotifHbA1c;

    private View view;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Fragment Settings");
        }

        linlayDelaisNotifHbA1c = (LinearLayout) view.findViewById(R.id.linlayDelaisNotifHbA1c);
        tbNotifHbA1c = (ToggleButton) view.findViewById(R.id.tbNotifHbA1c);
        if(tbNotifHbA1c.isChecked()){
            linlayDelaisNotifHbA1c.setVisibility(View.VISIBLE);
        } else {
            linlayDelaisNotifHbA1c.setVisibility(View.INVISIBLE);
        }

        //initialize all events.
        EventsInit();

        GetDatas();


        return view;
    }

    private void EventsInit() {
        //this TextWatcher create Listener for all EditText TextChangedListener
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //get Settings object from Json file
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                //update Settings object
                settings = updateSettings(settings);
                //overwrite file with new datas
                settings.setJsonSettings(settings, getActivity(), getString(R.string.SettingsJsonFilename));
            }
        };


        tbNotifHbA1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                settings.setNotificationHbA1c(tbNotifHbA1c.isChecked());
                settings.setJsonSettings(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                if(tbNotifHbA1c.isChecked()){
                    linlayDelaisNotifHbA1c.setVisibility(View.VISIBLE);
                } else {
                    linlayDelaisNotifHbA1c.setVisibility(View.INVISIBLE);
                }
            }
        });

        String [] delaisNotifHbA1c = {"1 jours", "2 jours", "3 jours", "4 jours", "5 jours", "6 jours", "7 jours", "8 jours", "9 jours", "10 jours", "11 jours", "12 jours", "13 jours", "14 jours", "15 jours", "16 jours", "17 jours", "18 jours", "19 jours", "20 jours", "21 jours", "22 jours", "23 jours", "24 jours", "25 jours", "26 jours", "27 jours", "28 jours", "29 jours", "30 jours", "1 mois", "1,5 mois", "2 mois", "3 mois", "4 mois"};
        spinDelaisNotifHbA1c = (Spinner)view.findViewById(R.id.spinDelaisNotifHbA1c);
        spinAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, delaisNotifHbA1c);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinDelaisNotifHbA1c.setAdapter(spinAdapter);
        spinDelaisNotifHbA1c.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                settings.setDelaisNotifHbA1c(spinDelaisNotifHbA1c.getSelectedItem().toString());
                settings.setJsonSettings(settings, getActivity(), getString(R.string.SettingsJsonFilename));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        edtBreakfastRatio = (EditText) view.findViewById(R.id.edtBreakfastRatio);
        edtBreakfastRatio.addTextChangedListener(textWatcher);

        edtBreakfastIndex = (EditText) view.findViewById(R.id.edtBreakfastIndex);
        edtBreakfastIndex.addTextChangedListener(textWatcher);

        edtLunchRatio = (EditText) view.findViewById(R.id.edtLunchRatio);
        edtLunchRatio.addTextChangedListener(textWatcher);

        edtLunchIndex = (EditText) view.findViewById(R.id.edtLunchIndex);
        edtLunchIndex.addTextChangedListener(textWatcher);

        edtDinnerRatio = (EditText) view.findViewById(R.id.edtDinnerRatio);
        edtDinnerRatio.addTextChangedListener(textWatcher);

        edtDinnerIndex = (EditText) view.findViewById(R.id.edtDinnerIndex);
        edtDinnerIndex.addTextChangedListener(textWatcher);

        edtGoalOutMeal = (EditText) view.findViewById(R.id.edtGoalOutMeal);
        edtGoalOutMeal.addTextChangedListener(textWatcher);

        edtGoalBeforeMeal = (EditText) view.findViewById(R.id.edtGoalBeforeMeal);
        edtGoalBeforeMeal.addTextChangedListener(textWatcher);

        edtHourBreakfast = (EditText) view.findViewById(R.id.edtHourBreakfast);
        edtHourBreakfast.addTextChangedListener(textWatcher);

        edtHourLunch = (EditText) view.findViewById(R.id.edtHourLunch);
        edtHourLunch.addTextChangedListener(textWatcher);

        edtHourDinner = (EditText) view.findViewById(R.id.edtHourDinner);
        edtHourDinner.addTextChangedListener(textWatcher);
    }

    private void GetDatas() {
        Settings s = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
        edtBreakfastRatio.setText(String.valueOf(s.getBreakfastRatio()));
        edtBreakfastIndex.setText(String.valueOf(s.getBreakfastIndex()));
        edtLunchRatio.setText(String.valueOf(s.getLunchRatio()));
        edtLunchIndex.setText(String.valueOf(s.getLunchIndex()));
        edtDinnerRatio.setText(String.valueOf(s.getDinnerRatio()));
        edtDinnerIndex.setText(String.valueOf(s.getDinnerIndex()));
        edtGoalBeforeMeal.setText(String.valueOf(s.getGoalBeforeMeal()));
        edtGoalOutMeal.setText(String.valueOf(s.getGoalOutMeal()));
        edtHourBreakfast.setText(String.valueOf(s.getHourBreakfast()));
        edtHourLunch.setText(String.valueOf(s.getHourLunch()));
        edtHourDinner.setText(String.valueOf(s.getHourDinner()));
        tbNotifHbA1c.setChecked(s.isNotificationHbA1c());
        spinDelaisNotifHbA1c.setSelection(spinAdapter.getPosition(s.getDelaisNotifHbA1c()));
    }

    private Settings updateSettings(Settings s) {
        try {
            s.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //s.setDate(new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(Calendar.getInstance()));
            if( !TextUtils.isEmpty(edtBreakfastRatio.getText()) ) {
                s.setBreakfastRatio(Float.parseFloat(edtBreakfastRatio.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtBreakfastIndex.getText()) ) {
                s.setBreakfastIndex(Float.parseFloat(edtBreakfastIndex.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtLunchRatio.getText()) ) {
                s.setLunchRatio(Float.parseFloat(edtLunchRatio.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtLunchIndex.getText()) ) {
                s.setLunchIndex(Float.parseFloat(edtLunchIndex.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtDinnerRatio.getText()) ) {
                s.setDinnerRatio(Float.parseFloat(edtDinnerRatio.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtDinnerIndex.getText()) ) {
                s.setDinnerIndex(Float.parseFloat(edtDinnerIndex.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtGoalBeforeMeal.getText()) ) {
                s.setGoalBeforeMeal(Short.parseShort(edtGoalBeforeMeal.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtGoalOutMeal.getText()) ) {
                s.setGoalOutMeal(Short.parseShort(edtGoalOutMeal.getText().toString()));
            }
            if( !TextUtils.isEmpty(edtHourBreakfast.getText()) ) {
                s.setHourBreakfast(edtHourBreakfast.getText().toString());
            }
            if( !TextUtils.isEmpty(edtHourLunch.getText()) ) {
                s.setHourLunch(edtHourLunch.getText().toString());
            }
            if( !TextUtils.isEmpty(edtHourDinner.getText()) ) {
                s.setHourDinner(edtHourDinner.getText().toString());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
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
        void onFragmentInteraction(String title);
    }
}
