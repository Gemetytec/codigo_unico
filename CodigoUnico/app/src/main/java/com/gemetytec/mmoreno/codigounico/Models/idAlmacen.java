package com.gemetytec.mmoreno.codigounico.Models;

public class idAlmacen {

    String IdAlmacen_Selecc;
    String Almacen_Selecc;

    public idAlmacen() {

    }

    /** set y get **/
    public String getIdAlmacen() {
        return IdAlmacen_Selecc;
    }
    public String getAlmacen() {
        return Almacen_Selecc;
    }
    public void setIdAlmacen(String Seleccion_Almacen,String Seleccion_IdAlmacen) {
        this.Almacen_Selecc = Seleccion_Almacen;
        this.IdAlmacen_Selecc = Seleccion_IdAlmacen;
    }


}
