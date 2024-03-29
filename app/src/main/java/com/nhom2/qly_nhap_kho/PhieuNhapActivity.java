package com.nhom2.qly_nhap_kho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom2.qly_nhap_kho.adapter.PhieuNhapAdapter;
import com.nhom2.qly_nhap_kho.listener.PhieuNhapListener;
import com.nhom2.qly_nhap_kho.model.Kho;
import com.nhom2.qly_nhap_kho.model.PhieuNhap;

import java.util.ArrayList;
import java.util.List;

public class PhieuNhapActivity extends AppCompatActivity {

    NhapKhoHelper helper;
    Spinner spinnerKho;

    RecyclerView recycleView;
    PhieuNhapAdapter phieuNhapAdapter;
    TextView txtTongPhieuNhap;
    FloatingActionButton floatingActionButton;

    List<PhieuNhap> arrayPhieuNhap = new ArrayList<>();
    Kho kho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phieu_nhap);
        helper = NhapKhoHelper.getInstance(this);
        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);


        spinnerKho = (Spinner) findViewById(R.id.spinner1);
        recycleView = findViewById(R.id.recycleView);
        txtTongPhieuNhap = findViewById(R.id.txtTongPhieuNhap);
        floatingActionButton = findViewById(R.id.floatingActionButton);


        ArrayList<Kho> arrayKho = new ArrayList<Kho>();
        //arrayKho.add(new Kho("K0", "Tất cả"));
        ArrayList<String> arrayTenKho = new ArrayList<String>();
        arrayTenKho.add("Tất cả");

        //Hien thi

        Cursor dataKho = helper.GetData("SELECT * FROM Kho");

        Kho kho;
        while (dataKho.moveToNext()) {
            kho = new Kho(dataKho.getString(0), dataKho.getString(1));
            arrayKho.add(kho);
            arrayTenKho.add(kho.getTenKho());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayTenKho);
        spinnerKho.setAdapter(arrayAdapter);

        spinnerKho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actionGetData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInsert();
            }
        });

    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Paint paint = new Paint();

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(PhieuNhapActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();

            Toast.makeText(PhieuNhapActivity.this, "Xóa thành công " + arrayPhieuNhap.get(position).getSoPhieu(), Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView

            helper.QueryData("DELETE FROM PhieuNhap WHERE SoPhieu='" + arrayPhieuNhap.get(position).getSoPhieu() + "'");
            arrayPhieuNhap.remove(position);
            phieuNhapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            float translationX = dX;
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX <= 0) // Swiping Left
            {
                translationX = -Math.min(-dX, height * 2);
                paint.setColor(Color.RED);
                RectF background = new RectF((float) itemView.getRight() + translationX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextAlign(Paint.Align.LEFT);
                Rect titleBounds = new Rect();
                String title = "Delete";
                paint.getTextBounds(title, 0, title.length(), titleBounds);

                double y = background.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom;
                c.drawText(title, background.left + 80, (float) (background.top + y), paint);
                //viewHolder.ItemView.TranslationX = translationX;
            } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX > 0) // Swiping Right
            {
                translationX = Math.min(dX, height * 2);
                paint.setColor(Color.RED);

                RectF background = new RectF((float) itemView.getRight() + translationX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setTextAlign(Paint.Align.LEFT);
                Rect titleBounds = new Rect();
                String title = "Delete";
                paint.getTextBounds(title, 0, title.length(), titleBounds);

                double y = background.height() / 2 + background.height() / 2 - titleBounds.bottom;
                c.drawText(title, background.left + 50, (float) (background.top + y), paint);


            }

            super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);

        }
    };

    public void actionGetData() {
        arrayPhieuNhap.clear();

        if (spinnerKho.getSelectedItem().toString().equals("Tất cả")) {

            Cursor dataPhieuNhap = helper.GetData("SELECT * FROM PhieuNhap ");
            kho = new Kho("Tất cả", "Tất cả");

            PhieuNhap phieuNhap;
            while (dataPhieuNhap.moveToNext()) {
                phieuNhap = new PhieuNhap(Integer.valueOf(dataPhieuNhap.getString(0)), dataPhieuNhap.getString(1), dataPhieuNhap.getString(2));
                arrayPhieuNhap.add(phieuNhap);
            }

            txtTongPhieuNhap.setText("Tổng số phiếu nhập: " + arrayPhieuNhap.size());
        } else {
            Cursor dataKho = helper.GetData("SELECT * FROM Kho WHERE TenKho='" + spinnerKho.getSelectedItem().toString() + "'");
            String ma = "";

            while (dataKho.moveToNext()) {
                ma = dataKho.getString(0);
                kho = new Kho(ma, dataKho.getString(1));
            }
            Cursor dataPhieuNhap = helper.GetData("SELECT * FROM PhieuNhap WHERE MaKho = '" + ma + "'");

            PhieuNhap phieuNhap;
            while (dataPhieuNhap.moveToNext()) {
                phieuNhap = new PhieuNhap(Integer.valueOf(dataPhieuNhap.getString(0)), dataPhieuNhap.getString(1), dataPhieuNhap.getString(2));
                arrayPhieuNhap.add(phieuNhap);
            }
            txtTongPhieuNhap.setText("Tổng số phiếu nhập: " + arrayPhieuNhap.size());
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycleView);


        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(PhieuNhapActivity.this, LinearLayoutManager.VERTICAL, false));

        phieuNhapAdapter = new PhieuNhapAdapter(PhieuNhapActivity.this, arrayPhieuNhap, phieuNhapListener);
        recycleView.setAdapter(phieuNhapAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    private final PhieuNhapListener phieuNhapListener = new PhieuNhapListener() {
        @Override
        public void onPhieuNhapClicked(String id, PhieuNhap phieuNhap) {

            Intent intent = new Intent(PhieuNhapActivity.this, ChiTietPhieuNhapActivity.class);

            Bundle bundle = new Bundle();

            bundle.putString("id", id);
            bundle.putSerializable("phieuNhap", phieuNhap);
            bundle.putSerializable("kho", kho);

            intent.putExtra("data", bundle);

            startActivity(intent);
        }
    };

    public void dialogUpdate(int soPhieu, String ngayLap, String maKho) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_suaphieunhap);

        //anh xa
        EditText editSoPhieu = (EditText) dialog.findViewById(R.id.editSoPhieu);
        EditText editNgayLap = (EditText) dialog.findViewById(R.id.editNgayLap);
        Button btnHoanTat = (Button) dialog.findViewById(R.id.btnHoanTat);
        Button btnXoa = (Button) dialog.findViewById(R.id.btnXoa);

        //them du lieu vao spinner
        Spinner spinnerMaKho = dialog.findViewById(R.id.spinnerMaKho);
        Cursor dataKho = helper.GetData("SELECT * FROM Kho");
        ArrayList<String> arrayMaKhoTam = new ArrayList<String>();
        Kho khoTam;
        while (dataKho.moveToNext()) {
            khoTam = new Kho(dataKho.getString(0), dataKho.getString(1));
            arrayMaKhoTam.add(khoTam.getMaKho());
        }
        ArrayAdapter arrayAdapterTam = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayMaKhoTam);
        spinnerMaKho.setAdapter(arrayAdapterTam);

        //set du lieu
        editSoPhieu.setText(soPhieu + "");
        editNgayLap.setText(ngayLap);
        spinnerMaKho.setSelection(arrayAdapterTam.getPosition(maKho));

        //bat su kien nut bam
        btnHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ngayLapMoi = String.valueOf(editNgayLap.getText());
                String maKhoMoi = String.valueOf(spinnerMaKho.getSelectedItem().toString());
                if (TextUtils.isEmpty(ngayLapMoi) || TextUtils.isEmpty(maKhoMoi)) {
                    Toast.makeText(PhieuNhapActivity.this, "Nội dung cần sửa chưa được nhập", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                helper.QueryData("UPDATE PhieuNhap SET NgayLap='" + ngayLapMoi + "',MaKho='" + maKhoMoi + "' WHERE SoPhieu='" + soPhieu + "'");
                dialog.dismiss();
                actionGetData();
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.QueryData("DELETE FROM PhieuNhap WHERE SoPhieu='" + soPhieu + "'");
                dialog.dismiss();
                actionGetData();
            }
        });

        dialog.show();
    }

    public void dialogInsert() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_themphieunhap);

        EditText editSoPhieu2 = (EditText) dialog.findViewById(R.id.editSoPhieu2);
        EditText editNgayLap2 = (EditText) dialog.findViewById(R.id.editNgayLap2);
        Button btnThem = (Button) dialog.findViewById(R.id.btnThem);
        Button btnHuy = (Button) dialog.findViewById(R.id.btnHuy);

        //them du lieu vao spinner
        Spinner spinnerMaKho2 = dialog.findViewById(R.id.spinnerMaKho2);
        Cursor dataKho = helper.GetData("SELECT * FROM Kho");
        ArrayList<Kho> arrayTam = new ArrayList<Kho>();
        ArrayList<String> arrayTenKhoTam = new ArrayList<String>();
        Kho khoTam;
        while (dataKho.moveToNext()) {
            khoTam = new Kho(dataKho.getString(0), dataKho.getString(1));
            arrayTam.add(khoTam);
            arrayTenKhoTam.add(khoTam.getTenKho());
        }
        ArrayAdapter arrayAdapterTam = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayTenKhoTam);
        spinnerMaKho2.setAdapter(arrayAdapterTam);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int soPhieuMoi = 0;
                String ngayLapMoi = String.valueOf(editNgayLap2.getText());
                String maKhoMoi = String.valueOf(arrayTam.get(spinnerMaKho2.getSelectedItemPosition()).getMaKho());
                if (TextUtils.isEmpty(String.valueOf(editSoPhieu2.getText())) || TextUtils.isEmpty(ngayLapMoi) || TextUtils.isEmpty(maKhoMoi)) {
                    Toast.makeText(PhieuNhapActivity.this, "Nội dung cần thêm chưa được nhập", Toast.LENGTH_SHORT).show();

                    return;
                }
                //kiem tra chu cai
                try {
                    soPhieuMoi = Integer.parseInt(String.valueOf(editSoPhieu2.getText()));
                } catch (Exception e) {
                    Toast.makeText(PhieuNhapActivity.this, "Số phiếu là một số nguyên", Toast.LENGTH_SHORT).show();
                    return;
                }
                //kiem tra trung
                Cursor dataPhieuNhap = helper.GetData("SELECT * FROM PhieuNhap");
                ArrayList<PhieuNhap> arrayPhieuNhap = new ArrayList<PhieuNhap>();
                PhieuNhap phieuNhapTam;
                while (dataPhieuNhap.moveToNext()) {
                    phieuNhapTam = new PhieuNhap(dataPhieuNhap.getInt(0), dataPhieuNhap.getString(1), dataPhieuNhap.getString(2));
                    arrayPhieuNhap.add(phieuNhapTam);
                }

                for (int i = 0; i < arrayPhieuNhap.size(); i++) {
                    if (soPhieuMoi == arrayPhieuNhap.get(i).getSoPhieu()) {
                        Toast.makeText(PhieuNhapActivity.this, "Số phiếu bị trùng", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }


                helper.QueryData("INSERT INTO PhieuNhap VALUES ('" + soPhieuMoi + "','" + ngayLapMoi + "', '" + maKhoMoi + "')");
                dialog.dismiss();
                actionGetData();

            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void btnClick(View view) {
        onBackPressed();
    }
}