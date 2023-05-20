package com.example.textedd.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Dao
public
interface NotesDAO {
    @Query("SELECT fileName FROM notes ORDER BY fileName")
    List<String> selectNamesABC();

    @Query("SELECT fileName FROM notes ORDER BY creationTime")
    List<String> selectNamesNEW();

    @Query("SELECT * FROM notes WHERE fileName=:fileName")
    NoteEntity findNoteById(String fileName);

    @Query("SELECT * FROM tags WHERE fileName=:tagFileName")
    TagEntity findTagById(String tagFileName);

    @Insert
    void insertNote(NoteEntity... notes);

    @Insert
    void insertTagE(TagEntity... tags);

    @Delete
    void deleteNoteE(NoteEntity... notes);

    @Delete
    void deleteTagE(TagEntity... tags);

    @Update
    void updateNoteE(NoteEntity... notes);

    @Update
    void updateTagE(TagEntity... tags);

    @Query("UPDATE notes SET tags=:tags WHERE fileName=:fileName")
    void updateNoteTags(String fileName, String tags);

    @Query("SELECT tags FROM notes WHERE fileName=:fileName ORDER BY creationTime")
    String selectTagsOfNote(String fileName); //Все тегги в записи

    default List<String> selectTagsOfNote(String fileName, List<String> tag_list) {
        String value = selectTagsOfNote(fileName);
        if (value != null) tag_list.addAll(Arrays.asList(value.split(" ,")));
        else tag_list = new ArrayList();
        return tag_list;
    } //Все тегги в записи

    @Query("SELECT links FROM notes WHERE fileName=:fileName ORDER BY creationTime")
    String selectLinksOfNote(String fileName); //Все ссылки в записи

    default List<String> selectLinksOfNote(String fileName, List<String> Link_list) {
        String value = selectLinksOfNote(fileName);
        if (value != null) Link_list.addAll(Arrays.asList(value.split(" ,")));
        else Link_list = new ArrayList();
        return Link_list;
    }

    @Query("SELECT links FROM tags WHERE fileName=:tagFileName ORDER BY creationTime")
    String selectLinksOfTag(String tagFileName);

    default List<String> selectLinksOfTag(String tagFileName, List<String> notes_taged){
        String value = selectLinksOfTag(tagFileName);
        if (value != null) notes_taged.addAll(Arrays.asList(value.split(" ,")));
        else notes_taged = new ArrayList();
        return notes_taged;
    }

    @Query("UPDATE tags SET links=:links WHERE fileName=:tagFileName")
    void updateTagedNotes(String tagFileName, String links);
}
