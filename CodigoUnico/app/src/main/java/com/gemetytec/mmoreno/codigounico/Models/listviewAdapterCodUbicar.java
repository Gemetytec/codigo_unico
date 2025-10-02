package com.gemetytec.mmoreno.codigounico.Models;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gemetytec.mmoreno.codigounico.R;

import java.util.ArrayList;

public class listviewAdapterCodUbicar extends ArrayAdapter<ModelCodigosProductUbicar> implements View.OnClickListener {


    private ArrayList<ModelCodigosProductUbicar> dataSet;
    Context mContext;



    // View lookup cache
    private static class ViewHolder {
        TextView CodigoBarras, NoImpresion,Estatus;
        //  ImageView info;
    }

    public listviewAdapterCodUbicar(ArrayList<com.gemetytec.mmoreno.codigounico.Models.ModelCodigosProductUbicar> data, Context context) {
        super(context, R.layout.listview_row_product_ubicar, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View view) {

        int position=(Integer) view.getTag();
        Object object= getItem(position);
        ModelCodigosProductUbicar dataModel=(ModelCodigosProductUbicar)object;

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el elemento de datos para esta posición
        ModelCodigosProductUbicar dataModel = getItem(position);
        // Compruebe si se está reutilizando una vista existente; de lo contrario, infle la vista
        listviewAdapterCodUbicar.ViewHolder viewHolder; // ver el caché de búsqueda almacenado en la etiqueta

        final View result;

        if (convertView == null) {

            viewHolder = new listviewAdapterCodUbicar.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_row_product_ubicar, parent, false);
            viewHolder.CodigoBarras = (TextView) convertView.findViewById(R.id.CodigoBarras);
            viewHolder.NoImpresion = (TextView) convertView.findViewById(R.id.NoImpresiones);
            viewHolder.Estatus = (TextView) convertView.findViewById(R.id.Estatus);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (listviewAdapterCodUbicar.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.CodigoBarras.setText(dataModel.getCodigoBrras());
        viewHolder.NoImpresion.setText(dataModel.getNoImpresion());
        viewHolder.Estatus.setText(dataModel.getEstatus());

        if(dataModel.getEstatus().contentEquals("Entrada validada") && !dataModel.getEstatus().contentEquals("Entrada ubicada")){
           // System.out.println(dataModel.getCodigoBrras()+"- Entrada validada");

            viewHolder.CodigoBarras.setTextColor(Color.parseColor("#737373"));
            viewHolder.NoImpresion.setTextColor(Color.parseColor("#737373"));
            viewHolder.Estatus.setTextColor(Color.parseColor("#737373"));

        }else if(dataModel.getEstatus().contentEquals("Entrada ubicada") && !dataModel.getEstatus().contentEquals("Entrada validada")){
           // System.out.println(dataModel.getCodigoBrras()+"- Entrada ubicada");

            viewHolder.CodigoBarras.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.NoImpresion.setTextColor(Color.parseColor("#2aa204"));
            viewHolder.Estatus.setTextColor(Color.parseColor("#2aa204"));

        }

        // Return the completed view to render on screen
        return convertView;
    }
}
