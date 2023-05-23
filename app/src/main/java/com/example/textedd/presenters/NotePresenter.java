package com.example.textedd.presenters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;

import com.example.textedd.shared.contracts.MainContract;
import com.example.textedd.data.Repository;

import java.util.ArrayList;
import java.util.List;

public class NotePresenter implements MainContract.Presenter {
    private static final String TAG = "MainContract.Presenter";
    private MainContract.View mView;
    private MainContract.Repository mRepository;

    public NotePresenter(MainContract.View mView) {
        this.mView = mView;
        this.mRepository = new Repository();
    }
    @Override
    public void presentNoteContent(Context context, String fileName){
        String content = mRepository.openFile(context, fileName);
        mView.showText(fileName, content);
        mView.createTagRV();
        mView.createLinksRV();
        Log.d(TAG, "File" + fileName + " content was presented");
    }
    @Override
    public void saveContent(Context context, String fileName){
        String content = mView.getEditTextAsString();
        mRepository.saveFile(context, content, fileName);
    }


    @Override
    public List<String> suggestLinks(Context context){
        String content = mView.getEditTextAsString();
        List<String> suggests_list = new ArrayList();
        List<String> file_names_list = new ArrayList(mRepository.getAllNoteNames(context));
        for (String file_name : file_names_list){
            String fn = file_name.replace(".md", "");
            if (content.contains(fn) && !fn.isEmpty()){
                suggests_list.add(file_name);
            }
        }
        return suggests_list;
    }

    @Override
    public  String removeFileExtensions(String fileName){
        return fileName.replace(".md", "");
    }

    @Override
    public  String addFileExtensions(String fileName){
        return (fileName + ".md");
    }


    @Override
    public void deleteNote(Context context, String fileName){
        mRepository.deleteFile(context, fileName);
    }
    @Override
    public void deleteTag(Context context, String tagName){
        mRepository.deleteTag(context, tagName);
    }
    @Override
    public void removeTagfromNote(Context context, String tagName, String fileName){
        mRepository.detachTagFromNote(context, tagName, fileName);
    }
    @Override
    public void createNote(Context context, String fileName){
        mRepository.createNoteFile(context, fileName);
    }
    @Override
    public List<String> getAllTagsOfNote(Context context, String fileName){
        return mRepository.getAllTagsOfNote(context, fileName);
    }

    @Override
    public List<String> addNewTagToANote(Context context, String tagName, String fileName){
        if (!tagName.isEmpty()) {
            mRepository.newTagToANote(context, tagName, fileName);
        }
        return getAllTagsOfNote(context, fileName);
    }
    @Override
    public List<String> addNewLinkToANote(Context context, String newLink, String fileName){
        if (!newLink.isEmpty()) {
            mRepository.addNewLinkToANote(context, newLink, fileName);
            mRepository.createNoteFile(context, newLink);
        }
        return getAllLinksOfNote(context, fileName);
    }
    @Override
    public List<String> getBackLinksOfNote(Context context, String fileName){
        return mRepository.getBackLinksOfNote(context, fileName);
    }
    @Override
    public List<String> getAllLinksOfNote(Context context, String fileName){
        return mRepository.getAllLinksOfNote(context, fileName);
    }
    @Override
    public void removeLinkFromNote(Context context, String linkName, String fileName){
        mRepository.detachLinkFromNote(context, linkName, fileName);
    }
    @Override
    public void addSpan(TextView textView, Object... spans) {
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        int end = builder.length();
        //int var8 = spans.length;
        for (Object span : spans) {
            builder.setSpan(span, 0, end, 33);
        }
        textView.setText((CharSequence)builder);
    }
    @Override
    public List<String> getNotesTaged(Context context, String tagFileName){
        return mRepository.getNotesTaged(context, tagFileName);
    }

    @Override
    public void findNoteOrTag(Context context, String tagFileName){
        int i = mRepository.findNoteOrTag(context, tagFileName);
        switch (i){
            case 0: {
                createNote(context, tagFileName);
                mView.openByLink(tagFileName);
                break;
            }
            case 1:{
                mView.openTag(tagFileName);
                break;
            }
            case 2:{
                mView.openByLink(tagFileName);
                break;
            }

        }
    }
}
