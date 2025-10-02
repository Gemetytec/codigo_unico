package com.gemetytec.mmoreno.codigounico

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gemetytec.mmoreno.codigounico.Models.DetalleProductosPadre
import com.gemetytec.mmoreno.codigounico.Models.ModelDetalleEntradasPadre
import com.gemetytec.mmoreno.codigounico.Models.listviewAdapter
import com.gemetytec.mmoreno.codigounico.Models.listviewAdapterProductUbicar
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import timber.log.Timber

class UbicarEntradas : AppCompatActivity()  {

    /**Varibles para Pantallas Flotantes**/
    var builder: AlertDialog.Builder? = null
    var alert: AlertDialog? = null

    var VersionApp: TextView?= null

    val bundle: Bundle get() = intent.extras!!

    /** Variables Globales Login**/
    var User_Login: String = ""
    var User_Id_Login: String = ""
    var almacenLoginId_Login: String = ""
    var lastLoginDate_Login: String = ""
    var IdEntrada_Entrada: String = ""

    var DetalleProdPadreList: java.util.ArrayList<ModelDetalleEntradasPadre>? = null
    var listaDetalleProdPadre: java.util.ArrayList<ModelDetalleEntradasPadre>? = null

    var Str_DatosProductSelecc: String? = ""
    var Str_IdProductoSelecc: String? = ""
    var Str_IdDetalleEntradaSelecc: String? = ""


    var Txt_FolioPadre : TextView? = null
    var Txt_Factura : TextView? = null
    var Txt_Origen : TextView? = null
    var listview_ProductosPadre : ListView? = null
    var scrollPrincp : ScrollView? = null

    private var adapter: listviewAdapterProductUbicar? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ubicar_entradas)

        /**Metodo para optener la version de la app **/
        try {
            VersionApp = findViewById<TextView>(R.id.txt_version)

            val pInfo: PackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            val version = pInfo.versionName

            //VersionApp!!.text = BuildConfig.VERSION_NAME+" -Gemetytec" //asi se solicitaba antes
            VersionApp!!.text = "  Edición "+version +" – por Gemetytec"
        }catch (E1 :Exception){
            Timber.e("VersionApp: "+E1.message.toString())
        }
        /****/


        try {
            User_Login =  bundle!!.getString("L_User").toString()
            almacenLoginId_Login =  bundle!!.getString("L_IdAlmacen").toString()
            lastLoginDate_Login =  bundle!!.getString("L_LastDate").toString()
            IdEntrada_Entrada = bundle!!.getString("L_IdEntrada").toString()
            User_Id_Login =  bundle!!.getString("L_IdUser").toString()
            println("datos bundle_Login= Usuario: $User_Login IdUser: $User_Id_Login Id_Almacen: $almacenLoginId_Login Fecha: $lastLoginDate_Login IdEntrada: $IdEntrada_Entrada")
        }catch (E1 : Exception){
            Timber.e("Error_bundle_Envio: ${E1.message}")
        }

        Txt_FolioPadre = findViewById<TextView>(R.id.txt_FolioPadre)
        Txt_Factura = findViewById<TextView>(R.id.txt_Factura)
        Txt_Origen = findViewById<TextView>(R.id.txt_Origen)
        listview_ProductosPadre = findViewById<ListView>(R.id.lista_productos)
        scrollPrincp = findViewById<ScrollView>(R.id.scrollViewPantalla)

        if(isConnected(this)){
            Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_SHORT).show()
            DetalleProductPadreApi(IdEntrada_Entrada)

        }else{
            EncenderWifi()
        }

        /** Metodo para Scroll Principal**/
        try {
            scrollPrincp?.setOnTouchListener(View.OnTouchListener { v, event ->
                findViewById<View>(R.id.lista_productos).parent
                    .requestDisallowInterceptTouchEvent(false)
                false
            })
        }catch (E:Exception){
            Timber.e("scrollPrincp: "+E.message.toString())
        }


    } // final onCreate

    /** CONSULTA API Detalle de productos padre  **/
    fun DetalleProductPadreApi(IdEntrada:String){ //(GetEntradasxRecibir)
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Entradas/"

        val getData_gem = "GetEntradaXUbicar?IdEntrada="

        //  "http://localhost:81/api/Entradas/GetEntradaXUbicar?IdEntrada=6" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + getData_gem + IdEntrada //+ variable_string
        println("url_api: "+url_gem)
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.GET, url_gem, null, { response ->

            // this method is called when we get a successful response from API.
            // on below line we are adding a try catch block.
            try {
                // on below line we are getting data from our response
                // and setting it in variables.

                val cadenaJson: String = response.toString()
                println("datos_json_DetalleProduct_volley: "+ cadenaJson+ " - - ")

                GsonDetalleProdPadre(cadenaJson)

                //  println("datos_json_volley: "+ response.getString("SucursalId")+ " - - "+response.getString("nombre"))


            } catch (e: Exception) {
                // on below line we are handling our exception.

                Log.e("datos_json_volley", "- $e")
            }

        }, { error ->
            // this method is called when we get
            // any error while fetching data from our API
            Log.e("-TAG-", "RESPONSE IS $error")
            // in this case we are simply displaying a toast message.
            Toasty.error(this, "Fail to get response: "+error , Toast.LENGTH_LONG).show()
        })
        // at last we are adding
        // our request to our queue.
        queue.add(request)

    }

    fun GsonDetalleProdPadre(D_Json:String){
        try {
            /** variables de detalle productos padre **/
            var DetalleProdPadre: ModelDetalleEntradasPadre? = null
            DetalleProdPadreList = java.util.ArrayList<ModelDetalleEntradasPadre>()

            val Response = Gson().fromJson(D_Json, DetalleProductosPadre::class.java)

            println(Response.Folio + " => " + Response.Factura+" - "+Response.Origen)


            var Folio = Response.Folio
            var Factura = Response.Factura
            var Origen = Response.Origen

            Txt_FolioPadre?.setText(Folio)
            Txt_Factura?.setText(Factura)
            Txt_Origen?.setText(Origen)


            for (datos in Response.EntradaDetalleDTO) {
                /** llenado de listas**/
                DetalleProdPadre = ModelDetalleEntradasPadre(datos.Codigo, datos.Descripcion, datos.Cantidad.toString(), datos.CantRecibida.toString(),datos.ProductoId.toString(),datos.Id.toString(),datos.CantUbicada.toString())

                println(" - - "+datos.Id + " =+> " +datos.Codigo +" - "+datos.Descripcion +" - "+datos.Cantidad +" - "
                        +datos.CantRecibida +" - "+datos.ProductoId +" - - ")

                DetalleProdPadreList!!.add(DetalleProdPadre)


            }


            ListViewProductosPadre()
            obtenerListaProductosPadre()

        }catch (E:Exception){
            Log.e("GsonPrueba",E.message.toString())
        }
    }

    private fun obtenerListaProductosPadre() {
        listaDetalleProdPadre = java.util.ArrayList()

        for (i in DetalleProdPadreList!!.indices) {
            // listaDetalleProdPadre!!.add(DetalleProdPadreList!![i].getCodigo(),)
            listaDetalleProdPadre!!.add(ModelDetalleEntradasPadre(DetalleProdPadreList!![i].getCodigo(),DetalleProdPadreList!![i].getDescripcion(), DetalleProdPadreList!![i].getCantidad(),"","","",DetalleProdPadreList!![i].getUbicada()))
            println("datos_lista_cp: "+listaDetalleProdPadre)

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun ListViewProductosPadre(){
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                /**===> funcion para el llenado de la lista **/

                /*  val adaptador: ArrayAdapter<CharSequence?> =
                      ArrayAdapter(this, android.R.layout.simple_list_item_1,listaDetalleProdPadre as List<CharSequence?>) */


                adapter = listviewAdapterProductUbicar(listaDetalleProdPadre , applicationContext)

                println("+ ListViewFoliosAlmacen +")
                listview_ProductosPadre!!.setAdapter(adapter)

                /**===> recuperacion de scroll listv**/
                listview_ProductosPadre?.setOnTouchListener(View.OnTouchListener { v, event ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                })

                listview_ProductosPadre?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

                    Str_DatosProductSelecc = DetalleProdPadreList!![position].getDescripcion()
                    Str_IdProductoSelecc = DetalleProdPadreList!![position].getProductoId()
                    Str_IdDetalleEntradaSelecc = DetalleProdPadreList!![position].getIdDetalleEntrada()

                    val Str_Cantidad = DetalleProdPadreList!![position].getCantidad()
                    val Str_Ubicada = DetalleProdPadreList!![position].getUbicada()

                    if(Str_Cantidad.contentEquals(Str_Ubicada)){

                    }else if(!Str_Cantidad.contentEquals(Str_Ubicada)){
                        Toasty.info(this, "Producto Seleccionado:"+" "+Str_DatosProductSelecc,  Toast.LENGTH_SHORT, true).show()
                        println("Dseleccion producto: "+Str_DatosProductSelecc+" - IdProducto: "+Str_IdProductoSelecc+" - IdDetalleEntrada: "+Str_IdDetalleEntradaSelecc)
                        ProductSeleccionado(Str_IdProductoSelecc.toString(),Str_IdDetalleEntradaSelecc.toString(),Str_DatosProductSelecc.toString())
                    }


                }

                /** fin de la  funcion para el llenado del Spinner**/
            }, 300)

        }catch (E:Exception){
            Log.e("ListViewFoliosAlmacen",E.message.toString())
        }

    }

    fun ProductSeleccionado(IdProducto:String,IdDetalleEntrada:String,ProdPadre:String){
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val prodCodigosHijos = Intent(this,ValidEntrUbicProd::class.java)
                prodCodigosHijos.putExtra("L_User",User_Login)
                prodCodigosHijos.putExtra("L_IdAlmacen",almacenLoginId_Login)
                prodCodigosHijos.putExtra("L_LastDate",lastLoginDate_Login)
                prodCodigosHijos.putExtra("L_IdEntrada",IdEntrada_Entrada)
                prodCodigosHijos.putExtra("L_IdProducto",IdProducto)
                prodCodigosHijos.putExtra("L_IdDetalleEntrada",IdDetalleEntrada)
                prodCodigosHijos.putExtra("L_Folio",Txt_FolioPadre!!.text.toString())
                prodCodigosHijos.putExtra("L_Factura",Txt_Factura!!.text.toString())
                prodCodigosHijos.putExtra("L_Origen",Txt_Origen!!.text.toString())
                prodCodigosHijos.putExtra("L_DescrpProducto",ProdPadre)
                prodCodigosHijos.putExtra("L_IdUser",User_Id_Login)
                startActivity(prodCodigosHijos)

            }catch (E1 : Exception){
                Timber.e("ProductSeleccionado: "+ E1)
                Toasty.error(this, "ProductSeleccionado: "+E1.message.toString(), Toast.LENGTH_LONG).show()
            }
        }, 100)
    }

    fun FinalizarUbicarEntrada(view: View){

        try {

            CerrarEntradaUbicadaApi(IdEntrada_Entrada,User_Login)

        }catch (E1 : Exception){
            Timber.e("FinalizarUbicarEntrada: "+ E1)
            Toasty.error(this, "FinalizarUbicarEntrada: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }


    }

    /** CONSULTA API RecibirECU **/
    fun CerrarEntradaUbicadaApi(IdEntrada:String,Usuario : String){
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Entradas/"

        val api_gem = "CerrarEntradaUbicada?"
        val usuario_gem = "&Usuario="
        val idEntrad_gem ="&IdEntrada="

        //  "http://localhost:81/api/Entradas/CerrarEntradaUbicada?IdEntrada=1&Usuario=e" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + api_gem + idEntrad_gem + IdEntrada+ usuario_gem + Usuario
        println("url_api: "+url_gem)

        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.POST, url_gem, null, { response ->

            // this method is called when we get a successful response from API.
            // on below line we are adding a try catch block.
            try {
                // on below line we are getting data from our response
                // and setting it in variables.

                val cadenaJson: String = response.toString()
                println("cadena valida: "+cadenaJson)

                if (!response.getString("RespuestaEstado").toString().trim().contentEquals("") && response.getString("RespuestaEstado").toString().trim()!="false" ){ //

                    // actualizar
                    Toasty.success(this, "Ubicación Finalizada exitosa", Toast.LENGTH_LONG).show()

                    Handler(Looper.getMainLooper()).postDelayed({

                        val FinalizaUbicEntrada = Intent(this,ListadoFoliosUbicar::class.java)
                        FinalizaUbicEntrada.putExtra("L_User",User_Login)
                        FinalizaUbicEntrada.putExtra("L_IdAlmacen",almacenLoginId_Login)
                        FinalizaUbicEntrada.putExtra("L_LastDate",lastLoginDate_Login)
                        FinalizaUbicEntrada.putExtra("L_IdUser",User_Id_Login)

                        startActivity(FinalizaUbicEntrada)

                    }, 500)

                }else if (!response.getString("RespuestaEstado").toString().trim().contentEquals("") && response.getString("RespuestaEstado").toString().trim()=="false"){

                    Toasty.error(this, response.getString("Mensaje") , Toast.LENGTH_LONG).show()

                }


            } catch (e: Exception) {
                // on below line we are handling our exception.
                Log.e("json_Login_volley", "- ${e.message.toString()}")
                //  errorUser(nombreUsuario,PassUsuario,Almacen)
            }

        }, { error ->
            // this method is called when we get
            // any error while fetching data from our API
            Log.e("-TAG-", "RESPONSE IS $error")
            // in this case we are simply displaying a toast message.
            // errorUser(nombreUsuario,PassUsuario,Almacen)
            Toasty.error(this, "Fail to get response: "+error , Toast.LENGTH_LONG).show()
        })
        // at last we are adding
        // our request to our queue.
        queue.add(request)

    }


    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun EncenderWifi(){
        try {
            builder = AlertDialog.Builder(this)

            builder!!.setTitle("Conexion Internet")
            builder!!.setMessage("Su conexion a internet no existe \n por favor habilite su wifi\n")

            builder!!.setPositiveButton("ENCENDER") { dialog, which ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS ))
                Toasty.info(this, "Encendiendo Wifi", Toast.LENGTH_LONG).show()
                alert!!.cancel()

            }

            builder!!.setCancelable(false)
                .setNeutralButton("CERRAR",
                    DialogInterface.OnClickListener { dialog, id ->
                        try {
                            Toasty.error(this, "No encendido", Toast.LENGTH_LONG).show()

                        } catch (e: java.lang.Exception) {
                            println(" ErrorVend: $e")
                        }

                    })

            alert = builder!!.create()
            // alert!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alert!!.show()
            alert!!.setCanceledOnTouchOutside(false) // limitar el salir de pantalla al tocar fuera de ella
            alert!!.getWindow()!!.setGravity(Gravity.BOTTOM) //mover pantalla flotante

        }catch (E:Exception){
            Timber.e("EncenderWifi: "+E.message.toString())
        }
    }


    fun atras(view: View){
        try {
            val AtrasMenu = Intent(this,ListadoFoliosUbicar::class.java)
            AtrasMenu.putExtra("L_User",User_Login)
            AtrasMenu.putExtra("L_IdAlmacen",almacenLoginId_Login)
            AtrasMenu.putExtra("L_LastDate",lastLoginDate_Login)
            AtrasMenu.putExtra("L_IdUser",User_Id_Login)
            startActivity(AtrasMenu)

        }catch (E1 : Exception){
            Timber.e("back_Menu: "+ E1)
            Toasty.error(this, "back_Menu: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }


}