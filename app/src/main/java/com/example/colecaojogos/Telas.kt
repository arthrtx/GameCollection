package com.example.colecaojogos

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val estados = listOf("Por Jogar", "A Jogar", "Concluído")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLista(
    jogos: List<Jogo>,
    pesquisa: String,
    onPesquisa: (String) -> Unit,
    onNovo: () -> Unit,
    onEditar: (Jogo) -> Unit,
    onEliminar: (Jogo) -> Unit,
    onAlternarFavorito: (Jogo) -> Unit,
    apenasFavoritos: Boolean,
    onFiltrarFavoritos: () -> Unit,
    perfil: Perfil,
    onMudarNome: (String) -> Unit,
    onMudarAvatar: (String) -> Unit,
    onMudarBanner: (String) -> Unit,
    escuro: Boolean,
    onAlternarTema: () -> Unit,
    abrirPerfilInicial: Boolean = false
) {
    var apagar by remember { mutableStateOf<Jogo?>(null) }
    var jogoAberto by remember { mutableStateOf<Long?>(null) }
    var editarNome by remember { mutableStateOf(abrirPerfilInicial) }
    val contexto = LocalContext.current

    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val caminho = copiarImagem(contexto, uri, "avatar.jpg")
            if (caminho != null) onMudarAvatar(caminho)
        }
    }
    val bannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val caminho = copiarImagem(contexto, uri, "banner.jpg")
            if (caminho != null) onMudarBanner(caminho)
        }
    }

    val visiveis = jogos.filter {
        it.nome.contains(pesquisa, true) && (!apenasFavoritos || it.favorito)
    }
    val bannerBitmap = remember(perfil.banner) { carregarBitmap(perfil.banner) }
    val avatarBitmap = remember(perfil.avatar) { carregarBitmap(perfil.avatar, 512) }

    Box(Modifier.fillMaxSize()) {
        if (bannerBitmap != null) {
            Image(
                bitmap = bannerBitmap,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF1C1C1E), Color(0xFF4A2E2E))))
            )
        }
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (escuro) 0.78f else 0.35f)))

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNovo,
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar jogo")
                }
            }
        ) { padding ->
            Row(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Column(
                    Modifier
                        .width(76.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable {
                                avatarLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    }
                    Text("PERFIL", color = Color.White, fontSize = 9.sp)

                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { editarNome = true }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Mudar nome",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("NOME", color = Color.White, fontSize = 8.sp)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onAlternarTema() }
                    ) {
                        Icon(
                            painterResource(R.drawable.lua),
                            contentDescription = "Mudar tema",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(if (escuro) "CLARO" else "ESCURO", color = Color.White, fontSize = 8.sp)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            bannerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.sem_imagem),
                            contentDescription = "Mudar banner",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("BANNER", color = Color.White, fontSize = 8.sp)
                    }
                }

                Column(Modifier.padding(start = 12.dp).fillMaxSize()) {
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(4.dp)
                        ) {
                            Aba("JOGOS", !apenasFavoritos) { if (apenasFavoritos) onFiltrarFavoritos() }
                            Aba("FAVORITOS", apenasFavoritos) { if (!apenasFavoritos) onFiltrarFavoritos() }
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Olá, " + perfil.nome.ifBlank { "Jogador" },
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${jogos.size} jogos na coleção",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("FAVORITOS", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
                                Text(
                                    "${jogos.count { it.favorito }}",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        shape = RoundedCornerShape(16.dp),
                        value = pesquisa,
                        onValueChange = onPesquisa,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        placeholder = { Text("Pesquisar pelo nome", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = Color.White.copy(alpha = 0.25f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedBorderColor = Color.White
                        )
                    )

                    if (visiveis.isEmpty()) {
                        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Text("Nenhum jogo encontrado", color = Color.White.copy(alpha = 0.8f))
                        }
                    } else {
                        LazyColumn(
                            Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 88.dp)
                        ) {
                            items(visiveis) { jogo ->
                                val expandido = jogoAberto == jogo.id
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            jogoAberto = if (expandido) null else jogo.id
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.15f)
                                    )
                                ) {
                                    if (expandido) {
                                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                                            Row {
                                                CapaJogo(jogo.capa, 100.dp, 140.dp)
                                                Column(Modifier.padding(start = 14.dp)) {
                                                    Text(
                                                        jogo.nome,
                                                        color = Color.White,
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(Modifier.height(10.dp))
                                                    Text(
                                                        "Plataforma: ${jogo.plataforma}",
                                                        color = Color.White,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        "Género: ${jogo.genero}",
                                                        color = Color.White,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        "Ano: ${jogo.ano}",
                                                        color = Color.White,
                                                        fontSize = 16.sp
                                                    )
                                                    Text(
                                                        "Estado: ${jogo.estado}",
                                                        color = Color.White,
                                                        fontSize = 16.sp
                                                    )
                                                }
                                            }
                                            Row(
                                                Modifier.fillMaxWidth().padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(onClick = { onEditar(jogo) }) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Editar",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                }
                                                IconButton(onClick = { apagar = jogo }) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Eliminar",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                }
                                                IconButton(onClick = { onAlternarFavorito(jogo) }) {
                                                    Icon(
                                                        if (jogo.favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                        contentDescription = "Favorito",
                                                        tint = if (jogo.favorito) Color(0xFFFFC107) else Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Row(
                                            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CapaJogo(jogo.capa, 56.dp, 56.dp)
                                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                                Text(
                                                    jogo.nome,
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    "${jogo.plataforma} · ${jogo.genero} · ${jogo.ano}",
                                                    color = Color.White.copy(alpha = 0.75f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    jogo.estado,
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            IconButton(onClick = { onEditar(jogo) }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                                            }
                                            IconButton(onClick = { apagar = jogo }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                                            }
                                            IconButton(onClick = { onAlternarFavorito(jogo) }) {
                                                Icon(
                                                    if (jogo.favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Favorito",
                                                    tint = if (jogo.favorito) Color(0xFFFFC107) else Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    apagar?.let { jogo ->
        AlertDialog(
            onDismissRequest = { apagar = null },
            title = { Text("Eliminar jogo") },
            text = { Text("Quer eliminar \"${jogo.nome}\" da coleção?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(jogo)
                    apagar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { apagar = null }) { Text("Cancelar") }
            }
        )
    }

    if (editarNome) {
        var textoNome by remember { mutableStateOf(perfil.nome) }
        AlertDialog(
            onDismissRequest = { if (!abrirPerfilInicial) editarNome = false },
            title = { Text(if (abrirPerfilInicial) "Crie o seu perfil" else "O seu perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = textoNome,
                        onValueChange = { textoNome = it },
                        label = { Text("Nome") },
                        singleLine = true
                    )
                    if (abrirPerfilInicial) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    avatarLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(if (perfil.avatar != null) "Mudar foto" else "Escolher foto", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    bannerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(if (perfil.banner != null) "Mudar banner" else "Escolher banner", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val nomeFinal = textoNome.trim()
                    if (nomeFinal.isBlank() && abrirPerfilInicial) return@TextButton
                    onMudarNome(nomeFinal)
                    editarNome = false
                }) { Text("Guardar") }
            },
            dismissButton = if (abrirPerfilInicial) null else {
                { TextButton(onClick = { editarNome = false }) { Text("Cancelar") } }
            }
        )
    }
}

@Composable
private fun Aba(texto: String, ativa: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (ativa) Color.White.copy(alpha = 0.25f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Text(
            texto,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (ativa) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CapaJogo(caminho: String?, largura: Dp, altura: Dp) {
    val bitmap = remember(caminho) { carregarBitmap(caminho, 256) }
    Box(
        Modifier
            .width(largura)
            .height(altura)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Capa",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painterResource(R.drawable.sem_imagem),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(altura / 3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFormulario(
    jogo: Jogo?,
    banner: String?,
    escuro: Boolean,
    onGuardar: (Jogo) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(jogo?.nome ?: "") }
    var plataforma by remember { mutableStateOf(jogo?.plataforma ?: "") }
    var genero by remember { mutableStateOf(jogo?.genero ?: "") }
    var ano by remember { mutableStateOf(jogo?.ano?.toString() ?: "") }
    var estado by remember { mutableStateOf(jogo?.estado ?: estados[0]) }
    var erros by remember { mutableStateOf(mapOf<String, String>()) }
    var menuAberto by remember { mutableStateOf(false) }
    var capa by remember { mutableStateOf(jogo?.capa) }
    val contexto = LocalContext.current

    val capaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val idBase = jogo?.id ?: System.currentTimeMillis()
            val caminho = copiarImagem(contexto, uri, "capa_$idBase.jpg")
            if (caminho != null) capa = caminho
        }
    }

    fun guardar() {
        val novos = mutableMapOf<String, String>()
        if (nome.isBlank()) novos["nome"] = "Preencha o nome"
        if (plataforma.isBlank()) novos["plataforma"] = "Preencha a plataforma"
        if (genero.isBlank()) novos["genero"] = "Preencha o género"
        val anoInt = ano.toIntOrNull()
        if (anoInt == null || anoInt < 1970 || anoInt > 2030) novos["ano"] = "Ano inválido"
        erros = novos
        if (novos.isEmpty() && anoInt != null) {
            onGuardar(
                Jogo(
                    id = jogo?.id ?: System.currentTimeMillis(),
                    nome = nome.trim(),
                    plataforma = plataforma.trim(),
                    genero = genero.trim(),
                    ano = anoInt,
                    estado = estado,
                    favorito = jogo?.favorito ?: false,
                    capa = capa
                )
            )
        }
    }

    val bannerBitmap = remember(banner) { carregarBitmap(banner) }

    Box(Modifier.fillMaxSize()) {
        if (bannerBitmap != null) {
            Image(
                bitmap = bannerBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF1C1C1E), Color(0xFF4A2E2E))))
            )
        }
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (escuro) 0.78f else 0.35f)))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (jogo == null) "Novo Jogo" else "Editar Jogo",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancelar) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoVidro(nome, { nome = it }, "Nome", erros["nome"])
                CampoVidro(plataforma, { plataforma = it }, "Plataforma", erros["plataforma"])
                CampoVidro(genero, { genero = it }, "Género", erros["genero"])
                CampoVidro(ano, { ano = it }, "Ano de lançamento", erros["ano"], numero = true)

                Text("Estado", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Box {
                    OutlinedButton(
                        onClick = { menuAberto = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(estado)
                    }
                    DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                        estados.forEach { opcao ->
                            DropdownMenuItem(
                                text = { Text(opcao) },
                                onClick = {
                                    estado = opcao
                                    menuAberto = false
                                }
                            )
                        }
                    }
                }

                Text("Capa do jogo", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val capaBitmap = remember(capa) { carregarBitmap(capa, 512) }
                    Box(
                        Modifier
                            .width(72.dp)
                            .height(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                capaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (capaBitmap != null) {
                            Image(
                                bitmap = capaBitmap,
                                contentDescription = "Capa",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.sem_imagem),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        OutlinedButton(
                            onClick = {
                                capaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Text(if (capa == null) "Escolher imagem" else "Mudar imagem")
                        }
                        if (capa != null) {
                            TextButton(onClick = { capa = null }) {
                                Text("Remover", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { guardar() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
private fun CampoVidro(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    erro: String? = null,
    numero: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(rotulo) },
        isError = erro != null,
        supportingText = { if (erro != null) Text(erro, color = Color(0xFFFFB4AB)) },
        singleLine = true,
        keyboardOptions = if (numero) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        textStyle = TextStyle(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.25f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            errorBorderColor = Color(0xFFFFB4AB),
            errorTextColor = Color.White,
            errorLabelColor = Color(0xFFFFB4AB)
        )
    )
}

private val jogosTeste = listOf(
    Jogo(1, "Zelda Tears of the Kingdom", "Switch", "Aventura", 2023, "Concluído", favorito = true),
    Jogo(2, "Elden Ring", "PC", "RPG", 2022, "A Jogar"),
    Jogo(3, "Hollow Knight", "PC", "Aventura", 2017, "Por Jogar")
)

@Preview(showBackground = true)
@Composable
fun PreviewLista() {
    MaterialTheme {
        TelaLista(
            jogos = jogosTeste,
            pesquisa = "",
            onPesquisa = {},
            onNovo = {},
            onEditar = {},
            onEliminar = {},
            onAlternarFavorito = {},
            apenasFavoritos = false,
            onFiltrarFavoritos = {},
            perfil = Perfil(nome = "Arthur"),
            onMudarNome = {},
            onMudarAvatar = {},
            onMudarBanner = {},
            escuro = false,
            onAlternarTema = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewListaEscuro() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        TelaLista(
            jogos = jogosTeste,
            pesquisa = "",
            onPesquisa = {},
            onNovo = {},
            onEditar = {},
            onEliminar = {},
            onAlternarFavorito = {},
            apenasFavoritos = false,
            onFiltrarFavoritos = {},
            perfil = Perfil(nome = "Arthur"),
            onMudarNome = {},
            onMudarAvatar = {},
            onMudarBanner = {},
            escuro = true,
            onAlternarTema = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormulario() {
    MaterialTheme {
        TelaFormulario(
            jogo = null,
            banner = null,
            escuro = false,
            onGuardar = {},
            onCancelar = {}
        )
    }
}

