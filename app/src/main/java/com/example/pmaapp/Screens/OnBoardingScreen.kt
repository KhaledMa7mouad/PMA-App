package com.example.pmaapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pmaapp.R
import com.example.pmaapp.navigation.AppRoutes
import com.example.pmaapp.ui.theme.FotGreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// Data class for onboarding pages
data class OnboardingPage(
    val image: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pages = listOf(
        OnboardingPage(
            image = R.drawable.r9_png,
            title = "Track Your Player Performance",
            description = "Monitor real-time stats and progress of your team members"
        ),
        OnboardingPage(
            image = R.drawable.garengha_png,
            title = "Track Your Player Health And Movement",
            description = "Analyze sensor-driven insights—heart rate, recovery, and movement"
        ),
        OnboardingPage(
            image = R.drawable.kaka_png,
            title = "AI-Powered Game IQ: Uncover Hidden Edges",
            description = "Decode your game DNA: master positioning, tactics, and plug performance leaks"
        )
    )

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Set the whole background to dark black
            .background(Color.Black)
    ) {
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageView(page = pages[page], pagerState = pagerState, pageIndex = page)
        }

        BottomSection(
            pagerState = pagerState,
            onGetStarted = {
                navController.navigate(AppRoutes.SIGNUP_ROUTE) {
                    popUpTo(AppRoutes.ONBOARDING_ROUTE) { inclusive = true }
                }
            },
            onSkip = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pages.lastIndex) {
                        pagerState.animateScrollToPage(pages.lastIndex)
                    }
                }
            },
            onNext = {
                coroutineScope.launch {
                    val nextPage = pagerState.currentPage + 1
                    if (nextPage <= pages.lastIndex) {
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingPageView(page: OnboardingPage, pagerState: PagerState, pageIndex: Int) {
    // Calculate the offset for this page relative to the current position
    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffset

    // Use the offset to animate scale and alpha.
    // Ensure the alpha remains in the 0..1 range.
    val imageScale = 1 - (pageOffset.absoluteValue * 0.2f)
    val textAlpha = (1 - pageOffset.absoluteValue).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        // Animated Image
        Image(
            painter = painterResource(id = page.image),
            contentDescription = page.title,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = imageScale
                    scaleY = imageScale
                    alpha = textAlpha
                }
                .size(500.dp)
                .shadow(16.dp, shape = CircleShape)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Title with fade animation
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { alpha = textAlpha }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description with fade animation
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.graphicsLayer { alpha = textAlpha }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomSection(
    pagerState: PagerState,
    onGetStarted: () -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        // Dots Indicator
        DotsIndicator(
            totalDots = 3,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Navigation Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            if (pagerState.currentPage != 2) {
                IconButton(onClick = onSkip) {
                    Text(
                        text = "Skip",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            val buttonText = if (pagerState.currentPage == 2) "Get Started" else "Next"
            Button(
                onClick = {
                    if (pagerState.currentPage == 2) {
                        onGetStarted()
                    } else {
                        onNext()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FotGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(text = buttonText, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            val color = if (index == selectedIndex) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            }

            val size = if (index == selectedIndex) 12.dp else 8.dp
            val elevation = if (index == selectedIndex) 4.dp else 0.dp

            Box(
                modifier = Modifier
                    .size(size)
                    .shadow(elevation, shape = CircleShape)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}