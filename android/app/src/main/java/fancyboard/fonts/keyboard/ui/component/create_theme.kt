package fancyboard.fonts.keyboard.ui.component

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fancyboard.fonts.keyboard.keyboard.KeyboardTheme
import fancyboard.fonts.keyboard.keyboard.deleteBackgroundImage
import fancyboard.fonts.keyboard.keyboard.deleteTheme
import fancyboard.fonts.keyboard.keyboard.getUserThemes
import fancyboard.fonts.keyboard.keyboard.lightKeyboardTheme
import fancyboard.fonts.keyboard.keyboard.saveTheme
import fancyboard.fonts.keyboard.resizeAndSaveImage
import java.io.File
import java.util.UUID

private fun getThemeById(context: Context, id: String?): KeyboardTheme {
    if (id == null) return lightKeyboardTheme.copy(id = UUID.randomUUID().toString())
    val userThemes = getUserThemes(context)
    val theme = userThemes.find { it.id == id }
    if (theme == null) return lightKeyboardTheme.copy(id = UUID.randomUUID().toString())
    return theme
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTheme(
    navHostController: NavHostController,
    themeId: String? = null
) {
    val context = LocalContext.current
    var warnDelete by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(getThemeById(context, themeId)) }
    val original = remember { settings.copy() }

    var uri by remember {
        mutableStateOf<Uri?>(
            if (settings.backgroundImage != null)
                Uri.fromFile(File(context.filesDir, settings.backgroundImage!!))
            else
                null
        )
    }

    fun handleAddTheme() {
        var saveSettings = settings.copy()

        // Delete the previous image if it was changed or removed
        if (original.backgroundImage != null && saveSettings.backgroundImage != original.backgroundImage) {
            deleteBackgroundImage(context, original)
        }

        if (uri != null) {
            // We would like to save the image to context
            val savedImage = resizeAndSaveImage(context, uri!!, settings.id!!)
            if (savedImage == null) {
                Toast.makeText(context, "Couldn't save the image", Toast.LENGTH_LONG).show()
                return
            }

            saveSettings =
                saveSettings.copy(backgroundImage = savedImage.relativeTo(context.filesDir).path)
        }

        saveTheme(context, saveSettings)
        navHostController.popBackStack()
    }

    fun handleDelete() {
        deleteTheme(context, settings)
        navHostController.popBackStack()
    }

    if (warnDelete)
        AlertDialog(
            { warnDelete = false },
            confirmButton = { OutlinedButton({ handleDelete() }) { Text("Okay") } },
            dismissButton = { TextButton({ warnDelete = false }) { Text("Cancel") } },
            title = { Text("Delete Theme") },
            text = { Text("Do you want to delete this theme?") }
        )

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Create Theme") },
            actions = {
                if (themeId != null) {
                    TextButton({ warnDelete = true }) {
                        Text("Delete")
                    }
                }

                TextButton({ navHostController.popBackStack() }) {
                    Text("Discard")
                }

                TextButton({ handleAddTheme() }) {
                    Text("Save")
                }
            },
        )
    }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Box(Modifier.fillMaxWidth()) {
                KeyboardViewPreview(settings, Modifier.align(Alignment.Center))
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    settings.name,
                    label = { Text("Theme Name") },
                    onValueChange = { settings = settings.copy(name = it) },
                )

                Text(
                    "Background Style",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ButtonColorPicker(
                        title = "Background Color",
                        value = settings.backgroundColor
                    ) {
                        settings = settings.copy(backgroundColor = it)
                    }

                    ImagePicker(title = "Background Image", uri = uri) {
                        settings = settings.copy(backgroundImage = it?.toString())
                        uri = it
                    }
                }

                Text(
                    "Key Style",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ButtonColorPicker(
                        title = "Key Color",
                        value = settings.keyColor,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        settings = settings.copy(keyColor = it)
                    }

                    ButtonColorPicker(
                        title = "Key Pressed Color",
                        value = settings.keyPressedColor,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        settings = settings.copy(keyPressedColor = it)
                    }
                }


                Text(
                    "Text Style",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ButtonColorPicker(
                        title = "Text Color",
                        value = settings.textColor,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        settings = settings.copy(textColor = it)
                    }

                    ButtonColorPicker(
                        title = "Suggestion Text Color",
                        value = settings.suggestionTextColor ?: Color.BLACK,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        settings = settings.copy(suggestionTextColor = it)
                    }
                }
            }
        }
    }
}