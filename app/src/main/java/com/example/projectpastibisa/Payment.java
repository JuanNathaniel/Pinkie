package com.example.projectpastibisa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Payment extends AppCompatActivity {
    EditText HasilTotal;
    private Button openButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        // Terima nilai jumlah_harga dari intent
        Intent intent = getIntent();
        int jumlahHarga = intent.getIntExtra("jumlah_harga", 0);

        // Set nilai jumlah_harga ke dalam EditText HasilTotal
        HasilTotal = findViewById(R.id.HasilTotal);
        HasilTotal.setText(String.valueOf(jumlahHarga));

        openButton = findViewById(R.id.backMain);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(jumlahHarga);
            }
        });
    }

    private void updateBalance(int jumlahHarga) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child(userId);

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentBalance = snapshot.getValue(Integer.class);
                    int newBalance = currentBalance - jumlahHarga;

                    userRef.child("balance").setValue(newBalance)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Payment.this, "Balance updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Payment.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Payment.this, "Failed to update balance", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Payment.this, "Balance not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Payment.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
