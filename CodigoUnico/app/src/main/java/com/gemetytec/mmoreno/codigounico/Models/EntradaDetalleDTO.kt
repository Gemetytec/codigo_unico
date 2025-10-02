package com.gemetytec.mmoreno.codigounico.Models

data class EntradaDetalleDTO(
    val CantRecibida: Int,
    val CantUbicada: Int,
    val Cantidad: Int,
    val Codigo: String,
    val Descripcion: String,
    val EntradaId: Int,
    val FolioEntrada: String,
    val Id: Int,
    val Producto: String,
    val ProductoId: Int
)