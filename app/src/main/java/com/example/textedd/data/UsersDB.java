package com.example.textedd.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={UserEntity.class}, version=1)
public abstract class UsersDB extends RoomDatabase {
    public abstract UsersDAO usersDAO();

    private static final String DB_NAME="users.db";
    private static volatile UsersDB INSTANCE=null;

    synchronized static UsersDB get(Context context) {
        if (INSTANCE==null) {
            INSTANCE=create(context, false);
        }
        return(INSTANCE);
    }
    public static UsersDB create(Context context, boolean memoryOnly) {
        RoomDatabase.Builder<UsersDB> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    UsersDB.class);
        }
        else {
            b=Room.databaseBuilder(context.getApplicationContext(), UsersDB.class,
                    DB_NAME);
        }
        return(b.build());

    }
}
