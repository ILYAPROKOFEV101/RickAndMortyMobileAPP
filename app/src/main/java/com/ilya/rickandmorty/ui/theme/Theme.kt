import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ilya.rickandmorty.ui.theme.YourColors
import com.ilya.rickandmorty.ui.theme.darkPalette
import com.ilya.rickandmorty.ui.theme.lightPalette


// 3. Настраиваем CompositionLocal
val LocalColors = staticCompositionLocalOf<YourColors> {
    error("Colors not provided")
}

// 4. Модифицируем AppTheme для поддержки тем
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkPalette else lightPalette

    CompositionLocalProvider(
        LocalColors provides colors,
        content = content
    )
}

// 5. Объект для доступа к цветам
object Theme {
    val colors: YourColors
        @Composable
        get() = LocalColors.current
}