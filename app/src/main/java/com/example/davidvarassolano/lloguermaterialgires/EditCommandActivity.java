package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditCommandActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Intent intent;
    TextView Nomcomanda;
    String nomcomanda,id;
    ListView listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_command);
        Nomcomanda = findViewById(R.id.lbl_nomcomanda);
        listItem = findViewById(R.id.list_items);
        intent = getIntent();
        if (intent != null){
            nomcomanda = intent.getStringExtra("name");
            id = intent.getStringExtra("id");
            Nomcomanda.setText("Nom de la comanda: " +nomcomanda);
        }

        db.collection("Comandas").document(id).collection("items").addSnapshotListener(this
                , new EventListener<QuerySnapshot>() {
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


        listItems = new ArrayList<>();
        //listItems.add("Casc");
        //listItems.add("Neopreno");
        //listItems.add("Arnes");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems);
        listItem.setAdapter(adapter);

    }

    public void ConfirmComanda(View view) {
        finish();
        Toast.makeText(EditCommandActivity.this,"hola",Toast.LENGTH_SHORT).show();
    }
}
