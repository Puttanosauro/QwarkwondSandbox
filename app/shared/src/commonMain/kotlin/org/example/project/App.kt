package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. Our dummy parser (we will replace this with the real one later!)
class QwarkdownParser {
    fun parseBasic(input: String): String {
        var parsed = input
        // Replace **bold** with HTML <b> tag for now
        parsed = parsed.replace(Regex("\\*\\*(.*?)\\*\\*"), "<b>$1</b>")
        // Replace *italic* with HTML <i> tag
        parsed = parsed.replace(Regex("\\*(.*?)\\*"), "<i>$1</i>")
        return parsed
    }
}

@Composable
fun App() {
    MaterialTheme {
        // 2. This is the "State". When this changes, the UI automatically redraws!
        var rawText by remember { mutableStateOf("Type **bold** and *italic* here...") }
        val parser = remember { QwarkdownParser() }

        // 3. A Row to put our editor and preview side-by-side
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // LEFT SIDE: The Raw Editor
            OutlinedTextField(
                value = rawText,
                onValueChange = { newText ->
                    rawText = newText // Updates the state on every keystroke
                },
                label = { Text("Raw .qd Input") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            // RIGHT SIDE: The Live Render Preview
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Text(
                    text = "Parsed Output:",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // For now, we are just printing the raw parsed string.
                // Later, we will inject this into a browser HTML view!
                Text(
                    text = parser.parseBasic(rawText)
                )
            }
        }
    }
}