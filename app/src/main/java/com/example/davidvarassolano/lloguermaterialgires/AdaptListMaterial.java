package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdaptListMaterial extends ArrayAdapter<Itemcomandprop> {
    public AdaptListMaterial(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        if (result == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.itemllistmaterial,null);
        }
        TextView txt_cantidad = result.findViewById(R.id.txt_quantitat);
        CheckBox item_check = result.findViewById(R.id.check_item);
        Itemcomandprop item = getItem(position);
        item_check.setText(item.getText());
        item_check.setChecked(item.isChecked());
        txt_cantidad.setText(item.getNumlloguer()+"/"+item.getNumtotal());
        return result;
    }
}
