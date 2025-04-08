package com.example.pmaapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pmaapp.R
import com.example.pmaapp.components.CategoryCard
import com.example.pmaapp.database.AppDatabase
import com.example.pmaapp.database.Player
import com.example.pmaapp.navigation.AppRoutes
import com.example.pmaapp.ui.theme.PMAAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersListScreen(navController: NavController) {
    // Room and coroutine setup
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val scope = rememberCoroutineScope()

    // State to hold the list of players
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }

    // Collect the Flow of players when the screen is first composed
    LaunchedEffect(key1 = true) {
        db.playerDao.getAllPlayers().collect { playersList ->
            players = playersList
        }
    }

    // Define card background colors based on player positions
    val positionColors = mapOf(
        "GK" to Color(0xFF1E88E5),       // Blue for goalkeepers
        "DEF" to Color(0xFF43A047),      // Green for defenders
        "MID" to Color(0xFFE53935),      // Red for midfielders
        "FWD" to Color(0xFFFFB300)       // Gold/Yellow for forwards
    )

    // Get appropriate color based on player position
    fun getColorForPosition(position: String): Color {
        val key = when {
            position.contains("GK", ignoreCase = true) -> "GK"
            position.contains("CB", ignoreCase = true) ||
                    position.contains("LB", ignoreCase = true) ||
                    position.contains("RB", ignoreCase = true) -> "DEF"

            position.contains("CM", ignoreCase = true) ||
                    position.contains("CDM", ignoreCase = true) ||
                    position.contains("CAM", ignoreCase = true) -> "MID"

            position.contains("ST", ignoreCase = true) ||
                    position.contains("LW", ignoreCase = true) ||
                    position.contains("RW", ignoreCase = true) -> "FWD"

            else -> "MID" // Default to midfield if unknown
        }
        return positionColors[key] ?: Color(0xFF6D4C41) // Brown as fallback
    }

    PMAAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Player Squad",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
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
                    )
                )
            }
        ) { paddingValues ->
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    if (players.isEmpty()) {
                        // Show message when no players exist
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No players added yet",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add players from the Add Player screen",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Display grid of player cards with proper spacing
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${players.size} players in squad",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 160.dp),
                                contentPadding = PaddingValues(
                                    top = 8.dp,
                                    bottom = 24.dp,
                                    start = 4.dp,
                                    end = 4.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(players) { player ->
                                    // Player card
                                    CategoryCard(
                                        backgroundColor = getColorForPosition(player.position),
                                        // Use a default image or position-based image resource
                                        teamLogoRes = when {
                                            player.position.contains(
                                                "GK",
                                                ignoreCase = true
                                            ) -> R.drawable.goalkeeper

                                            player.position.contains(
                                                "CB",
                                                ignoreCase = true
                                            ) -> R.drawable.def

                                            player.position.contains(
                                                "MID",
                                                ignoreCase = true
                                            ) -> R.drawable.mid

                                            player.position.contains(
                                                "FWD",
                                                ignoreCase = true
                                            ) -> R.drawable.stricker

                                            else -> R.drawable.baseline_supervised_user_circle_24
                                        },
                                        teamName = player.name,
                                        descriptionName = "${player.position} • ${player.age} years",
                                        onClick = {
                                            // Navigate to player detail screen with player ID
                                            navController.navigate(
                                                "${
                                                    AppRoutes.PLAYER_DETAIL_ROUTE.split(
                                                        "/"
                                                    )[0]
                                                }/${player.id}"
                                            )
                                        }
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