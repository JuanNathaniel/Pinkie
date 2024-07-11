package com.example.pinkiewallet.backend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.pinkiewallet.R;
import com.example.pinkiewallet.databinding.CreateQrBinding;
import com.example.pinkiewallet.view.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateQR extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageView imageView;
    private TextView phoneNumberTextView;
    private Button shareButton;
    private Bitmap qrBitmap;
    private CreateQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the support action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        imageView = findViewById(R.id.qrCodeImageView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        shareButton = findViewById(R.id.shareButton);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getPhoneNumberAndGenerateQRCode();

        shareButton.setOnClickListener(v -> shareQRCode());
        binding.backwardButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateQR.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getPhoneNumberAndGenerateQRCode() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId).child("phone_number");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String phoneNumber = snapshot.getValue(String.class);
                        phoneNumberTextView.setText("Nomor Telepon: " + phoneNumber);
                        generateQRCode(phoneNumber);
                    } else {
                        // Handle the case where the phone number doesn't exist in the database
                        String defaultText = "No Phone Number";
                        phoneNumberTextView.setText("Nomor Telepon: " + defaultText);
                        generateQRCode(defaultText);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    error.toException().printStackTrace();
                }
            });
        }
    }

    private void generateQRCode(String text) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            qrBitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void shareQRCode() {
        if (qrBitmap != null) {
            try {
                // Simpan gambar ke penyimpanan sementara
                File cachePath = new File(getExternalCacheDir(), "my_images/");
                cachePath.mkdirs(); // Pastikan direktori ada
                File file = new File(cachePath, "qr_code.png");
                FileOutputStream stream = new FileOutputStream(file); // Timpa file
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();

                Uri contentUri = FileProvider.getUriForFile(this, "com.example.pinkiewallet.fileprovider", file);

                // Buat Intent untuk berbagi gambar
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "A-Abangg aku minta dana donggg.... ini kode QR akuu. Scan di Aplikasi Pinkie Yaaa :)");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Bagikan QR Code via"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
