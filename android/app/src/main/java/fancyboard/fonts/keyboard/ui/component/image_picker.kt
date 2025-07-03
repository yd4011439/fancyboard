package fancyboard.fonts.keyboard.ui.component

import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    title: String,
    uri: Uri?,
    onSelected: (uri: Uri?) -> Unit,
) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            if (it != null)
                onSelected(it)
        }

    Row {
        Row(
            modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AndroidView(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(32.dp)),
                factory = {
                    ImageView(it).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                    }
                },
                update = {
                    Glide.with(it).load(uri).into(it)
                }
            )

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (uri != null) {
            IconButton({}) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Icon",
                    Modifier.size(24.dp)
                )
            }
        }
    }
}