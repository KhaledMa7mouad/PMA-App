package com.example.pmaapp.Screens // Changed from Screens to screens to match the package structure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pmaapp.R
import com.example.pmaapp.database.AppDatabase
import com.example.pmaapp.database.Player
import com.example.pmaapp.ui.theme.PMAAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(navController: NavController, playerId: Int) {
    // Room and coroutine setup
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    // State to hold player info
    var player by remember { mutableStateOf<Player?>(null) }

    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State for snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Load player data when screen is first composed
    LaunchedEffect(key1 = playerId) {
        scope.launch {
            try {
                val fetchedPlayer = db.playerDao.getPlayerById(playerId)
                player = fetchedPlayer
                if (fetchedPlayer == null) {
                    snackbarHostState.showSnackbar("Player not found")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error loading player: ${e.localizedMessage}")
            }
        }
    }

    // Function to handle player deletion
    fun deletePlayer() {
        player?.let { playerData ->
            scope.launch {
                try {
                    db.playerDao.deletePlayer(playerData)
                    snackbarHostState.showSnackbar("Player deleted successfully")
                    // Navigate back to players list screen
                    navController.navigateUp()
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error deleting player: ${e.localizedMessage}")
                }
            }
        }
    }

    PMAAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = player?.name ?: "Player Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            player?.let { playerData ->
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Player header info
                    PlayerHeaderSection(playerData)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Player stats sections
                    Text(
                        text = "Player Attributes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Pace attributes
                    AttributeCategory(
                        title = "Pace",
                        backgroundColor = Color(0xFFFBC02D),
                        attributes = listOf(
                            "Acceleration" to playerData.acceleration,
                            "Sprint Speed" to playerData.sprintSpeed
                        )
                    )

                    // Shooting attributes
                    AttributeCategory(
                        title = "Shooting",
                        backgroundColor = Color(0xFFF57C00),
                        attributes = listOf(
                            "Positioning" to playerData.positioning,
                            "Finishing" to playerData.finishing,
                            "Shot Power" to playerData.shotPower,
                            "Long Shots" to playerData.longShots,
                            "Volleys" to playerData.volleys,
                            "Penalties" to playerData.penalties
                        )
                    )

                    // Passing attributes
                    AttributeCategory(
                        title = "Passing",
                        backgroundColor = Color(0xFF7CB342),
                        attributes = listOf(
                            "Vision" to playerData.vision,
                            "Crossing" to playerData.crossing,
                            "FK Accuracy" to playerData.fkAccuracy,
                            "Short Passing" to playerData.shortPassing,
                            "Long Passing" to playerData.longPassing,
                            "Curve" to playerData.curve
                        )
                    )

                    // Dribbling attributes
                    AttributeCategory(
                        title = "Dribbling",
                        backgroundColor = Color(0xFF00ACC1),
                        attributes = listOf(
                            "Agility" to playerData.agility,
                            "Balance" to playerData.balance,
                            "Reactions" to playerData.reactions,
                            "Ball Control" to playerData.ballControl,
                            "Dribbling" to playerData.dribbling,
                            "Composure" to playerData.composure
                        )
                    )

                    // Defending attributes
                    AttributeCategory(
                        title = "Defending",
                        backgroundColor = Color(0xFF5E35B1),
                        attributes = listOf(
                            "Interceptions" to playerData.interceptions,
                            "Heading Accuracy" to playerData.headingAccuracy,
                            "Marking" to playerData.marking,
                            "Standing Tackle" to playerData.standingTackle,
                            "Sliding Tackle" to playerData.slidingTackle
                        )
                    )

                    // Physical attributes
                    AttributeCategory(
                        title = "Physical",
                        backgroundColor = Color(0xFF8D6E63),
                        attributes = listOf(
                            "Jumping" to playerData.jumping,
                            "Stamina" to playerData.stamina,
                            "Strength" to playerData.strength,
                            "Aggression" to playerData.aggression
                        )
                    )

                    // Goalkeeper attributes (show only if player is a goalkeeper)
                    if (playerData.position.contains("GK", ignoreCase = true)) {
                        AttributeCategory(
                            title = "Goalkeeping",
                            backgroundColor = Color(0xFF1E88E5),
                            attributes = listOf(
                                "GK Diving" to playerData.gkDiving,
                                "GK Handling" to playerData.gkHandling,
                                "GK Kicking" to playerData.gkKicking,
                                "GK Positioning" to playerData.gkPositioning,
                                "GK Reflexes" to playerData.gkReflexes
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Player details section
                    Text(
                        text = "Player Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Player details grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Height", "${playerData.height} cm", Modifier.weight(1f))
                        DetailItem("Weight", "${playerData.weight} kg", Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Weak Foot", playerData.weakFoot, Modifier.weight(1f))
                        DetailItem("Skill Moves", playerData.skillMoves.toString(), Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Att. Work Rate", playerData.attackingWorkRate, Modifier.weight(1f))
                        DetailItem("Def. Work Rate", playerData.defensiveWorkRate, Modifier.weight(1f))
                    }

                    DetailItem("Best Position", playerData.bestPosition, Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add delete button at the bottom
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Delete Player",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Add some space at the bottom for better UI
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                // Loading or error state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Player") },
            text = { Text("Are you sure you want to delete ${player?.name}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        deletePlayer()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlayerHeaderSection(player: Player) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player icon/avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    when {
                        player.position.contains("GK", ignoreCase = true) -> Color(0xFF1E88E5)
                        player.position.contains("DEF", ignoreCase = true) -> Color(0xFF43A047)
                        player.position.contains("MID", ignoreCase = true) -> Color(0xFFE53935)
                        player.position.contains("FWD", ignoreCase = true) -> Color(0xFFFFB300)
                        else -> Color(0xFF6D4C41)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = when {
                        player.position.contains("GK", ignoreCase = true) -> R.drawable.baseline_password_24
                        player.position.contains("DEF", ignoreCase = true) -> R.drawable.baseline_password_24
                        player.position.contains("MID", ignoreCase = true) -> R.drawable.baseline_password_24
                        player.position.contains("FWD", ignoreCase = true) -> R.drawable.baseline_password_24
                        else -> R.drawable.baseline_password_24
                    }
                ),
                contentDescription = "Player Position",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Player basic info
        Column {
            Text(
                text = player.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = player.position,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Text(
                text = "${player.age} years",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AttributeCategory(
    title: String,
    backgroundColor: Color,
    attributes: List<Pair<String, Int>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Category header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Attributes
            attributes.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { (name, value) ->
                        AttributeItem(
                            name = name,
                            value = value,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // If odd number of attributes, add empty space
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AttributeItem(name: String, value: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Attribute name
        Text(
            text = name,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        // Attribute value with rating color
        val ratingColor = when {
            value >= 90 -> Color(0xFF00C853) // Excellent (Green)
            value >= 80 -> Color(0xFF64DD17) // Very Good (Light Green)
            value >= 70 -> Color(0xFFFFEB3B) // Good (Yellow)
            value >= 60 -> Color(0xFFFF9800) // Average (Orange)
            else -> Color(0xFFFF5252)        // Poor (Red)
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ratingColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}