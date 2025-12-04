package com.cs407.saleselector.ui.screen

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.saleselector.auth.EmailResult
import com.cs407.saleselector.auth.PasswordResult
import com.cs407.saleselector.auth.signIn
import com.cs407.saleselector.auth.validateEmail
import com.cs407.saleselector.auth.validatePassword
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(text = error, color = colorResource(id = com.cs407.saleselector.R.color.white), textAlign = TextAlign.Center)
}

@Composable
fun userEmail(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(stringResource(com.cs407.saleselector.R.string.email_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun userPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(stringResource(com.cs407.saleselector.R.string.password_hint)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun LogInSignUpButton(
    email: String,
    password: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        border = BorderStroke(2.dp, colorResource(id = com.cs407.saleselector.R.color.dark_blue)),
        colors = ButtonDefaults.buttonColors(colorResource(id = com.cs407.saleselector.R.color.white)),

    ) {
        Text(stringResource(com.cs407.saleselector.R.string.login_button),
            color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onToCreate: () -> Unit,
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Log In", style = MaterialTheme.typography.displayLarge,
                    color = colorResource(id = com.cs407.saleselector.R.color.white))},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorResource(id = com.cs407.saleselector.R.color.light_blue)),
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = colorResource(com.cs407.saleselector.R.color.light_blue)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ){
            OutlinedTextField( // userEmail
                value = email,
                onValueChange = {email = it},
                label = {Text("Email")},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                ))
            OutlinedTextField(  // userPassword
                value = password,
                onValueChange = {password = it},
                label = {Text("Password")},
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedBorderColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    focusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedLabelColor = colorResource(id = com.cs407.saleselector.R.color.dark_blue),
                    cursorColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    focusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white),
                    unfocusedTextColor = colorResource(id = com.cs407.saleselector.R.color.white)
                ))
            LogInSignUpButton(
                email = email,
                password = password,
                modifier = Modifier.fillMaxWidth(),

                onClick = {
                    error = null

                    // Validate email
                    when (validateEmail(email)) {
                        EmailResult.Empty -> {
                            error = context.getString(com.cs407.saleselector.R.string.empty_email)
                            return@LogInSignUpButton
                        }
                        EmailResult.Invalid -> {
                            error = context.getString(com.cs407.saleselector.R.string.invalid_email)
                            return@LogInSignUpButton
                        }
                        EmailResult.Valid -> {}
                    }

                    // Validate password
                    when (validatePassword(password)) {
                        PasswordResult.Empty -> {
                            error = context.getString(com.cs407.saleselector.R.string.empty_password)
                            return@LogInSignUpButton
                        }
                        PasswordResult.Short -> {
                            error = context.getString(com.cs407.saleselector.R.string.short_password)
                            return@LogInSignUpButton
                        }
                        PasswordResult.Invalid -> {
                            error = context.getString(com.cs407.saleselector.R.string.invalid_password)
                            return@LogInSignUpButton
                        }
                        PasswordResult.Valid -> {}
                    }

                    // Attempt sign in
                    signIn(email, password) { success, message, uid ->
                        if (success && uid != null) {
                            // Check if user has display name
                            val auth = Firebase.auth
                            val currentUser = auth.currentUser
                            val displayName = currentUser?.displayName

                            kotlinx.coroutines.MainScope().launch {
                                onLogin()
                            }

                            onLogin()
                        } else {
                            error = message ?: "Authentication failed"
                        }
                    }
                }
            )
            ErrorText(error = error,)
            TextButton(onClick = onToCreate) {
                Text(text ="If you don't have an account, create one!",
                    color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))
            }
            Divider(color = colorResource(id = com.cs407.saleselector.R.color.dark_blue),)
            TextButton(onClick = {
                //implement in the future
            }){
                Text("Forgot Password?",
                    color = colorResource(id = com.cs407.saleselector.R.color.dark_blue))
            }
        }
    }
}