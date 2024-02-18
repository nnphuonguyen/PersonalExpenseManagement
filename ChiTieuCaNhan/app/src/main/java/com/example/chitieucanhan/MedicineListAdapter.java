package com.example.chitieucanhan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitieucanhan.mydatabase.MedicalDB;
import com.example.chitieucanhan.mydatabase.MedicineEntity;
import com.example.chitieucanhan.mydatabase.RemindDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// İlaç listesi için RecyclerView adaptörü
public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MedicineHolder> {
    // İlaç listesi verilerini tutan Cursor
    private Cursor med_list;
    // Uygulama bağlamı
    public Context context;
    // Veritabanı yardımcı sınıfı
    public MedicalDB helper;
    private List<MedicineEntity> medicineList;
    private RemindDatabase remindDatabase;

    // Adaptörün oluşturulması
    public MedicineListAdapter(Context context, RemindDatabase appDatabase) {
        this.context = context;
        this.remindDatabase = remindDatabase;
        this.medicineList = new ArrayList<>();
    }

    // Veri setini güncelleyen metot
    public void setUserData(List<MedicineEntity> medicineList) {
        this.medicineList = medicineList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ViewHolder'ın oluşturulması
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_card, parent, false);
        MedicineHolder vh = new MedicineHolder(v);
        return vh;
    }


    public void onBindViewHolder(@NonNull MedicineHolder holder, int position) {
        if (med_list != null) {
            // ViewHolder'ın ilgili öğelerini doldurma
            MedicineEntity medicineEntity = medicineList.get(position);

            holder.medName.setText(medicineEntity.MED_NAME);
            holder.qty.setText("Adet: " + medicineEntity.QTY);
            holder.id = medicineEntity.id;
            holder.time.setText("Saat: " + medicineEntity.DATE_TIME);

            // İlaç durumu (açık/kapalı) için anahtarlama düğmesinin durumunu ayarlama
            boolean isChecked = medicineEntity.ENABLE == 1;
            holder.toggleSwitch.setChecked(isChecked);

            // Anahtarlama düğmesine tıklanınca çağrılan listener
            holder.toggleSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // İlaç durumunu güncelleme
                    if (((MedicineHolder) holder).toggleSwitch.isChecked()) {
                        helper.setEnable(((MedicineHolder) holder).id, 1);
                    } else {
                        helper.setEnable(((MedicineHolder) holder).id, 0);
                    }

                    // Bildirim için alarmı ayarlama
                    MedicineEntity medicineEntity = remindDatabase.medicineDao().getMedicine(((MedicineHolder) holder).id);

                    String[] raw_time = medicineEntity.DATE_TIME.split(":");
                    int hour = Integer.parseInt(raw_time[0]);
                    int min = Integer.parseInt(raw_time[1]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, min);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.SECOND, 0);
                    now.set(Calendar.MILLISECOND, 0);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
                    intent.putExtra("medName", medicineEntity.MED_NAME);
                    intent.putExtra("medQty", String.valueOf(medicineEntity.QTY));


                    if (((MedicineHolder) holder).toggleSwitch.isChecked()) {
                        // Tekrarlama günlerini kontrol et
                        String days = medicineEntity.DAYS;
                        if (days.equals("0000000")) {
                            // Gün belirtilmemişse, bir sonraki güne ayarla
                            if (cal.before(now)) {
                                cal.add(Calendar.DATE, 1);
                            }
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, medicineEntity.id, intent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                            Toast.makeText(context, "Hatırlatıcı " + medicineEntity.MED_NAME + " ilacı için saat " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ", tarih " + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " olarak ayarlandı.", Toast.LENGTH_LONG).show();
                        } else {
                            // Gün belirtilmişse, her bir gün için ayrı alarm ayarla
                            int ct = 1;
                            for (char d : days.toCharArray()) {
                                if (d == '1') {
                                    cal.set(Calendar.DAY_OF_WEEK, ct);
                                    if (cal.before(now)) {
                                        cal.add(Calendar.DATE, 7);
                                    }
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(((MedicineHolder) holder).id + "" + ct), intent, 0);
                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                                }
                                ct++;
                            }
                            Toast.makeText(context, "Hatırlatıcı " + medicineEntity.MED_NAME + " ilacı için saat " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + " olarak ayarlandı.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // İlaç kapatıldıysa, ilgili alarmı iptal et
                        String days = medicineEntity.DAYS;
                        if (days.equals("0000000")) {
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, medicineEntity.id, intent, 0);
                            alarmManager.cancel(pendingIntent);
                        } else {
                            int ct = 1;
                            for (char d : days.toCharArray()) {
                                if (d == '1') {
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(medicineEntity.id + "" + ct), intent, 0);
                                    alarmManager.cancel(pendingIntent);
                                }
                                ct++;
                            }
                        }
                    }
                }
            });

            // İlaç silme düğmesine tıklanınca çağrılan listener
            holder.deleteMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // İlaç silme işlemini gerçekleştir
                    remindDatabase.medicineDao().deleteMedicine(((MedicineHolder) holder).id);
                    // Veri setini güncelle và RecyclerView'yi yeniden ayarla
                    setUserData(remindDatabase.medicineDao().getAllMedicine());

                }
            });
            // Cursor'daki sonraki ilaca geçme
            med_list.moveToNext();
        }
    }

    @Override
    public int getItemCount() {
        // Adaptörün tuttuğu öğe sayısını döndürme
        return medicineList.size();
    }

    // İlaç öğesini tutan ViewHolder sınıfı
    public class MedicineHolder extends RecyclerView.ViewHolder {
        TextView medName, time, qty;
        ImageButton deleteMed;
        int id;
        Switch toggleSwitch;

        // ViewHolder'ın oluşturulması
        public MedicineHolder(@NonNull View itemView) {
            super(itemView);
            // ViewHolder içindeki öğelerin atanması
            medName = (TextView) itemView.findViewById(R.id.med_name);
            time = (TextView) itemView.findViewById(R.id.med_time);
            qty = (TextView) itemView.findViewById(R.id.med_quantity);
            deleteMed = (ImageButton) itemView.findViewById(R.id.delete_med);
            toggleSwitch = (Switch) itemView.findViewById(R.id.toggle_switch);
        }
    }
}
