package org.smartregister.uniceftunisia.reporting.indicatorposition

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.sqlite.db.transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.application.UnicefTunisiaApplication
import org.smartregister.util.Utils
import net.sqlcipher.database.SQLiteDatabase as SQLiteCipherDatabase

/**
 * This the repository class that extends [BaseRepository] creates a table that will be used for
 * holding indicator positions used
 */
class IndicatorPositionRepository private constructor() : BaseRepository() {

    private val application = UnicefTunisiaApplication.getInstance()

    private val sharePreference = Utils.getAllSharedPreferences()

    private object Constants {
        const val TABLE_NAME = "indicator_position"
        const val INDICATOR_POSITION_PREF = "indicator_positions_pref"
    }

    private object ColumnNames {
        const val INDICATOR = "indicator"
        const val POSITION = "position"
    }

    private object TableQueries {
        const val CREATE_TABLE_SQL = """
        CREATE TABLE indicator_position
        (
            _id             INTEGER
                constraint indicator_position_pk
                    primary key autoincrement,
            indicator       TEXT NOT NULL,
            position REAL NOT NULL
        );
        """
    }


    fun createTable(database: SQLiteCipherDatabase) = database.execSQL(TableQueries.CREATE_TABLE_SQL)

    fun populateIndicatorPosition(database: SQLiteCipherDatabase, oldVersion: Int, newVersion: Int) {
        val indicatorPositionPref = sharePreference.getPreference(Constants.INDICATOR_POSITION_PREF)
        if (!indicatorPositionPref.isNullOrEmpty() && newVersion > oldVersion
                && newVersion > indicatorPositionPref.toInt()) {
            database.run {
                execSQL("DROP TABLE IF EXISTS indicator_position")
                createTable(this)
                delete("sqlite_sequence", "name = ?", arrayOf(Constants.TABLE_NAME))
            }
        }
        val indicatorIndicesJson = Utils.readAssetContents(application.applicationContext,
                "configs/reporting/indicator-positions.json")
        val indicatorIndices = Gson().fromJson<List<IndicatorPosition>>(indicatorIndicesJson,
                object : TypeToken<List<IndicatorPosition>>() {}.type)
        CoroutineScope(Dispatchers.IO).launch {
            if (saveIndicatorPositions(database, indicatorIndices))
                sharePreference.savePreference(Constants.INDICATOR_POSITION_PREF, newVersion.toString())
        }
    }

    private suspend fun saveIndicatorPositions(database: SQLiteCipherDatabase, indicatorPositions: List<IndicatorPosition>): Boolean {
        return withContext(Dispatchers.IO) {
            if (!indicatorPositions.isNullOrEmpty()) {
                database.transaction(exclusive = true) {
                    indicatorPositions.forEach {
                        val contentValues = contentValuesOf(
                                Pair(ColumnNames.INDICATOR, it.indicator),
                                Pair(ColumnNames.POSITION, it.position),
                        )
                        database.insertWithOnConflict(Constants.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
                    }
                    true
                }

            }
            false
        }
    }

    companion object {
        @Volatile
        private var instance: IndicatorPositionRepository? = null

        @JvmStatic
        fun getInstance(): IndicatorPositionRepository = instance ?: synchronized(this) {
            IndicatorPositionRepository().also { instance = it }
        }
    }
}