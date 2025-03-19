package com.example.holatoasts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UIPrincipal() }
    }
}

@Composable
fun UIPrincipal() {
    var cadTxtOp1 by remember { mutableStateOf("") }
    var cadTxtOp2 by remember { mutableStateOf("") }
    var cadTxtRes by remember { mutableStateOf("") }

    fun btnLimpiar_click(){
        cadTxtOp1=""
        cadTxtOp2=""
        cadTxtRes=""

    }

    fun btnSumar_click(){
        val op1 = cadTxtOp1.toIntOrNull() ?: 0
        val op2 = cadTxtOp2.toIntOrNull() ?: 0
        cadTxtRes = (op1 + op2).toString()


    }


    Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Top, Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
            Text("Op1" , Modifier.weight(1f))
            Text("Op2" , Modifier.weight(1f))
            Text("Res" , Modifier.weight(1f))

        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
            TextField(value = cadTxtOp1 , onValueChange = {cadTxtOp1 = it}, Modifier.weight(1f))
            TextField(value = cadTxtOp2 , onValueChange = {cadTxtOp2 = it}, Modifier.weight(1f))
            TextField(value = cadTxtRes , onValueChange = {cadTxtRes = it}, Modifier.weight(1f))

        }
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
            Button(onClick = { btnLimpiar_click()} , Modifier.weight(1.5f)) {
                Text("Limpiar")
            }
            Button(onClick = { btnSumar_click()} , Modifier.weight(1.5f)) {
                Text("Sumar")
            }

        }
    }





    /*
    var cadTxtNombre by remember { mutableStateOf("") }
    var cadlblMensaje by remember { mutableStateOf("Área del mensaje") }

    fun btnSaludar_click(){
        cadlblMensaje = "Hola $cadTxtNombre"
    }


    Column( Modifier.fillMaxSize().padding(16.dp), Arrangement.Top, Alignment.Start ){
        Text("¿Cómo te llamas?")
        TextField( value = cadTxtNombre, onValueChange = {cadTxtNombre = it})
        Button(onClick = { btnSaludar_click() }) {
            Text("Saludar!!")

        }
        Text( cadlblMensaje )
    }
    */







    /*
    val contexto = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var saludo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Ingrese su nombre:")

        BasicTextField(
            value = nombre,
            onValueChange = { nombre = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(onClick = {
            if (nombre.isNotBlank()) {
                saludo = "Hola $nombre!"
                Toast.makeText(contexto, saludo, Toast.LENGTH_SHORT).show()
            } else {
                saludo = "Por favor, ingrese su nombre."
            }
        }) {
            Text("Saludar!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Text(text = saludo)
    }*/
}

@Preview(showBackground = true)
@Composable
fun Previsualizacion() {
    UIPrincipal()
}
