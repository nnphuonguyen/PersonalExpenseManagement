package com.example.chitieucanhan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chitieucanhan.R;
import com.example.chitieucanhan.adapter.FormatSendFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhanHoiActivity extends AppCompatActivity {
    private EditText editEmail;
    private EditText editNoiDung;
    private Button btnGui;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phanhoi);

        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Reference to the root node of the database
        databaseReference = firebaseDatabase.getReference();

        // Ánh xạ các phần tử từ layout
        editEmail = findViewById(R.id.editEmail);
        editNoiDung = findViewById(R.id.editNoiDung);
        btnGui = findViewById(R.id.btnGui);

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String noiDung = editNoiDung.getText().toString();

                if (!email.isEmpty() && !noiDung.isEmpty()) {

                    // Tạo đối tượng Phản hồi với giá trị email và noiDung
                    FormatSendFireBase feedback = new FormatSendFireBase(email, noiDung);

                    int atIndex = email.indexOf('@');
                    if (atIndex != -1) {
                        String sanitizedEmail = email.substring(0, atIndex);
                        // Tiếp tục với việc sử dụng sanitizedEmail làm key trong Firebase
                        databaseReference.child("feedback").child(sanitizedEmail).setValue(feedback);
                    } else {
                        // Xử lý trường hợp không có ký tự @ trong địa chỉ email
                        Toast.makeText(PhanHoiActivity.this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(PhanHoiActivity.this, "Gửi phản hồi thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhanHoiActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}