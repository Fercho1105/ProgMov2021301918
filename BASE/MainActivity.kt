package com.example.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.base.ui.theme.BASETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BASETheme {
                UIPrincipal()
            }
        }
    }
}

data class Producto(
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val imagenBase64: String
)

fun obtenerProductos(context: Context): List<Producto> {
    val lista = mutableListOf<Producto>()
    val db = DBHelper1(context).readableDatabase
    // Se asume que la tabla tiene columnas: nombre, precio, descripcion, imagen
    val cursor = db.rawQuery("SELECT nombre, precio, descripcion, imagen FROM Producto", null)

    if (cursor.moveToFirst()) {
        do {
            val nombre = cursor.getString(0)
            // Se formatea el precio con $ y se lee como double
            val precio = "$ ${cursor.getDouble(1)}"
            val descripcion = cursor.getString(2) ?: ""
            val imagenBase64 = cursor.getString(3) ?: ""
            lista.add(Producto(nombre, precio, descripcion, imagenBase64))
        } while (cursor.moveToNext())
    }

    cursor.close()
    db.close()

    return lista
}

@Composable
fun UIPrincipal() {
    val context = LocalContext.current
    val productos = obtenerProductos(context)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Button(
                onClick = { /* TODO: Acción para agregar producto */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xffcca9dd),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.size(35.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Lista de productos
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { prod ->
                PlaceholderCard(
                    producto = prod.nombre,
                    precio = prod.precio,
                    descripcion = prod.descripcion,
                    imagenBase64 = prod.imagenBase64
                )
            }
        }
    }
}

@Composable
fun PlaceholderCard(
    producto: String,
    precio: String,
    descripcion: String,
    imagenBase64: String
) {
    // Convertir la cadena Base64 a un Bitmap y luego a ImageBitmap
    val imageBitmap = remember(imagenBase64) {
        try {
            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
            val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Producto",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = precio,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                IconButton(onClick = { /* TODO: Acción editar */ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar"
                    )
                }
                IconButton(onClick = { /* TODO: Acción eliminar */ }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previzualizacion() {
    BASETheme {
        UIPrincipal()
    }
}
