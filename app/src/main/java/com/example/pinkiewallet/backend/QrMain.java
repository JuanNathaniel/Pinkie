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
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinkiewallet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QrMain extends AppCompatActivity {
    TextView resultText;
    int balance; // Mengganti variabel dummy menjadi balance yang merepresentasikan saldo user
    private Button openButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_main);

        resultText = findViewById(R.id.result_text);
        openButton = findViewById(R.id.btnConfirmBayar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getBalanceFromDatabase();

        // Get the scanned data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("intentData")) {
            String intentData = intent.getStringExtra("intentData");
            resultText.setText("Rp"+intentData);

            if (isPrice(intentData)) {
                int jumlahHarga = Integer.parseInt(intentData);
//
                // Navigate to Payment after confirmation button clicked
                openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (balance >= jumlahHarga) {
                            navigateToPayment(String.valueOf(jumlahHarga));
                        } else {
                            Toast.makeText(QrMain.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (isPhoneNumber(intentData)) {
                // Navigate to Transfer after confirmation button clicked
                openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigateToTransfer(intentData);
                    }
                });
            } else {
                // Handle invalid QR code
                resultText.setText("Invalid QR Code");
            }
        }
    }

    private void getBalanceFromDatabase() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId).child("balance");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        balance = snapshot.getValue(Integer.class); // Mengambil nilai balance dari database
//                        HasilTotal.setText(String.valueOf(balance));
                    } else {
                        // Handle the case where balance doesn't exist in the database
                        balance = 0;
//                        HasilTotal.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    error.toException().printStackTrace();
                    balance = 0;
//                    HasilTotal.setText("0");
                }
            });
        }
    }

    private boolean isPrice(String str) {
        return str.matches("[1-9]\\d*"); // Memeriksa apakah string adalah angka yang tidak dimulai dengan 0
    }

    private boolean isPhoneNumber(String str) {
        return str.matches("0\\d{9,12}"); // Memeriksa apakah string adalah nomor telepon yang dimulai dengan 0 dan memiliki panjang antara 10 hingga 13 digit
    }

    private void navigateToPayment(String amount) {
        Intent intent = new Intent(QrMain.this, PinReqActivity.class);
        intent.putExtra("jumlah_harga", amount);
        intent.putExtra("caller", "QrMain");  // Corrected Intent.putExtra
        startActivity(intent);
    }


    private void navigateToTransfer(String phoneNumber) {
        Intent intent = new Intent(QrMain.this, TransferActivity.class);
        intent.putExtra("nomor_telepon", phoneNumber);
        startActivity(intent);
    }
}
