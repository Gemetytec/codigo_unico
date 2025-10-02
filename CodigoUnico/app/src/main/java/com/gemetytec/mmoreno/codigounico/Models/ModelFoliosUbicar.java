package com.gemetytec.mmoreno.codigounico.Models;

import org.jetbrains.annotations.NotNull;

public class ModelFoliosUbicar {

    String Folio;
    String Factura;
    String Origen;
    String Cantidad;
    String IdFolio;


    public ModelFoliosUbicar(String Folio,String Factura,String Origen,String Cantidad,String IdFolio) {
        this.Folio = Folio;
        this.Factura = Factura;
        this.Origen = Origen;
        this.Cantidad = Cantidad;
        this.IdFolio = IdFolio;

    }

    /** set y get **/

    public String getFolio() {
        return Folio;
    }
    public String getFactura() {
        return Factura;
    }
    public String getOrigen() {
        return Origen;
    }
    public String getCantidad() {
        return Cantidad;
    }
    public String getIdFolio() {
        return IdFolio;
    }


    public void setFoliosUbicar(String Folio,String Factura,String Origen,String Cantidad,String IdFolio) {
        this.Folio = Folio;
        this.Factura = Factura;
        this.Origen = Origen;
        this.Cantidad = Cantidad;
        this.IdFolio = IdFolio;

    }

}
