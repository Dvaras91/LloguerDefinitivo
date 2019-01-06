package com.example.davidvarassolano.lloguermaterialgires;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

public class AdapListInformation extends ArrayAdapter {
    public AdapListInformation(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }
}
