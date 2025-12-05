package com.crypto.cryptoview.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.crypto.cryptoview.presentation.main.MainViewModel

@Composable
fun HoldingsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117)),
        contentAlignment = Alignment.Center
    ) {
        Text("Holdings Screen", color = Color.White)
    }
}