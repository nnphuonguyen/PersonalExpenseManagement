package com.example.chitieucanhan.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chitieucanhan.MedicineListAdapter;
import com.example.chitieucanhan.R;
import com.example.chitieucanhan.TimePickerFragment;
import com.example.chitieucanhan.mydatabase.MedicalDB;
import com.example.chitieucanhan.mydatabase.MedicineDao;
import com.example.chitieucanhan.mydatabase.MedicineEntity;
import com.example.chitieucanhan.mydatabase.RemindDatabase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class NhacNhoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public RecyclerView medList;
    // İlaç listesi adaptörü
    public static MedicineListAdapter medListAdapter;
    // İlaç ekleme butonu
    public FloatingActionButton medFab;
    // İlaç bilgileri girişi için UI elemanları
    Button medTime;
    public MedicalDB medicalDB;
    public static MedicineDao medicineDao;
    public MedicineEntity medicineEntity;
    public RemindDatabase remindDatabase;
    EditText medName, medQty;
    Switch isRepeat;
    ChipGroup chipGroup;
    Chip pzt, sal, crs, prs, cum, cts, pzr;


    // Veritabanı nesnesi
    public MedicalDB DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhacnho);

        remindDatabase = MedicalDB.getInstance(getApplicationContext());


        // View'ları aktiviteye bağla
        medList = findViewById(R.id.med_list);
        medFab = findViewById(R.id.med_fab);

        // RecyclerView için layout manager'ın ayarlanması
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        medList.setLayoutManager(linearLayoutManager);

        // İlaç listesi adaptörünün oluşturulması ve verilerin atanması
        medListAdapter = new MedicineListAdapter(getApplicationContext(), remindDatabase);
        medList.setAdapter(medListAdapter);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<MedicineEntity> medicineEntities = remindDatabase.medicineDao().getAllMedicine();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI here with the data
                        medListAdapter.setUserData(medicineEntities);
                        medListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });


        // İlaç ekleme butonuna tıklanınca çağrılan listener
        medFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // İlaç ekleme dialogunu gösteren metot
                medicineAdder().show();
            }
        });
    }

    private AlertDialog medicineAdder() {
        // Dialogun içeriğini belirten layout'un tanımlanması
        View layout = View.inflate(this, R.layout.add_med_dialog, null);

        // İlaç detayları
        medName = layout.findViewById(R.id.add_med_name);
        medQty = layout.findViewById(R.id.add_med_qty);
        medTime = layout.findViewById(R.id.add_med_time);
        // UI elemanları
        isRepeat = layout.findViewById(R.id.repeat_switch);
        chipGroup = layout.findViewById(R.id.chip_group);
        setChildrenEnabled(chipGroup, false);
        pzt = layout.findViewById(R.id.pazartesi);
        sal = layout.findViewById(R.id.sali);
        crs = layout.findViewById(R.id.carsamba);
        prs = layout.findViewById(R.id.persembe);
        cum = layout.findViewById(R.id.cuma);
        cts = layout.findViewById(R.id.cumartesi);
        pzr = layout.findViewById(R.id.pazar);

        // Saat seçimi için tıklanınca çağrılan listener
        medTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // Tekrarlama switch'ine tıklanınca çağrılan listener
        isRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tekrarlama açık ise günleri etkinleştir, değilse devre dışı bırak
                if (!isRepeat.isChecked()) {
                    setChildrenEnabled(chipGroup, false);
                } else {
                    setChildrenEnabled(chipGroup, true);
                }
            }
        });

        // AlertDialog oluşturucu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);// Layout'un AlertDialog'a atanması

        // "EKLE" butonuna tıklanınca yapılacak işlemler
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // İlaç miktarını sayıya çevir
                int qty = 0;//default değer
                if (!"".equals(medQty.getText().toString()))
                    qty = Integer.parseInt(medQty.getText().toString());

                // Günlerin formatını belirle
                String days = "0000000";//default değer
                if (isRepeat.isChecked()) {
                    days = setDaysFormat(pzr, pzt, sal, crs, prs, cum, cts);
                }
//                new NhacNhoActivity().InsertMedicineTask().execute(medicineEntity);
                // İlaç eklemeyi gerçekleştir

                MedicineEntity medicineEntity = MedicineEntity.createDefault();
                // İlaç listesini güncelle ve RecyclerView'yi yeniden ayarla
//                new NhacNhoActivity().InsertMedicineTask().execute(medicineEntity);
                medicineEntity.setMED_NAME(medName.getText().toString());
                medicineEntity.setQTY(qty);
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Chèn dữ liệu vào cơ sở dữ liệu trên một luồng khác
                        remindDatabase.medicineDao().insertMedicine(medicineEntity);

                        // Lấy danh sách mới từ cơ sở dữ liệu
                        final List<MedicineEntity> medicineEntities = remindDatabase.medicineDao().getAllMedicine();

                        // Cập nhật UI với danh sách mới trên luồng chính
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                medListAdapter.setUserData(medicineEntities);
                                medListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                medListAdapter.notifyDataSetChanged();
                medList.setAdapter(medListAdapter);
            }
        });

        // "İPTAL" butonuna tıklanınca yapılacak işlemler
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();  // Oluşturulan AlertDialog'un döndürülmesi
    }

    private class InsertMedicineTask extends AsyncTask<MedicineEntity, Void, Long> {
        // Các bước tiền xử lý (nếu cần)...

        @Override
        protected Long doInBackground(MedicineEntity... medicineEntities) {
            if (medicineEntities[0] != null) {
                // Thực hiện công việc chèn dữ liệu vào cơ sở dữ liệu
                long insertedId = remindDatabase.medicineDao().insertMedicine(medicineEntities[0]);

                // Kiểm tra xem bản ghi có được thêm thành công không
                if (insertedId != -1) {
                    // Nếu thành công, lấy danh sách mới từ cơ sở dữ liệu và trả về
                    return insertedId;
                } else {
                    // Nếu có lỗi, trả về -1 để xử lý lỗi
                    return -1L;
                }
            } else {
                // Nếu medicineEntities[0] là null, trả về -1 để xử lý lỗi
                return -1L;
            }
        }

        @Override
        protected void onPostExecute(Long insertedId) {
            // Xử lý kết quả trả về từ doInBackground
            if (insertedId != -1) {
                // Bản ghi đã được thêm thành công, có thể thực hiện các bước tiếp theo
                // Lấy danh sách mới từ cơ sở dữ liệu


                new AsyncTask<Void, Void, List<MedicineEntity>>() {
                    @Override
                    protected List<MedicineEntity> doInBackground(Void... voids) {
                        return remindDatabase.medicineDao().getAllMedicine();
                    }

                    @Override
                    protected void onPostExecute(List<MedicineEntity> medicineEntities) {
                        // Cập nhật UI với danh sách mới trên luồng chính
                        medListAdapter.setUserData(medicineEntities);
                        medListAdapter.notifyDataSetChanged();
                    }
                }.execute();
            } else {
                Toast.makeText(NhacNhoActivity.this, "Lỗi khi thêm bản ghi", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class LoadMedicineTask extends AsyncTask<Void, Void, List<MedicineEntity>> {
        @Override
        protected List<MedicineEntity> doInBackground(Void... voids) {
            // Lấy danh sách mới từ cơ sở dữ liệu và trả về
            return remindDatabase.medicineDao().getAllMedicine();
        }

        @Override
        protected void onPostExecute(List<MedicineEntity> medicineEntities) {
            // Cập nhật UI với danh sách mới trên luồng chính
            medListAdapter.setUserData(medicineEntities);
            medListAdapter.notifyDataSetChanged();
        }
    }
    public String setDaysFormat(Chip pzr, Chip pzt, Chip sal, Chip crs, Chip prs, Chip cum, Chip cts) {
        String dayString = "" + (pzr.isChecked() ? "1" : "0") + (pzt.isChecked() ? "1" : "0") + (sal.isChecked() ? "1" : "0") + (crs.isChecked() ? "1" : "0") + (prs.isChecked() ? "1" : "0") + (cum.isChecked() ? "1" : "0") + (cts.isChecked() ? "1" : "0");
        return dayString;
    }

    // ChipGroup içindeki tüm elemanların tıklanabilir olma etkinliğini ayarlayan metot
    public void setChildrenEnabled(ChipGroup chipGroup, Boolean enable) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            chipGroup.getChildAt(i).setEnabled(enable);
        }
    }
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }
}