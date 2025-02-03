package com.example.piec_1

import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.piec_1.ui.theme.ButtonCamera
import com.example.piec_1.ui.theme.ButtonColor
import com.example.piec_1.ui.theme.PrimaryColor
import com.example.piec_1.ui.theme.SecondaryColor

class Interface {

    //Tela Inicial
    @Composable
    fun TelaInicial(navController: NavController) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryColor, SecondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            ContentCard(navController)
        }
    }

    //Conteudo da tela inicial
    @Composable
    fun ContentCard(navController: NavController) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(900.dp)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(

                    painter = painterResource(id = R.drawable.iconetransparente),
                    contentDescription = "Icone Coração",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(420.dp)
                        .height(400.dp)

                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MedTrack",
                        fontFamily = MontserratFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("TelaCadastro")
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .width(260.dp)
                        .height(60.dp)
                ) {
                    Text(
                        text = "Iniciar",
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MontserratFont
                    )
                }
            }
        }
    }

    //Criar uma navegação entre as telas
//    @Composable
//    fun AppNavigation() {
//        val navController = rememberNavController()
//
//        NavHost(
//            navController = navController,
//            startDestination = "TelaInicial"
//        ) {
//            composable("TelaInicial") {
//                TelaInicial(navController)
//            }
//            composable("TelaCadastro") {
//                TelaCadastro(navController)
//            }
//            composable("TelaPrincipal"){
//                TelaPrincipal(navController)
//            }
//            composable("TelaCamera"){
//                val viewModel: CameraViewModel = viewModel()
//                TelaCamera(navController, viewModel)
//
//            }
//        }
//    }

    //Interface Tela de Cadastro
    @Composable
    fun TelaCadastro(navController: NavController) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryColor, SecondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(750.dp)
                    .background(
                        color = Color.White,
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.TopCenter)


            {
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Icone Coração",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(47.dp)
                        .height(47.dp)
                        .align(Alignment.TopStart)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 100.dp)
                ){
                    Text(
                        text = "Entrar",
                        fontFamily = RobotoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 46.sp
                    )
                    Text(
                        text = "Preencha os campos abaixo.",
                        fontFamily = RobotoFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(top = 10.dp)

                    )
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        EntradaDeTextoMaterial3("Usuário")
                        EntradaDeTextoMaterial3("Senha", isPassword = true)

                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = {navController.navigate("TelaPrincipal")},
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                        modifier = Modifier
                            .width(260.dp)
                            .height(50.dp)
                            .padding(top = 0.dp)
                    ) {
                        Text(
                            text = "Entrar",
                            color = Color.White,
                            fontFamily = RobotoFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = {}) {
                        Text(
                            modifier = Modifier.padding(top = 0.dp),
                            text = "Esqueceu sua senha?",
                            fontSize = 14.sp,
                            fontFamily = RobotoFont,

                            )
                    }
                }
            }
        }
    }

    //Caixa de Texto da Tela de cadastro
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EntradaDeTextoMaterial3(label: String, isPassword: Boolean = false) {
        var text by remember { mutableStateOf("") }

        Text(
            text = label + "*",
            fontSize = 16.sp,
            fontFamily = RobotoFont,
            color = Color.Black,
            modifier = Modifier.padding(top = 10.dp)

        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = SecondaryColor,
                unfocusedBorderColor = Color.Gray,
                cursorColor = SecondaryColor,
                focusedLabelColor = SecondaryColor,
                unfocusedLabelColor = Color(0xFF999999),
                focusedTextColor = Color.Black

            )
        )
    }

    //Tela principal
    @Composable
    fun TelaPrincipal(navController: NavController){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryColor, SecondaryColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(750.dp)
                    .background(
                        color = Color.White,
                    )
                    .padding(16.dp)

            ){
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Icone Coração",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(47.dp)
                        .height(47.dp))


                Icon(painter = painterResource(id = R.drawable.img_1),
                    contentDescription = "Perfil",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .align(Alignment.TopEnd))

                Button(onClick = {navController.navigate("TelaCamera")},
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomEnd)

                ) {
                    Icon(painter = painterResource(id = R.drawable.img_2),
                        contentDescription = "imagem de uma câmera",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .width(78.dp)
                            .height(58.dp))
                }
                Box(modifier = Modifier
                    .width(363.dp)
                    .height(590.dp)
                    .padding(top = 60.dp)
                    .background(ButtonCamera)
                    .align(Alignment.TopStart)
                    .verticalScroll(rememberScrollState()))
                {
                    Column (modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)){
                        repeat(20){
                            Timers("Rivotril", "18:50")
                        }
                    }


                }


            }


        }}

    //Horário dos remédios
    @Composable
    fun Timers(label: String, time: String){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(bottom = 6.dp)
            .background(
                Color.White
            ),
            contentAlignment = Alignment.CenterStart)

        {
            Icon(painter = painterResource(id = R.drawable.img),
                contentDescription = "foto do remédio",
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .padding(10.dp))

            Text(
                text = "$label - $time",
                fontWeight = FontWeight.Normal,
                fontFamily = RobotoFont,
                fontSize = 32.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)

            )
        }
    }

    // Tela da Câmera
    @Composable
    fun TelaCamera(
        navController: NavController,
        viewModel: CameraViewModel = viewModel()
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val previewView = remember { PreviewView(context) }

        LaunchedEffect(Unit) {
            viewModel.startCamera(previewView, lifecycleOwner)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = {
                    viewModel.capturePhoto(context) { imagePath ->
                        Toast.makeText(context, "Foto salva em: $imagePath", Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomEnd)

            ) {
                Icon(painter = painterResource(id = R.drawable.img_2),
                    contentDescription = "imagem de uma câmera",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .width(78.dp)
                        .height(58.dp))
            }
        }
    }


    //Fontes utilizadas
    private val MontserratFont = FontFamily(
        Font(R.font.montserratbold, FontWeight.Bold),
        Font(R.font.montserratthin, FontWeight.Thin)
    )
    private val RobotoFont = FontFamily(
        Font(R.font.robotobold,FontWeight.Bold),
        Font(R.font.robotoblack, FontWeight.Black),
        Font(R.font.robotoregular, FontWeight.Normal)
    )


}