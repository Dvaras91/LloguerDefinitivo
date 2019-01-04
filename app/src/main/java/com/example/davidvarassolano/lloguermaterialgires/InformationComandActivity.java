package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InformationComandActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;

    Intent intent;
    TextView Nomcomanda;
    String nomcomanda,id;
    ListView listmaterial;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_comand);
        Nomcomanda = findViewById(R.id.lbl_nom);
        listmaterial = findViewById(R.id.list_material);
        intent = getIntent();
        if (intent!=null){
            nomcomanda = intent.getStringExtra("name");
            Nomcomanda.setText(nomcomanda);
            id = intent.getStringExtra("id");
        }

        listItems = new ArrayList<>();
        db.collection("Comandas").document(id).collection("items").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){
                    Log.e("LloguerMaterialGires","Firestore Error "+e.toString());
                    return;
                }
                listItems.clear();
                for (DocumentSnapshot doc: documentSnapshots){
                    listItems.add(doc.getString("nombre"));
                }
                adapter.notifyDataSetChanged();
            }
        });


        //listItems.add("Casc");
        //listItems.add("Dissipador");
        //listItems.add("Mosquetons HMS");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems);
        listmaterial.setAdapter(adapter);


    }

    public void ReturnNormalUser(View view) {
        finish();
    }
}
