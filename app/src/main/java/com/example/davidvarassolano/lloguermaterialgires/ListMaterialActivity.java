package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListMaterialActivity extends AppCompatActivity {

    private AdaptListMaterial adapter;
    private ArrayList<Itemcomandprop> list_item;
    private ListView list_material;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_material);

        //list_material = findViewById(R.id.list_material);
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
                    list_item.add(new Itemcomandprop(doc.getString("nombre"),doc.getDouble("cantidad").intValue()));
                }
                adapter.notifyDataSetChanged();
            }
        });
        //list_item.add("Mosquetons");
        //list_item.add("Dissipador");
        //list_item.add("Corda 50m");
        //list_item.add("Casc escalada");
        //list_item.add("Neopreno");
        final ListView list_material = findViewById( R.id.list_material );
        adapter = new AdaptListMaterial(this,R.layout.itemllistmaterial,list_item);
        list_material.setAdapter(adapter);

        list_material.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list_item.get( i ).isChecked()==false) {
                    Toast.makeText( ListMaterialActivity.this, String.format( list_item.get( i ).getText() ), Toast.LENGTH_SHORT ).show();
                }
                list_item.get( i ).toggleChecked();
                adapter.notifyDataSetChanged();
            }
        } );
    }


    public void confirmItems(View view) {
        int i = 0;
        ArrayList<String> material;
        material = new ArrayList<>(  );

        Intent data = new Intent(  );

        while (i<list_item.size()){
            if (list_item.get( i ).isChecked()){
                material.add( list_item.get( i ).getText() );
            }
            i++;
        }
        data.putExtra( "material", material);
        setResult( RESULT_OK,data );
        finish();
    }
}
