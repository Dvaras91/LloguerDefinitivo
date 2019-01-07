package com.example.davidvarassolano.lloguermaterialgires;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapListInformation extends ArrayAdapter {
    public AdapListInformation(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects); }

    @NonNull
    @Override
    public View getView ( int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View result = convertView;
        if (result == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.itemreturn, null);
        }

        TextView txt_item = (TextView) result.findViewById(R.id.lbl_item);
        TextView txt_quantitat = (TextView) result.findViewById(R.id.lbl_quantitat);
        Itemcomandprop item = (Itemcomandprop) getItem(position);
        txt_item.setText("- "+item.getText());
        String cantidad = Integer.toString(item.getNumtotal());
        txt_quantitat.setText(cantidad);
        return result;
    }
}