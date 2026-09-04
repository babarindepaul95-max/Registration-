package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [User::class, ChurchEvent::class, Registration::class],
    version = 1,
    exportSchema = false
)
abstract class ChurchDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun churchEventDao(): ChurchEventDao
    abstract fun registrationDao(): RegistrationDao

    companion object {
        @Volatile
        private var INSTANCE: ChurchDatabase? = null

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): ChurchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChurchDatabase::class.java,
                    "church_registration_database.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: ChurchDatabase) {
                val userDao = database.userDao()
                val eventDao = database.churchEventDao()
                val regDao = database.registrationDao()

                // Seed Default Admin
                val adminId = userDao.insertUser(
                    User(
                        fullName = "Pastor David Adeleke",
                        phoneNumber = "+1 555-019-2831",
                        email = "admin@church.org",
                        passwordHash = hashPassword("admin123"),
                        role = "ADMIN"
                    )
                )

                // Seed Default Member
                val memberId = userDao.insertUser(
                    User(
                        fullName = "Johnathan Smith",
                        phoneNumber = "+1 555-012-3456",
                        email = "john.smith@example.com",
                        passwordHash = hashPassword("password123"),
                        role = "USER"
                    )
                )

                // Seed Required 4 Event Categories
                val event1Id = eventDao.insertEvent(
                    ChurchEvent(
                        eventName = "Children's Day Party",
                        category = "CHILDREN",
                        description = "A vibrant celebration for children, teenagers, and families featuring fun games, puppet shows, biblical storytelling, prizes, bouncy castles, and delightful refreshments.",
                        eventDate = "Saturday, May 23, 2026 • 10:00 AM - 2:00 PM",
                        venue = "Church Youth Ground & Fellowship Garden",
                        status = "OPEN",
                        capacity = 300
                    )
                )

                val event2Id = eventDao.insertEvent(
                    ChurchEvent(
                        eventName = "Adults' Day Party",
                        category = "ADULT",
                        description = "An inspiring fellowship gala with keynote reflections, live sacred acoustic orchestra, gourmet buffet dinner, and high-impact networking for church adults and invited guests.",
                        eventDate = "Friday, July 17, 2026 • 6:30 PM - 9:30 PM",
                        venue = "Grand Jubilee Fellowship Hall",
                        status = "OPEN",
                        capacity = 250
                    )
                )

                val event3Id = eventDao.insertEvent(
                    ChurchEvent(
                        eventName = "Women's Day Party",
                        category = "WOMEN",
                        description = "A special high tea banquet dedicated to empowering women in faith, leadership, and community service. Includes worship symposium, mentorship circles, and guest speaker reflections.",
                        eventDate = "Saturday, August 15, 2026 • 11:00 AM - 3:00 PM",
                        venue = "Grace Banquet Terrace & Main Auditorium",
                        status = "OPEN",
                        capacity = 200
                    )
                )

                val event4Id = eventDao.insertEvent(
                    ChurchEvent(
                        eventName = "General Harvest Party",
                        category = "HARVEST",
                        description = "The main annual church harvest and thanksgiving festival celebrating God's bountiful blessings with praise celebrations, choir performances, harvest offering presentations, and a community feast.",
                        eventDate = "Sunday, November 8, 2026 • 9:00 AM - 3:00 PM",
                        venue = "Main Sanctuary & Celebration Pavilions",
                        status = "OPEN",
                        capacity = 600
                    )
                )

                // Seed sample confirmed registration for member
                regDao.insertRegistration(
                    Registration(
                        referenceNumber = "CHR-2026-8492",
                        userId = memberId,
                        eventId = event4Id,
                        attendeesCount = 3,
                        registrationDate = System.currentTimeMillis() - 86400000L * 2,
                        comments = "Family attending with 2 kids. Special seating near front requested.",
                        status = "CONFIRMED"
                    )
                )
            }
        }
    }
}
