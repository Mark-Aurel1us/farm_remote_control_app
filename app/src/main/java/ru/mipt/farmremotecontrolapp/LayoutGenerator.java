package ru.mipt.farmremotecontrolapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Date;

public class LayoutGenerator {

    public static class GeneratorConfiguration {
       LayoutGenerator.GeneratorConfigurationEntry[] entries;
        public GeneratorConfiguration(GeneratorConfigurationEntry[] generatorConfigurationEntries){
            this.entries = generatorConfigurationEntries;
        }
    }

    public static class GeneratorConfigurationEntry{
        public FIELD_TYPES fieldType;
        public String fieldName;
        public String fieldNameRu;
        public int min = Integer.MIN_VALUE;
        public int max = Integer.MAX_VALUE;
        //public String defaultString = null;
        //public int defaultInt;
        public GeneratorConfigurationEntry[] children = null;
        //public GeneratorConfiguration child = null;
    }

    enum FIELD_TYPES {
        //NUMBER,
        NUMBER_RUNNER,
        TIME,
        TITLE,
        DATE,
        ENCLOSING,
        //TEXT,
        //BUTTON
    }

    public static final String TAG = "LayoutGenerator";


    public abstract class InputField {
        GeneratorConfigurationEntry entry;
        String fieldName;
        LayoutGenerator layoutGenerator;
        Context context;

        public InputField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry){
            this.entry = entry;
            this.context = layoutGenerator.context;
            this.fieldName = entry.fieldName;
            this.layoutGenerator = layoutGenerator;
        }

        public  abstract void checkValidity();

        public abstract void putValueToJSON(JSONObject jsonObject);
        //jsonObject.put(fieldName, ... );

        public abstract void appendView(LinearLayout linearLayout);
        //linearLayout.addView(view);

        public abstract void setJSON(JSONObject jsonObject);

    }

    public class NumberRunnerField extends InputField{
        int number;
        TextView tw;
        SeekBar sb;
        EditText et;

        public NumberRunnerField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry) {
            super(layoutGenerator, entry);

        }

        public void checkValidity(){
            if(number < this.entry.min){this.number = this.entry.min;}
            if(number > this.entry.max){this.number = this.entry.max;}
        }



        @Override
        public void putValueToJSON(JSONObject jsonObject) {
            //TODO
            try {
                jsonObject.put(fieldName, number);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        @Override
        public void appendView(LinearLayout linearLayout){
            LinearLayout horisontalLinearLayout = new LinearLayout(layoutGenerator.context);
            horisontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            horisontalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            tw = new TextView(layoutGenerator.context);
            sb = new SeekBar(layoutGenerator.context);
            et = new EditText(layoutGenerator.context);


            tw.setText(fieldName);

            sb.setProgress(number);
            et.setText("" + number);

            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                EditText editText = et;
                LayoutGenerator.NumberRunnerField numberInputField = NumberRunnerField.this;

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    editText.setText(String.valueOf(i));
                    numberInputField.number = i;
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                SeekBar seekBar = sb;
                LayoutGenerator.NumberRunnerField numberInputField = NumberRunnerField.this;

                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    numberInputField.number = (int) Integer.valueOf(textView.getText().toString());
                    numberInputField.checkValidity();
                    seekBar.setProgress((int) Integer.valueOf(textView.getText().toString()));
                    et.setText(String.valueOf(numberInputField.number));
                    return false;
                }
            });

            horisontalLinearLayout.addView(tw);
            horisontalLinearLayout.addView(sb);
            horisontalLinearLayout.addView(et);

            linearLayout.addView(horisontalLinearLayout);
        }

        @Override
        public void setJSON(JSONObject jsonObject) {
            try {
                this.number = jsonObject.getInt(fieldName);
                this.sb.setProgress(number);
                this.et.setText(String.valueOf(number));
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public class TimeField extends InputField{
        TimePicker timePicker;//TODO:remove
        TimePickerDialog timePickerDialog;
        String time;
        TextView tw;
        Button bt;

        public TimeField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry) {
            super(layoutGenerator, entry);
        }


        @Override
        public void checkValidity() {

        }

        @Override
        public void putValueToJSON(JSONObject jsonObject) {
            try {
                jsonObject.put(fieldName, time);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        @Override
        public void appendView(LinearLayout linearLayout) {//TODO
            TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    TimeField.this.time = (hourOfDay >= 10 ? "" : "0") + hourOfDay + ":" + (minute >= 10 ? "" : "0") + minute;
                }
            };
            timePickerDialog = new TimePickerDialog(context, onTimeSetListener, 0, 0, true);

            LinearLayout horisontalLinearLayout = new LinearLayout(context);
            horisontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            horisontalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            tw = new TextView(context);
            bt = new Button(context);

            tw.setText(fieldName);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timePickerDialog.show();
                }
            });
            horisontalLinearLayout.addView(tw);
            horisontalLinearLayout.addView(bt);

            linearLayout.addView(horisontalLinearLayout);
            /*
            timePicker = new TimePicker(context);

            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    TimeField.this.time = (hourOfDay >= 10 ? "" : "0") + hourOfDay + ":" + (minute >= 10 ? "" : "0") + minute;
                }
            });
            linearLayout.addView(timePicker);*/
        }

        @Override
        public void setJSON(JSONObject jsonObject) {
            try {
                this.time = jsonObject.getString(fieldName);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
            SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
            try {
                Date date = format.parse(time);
                this.timePicker.setHour(date.getHours());
                this.timePicker.setMinute(date.getMinutes());
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public class TitleField extends InputField{

        public TitleField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry) {
            super(layoutGenerator, entry);
        }

        @Override
        public void checkValidity() {
        }

        @Override
        public void putValueToJSON(JSONObject jsonObject) {
        }

        @Override
        public void appendView(LinearLayout linearLayout) {
            TextView textView = new TextView(context);
            textView.setText(entry.fieldNameRu);
            linearLayout.addView(textView);
        }

        @Override
        public void setJSON(JSONObject jsonObject) {
        }
    }

    public class DateField extends InputField{
        DatePickerDialog datePickerDialog;

        long epochDay;

        public DateField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry) {
            super(layoutGenerator, entry);
        }


        @Override
        public void checkValidity() {
            if(epochDay < 0){epochDay = LocalDate.now().toEpochDay();}
        }

        @Override
        public void putValueToJSON(JSONObject jsonObject) {
            try {
                jsonObject.put(fieldName, epochDay);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        @Override
        public void appendView(LinearLayout linearLayout) {
            datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    LocalDate localDate = LocalDate.of(year, month+1, dayOfMonth);
                    LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.now());
                    DateField.this.epochDay = localDateTime.toEpochSecond(ZoneOffset.of("+06:00"));
                }
            });
            Button dateButton = new Button(context);
            dateButton.setText(this.entry.fieldNameRu);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
            linearLayout.addView(dateButton);

        }

        @Override
        public void setJSON(JSONObject jsonObject) {
        }
    }

    public class EnclosingField extends InputField{
        InputField[] children;

        public EnclosingField(LayoutGenerator layoutGenerator, GeneratorConfigurationEntry entry) {
            super(layoutGenerator, entry);
            this.children = toInputFields(entry.children, linearLayout, layoutGenerator);
        }

        @Override
        public void checkValidity() {
            for (int i = 0; i < children.length; i++){
                children[i].checkValidity();
            }
        }

        @Override
        public void putValueToJSON(JSONObject jsonObject) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                for (int i = 0; i < children.length; i++) {
                    children[i].putValueToJSON(jsonObject1);
                }
                jsonObject.put(fieldName, jsonObject1);
            }catch (JSONException e){
                Log.d(TAG, e.getMessage());
            }
        }

        @Override
        public void appendView(LinearLayout linearLayout) {
            for(int i = 0; i < this.children.length; i++){
                this.children[i].appendView(linearLayout);
            }
        }

        @Override
        public void setJSON(JSONObject jsonObject) {
            try {
                JSONObject jsonObject1 = jsonObject.getJSONObject(fieldName);
                for (int i = 0; i < children.length; i++) {
                    children[i].putValueToJSON(jsonObject1);
                }
            }catch (JSONException e){
                Log.d(TAG, e.getMessage());
            }
        }
    }


    static GeneratorConfigurationEntry numberRunnerEntryFabric(String fieldName, String fieldNameRu, int min, int max){//TODO
        GeneratorConfigurationEntry entry = new GeneratorConfigurationEntry();
        entry.fieldName = fieldName;
        entry.fieldNameRu = fieldNameRu;
        entry.fieldType = FIELD_TYPES.NUMBER_RUNNER;
        entry.min = min;
        entry.max = max;
        return entry;
    }

    static GeneratorConfigurationEntry timeEntryFabric(String fieldName, String fieldNameRu){//TODO
        GeneratorConfigurationEntry entry = new GeneratorConfigurationEntry();
        entry.fieldName = fieldName;
        entry.fieldNameRu = fieldNameRu;
        entry.fieldType = FIELD_TYPES.TIME;
        return entry;
    }

    static GeneratorConfigurationEntry titleEntryFabric(String fieldNameRu){
        GeneratorConfigurationEntry entry = new GeneratorConfigurationEntry();
        entry.fieldNameRu = fieldNameRu;
        entry.fieldType = FIELD_TYPES.TITLE;
        return entry;
    }

    static GeneratorConfigurationEntry dateEntryFabric(String fieldName, String fieldNameRu){
        GeneratorConfigurationEntry entry = new GeneratorConfigurationEntry();
        entry.fieldName = fieldName;
        entry.fieldNameRu = fieldNameRu;
        entry.fieldType = FIELD_TYPES.DATE;
        return entry;
    }

    static GeneratorConfigurationEntry enclosingEntryFabric(String fieldName, GeneratorConfigurationEntry[] children){
        GeneratorConfigurationEntry entry = new GeneratorConfigurationEntry();
        entry.fieldName = fieldName;
        entry.fieldType = FIELD_TYPES.ENCLOSING;
        entry.children = children;
        return entry;
    }

    //public class NumberField extends InputField{} //unused


    //LayoutGenerator.GeneratorConfiguration generatorConfiguration = null;
    private LinearLayout linearLayout;
    private Context context;
    InputField[] inputFields;



    public LayoutGenerator(Context context, LinearLayout linearLayout, LayoutGenerator.GeneratorConfiguration configuration){
        this.context = context;
        this.linearLayout = linearLayout;
        this.inputFields = toInputFields(configuration.entries, linearLayout, this);
    }

    public LayoutGenerator(Context context, LinearLayout linearLayout, LayoutGenerator.GeneratorConfigurationEntry[] generatorConfigurationEntries){
        this.context = context;
        this.linearLayout = linearLayout;
        this.inputFields = new InputField[generatorConfigurationEntries.length];
        for(int i = 0; i < inputFields.length; i ++){
            //inputFields[i] =generatorConfigurationEntries[i].toField();
            switch (generatorConfigurationEntries[i].fieldType){
                case NUMBER_RUNNER:
                    inputFields[i] = new NumberRunnerField(LayoutGenerator.this, generatorConfigurationEntries[i]);
                    break;
                case TIME:
                    inputFields[i] = new TimeField(LayoutGenerator.this, generatorConfigurationEntries[i]);
                    break;
                case TITLE:
                    inputFields[i] = new TitleField(LayoutGenerator.this, generatorConfigurationEntries[i]);
                    break;
                case DATE:
                    inputFields[i] = new DateField(LayoutGenerator.this, generatorConfigurationEntries[i]);
                    break;
                case ENCLOSING:
                    inputFields[i] = new EnclosingField(LayoutGenerator.this, generatorConfigurationEntries[i]);
                default:
                    break;
            }
        }
        for(int i = 0; i < inputFields.length; i ++){
            inputFields[i].appendView(linearLayout);
        }
        //TODO
    }

    public InputField[] toInputFields(LayoutGenerator.GeneratorConfigurationEntry[] entries, LinearLayout linearLayout, LayoutGenerator layoutGenerator){
        InputField[] inputFields = new InputField[entries.length];
        for(int i = 0; i < inputFields.length; i ++){
            //inputFields[i] =generatorConfigurationEntries[i].toField();
            switch (entries[i].fieldType){
                case NUMBER_RUNNER:
                    inputFields[i] = new NumberRunnerField(layoutGenerator, entries[i]);
                    break;
                case TIME:
                    inputFields[i] = new TimeField(layoutGenerator, entries[i]);
                    break;
                case TITLE:
                    inputFields[i] = new TitleField(layoutGenerator, entries[i]);
                    break;
                case DATE:
                    inputFields[i] = new DateField(layoutGenerator, entries[i]);
                    break;
                case ENCLOSING:
                    inputFields[i] = new EnclosingField(layoutGenerator, entries[i]);
                    break;
                default:
                    break;
            }
        }
        for(int i = 0; i < inputFields.length; i ++){
            inputFields[i].appendView(linearLayout);
        }
        return inputFields;
    }

    public JSONObject getJSONObject(){
        JSONObject jsonObject = new JSONObject();
        for(int i = 0; i < inputFields.length; i ++){
            inputFields[i].putValueToJSON(jsonObject);
        }
        Log.d(TAG, jsonObject.toString());
        return jsonObject;
    }

    public void setJSONObject(JSONObject jsonObject){
        for(int i = 0; i < inputFields.length; i ++){
            inputFields[i].setJSON(jsonObject);
        }
    }

}