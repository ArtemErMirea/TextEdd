package com.example.textedd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;


public class MainActivity extends AppCompatActivity {
    private static String FILENAME = "sample.md"; // имя файла
    private EditText mEditText;
    //Для диалогового окна
    private String dialogInput;
    final Context context = this;
    //private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Инициализируем элементы:
        mEditText = findViewById(R.id.editText);
        final Markwon markwon = Markwon.create(context);
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        mEditText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                mEditText));

        //button = (Button) findViewById(R.id.prompt_button);
        textView = (TextView) findViewById(R.id.textView);
        //Добавляем слушателя нажатий по кнопке Button:
        /*
        button.setOnClickListener(new OnClickListener()
            @Override
            public void onClick(View arg0) {
                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);
                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        FILENAME = String.valueOf(userInput.getText());}})
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();}});} */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /*Метод getMenuInflater возвращает объект MenuInflater, у которого вызывается метод inflate().
    Этот метод в качестве первого параметра принимает ресурс,
    представляющий наше декларативное описание меню в xml,
    и наполняет им объект menu, переданный в качестве второго параметра.*/

    @SuppressLint("NonConstantResourceId") //подавление ложных срабатываний
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
               try {
                   //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                   LayoutInflater li = LayoutInflater.from(context);
                   View promptsView = li.inflate(R.layout.prompt, null);

                   //Создаем AlertDialog
                   AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

                   //Настраиваем prompt.xml для нашего AlertDialog:
                   mDialogBuilder.setView(promptsView);

                   //Настраиваем отображение поля для ввода текста в открытом диалоге:
                   final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);

                   //Настраиваем сообщение в диалоговом окне:
                   mDialogBuilder
                           .setCancelable(false)
                           .setPositiveButton("OK",
                                   (dialog, id) -> {
                                       //Вводим текст и получаем имя файла
                                       dialogInput = String.valueOf(userInput.getText());
                                       FILENAME = dialogInput + ".md";
                                       //textView.setText(dialogInput);
                                       openFile(FILENAME);
                                   })
                           .setNegativeButton("Отмена",
                                   (dialog, id) -> dialog.cancel());
                   //Создаем AlertDialog:
                   AlertDialog alertDialog = mDialogBuilder.create();

                   //и отображаем его:
                   alertDialog.show();
                   //Открываем файл
                   openFile(FILENAME);
               }catch (Throwable t){
                   Toast.makeText(getApplicationContext(),
                           "Exception: " + t.toString(),
                           Toast.LENGTH_LONG).show();
               }
                openFile(FILENAME);
                return true;
            case R.id.action_save:
                saveFile(FILENAME);
                return true;
            default:
                return true;
        }
}

    // Метод для открытия файла
    private void openFile(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                inputStream.close();
                textView.setText(fileName);
                mEditText.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Метод для сохранения файла
    private void saveFile(String fileName) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(mEditText.getText().toString());
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}