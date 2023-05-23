package com.example.textedd.shared.contracts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public interface MainContract {
    interface View {
        void showText(String fileName, String content);

        String getEditTextAsString();

        void createTagRV();

        void updateTagRV();

        void createLinksRV();

        @SuppressLint("NotifyDataSetChanged")
        void updateLinksRV();

        void openByLink(String fileName);

        void openTag(String fileName);
        //void showText();
    }

    interface Presenter {
        void presentNoteContent(Context context, String filename);
        void saveContent(Context context, String filename);

        List<String> suggestLinks(Context context);

        String removeFileExtensions(String fileName);

        String addFileExtensions(String fileName);

        void deleteNote(Context context, String filename);

        void deleteTag(Context context, String tagName);

        void removeTagfromNote(Context context, String tagName, String fileName);

        void createNote(Context context, String filename);
        List<String> getAllTagsOfNote(Context context, String fileName);
        List<String> addNewTagToANote(Context context, String tagName, String fileName);

        List<String> addNewLinkToANote(Context context, String newLink, String fileName);

        List<String> getBackLinksOfNote(Context context, String fileName);

        List<String> getAllLinksOfNote(Context context, String fileName);

        void removeLinkFromNote(Context context, String linkName, String fileName);

        void addSpan(TextView textView, Object... spans);

        List<String> getNotesTaged(Context context, String tagFileName);

        void findNoteOrTag(Context context, String tagFileName);
        //void onButtonWasClicked();
        //void onDestroy();
    }

    interface Repository {
        void deleteTag(Context context, String tagName);

        void detachTagFromNote(Context context, String tagName, String fileName);

        void saveFile(Context context, String content, String fileName);

        boolean login(Context context, String username, String password);

        boolean register(Context context, String username, String password);

        File getUserDirectory(Context context, String username);

        void deleteFile(Context context, String fileName);
        String openFile(Context context, String fileName);
        void createNoteFile(Context context, String fileName);
        //String loadMessage();
        File[] getAllNotesInDir(Context context);
        List<String> getAllTagsOfNote(Context context, String fileName);
        void createTagFile(Context context, String tagFileName);
        void newTagToANote(Context context, String tagName, String fileName);

        List<String> getAllLinksOfNote(Context context, String fileName);

        List<String> getAllNoteNames(Context context);

        List<String> getBackLinksOfNote(Context context, String fileName);

        void addNewLinkToANote(Context context, String newLink, String fileName);

        void detachLinkFromNote(Context context, String linkName, String fileName);

        List<String> getNotesTaged(Context context, String tagFileName);

        Integer findNoteOrTag(Context context, String tagFileName);
    }
}
