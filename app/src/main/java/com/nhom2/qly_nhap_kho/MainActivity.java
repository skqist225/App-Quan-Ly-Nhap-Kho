package com.nhom2.qly_nhap_kho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nhom2.qly_nhap_kho.adapter.CustomGridAdapter;
import com.nhom2.qly_nhap_kho.model.GridItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NhapKhoHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = NhapKhoHelper.getInstance(this);

        List<GridItem> image_details = getListData();
        final GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new CustomGridAdapter(this, image_details));


        // When the user clicks on the GridItem
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(MainActivity.this, KhoActivity.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this, PhieuNhapActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(MainActivity.this, VatTuActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(MainActivity.this, ListBaoCaoActivity.class);
                        startActivity(intent3);
                        break;
                }
            }
        });
    }

    private List<GridItem> getListData() {
        List<GridItem> list = new ArrayList<GridItem>();
        GridItem gridItem = new GridItem("Kho", "warehouse_64px");
        GridItem gridItem1 = new GridItem("Phiếu nhập", "receipt_64px");
        GridItem gridItem2 = new GridItem("Vật tư", "commodity_64px");
        GridItem gridItem3 = new GridItem("Báo cáo", "presentation_64px");

        list.add(gridItem);
        list.add(gridItem1);
        list.add(gridItem2);
        list.add(gridItem3);

        return list;
    }


}