package com.example.textedd.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.List;

@Database(entities={NoteEntity.class, TagEntity.class}, version=1)
@TypeConverters({Converters.class})
public abstract class NotesDB extends RoomDatabase {
    public abstract NotesDAO notesDAO();

    private static final String DB_NAME="notes.db";
    private static volatile NotesDB INSTANCE=null;

    synchronized static NotesDB get(Context context) {

        if (INSTANCE==null) {
            INSTANCE=create(context, false);
        }
        return(INSTANCE);
    }
    public static NotesDB create(Context context, boolean memoryOnly) {
        RoomDatabase.Builder<NotesDB> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    NotesDB.class);
        }
        else {
            b=Room.databaseBuilder(context.getApplicationContext(), NotesDB.class,
                    DB_NAME);
        }
        return(b.build());

    }

    void updateNoteTags(String fileName, List<String> tags){
        String joined = TextUtils.join(", ", tags);
        notesDAO().updateNoteTags(fileName, joined);
    }

    void updateTagedNotes(String tagFileName, List<String> links){
        String joined = TextUtils.join(", ", links);
        notesDAO().updateTagedNotes(tagFileName, joined);
    }
    /*
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes "
                    +"ADD COLUMN address TEXT");

        }
    };*/
}
