package com.nhom2.qly_nhap_kho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom2.qly_nhap_kho.adapter.KhoAdapter;
import com.nhom2.qly_nhap_kho.adapter.VatTuAdapter;
import com.nhom2.qly_nhap_kho.model.Kho;
import com.nhom2.qly_nhap_kho.model.VatTu;

import java.util.ArrayList;
import java.util.List;

public class KhoActivity extends AppCompatActivity {
    NhapKhoHelper nhapKhoHelper;
    KhoAdapter khoAdapter;
    RecyclerView recyclerViewKho;
    List<Kho> arrayKho = new ArrayList<>();
    Kho kho;
    FloatingActionButton floatingActionButtonKho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kho);
        nhapKhoHelper=new NhapKhoHelper(this);
        anhXa();
        actionGetData();

        floatingActionButtonKho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInsert();
            }
        });
    }

    private void anhXa() {
        recyclerViewKho = findViewById(R.id.recyclerViewKho);
        floatingActionButtonKho=findViewById(R.id.floatingActionButtonKho);
    }
    public void dialogInsert() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_themkho);

        EditText editMaKho = (EditText) dialog.findViewById(R.id.editMaKho);
        EditText editTenKho = (EditText) dialog.findViewById(R.id.editTenKho);
        Button btnThem = (Button) dialog.findViewById(R.id.btnThemKho);
        Button btnHuy = (Button) dialog.findViewById(R.id.btnHuyKho);


        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maKho = String.valueOf(editMaKho.getText());
                String tenKho = String.valueOf(editTenKho.getText());
                if (TextUtils.isEmpty(maKho) || TextUtils.isEmpty(tenKho) ) {
                    Toast.makeText(KhoActivity.this, "Nội dung cần thêm chưa được nhập", Toast.LENGTH_SHORT).show();

                    return;
                }
                //kiem tra trung
                Cursor dataKho = nhapKhoHelper.GetData("SELECT * FROM Kho");
                ArrayList<Kho> arrayKho = new ArrayList<Kho>();
                Kho khoTam;
                while (dataKho.moveToNext()) {
                    khoTam = new Kho(dataKho.getString(0), dataKho.getString(1));
                    arrayKho.add(khoTam);
                }

                for (int i = 0; i < arrayKho.size(); i++) {
                    if(maKho==arrayKho.get(i).getMaKho()){
                        Toast.makeText(KhoActivity.this, "Mã kho bị trùng", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }
                for (int i = 0; i < arrayKho.size(); i++) {
                    if(tenKho==arrayKho.get(i).getTenKho()){
                        Toast.makeText(KhoActivity.this, "Tên kho bị trùng", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }


                nhapKhoHelper.QueryData("INSERT INTO Kho VALUES ('" + maKho + "','" + tenKho + "')");
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
    public void dialogUpdate(String maKho, String tenKho) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_suakho);

        //anh xa
        EditText editSuaMaKho = (EditText) dialog.findViewById(R.id.editSuaMaKho);
        EditText editSuaTenKho = (EditText) dialog.findViewById(R.id.editSuaTenKho);

        Button btnSuaKho = (Button) dialog.findViewById(R.id.btnSuaKho);
        Button btnXoaKho = (Button) dialog.findViewById(R.id.btnXoaKho);


        //set du lieu
        editSuaMaKho.setText(maKho);
        editSuaTenKho.setText(tenKho);


        //bat su kien nut bam
        btnSuaKho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String TenKhoMoi = String.valueOf(editSuaTenKho.getText());
                if (TextUtils.isEmpty(TenKhoMoi)) {
                    Toast.makeText(KhoActivity.this, "Nội dung cần sửa chưa được nhập", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                nhapKhoHelper.QueryData("UPDATE Kho SET TenKho='" + TenKhoMoi + "' WHERE MaKho='" + maKho + "'");
                dialog.dismiss();
                actionGetData();
            }
        });

        btnXoaKho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiem tra kho co phieu nhap khong
                //kiem tra trung
                Cursor data = nhapKhoHelper.GetData("SELECT * FROM PhieuNhap WHERE MaKho='"+maKho+"'");
                while (data.moveToNext()) {
                    Toast.makeText(KhoActivity.this, "Kho có phiếu nhập không thể xóa", Toast.LENGTH_SHORT).show();

                    return;
                }

                nhapKhoHelper.QueryData("DELETE FROM Kho WHERE MaKho='" + maKho + "'");
                dialog.dismiss();
                actionGetData();
            }
        });

        dialog.show();
    }
    private void actionGetData(){
        arrayKho.clear();

        Cursor dataKho = nhapKhoHelper.GetData("SELECT * FROM Kho");

        while (dataKho.moveToNext()) {
            kho = new Kho(dataKho.getString(0), dataKho.getString(1));
            arrayKho.add(kho);
        }



        recyclerViewKho.setHasFixedSize(true);
        recyclerViewKho.setLayoutManager(new LinearLayoutManager(KhoActivity.this, LinearLayoutManager.VERTICAL, false));

        khoAdapter = new KhoAdapter(KhoActivity.this, arrayKho);
        recyclerViewKho.setAdapter(khoAdapter);
    }
}