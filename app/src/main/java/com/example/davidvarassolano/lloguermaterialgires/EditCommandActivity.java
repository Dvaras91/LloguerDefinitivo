package com.example.davidvarassolano.lloguermaterialgires;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditCommandActivity extends AppCompatActivity {

    private static final String USUARI = "usuari";
    private static final String DATA = "data";
    private static final String ENTREGA = "entrega";
    private static final String NOM = "name";
    private static final String NOMBRE = "nombre";
    private static final String CANTIDAD = "cantidad";

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
        Toast.makeText(EditCommandActivity.this,id,Toast.LENGTH_SHORT).show();

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
        listItem.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                listmaterial.get( pos ).toggleChecked();
                adapter.notifyDataSetChanged();
            }
        } );

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
        final Calendar calendar = Calendar.getInstance();
        int dia = calendar.get( Calendar.DAY_OF_MONTH);
        int mes = calendar.get( Calendar.MONTH );
        int ano = calendar.get( Calendar.YEAR );
        DatePickerDialog datePickerDialog = new DatePickerDialog( EditCommandActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                guardaComanda(year, month, dayOfMonth);
            }
        },ano,mes,dia );
        Toast.makeText(this,nomcomanda,Toast.LENGTH_SHORT).show();
        datePickerDialog.show();
    }

    private void guardaComanda(int year, int month, int dayOfMonth){
        final String fecha = dayOfMonth+"/"+(month+1)+"/"+year;
        //Intent data = new Intent(  );
        //data.putExtra( "fecha",fecha );
        //setResult( RESULT_OK,data );

        final WriteBatch batch = db.batch();


        final DocumentReference comRef = db.collection( "Comandas" ).document( id );
        //comRef.collection("items").document().delete(); //Prova per el·liminar tots el
        //Toast.makeText(this,"borra",Toast.LENGTH_SHORT).show();
        batch.delete(comRef);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CompleteEraseDocument(fecha);
                //Intent data = new Intent();
                //data.putExtra( "fecha",fecha );
                //setResult(RESULT_OK,data);
                //finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCommandActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                Log.d("ERROR",e.toString());
            }
        });


    }
    public void CompleteEraseDocument(String fecha){
        DocumentReference comRef = db.collection( "Comandas" ).document(  );
        WriteBatch batch = db.batch();
        Map<String,Object> comand = new HashMap<>(  );
        comand.put( USUARI,"paco" );
        comand.put( DATA,fecha );
        comand.put( ENTREGA,false );
        comand.put( NOM,nomcomanda );
        batch.set(comRef, comand);

        for (int d = 0;d < listmaterial.size(); d++) {
            Map<String,Object> items = new HashMap<>(  );

            items.put( NOMBRE, listmaterial.get( d ).getText() );
            items.put( CANTIDAD, listmaterial.get( d ).getNumlloguer() );
            batch.set(comRef.collection("items").document(), items);
        }

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent data = new Intent();
                setResult(RESULT_OK,data);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCommandActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                Log.d("ERROR",e.toString());
            }
        });
    }

}
