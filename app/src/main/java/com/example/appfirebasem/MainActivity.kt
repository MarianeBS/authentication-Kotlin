package com.example.appfirebasem

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirebasem.ui.theme.AppFirebaseMTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            AppFirebaseMTheme {
                AuthenticationScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AuthenticationScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var isLogin by remember { mutableStateOf(true) }

        val pinkBackground = Color(0xFFFFFFFF) // Fundo em tom rosado
        val pinkPrimary = Color(0xFFD81B60)    // Cor primária rosa para destaque

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(pinkBackground),  // Fundo rosa
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLogin) "Login" else "Criar Conta",
                style = MaterialTheme.typography.headlineMedium,
                color = pinkPrimary  // Cor do título em rosa
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Exibe a mensagem de erro ou sucesso
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                color = if (message.startsWith("Erro")) Color(0xFFD32F2F) else Color(0xFF388E3C)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail", color = pinkPrimary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = pinkPrimary,
                    cursorColor = pinkPrimary,

                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha", color = pinkPrimary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = pinkPrimary,
                    cursorColor = pinkPrimary,

                ),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isLogin) {
                        // Fazer login com o usuário existente
                        if (validateInput(email, password)) {
                            loginUser(email, password) { success, errorMessage ->
                                message = if (success) {
                                    "Login bem-sucedido!"
                                } else {
                                    errorMessage ?: "Erro desconhecido."
                                }
                            }
                        } else {
                            message = "Por favor, insira um e-mail válido e uma senha de pelo menos 6 caracteres."
                        }
                    } else {
                        // Criar um novo usuário
                        if (validateInput(email, password)) {
                            createUser(email, password) { success, errorMessage ->
                                message = if (success) {
                                    "Conta criada com sucesso!"
                                } else {
                                    errorMessage ?: "Erro desconhecido."
                                }
                            }
                        } else {
                            message = "Por favor, insira um e-mail válido e uma senha de pelo menos 6 caracteres."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = pinkPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Login" else "Criar Conta", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para alternar entre Login e Cadastro
            TextButton(onClick = {
                isLogin = !isLogin
                email = ""
                password = ""
                message = ""
            }) {
                Text(
                    if (isLogin) "Não tem uma conta? Criar conta" else "Já tem uma conta? Fazer login",
                    color = pinkPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
    }

    private fun createUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "onCreate: Sucesso")
                    callback(true, null)
                } else {
                    Log.i(TAG, "onCreate: Falha -> ${task.exception}")
                    callback(false, task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    private fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Login: Sucesso")
                    callback(true, null)
                } else {
                    Log.i(TAG, "Login: Falha -> ${task.exception}")
                    callback(false, task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
