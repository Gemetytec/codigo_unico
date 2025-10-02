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

public class listviewAdapter extends ArrayAdapter<ModelDetalleEntradasPadre> implements View.OnClickListener{



    private ArrayList<com.gemetytec.mmoreno.codigounico.Models.ModelDetalleEntradasPadre> dataSet;
    Context mContext;



    // View lookup cache
    private static class ViewHolder {
        TextView CodigoProduct, DescripcionProduct,CantidadProduct,RecibidoProduct;
      //  ImageView info;
    }

    public listviewAdapter(ArrayList<com.gemetytec.mmoreno.codigounico.Models.ModelDetalleEntradasPadre> data, Context context) {
        super(context, R.layout.listview_row, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View view) {

        int position=(Integer) view.getTag();
        Object object= getItem(position);
        ModelDetalleEntradasPadre dataModel=(ModelDetalleEntradasPadre)object;

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el elemento de datos para esta posición
        ModelDetalleEntradasPadre dataModel = getItem(position);
        // Compruebe si se está reutilizando una vista existente; de lo contrario, infle la vista
        ViewHolder viewHolder; // ver el caché de búsqueda almacenado en la etiqueta

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_row, parent, false);
            viewHolder.CodigoProduct = (TextView) convertView.findViewById(R.id.CodigoProduct);
            viewHolder.DescripcionProduct = (TextView) convertView.findViewById(R.id.DescripcionProduct);
            viewHolder.CantidadProduct = (TextView) convertView.findViewById(R.id.CantidadProduct);
            viewHolder.RecibidoProduct = (TextView) convertView.findViewById(R.id.RecibidoProduct);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.CodigoProduct.setText(dataModel.getCodigo());
        viewHolder.DescripcionProduct.setText(dataModel.getDescripcion());
        viewHolder.CantidadProduct.setText(dataModel.getCantidad());
        viewHolder.RecibidoProduct.setText(dataModel.getRecibido());

        if(dataModel.getCantidad() != dataModel.getRecibido()){

        }else if(dataModel.getCantidad() == dataModel.getRecibido()){
            viewHolder.CodigoProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.DescripcionProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.CantidadProduct.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.RecibidoProduct.setTextColor(Color.parseColor("#2aa204"));
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
