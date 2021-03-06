package com.example.davidvarassolano.lloguermaterialgires;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditCommandActivity extends AppCompatActivity {


    private static final String FILENAME = "rent_list";
    private static final int MAX_BYTES = 8000;

    private static final String USUARI = "usuari";
    private static final String DATA = "data";
    private static final String ENTREGA = "entrega";
    private static final String NOM = "name";
    private static final String NOMBRE = "nombre";
    private static final String CANTIDAD = "cantidad";
    private static final String PRECIOITEM = "precio";

    private AdaptListEditComand adapter;
    private static final int EDIT_NAME = 1; //Anar a ListMaterialActivity per sel·lecionar més items a la comanda
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Itemcomandprop> listmaterial;
    private ArrayList<Itemcomandprop> list_item;




    Intent intent;
    TextView Nomcomanda, Preu;
    String nomcomanda,id;
    ListView listItem;


    public void save_list (){
        try {
            FileOutputStream fos= openFileOutput(FILENAME, MODE_PRIVATE); //Obtenemos el fichero
            for (int i=0; i < listmaterial.size(); i++){
                Itemcomandprop it = listmaterial.get(i);
                String line = String.format("%s;%b\n", it.getText(), it.isChecked() );
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) { //Detecta errores a la hora de obtener el fichero
            Log.e("LloguerDefinitvo","save_list: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("LloguerDefinitvo","save_list: IOException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        }


    }

    public void read_list (){
        listmaterial = new ArrayList<>();
        try {
            FileInputStream fis= openFileInput(FILENAME);
            byte buffer [] = new byte[MAX_BYTES];
            int nread = fis.read(buffer);
            String content =  new String(buffer, 0, nread);
            String[] lines = content.split("\n");
            for (String line : lines) {
                String[] parts = line.split(";");
                listmaterial.add(new Itemcomandprop(parts[0], parts[1].equals("true")));
            }
            fis.close();
        } catch (FileNotFoundException e) { //para cuando no encuentra el fichero, primer uso
            Log.e("LloguerDefinitivo", "read_list: FileNotFoundException");
        } catch (IOException e) {
            Log.e("LloguerDefinitvo","read_list: IOException");
            Toast.makeText(this, R.string.cannot_reat, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        save_list();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_command);
        //Icone:
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        //Referències:
        Nomcomanda = findViewById(R.id.lbl_nomcomanda);
        listItem = findViewById(R.id.list_items);
        Preu = findViewById(R.id.lbl_preupagar);
        

        //Info que ve de l' activitat NormalUser:
        intent = getIntent();
        if (intent != null){
            nomcomanda = intent.getStringExtra("name");
            id = intent.getStringExtra("id");
            Nomcomanda.setText("Nom de la comanda: " +nomcomanda);
        }



        //Carreguem comanda de la base de dades:
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
                            listmaterial.add(new Itemcomandprop(doc.getString("nombre"),doc.getDouble("cantidad").intValue(),1,doc.getDouble("precio").intValue()));
                            //listmaterial.add(new Itemcomandprop(doc.getString("nombre"),doc.getDouble("cantidad").intValue()));
                        }
                        adapter.notifyDataSetChanged();
                        modificarpreutotal();

                    }
                });

        listmaterial = new ArrayList<>();
        //listItems.add("Casc");
        //listItems.add("Neopreno");
        //listItems.add("Arnes");
        adapter = new AdaptListEditComand(this,R.layout.itemedit,listmaterial);
        listItem.setAdapter(adapter);
        listItem.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                listmaterial.get( pos ).toggleChecked();

                adapter.notifyDataSetChanged();
            }
        } );
        listItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Modificar quantitat el·liminar element.
                modificarquantitatitems(position);

                return true;
            }
        });

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
                    ArrayList<Integer> cantidad,precio,cantidadtotal;
                    cantidad = new ArrayList<>();
                    precio = new ArrayList<>();
                    cantidadtotal = new ArrayList<>();

                    material = data.getStringArrayListExtra( "material" );
                    //ArrayList<Itemcomandprop> materialnew = new ArrayList<>(data.getParcelableArrayListExtra("materialnew"));
                    cantidad = data.getIntegerArrayListExtra("cantidad");
                    precio = data.getIntegerArrayListExtra("precio");
                    cantidadtotal = data.getIntegerArrayListExtra("ctotal");

                    int i = 0;
                    boolean coincide = false;
                    //Agregar material en la lista. Si el material ya existe, no lo agregará
                    while (i<material.size()){
                        coincide = false;
                        for (int b = 0;b<=listmaterial.size()-1;b++){
                            String item = listmaterial.get(b).getText().toString();
                            if (material.get(i).equals(item)){
                                coincide = true;
                            }
                        }
                        if (!coincide){
                        listmaterial.add( new Itemcomandprop( material.get( i ) ,cantidad.get(i),cantidadtotal.get(i),precio.get(i)) );}
                        i++;

                    }

                    //noumaterial = data.getStringExtra( "material" );
                    //listmaterial.add(new Itemcomandprop( noumaterial ));

                    adapter.notifyDataSetChanged();
                    listItem.smoothScrollToPosition( listmaterial.size()-1 );

                    modificarpreutotal();   //actualitzem el preu del textview
                }
            default:
                super.onActivityResult( requestCode, resultCode, data );
        }}

    public void deleteItems(View view) {
        //final WriteBatch batch = db.batch(); Provar de fer - ho amb un batch
        int i = 0;
        while (i<listmaterial.size()){
            if (listmaterial.get( i ).isChecked()){
                listmaterial.remove( i );
            } else {
                i++;
            }
        }
        adapter.notifyDataSetChanged();
        modificarpreutotal(); //actualitzem el preu del textview
        /*db.collection("Catalogo").whereEqualTo("nombre","Dissipador").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    QuerySnapshot document = task.getResult();
                    Toast.makeText(EditCommandActivity.this,Double.toString(document.getDocuments().get(0).getDouble("cantidad")),Toast.LENGTH_SHORT).show();
                    //document.getDocuments().get(0).getDouble("cantidad");
                    //Toast.makeText(EditCommandActivity.this,document.getString("cantidad")

                } else {
                    Log.d("LloguerMaterialGires",task.getException().toString());
                }
            }
        });*/
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
        //Prova transacció:
        /*for (int d = 0;d<listmaterial.size();d++){
            DocumentReference DocCatalogo = db.collection("Catalogo").document(listmaterial.get(d).);
        }
        DocumentReference DocCatalogo = db.collection("Catalogo").document();
        db.runTransaction(new Transaction.Function<Integer>() {
            @Nullable
            @Override
            public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                //DocumentSnapshot snapshot = transaction.get(DocCatalogo);
                for (DocumentSnapshot doc:)
                return null;
            }
        });*/
        /*list_item = new ArrayList<>();
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

            }
        });*/



        DocumentReference comRef = db.collection( "Comandas" ).document(  );

        WriteBatch batch = db.batch();
        Map<String,Object> comand = new HashMap<>(  );
        comand.put( USUARI,"paco" );
        comand.put( DATA,fecha );
        comand.put( ENTREGA,false );
        comand.put( NOM,nomcomanda );
        batch.set(comRef, comand);



        for (int d = 0;d < listmaterial.size(); d++) {
            DocumentReference comCatalogo = db.collection("Catalogo").document(listmaterial.get(d).getText());
            Map<String,Object> cantidad = new HashMap<>();
            Map<String,Object> items = new HashMap<>(  );
            cantidad.put("cantidad",(listmaterial.get(d).getNumtotal()-listmaterial.get(d).getNumlloguer()));
            items.put( NOMBRE, listmaterial.get( d ).getText() );
            items.put( CANTIDAD, listmaterial.get( d ).getNumlloguer() );
            items.put(PRECIOITEM,listmaterial.get(d).getPrecio());
            batch.set(comRef.collection("items").document(), items);
            batch.update(comCatalogo,"cantidad",cantidad);

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

    //Metode per si cliques al botó enrere et pregunti si vols tancar l' activitat sense guardar la informació o vols seguir en l' activitat.
    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro que deseas ir para atrás?");
        builder.setTitle("Atención");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditCommandActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //Metode que ens servirà per modificar el contingut del textview que ens mostra en tot moment el preu de la nostra comanda.
    public void modificarpreutotal (){
        int i = 0;
        int preu = 0;
        while (i<listmaterial.size()){
            preu = preu + listmaterial.get(i).getNumtotal()*listmaterial.get(i).getPrecio();
            i = i + 1;
        }
        String preucomanda = Integer.toString(preu);
        Preu.setText(preucomanda+"€");
    }
    public void modificarquantitatitems (final int position){
        //Metode per modificar la quantitat que volem llogar
        AlertDialog.Builder modificar = new AlertDialog.Builder(this);
        modificar.setTitle("Modificar quantitat de material")
                .setMessage("Posa la nova quantitat que vols llogar:");
        final EditText edquantitat = new EditText(this);
        final int quant=1;
        edquantitat.setInputType(InputType.TYPE_CLASS_NUMBER);
        modificar.setView(edquantitat);
        modificar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String quantitat = edquantitat.getText().toString();
                if (quantitat.isEmpty()){
                    return;
                } else {
                        //Toast.makeText(EditCommandActivity.this,edquantitat.getText().toString(),Toast.LENGTH_SHORT).show();
                        String nommaterial = listmaterial.get(position).getText().toString();
                    db.collection("Catalogo").whereEqualTo("nombre",nommaterial).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                QuerySnapshot document = task.getResult();
                                Toast.makeText(EditCommandActivity.this,Double.toString(document.getDocuments().get(0).getDouble("cantidad")),Toast.LENGTH_SHORT).show();
                                //document.getDocuments().get(0).getDouble("cantidad");
                                //Toast.makeText(EditCommandActivity.this,document.getString("cantidad")
                                int quant =  document.getDocuments().get(0).getDouble("cantidad").intValue();
                                if (quant>=Integer.parseInt(edquantitat.getText().toString())){
                                    listmaterial.get(position).setNumlloguer(Integer.parseInt(edquantitat.getText().toString()));
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(EditCommandActivity.this,"Has sobrepassat la quantitat màxima",Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Log.d("LloguerMaterialGires",task.getException().toString());
                            }
                        }
                    });


                }

            }
        })
                .setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        modificar.show();

    }

}
