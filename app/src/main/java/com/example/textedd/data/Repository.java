package com.example.textedd.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.textedd.shared.contracts.MainContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Repository implements MainContract.Repository {
    private static final String TAG = "MainContract.Repository";

    public static File dir;

    @Override
    public boolean login(Context context, String username, String password) {
        class LoginThread extends Thread{
            String matchUser;
            @Override
            public void run() {
                UsersDB uDB;
                uDB = UsersDB.create(context, false);
                UsersDAO usersDAO = uDB.usersDAO();
                matchUser = usersDAO.findUserByInfo(username, password);
                // обращение к БД выносим в отдельный потк
            }
            public String getMatchUser(){
                return matchUser;
            }
            public boolean matchUserExist(){
                return (matchUser != null);
            }
        }
        LoginThread loginThread = new LoginThread();
        loginThread.start(); //запусккаем поток
        try {
            loginThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loginThread.matchUserExist();
    }
    @Override
    public boolean register(Context context, String username, String password) {
        class RegisterThread extends Thread{
            boolean registration;
            @Override
            public void run() {
                UsersDB uDB;
                uDB = UsersDB.create(context, false);
                UsersDAO usersDAO = uDB.usersDAO();
                if (usersDAO.findUserByName(username) == null){
                    UserEntity uE = new UserEntity(username, password);
                    usersDAO.insertUserEntity(uE);
                    registration = true;
                }
                else {
                    registration = false;
                }
                // обращение к БД выносим в отдельный потк
            }
            public boolean getRegistration(){
                return registration;
            }
        }
        RegisterThread registerThread = new RegisterThread();
        registerThread.start(); //запусккаем поток
        try {
            registerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return registerThread.getRegistration();
    }
    @Override
    public File getUserDirectory(Context context, String username){
        dir = context.getDir(username, Context.MODE_PRIVATE);
        Log.d(TAG, "User Directory" + dir.toString());
        return dir;
    }



    @Override
    public void deleteFile(Context context, String fileName){
        class DestructionThread extends Thread{
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                //File dir = context.getFilesDir();
                File note = new File(dir, fileName);
                NoteEntity ne = notesDAO.findNoteById(fileName);
                if (ne != null) notesDAO.deleteNoteE(ne);
                if(note.delete()){
                    //Удаляется файл и его метаданные, если они есть;
                    Log.d(TAG, "FileWas " + fileName + " Deleted");
                }
                else {
                    //Toast.makeText(context, fileName + " has NOT been deleted", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "FileWas" + fileName + " Not Deleted");
                }
                // обращение к БД выносим в отдельный потк
            }
        }
        DestructionThread destructionThread = new DestructionThread();
        destructionThread.start(); //запусккаем поток
        try {
            destructionThread.join();
            //объединяем поток  с главным потоком,
            // иначе созданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deleteTag(Context context, String tagName){
        class DestructionThread extends Thread{
            List<String> notes_taged = new ArrayList();
            List<String> tag_list = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                ///File dir = context.getFilesDir();
                File tagFile = new File(dir, tagName);
                TagEntity te = notesDAO.findTagById(tagName);
                notes_taged = notesDAO.selectLinksOfTag(tagName, notes_taged);
                for (String note_taged : notes_taged) {
                    detachTagFromNote(context, tagName, note_taged);
                    Log.d(TAG, tagName + " metadata in " + note_taged + " deleted");
                    //удаление упоминаний тега
                }
                if (te != null) {
                    notesDAO.deleteTagE(te);
                    Log.d(TAG, tagName + " metadata deleted");
                }
                if(tagFile.delete()){

                    //Удаляется файл и его метаданные, если они есть;
                    Log.d(TAG, "FileWas " + tagName + " Deleted");
                }
                else {
                    //Toast.makeText(context, fileName + " has NOT been deleted", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "FileWas" + tagName + " Not Deleted");
                }
                // обращение к БД выносим в отдельный потк
            }
        }
        DestructionThread destructionThread = new DestructionThread();
        destructionThread.start(); //запусккаем поток
        try {
            destructionThread.join();
            //объединяем поток  с главным потоком,
            // иначе созданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void detachTagFromNote(Context context, String tagName, String fileName){
        class DestructionThread extends Thread{
            List<String> notes_taged = new ArrayList();
            List<String> tag_list = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                TagEntity te = notesDAO.findTagById(tagName);
                notes_taged = notesDAO.selectLinksOfTag(tagName, notes_taged);
                notes_taged.remove(fileName);
                if (notes_taged == null) notes_taged = new ArrayList();
                if (!te.links.isEmpty()) te.links = notes_taged;
                notesDAO.updateTagE(te);
                NoteEntity ne = notesDAO.findNoteById(fileName);
                tag_list = notesDAO.selectTagsOfNote(fileName, tag_list);
                tag_list.remove(tagName);
                ne.tags = tag_list;
                notesDAO.updateNoteE(ne);
                // обращение к БД выносим в отдельный потк
            }
        }
        DestructionThread destructionThread = new DestructionThread();
        destructionThread.start(); //запусккаем поток
        try {
            destructionThread.join();
            //объединяем поток  с главным потоком,
            // иначе созданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void saveFile(Context context, String content, String fileName){
        try {
            OutputStream outputStream = context.openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(content);
            osw.close();
            Log.d(TAG, "File " + fileName + " Was Saved");
        } catch (Throwable t) {
            Toast.makeText(context.getApplicationContext(),
                    "Exception: " + t,
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public String openFile(Context context, String fileName) {
        try {
            //Context context = getApplication();
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                inputStream.close();
                Log.d(TAG, "FileWas " + fileName + " was opened");
                return builder.toString();
            }
        } catch (Throwable t) {
            Log.d(TAG, "File" + fileName + "not Found in" + dir);
        }
        return "";
    }
    @Override
    public void createNoteFile(Context context, String fileName){
        class CreationThread extends Thread{
            List<String> tag_list = new ArrayList();
            List<String> links = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                //File dir = context.getFilesDir();
                File note = new File(dir, fileName);
                try {
                    if(note.createNewFile()){
                        NoteEntity ne = new NoteEntity(fileName, dir,links, tag_list);
                        notesDAO.insertNote(ne);
                        Log.d(TAG, "File" + fileName + "Was Created");
                    } else Log.d(TAG, "File" + fileName + "Was Not created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // обращение к БД выносим в отдельный потк
            }
        }
        CreationThread creationThread = new CreationThread();
        creationThread.start(); //запусккаем поток
        try {
            creationThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public File[] getAllNotesInDir(Context context){
        assert context != null;
        //File directory = new File(String.valueOf(dir)); // directory path
        Log.d(TAG, "getAllNotesInDir" + dir.toString());
        File[] files = dir.listFiles(); // list all files in directory
        return files;
    }
    @Override
    public List<String> getAllTagsOfNote(Context context, String fileName){
        class TagsThread extends Thread{
            List<String> tag_list = new ArrayList();
            //List<String> tags_of_note = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                tag_list = notesDAO.selectTagsOfNote(fileName, tag_list);
                tag_list.removeAll(Arrays.asList("", " ", "  ", "   ", null));
                // обращение к БД выносим в отдельный потк
            }
            public List<String> getTag_list(){
                return tag_list;
            }
        }
        TagsThread tagsThread = new TagsThread();
        tagsThread.start(); //запусккаем поток
        try {
            tagsThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }Log.d(TAG, "List of Tags was selected");
        return tagsThread.getTag_list();
    }
    @Override
    public void createTagFile(Context context, String tagFileName){
        class CreationThread extends Thread{
            List<String> tag_list = new ArrayList();
            List<String> notes_taged = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                //File dir = context.getFilesDir();
                File tagFile = new File(dir, tagFileName);
                try {
                    if(tagFile.createNewFile()){
                        TagEntity te = new TagEntity(tagFileName, dir, notes_taged, tag_list);
                        notesDAO.insertTagE(te);
                        Log.d(TAG, "File" + tagFileName + "Was Created");
                    } else Log.d(TAG, "File" + tagFileName + "Was Not created");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // обращение к БД выносим в отдельный потк
            }
        }
        CreationThread creationThread = new CreationThread();
        creationThread.start(); //запусккаем поток
        try {
            creationThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void newTagToANote(Context context, String tagName, String fileName){
        class TagsThread extends Thread{
            List<String> tag_list = new ArrayList(); // Инициализируем спискм
            List<String> notes_taged = new ArrayList();
            //List<String> tags_of_note = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                tag_list = notesDAO.selectTagsOfNote(fileName, tag_list); // Получаем списог тегов текущей записи
                if (!tag_list.contains(tagName)) //Если в этом файле нет ещё этого тега
                {
                    tag_list.add(tagName);//Добавляем этот тег в списое тегов
                    tag_list.removeAll(Arrays.asList("", " ", "  ", "   ", null)); //Удаляем пустые значения
                    NoteEntity noteEntity = notesDAO.findNoteById(fileName); // Получаем сущность записи из БД по ID
                    noteEntity.tags = tag_list; // Задаём сущности новый список тегов
                    notesDAO.updateNoteE(noteEntity); // Обновляем сущность
                    if (notesDAO.findTagById(tagName) == null) // Если файла тега не существует
                        createTagFile(context, tagName); // Создаём его
                    notes_taged = notesDAO.selectLinksOfTag(tagName, notes_taged); // Получаем список помеченных тегом записей                      if (notes_of_tag != null) //
                    //notes_taged.addAll(notes_of_tag);
                    notes_taged.add(fileName); // Добавляем туда текущую запись
                    TagEntity tagEntity = notesDAO.findTagById(tagName); // Получаем сущность тега из БД по ID
                    tagEntity.links = notes_taged; // Задаём сущности новый список помеченных тегом записей
                    notesDAO.updateTagE(tagEntity); // Обновляем сущность
                }
            }
            public List<String> getTag_list(){
                return tag_list;
            }
        }
        TagsThread tagsThread = new TagsThread();
        tagsThread.start(); //запусккаем поток
        try {
            tagsThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<String> getAllLinksOfNote(Context context, String fileName){
        class LinkThread extends Thread{
            List<String> link_list = new ArrayList();
            //List<String> tags_of_note = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                link_list = notesDAO.selectLinksOfNote(fileName, link_list);
                link_list.removeAll(Arrays.asList("", " ", "  ", "   ", null));
                // обращение к БД выносим в отдельный потк
            }
            public List<String> getLink_list(){
                return link_list;
            }
        }
        LinkThread linkThread = new LinkThread();
        linkThread.start(); //запусккаем поток
        try {
            linkThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }Log.d(TAG, "List of Links was selected");
        return linkThread.getLink_list();
    }
    @Override
    public List<String> getAllNoteNames(Context context){
        class NameThread extends Thread{
            List<String> file_names_list = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                file_names_list = notesDAO.selectNamesABC();
            }
            public List<String> getNamesList(){
                return file_names_list;
            }
        }
        NameThread nameThread = new NameThread();
        nameThread.start();
        try {
            nameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nameThread.getNamesList();
    }

    @Override
    public List<String> getBackLinksOfNote(Context context, String fileName){
        class LinkThread extends Thread{
            List<String> file_names_list = new ArrayList();
            List<String> back_links = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                file_names_list = notesDAO.selectNamesABC();
                for (String file_name : file_names_list){
                    String lnks = notesDAO.selectLinksOfNote(file_name);
                    if (lnks.contains(fileName)){
                        back_links.add(file_name);
                    }
                }
                //back_links.addAll(notesDAO.selectBackLinksOfNote(fileName));
                //back_links.removeAll(Arrays.asList("", " ", "  ", "   ", null));
                // обращение к БД выносим в отдельный потк
            }
            public List<String> getBack_Links(){
                return back_links;
            }
        }
        LinkThread linkThread = new LinkThread();
        linkThread.start(); //запусккаем поток
        try {
            linkThread.join();
            //объединяем поток поиска ссылок с главным потоком,
            // иначе обратный ссылки не будут показаны сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (linkThread.getBack_Links().isEmpty()){
            Log.d(TAG, fileName + "List of Back Links is Empty");
        }
        Log.d(TAG, "List of Back Links was selected");
        return linkThread.getBack_Links();
    }

    @Override
    public void addNewLinkToANote(Context context, String newLink, String fileName){
        class LinkThread extends Thread{
            List<String> link_list = new ArrayList(); // Инициализируем спискм
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                link_list = notesDAO.selectLinksOfNote(fileName, link_list); // Получаем списог тегов текущей записи
                if (!link_list.contains(newLink)) //Если ссылки на этот файл ещё нет
                {
                    link_list.add(newLink);//Добавляем этот тег в списое тегов
                    link_list.removeAll(Arrays.asList("", " ", "  ", "   ", null)); //Удаляем пустые значения
                    NoteEntity noteEntity = notesDAO.findNoteById(fileName); // Получаем сущность записи из БД по ID
                    noteEntity.links = link_list; // Задаём сущности новый список тегов
                    notesDAO.updateNoteE(noteEntity); // Обновляем сущность
                }
            }
            public List<String> getLink_List(){
                return link_list;
            }
        }
        LinkThread linkThread = new LinkThread();
        linkThread.start(); //запусккаем поток
        try {
            linkThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void detachLinkFromNote(Context context, String linkName, String fileName){
        class DetachmentLinkThread extends Thread{
            List<String> link_list = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                NoteEntity ne = notesDAO.findNoteById(fileName);
                link_list = notesDAO.selectLinksOfNote(fileName, link_list);
                link_list.remove(linkName);
                ne.links = link_list;
                notesDAO.updateNoteE(ne);
                // обращение к БД выносим в отдельный потк
            }
        }
        DetachmentLinkThread detachmentLinkThread = new DetachmentLinkThread();
        detachmentLinkThread.start(); //запусккаем поток
        try {
            detachmentLinkThread.join();
            //объединяем поток  с главным потоком,
            // иначе созданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getNotesTaged(Context context, String tagFileName){
        class LinkThread extends Thread{
            List<String> notes_taged = new ArrayList();
            //List<String> tags_of_note = new ArrayList();
            @Override
            public void run() {
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                notes_taged = notesDAO.selectLinksOfTag(tagFileName, notes_taged);
                notes_taged.removeAll(Arrays.asList("", " ", "  ", "   ", null));
                // обращение к БД выносим в отдельный потк
            }
            public List<String> getNotes_taged(){
                return notes_taged;
            }
        }
        LinkThread linkThread = new LinkThread();
        linkThread.start(); //запусккаем поток
        try {
            linkThread.join();
            //объединяем поток создания с главным потоком,
            // иначе созлданный файл не будет найден сразу
        } catch (InterruptedException e) {
            e.printStackTrace();
        }Log.d(TAG, "List of Links was selected");
        return linkThread.getNotes_taged();
    }
    @Override
    public Integer findNoteOrTag(Context context, String tagFileName){
        class SearchThread extends Thread{
            Integer i;
            @Override
            public void run(){
                NotesDB db;
                db = NotesDB.create(context, false);
                NotesDAO notesDAO = db.notesDAO();
                if (notesDAO.findTagById(tagFileName) != null){
                    i = 1;
                }
                else if (notesDAO.findNoteById(tagFileName) != null) {
                    i = 2;
                } else i = 0;
            }
            public Integer getI(){
                return i;
            }
        }
        SearchThread searchThread = new SearchThread();
        searchThread.start();
        try {
            searchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }Log.d(TAG, "List of Links was selected");
        return searchThread.getI();

    }
}
