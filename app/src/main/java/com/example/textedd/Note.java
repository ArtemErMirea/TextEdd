package com.example.textedd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class Note extends File {
    public String fileName;
    public File dir;

    public Note(@NonNull String fileName) {
        super(fileName);
    }

    public Note(@Nullable File dir, @NonNull String fileName) {
        super(dir, fileName);
    }

    //LocalDate creationDate;

}
