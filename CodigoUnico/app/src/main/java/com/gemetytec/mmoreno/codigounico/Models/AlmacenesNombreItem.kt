package com.gemetytec.mmoreno.codigounico.Models

data class AlmacenesNombreItem(
    val Activo: Boolean,
    val Descripcion: String,
    val FechaCreacion: String,
    val FechaModificacion: String,
    val Id: Int,
    val Nombre: String,
    val Sucursal: String,
    val SucursalId: Int,
    val Tipo: String,
    val UsuarioModificacion: String
)