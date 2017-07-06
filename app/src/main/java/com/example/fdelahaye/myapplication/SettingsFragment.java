package com.example.fdelahaye.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.fdelahaye.myapplication.Objects.Alarm;
import com.example.fdelahaye.myapplication.Objects.Settings;
import com.example.fdelahaye.myapplication.Objects.Validation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment {

    private EditText edtBreakfastRatio, edtBreakfastIndex;
    private EditText edtLunchRatio, edtLunchIndex;
    private EditText edtDinnerRatio, edtDinnerIndex;
    private EditText edtGoalOutMeal, edtGoalBeforeMeal;
    private ToggleButton tbNotifHbA1c;
    private EditText edtHourBreakfast, edtHourLunch, edtHourDinner;
    private Spinner spinDelaisNotifHbA1c, spinChangePlaceInjection;
    private ArrayAdapter<String> spinAdapterHba1c, spinAdapterPlaceInjection;
    private LinearLayout linlayDelaisNotifHbA1c;

    private Settings settings;

    private View view;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //hide OptionsMenu if no item is show
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Paramètres");
        }

        //get Settings object from Json file
        settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));

        DisplayActionBar();

        spinDelaisNotifHbA1c = (Spinner) view.findViewById(R.id.spinDelaisNotifHbA1c);
        spinChangePlaceInjection = (Spinner) view.findViewById(R.id.spinChangePlaceInjection);
        linlayDelaisNotifHbA1c = (LinearLayout) view.findViewById(R.id.linlayDelaisNotifHbA1c);
        tbNotifHbA1c = (ToggleButton) view.findViewById(R.id.tbNotifHbA1c);
        edtBreakfastRatio = (EditText) view.findViewById(R.id.edtBreakfastRatio);
        edtBreakfastIndex = (EditText) view.findViewById(R.id.edtBreakfastIndex);
        edtLunchRatio = (EditText) view.findViewById(R.id.edtLunchRatio);
        edtLunchIndex = (EditText) view.findViewById(R.id.edtLunchIndex);
        edtDinnerRatio = (EditText) view.findViewById(R.id.edtDinnerRatio);
        edtDinnerIndex = (EditText) view.findViewById(R.id.edtDinnerIndex);
        edtGoalOutMeal = (EditText) view.findViewById(R.id.edtGoalOutMeal);
        edtGoalBeforeMeal = (EditText) view.findViewById(R.id.edtGoalBeforeMeal);
        edtHourBreakfast = (EditText) view.findViewById(R.id.edtHourBreakfast);
        edtHourLunch = (EditText) view.findViewById(R.id.edtHourLunch);
        edtHourDinner = (EditText) view.findViewById(R.id.edtHourDinner);

        String [] delaisNotifHbA1c = {"1 jours", "2 jours", "3 jours", "4 jours", "5 jours", "6 jours", "7 jours", "8 jours", "9 jours", "10 jours", "11 jours", "12 jours", "13 jours", "14 jours", "15 jours", "16 jours", "17 jours", "18 jours", "19 jours", "20 jours", "21 jours", "22 jours", "23 jours", "24 jours", "25 jours", "26 jours", "27 jours", "28 jours", "29 jours", "30 jours", "1 mois", "1,5 mois", "2 mois", "3 mois", "4 mois"};
        spinAdapterHba1c = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, delaisNotifHbA1c);
        spinAdapterHba1c.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinDelaisNotifHbA1c.setAdapter(spinAdapterHba1c);

        String [] changePlaceInjection = {"1 jours", "2 jours", "3 jours", "4 jours", "5 jours", "6 jours", "7 jours"};
        spinAdapterPlaceInjection = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, changePlaceInjection);
        spinAdapterPlaceInjection.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinChangePlaceInjection.setAdapter(spinAdapterPlaceInjection);

        //set text with all datas from Settings
        BindSettingsDatas();

        return view;
    }

    //start events on onResume event, else, in onCreateView, all textchangelistener are called when fragment load.
    @Override
    public void onResume() {
        super.onResume();

        //initialize all events.
        EventsInit();

        if(tbNotifHbA1c.isChecked()){
            linlayDelaisNotifHbA1c.setVisibility(View.VISIBLE);
        } else {
            linlayDelaisNotifHbA1c.setVisibility(View.INVISIBLE);
        }
    }

    private void EventsInit() {
        // ------- Notification HbA1c -------
        tbNotifHbA1c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                settings.setNotificationHbA1c(tbNotifHbA1c.isChecked());
                settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));

                if(tbNotifHbA1c.isChecked()){
                    linlayDelaisNotifHbA1c.setVisibility(View.VISIBLE);
                } else {
                    linlayDelaisNotifHbA1c.setVisibility(View.INVISIBLE);
                }

                createAlarmFromDelaisNotifHbA1c();
            }
        });
        // ------ Spinner DelaisNotifHbA1c ------
        spinDelaisNotifHbA1c.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                settings.setDelaisNotifHbA1c(spinDelaisNotifHbA1c.getSelectedItem().toString());
                settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));

                createAlarmFromDelaisNotifHbA1c();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //------- change place injection
        spinChangePlaceInjection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings settings = Settings.getJsonSettings(getActivity(), getString(R.string.SettingsJsonFilename));
                settings.setDelaisChangePlaceInjection(spinChangePlaceInjection.getSelectedItem().toString());
                settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ------ Text Change ------
        edtBreakfastRatio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBreakfastRatio.addTextChangedListener(new Validation(edtBreakfastRatio) {
            @Override public void validate() {
                if(isDecimalNumber(edtBreakfastRatio, true)){
                    settings.setBreakfastRatio(Float.parseFloat(edtBreakfastRatio.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtBreakfastIndex.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtBreakfastIndex.addTextChangedListener(new Validation(edtBreakfastIndex) {
            @Override public void validate() {
                if(isDecimalNumber(edtBreakfastIndex, true)){
                    settings.setBreakfastIndex(Float.parseFloat(edtBreakfastIndex.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtLunchRatio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtLunchRatio.addTextChangedListener(new Validation(edtLunchRatio) {
            @Override public void validate() {
                if(isDecimalNumber(edtLunchRatio, true)){
                    settings.setLunchRatio(Float.parseFloat(edtLunchRatio.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtLunchIndex.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtLunchIndex.addTextChangedListener(new Validation(edtLunchIndex) {
            @Override public void validate() {
                if(isDecimalNumber(edtLunchIndex, true)){
                    settings.setLunchIndex(Float.parseFloat(edtLunchIndex.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtDinnerRatio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDinnerRatio.addTextChangedListener(new Validation(edtDinnerRatio) {
            @Override public void validate() {
                if(isDecimalNumber(edtDinnerRatio, true)){
                    settings.setDinnerRatio(Float.parseFloat(edtDinnerRatio.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtDinnerIndex.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtDinnerIndex.addTextChangedListener(new Validation(edtDinnerIndex) {
            @Override public void validate() {
                if(isDecimalNumber(edtDinnerIndex, true)){
                    settings.setDinnerIndex(Float.parseFloat(edtDinnerIndex.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtGoalOutMeal.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtGoalOutMeal.addTextChangedListener(new Validation(edtGoalOutMeal) {
            @Override public void validate() {
                if(isNumber(edtGoalOutMeal, true)){
                    settings.setGoalOutMeal(Short.parseShort(edtGoalOutMeal.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtGoalBeforeMeal.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtGoalBeforeMeal.addTextChangedListener(new Validation(edtGoalBeforeMeal) {
            @Override public void validate() {
                if(isNumber(edtGoalBeforeMeal, true)){
                    settings.setGoalBeforeMeal(Short.parseShort(edtGoalBeforeMeal.getText().toString()));
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtHourBreakfast.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        edtHourBreakfast.addTextChangedListener(new Validation(edtHourBreakfast) {
            @Override public void validate() {
                if(isTime(edtHourBreakfast, true)) {
                    settings.setHourBreakfast(edtHourBreakfast.getText().toString());
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtHourLunch.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        edtHourLunch.addTextChangedListener(new Validation(edtHourLunch) {
            @Override public void validate() {
                if(isTime(edtHourLunch, true)) {
                    settings.setHourLunch(edtHourLunch.getText().toString());
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });

        edtHourDinner.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        edtHourDinner.addTextChangedListener(new Validation(edtHourDinner) {
            @Override public void validate() {
                if(isTime(edtHourDinner, true)) {
                    settings.setHourDinner(edtHourDinner.getText().toString());
                    settings.update(settings, getActivity(), getString(R.string.SettingsJsonFilename));
                    DisplayActionBar();
                } else ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        });
    }

    private void BindSettingsDatas() {
        if(settings != null) {
            edtBreakfastRatio.setText(settings.getBreakfastRatio() != 0.0f ? String.valueOf(settings.getBreakfastRatio()) : null);
            edtBreakfastIndex.setText(settings.getBreakfastIndex() != 0.0f ? String.valueOf(settings.getBreakfastIndex()) : null);
            edtLunchRatio.setText(settings.getLunchRatio() != 0.0f ? String.valueOf(settings.getLunchRatio()) : null);
            edtLunchIndex.setText(settings.getLunchIndex() != 0.0f ? String.valueOf(settings.getLunchIndex()) : null);
            edtDinnerRatio.setText(settings.getDinnerRatio() != 0.0f ? String.valueOf(settings.getDinnerRatio()) : null);
            edtDinnerIndex.setText(settings.getDinnerIndex() != 0.0f ? String.valueOf(settings.getDinnerIndex()) : null);
            edtGoalBeforeMeal.setText(settings.getGoalBeforeMeal() != 0 ? String.valueOf(settings.getGoalBeforeMeal()) : null);
            edtGoalOutMeal.setText(settings.getGoalOutMeal() != 0 ? String.valueOf(settings.getGoalOutMeal()) : null);
            edtHourBreakfast.setText(settings.getHourBreakfast() != null ? String.valueOf(settings.getHourBreakfast()) : null);
            edtHourLunch.setText(settings.getHourLunch() != null ? String.valueOf(settings.getHourLunch()) : null);
            edtHourDinner.setText(settings.getHourDinner() != null ? String.valueOf(settings.getHourDinner()) : null);
            tbNotifHbA1c.setChecked(settings.isNotificationHbA1c());
            spinDelaisNotifHbA1c.setSelection(settings.getDelaisNotifHbA1c() != null ? spinAdapterHba1c.getPosition(settings.getDelaisNotifHbA1c()) : 0);
            spinChangePlaceInjection.setSelection(settings.getDelaisChangePlaceInjection() != null ? spinAdapterPlaceInjection.getPosition(settings.getDelaisChangePlaceInjection()) : 0);
        }
    }

    private void DisplayActionBar() {
        if(settings.isComplete()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    private void createAlarmFromDelaisNotifHbA1c() {
        String delaisNotif = spinDelaisNotifHbA1c.getSelectedItem().toString();

        if(tbNotifHbA1c.isChecked()) {
            int numberDelaisNotif = Integer.parseInt(delaisNotif.substring(0, delaisNotif.indexOf(" ")));
            boolean isDay = delaisNotif.contains("jours");

            //set date
            Calendar notifTime = Calendar.getInstance();
            if (isDay)
                notifTime.set(Calendar.DAY_OF_MONTH, notifTime.get(Calendar.DAY_OF_MONTH) + numberDelaisNotif);
            else
                notifTime.set(Calendar.MONTH, notifTime.get(Calendar.MONTH) + numberDelaisNotif);
            notifTime.set(Calendar.HOUR_OF_DAY, 00);
            notifTime.set(Calendar.MINUTE, 00);

            //start alarm
            startAlarm(notifTime);
        }
    }

    private void startAlarm(Calendar calendar) {
        Intent myIntent = new Intent(getActivity(), Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); //System.currentTimeMillis()+3000
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
