package com.gemetytec.mmoreno.codigounico.Models;

public class ModelCodigosProductUbicar {


    String CodigoUnico;
    String NoImpresion;
    String Estatus;


    public ModelCodigosProductUbicar(String Codigo,String NoImpresion,String Estatus) {
        this.CodigoUnico = Codigo;
        this.NoImpresion = NoImpresion;
        this.Estatus = Estatus;

    }

    /** set y get **/
    public String getCodigoBrras() {
        return CodigoUnico;
    }
    public String getNoImpresion() {
        return NoImpresion;
    }
    public String getEstatus() {
        return Estatus;
    }


}
