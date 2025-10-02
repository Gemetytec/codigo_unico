package com.gemetytec.mmoreno.codigounico.Models;

import org.jetbrains.annotations.NotNull;

public class ModelDetalleEntradasPadre {

    String Codigo;
    String Descripcion;
    String Cantidad;
    String Recibido;
    String Producto_Datos;
    String ProductoId;
    String IdDetalleEntrada;
    String Ubicada;

    public ModelDetalleEntradasPadre(String Codigo,String Descripcion,String Cantidad,String Recibido,String IdProducto,String DetalleIdEntrada,String CantUbicada) {
        this.Codigo = Codigo;
        this.Descripcion = Descripcion;
        this.Cantidad = Cantidad;
        this.Recibido = Recibido;
        this.ProductoId = IdProducto;
        this.IdDetalleEntrada = DetalleIdEntrada;
        this.Ubicada = CantUbicada;
    }

    /** set y get **/
    public String getProducto_Datos() {
        return Producto_Datos;
    }
    public String getCodigo() {
        return Codigo;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public String getCantidad() {
        return Cantidad;
    }
    public String getRecibido() {
        return Recibido;
    }
    public String getProductoId() {
        return ProductoId;
    }
    public String getUbicada() {
        return Ubicada;
    }
    public String getIdDetalleEntrada() {
        return IdDetalleEntrada;
    }

}
