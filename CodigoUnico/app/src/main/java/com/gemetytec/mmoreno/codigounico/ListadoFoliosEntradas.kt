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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gemetytec.mmoreno.codigounico.Models.*
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import timber.log.Timber
import java.util.regex.Pattern

class ListadoFoliosEntradas : AppCompatActivity()  {

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

    var FoliosAlmacenList: java.util.ArrayList<ModelFoliosUbicar>? = null
    var listaFoliosAlmacen: java.util.ArrayList<ModelFoliosUbicar>? = null
    private var adapter: listviewAdapterFoliosUbicar? = null
    var listview_Folios_Entradas : ListView? = null

    var Str_FolioEntradaSelecc: String? = ""
    var Str_IdFolioEntradaSelecc: String? = ""
    var espacioFolio: String? = ""
    var espacioFactura: String? = ""
    var espacioOrigen: String? = ""
    var espacioCantida: String? = ""


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_listado_folios_entradas)

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


        try {
            User_Login =  bundle!!.getString("L_User").toString()
            almacenLoginId_Login =  bundle!!.getString("L_IdAlmacen").toString()
            lastLoginDate_Login =  bundle!!.getString("L_LastDate").toString()
            User_Id_Login =  bundle!!.getString("L_IdUser").toString()
            println("datos bundle_Login= Usuario: $User_Login IdUser: $User_Id_Login  Id_Almacen: $almacenLoginId_Login Fecha: $lastLoginDate_Login")
        }catch (E1 : Exception){
            Timber.e("Error_bundle_Envio: ${E1.message}")
        }

        listview_Folios_Entradas= findViewById<ListView>(R.id.lista_entradas)



        if(isConnected(this)){
            Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_SHORT).show()
            ListadoEntradasFolioApi(almacenLoginId_Login)

        }else{
            EncenderWifi()
        }



    }// final onCreate

    /** CONSULTA API Almacenes **/
    fun ListadoEntradasFolioApi(AlmacenId:String){ //(GetEntradasxRecibir)
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Entradas/"

        val getData_gem = "GetEntradasXRecibir?AlmacenId="

        //  "http://localhost:81/api/Entradas/GetEntradasXRecibir?AlmacenId=1" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + getData_gem + AlmacenId //+ variable_string
        println("url_api: "+url_gem)
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.GET, url_gem, null, { response ->

            // this method is called when we get a successful response from API.
            // on below line we are adding a try catch block.
            try {
                // on below line we are getting data from our response
                // and setting it in variables.

              /*  val cadenaJson: String = response.toString()
                println("datos_json_Listado_volley: "+ cadenaJson+ " - - ")

                GsonFoliosAlmacen(cadenaJson) */

                val cadenaJson: String = response.toString()
                println("datos_json_Listado_volley: "+ cadenaJson+ " - - ")

                if (!response.getString("Entradas").toString().trim().contentEquals("") && response.getString("Entradas").toString().trim()!="Error" ){ //

                    val cadenaJson: String = response.toString()
                    println("datos_json_Listado_volley: "+ cadenaJson+ " - - ")
                    GsonFoliosAlmacen(cadenaJson)

                }else if (!response.getString("Mensaje").toString().trim().contentEquals("")){

                    Toasty.info(this, response.getString("Mensaje") , Toast.LENGTH_LONG).show()

                }


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

    fun GsonFoliosAlmacen(D_Json:String){
        try {
            /** variables de lista de Almacenes **/
            var Folios_Almacen: ModelFoliosUbicar? = null
            FoliosAlmacenList = java.util.ArrayList<ModelFoliosUbicar>()

            val Response = Gson().fromJson(D_Json,FoliosAlmacenUbicar::class.java)

           /* for (datos in Response) {
                println(datos.Folio + " => " + datos.Factura+" - "+datos.Origen+" - "+datos.Cantidad)

                /** llenado de listas**/
                Folios_Almacen = AlmacenFolios()

                var countFolio = datos.Folio
                var countFactura = datos.Factura
                var countOrigen = datos.Origen
                var countCantidad = datos.Cantidad

                contadorEspaciosLista(countFolio,countFactura,countOrigen,countCantidad.toString())

                Folios_Almacen?.setAlmacenFolio("  "+datos.Folio+espacioFolio+" - "+espacioFactura+datos.Factura+espacioFactura+" - "+espacioOrigen+datos.Origen+espacioOrigen+" - "+espacioCantida+datos.Cantidad,datos.Id.toString())
                FoliosAlmacenList!!.add(Folios_Almacen)

            } */

            for (datos in Response.Entradas) {
                println(datos.Folio + " => " + datos.Factura+" - "+datos.Origen+" - "+datos.Cantidad)

                /** llenado de listas**/
                Folios_Almacen = ModelFoliosUbicar(datos.Folio,datos.Factura,datos.Origen,datos.Cantidad.toString(),datos.Id.toString())

                FoliosAlmacenList!!.add(Folios_Almacen)

            }

            ListViewFoliosAlmacen()
            obtenerListaFoliosAlmacen()

        }catch (E:Exception){
            Log.e("GsonPrueba",E.message.toString())
        }
    }

    private fun obtenerListaFoliosAlmacen() {
        listaFoliosAlmacen = java.util.ArrayList()

        for (i in FoliosAlmacenList!!.indices) {
           // listaFoliosAlmacen!!.add(FoliosAlmacenList!![i].getAlmacenFolio())
            listaFoliosAlmacen!!.add(ModelFoliosUbicar(FoliosAlmacenList!![i].getFolio(),FoliosAlmacenList!![i].getFactura(),FoliosAlmacenList!![i].getOrigen(),FoliosAlmacenList!![i].getCantidad(),""))
            println("datos_lista_cp: "+listaFoliosAlmacen)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun ListViewFoliosAlmacen(){
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                /**===> funcion para el llenado de la lista **/

              /*  val adaptador: ArrayAdapter<CharSequence?> =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1,listaFoliosAlmacen as List<CharSequence?>) */

                adapter = listviewAdapterFoliosUbicar(listaFoliosAlmacen , applicationContext)

                println("+ ListViewFoliosAlmacen +")
               // listview_Folios_Entradas!!.setAdapter(adaptador)
                listview_Folios_Entradas!!.setAdapter(adapter)


                listview_Folios_Entradas?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

                    Str_FolioEntradaSelecc = FoliosAlmacenList!![position].getFolio() //getAlmacenFolio()
                    Str_IdFolioEntradaSelecc = FoliosAlmacenList!![position].getIdFolio() //getAlmacenFolioId()

                    Toasty.info(this, "Dirección Seleccionada:"+" "+Str_FolioEntradaSelecc,  Toast.LENGTH_SHORT, true).show()
                    println("Dseleccion FoliosAlmacenList: "+Str_FolioEntradaSelecc+" - ID: "+Str_IdFolioEntradaSelecc)
                    FolioSeleccionado(Str_IdFolioEntradaSelecc.toString())

                }

                /** fin de la  funcion para el llenado del Spinner**/
            }, 300)

        }catch (E:Exception){
            Log.e("ListViewFoliosAlmacen",E.message.toString())
        }

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

    fun FolioSeleccionado(IdEntrada:String){
        Handler(Looper.getMainLooper()).postDelayed({
        try {
            val siguente = Intent(this,RecibirEntradas::class.java)
            siguente.putExtra("L_User",User_Login)
            siguente.putExtra("L_IdUser",User_Id_Login)
            siguente.putExtra("L_IdAlmacen",almacenLoginId_Login)
            siguente.putExtra("L_LastDate",lastLoginDate_Login)
            siguente.putExtra("L_IdEntrada",IdEntrada)
            startActivity(siguente)

        }catch (E1 : Exception){
            Timber.e("FolioSeleccionado: "+ E1)
            Toasty.error(this, "FolioSeleccionado: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
        }, 100)
    }

    fun atras(view: View){
        try {
            val AtrasMenu = Intent(this,SubMenuEntradas::class.java)
                AtrasMenu.putExtra("L_User",User_Login)
                AtrasMenu.putExtra("L_IdUser",User_Id_Login)
                AtrasMenu.putExtra("L_IdAlmacen",almacenLoginId_Login)
                AtrasMenu.putExtra("L_LastDate",lastLoginDate_Login)
            startActivity(AtrasMenu)

        }catch (E1 : Exception){
            Timber.e("back_Menu: "+ E1)
            Toasty.error(this, "back_Menu: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }



}