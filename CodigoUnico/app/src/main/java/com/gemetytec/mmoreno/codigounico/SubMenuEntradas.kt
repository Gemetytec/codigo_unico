package com.gemetytec.mmoreno.codigounico

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import timber.log.Timber

class SubMenuEntradas  : AppCompatActivity()  {

    var VersionApp: TextView?= null

    val bundle: Bundle get() = intent.extras!!

    /** Variables Globales Login**/
    var User_Login: String = ""
    var User_Id_Login: String = ""
    var almacenLoginId_Login: String = ""
    var lastLoginDate_Login: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sub_menu_entradas)



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
            println("datos bundle_Login= Usuario: $User_Login IdUser: $User_Id_Login Id_Almacen: $almacenLoginId_Login Fecha: $lastLoginDate_Login")
        }catch (E1 : Exception){
            Timber.e("Error_bundle_Envio: ${E1.message}")
        }



    }

    fun RecibirEntrada_Pantalla(view: View){
        try {
            val Entradas = Intent(this,ListadoFoliosEntradas::class.java)
                Entradas.putExtra("L_User",User_Login)
                Entradas.putExtra("L_IdAlmacen",almacenLoginId_Login)
                Entradas.putExtra("L_LastDate",lastLoginDate_Login)
                Entradas.putExtra("L_IdUser",User_Id_Login)
            startActivity(Entradas)

        }catch (E1 : Exception){
            Timber.e("RecibirEntrada_Pantalla: "+ E1)
            Toasty.error(this, "RecibirEntrada_Pantalla: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun UbicarEntrada_Pantalla(view: View){
        try {
            val UbicEntradas = Intent(this,ListadoFoliosUbicar::class.java)
            UbicEntradas.putExtra("L_User",User_Login)
            UbicEntradas.putExtra("L_IdAlmacen",almacenLoginId_Login)
            UbicEntradas.putExtra("L_LastDate",lastLoginDate_Login)
            UbicEntradas.putExtra("L_IdUser",User_Id_Login)
            startActivity(UbicEntradas)

        }catch (E1 : Exception){
            Timber.e("UbicarEntrada_Pantalla: "+ E1)
            Toasty.error(this, "UbicarEntrada_Pantalla: "+E1.message.toString(), Toast.LENGTH_LONG).show()
        }
    }



    fun atras(view: View){
        try {
            val AtrasMenu = Intent(this,Menu::class.java)
            AtrasMenu.putExtra("L_User",User_Login)
            AtrasMenu.putExtra("L_IdAlmacen",almacenLoginId_Login)
            AtrasMenu.putExtra("L_LastDate",lastLoginDate_Login)
            AtrasMenu.putExtra("L_IdUser",User_Id_Login)
            startActivity(AtrasMenu)

        }catch (E1 : Exception){
            Timber.e("error_back_Menu: "+ E1)
        }
    }

}