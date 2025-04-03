import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.NavHost

import com.example.pmaapp.components.ReusableOutlinedTextField

@Composable
fun AddPlayerScreen(navController: NavController) {
    var playerName by remember { mutableStateOf("") }
    var playerPosition by remember { mutableStateOf("") }
    var playerClub by remember { mutableStateOf("") }
    var playerAge by remember { mutableStateOf("") }
    var playerRate by remember { mutableStateOf("") }
    var playerNationality by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enter the Player Info",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 12.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ReusableOutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = "Player Name",
                leadingIconId = android.R.drawable.ic_menu_edit,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReusableOutlinedTextField(
                value = playerPosition,
                onValueChange = { playerPosition = it },
                label = "Position",
                leadingIconId = android.R.drawable.ic_menu_info_details,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReusableOutlinedTextField(
                value = playerClub,
                onValueChange = { playerClub = it },
                label = "Club",
                leadingIconId = android.R.drawable.ic_menu_compass,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(12.dp))

            ReusableOutlinedTextField(
                value = playerAge,
                onValueChange = { playerAge = it },
                label = "Age",
                leadingIconId = android.R.drawable.ic_menu_my_calendar,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(12.dp))

            ReusableOutlinedTextField(
                value = playerRate,
                onValueChange = { playerRate = it },
                label = "Rate",
                leadingIconId = android.R.drawable.star_on,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(12.dp))

            ReusableOutlinedTextField(
                value = playerNationality,
                onValueChange = { playerNationality = it },
                label = "Nationality",
                leadingIconId = android.R.drawable.ic_search_category_default,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

        }
    }
}

@Preview
@Composable
private fun AddPlayerScreenPreview() {
    AddPlayerScreen(navController = NavController(context = LocalContext.current))
}
