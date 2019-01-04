package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListCRecollirAdapt extends ArrayAdapter<Comanda> {

    public ListCRecollirAdapt(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        if (result==null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.layoutlistrecollir,null);
        }
        TextView Nomcomanda = (TextView) result.findViewById(R.id.lbl_nom);
        Comanda com = getItem(position);
        Nomcomanda.setText(com.name);
        //String nom = getItem(position).toString();
        //Nomcomanda.setText(nom);
        return result;
    }
}
