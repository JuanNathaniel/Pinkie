package com.example.projectpastibisa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private EditText etNoHp, etKodeOtp;
    private Button btnSubmit, btnVerif, btnKembali;
    private ProgressBar bar;
    private LinearLayout llOtp;

    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private String verifikasiId;
    private boolean isNewUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNoHp = findViewById(R.id.etNoHp);
        btnSubmit = findViewById(R.id.btnRegister);
        btnKembali = findViewById(R.id.btnKembali);

        bar = findViewById(R.id.bar);
        etKodeOtp = findViewById(R.id.etKodeOtp);
        btnVerif = findViewById(R.id.btnVerif);
        llOtp = findViewById(R.id.llOtp);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noHp = etNoHp.getText().toString();

                if (!noHp.isEmpty()) {
                    checkIfUserExists(noHp);
                    bar.setVisibility(View.VISIBLE);
                    llOtp.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = etKodeOtp.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kode OTP Salah", Toast.LENGTH_SHORT).show();
                } else {
                    verifKodeOTP(code);
                }
            }
        });
    }

    private void checkIfUserExists(String noHp) {
        DatabaseReference userRef = database.child("users");
        userRef.orderByChild("phone_number").equalTo(noHp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isNewUser = false;
                    Toast.makeText(getApplicationContext(), "Nomor terdaftar, lanjutkan ke login", Toast.LENGTH_SHORT).show();
                } else {
                    isNewUser = true;
                    Toast.makeText(getApplicationContext(), "Nomor belum terdaftar, lanjutkan ke registrasi", Toast.LENGTH_SHORT).show();
                }
                kodeOTP(noHp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDB", "Gagal memeriksa nomor telepon", databaseError.toException());
            }
        });
    }

    private void verifKodeOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifikasiId, code);
        loginByCredential(credential);
    }

    private void loginByCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (isNewUser) {
                                savePhoneNumberToDatabase();
                            } else {
                                Log.d("Auth", "Login Berhasil");
                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        } else {
                            Log.e("Auth", "Verifikasi gagal", task.getException());
                            Toast.makeText(getApplicationContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savePhoneNumberToDatabase() {
        String noHp = etNoHp.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.child("users").child(userId);
        userRef.child("phone_number").setValue(noHp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseDB", "Nomor telepon berhasil disimpan untuk userId: " + userId);
                            Toast.makeText(getApplicationContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DaftarActivity.class));
                            finish();
                        } else {
                            Log.e("FirebaseDB", "Gagal menyimpan nomor telepon untuk userId: " + userId, task.getException());
                            Toast.makeText(getApplicationContext(), "Gagal menyimpan nomor telepon", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void kodeOTP(String noHp) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+62" + noHp)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            loginByCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e("OTP", "Verifikasi gagal", e);
            Toast.makeText(getApplicationContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verifikasiId = s;
            Log.d("OTP", "Kode OTP terkirim: " + s);
            Toast.makeText(getApplicationContext(), "Mengirim Kode OTP", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.VISIBLE);
        }
    };
}
