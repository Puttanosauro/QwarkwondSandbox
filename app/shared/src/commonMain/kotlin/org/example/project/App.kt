package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. Our dummy parser (we will replace this with the real one later!)

// 1. The AST Definition
sealed interface QdBlock {
    data class Paragraph(val text: String) : QdBlock
    data class Heading(val level: Int, val text: String) : QdBlock
    data class MathBlock(val formula: String) : QdBlock
    //raw src to the image I guess
    data class  ImageRef(val ref: String) : QdBlock
}

//sealed interface InlineToken {
//    data class NormalText(val text: String) : InlineToken
//    data class Bold(val text: String) : InlineToken
//    data class Subscript(val text: String) : InlineToken
//}
//
//data class Paragraph(val children: List<InlineToken>) : QdBlock



class QwarkdownParser {
    fun parse(input: String): List<QdBlock> {
        val blocks = mutableListOf<QdBlock>()

        // Split the document by double line breaks (paragraphs)
        val rawBlocks = input.split(Regex("\\n\\s*\\n"))

        for (raw in rawBlocks) {
            val text = raw.trim()
            if (text.isEmpty()) continue

            when {
                // If it starts with $$ and ends with $$, it's a Math Block
                text.startsWith("$$") && text.endsWith("$$") -> {
                    val formula = text.removeSurrounding("$$").trim()
                    blocks.add(QdBlock.MathBlock(formula))
                }
                // If it starts with #, it's a Heading
                text.startsWith("# ") -> {
                    blocks.add(QdBlock.Heading(1, text.removePrefix("# ").trim()))
                }
                text.startsWith("## ") -> {
                    blocks.add(QdBlock.Heading(2, text.removePrefix("## ").trim()))
                }

                //![Icon](assets/icon.svg)
                text.startsWith("![Icon](") && text.endsWith(")") -> {
                    val image = text.removePrefix("![Icon](").removeSuffix(")").trim()
                    blocks.add(QdBlock.ImageRef(image) )
                }
                // Otherwise, it's just a normal paragraph
                else -> {
                    blocks.add(QdBlock.Paragraph(text))
                }
            }
        }
        return blocks
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
            // RIGHT SIDE: The Live Render Preview
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 16.dp)
            ) {
                Text(
                    text = "Parsed Output:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 1. Run the parser
                val parsedBlocks = parser.parse(rawText)

                // 2. Loop through the AST and draw the correct Compose UI for each block
                for (block in parsedBlocks) {
                    when (block) {
                        is QdBlock.Heading -> {
                            val style = if (block.level == 1)
                                MaterialTheme.typography.headlineLarge
                            else
                                MaterialTheme.typography.headlineMedium

                            Text(
                                text = block.text,
                                style = style,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        is QdBlock.Paragraph -> {
                            Text(
                                text = block.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        is QdBlock.MathBlock -> {
                            // For now, we will just draw math blocks as gray code boxes.
                            // Later, we will inject KaTeX here!
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                // Note: In Compose Web, we usually handle colors via standard UI tools,
                                // but for this quick test we'll just use a basic outline.
                            ) {
                                Text(
                                    text = "[ MATH RENDERER PLACEHOLDER ]\n${block.formula}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        is QdBlock.ImageRef -> {
                            // ill just print the standard HTML syntax for images: <img src="img.png>"
                            Text(
                                text = "<img src=\" ${block.ref} \"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.surfaceTint
                            )


                        }
                    }
                }
            }
        }
    }
}