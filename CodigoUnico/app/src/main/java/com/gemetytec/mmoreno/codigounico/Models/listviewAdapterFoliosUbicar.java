package com.gemetytec.mmoreno.codigounico.Models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gemetytec.mmoreno.codigounico.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class listviewAdapterFoliosUbicar extends ArrayAdapter<ModelFoliosUbicar> implements View.OnClickListener{



    private ArrayList<com.gemetytec.mmoreno.codigounico.Models.ModelFoliosUbicar> dataSet;
    Context mContext;



    // View lookup cache
    private static class ViewHolder {
        TextView FolioUbicar, FacturaUbicar,OrigenUbicar,CantidadUbicar;
      //  ImageView info;
    }

    public listviewAdapterFoliosUbicar(ArrayList<com.gemetytec.mmoreno.codigounico.Models.ModelFoliosUbicar> data, Context context) {
        super(context, R.layout.listview_row_folios_ubicar, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View view) {

        int position=(Integer) view.getTag();
        Object object= getItem(position);
        ModelFoliosUbicar dataModel=(ModelFoliosUbicar)object;

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el elemento de datos para esta posición
        ModelFoliosUbicar dataModel = getItem(position);
        // Compruebe si se está reutilizando una vista existente; de lo contrario, infle la vista
        ViewHolder viewHolder; // ver el caché de búsqueda almacenado en la etiqueta

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_row_folios_ubicar, parent, false);
            viewHolder.FolioUbicar = (TextView) convertView.findViewById(R.id.FolioUbic);
            viewHolder.FacturaUbicar = (TextView) convertView.findViewById(R.id.FacturaUbic);
            viewHolder.OrigenUbicar = (TextView) convertView.findViewById(R.id.OrigenUbic);
            viewHolder.CantidadUbicar = (TextView) convertView.findViewById(R.id.CantidadUbic);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.FolioUbicar.setText(dataModel.getFolio());
        viewHolder.FacturaUbicar.setText(dataModel.getFactura());
        viewHolder.OrigenUbicar.setText(dataModel.getOrigen());
        viewHolder.CantidadUbicar.setText(dataModel.getCantidad());

       /* if(dataModel.getCantidad() != dataModel.getRecibido()){

        }else if(dataModel.getCantidad() == dataModel.getRecibido()){
            viewHolder.CodigoProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.DescripcionProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.CantidadProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.RecibidoProduct.setTextColor(Color.parseColor("#2aa204"));

        } */

        // Return the completed view to render on screen
        return convertView;
    }

}
