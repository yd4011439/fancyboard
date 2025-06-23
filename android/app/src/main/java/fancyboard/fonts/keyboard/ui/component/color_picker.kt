package fancyboard.fonts.keyboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

fun intToHexARGB(color: Int): String {
    return String.format("#%08X", color)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonColorPicker(
    title: String,
    value: Int,
    modifier: Modifier = Modifier,
    onChange: (color: Int) -> Unit
) {
    val controller = rememberColorPickerController()
    var open by remember { mutableStateOf(false) }

    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { open = true }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(24.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(Color(value)),
        )

        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (open) {
        ModalBottomSheet({ open = false }, sheetState = rememberModalBottomSheetState()) {
            Column(
                Modifier
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row {
                    HsvColorPicker(
                        initialColor = Color(value),
                        onColorChanged = { onChange(it.color.toArgb()) },
                        controller = controller,
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        AlphaSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp),
                            controller = controller,
                        )

                        BrightnessSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(35.dp),
                            controller = controller,
                        )

                        Button({ open = false }) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }
}