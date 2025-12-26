package com.gadgeski.abbozzo.ui.component

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern

/**
 * Abbozzo Design Language: Secure Link Component.
 * Automatically detects http/https URLs and opens them in Chrome Custom Tabs.
 *
 * @param text The content text containing potential URLs.
 * @param modifier Modifier for layout.
 * @param baseColor Color for non-link text.
 * @param linkColor Color for link text (Neon Cyan).
 * @param fontSize Font size for the text.
 * @param onNonLinkClick Optional callback for when non-link text is clicked.
 */
@Composable
fun AbbozzoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFEEEEEE), // High Contrast White
    linkColor: Color = Color(0xFF00F0FF), // Neon Cyan
    fontSize: TextUnit = 14.sp,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onNonLinkClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // SECURITY: Strict Regex to match only http and https.
    // Excludes potentially dangerous schemes like file://, content://, javascript:
    val urlPattern = remember { Pattern.compile("(https?://\\S+)") }

    val annotatedString = remember(text) {
        buildAnnotatedString {
            val matcher = urlPattern.matcher(text)
            
            // Base Text Style - Append all text initially if no links, 
            // but since we need to interleave links, we might need a different approach 
            // OR use addStyle/addLink on the full text.
            // Using addLink is cleaner with regex.
            
            append(text)
            
            // Apply base style to everything
            addStyle(
                style = SpanStyle(
                    color = baseColor,
                    fontSize = fontSize,
                    fontFamily = FontFamily.Monospace // Industrial look
                ),
                start = 0,
                end = text.length
            )

            // Link Detection & Styling
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val url = matcher.group()

                // Double Check: Ensure strictly http/s
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    val linkStyles = TextLinkStyles(
                        style = SpanStyle(
                            color = linkColor,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )

                    val linkAnnotation = LinkAnnotation.Url(
                        url = url,
                        styles = linkStyles,
                        linkInteractionListener = { annotation ->
                            launchSecureBrowser(context, (annotation as LinkAnnotation.Url).url)
                        }
                    )
                    
                    addLink(linkAnnotation, start, end)
                }
            }
        }
    }

    androidx.compose.material3.Text(
        text = annotatedString,
        modifier = modifier,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize
        ),
        softWrap = true,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * Launches the URL in a secure Chrome Custom Tab.
 */
private fun launchSecureBrowser(context: Context, url: String) {
    try {
        val builder = CustomTabsIntent.Builder()
        
        // Abbozzo Theme: Dark Background for Browser Toolbar
        val params = androidx.browser.customtabs.CustomTabColorSchemeParams.Builder()
            .setToolbarColor(android.graphics.Color.BLACK) 
            .build()
            
        builder.setDefaultColorSchemeParams(params)
        builder.setShowTitle(true)
        
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (_: Exception) {
        // Fallback: Use standard intent if Custom Tabs fails
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            // Log error silently
        }
    }
}
