
package com.example.productos

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material.icons.filled.Settings
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productos.ui.theme.*
import java.io.ByteArrayOutputStream
import androidx.compose.ui.window.Dialog




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado global de tema
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { }

            LaunchedEffect(Unit) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }

            // Aplicar tema
            ProductosTheme(darkTheme = isDarkTheme) {
                UIPrincipal(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDark -> isDarkTheme = isDark }
                )
            }
        }
    }
}

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: String,
    val descripcion: String? = null,
    val imagenBase64: String? = null
)

@Composable
fun UIPrincipal(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper1(context) }
    var productos by remember { mutableStateOf(obtenerProductosDesdeDB(context)) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var productoSeleccionadoId by rememberSaveable { mutableStateOf(-2) } // -2 = ninguno, -1 = nuevo

    val productoSeleccionado = productos.find { it.id == productoSeleccionadoId }


    val background = MaterialTheme.colorScheme.background

    if (mostrarDialogo) {
        DialogoProducto(
            producto = productoSeleccionado,
            onGuardar = { prod ->
                if (prod.id == -1) dbHelper.insertarProducto(prod)
                else dbHelper.editarProducto(prod)

                productos = obtenerProductosDesdeDB(context)
                mostrarDialogo = false
                productoSeleccionadoId = -2
            },
            onCancelar = {
                mostrarDialogo = false
                productoSeleccionadoId = -2
            }

        )
    }

    Column(modifier = Modifier.fillMaxSize()
        .background(background) ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Productos disponibles",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingsButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
                IconButton(onClick = {
                    productoSeleccionadoId = -1 // Nuevo producto
                    mostrarDialogo = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }

            }

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { prod ->
                PlaceholderCard(
                    producto = prod,
                    onEditar = {
                        productoSeleccionadoId = prod.id
                        mostrarDialogo = true
                    },
                    onEliminar = {
                        dbHelper.eliminarProducto(prod.id)
                        productos = obtenerProductosDesdeDB(context)
                    }
                )
            }
        }
    }
}

@Composable
fun PlaceholderCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.secondaryContainer,
            contentColor = colors.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            producto.imagenBase64?.let {
                val byteArray = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            } ?: Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    color = colors.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )

                val precioFormateado = try {
                    java.text.NumberFormat.getCurrencyInstance().format(producto.precio.toDouble())
                } catch (e: Exception) {
                    producto.precio
                }

                Text(
                    text = precioFormateado,
                    color = colors.onSecondaryContainer // o onBackground si lo deseas más neutro
                )

                Text(
                    text = producto.descripcion ?: "Sin descripción",
                    color = colors.onSecondaryContainer.copy(alpha = 0.7f) // menor opacidad para diferenciar
                )
            }

            Column {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}


// Estilo común para los TextFields
val textFieldModifier = Modifier
    .fillMaxWidth()
    .heightIn(min = 60.dp)  // Altura mínima aumentada

@Composable
fun DialogoProducto(
    producto: Producto?,
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit
) {
    // Estados del formulario
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var imagenBase64 by remember { mutableStateOf(producto?.imagenBase64) }
    val context = LocalContext.current

    // Launchers para imágenes
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            imagenBase64 = bitmapToBase64(bitmap)
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { imagenBase64 = bitmapToBase64(it) }
    }

    // Configuración adaptable
    val configuration = LocalConfiguration.current
    val isLandscape = remember { configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }

    // Dimensiones adaptativas
    val dialogProperties by remember(isLandscape) {
        mutableStateOf(
            if (isLandscape) {
                // Para horizontal
                DialogProperties(
                    width = 0.8f, // 80% del ancho
                    imageSize = 120.dp,
                    textFieldWidth = 0.5f // Mitad del espacio disponible
                )
            } else {
                // Para vertical
                DialogProperties(
                    width = 0.95f, // 95% del ancho
                    imageSize = 150.dp,
                    textFieldWidth = 1f // Ancho completo
                )
            }
        )
    }

    Dialog(
        onDismissRequest = onCancelar,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = if (isLandscape) 800.dp else 500.dp)
                .fillMaxWidth(dialogProperties.width)
        ) {
            if (isLandscape) {
                // Diseño horizontal - Dos columnas
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Columna izquierda - Formulario
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (producto?.id == -1) "Nuevo producto" else "Editar producto",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            modifier = textFieldModifier,
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = precio,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                    precio = input
                                }
                            },
                            label = { Text("Precio") },
                            leadingIcon = { Text("$") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = textFieldModifier,
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = textFieldModifier.height(120.dp), // Altura fija mayor
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { galeriaLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Galería")
                            }
                            Button(
                                onClick = { camaraLauncher.launch(null) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cámara")
                            }
                        }
                    }

                    // Columna derecha - Imagen y botones
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imagenBase64 != null) {
                                val bitmap = remember(imagenBase64) {
                                    Base64.decode(imagenBase64, Base64.DEFAULT).let {
                                        BitmapFactory.decodeByteArray(it, 0, it.size)
                                    }
                                }
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen del producto",
                                    modifier = Modifier
                                        .size(dialogProperties.imageSize)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(dialogProperties.imageSize)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Placeholder de imagen",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onCancelar) {
                                Text("Cancelar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (validarFormulario(context, nombre, precio, descripcion)) {
                                        onGuardar(
                                            Producto(
                                                id = producto?.id ?: -1,
                                                nombre = nombre,
                                                precio = precio,
                                                descripcion = descripcion,
                                                imagenBase64 = imagenBase64
                                            )
                                        )
                                    }
                                }
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            } else {
                // Diseño vertical - Una columna
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (producto?.id == -1) "Nuevo producto" else "Editar producto",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d{0,7}(\\.\\d{0,2})?$"))) {
                                precio = input
                            }
                        }
                        ,
                        label = { Text("Precio") },
                        leadingIcon = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { galeriaLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Galería")
                        }
                        Button(
                            onClick = { camaraLauncher.launch(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cámara")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenBase64 != null) {
                            val bitmap = remember(imagenBase64) {
                                Base64.decode(imagenBase64, Base64.DEFAULT).let {
                                    BitmapFactory.decodeByteArray(it, 0, it.size)
                                }
                            }
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagen del producto",
                                modifier = Modifier
                                    .size(dialogProperties.imageSize)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(dialogProperties.imageSize)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Placeholder de imagen",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onCancelar) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (validarFormulario(context, nombre, precio, descripcion)) {
                                    onGuardar(
                                        Producto(
                                            id = producto?.id ?: -1,
                                            nombre = nombre,
                                            precio = precio,
                                            descripcion = descripcion,
                                            imagenBase64 = imagenBase64
                                        )
                                    )
                                }
                            }
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

// Clase de datos para propiedades del diálogo
private data class DialogProperties(
    val width: Float,
    val imageSize: Dp,
    val textFieldWidth: Float
)

// Función de validación del formulario
private fun validarFormulario(context: Context, nombre: String, precio: String, descripcion: String): Boolean {
    return when {
        nombre.isBlank() -> {
            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            false
        }
        precio.isBlank() -> {
            Toast.makeText(context, "El precio no puede estar vacío", Toast.LENGTH_SHORT).show()
            false
        }
        precio.toDoubleOrNull() == null -> {
            Toast.makeText(context, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            false
        }
        !precio.matches(Regex("^\\d{1,7}(\\.\\d{1,2})?$")) -> {
            Toast.makeText(context, "El precio debe tener hasta 7 enteros y 2 decimales", Toast.LENGTH_SHORT).show()
            false
        }
        descripcion.isBlank() -> {
            Toast.makeText(context, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}





fun bitmapToBase64(bitmap: Bitmap): String {
    val output = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
}

fun obtenerProductosDesdeDB(context: android.content.Context): List<Producto> {
    val dbHelper = DBHelper1(context)
    val db = dbHelper.readableDatabase
    val cursor = db.rawQuery("SELECT id_producto, nombre, precio, descripcion, imagen FROM Producto", null)

    val lista = mutableListOf<Producto>()
    if (cursor.moveToFirst()) {
        do {
            lista.add(
                Producto(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    precio = cursor.getDouble(2).toString(),
                    descripcion = cursor.getString(3),
                    imagenBase64 = cursor.getString(4)
                )
            )
        } while (cursor.moveToNext())
    }
    cursor.close()
    db.close()
    return lista
}


@Composable
fun SettingsButton(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showPaletteMenu by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Ícono de tuerca para abrir el diálogo principal
    IconButton(onClick = { showSettingsDialog = true }) {
        Icon(Icons.Default.Settings, contentDescription = "Configuración")
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest  = { showSettingsDialog = false },
            containerColor    = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor  = MaterialTheme.colorScheme.onSecondaryContainer,

            title = { Text("Configuración") },
            text = {
                // Un solo composable que agrupe todo
                Row(horizontalArrangement = Arrangement.spacedBy(45.dp)) {
                    // Botón Tema + sub-menú
                    Box {
                        IconButton(onClick = { showPaletteMenu = true }) {
                            Icon(Icons.Default.Palette, contentDescription = "Cambiar tema")
                        }
                        DropdownMenu(
                            expanded          = showPaletteMenu,
                            onDismissRequest  = { showPaletteMenu = false },
                            modifier         = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            DropdownMenuItem(
                                text    = { Text("Tema Claro", color = MaterialTheme.colorScheme.onTertiaryContainer) },
                                colors = MenuDefaults.itemColors(
                                    textColor       = MaterialTheme.colorScheme.onTertiaryContainer,
                                    leadingIconColor= MaterialTheme.colorScheme.onTertiaryContainer),
                                onClick = { onToggleTheme(false); showPaletteMenu = false }
                            )
                            DropdownMenuItem(
                                text    = { Text("Tema Oscuro", color = MaterialTheme.colorScheme.onTertiaryContainer) },
                                colors = MenuDefaults.itemColors(
                                    textColor       = MaterialTheme.colorScheme.onTertiaryContainer,
                                    leadingIconColor= MaterialTheme.colorScheme.onTertiaryContainer),
                                onClick = { onToggleTheme(true); showPaletteMenu = false }
                            )
                        }
                    }

                    // Botón Info
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Información del Equipo")
                    }
                    // Sub-diálogo Info
                    if (showInfoDialog) {
                        AlertDialog(
                            onDismissRequest   = { showInfoDialog = false },
                            containerColor     = MaterialTheme.colorScheme.secondaryContainer,
                            titleContentColor  = MaterialTheme.colorScheme.onSecondaryContainer,
                            textContentColor   = MaterialTheme.colorScheme.onSecondaryContainer,
                            title = { Text("Acerca de los creadores") },
                            text = { Text("Desarrollado por:\n- 2022370270 - Almaraz Paulín Lisset Ameyalli" +
                                    " \n- 2022620006 - Castro Ramírez Joshua\n" +
                                    "- 2021301918 -  Diaz Hidalgo Fernando\n"+
                                    "\n 2025 DreamTeam©") },
                            confirmButton = {
                                TextButton(onClick = { showInfoDialog = false }) {
                                    Text("Cerrar")
                                }
                            }
                        )
                    }

                    // Botón Ayuda
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.Default.Help, contentDescription = "Ayuda")
                    }
                    // Sub-diálogo Ayuda
                    if (showHelpDialog) {
                        AlertDialog(
                            onDismissRequest   = { showInfoDialog = false },
                            containerColor     = MaterialTheme.colorScheme.secondaryContainer,
                            titleContentColor  = MaterialTheme.colorScheme.onSecondaryContainer,
                            textContentColor   = MaterialTheme.colorScheme.onSecondaryContainer,
                            title = { Text("Ayuda") },
                            text = {
                                Text(
                                    "Para agregar un producto, toca el botón +.\n" +
                                            "Para editar/eliminar, usa los íconos correspondientes."
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = { showHelpDialog = false }) {
                                    Text("Entendido")
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}


