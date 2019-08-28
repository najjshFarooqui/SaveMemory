package com.smnetinfo.savememory;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class PaymentMethod extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText cardNumber, cvc;
    Spinner cardType;
    String getCard;
    CardView cardView;
    private RadioGroup radioGroup;
    String amount = "";
    TextView amountTv;
    RelativeLayout cardSelected, payaplSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        cardType = findViewById(R.id.spinnerCardType);
        cardNumber = findViewById(R.id.cardNumber);
        cvc = findViewById(R.id.cvc);
        cardView = findViewById(R.id.submit);
        amountTv = findViewById(R.id.tvAmount);
        payaplSelected = findViewById(R.id.payaplSelected);
        cardSelected = findViewById(R.id.cardSelected);


        amount = getIntent().getStringExtra("AMOUNT");
        amountTv.setText(amount);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {


                    if (rb.getText().toString().trim().equalsIgnoreCase("Card")) {

                        payaplSelected.setVisibility(View.GONE);
                        cardSelected.setVisibility(View.VISIBLE);
                    } else if (rb.getText().toString().trim().equalsIgnoreCase("Paypal")) {

                        payaplSelected.setVisibility(View.VISIBLE);
                        cardSelected.setVisibility(View.GONE);
                    }

                }

            }
        });

    }


    public void onSubmit(View v) {
        RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if (rb.getText().toString().equals("optionCard")) {

        } else if (rb.getText().toString().equals("optionPaypal")) {


        } else {


        }


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cards_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        cardType.setAdapter(adapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        getCard = adapterView.getItemAtPosition(i).toString();
        cardType.setSelection(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}