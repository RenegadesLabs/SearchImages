package com.renegades.labs.searchimages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    private NumberPicker numberPicker;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(5);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);

        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString("searchTerm"));
            numberPicker.setValue(savedInstanceState.getInt("numColumns"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editText.setText(appPreferences.getString("searchTerm", ""));
        numberPicker.setValue(appPreferences.getInt("numColumns", 2));
    }

    public void buttonFind(View view) {
        String searchTerm = editText.getText().toString();
        int columnsQuantity = numberPicker.getValue();

        if (searchTerm.length() == 0 || searchTerm.equals("Enter search Term")) {
            editText.setText("Enter search Term");
        } else {
            Intent intent = new Intent(this, GridActivity.class);
            intent.putExtra("searchTerm", searchTerm);
            intent.putExtra("columnsQuantity", columnsQuantity);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString("searchTerm", editText.getText().toString());
        editor.putInt("numColumns", numberPicker.getValue());
        editor.apply();
    }
}
