package com.example.pmaapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pmaapp.components.ReusableOutlinedTextField
import com.example.pmaapp.database.AppDatabase
import com.example.pmaapp.database.Player
import com.example.pmaapp.ui.theme.PMAAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(navController: NavController) {
    // 1 Setup Room & coroutine scope
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    // State for showing success message
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // 2 Only this screen in dark mode
    PMAAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Add Player",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    actions = {
                        // Quick save action
                        IconButton(onClick = {
                            // Will be handled by the main save button
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Save player"
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = remember { SnackbarHostState() }) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        ) { paddingValues ->
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Enter the Player Info:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, top = 8.dp)
                    )

                    // 3️⃣ Inline helper to reduce boilerplate
                    @Composable
                    fun StatField(
                        value: String,
                        onValueChange: (String) -> Unit,
                        label: String,
                        type: KeyboardType = KeyboardType.Text,
                        action: ImeAction = ImeAction.Next
                    ) {
                        ReusableOutlinedTextField(
                            value = value,
                            onValueChange = onValueChange,
                            label = label,
                            leadingIconId = android.R.drawable.ic_menu_edit,
                            keyboardType = type,
                            imeAction = action
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // 4️⃣ All your state holders
                    var playerName by remember { mutableStateOf("") }
                    var playerPosition by remember { mutableStateOf("") }
                    var playerAge by remember { mutableStateOf("") }
                    var height by remember { mutableStateOf("") }
                    var weight by remember { mutableStateOf("") }
                    var bestPosition by remember { mutableStateOf("") }
                    var weakFoot by remember { mutableStateOf("") }
                    var skillMoves by remember { mutableStateOf("") }
                    var attackingWorkRate by remember { mutableStateOf("") }
                    var defensiveWorkRate by remember { mutableStateOf("") }
                    var crossing by remember { mutableStateOf("") }
                    var finishing by remember { mutableStateOf("") }
                    var headingAccuracy by remember { mutableStateOf("") }
                    var shortPassing by remember { mutableStateOf("") }
                    var volleys by remember { mutableStateOf("") }
                    var dribbling by remember { mutableStateOf("") }
                    var curve by remember { mutableStateOf("") }
                    var fkAccuracy by remember { mutableStateOf("") }
                    var longPassing by remember { mutableStateOf("") }
                    var ballControl by remember { mutableStateOf("") }
                    var acceleration by remember { mutableStateOf("") }
                    var sprintSpeed by remember { mutableStateOf("") }
                    var agility by remember { mutableStateOf("") }
                    var reactions by remember { mutableStateOf("") }
                    var balance by remember { mutableStateOf("") }
                    var shotPower by remember { mutableStateOf("") }
                    var jumping by remember { mutableStateOf("") }
                    var stamina by remember { mutableStateOf("") }
                    var strength by remember { mutableStateOf("") }
                    var longShots by remember { mutableStateOf("") }
                    var aggression by remember { mutableStateOf("") }
                    var interceptions by remember { mutableStateOf("") }
                    var positioning by remember { mutableStateOf("") }
                    var vision by remember { mutableStateOf("") }
                    var penalties by remember { mutableStateOf("") }
                    var composure by remember { mutableStateOf("") }
                    var marking by remember { mutableStateOf("") }
                    var standingTackle by remember { mutableStateOf("") }
                    var slidingTackle by remember { mutableStateOf("") }
                    var gkDiving by remember { mutableStateOf("") }
                    var gkHandling by remember { mutableStateOf("") }
                    var gkKicking by remember { mutableStateOf("") }
                    var gkPositioning by remember { mutableStateOf("") }
                    var gkReflexes by remember { mutableStateOf("") }

                    // Section title for basic info
                    Text(
                        text = "Basic Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // 5️⃣ Render each field
                    StatField(playerName, { playerName = it }, "Player Name")
                    StatField(playerPosition, { playerPosition = it }, "Position")
                    StatField(playerAge, { playerAge = it }, "Age", KeyboardType.Number)
                    StatField(height, { height = it }, "Height", KeyboardType.Number)
                    StatField(weight, { weight = it }, "Weight", KeyboardType.Number)
                    StatField(bestPosition, { bestPosition = it }, "Best Position")
                    StatField(weakFoot, { weakFoot = it }, "Weak Foot")
                    StatField(skillMoves, { skillMoves = it }, "Skill Moves", KeyboardType.Number)

                    // Section title for work rates
                    Text(
                        text = "Work Rates",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(attackingWorkRate, { attackingWorkRate = it }, "Attacking Work Rate")
                    StatField(defensiveWorkRate, { defensiveWorkRate = it }, "Defensive Work Rate")

                    // Section title for attacking stats
                    Text(
                        text = "Attacking Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(crossing, { crossing = it }, "Crossing", KeyboardType.Number)
                    StatField(finishing, { finishing = it }, "Finishing", KeyboardType.Number)
                    StatField(headingAccuracy, { headingAccuracy = it }, "Heading Accuracy", KeyboardType.Number)
                    StatField(shortPassing, { shortPassing = it }, "Short Passing", KeyboardType.Number)
                    StatField(volleys, { volleys = it }, "Volleys", KeyboardType.Number)

                    // Section title for skill stats
                    Text(
                        text = "Skill Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(dribbling, { dribbling = it }, "Dribbling", KeyboardType.Number)
                    StatField(curve, { curve = it }, "Curve", KeyboardType.Number)
                    StatField(fkAccuracy, { fkAccuracy = it }, "FK Accuracy", KeyboardType.Number)
                    StatField(longPassing, { longPassing = it }, "Long Passing", KeyboardType.Number)
                    StatField(ballControl, { ballControl = it }, "Ball Control", KeyboardType.Number)

                    // Section title for movement stats
                    Text(
                        text = "Movement Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(acceleration, { acceleration = it }, "Acceleration", KeyboardType.Number)
                    StatField(sprintSpeed, { sprintSpeed = it }, "Sprint Speed", KeyboardType.Number)
                    StatField(agility, { agility = it }, "Agility", KeyboardType.Number)
                    StatField(reactions, { reactions = it }, "Reactions", KeyboardType.Number)
                    StatField(balance, { balance = it }, "Balance", KeyboardType.Number)

                    // Section title for power stats
                    Text(
                        text = "Power Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(shotPower, { shotPower = it }, "Shot Power", KeyboardType.Number)
                    StatField(jumping, { jumping = it }, "Jumping", KeyboardType.Number)
                    StatField(stamina, { stamina = it }, "Stamina", KeyboardType.Number)
                    StatField(strength, { strength = it }, "Strength", KeyboardType.Number)
                    StatField(longShots, { longShots = it }, "Long Shots", KeyboardType.Number)

                    // Section title for mentality stats
                    Text(
                        text = "Mentality Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(aggression, { aggression = it }, "Aggression", KeyboardType.Number)
                    StatField(interceptions, { interceptions = it }, "Interceptions", KeyboardType.Number)
                    StatField(positioning, { positioning = it }, "Positioning", KeyboardType.Number)
                    StatField(vision, { vision = it }, "Vision", KeyboardType.Number)
                    StatField(penalties, { penalties = it }, "Penalties", KeyboardType.Number)
                    StatField(composure, { composure = it }, "Composure", KeyboardType.Number)

                    // Section title for defending stats
                    Text(
                        text = "Defending Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(marking, { marking = it }, "Marking", KeyboardType.Number)
                    StatField(standingTackle, { standingTackle = it }, "Standing Tackle", KeyboardType.Number)
                    StatField(slidingTackle, { slidingTackle = it }, "Sliding Tackle", KeyboardType.Number)

                    // Section title for goalkeeper stats
                    Text(
                        text = "Goalkeeper Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    StatField(gkDiving, { gkDiving = it }, "GK Diving", KeyboardType.Number)
                    StatField(gkHandling, { gkHandling = it }, "GK Handling", KeyboardType.Number)
                    StatField(gkKicking, { gkKicking = it }, "GK Kicking", KeyboardType.Number)
                    StatField(gkPositioning, { gkPositioning = it }, "GK Positioning", KeyboardType.Number)

                    // Last one with Done
                    ReusableOutlinedTextField(
                        value = gkReflexes,
                        onValueChange = { gkReflexes = it },
                        label = "GK Reflexes",
                        leadingIconId = android.R.drawable.ic_menu_edit,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )

                    Spacer(Modifier.height(24.dp))

                    // 6️⃣ Save button
                    Button(
                        onClick = {
                            scope.launch {
                                val player = Player(
                                    name = playerName,
                                    position = playerPosition,
                                    age = playerAge.toIntOrNull() ?: 0,
                                    height = height.toFloatOrNull() ?: 0f,
                                    weight = weight.toFloatOrNull() ?: 0f,
                                    bestPosition = bestPosition,
                                    weakFoot = weakFoot,
                                    skillMoves = skillMoves.toIntOrNull() ?: 0,
                                    attackingWorkRate = attackingWorkRate,
                                    defensiveWorkRate = defensiveWorkRate,
                                    crossing = crossing.toIntOrNull() ?: 0,
                                    finishing = finishing.toIntOrNull() ?: 0,
                                    headingAccuracy = headingAccuracy.toIntOrNull() ?: 0,
                                    shortPassing = shortPassing.toIntOrNull() ?: 0,
                                    volleys = volleys.toIntOrNull() ?: 0,
                                    dribbling = dribbling.toIntOrNull() ?: 0,
                                    curve = curve.toIntOrNull() ?: 0,
                                    fkAccuracy = fkAccuracy.toIntOrNull() ?: 0,
                                    longPassing = longPassing.toIntOrNull() ?: 0,
                                    ballControl = ballControl.toIntOrNull() ?: 0,
                                    acceleration = acceleration.toIntOrNull() ?: 0,
                                    sprintSpeed = sprintSpeed.toIntOrNull() ?: 0,
                                    agility = agility.toIntOrNull() ?: 0,
                                    reactions = reactions.toIntOrNull() ?: 0,
                                    balance = balance.toIntOrNull() ?: 0,
                                    shotPower = shotPower.toIntOrNull() ?: 0,
                                    jumping = jumping.toIntOrNull() ?: 0,
                                    stamina = stamina.toIntOrNull() ?: 0,
                                    strength = strength.toIntOrNull() ?: 0,
                                    longShots = longShots.toIntOrNull() ?: 0,
                                    aggression = aggression.toIntOrNull() ?: 0,
                                    interceptions = interceptions.toIntOrNull() ?: 0,
                                    positioning = positioning.toIntOrNull() ?: 0,
                                    vision = vision.toIntOrNull() ?: 0,
                                    penalties = penalties.toIntOrNull() ?: 0,
                                    composure = composure.toIntOrNull() ?: 0,
                                    marking = marking.toIntOrNull() ?: 0,
                                    standingTackle = standingTackle.toIntOrNull() ?: 0,
                                    slidingTackle = slidingTackle.toIntOrNull() ?: 0,
                                    gkDiving = gkDiving.toIntOrNull() ?: 0,
                                    gkHandling = gkHandling.toIntOrNull() ?: 0,
                                    gkKicking = gkKicking.toIntOrNull() ?: 0,
                                    gkPositioning = gkPositioning.toIntOrNull() ?: 0,
                                    gkReflexes = gkReflexes.toIntOrNull() ?: 0
                                )
                                db.playerDao.upsertPlayer(player)

                                // Show success message
                                snackbarMessage = "Player saved successfully"
                                showSnackbar = true

                                // Optional: Navigate back after saving
                                // navController.navigateUp()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save Player", fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPlayerScreenPreview() {
    PMAAppTheme(darkTheme = true) {
        AddPlayerScreen(navController = rememberNavController())
    }
}