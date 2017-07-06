package com.example.fdelahaye.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fdelahaye.myapplication.Objects.HbA1c;
import com.example.fdelahaye.myapplication.Objects.Utils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
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
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HbA1cFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HbA1cFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText edtHbA1cCheck;
    private Button btnHbA1cValidation;
    private TextView tvResult, tvDateStart, tvDateStop;

    private ArrayList<HbA1c> hbA1cList;

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

    public HbA1cFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //hide all elements in OptionsMenu
        menu.findItem(R.id.action_pdf).setVisible(false);
        menu.findItem(R.id.action_excel).setVisible(false);
        menu.findItem(R.id.action_csv).setVisible(true);
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
                exportCSV();
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
        View view = inflater.inflate(R.layout.fragment_hba1c, container, false);

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Contrôle HbA1c");
        }

        edtHbA1cCheck = (EditText) view.findViewById(R.id.edtHbA1cCheck);
        btnHbA1cValidation = (Button) view.findViewById(R.id.btnHbA1cValidation);
        tvResult = (TextView) view.findViewById(R.id.tvResult);

        tvDateStart = (TextView) view.findViewById(R.id.tvDateStart);
        tvDateStop = (TextView) view.findViewById(R.id.tvDateStop);
        chart = (BubbleChartView) view.findViewById(R.id.chart);

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

        ValidateButton();

        BindDate();

        generateGraph();

        return view;
    }

    private void ValidateButton() {
        btnHbA1cValidation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                HbA1c hbA1c = new HbA1c(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Float.parseFloat(edtHbA1cCheck.getText().toString()));
                hbA1c.set(getActivity(), getString(R.string.HbA1cJsonFilename));

                tvResult.setText("Enregistré !");
                edtHbA1cCheck.setText(null);

                generateGraph();
            }
        });
    }

    private void BindDate() {

        dStart = Utils.parseDate((year-1) + "-" + (month <= 9 ? "0" + month : month) + "-" + day); //display from a year ago ...
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
        hbA1cList = HbA1c.getDateStartStopHbA1cList(getActivity(), getString(R.string.HbA1cJsonFilename), dStart, dStop);   //get list from date
        Calendar currentCalendar = Calendar.getInstance();

        if ( hbA1cList != null && hbA1cList.size() > 0 ) {
            //must have one bubble with a big "Z" value to have small circle after that
            currentCalendar.setTime(Utils.parseDateTime(hbA1cList.get(0).getDate()));
            BubbleValue value = new BubbleValue(currentCalendar.getTimeInMillis(), hbA1cList.get(0).getControl(), (float) 1000);
            value.setColor(Color.TRANSPARENT);  //color transparent to hide him
            value.setShape(ValueShape.CIRCLE);
            values.add(value);

            int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            for (HbA1c h : hbA1cList) {
                currentCalendar.setTime(Utils.parseDateTime(h.getDate()));

                value = new BubbleValue(currentCalendar.getTimeInMillis(), h.getControl(), (float) 1);
                value.setColor(R.color.colorPrimary);
                value.setShape(ValueShape.CIRCLE);
                values.add(value);

                //add axis X values
                AxisValue axisValue = new AxisValue(currentCalendar.getTimeInMillis());
                if (currentCalendar.get(Calendar.DAY_OF_MONTH) != date)
                    axisValue.setLabel(h.getDate().substring(0, h.getDate().indexOf(" ")));
                else
                    axisValue.setLabel("");
                axisValues.add(axisValue);

                date = currentCalendar.get(Calendar.DAY_OF_MONTH);
            }
        }

        data = new BubbleChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Date");
                axisY.setName("Contrôle");
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

    private void exportPdf() {
        /*chart.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(chart.getDrawingCache());
        chart.setDrawingCacheEnabled(false);*/

        /*try {
            Document document = new Document();

            File path = new File( Environment.getExternalStorageDirectory(), "Diabete" );

            if ( !path.exists() ){ path.mkdir(); }
            File file = new File(path, "sample1.pdf");

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);
            document.add(myImg);

            document.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void exportCSV() {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(dStart);
        Calendar cStop = Calendar.getInstance();
        cStop.setTime(dStop);
        String filename = "hba1c"+
                cStart.get(Calendar.YEAR)+cStart.get(Calendar.MONTH)+cStart.get(Calendar.DAY_OF_MONTH) + "-" +
                cStop.get(Calendar.YEAR)+cStop.get(Calendar.MONTH)+cStop.get(Calendar.DAY_OF_MONTH) +".csv";

        ///storage/sdcard/Documents/sample1.pdf
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filename);

        //check if directory exist, and create him if not.
        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).exists()) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs();
        }

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            //header
            myOutWriter.append("\"Date\";\"Controle\"");
            myOutWriter.append("\n");
            for (HbA1c h : hbA1cList) {
                //content
                myOutWriter.append("\"" + h.getDate().replace("\"","'") + "\";\"" + h.getControl() + "\"");
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();

            //display alert message
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Export CSV");
            builder.setMessage("Le fichier "+ filename + " à bien été créée dans le dossier Documents de votre carte SD");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {}
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
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
