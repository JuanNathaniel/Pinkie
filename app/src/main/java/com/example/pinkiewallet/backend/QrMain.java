package com.example.pinkiewallet.backend;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pinkiewallet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.passcodeview.PasscodeView;

public class QrMain extends AppCompatActivity {
    Button btnScanBarcode;
    EditText resultText;
    EditText HasilTotal;
    int dummy;
    private Button openButton;
    private PasscodeView passcodeView;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_main);

        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        resultText = findViewById(R.id.result_text);
        HasilTotal = findViewById(R.id.HasilTotal);
        openButton = findViewById(R.id.btnConfirmBayar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getBalanceFromDatabase();

        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrMain.this, ScannedBarcodeActivity.class);
                scanBarcodeLauncher.launch(intent);
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrMain.this, PinReqActivity.class);
                intent.putExtra("dummyValue", dummy);
                startActivity(intent); // Memulai PinReqActivity
            }
        });
    }

    private void getBalanceFromDatabase() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId).child("balance");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        dummy = snapshot.getValue(Integer.class);
                        HasilTotal.setText(String.valueOf(dummy));
                    } else {
                        // Handle the case where balance doesn't exist in the database
                        dummy = 0;
                        HasilTotal.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    error.toException().printStackTrace();
                    dummy = 0;
                    HasilTotal.setText("0");
                }
            });
        }
    }

    private int performSubtraction(String value) {
        int intValue = Integer.parseInt(value);
        return dummy - intValue;
    }

    private ActivityResultLauncher<Intent> scanBarcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("intentData")) {
                        String intentData = data.getStringExtra("intentData");
                        resultText.setText(intentData);

                        if (isPrice(intentData)) {
                            int remainingBalance = performSubtraction(intentData);
                            HasilTotal.setText(String.valueOf(remainingBalance));
                            navigateToPayment(remainingBalance);
                        } else if (isPhoneNumber(intentData)) {
                            navigateToTransfer(intentData);
                        } else {
                            // Handle invalid QR code
                            resultText.setText("Invalid QR Code");
                        }
                    }
                }
            });

    private boolean isPrice(String str) {
        return str.matches("[1-9]\\d*"); // Memeriksa apakah string adalah angka yang tidak dimulai dengan 0
    }

    private boolean isPhoneNumber(String str) {
        return str.matches("0\\d{9,12}"); // Memeriksa apakah string adalah nomor telepon yang dimulai dengan 0 dan memiliki panjang antara 10 hingga 13 digit
    }

    private void navigateToPayment(int amount) {
        Intent intent = new Intent(QrMain.this, PinReqActivity.class);
        intent.putExtra("jumlah_harga", amount);
        startActivity(intent);
    }

    private void navigateToTransfer(String phoneNumber) {
        Intent intent = new Intent(QrMain.this, TransferActivity.class);
        intent.putExtra("nomor_telepon", phoneNumber);
        startActivity(intent);
    }
}
