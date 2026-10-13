package com.example.colecaojogos

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class Jogo(
    val id: Long,
    val nome: String,
    val plataforma: String,
    val genero: String,
    val ano: Int,
    val estado: String,
    val favorito: Boolean = false,
    val capa: String? = null
)

data class Perfil(
    val nome: String = "",
    val avatar: String? = null,
    val banner: String? = null
)

fun copiarImagem(context: Context, uri: Uri, nome: String): String? {
    return try {
        val destino = File(context.filesDir, nome)
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            FileOutputStream(destino).use { saida -> entrada.copyTo(saida) }
        }
        destino.absolutePath
    } catch (e: Exception) {
        null
    }
}

fun carregarBitmap(caminho: String?, max: Int = 1280): ImageBitmap? {
    if (caminho.isNullOrBlank() || !File(caminho).exists()) return null
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(caminho, limites)
    var escala = 1
    while (limites.outWidth / escala > max || limites.outHeight / escala > max) escala *= 2
    val opcoes = BitmapFactory.Options().apply { inSampleSize = escala }
    return BitmapFactory.decodeFile(caminho, opcoes)?.asImageBitmap()
}

class Armazenamento(context: Context) {

    private val prefs = context.getSharedPreferences("jogos", Context.MODE_PRIVATE)

    fun carregar(): List<Jogo> {
        val lista = mutableListOf<Jogo>()
        val json = JSONArray(prefs.getString("lista", "[]"))
        for (i in 0 until json.length()) {
            val o = json.getJSONObject(i)
            lista.add(
                Jogo(
                    id = o.getLong("id"),
                    nome = o.getString("nome"),
                    plataforma = o.getString("plataforma"),
                    genero = o.getString("genero"),
                    ano = o.getInt("ano"),
                    estado = o.getString("estado"),
                    favorito = o.optBoolean("favorito"),
                    capa = o.optString("capa", "").ifBlank { null }
                )
            )
        }
        return lista
    }

    fun guardar(jogos: List<Jogo>) {
        val json = JSONArray()
        for (j in jogos) {
            val o = JSONObject()
            o.put("id", j.id)
            o.put("nome", j.nome)
            o.put("plataforma", j.plataforma)
            o.put("genero", j.genero)
            o.put("ano", j.ano)
            o.put("estado", j.estado)
            o.put("favorito", j.favorito)
            o.put("capa", j.capa ?: "")
            json.put(o)
        }
        prefs.edit().putString("lista", json.toString()).apply()
    }

    fun carregarPerfil() = Perfil(
        nome = prefs.getString("nome", "") ?: "",
        avatar = prefs.getString("avatar", null),
        banner = prefs.getString("banner", null)
    )

    fun guardarPerfil(perfil: Perfil) {
        prefs.edit()
            .putString("nome", perfil.nome)
            .putString("avatar", perfil.avatar)
            .putString("banner", perfil.banner)
            .apply()
    }
}
