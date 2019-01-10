package com.example.davidvarassolano.lloguermaterialgires;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NormalUserActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String NOMBRE = "name";
    private static final String ENTREGA = "entrega";
    private static final String USUARI = "usuari";
    private ListView ListEntregades;
    private ListView ListPendents;
    private static final int EDIT_NAME = 3;
    private static final int SEARCH = 2;
    private static final int VIEW = 4;
    private ListcomAdapt adapter;
    private ListCRecollirAdapt adapterrec;
    private ArrayList<Comanda> listcomandes;
    private ArrayList <Comanda> listcomrecollir;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);
        ListEntregades = (ListView)findViewById(R.id.list_entregades);
        ListPendents = (ListView)findViewById(R.id.list_pendents);
        listcomandes = new ArrayList<>(  );
        listcomrecollir = new ArrayList<>();

        // en la pantalla d' usuari s' haura d' ingressar el nom identificador que serà el mateix que
        // posarem en el segon whereEqualto
        db.collection("Comandas").whereEqualTo("entrega",true).whereEqualTo("usuari","paco").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){
                    Log.e("LloguerMaterialGires","Firestore Error:"+e.toString());
                    return;
                }
                listcomandes.clear();
                for (DocumentSnapshot doc: documentSnapshots){
                    Comanda comanda = new Comanda(doc.getString("name"),doc.getString("usuari"),doc.getString("data"));
                    comanda.setId(doc.getId());
                    listcomandes.add(comanda);
                }
                adapter.notifyDataSetChanged();
            }
        });

        // comandes per recollir de la base de dades
        RefreshListPrep();
        //listcomrecollir.add("Savassona");
        //listcomrecollir.add("Benasque");
        //listcomandes.add( "Cova Forat Mico" );
        //listcomandes.add( "Aneto" );
        //listcomandes.add( "Pica d' estats" );
        adapter = new ListcomAdapt(this,R.layout.layoutlistaentregades,listcomandes  );
        adapterrec = new ListCRecollirAdapt(this,R.layout.layoutlistrecollir,listcomrecollir);
        ListEntregades.setAdapter(adapter);
        ListPendents.setAdapter(adapterrec);
        ListEntregades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NormalUserActivity.this,String.format(listcomandes.get(position).name),Toast.LENGTH_SHORT).show();
                viewComanda(String.format(listcomandes.get(position).name),listcomandes.get(position).id);
            }
        });
        ListPendents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NormalUserActivity.this,String.format(listcomrecollir.get(position).name),Toast.LENGTH_SHORT).show();
                novacomanda(String.format(listcomrecollir.get(position).name),listcomrecollir.get(position).id);
            }
        });



    }

    public void newComand(final View view) {
        AlertDialog.Builder novacomanda = new AlertDialog.Builder(this);
        novacomanda.setMessage("Introduïu el nom de la comanda");
        novacomanda.setTitle("Comanda");
        final EditText Nomcomanda = new EditText(this);
        novacomanda.setView(Nomcomanda);
        novacomanda.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String nomcomanda = Nomcomanda.getText().toString();
                if (nomcomanda.isEmpty()){
                    Toast.makeText(NormalUserActivity.this,"Posa un nom vago",Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(NormalUserActivity.this,"Creant comanda"+" "+nomcomanda,Toast.LENGTH_SHORT).show();
                    //Crear la nova activitat
                    //DocumentReference comRef = db.collection("Comandas").document(nomcomanda);
                    // Pensar millor com ho faig
                    Map<String,Object> com = new HashMap<>();
                    com.put(NOMBRE,nomcomanda);
                    com.put(USUARI,"paco");
                    com.put(ENTREGA,false);
                    db.collection("Comandas").document().set(com).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            novacomanda(nomcomanda,"hola"); //Millorar
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NormalUserActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            Log.e("LloguerMaterialGires","Error Firebase: "+e.toString());
                        }
                    });


                }

            }
        });
        novacomanda.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NormalUserActivity.this,"Comanda cancel·lada",Toast.LENGTH_SHORT).show();
            }
        });
        novacomanda.show();


    }
    public void novacomanda (String nomcomanda,String id){
        Intent intent = new Intent(this,EditCommandActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("name",nomcomanda);

        startActivityForResult(intent,EDIT_NAME);
    }
    public void viewMaterial (View view){
        Intent intent = new Intent(this,ListMaterialActivity.class);
        startActivityForResult(intent,SEARCH);
    }
    public void viewComanda (String nomcomanda, String id){
        Intent intent = new Intent(this,InformationComandActivity.class);
        intent.putExtra ("id",id);
        intent.putExtra("name",nomcomanda);
        startActivityForResult(intent, VIEW);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case EDIT_NAME:
                if (resultCode==RESULT_OK){
                    RefreshListPrep();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void RefreshListPrep(){
        // comandes per recollir de la base de dades
        db.collection("Comandas").whereEqualTo("entrega",false).whereEqualTo("usuari","paco").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){
                    Log.e("LloguerMaterialGires","Firestore Error:"+e.toString());
                    return;
                }
                listcomrecollir.clear();
                for (DocumentSnapshot doc: documentSnapshots){
                    Comanda comanda = new Comanda(doc.getString("name"),doc.getString("usuari"),doc.getString("data"));
                    comanda.setId(doc.getId());
                    listcomrecollir.add(comanda);
                }
                adapterrec.notifyDataSetChanged();
            }
        });
    }



}
