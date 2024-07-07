package com.example.pinkiewallet.backend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pinkiewallet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.pinkiewallet.view.activity.MainActivity;

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
                Intent intent = new Intent(Payment.this, MainActivity.class);
                startActivity(intent);
                finish(); // Menutup activity Payment

            }
        });
    }
}
