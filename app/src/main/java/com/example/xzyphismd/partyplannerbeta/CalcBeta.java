package com.example.xzyphismd.partyplannerbeta;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

// TODO change Dialog to fragments
// http://stackoverflow.com/questions/21821267/how-to-set-selected-dialog-values-to-textviews-in-fragment

public class CalcBeta extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private static final String LIGHT_DRINKERS = "LIGHT_DRINKERS";
    private static final String MED_DRINKERS = "MED_DRINKERS";
    private static final String HEAVY_DRINKERS = "HEAVY_DRINKERS";

    private static final Integer BaselineDrinks = 8;
    private static final Double lightMult = 0.5;
    private static final Double medMult = 1.0;
    private static final Double heavyMult = 1.5;

    double nBeer;
    double nWine;
    double nLiq;
    double nTotal;

    private int nLight;
    private int nMed;
    private int nHeavy;

    private int wheel;

    double beerABV = 0.04;
    double wineABV = 0.12;
    double liquorABV = 0.4;

    double beerVol = 355; /* ml */
    double wineVol = 750;
    double liquorVol = 1000;

    double mapd = 17.7441; /* ml of alcohol per drink */

    EditText lightDrinkersET;
    EditText medDrinkersET;
    EditText heavyDrinkersET;

    EditText nBeerET;
    EditText nWineET;
    EditText nLiqET;
    EditText nTotalET;

    EditText WheelET;

    static Dialog numD;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_beta);

        if (savedInstanceState == null) {
            nLight = 0;
            nMed = 0;
            nHeavy = 0;

        } else {
            nLight = savedInstanceState.getInt(LIGHT_DRINKERS);
            nMed = savedInstanceState.getInt(MED_DRINKERS);
            nHeavy = savedInstanceState.getInt(HEAVY_DRINKERS);
        }

        lightDrinkersET = (EditText) findViewById(R.id.nLightDrinkET);
        medDrinkersET = (EditText) findViewById(R.id.nMediumDrinkET);
        heavyDrinkersET = (EditText) findViewById(R.id.nHeavyDrinkET);
        nBeerET = (EditText) findViewById(R.id.nBeerET);
        nWineET = (EditText) findViewById(R.id.nWineET);
        nLiqET = (EditText) findViewById(R.id.nLiqET);
        nTotalET = (EditText) findViewById(R.id.nTotalET);
        WheelET = (EditText) findViewById(R.id.WheelET);

        WheelET.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        lightDrinkersET.addTextChangedListener(lightDrinkersListener);
        medDrinkersET.addTextChangedListener(medDrinkersListener);
        heavyDrinkersET.addTextChangedListener(heavyDrinkersListener);
        updateDrinks();



    }


    public void show()
    {

        Log.v("Definitely", "InSHow2");
        final Dialog numD = new Dialog(CalcBeta.this);
        Drawable d = new ColorDrawable(Color.WHITE);
        //d.setAlpha(85);
        numD.getWindow().setBackgroundDrawable(d);

        numD.setTitle("NumberPicker");
        numD.setContentView(R.layout.new_numpick);
        Button set = (Button) numD.findViewById(R.id.dialogSetButton);
        final NumberPicker np = (NumberPicker) numD.findViewById(R.id.dialogNumPick);
        np.setMinValue(0);
        np.setMaxValue(25);
        int npval = (WheelET.getText().toString()).equals("")?0:Integer.parseInt(WheelET.getText().toString());
        np.setValue(npval);


        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener((NumberPicker.OnValueChangeListener) this);
        set.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheel=np.getValue();
                WheelET.setText(String.valueOf(np.getValue()));
                numD.dismiss();
            }
        });
        numD.show();
    }

    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(LIGHT_DRINKERS, nLight);
        outState.putInt(MED_DRINKERS, nMed);
        outState.putInt(HEAVY_DRINKERS, nHeavy);
    }

    private TextWatcher lightDrinkersListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                nLight = Integer.parseInt(s.toString());
            } catch (NumberFormatException e) {
                nLight = 0;

            }

            updateDrinks();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher medDrinkersListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                nMed = Integer.parseInt(s.toString());
            } catch (NumberFormatException e) {
                nMed = 0;

            }

            updateDrinks();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher heavyDrinkersListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                nHeavy = Integer.parseInt(s.toString());
            } catch (NumberFormatException e) {
                nHeavy = 0;

            }

            updateDrinks();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateDrinks() {

        nTotal = nLight * lightMult * BaselineDrinks + nMed * medMult * BaselineDrinks + nHeavy * heavyMult * BaselineDrinks;
        nTotalET.setText(String.format("%.1f", nTotal));

        nBeer = nTotal * mapd / (beerABV * beerVol);
        nWine = nTotal * mapd / (wineABV * wineVol);
        nLiq = nTotal * mapd / (liquorABV * liquorVol);

        nBeerET.setText(String.format("%d", (int) Math.round(nBeer)));
        nWineET.setText(String.format("%d", (int) Math.round(nWine)));
        nLiqET.setText(String.format("%d", (int) Math.round(nLiq)));

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
