package com.example.pinkiewallet.backend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Payment extends AppCompatActivity {
    TextView HasilTotal;
    TextView KataAsal;
    TextView timeTextView;

    private Button openButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        // Terima nilai jumlah_harga dari intent
        Intent intent = getIntent();
        int jumlahHarga = intent.getIntExtra("jumlah_harga", 0);
        String origin = intent.getStringExtra("origin");
        String asal = origin;

        // Format jumlah_harga ke dalam format Rupiah
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String formattedHarga = rupiahFormat.format(jumlahHarga);

        // Set nilai jumlah_harga ke dalam EditText HasilTotal
        HasilTotal = findViewById(R.id.HasilTotal);
        HasilTotal.setText(formattedHarga);

        KataAsal = findViewById(R.id.Asal);
        KataAsal.setText(asal);

        // Set tanggal dan waktu saat ini
        timeTextView = findViewById(R.id.time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(Calendar.getInstance().getTime());
        timeTextView.setText(currentTime);

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
