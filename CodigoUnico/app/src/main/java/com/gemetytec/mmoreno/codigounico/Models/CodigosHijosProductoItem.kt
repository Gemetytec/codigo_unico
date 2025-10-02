package com.gemetytec.mmoreno.codigounico.Models

data class CodigosHijosProductoItem(
    val CodigosUnicos: List<CodigosUnicoX>,
    val IdEntradaDetalle: Int,
    val RespuestaEstado: Boolean
)