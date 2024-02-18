package com.example.chitieucanhan.adapter;

// Tạo một lớp Java mới
public class FormatSendFireBase {
    private String email;
    private String noiDung;

    // Hàm tạo trống (được Firebase yêu cầu)
    public FormatSendFireBase() {
    }

    // Hàm tạo với các tham số
    public FormatSendFireBase(String email, String noiDung) {
        this.email = email;
        this.noiDung = noiDung;
    }

    // Phương thức Getter
    public String getEmail() {
        return email;
    }

    public String getNoiDung() {
        return noiDung;
    }
}