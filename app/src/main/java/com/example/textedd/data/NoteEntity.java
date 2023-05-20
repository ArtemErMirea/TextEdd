package com.example.textedd.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.util.Date;
import java.util.List;

@Entity(tableName = "Notes")
public class NoteEntity {
    @PrimaryKey
    @NonNull
    String fileName;
    File directory;
    long creationTime;
    List<String> links;
    List<String> tags;



    public NoteEntity(@NonNull String fileName, File directory,
                      List<String> links, List<String> tags){
        this.fileName = fileName;
        this.directory = directory;
        this.creationTime = new Date().getTime();
        this.links = links;
        this.tags = tags;
    }
    /*
    public NoteEntity(int _id, String fileName, File directory, int[] links, int[] tags){
        this._id = _id;
        this.directory = directory;
        this.fileName = fileName;
        //BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        //FileTime fileTime = attr.creationTime();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
        }

        this.creationTime = getCreationTime(fileName, directory);
        this.lastAccessTime = lastAccessTime;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                this.lastModifiedTime = Files.getLastModifiedTime(Paths.get(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.links = links;
        this.tags = tags;
    }
    public static BasicFileAttributes readNoteAttribute(String fileName, File directory){
        File file = new File(directory, fileName);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Path filePath = file.toPath();
            try {
                return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                //attr.creationTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static FileTime getCreationTime(String fileName, File directory){
        BasicFileAttributes attr = readNoteAttribute(fileName, directory);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert attr != null;
            return attr.creationTime();
        }
        return null;
    }*/
}
