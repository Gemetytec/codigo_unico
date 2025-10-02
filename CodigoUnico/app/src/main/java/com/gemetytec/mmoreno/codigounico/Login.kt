package com.gemetytec.mmoreno.codigounico

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
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gemetytec.mmoreno.codigounico.Models.AlmacenesNombreItem
import com.gemetytec.mmoreno.codigounico.Models.idAlmacen
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import timber.log.Timber
import java.util.regex.Pattern


class Login : AppCompatActivity()  {


    /**Varibles para Pantallas Flotantes**/
    var builder: AlertDialog.Builder? = null
    var alert: AlertDialog? = null

    /** Variables Globales Login**/
    var User_Login: String = ""
    var User_Id_Login: String = ""
    var almacenLoginId_Login: String = ""
    var lastLoginDate_Login: String = ""

    var UsuarioStr:String = ""
    var PasswordStr:String = ""

    var VersionApp: TextView?= null
    var Usuario: EditText? = null
    var Password: EditText? = null
    var btnLogin: ImageView? = null

    var IdAlmacenesList: java.util.ArrayList<idAlmacen>? = null
    var listaIdAlmacenes: java.util.ArrayList<String>? = null
    var SpinnerAlmacenes: Spinner? = null
    var Str_AlmacenSelecc:String? = null
    var Str_IdAlmacenSelecc:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


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

        Usuario = findViewById<EditText>(R.id.Txt_Usuario)
        Password = findViewById<EditText>(R.id.Txt_Password)
        SpinnerAlmacenes= findViewById<Spinner>(R.id.spinner_Almacen)
        btnLogin = findViewById<ImageView>(R.id.ButtonLogin)

        SpinnerAlmacenes!!.isEnabled = false

        Toasty.info(this, "ingrese su datos y de enter", Toast.LENGTH_SHORT).show()

        /** Metodo para el Enter en la caja del Usuario **/
        try {
            Usuario!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                    UsuarioStr = Usuario!!.editableText.toString()
                    /**convierte el texto en mayuscukas**/
                 //   UsuarioStr = UsuarioStr.toUpperCase()
                    Password!!.requestFocus()
                    println("usuario_Regist: "+UsuarioStr)
                    return@OnKeyListener true
                }
                false
            })
        }catch (E2 :Exception){
            Timber.e("Usuario: "+E2.message.toString())
        }

        /** Metodo para el Enter en la caja de la contraseña **/
        try {
            Password!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                    UsuarioStr = Usuario!!.editableText.toString().trim().contentEquals("").toString()
                  //  UsuarioStr = UsuarioStr.toUpperCase()
                    PasswordStr = Password!!.editableText.toString().trim().contentEquals("").toString()
                  //  PasswordStr = PasswordStr.toUpperCase()

                    SpinnerAlmacenes!!.isEnabled = true

                    // en la línea de abajo obteniendo la vista actual.
                    val view: View? = this.currentFocus
                    // en la línea de abajo comprobando si la vista no es nula.
                    if (view != null) {
                        // en la línea de abajo estamos creando una variable
                        // para el administrador de entrada e inicializándolo.
                        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        // en la línea de abajo ocultando tu teclado.
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                    }

                    SpinnerAlmacenes!!.requestFocus()

                    return@OnKeyListener true
                }
                false
            })
        }catch (E3 :Exception){
            Timber.e("Password: "+E3.message.toString())
        }



       /* if(isConnected(this)){
            Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_SHORT).show()
            AlmacenesApi()

        }else{
            EncenderWifi()
        } */


    } // final onCreate

    override fun onPause() {
        super.onPause()

            println("SCREEN onPause")

    }

    override fun onResume() {
        super.onResume()

        println("SCREEN onResume")
        Handler(Looper.getMainLooper()).postDelayed({
            if(isConnected(this)){
                AlmacenesApi()
                Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_SHORT).show()
            }else{
                EncenderWifi()
            }
        }, 700)
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


    /** CONSULTA API Login **/
    fun UserLoginApi(nombreUsuario:String,PassUsuario : String,Almacen: String){
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Usuario/"

        val Login_gem = "Login?usuario="
        val pass_gem = "&password="
        val userNameGem =  nombreUsuario
        val almac_gem = "&AlmacenId="

        //  "http://localhost:81/api/Usuario/Login?usuario=a&password=0000&AlmacenId=1" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + Login_gem + userNameGem  + pass_gem + PassUsuario + almac_gem + Almacen
        println("url_api: "+url_gem)

        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.GET, url_gem, null, { response ->

            // this method is called when we get a successful response from API.
            // on below line we are adding a try catch block.
            try {
                // on below line we are getting data from our response
                // and setting it in variables.
                if (!response.getString("Usuario").toString().trim().contentEquals("")
                    && response.getString("Usuario").toString().trim()!="Error"){

                    val cadenaJson: String = response.toString()
                    println("datos_json_Login_volley: "+ cadenaJson)

                    User_Login = response.getString("Usuario").toString().trim()
                    User_Id_Login = response.getString("Id").toString().trim()
                    almacenLoginId_Login = response.getString("AlmacenLoginId").toString().trim()
                    lastLoginDate_Login = response.getString("LastLoginDate").toString()
                    LoginCheck()

                }else if (!response.getString("Mensaje").toString().trim().contentEquals("")){
                    Toasty.error(this, response.getString("Mensaje") , Toast.LENGTH_LONG).show()
                }else{
                    println("error user")
                }

            } catch (e: Exception) {
                // on below line we are handling our exception.
                Log.e("json_Login_volley", "- ${e.message.toString()}")
                errorUser(nombreUsuario,PassUsuario,Almacen)
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

    fun errorUser(nombreUsuario:String,PassUsuario : String,Almacen: String){
        try {

            val queue: RequestQueue = Volley.newRequestQueue(applicationContext)

            val endpoint_gem = "http://192.168.1.10:81/api/"
            val method_gem = "Usuario/"

            val Login_gem = "Login?usuario="
            val pass_gem = "&password="
            val userNameGem =  nombreUsuario
            val almac_gem = "&AlmacenId="

            //  "http://localhost:81/api/Usuario/Login?usuario=a&password=0000&AlmacenId=1" 192.168.100.2:81

            val url_gem = endpoint_gem + method_gem + Login_gem + userNameGem  + pass_gem + PassUsuario + almac_gem + Almacen
            println("url_api: "+url_gem)

            val request = JsonObjectRequest(Request.Method.GET, url_gem, null, { response ->

                try {
                  if (!response.getString("Mensaje").toString().trim().contentEquals("")){
                      val cadenaJson: String = response.toString()
                      println("datos_json_Login_volley: "+ cadenaJson)

                        Toasty.error(this, response.getString("Mensaje") , Toast.LENGTH_LONG).show()
                    }else{
                        println("error user")
                    }

                } catch (e: Exception) {
                    // on below line we are handling our exception.
                    Log.e("datos_json_errorLogin_volley", "- ${e.message.toString()}")
                    errorUser(nombreUsuario,PassUsuario,Almacen)
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



        }catch (E:Exception){
            Timber.e("errorUser: "+E.message.toString())
        }
    }

    /** CONSULTA API Almacenes **/
    fun AlmacenesApi(){
        // on below line we are creating a variable for our
        // request queue and initializing it.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // on below line we are creating a variable for request
        // and initializing it with json object request

        val endpoint_gem = "http://192.168.1.10:81/api/"
        val method_gem = "Almacen/"

        val getData_gem = "GetAlmacenes"

        //  "http://localhost:81/api/Almacen/GetAlmacenes" 192.168.100.2:81

        val url_gem = endpoint_gem + method_gem + getData_gem  //+ variable_string
        println("url_api: "+url_gem)
        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonArrayRequest(Request.Method.GET, url_gem, null, { response ->

            // this method is called when we get a successful response from API.
            // on below line we are adding a try catch block.
            try {
                // on below line we are getting data from our response
                // and setting it in variables.

                val cadenaJson: String = response.toString()
                println("datos_json_volley: "+ cadenaJson+ " - - ")

                GsonAlmacenes(cadenaJson)
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



    fun GsonAlmacenes(D_Json:String){
        try {
            /** variables de lista de Almacenes **/
            var datos_Cp: idAlmacen? = null
            IdAlmacenesList = java.util.ArrayList<idAlmacen>()


            val Response = Gson().fromJson(D_Json,Array<AlmacenesNombreItem>::class.java)

            for (datos in Response) {
                println(datos.Id.toString() + " => " + datos.Nombre+" - ")
                //  println( datos.response.asentamiento+" - ")

                /** llenado de listas**/
                datos_Cp = idAlmacen()


                datos_Cp?.setIdAlmacen(datos.Nombre,datos.Id.toString())

                Log.i("ALMACEN: ", datos.Id.toString()+" - "+datos.Nombre)

                IdAlmacenesList!!.add(datos_Cp)

            }
            SpinnerListaAlmacenes()
            obtenerListaAlmacenes()


        }catch (E:Exception){
            Log.e("GsonPrueba",E.message.toString())
        }
    }

    fun SpinnerListaAlmacenes(){
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                /**===> funcion para el llenado del Spinner **/

                val adaptador: ArrayAdapter<CharSequence?> =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item,listaIdAlmacenes as List<CharSequence?>)


                SpinnerAlmacenes!!.setAdapter(adaptador)
                SpinnerAlmacenes!!.setOnItemSelectedListener(object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, idl: Long) {
                            if (position != 0) {
                              //  Muni_Spinn!!.isEnabled = true
                                Str_AlmacenSelecc = IdAlmacenesList!![position - 1].getAlmacen()

                                Str_IdAlmacenSelecc = IdAlmacenesList!![position - 1].getIdAlmacen()
                                println("Dseleccion Almacen: "+Str_AlmacenSelecc)

                                btnLogin!!.requestFocus()
                            } else { }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    })

                /** fin de la  funcion para el llenado del Spinner**/
            }, 400)

        }catch (E:Exception){
            Log.e("SpinnerColonia",E.message.toString())
        }

    }

    private fun obtenerListaAlmacenes() {
        listaIdAlmacenes = java.util.ArrayList()
        listaIdAlmacenes!!.add("Seleccione")

        for (i in IdAlmacenesList!!.indices) {
            listaIdAlmacenes!!.add(IdAlmacenesList!![i].getAlmacen())
            println("datos_lista_cp: "+listaIdAlmacenes)
        }
    }



    private  fun showError(){
        Toasty.error(this, "Ha ocurrido un error!", Toast.LENGTH_SHORT).show()
    }

    private fun ConexionInternet(error_: String?) {
        try {
           // Timber.e("AlmacenesApi",error_)
            println("AlmacenesApi: "+error_)
            Toasty.error(this, "falla conexion : "+error_ , Toast.LENGTH_LONG).show()

        }catch (EIO:Exception){
            Timber.e("ConexionInternet= "+ EIO.message.toString())
        }
    }



    fun LoginCheck(){
        try {
            val Menu_verif = Intent(this, Menu::class.java)
                Menu_verif.putExtra("L_User",User_Login)
                Menu_verif.putExtra("L_IdAlmacen",almacenLoginId_Login)
                Menu_verif.putExtra("L_LastDate",lastLoginDate_Login)
                Menu_verif.putExtra("L_IdUser",User_Id_Login)
            startActivity(Menu_verif)

        }catch (E:Exception){
            Timber.e("LoginCheck: "+E.message.toString())
            Toasty.error(this, "LoginCheck: "+E.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun VerificacionLogin(view: View){

        try {
            UsuarioStr = Usuario!!.editableText.toString()
            PasswordStr = Password!!.editableText.toString()

            if(isConnected(this) && UsuarioStr.isNotEmpty() && PasswordStr.isNotEmpty() && Str_IdAlmacenSelecc!!.isNotEmpty()){


                println("datos login: "+UsuarioStr+" - - "+PasswordStr+" - - "+Str_IdAlmacenSelecc)
                UserLoginApi(UsuarioStr,PasswordStr,Str_IdAlmacenSelecc.toString())

            }else if (!isConnected(this)){
                EncenderWifi()
            }else{
                Toasty.info(this, "Ingrese credenciales", Toast.LENGTH_LONG).show()
            }
        }catch (E:Exception){
            Timber.e("VerificacionLogin: "+E.message.toString())
            Toasty.error(this, "VerificacionLogin: "+E.message.toString(), Toast.LENGTH_LONG).show()
        }
    }


    fun MenuPruebas (view: View){

        try {
            if(isConnected(this)){
                AlmacenesApi()
                val Menu_verif = Intent(this, Menu::class.java)
                //   Menu_verif.putExtra("UsuarioLogeado",UsuarioStr)
                startActivity(Menu_verif)
                Toasty.success(this, "Existe conexión a Internet", Toast.LENGTH_LONG).show()
            }else{
                EncenderWifi()
            }

        }catch (E:Exception){
            Timber.e("Menu: "+E.message.toString())
            Toasty.error(this, "Menu: "+E.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    /**Metodo Home **/
    fun Home ( view: View ){
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)


        }catch (E1 : Exception){
            Timber.e("Home: "+E1.message.toString())
            Toasty.error(this, "Home: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun restartApp() {
        val ctx = applicationContext
        val pm = ctx.packageManager
        val intent = pm.getLaunchIntentForPackage(ctx.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
        ctx.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }


}