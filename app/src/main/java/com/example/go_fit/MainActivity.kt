package com.example.go_fit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.go_fit.member.HomeMemberActivity
import com.example.go_fit.api.InstrukturApi
import com.example.go_fit.api.MemberApi
import com.example.go_fit.api.PegawaiApi
import com.example.go_fit.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var idLayout : TextInputLayout
    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("dd, MMM, yyyy", Locale.US)
    private lateinit var passLayout : TextInputLayout
    private lateinit var login : Button
    private lateinit var user : EditText
    private lateinit var forgot : TextView
    private lateinit var pass : EditText
    private lateinit var loading : LinearLayout
    private var queue: RequestQueue? = null
    companion object{
        private val Login_LIST = arrayOf(
            "Member",
            "Pegawai",
            "Instruktur"
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()
        setExposeDropDownMenu()
        idLayout = binding.userInput
        passLayout = binding.passInput
        loading = findViewById(R.id.layout_loading)
        idLayout.visibility = View.GONE
        passLayout.visibility = View.GONE
        login = binding.loginButton
        forgot = binding.forgot
        forgot.visibility= View.GONE
        login.visibility = View.GONE
        user = binding.user
        pass = binding.pass
        queue= Volley.newRequestQueue(this)
        ed_jenis!!.setOnItemClickListener{adapterView, view, position, id ->
            idLayout.editText?.setText("")
            passLayout.editText?.setText("")
            pass!!.setFocusable(true)
            if(ed_jenis!!.text.toString() == "Member"){
                idLayout.visibility = View.VISIBLE
                passLayout.visibility = View.VISIBLE
                login.visibility = View.VISIBLE
                forgot.visibility = View.GONE
                idLayout.setHint("Email Member")
                user.setHint("email Member")
                passLayout.setStartIconDrawable(R.drawable.ic_baseline_lock_24)
                passLayout!!.setFocusable(true)
                passLayout.setStartIconOnClickListener(null)
                pass.setHint("Password")
                login.setText("Login")
            }else if(ed_jenis!!.text.toString() == "Pegawai"){
                idLayout.visibility = View.VISIBLE
                passLayout.visibility = View.VISIBLE
                login.visibility = View.VISIBLE
                forgot.visibility = View.VISIBLE
                idLayout.setHint("Email Pegawai")
                user.setHint("Email Pegawai")
                passLayout.setStartIconDrawable(R.drawable.ic_baseline_lock_24)
                passLayout!!.setFocusable(true)
                passLayout.setStartIconOnClickListener(null)
                pass.setHint("Password")
                login.setText("Login")
            }else if(ed_jenis!!.text.toString() == "Instruktur") {
                idLayout.visibility = View.VISIBLE
                passLayout.visibility = View.VISIBLE
                login.visibility = View.VISIBLE
                forgot.visibility = View.VISIBLE
                idLayout.setHint("Email Instruktur")
                user.setHint("Email Instruktur")
                passLayout.setStartIconDrawable(R.drawable.ic_baseline_lock_24)
                passLayout!!.setFocusable(true)
                passLayout.setStartIconOnClickListener(null)
                pass.setHint("Password")
                login.setText("Login")
            }
        }
        login.setOnClickListener{
            getAkun(idLayout.getEditText()?.getText().toString(),passLayout.getEditText()?.getText().toString(),ed_jenis.text.toString())
        }
    }

    private fun setExposeDropDownMenu() {
        val adapterList: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.item_list, Login_LIST)
        ed_jenis!!.setAdapter(adapterList)
//        ed_jenis.setText(adapterList.getItem(),false);
    }

//    override fun onDateSet(view: DatePicker?, year:Int, month:Int , dayofMonth : Int) {
//        Log.e("Calender","$year -- $month -- $dayofMonth")
//        calender.set(year, month, dayofMonth)
//        displayFormattedDate(calender.timeInMillis)
//    }

    private fun displayFormattedDate(timestamp: Long){
        val pattern = "yyyy-MM-dd" // Define your desired date format
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        findViewById<EditText>(R.id.pass).setText(formatter.format(timestamp))
        Log.i("Formatting",timestamp.toString())
    }


    private fun getAkun(username: String, password:String, jenis:String) {
        setLoading(true)
        if (jenis == "Pegawai") {
            val StringRequest: StringRequest = object : StringRequest(Method.GET,
                PegawaiApi.GET_BY_USERNAME + username + "/" + password,
                Response.Listener { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    setLoading(false)
                    // Check if role is Manajer Operasional
                    val data = jsonObject.optJSONObject("data")
                    val role = data?.optString("jabatan")
                    println("jabatan: $role")
                    if (role == "Manajer Operasional") {
                        // Continue to another page
                        val intent = Intent(this,HomePegawaiActivity::class.java)
                        val mBundle = Bundle()
                        mBundle.putString("username",idLayout.getEditText()?.getText().toString())
                        mBundle.putString("password",passLayout.getEditText()?.getText().toString())
                        intent.putExtra("profile",mBundle)
                        startActivity(intent)
                    } else {
                        // Show warning
                        Toast.makeText(
                            this@MainActivity,
                            "You are not authorized to access this page.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@MainActivity,
                            "Akun Belum Terdaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = idLayout.getEditText()?.getText().toString()
                    params["password"] = passLayout.getEditText()?.getText().toString()
                    return params
                }
            }
            queue!!.add(StringRequest)
        }else if(jenis == "Member"){
            val StringRequest: StringRequest = object : StringRequest(Method.GET,
                MemberApi.GET_BY_USERNAME + username + "/" + password,
                Response.Listener { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    setLoading(false)

                    // Check if role is Manajer Operasional
                    val data = jsonObject.optJSONObject("data")
                    val role = data?.optString("status")
                    println("status: $role")
                    if (role == "active") {
                        // Continue to another page
                        val intent = Intent(this, HomeMemberActivity::class.java)
                        val mBundle = Bundle()
                        mBundle.putString("username",idLayout.getEditText()?.getText().toString())
                        mBundle.putString("password",passLayout.getEditText()?.getText().toString())
                        intent.putExtra("profile",mBundle)
                        startActivity(intent)
                    } else {
                        // Show warning
                        Toast.makeText(
                            this@MainActivity,
                            "You are not authorized to access this page.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@MainActivity,
                            "Akun Belum Terdaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = idLayout.getEditText()?.getText().toString()
                    params["password"] = passLayout.getEditText()?.getText().toString()
                    return params
                }
            }
            queue!!.add(StringRequest)
        }else if(jenis == "Instruktur"){
            val StringRequest: StringRequest = object : StringRequest(Method.GET,
                InstrukturApi.GET_BY_USERNAME + username + "/" + password,
                Response.Listener { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    setLoading(false)
                    val intent = Intent(this,HomeInstrukturActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putString("username",idLayout.getEditText()?.getText().toString())
                    mBundle.putString("password",passLayout.getEditText()?.getText().toString())
                    intent.putExtra("profile",mBundle)
                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    setLoading(false)
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this@MainActivity,
                            "Akun Belum Terdaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = idLayout.getEditText()?.getText().toString()
                    params["password"] = passLayout.getEditText()?.getText().toString()
                    return params
                }
            }
            queue!!.add(StringRequest)
        }else{
            setLoading(false)
            val intent = Intent(this,JadwalHarianActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLoading(isLoading:Boolean){
        if(isLoading){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            loading!!.visibility = View.VISIBLE
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            loading!!.visibility = View.INVISIBLE
        }
    }
}