package com.example.chitieucanhan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.chitieucanhan.R;
import com.example.chitieucanhan.adapter.FormatSendFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DanhGiaActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnLater, btnRate;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danhgia);

        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Reference to the root node of the database
        databaseReference = firebaseDatabase.getReference();

        ratingBar = findViewById(R.id.ratingBar);
        btnLater = findViewById(R.id.btnLater);
        btnRate = findViewById(R.id.btnRate);

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng chọn "Để lần sau"
                finish(); // Đóng activity hiện tại
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng chọn "Gửi"
                float rating = ratingBar.getRating();
                // Thực hiện xử lý đánh giá, ví dụ lưu vào cơ sở dữ liệu hoặc gửi điều kiện đánh giá lên server

                // Lấy giá trị email từ SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String receivedEmail = preferences.getString("email", "");

                FormatSendFireBase rateSend = new FormatSendFireBase(receivedEmail, Float.toString(rating));

                int atIndex = receivedEmail.indexOf('@');
                if (atIndex != -1) {
                    String sanitizedEmail = receivedEmail.substring(0, atIndex);
                    // Tiếp tục với việc sử dụng sanitizedEmail làm key trong Firebase
                    databaseReference.child("Rating").child(sanitizedEmail).setValue(rateSend);
                }

                // Hiển thị thông báo
                Toast.makeText(DanhGiaActivity.this, "Cảm ơn bạn đã đánh giá " + rating + " sao!", Toast.LENGTH_SHORT).show();

                finish(); // Đóng activity hiện tại
            }
        });
    }
}