package com.gemetytec.mmoreno.codigounico.Models

data class Entrada(
    val Activo: Boolean,
    val AlmacenId: Int,
    val Cantidad: Int,
    val Comentarios: String,
    val EntradaDetalleDTO: Any,
    val EstatusEntrada: Int,
    val EstatusEntradaS: String,
    val Factura: String,
    val FechaModificacion: String,
    val Folio: String,
    val Id: Int,
    val Origen: String,
    val TipoMovimiento: String
)