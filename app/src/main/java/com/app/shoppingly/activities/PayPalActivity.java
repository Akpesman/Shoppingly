package com.app.shoppingly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.shoppingly.PaypalClientIDConfigClass;
import com.app.shoppingly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class PayPalActivity extends BaseActivity {

    String userId="";

    Button btnPay;
    private int PAYPAL_REQ_CODE = 12;
    private static PayPalConfiguration payPalConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PaypalClientIDConfigClass.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        btnPay = findViewById(R.id.btnPay);
        //btnPayTest = findViewById(R.id.btnPayTest);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paypalPayment();
            }
        });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void paypalPayment() {
        int q = Integer.parseInt(ItemDetailsActivity.quantity);
        float p = Float.parseFloat(ItemDetailsActivity.model.getPrice());

        double total = q*p;

        PayPalPayment payment = new PayPalPayment(new BigDecimal(total), "GBP", "Items Purchasing payment",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent,PAYPAL_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQ_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString();
                        startActivity(new Intent(getApplicationContext(), PaymentDetails.class).putExtra("details",paymentDetails));
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                Toast.makeText(this, "Payment successfull", Toast.LENGTH_SHORT).show();
            }else if(resultCode ==RESULT_CANCELED){
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PayPalService.class));
        super.onDestroy();
    }

}