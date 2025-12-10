package com.crypto.cryptoview.presentation.component.assetsOverview.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(200.dp)) {
        // Canvas 그리기 로직
        val total = data.sumOf { it.value }
        var startAngle = -90f

        data.forEach { item ->
            val sweepAngle = (item.value / total * 360f).toFloat()
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 40f)
            )
            startAngle += sweepAngle
        }
    }
}

