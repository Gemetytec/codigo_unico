package com.gemetytec.mmoreno.codigounico.Models;

public class AlmacenFolios {

    String AlmacenFolio_Selecc;
    String Id_Selecc;


    public AlmacenFolios() {

    }

    /** set y get **/
    public String getAlmacenFolio() {
        return AlmacenFolio_Selecc;
    }
    public String getAlmacenFolioId() {
        return Id_Selecc;
    }
    public void setAlmacenFolio(String Seleccion_AlmacenFolio,String Seleccion_Id) {
        this.AlmacenFolio_Selecc = Seleccion_AlmacenFolio;
        this.Id_Selecc = Seleccion_Id;
    }

}
