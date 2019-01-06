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

    private AdaptListEditComand adapter;
    private static final int EDIT_NAME = 1; //Anar a ListMaterialActivity per sel·lecionar més items a la comanda
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Itemcomandprop> listmaterial;

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
                        listmaterial.clear();
                        for (DocumentSnapshot doc: documentSnapshots){
                            listmaterial.add(new Itemcomandprop(doc.getString("nombre"),doc.getDouble("cantidad").intValue()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        listmaterial = new ArrayList<>();
        //listItems.add("Casc");
        //listItems.add("Neopreno");
        //listItems.add("Arnes");
        adapter = new AdaptListEditComand(this,android.R.layout.simple_list_item_1,listmaterial);
        listItem.setAdapter(adapter);

    }

    public void addItems(View view) {
        //Afegir elements a la comanda
        Intent intent = new Intent(this,ListMaterialActivity.class);
        startActivityForResult(intent,EDIT_NAME);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case EDIT_NAME:
                if (resultCode==RESULT_OK){
                    ArrayList<String> material = new ArrayList<>(  );
                    material = data.getStringArrayListExtra( "material" );
                    int i = 0;
                    while (i<material.size()){
                        listmaterial.add( new Itemcomandprop( material.get( i ) ) );
                        i++;

                    }
                    //noumaterial = data.getStringExtra( "material" );
                    //listmaterial.add(new Itemcomandprop( noumaterial ));

                    adapter.notifyDataSetChanged();
                    listItem.smoothScrollToPosition( listmaterial.size()-1 );
                }
            default:
                super.onActivityResult( requestCode, resultCode, data );
        }}

    public void deleteItems(View view) {
        int i = 0;
        while (i<listmaterial.size()){
            if (listmaterial.get( i ).isChecked()){
                listmaterial.remove( i );
            } else {
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void ConfirmComanda(View view) {
        finish();
        Toast.makeText(EditCommandActivity.this,"hola",Toast.LENGTH_SHORT).show();
    }

    private void guardaComanda(int year, int month, int dayOfMonth){

    }

}
