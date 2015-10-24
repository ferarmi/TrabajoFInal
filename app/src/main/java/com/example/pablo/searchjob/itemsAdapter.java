package com.example.pablo.searchjob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DOCENTES on 20/10/2015.
 */
public class itemsAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<job> datos;

    public itemsAdapter(Context context, ArrayList<job> datos) {
        super(context, R.layout.job_post_layout, datos);
        this.context = context;
        this.datos = datos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.job_post_layout, null);

        TextView title = (TextView) item.findViewById(R.id.job_post_title);
        title.setText(datos.get(position).getTitle());

        TextView posted_date= (TextView) item.findViewById(R.id.job_post_posted_date);
        posted_date.setText(datos.get(position).getPosted_date());

        // Devolvemos la vista para que se muestre en el ListView.
        return item;
    }

}
