package com.example.pmaapp.Screens

import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

// Keys for SharedPreferences
private const val PREFS_NAME = "UserCredentials"
private const val KEY_EMAIL = "email"
private const val KEY_COACH_NAME = "coachName"
private const val KEY_TEAM_NAME = "teamName"
private const val KEY_REMEMBER_USER = "rememberUser"

@Composable
fun SigninScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Check if we have saved credentials
    val savedEmail = prefs.getString(KEY_EMAIL, "") ?: ""
    val savedCoachName = prefs.getString(KEY_COACH_NAME, "") ?: ""
    val savedTeamName = prefs.getString(KEY_TEAM_NAME, "") ?: ""
    val rememberedUser = prefs.getBoolean(KEY_REMEMBER_USER, false)

    var email by remember { mutableStateOf(if (rememberedUser) savedEmail else "") }
    var coachName by remember { mutableStateOf(if (rememberedUser) savedCoachName else "") }
    var teamName by remember { mutableStateOf(if (rememberedUser) savedTeamName else "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(rememberedUser) }

    val coroutineScope = rememberCoroutineScope()

    auth = Firebase.auth

    // Check if user is already signed in
    DisposableEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified && rememberedUser) {
            // User is already signed in and remembered
            navController.navigate("home/$savedCoachName/$savedTeamName")
        }
        onDispose { }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Using verticalScroll for natural scrolling of the entire column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.player_1),
                contentDescription = "Logo",
                modifier = Modifier.size(400.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login Title
            Text(
                text = "Sign In",
                color = Color.White,
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Please sign in to continue",
                color = Color.Gray,
                style = TextStyle(fontSize = 14.sp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                        contentDescription = "Email Icon",
                        tint = FotGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                    unfocusedLabelColor = Color.White,
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
                label = { Text("Password", color = Color.White) },
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
                    unfocusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                    unfocusedLabelColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Remember Me checkbox and Forgot Password link in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FotGreen,
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "Remember Me",
                        color = Color.White,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }

                Text(
                    text = "Forgot Password?",
                    color = FotGreen,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.clickable {
                        // Navigate to forgot password screen (if available)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Coach Name Input
            OutlinedTextField(
                value = coachName,
                onValueChange = { coachName = it },
                label = { Text("Coach Name", color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                        contentDescription = "Coach Name Icon",
                        tint = FotGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                    unfocusedLabelColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Team Name Input
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text("Team Name", color = Color.White) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                        contentDescription = "Team Name Icon",
                        tint = FotGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedPlaceholderColor = FotGreen,
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedBorderColor = FotGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = FotGreen,
                    unfocusedLabelColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Login Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            // Sign in with email and password
                            val authResult = auth.signInWithEmailAndPassword(email, password).await()
                            val user = authResult.user

                            // Check if email is verified
                            if (user != null && user.isEmailVerified) {
                                // Save credentials if remember me is checked
                                if (rememberMe) {
                                    saveUserCredentials(context, email, coachName, teamName, true)
                                } else {
                                    // Clear saved credentials if remember me is unchecked
                                    clearUserCredentials(context)
                                }

                                // Navigate to home screen
                                navController.navigate("home/$coachName/$teamName")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please verify your email first.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Sign-in error: ${e.message}")
                            Toast.makeText(
                                context,
                                "Sign-in failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FotGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 7.dp
                )
            ) {
                Text(
                    text = "Login",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Up Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign Up",
                    color = FotGreen,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.clickable {
                        navController.navigate(AppRoutes.SIGNUP_ROUTE)
                    }
                )
            }
        }
    }
}

// Function to save user credentials
private fun saveUserCredentials(context: Context, email: String, coachName: String, teamName: String, remember: Boolean) {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.putString(KEY_EMAIL, email)
    editor.putString(KEY_COACH_NAME, coachName)
    editor.putString(KEY_TEAM_NAME, teamName)
    editor.putBoolean(KEY_REMEMBER_USER, remember)

    editor.apply()
}

// Function to clear saved credentials
private fun clearUserCredentials(context: Context) {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.clear()
    editor.apply()
}