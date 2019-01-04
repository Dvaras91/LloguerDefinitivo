package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListMaterialActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ArrayList<String> list_item;
    private ListView list_material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_material);

        list_material = findViewById(R.id.list_material);
        Intent intent = getIntent();
        list_item = new ArrayList<>();
        list_item.add("Mosquetons");
        list_item.add("Dissipador");
        list_item.add("Corda 50m");
        list_item.add("Casc escalada");
        list_item.add("Neopreno");
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list_item);
        list_material.setAdapter(adapter);
    }

    public void confirmItems(View view) {
        finish();
    }
}
