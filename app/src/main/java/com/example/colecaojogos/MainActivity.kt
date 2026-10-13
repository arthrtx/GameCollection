package com.example.colecaojogos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val banco = Armazenamento(this)
        var jogos by mutableStateOf(banco.carregar())
        var pesquisa by mutableStateOf("")
        var tela by mutableStateOf("lista")
        var editado by mutableStateOf<Jogo?>(null)
        var escuro by mutableStateOf(false)
        var apenasFavoritos by mutableStateOf(false)
        var perfil by mutableStateOf(banco.carregarPerfil())
        var abrirPerfil by mutableStateOf(perfil.nome.isBlank())

        setContent {
            MaterialTheme(colorScheme = if (escuro) darkColorScheme() else lightColorScheme()) {
                BackHandler(enabled = tela == "form") {
                    tela = "lista"
                }

                if (tela == "lista") {
                    TelaLista(
                        jogos = jogos,
                        pesquisa = pesquisa,
                        onPesquisa = { pesquisa = it },
                        onNovo = {
                            editado = null
                            tela = "form"
                        },
                        onEditar = {
                            editado = it
                            tela = "form"
                        },
                        onEliminar = { apagado ->
                            jogos = jogos.filter { it.id != apagado.id }
                            banco.guardar(jogos)
                        },
                        escuro = escuro,
                        onAlternarTema = { escuro = !escuro },
                        apenasFavoritos = apenasFavoritos,
                        onFiltrarFavoritos = { apenasFavoritos = !apenasFavoritos },
                        onAlternarFavorito = { favorito ->
                            jogos = jogos.map {
                                if (it.id == favorito.id) it.copy(favorito = !it.favorito) else it
                            }
                            banco.guardar(jogos)
                        },
                        perfil = perfil,
                        onMudarNome = { nome ->
                            perfil = perfil.copy(nome = nome)
                            banco.guardarPerfil(perfil)
                            abrirPerfil = false
                        },
                        onMudarAvatar = { caminho ->
                            perfil = perfil.copy(avatar = caminho)
                            banco.guardarPerfil(perfil)
                        },
                        onMudarBanner = { caminho ->
                            perfil = perfil.copy(banner = caminho)
                            banco.guardarPerfil(perfil)
                        },
                        abrirPerfilInicial = abrirPerfil
                    )
                } else {
                    TelaFormulario(
                        jogo = editado,
                        banner = perfil.banner,
                        escuro = escuro,
                        onGuardar = { novo ->
                            jogos = if (editado == null) {
                                jogos + novo
                            } else {
                                jogos.map { if (it.id == novo.id) novo else it }
                            }
                            banco.guardar(jogos)
                            tela = "lista"
                        },
                        onCancelar = { tela = "lista" }
                    )
                }
            }
        }
    }
}
