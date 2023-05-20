package com.example.textedd.shared;

import android.content.Context;

import java.io.File;
import java.util.List;

public interface CatalogContract {
    interface View {

        void openFileAsNote(String fileName);

        void openFileAsTag(String fileName);
    }

    interface Presenter {
        List<File> getAllFilesList(Context context, List<File> textFiles);
        void createNote(Context context, String fileName);

        void createTag(Context context, String fileName);

        void findNoteOrTag(Context context, String tagFileName);
    }
}
