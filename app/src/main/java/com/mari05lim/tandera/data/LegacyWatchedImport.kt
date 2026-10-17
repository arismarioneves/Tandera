package com.mari05lim.tandera.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import java.time.Instant
import java.time.ZoneOffset

/**
 * Importa uma única vez os "assistidos" da versão 2.x (SQLite "tandera.db", tabelas
 * Movie e TvShow) e apaga o banco antigo. Sem o banco, não faz nada.
 */
class LegacyWatchedImport(private val context: Context, private val watched: WatchedStore) {

    suspend fun run() {
        if (!context.getDatabasePath(DB_NAME).exists()) return

        val items = try {
            SQLiteDatabase.openDatabase(context.getDatabasePath(DB_NAME).path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
                read(db, "Movie", MediaType.Movie) + read(db, "TvShow", MediaType.Series)
            }
        } catch (e: SQLiteException) {
            Log.w(TAG, "Banco da versão antiga ilegível; descartado.", e)
            emptyList()
        }

        watched.addAll(items)
        context.deleteDatabase(DB_NAME)
    }

    private fun read(db: SQLiteDatabase, table: String, type: MediaType): List<TitleSummary> = try {
        db.query(table, arrayOf("id", "title", "poster", "release_time"), "library = 1", null, null, null, null).use { c ->
            buildList {
                while (c.moveToNext()) {
                    val id = c.getString(0)?.toIntOrNull() ?: continue
                    add(
                        TitleSummary(
                            id = id,
                            type = type,
                            title = c.getString(1).orEmpty(),
                            year = if (c.isNull(3)) null else yearOf(c.getLong(3)),
                            posterUrl = c.getString(2)?.takeIf { it.isNotBlank() }?.let { "$POSTER_BASE$it" },
                        ),
                    )
                }
            }
        }
    } catch (e: SQLiteException) {
        emptyList() // tabela ausente: a pessoa nunca marcou nada desse tipo
    }

    private fun yearOf(epochMillis: Long): Int? =
        if (epochMillis <= 0) null else Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).year

    private companion object {
        const val TAG = "LegacyWatchedImport"
        const val DB_NAME = "tandera.db"
        const val POSTER_BASE = "https://image.tmdb.org/t/p/w342"
    }
}
