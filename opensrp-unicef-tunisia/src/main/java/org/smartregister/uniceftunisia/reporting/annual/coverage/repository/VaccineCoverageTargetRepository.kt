package org.smartregister.uniceftunisia.reporting.annual.coverage.repository

import androidx.core.content.contentValuesOf
import androidx.sqlite.db.transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import org.smartregister.repository.BaseRepository
import org.smartregister.uniceftunisia.reporting.ReportsDao
import org.smartregister.uniceftunisia.reporting.annual.coverage.domain.CoverageTarget
import android.database.sqlite.SQLiteDatabase as SqlCipherSQLite

class VaccineCoverageTargetRepository private constructor() : BaseRepository() {

    private object Constants {
        const val TABLE_NAME = "annual_coverage_target"
    }

    private object TableQueries {
        const val CREATE_TABLE_SQL = """
        CREATE TABLE annual_coverage_target
        (
            _id             INTEGER
                constraint annual_coverage_target_pk
                    primary key autoincrement,
            target_type       TEXT NOT NULL,
            year              INTEGER NOT NULL,
            target            INTEGER NOT NULL
        );
        """
        const val CREATE_TARGET_TYPE_UNIQUE_INDEX =
                "CREATE UNIQUE INDEX annual_coverage_target_target_type_index ON annual_coverage_target (target_type, year);"
    }

    object ColumnNames {
        const val TARGET_TYPE = "target_type"
        const val YEAR = "year"
        const val TARGET = "target"
    }

    fun createTable(database: SQLiteDatabase) = database.run {
        execSQL(TableQueries.CREATE_TABLE_SQL)
        execSQL(TableQueries.CREATE_TARGET_TYPE_UNIQUE_INDEX)
    }

    fun saveCoverageTarget(yearTarget: CoverageTarget) {
        val failedInsertionsList = arrayListOf<Long>()
        writableDatabase.transaction(exclusive = true) {
            val contentValues = contentValuesOf(
                    Pair(ColumnNames.TARGET_TYPE, yearTarget.targetType.name),
                    Pair(ColumnNames.YEAR, yearTarget.year),
                    Pair(ColumnNames.TARGET, yearTarget.target),
            )
            val id = writableDatabase.insertWithOnConflict(Constants.TABLE_NAME, null,
                    contentValues, SqlCipherSQLite.CONFLICT_REPLACE)
            if (id == (-1).toLong()) failedInsertionsList.add(id)
            failedInsertionsList.isEmpty()
        }
    }

    suspend fun getCoverageTarget(year: Int) = withContext(Dispatchers.IO) {
        ReportsDao.getCoverageTarget(year)
    }

    companion object {
        @Volatile
        private var instance: VaccineCoverageTargetRepository? = null

        @JvmStatic
        fun getInstance(): VaccineCoverageTargetRepository = instance ?: synchronized(this) {
            VaccineCoverageTargetRepository().also { instance = it }
        }
    }
}