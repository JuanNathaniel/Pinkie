package com.example.projectpastibisa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class TransferActivity extends AppCompatActivity {

    private EditText etRecipientPhone, etAmount;
    private Button btnTransfer;

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        etRecipientPhone = findViewById(R.id.etRecipientPhone);
        etAmount = findViewById(R.id.etAmount);
        btnTransfer = findViewById(R.id.btnTransfer);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // Ambil nomor telepon dari intent dan set ke etRecipientPhone
        String phoneNumber = getIntent().getStringExtra("nomor_telepon");
        if (phoneNumber != null) {
            etRecipientPhone.setText(phoneNumber);
        }

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipientPhone = etRecipientPhone.getText().toString();
                String amountStr = etAmount.getText().toString();

                if (recipientPhone.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    int amount = Integer.parseInt(amountStr);
                    transferMoney(recipientPhone, amount);
                }
            }
        });
    }

    private void transferMoney(String recipientPhone, int amount) {
        String senderId = mAuth.getCurrentUser().getUid();

        database.child("users").child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer senderBalanceObj = dataSnapshot.child("balance").getValue(Integer.class);
                    if (senderBalanceObj != null) {
                        int senderBalance = senderBalanceObj;

                        if (senderBalance >= amount) {
                            DatabaseReference recipientRef = database.child("users");
                            recipientRef.orderByChild("phone_number").equalTo(recipientPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot recipientSnapshot) {
                                    if (recipientSnapshot.exists()) {
                                        String recipientId = recipientSnapshot.getChildren().iterator().next().getKey();
                                        Integer recipientBalanceObj = recipientSnapshot.child(recipientId).child("balance").getValue(Integer.class);
                                        if (recipientBalanceObj != null) {
                                            int recipientBalance = recipientBalanceObj;

                                            // Update sender and recipient balances
                                            database.child("users").child(senderId).child("balance").setValue(senderBalance - amount);
                                            database.child("users").child(recipientId).child("balance").setValue(recipientBalance + amount);

                                            // Save transaction record
                                            DatabaseReference transactionRef = database.child("transactions").push();
                                            transactionRef.child("from").setValue(senderId);
                                            transactionRef.child("to").setValue(recipientId);
                                            transactionRef.child("amount").setValue(amount);
                                            transactionRef.child("timestamp").setValue(ServerValue.TIMESTAMP);

                                            Toast.makeText(getApplicationContext(), "Transfer berhasil", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Saldo penerima tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Nomor penerima tidak ditemukan", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("FirebaseDB", "Gagal mencari nomor penerima", databaseError.toException());
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Saldo tidak cukup", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Saldo pengirim tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Pengirim tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDB", "Gagal mencari saldo pengirim", databaseError.toException());
            }
        });
    }
}
