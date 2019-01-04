package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListMaterialActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ArrayList<String> list_item;
    private ListView list_material;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_material);

        list_material = findViewById(R.id.list_material);
        Intent intent = getIntent();
        list_item = new ArrayList<>();
        db.collection("Catalogo").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null){
                    Log.e("LloguerMaterialGires","Firestore Error: "+e.toString());
                    return;
                }
                list_item.clear();
                for (DocumentSnapshot doc: documentSnapshots){
                    list_item.add(doc.getString("nombre"));
                }
                adapter.notifyDataSetChanged();
            }
        });
        //list_item.add("Mosquetons");
        //list_item.add("Dissipador");
        //list_item.add("Corda 50m");
        //list_item.add("Casc escalada");
        //list_item.add("Neopreno");
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list_item);
        list_material.setAdapter(adapter);
    }

    public void confirmItems(View view) {
        finish();
    }
}
