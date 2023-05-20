package com.example.textedd.domain;

import android.content.Context;

import com.example.textedd.MainContract;
import com.example.textedd.data.Repository;
import com.example.textedd.shared.CatalogContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CatalogPresenter implements CatalogContract.Presenter {
    private static final String TAG = "CatalogContract.Presenter";
    private CatalogContract.View mView;
    private MainContract.Repository mRepository;

    public CatalogPresenter(CatalogContract.View mView) {
        this.mView = mView;
        this.mRepository = new Repository();
    }
    @Override
    public List<File> getAllFilesList(Context context, List<File> textFiles){
        textFiles = new ArrayList<>();
        File[] files = mRepository.getAllNotesInDir(context);
        assert files != null;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".md")) {
                textFiles.add(file); // add text file to list
            }
        }
        return textFiles;
    }
    @Override
    public void createNote(Context context, String fileName){
        mRepository.createNoteFile(context, fileName);
    }

    @Override
    public void createTag(Context context, String fileName){
        mRepository.createTagFile(context, fileName);
    }

    @Override
    public void findNoteOrTag(Context context, String tagFileName){
        int i = mRepository.findNoteOrTag(context, tagFileName);
        switch (i){
            case 0: {
                createNote(context, tagFileName);
                mView.openFileAsNote(tagFileName);
                break;
            }
            case 1:{
                mView.openFileAsTag(tagFileName);
                break;
            }
            case 2:{
                mView.openFileAsNote(tagFileName);
                break;
            }

        }
    }

}
