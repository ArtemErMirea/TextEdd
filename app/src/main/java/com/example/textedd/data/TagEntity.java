package com.example.textedd.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "Tags")
public class TagEntity {
    @PrimaryKey
    @NonNull
    String fileName;
    File directory;
    long creationTime;
    List<String> links;
    List<String> tags;

    public TagEntity(@NonNull String fileName, File directory,
                      List<String> links, List<String> tags){
        this.fileName = fileName;
        this.directory = directory;
        this.creationTime = new Date().getTime();
        this.links = links;
        this.tags = tags;
    }

    public String toString() { return String.format(Locale.getDefault(), "%s: %s (%d)", fileName, directory , creationTime); }
}
