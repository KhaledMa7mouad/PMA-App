package com.example.pmaapp.Screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pmaapp.R
import com.example.pmaapp.navigation.AppRoutes
import com.example.pmaapp.ui.theme.FotGreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private lateinit var auth: FirebaseAuth

@Composable
fun SignupScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isVerificationSent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    auth = Firebase.auth

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.messi_png),
                contentDescription = "Logo", modifier = Modifier.size(400.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login Title
            Text(
                text = "Signup",
                color = Color.White,
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Please Create an Account to continue",
                color = Color.Gray,
                style = TextStyle(fontSize = 14.sp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Write an Email" , color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                        contentDescription = "Username Icon",
                        tint = FotGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Write a Password" , color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_password_24),
                        contentDescription = "Password Icon",
                        tint = FotGreen
                    )
                },
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.view else R.drawable.close_eye
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Toggle Password Visibility",
                            tint = FotGreen
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Repeat Password Input
            OutlinedTextField(
                value = repassword,
                onValueChange = { repassword = it },
                label = { Text("Repeat Your Password" , color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_password_24),
                        contentDescription = "Password Icon",
                        tint = FotGreen
                    )
                },
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.view else R.drawable.close_eye
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Register Button
            Button(
                onClick = {
                    // Validate passwords match
                    if (password != repassword) {
                        Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    coroutineScope.launch {
                        try {
                            // Check if the email is already in use
                            val signInMethods = auth.fetchSignInMethodsForEmail(email).await()
                            if (signInMethods.signInMethods?.isNotEmpty() == true) {
                                // Email is already in use
                                Toast.makeText(
                                    context,
                                    "Email is already in use by another account.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@launch
                            }

                            // 1. Create user account
                            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                            val user = authResult.user

                            // 2. Send verification email
                            user?.sendEmailVerification()?.await()

                            // 3. Update UI state
                            isVerificationSent = true

                            // 4. Show success message
                            Toast.makeText(
                                context,
                                "Verification email sent! Please check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()

                            // 5. Navigate to the sign-in screen
                            navController.navigate(AppRoutes.SIGNIN_ROUTE)

                        } catch (e: Exception) {
                            Log.w(TAG, "Signup error: ${e.message}")
                            Toast.makeText(
                                context,
                                "Error: ${e.message?.substringAfter("]")?.trim()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FotGreen,
                    contentColor = Color.DarkGray,
                    disabledContainerColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 7.dp
                )
            ) {
                Text("Register")
            }

            // Show verification message if email is sent
            if (isVerificationSent) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Verification email sent! Please check your inbox.",
                    color = FotGreen,
                    style = TextStyle(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign In Link
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign in",
                    modifier = Modifier.clickable { navController.navigate(AppRoutes.SIGNIN_ROUTE) },
                    color = FotGreen,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}