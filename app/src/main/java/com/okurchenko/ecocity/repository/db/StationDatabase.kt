package com.okurchenko.ecocity.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationItem

@Database(entities = [StationItem::class, StationDetails::class], version = 4)
abstract class StationDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
    abstract fun stationDataDao(): StationDetailsDao
}
object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE stationdataitem_new (id TEXT NOT NULL, name TEXT NOT NULL, unit TEXT, cr TEXT, value TEXT, localName TEXT NOT NULL, localUnit TEXT, time TEXT NOT NULL, `index` INTEGER NOT NULL, PRIMARY KEY(id))"
            )
            database.execSQL(
                "INSERT INTO stationdataitem_new (id, name, unit, cr, value, localName, localUnit, time, `index`) SELECT id, name, unit, cr, value, localName, localUnit, time, `index` FROM stationdataitem"
            )
            database.execSQL("DROP TABLE stationdataitem")
            database.execSQL("ALTER TABLE stationdataitem_new RENAME TO stationdataitem")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE stationdataitem_new (id TEXT NOT NULL, name TEXT NOT NULL, unit TEXT, cr TEXT, value TEXT, localName TEXT NOT NULL, localUnit TEXT, time TEXT, share TEXT NOT NULL DEFAULT '', levels TEXT, `offset` TEXT NOT NULL DEFAULT '', level INTEGER, `index` INTEGER, PRIMARY KEY(id))"
            )
            database.execSQL(
                "INSERT INTO stationdataitem_new (id, name, unit, cr, value, localName, localUnit, time, `index`) SELECT id, name, unit, cr, value, localName, localUnit, time, `index` FROM stationdataitem"
            )
            database.execSQL("DROP TABLE stationdataitem")
            database.execSQL("ALTER TABLE stationdataitem_new RENAME TO stationdataitem")
        }
    }

    val MIGRATION_3_4 = object : Migration(3,4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE stationdataitem_new (itemId INTEGER NOT NULL, id TEXT , name TEXT, unit TEXT, cr TEXT, value TEXT, localName TEXT, localUnit TEXT, time TEXT NOT NULL, share TEXT, levels TEXT, `offset` TEXT, level INTEGER, `index` INTEGER, PRIMARY KEY(itemId))"
            )
            database.execSQL(
                "INSERT INTO stationdataitem_new (id, name, unit, cr , value, localName, localUnit, time, share, levels , `offset`, level, `index`) SELECT id, name, unit, cr , value, localName, localUnit, time, share, levels , `offset`, level, `index` FROM stationdataitem"
            )
            database.execSQL("DROP TABLE stationdataitem")
            database.execSQL("ALTER TABLE stationdataitem_new RENAME TO stationdataitem")
        }
    }
}