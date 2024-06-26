package com.example.projectpastibisa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hanks.passcodeview.PasscodeView;

public class PinReqActivity extends AppCompatActivity {

    private PasscodeView passcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_req);

        // Mengambil nilai jumlah harga dari intent
        int jumlahHarga = getIntent().getIntExtra("jumlah_harga", 0);

        // Mengambil referensi PasscodeView dari layout XML
        passcodeView = findViewById(R.id.passcodeview);

        // Memeriksa apakah passcodeView tidak null sebelum menggunakan
        if (passcodeView != null) {
            // Set passcode length
            passcodeView.setPasscodeLength(5);
            // Menetapkan kode passcode statis
            passcodeView.setLocalPasscode("12369");
            // Menetapkan listener untuk menangani keberhasilan atau kegagalan
            passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {
                    // Menampilkan pesan kesalahan jika passcode salah
                    Toast.makeText(PinReqActivity.this, "Password is wrong!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    // Panggil method untuk membuka aktivitas Pembayaran
                    openPaymentActivity(jumlahHarga);
                }
            });
        }
    }

    private void openPaymentActivity(int jumlahHarga) {
        Intent paymentIntent = new Intent(this, Payment.class);
        paymentIntent.putExtra("jumlah_harga", jumlahHarga);
        startActivity(paymentIntent);
    }
}
