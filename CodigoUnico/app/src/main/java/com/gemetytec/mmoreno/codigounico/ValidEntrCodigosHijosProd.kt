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
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gemetytec.mmoreno.codigounico.Models.CodigosHijosProductoItem
import com.gemetytec.mmoreno.codigounico.Models.ModelCodigosHijosProduct
import com.gemetytec.mmoreno.codigounico.Models.listviewAdapterCodHijos
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import timber.log.Timber


class ValidEntrCodigosHijosProd : AppCompatActivity() {

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

    /**Variables Globales producto **/
    var IdProducto_Producto: String = ""
    var IdDetalleEntrada_Producto: String = ""
    var Txt_FolioPadre: TextView?= null
    var Txt_Factura: TextView?= null
    var Txt_Origen: TextView?= null
    var Txt_ProdPadre : TextView?= null
    var Factura_Producto: String = ""
    var Origen_Producto: String = ""
    var FolioPadre_Producto: String = ""
    var ProdPadre_Producto: String = ""

    var CodHijosProductList: java.util.ArrayList<ModelCodigosHijosProduct>? = null
    var listaCodHijosProduct: java.util.ArrayList<ModelCodigosHijosProduct>? = null
    var listview_ProductosPadre : ListView? = null

    private var adapter: listviewAdapterCodHijos? = null

    var EdTxt_BarCode: EditText?= null
    var Srt_BarCode: String = ""

    var indeterminateSwitch: ProgressBar? = null
    var scrollPrincp :ScrollView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recibir_codigos_producto_hijos)

        /**Metodo para optener la version de la app **/
        try {
            VersionApp = findViewById<EditText>(R.id.txt_version)

            val pInfo: PackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            val version = pInfo.versionName

            //VersionApp!!.text = BuildConfig.VERSION_NAME+" -Gemetytec" //asi se solicitaba antes
            VersionApp!!.text = "  Edición "+version +" – por Gemetytec"
        }catch (E1 :Exception){
            Timber.e("VersionApp: "+E1.message.toString())
        }
        /****/

        hideSoftKeyboard()

        try {
            User_Login =  bundle!!.getString("L_User").toString()
            almacenLoginId_Login =  bundle!!.getString("L_IdAlmacen").toString()
            lastLoginDate_Login =  bundle!!.getString("L_LastDate").toString()
            IdEntrada_Entrada = bundle!!.getString("L_IdEntrada").toString()
            IdProducto_Producto = bundle!!.getString("L_IdProducto").toString()
            IdDetalleEntrada_Producto = bundle!!.getString("L_IdDetalleEntrada").toString()
            FolioPadre_Producto = bundle!!.getString("L_Folio").toString()
            Factura_Producto= bundle!!.getString("L_Factura").toString()
            Origen_Producto = bundle!!.getString("L_Origen").toString()
            ProdPadre_Producto = bundle!!.getString("L_DescrpProducto").toString()
            User_Id_Login =  bundle!!.getString("L_IdUser").toString()

            println("datos bundle_producto= Usuario: $User_Login IdUser: $User_Id_Login Id_Almacen: $almacenLoginId_Login Fecha: $lastLoginDate_Login IdEntrada: $IdEntrada_Entrada IdProducto: $IdProducto_Producto " +
                    "IdDetalleEntrada: $IdDetalleEntrada_Producto" )
        }catch (E1 : Exception){
            Timber.e("Error_bundle_Envio: ${E1.message}")
        }

        Txt_FolioPadre = findViewById<TextView>(R.id.txt_FolioPadre)
        Txt_Factura = findViewById<TextView>(R.id.txt_Factura)
        Txt_Origen = findViewById<TextView>(R.id.txt_Origen)
        Txt_ProdPadre = findViewById<TextView>(R.id.txt_ProductPadre)
        scrollPrincp = findViewById<ScrollView>(R.id.scrollViewPantalla)
        listview_ProductosPadre = findViewById<ListView>(R.id.lista_CodigosUnicos)
        EdTxt_BarCode= findViewById<EditText>(R.id.EdTxt_Codigo)

        indeterminateSwitch = findViewById(R.id.indeterminate_circular_indicator)


        Txt_FolioPadre?.setText(FolioPadre_Producto)
        Txt_Factura?.setText(Factura_Producto)
        Txt_Origen?.setText(Origen_Producto)
        Txt_ProdPadre?.setText(ProdPadre_Producto)


        if(isConnected(this)){
            Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_LONG).show()
            CodHijProductsApi(IdProducto_Producto,IdDetalleEntrada_Producto)

        }else{
            EncenderWifi()
        }

        /** Metodo para Scroll Principal**/
        try {
            scrollPrincp?.setOnTouchListener(OnTouchListener { v, event ->
                findViewById<View>(R.id.lista_CodigosUnicos).parent
                    .requestDisallowInterceptTouchEvent(false)
                false
            })
        }catch (E:Exception){
            Timber.e("scrollPrincp: "+E.message.toString())
        }


        /** Metodo para el Enter en la caja de Codigo Unico **/
        try {
            EdTxt_BarCode!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    indeterminateSwitch!!.visibility = View.VISIBLE
                    Srt_BarCode = EdTxt_BarCode!!.editableText.toString()

                    println("BarCode: "+Srt_BarCode)
                    ValidacionECUApi(Srt_BarCode,User_Login,IdDetalleEntrada_Producto)
                    return@OnKeyListener true
                }
                false
            })
        }catch (E2 :Exception){
            Timber.e("EdTxt_BarCode: "+E2.message.toString())
        }

        Toasty.info(this, "Una vez validado los codigos Finalizar Recepción", Toast.LENGTH_LONG).show()

    } // final onCreate

    /** CONSULTA API RecibirECU **/
    fun ValidacionECUApi(CodigoUnico:String,Usuario : String,IdDtEntrada:String){
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Entradas/"

        val api_gem = "RecibirECU?"
        val cod_gem = "CodigoUnico="
        val usuario_gem = "&Usuario="
        val idDetalle_gem ="&IdDetalleEntrada="

        //  "http://localhost:81/api/Entradas/RecibirECU?CodigoUnico=423110&Usuario=a&IdDetalleEntrada=3" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + api_gem + cod_gem  + CodigoUnico + usuario_gem + Usuario+ idDetalle_gem + IdDtEntrada
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
            //    println("cadena valida: "+cadenaJson)

                    if (response.getString("Entradas").toString().trim().contentEquals("") && response.getString("Entradas").toString().trim()!="Error" ){ //

                        val cadenaJson: String = response.toString()
                        println("cadena valida: "+cadenaJson)
                        Toasty.success(this, "Codigo recibido" , Toast.LENGTH_SHORT).show()
                        indeterminateSwitch!!.visibility = View.INVISIBLE
                        EdTxt_BarCode?.getText()!!.clear()
                        Srt_BarCode=""
                        CodHijProductsApi(IdProducto_Producto,IdDetalleEntrada_Producto)

                    }else if (!response.getString("Mensaje").toString().trim().contentEquals("")){
                        indeterminateSwitch!!.visibility = View.INVISIBLE
                        EdTxt_BarCode?.getText()!!.clear()
                        Srt_BarCode=""
                        Toasty.error(this, response.getString("Mensaje") , Toast.LENGTH_LONG).show()
                        CodHijProductsApi(IdProducto_Producto,IdDetalleEntrada_Producto)
                    }else{
                        indeterminateSwitch!!.visibility = View.INVISIBLE
                        EdTxt_BarCode?.getText()!!.clear()
                        Srt_BarCode=""
                        println("error user")
                        CodHijProductsApi(IdProducto_Producto,IdDetalleEntrada_Producto)
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

    fun CorrectoOError(CadenaJson:String){
        try {


        }catch (E:Exception){
            Log.e("GsonPrueba",E.message.toString())
        }
    }


    /** CONSULTA API CodigosHijos del Producto **/
    fun CodHijProductsApi(IdProducto:String,IdDetalleEnt:String){
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Entradas/"
        val getData_gem = "GetCUsXRecibir?"
        val getIdProd_gem = "IdProducto="
        val getIdDetEntr_gem = "&IdDetalleEntrada="

        //  "http://localhost:81/api/Entradas/GetCUsXRecibir?IdProducto=22&IdDetalleEntrada=2s" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + getData_gem + getIdProd_gem + IdProducto + getIdDetEntr_gem + IdDetalleEnt  //+ variable_string
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
                println("datos_json_volley: "+ cadenaJson+ " - - ")

                GsonProdPadre_Hijos(cadenaJson)
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

    fun GsonProdPadre_Hijos(D_Json:String){
        try {
            /** variables de detalle productos padre **/
            var CodHijosProduct: ModelCodigosHijosProduct? = null
            CodHijosProductList = java.util.ArrayList<ModelCodigosHijosProduct>()

         //   val Response = Gson().fromJson(D_Json, DetalleProductosPadre::class.java)

            val Response = Gson().fromJson(D_Json,CodigosHijosProductoItem::class.java)


            for (datos in Response.CodigosUnicos) {
                println(datos.CodigoBarras + " => " + datos.NoReimpresion+" - "+ datos.TipoMovimiento+" - ")

                /** llenado de listas**/
                CodHijosProduct = ModelCodigosHijosProduct(datos.CodigoBarras, datos.NoReimpresion.toString(), datos.TipoMovimiento)

                CodHijosProductList!!.add(CodHijosProduct)

            }


            ListViewProductosPadre()
            obtenerListaProductosPadre()

        }catch (E:Exception){
            Log.e("GsonPrueba",E.message.toString())
        }
    }

    private fun obtenerListaProductosPadre() {
        listaCodHijosProduct = java.util.ArrayList()

        for (i in CodHijosProductList!!.indices) {
            // listaDetalleProdPadre!!.add(DetalleProdPadreList!![i].getCodigo(),)
            listaCodHijosProduct!!.add(ModelCodigosHijosProduct(CodHijosProductList!![i].getCodigoBrras(),CodHijosProductList!![i].getNoImpresion(), CodHijosProductList!![i].getEstatus()))
            println("datos_lista: "+listaCodHijosProduct.toString())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun ListViewProductosPadre(){
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                /**===> funcion para el llenado de la lista **/

                /*  val adaptador: ArrayAdapter<CharSequence?> =
                      ArrayAdapter(this, android.R.layout.simple_list_item_1,listaDetalleProdPadre as List<CharSequence?>) */


             /*   val header: View = layoutInflater.inflate(R.layout.cabecera_validacion_recep, null)
                listview_ProductosPadre?.addHeaderView(header) */

                adapter = listviewAdapterCodHijos(listaCodHijosProduct , applicationContext)


                println("+ ListViewCodigosHijos +")
                listview_ProductosPadre!!.setAdapter(adapter)

                listview_ProductosPadre?.setOnTouchListener(OnTouchListener { v, event ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    false
                })


              /*  listview_ProductosPadre?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

                    Str_DatosProductSelecc = DetalleProdPadreList!![position].getDescripcion()
                    Str_IdProductoSelecc = DetalleProdPadreList!![position].getProductoId()
                    Str_IdDetalleEntradaSelecc = DetalleProdPadreList!![position].getIdDetalleEntrada()


                    Toasty.info(this, "Producto Seleccionado:"+" "+Str_DatosProductSelecc,  Toast.LENGTH_LONG, true).show()
                    println("Dseleccion producto: "+Str_DatosProductSelecc+" - IdProducto: "+Str_IdProductoSelecc+" - IdDetalleEntrada: "+Str_IdDetalleEntradaSelecc)

                  //  ProductSeleccionado(Str_IdProductoSelecc.toString(),Str_IdDetalleEntradaSelecc.toString(),Str_DatosProductSelecc.toString())



                } */

                /** fin de la  funcion para el llenado del Spinner**/
            }, 300)

        }catch (E:Exception){
            Log.e("ListViewFoliosAlmacen",E.message.toString())
        }

    }



    override fun onPause() {
        super.onPause()

        println("SCREEN onPause")

    }

  /*  override fun onResume() {
        super.onResume()

        println("SCREEN onResume")
         Handler(Looper.getMainLooper()).postDelayed({
            if(isConnected(this)){
                CodHijProductsApi(IdProducto_Producto,IdDetalleEntrada_Producto)
                Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_LONG).show()
            }
        }, 700)
    } */


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
            val AtrasRecepcionEntradas = Intent(this,RecibirEntradas::class.java)
            AtrasRecepcionEntradas.putExtra("L_User",User_Login)
            AtrasRecepcionEntradas.putExtra("L_IdAlmacen",almacenLoginId_Login)
            AtrasRecepcionEntradas.putExtra("L_LastDate",lastLoginDate_Login)
            AtrasRecepcionEntradas.putExtra("L_IdEntrada",IdEntrada_Entrada)
            AtrasRecepcionEntradas.putExtra("L_IdUser",User_Id_Login)
            startActivity(AtrasRecepcionEntradas)

        }catch (E1 : Exception){
            Timber.e("AtrasRecepcionEntradas: "+ E1)
            Toasty.error(this, "AtrasRecepcionEntradas: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Hides the soft keyboard
     */
    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }


}