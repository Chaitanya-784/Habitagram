package com.example.habittrackerapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                HabitEntity.class,
                UserEntity.class,
                HabitCompletionEntity.class,
                FriendEntity.class,
                SentImageEntity.class
        },
        version = 8
)
public abstract class HabitDatabase extends RoomDatabase {

    public abstract HabitDao habitDao();
    public abstract UserDao userDao();
    public abstract HabitCompletionDao habitCompletionDao();
    public abstract FriendDao friendDao();

    public abstract SentImageDao sentImageDao();

    private static volatile HabitDatabase INSTANCE;

    public static HabitDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HabitDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HabitDatabase.class, "habit_database")
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3,
                                    MIGRATION_3_4,
                                    MIGRATION_4_5,
                                    MIGRATION_5_6,
                                    MIGRATION_6_7,
                                    MIGRATION_7_8

                            )
                            .build();
                }

            }
        }
        INSTANCE.getOpenHelper().getWritableDatabase();
        return INSTANCE;
    }

    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE habits_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, time TEXT, xp INTEGER NOT NULL, " +
                    "isCompleted INTEGER NOT NULL DEFAULT 0, " +
                    "username TEXT)");
            db.execSQL("INSERT INTO habits_new (id, name, time, xp, isCompleted, username) " +
                    "SELECT id, name, time, xp, completed, 'User' FROM habits");
            db.execSQL("DROP TABLE habits");
            db.execSQL("ALTER TABLE habits_new RENAME TO habits");

            db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "username TEXT, password TEXT, xp INTEGER NOT NULL)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // No schema changes
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS habit_completions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "habitId INTEGER NOT NULL, " +
                    "date TEXT, " +
                    "username TEXT)");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE habits ADD COLUMN date TEXT");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS friends (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "myUsername TEXT, " +
                    "friendUsername TEXT, " +
                    "UNIQUE(myUsername, friendUsername))");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE friends ADD COLUMN status TEXT DEFAULT 'pending'");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS sent_images (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "sender TEXT, " +
                    "receiver TEXT, " +
                    "imageUrl TEXT)");
        }
    };

}
