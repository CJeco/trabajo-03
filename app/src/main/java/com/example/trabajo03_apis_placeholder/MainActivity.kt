package com.example.trabajo03_apis_placeholder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trabajo03_apis_placeholder.data.JsonPlaceholderApi
import com.example.trabajo03_apis_placeholder.data.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loadingScreen = findViewById<LinearLayout>(R.id.loadingScreen)
        val mainContent = findViewById<ScrollView>(R.id.mainContent)
        val textContainers = listOf(
            findViewById<TextView>(R.id.textContainer1),
            findViewById<TextView>(R.id.textContainer2),
            findViewById<TextView>(R.id.textContainer3),
            findViewById<TextView>(R.id.textContainer4),
            findViewById<TextView>(R.id.textContainer5)
        )

        // Config Retrofit con la endpoint base de jsonplaceholder
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(JsonPlaceholderApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Iniciamos la carga de datos
                val todos = mutableListOf<Todo>()
                for (i in 1..5) {
                    val response = apiService.getTodoById(i)
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let { todos.add(it) }
                    }
                }

                // Forzamos la espera de 5 segundos solicitada
                delay(5.seconds)

                withContext(Dispatchers.Main) {
                    if (todos.isNotEmpty()) {
                        todos.forEachIndexed { index, todo ->
                            if (index < textContainers.size) {
                                val status = if (todo.completed) getString(R.string.status_completed) else getString(R.string.status_pending)
                                textContainers[index].text = getString(R.string.todo_format, todo.id, todo.title, status)
                            }
                        }
                        loadingScreen.visibility = View.GONE
                        mainContent.visibility = View.VISIBLE
                    } else {
                        Log.e("API_ERROR", "No se obtuvieron tareas")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Error en la solicitud: ${e.message}")
            }
        }
    }
}
