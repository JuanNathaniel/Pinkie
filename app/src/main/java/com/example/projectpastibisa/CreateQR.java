package com.example.projectpastibisa;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CreateQR extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_qr);

        imageView = findViewById(R.id.qrCodeImageView);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getPhoneNumberAndGenerateQRCode();
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
                        generateQRCode(phoneNumber);
                    } else {
                        // Handle the case where the phone number doesn't exist in the database
                        generateQRCode("No Phone Number");
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
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
