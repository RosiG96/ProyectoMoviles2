package com.example.gameproyecto

import android.content.ContentValues
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class MainActivityNivel3 : AppCompatActivity() { private lateinit var myToolbar : Toolbar
    private lateinit var mp : MediaPlayer
    private lateinit var mpGreat : MediaPlayer
    private lateinit var mpBad : MediaPlayer
    private lateinit var tv_nombre : TextView
    private lateinit var tv_score : TextView
    private lateinit var et_Respuesta : EditText
    private lateinit var ivAuno : ImageView
    private lateinit var iv_Vidas : ImageView
    private lateinit var ivAdos : ImageView

    private var score: Int = 0
    private var numAleatorio_Uno: Int = 0
    private var numAleatorio_Dos: Int = 0
    private var result: Int = 0
    private var vidas: Int = 0

    private lateinit var nombre_Jugador: String
    private lateinit var string_Vcore: String
    private lateinit var string_Vidas: String

    val numeros = arrayOf("cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nivel3)

        Toast.makeText(this, "Nivel 3 - Restas", Toast.LENGTH_SHORT).show()

        tv_score = findViewById(R.id.tv_score)
        tv_nombre = findViewById(R.id.tv_nombre)
        ivAuno = findViewById(R.id.NumeroUno)
        ivAdos = findViewById(R.id.NumeroDos)
        iv_Vidas = findViewById(R.id.imageView)
        et_Respuesta = findViewById(R.id.et_resultado)
        vidas = 4
        nombre_Jugador = intent.getStringExtra("Jugador").toString()
        tv_nombre.text = "Jugador $nombre_Jugador"

        string_Vcore = intent.getStringExtra("score").toString()
        score = Integer.parseInt(string_Vcore);
        tv_score.text = "Score: $score"

        string_Vidas = intent.getStringExtra("vidas").toString()
        vidas = Integer.parseInt(string_Vidas);

        when (vidas) {
            3 -> iv_Vidas.setImageResource(R.drawable.tresvidas)
            2 -> iv_Vidas.setImageResource(R.drawable.dosvidas)
            1 -> iv_Vidas.setImageResource(R.drawable.unavida)
        }

        val myToolbar: Toolbar = findViewById(R.id.toolbarNivel3)
        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setIcon(R.mipmap.ic_launcher)

        mp = MediaPlayer.create(this,R.raw.alphabet_song)
        mp.start()
        mp.isLooping = true

        mpGreat = MediaPlayer.create(this, R.raw.wonderful)
        mpBad = MediaPlayer.create(this, R.raw.bad)

        numeroAleatorio()

    }

    fun comparar(view: View) {
        val respuesta = et_Respuesta.text.toString()

        if (respuesta.isNotEmpty()) {

            println("numAleatorio_Uno: $numAleatorio_Uno")
            println("numAleatorio_Dos: $numAleatorio_Dos")
            println("Comparison result: ${(numAleatorio_Uno + numAleatorio_Dos) == respuesta.toInt()}")

            if((numAleatorio_Uno + numAleatorio_Dos) == respuesta.toInt()){
                val intent = Intent(this, MainActivityNivel2::class.java)
                string_Vcore = score.toString()
                string_Vidas = vidas.toString()

                intent.putExtra("Jugador", nombre_Jugador)
                intent.putExtra("score", string_Vcore)
                intent.putExtra("vidas", string_Vidas)

                startActivity(intent)


            }
            else{
                mpBad.start()
                vidas--
                println("Vidas : ${vidas}")
                when (vidas) {
                    3 -> {
                        iv_Vidas.setImageResource(R.drawable.tresvidas)
                    }
                    2 -> {
                        iv_Vidas.setImageResource(R.drawable.dosvidas)
                        Toast.makeText(this, "Quedan dos manzanas", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        iv_Vidas.setImageResource(R.drawable.unavida)
                        Toast.makeText(this, "Queda una manzana", Toast.LENGTH_SHORT).show()
                    }
                    0 -> {
                        iv_Vidas.setImageResource(R.drawable.dosvidas)
                        mp.stop()
                        mp.release()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                baseDeDatos();
                et_Respuesta.setText("")
                numeroAleatorio();

            }


        }
        else{
            Toast.makeText(this, "Debes dar una respuesta", Toast.LENGTH_SHORT).show()
        }
    }



    fun numeroAleatorio() {
        if (score <= 29) {
            numAleatorio_Uno = (0..9).random()
            numAleatorio_Dos = (0..9).random()

            result = numAleatorio_Uno - numAleatorio_Dos

            if (result >= 0) {
                for (i in 0 until numeros.length) {
                    val id = resources.getIdentifier(numeros[i], "drawable", packageName)
                    if (numAleatorio_Uno == i) {
                        ivAuno.setImageResource(id)
                    }
                    if (numAleatorio_Dos == i) {
                        ivAdos.setImageResource(id)
                    }
                }
            }
        } else {
            numeroAleatorio()
        }
            val intent = Intent(this, MainActivityNivel4::class.java)
            string_Vcore = score.toString()
            string_Vidas = vidas.toString()


            intent.putExtra("Jugador", nombre_Jugador)
            intent.putExtra("score", string_Vcore)
            intent.putExtra("vidas", string_Vidas)



            mp.stop()
            mp.release()
            startActivity(intent)
            finish()

        }
    }

fun baseDeDatos() {
    val admin = AdminnSALiteOpenHelper(this, "BD", null, 1)
    val BD = admin.writableDatabase

    val consulta = BD.rawQuery("select * " +
            "from puntaje " +
            "where score = " +
            "(select max(score) " +
            "from puntaje)", null)
    if (consulta.moveToFirst()) {
        val temp_Nombre = consulta.getString(0)
        val temp_Score = consulta.getString(1)

        val bestScore = temp_Score.toInt()

        if (score > bestScore) {
            val modificacion = ContentValues()
            modificacion.put("nombre", nombre_Jugador)
            modificacion.put("score", score)
            BD.update("puntaje", modificacion, "score=$bestScore", null)
        }
    } else {
        val insertar = ContentValues()
        insertar.put("nombre", nombre_Jugador)
        insertar.put("score", score)
        BD.insert("puntaje", null, insertar)
    }
    BD.close()
}
}
