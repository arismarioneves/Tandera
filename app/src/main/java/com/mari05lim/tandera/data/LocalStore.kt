package com.mari05lim.tandera.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Títulos marcados como assistidos, do mais recente para o mais antigo. */
class WatchedStore(private val dataStore: DataStore<Preferences>, private val json: Json) {

    val items: Flow<List<TitleSummary>> = dataStore.data.map { decode(it[KEY]) }.distinctUntilChanged()

    fun isWatched(type: MediaType, id: Int): Flow<Boolean> =
        items.map { list -> list.any { it.matches(type, id) } }.distinctUntilChanged()

    suspend fun toggle(item: TitleSummary) = update { list ->
        if (list.any { it.matches(item.type, item.id) }) list.filterNot { it.matches(item.type, item.id) }
        else listOf(item) + list
    }

    /** Acrescenta ao fim os itens ainda ausentes (usado na importação da versão antiga). */
    suspend fun addAll(newItems: List<TitleSummary>) = update { list ->
        list + newItems.filter { new -> list.none { it.matches(new.type, new.id) } }
    }

    private suspend fun update(transform: (List<TitleSummary>) -> List<TitleSummary>) {
        dataStore.edit { it[KEY] = json.encodeToString(serializer, transform(decode(it[KEY]))) }
    }

    private fun decode(raw: String?): List<TitleSummary> =
        raw?.let { runCatching { json.decodeFromString(serializer, it) }.getOrNull() } ?: emptyList()

    private fun TitleSummary.matches(type: MediaType, id: Int) = this.type == type && this.id == id

    private companion object {
        val KEY = stringPreferencesKey("watched")
        val serializer = ListSerializer(TitleSummary.serializer())
    }
}

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Cores de destaque (mesma paleta do Bibliateca). RED é a cor da marca; DYNAMIC usa o papel de parede (Android 12+). */
enum class Accent(val argb: Long) {
    RED(0xFFE53935),
    DYNAMIC(0xFFE53935),
    TEAL(0xFF00897B),
    GREEN(0xFF43A047),
    YELLOW(0xFFF9A825),
    ORANGE(0xFFEF6C00),
    BLUE(0xFF1E88E5),
    DARK_BLUE(0xFF283593),
    DARK(0xFF455A64),
}

data class AppPrefs(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accent: Accent = Accent.RED,
    val region: Region = Region.Brazil,
)

/** Preferências do usuário: aparência e país usado nas listas e na disponibilidade dos streamings. */
class SettingsStore(private val dataStore: DataStore<Preferences>) {

    val prefs: Flow<AppPrefs> = dataStore.data
        .map { p ->
            AppPrefs(
                themeMode = enumOrDefault(p[THEME], ThemeMode.SYSTEM),
                accent = enumOrDefault(p[ACCENT], Accent.RED),
                region = Region.fromCode(p[REGION]),
            )
        }
        .distinctUntilChanged()

    val region: Flow<Region> = prefs.map { it.region }.distinctUntilChanged()

    suspend fun setRegion(region: Region) {
        dataStore.edit { it[REGION] = region.code }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME] = mode.name }
    }

    suspend fun setAccent(accent: Accent) {
        dataStore.edit { it[ACCENT] = accent.name }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: default

    private companion object {
        val REGION = stringPreferencesKey("region")
        val THEME = stringPreferencesKey("theme_mode")
        val ACCENT = stringPreferencesKey("accent")
    }
}

/** [ofName]: forma com preposição para frases ("streamings do Brasil"). */
enum class Region(val code: String, val displayName: String, val ofName: String) {
    Brazil("BR", "Brasil", "do Brasil"),
    Portugal("PT", "Portugal", "de Portugal"),
    UnitedStates("US", "Estados Unidos", "dos Estados Unidos"),
    Argentina("AR", "Argentina", "da Argentina"),
    Mexico("MX", "México", "do México"),
    ;

    companion object {
        fun fromCode(code: String?): Region = entries.firstOrNull { it.code == code } ?: Brazil
    }
}
