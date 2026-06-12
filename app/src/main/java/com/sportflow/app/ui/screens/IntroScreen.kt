package com.sportflow.app.ui.screens

import com.sportflow.app.ui.localization.localizedText

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.sportflow.app.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportflow.app.R
import com.sportflow.app.ui.theme.SportFlowGreen
import androidx.compose.ui.platform.LocalContext
import android.content.Context

@Composable
fun IntroScreen(onNavigate: (String) -> Unit) {
    // Progress animation from 0.0f to 1.0f
    val progress = remember { Animatable(0f) }
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("sportflow_prefs", Context.MODE_PRIVATE) }

    LaunchedEffect(Unit) {
        // Animate the progress loading bar over 2.5 seconds
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2500)
        )
        // Automatic navigation callback when load finishes
        val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
        val targetRoute = if (isLoggedIn) com.sportflow.app.navigation.NavRoutes.HOME else com.sportflow.app.navigation.NavRoutes.LANDING
        onNavigate(targetRoute)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep Slate Dark Blue (Slate 900)
                        Color(0xFF020617)  // Near Black Blue (Slate 950)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.weight(1.2f))

            // Glowing logo aura section
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Soft radial green glow
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SportFlowGreen.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // High fidelity rounded logo square container matching figma screenshot
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(
                            1.5.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    SportFlowGreen.copy(alpha = 0.3f)
                                )
                            ),
                            RoundedCornerShape(36.dp)
                        )
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sportflow_logo),
                        contentDescription = localizedText("SportFlow Logo"),
                        modifier = Modifier.size(75.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title
            Text(
                text = "SportFlow",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Brand Tagline in spaced green caps
            Text(
                text = "PERFORMANCE EM TEMPO REAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportFlowGreen,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.weight(1.0f))

            // Progress Indicators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Custom Rounded progress loader
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF334155)) // Slate 700 background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.value)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SportFlowGreen)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Loading Text
                Text(
                    text = "A CARREGAR ECOSSISTEMA...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Powered by Signature at bottom
            Text(
                text = "POWERED BY KINETIC ARCHITECT",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.weight(1.2f))

            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SportFlowGreen.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(
                            1.5.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    SportFlowGreen.copy(alpha = 0.3f)
                                )
                            ),
                            RoundedCornerShape(36.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sportflow_logo),
                        contentDescription = localizedText("SportFlow Logo"),
                        modifier = Modifier.size(75.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SportFlow",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "PERFORMANCE EM TEMPO REAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportFlowGreen,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.weight(1.0f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF334155))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SportFlowGreen)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "A CARREGAR ECOSSISTEMA...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            Text(
                text = "POWERED BY KINETIC ARCHITECT",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}