package com.example.productos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper1(context: Context) : SQLiteOpenHelper(context, "productos.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE Producto (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                precio REAL NOT NULL,
                descripcion TEXT,
                imagen TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Producto")
        onCreate(db)
    }

    fun insertarProducto(producto: Producto) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("precio", producto.precio.toDoubleOrNull() ?: 0.0)
            put("descripcion", producto.descripcion)
            put("imagen", producto.imagenBase64)
        }
        db.insert("Producto", null, values)
        db.close()
    }

    fun editarProducto(producto: Producto) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("precio", producto.precio.toDoubleOrNull() ?: 0.0)
            put("descripcion", producto.descripcion)
            put("imagen", producto.imagenBase64)
        }
        db.update("Producto", values, "id_producto = ?", arrayOf(producto.id.toString()))
        db.close()
    }

    fun eliminarProducto(idProducto: Int) {
        val db = writableDatabase
        db.delete("Producto", "id_producto = ?", arrayOf(idProducto.toString()))
        db.close()
    }
}
