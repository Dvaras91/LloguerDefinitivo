package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class AccesUserActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText Usuari, Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acces_user);
        Usuari = findViewById(R.id.txt_usuari);
        Pass = findViewById(R.id.txt_psw);


    }


    public void entryApp(View view) {
        final String usuari = Usuari.getText().toString();
        final String pass = Pass.getText().toString();
        db.collection("Usuarios").whereEqualTo("id_usuario",usuari).whereEqualTo("pass",pass).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){
                    Log.e("LloguerMaterialGires","Firestore Error:"+e.toString());
                    ErrorUser();
                    return;
                }
                for (DocumentSnapshot doc: documentSnapshots){
                    if (usuari.equals(doc.getString("id_usuario")) && (pass.equals(doc.getString("pass")))){
                        CorrectUser();

                    }
                    else {
                        ErrorUser();
                    }
                }

            }
        });
    }

    void ErrorUser(){
        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
    }
    void CorrectUser(){
        Intent intent = new Intent(this,NormalUserActivity.class);
        startActivity(intent);
    }
}
