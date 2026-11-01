package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.EqBand
import com.example.ui.theme.DuskHeartPink
import com.example.ui.theme.DuskMagenta
import com.example.ui.theme.DuskNightDeep
import com.example.ui.theme.DuskPurpleDark
import com.example.ui.theme.DuskPurpleMid
import com.example.ui.theme.DuskSunsetOrange
import com.example.ui.theme.DuskSunsetRose
import com.example.ui.theme.TextLavenderMuted
import com.example.ui.theme.TextPureWhite
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MusicPlayerViewModel

@Composable
fun EqualizerScreen(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val isEqEnabled by viewModel.isEqEnabled.collectAsState()
    val bands by viewModel.eqBands.collectAsState()
    val activePresetName by viewModel.activePresetName.collectAsState()
    val allPresets by viewModel.allPresets.collectAsState()
    val bassBoost by viewModel.bassBoostLevel.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DuskPurpleDark,
                        DuskNightDeep
                    )
                )
            )
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header Row with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.PLAYER) },
                    modifier = Modifier.testTag("eq_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Player",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "AUDIO ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuskSunsetOrange,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Equalizer & FX",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite
                    )
                }
            }

            // Master EQ ON/OFF Power Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Switch(
                    checked = isEqEnabled,
                    onCheckedChange = { viewModel.setEqEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DuskSunsetOrange,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("eq_power_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Presets Header & Save Custom Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOUND PRESETS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DuskSunsetOrange,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .testTag("save_preset_button")
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(8.dp))
                    .clickable { showSaveDialog = true }
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = "Save Custom Preset",
                    tint = DuskSunsetOrange,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SAVE PRESET",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DuskSunsetOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Presets Selector Carousel
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allPresets) { preset ->
                val isSelected = activePresetName == preset.name
                Box(
                    modifier = Modifier
                        .testTag("preset_${preset.name}")
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(
                                listOf(DuskSunsetOrange, DuskSunsetRose)
                            ) else Brush.horizontalGradient(
                                listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.05f))
                            )
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.applyPreset(preset) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset.name,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextLavenderMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 5-Band Equalizer Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(1.2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FREQUENCY BANDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuskSunsetOrange
                    )
                    Text(
                        text = "±15 dB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLavenderMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bands.forEach { band ->
                        SingleEqBandFader(
                            band = band,
                            isEnabled = isEqEnabled,
                            onLevelChange = { newLevel ->
                                viewModel.setBandLevel(band.index, newLevel)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bass Boost Rotary Slider Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.2.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BASS BOOST RESONANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DuskSunsetOrange
                        )
                        Text(
                            text = "Dynamic Sub-Bass Enhancer",
                            fontSize = 11.sp,
                            color = TextLavenderMuted
                        )
                    }

                    Text(
                        text = "${(bassBoost / 10)}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DuskSunsetOrange
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = bassBoost.toFloat(),
                    onValueChange = { viewModel.setBassBoost(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = DuskSunsetOrange,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("bass_boost_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset to Flat Reference button
        Button(
            onClick = {
                val flat = allPresets.firstOrNull { it.name == "Audiophile Flat" }
                if (flat != null) viewModel.applyPreset(flat)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.12f),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_flat_button")
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "Reset",
                tint = DuskSunsetOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RESET TO FLAT (0 dB)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Save Custom Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "Save Custom Preset",
                    fontWeight = FontWeight.Bold,
                    color = TextPureWhite
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a name for your custom EQ preset:",
                        fontSize = 12.sp,
                        color = TextLavenderMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        placeholder = { Text("e.g. My Sunset Sound") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DuskSunsetOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = TextPureWhite,
                            unfocusedTextColor = TextPureWhite
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_preset_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank()) {
                            viewModel.saveCustomPreset(newPresetName.trim())
                            showSaveDialog = false
                            newPresetName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DuskSunsetOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_save_preset_button")
                ) {
                    Text("SAVE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("CANCEL", color = TextLavenderMuted)
                }
            },
            containerColor = DuskPurpleDark
        )
    }
}

@Composable
fun SingleEqBandFader(
    band: EqBand,
    isEnabled: Boolean,
    onLevelChange: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val db = band.currentLevelDb
        val dbText = if (db > 0) "+%.1f".format(db) else "%.1f".format(db)
        Text(
            text = dbText,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            color = if (isEnabled) DuskSunsetOrange else Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = band.currentLevelMb.toFloat(),
            onValueChange = { onLevelChange(it.toInt().toShort()) },
            valueRange = band.minLevelMb.toFloat()..band.maxLevelMb.toFloat(),
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = DuskSunsetOrange,
                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("band_${band.centerFreqHz}_slider")
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = band.formattedFreq,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = TextLavenderMuted,
            textAlign = TextAlign.Center
        )
    }
}
