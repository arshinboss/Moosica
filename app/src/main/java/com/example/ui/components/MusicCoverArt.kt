package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.model.Track
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskMagenta
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleGlow
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.DuskSunsetRose

/**
 * Modern album cover card matching the reference design.
 * Automatically displays the track's album art image,
 * or falls back to an elegant glowing music icon logo when no thumbnail exists.
 */
@Composable
fun MusicCoverArt(
    track: Track?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) DuskHeartPink else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(250),
        label = "heart_color"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .border(1.2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
            .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = DuskMagenta)
    ) {
        if (track?.coverDrawableRes != null) {
            Image(
                painter = painterResource(id = track.coverDrawableRes),
                contentDescription = "${track.title} Cover Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (track?.embeddedPictureBytes != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.embeddedPictureBytes)
                    .crossfade(true)
                    .build(),
                contentDescription = "${track.title} Cover Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = {
                    FallbackMusicIconLogo(track = track)
                }
            )
        } else if (track?.albumArtUri != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.albumArtUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "${track.title} Cover Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = {
                    FallbackMusicIconLogo(track = track)
                }
            )
        } else {
            // No thumbnail available -> show elegant music icon logo as requested
            FallbackMusicIconLogo(track = track)
        }

        // Floating Frosted Heart / Favorite Button in bottom right corner (from reference image)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                .clickable(onClick = onToggleFavorite)
                .testTag("favorite_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = heartColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Fallback Music Icon Logo displayed when a music file does NOT have a thumbnail.
 * Crafted with a sunset twilight gradient, concentric vinyl soundwave rings,
 * and a glowing centered music note logo.
 */
@Composable
fun FallbackMusicIconLogo(
    track: Track?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        DuskNightDeep,
                        DuskPurpleGlow,
                        DuskMagenta,
                        DuskSunsetRose,
                        DuskSunsetOrange
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 800f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative concentric soundwave rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f
            for (i in 1..4) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f + (i * 0.03f)),
                    radius = maxRadius * (i / 4.5f),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // Center frosted disc with glowing music icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .shadow(12.dp, CircleShape, spotColor = DuskSunsetRose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music Logo",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AUDIO TRACK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}
