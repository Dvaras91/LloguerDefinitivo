package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdaptListEditComand extends ArrayAdapter {
    public AdaptListEditComand(@NonNull Context context, int resource, @NonNull List objects) {
        super( context, resource, objects );
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View result = convertView;
        if (result==null){
            LayoutInflater inflater =LayoutInflater.from( getContext() );
            result = inflater.inflate( R.layout.itemedit,null );

        }
        CheckBox item_comand = (CheckBox) result.findViewById( R.id.check_item );
        TextView item_quantitat = (TextView) result.findViewById( R.id.txt_quantitat );
        Itemcomandprop item = (Itemcomandprop) getItem( position );
        item_comand.setText( item.getText() );
        item_comand.setChecked( item.isChecked() );
        if (item.isChecked()){
            item_comand.setPaintFlags(item_comand.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }else{
            item_comand.setPaintFlags(item_comand.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG );
        }
        String cantidad = Integer.toString(item.getNumlloguer());
        item_quantitat.setText( cantidad);

        return result;
    }
}
