package com.example.textedd.presentation.frags;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textedd.shared.contracts.MainContract;
import com.example.textedd.shared.markwon.MarkwonET;
import com.example.textedd.R;
import com.example.textedd.presenters.NotePresenter;
import com.example.textedd.presentation.MainActivity;
import com.example.textedd.shared.adapters.LinksAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagFragment extends Fragment implements MainContract.View, MenuProvider {
    private static final String TAG = "TagFragment";

    private View mView;

    private static String filename = "sampleTag.md"; // имя файла
    private EditText mEditText;

    private MainContract.Presenter mPresenter;
    private RecyclerView linksRecyclerView;
    private List<String> links_list = new ArrayList();
    private LinksAdapter linkAdapter;
    Context context;

    private boolean sortABC;
    private boolean sortOrderReverse;

    public TagFragment() {
        // Required empty public constructor
    }

    public static TagFragment newInstance(String param1, String param2) {
        TagFragment fragment = new TagFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getApplicationContext();
        mPresenter = new NotePresenter(this);
        //Создаём Presenter и в аргументе передаём ему this -
        // эта Activity расширяет интерфейс MainContract.View
        Log.d(TAG, "Tg Fragment Was Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag, container, false);
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Tag View Was Created");
        assert getArguments() != null;
        String arg = getArguments().getString("fn"); //получить bundle и использовать его содержимое
        if(arg != null && !arg.isEmpty()){
            filename = arg;
        }
        Log.d(TAG, "Tag Got the bundle " + filename);
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        //Инициализируем элементы:
        mEditText = view.findViewById(R.id.editText);
        Button head1 = (Button) view.findViewById(R.id.heading1);
        Button head2 = (Button) view.findViewById(R.id.heading2);
        Button bold = (Button) view.findViewById(R.id.bold);
        Button italic = (Button) view.findViewById(R.id.italic);
        Button strike = (Button) view.findViewById(R.id.strike);
        Button quote = (Button) view.findViewById(R.id.quote);
        //code = (Button) view.findViewById(R.id.code);
        mEditText.setScroller(new Scroller(context));
        mEditText.setMaxLines(12);
        mEditText.setVerticalScrollBarEnabled(true);
        mEditText.setMovementMethod(new ScrollingMovementMethod());
        mEditText.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.editText) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });

        this.addSpan(bold, new StrongEmphasisSpan());
        this.addSpan(italic, new EmphasisSpan());
        this.addSpan(strike, new StrikethroughSpan());

        head1.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "# "));
        head2.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "## "));
        bold.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "**"));
        italic.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "_"));
        strike.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "~~"));
        //code.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "`"));
        quote.setOnClickListener((View.OnClickListener) (it -> {
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            if (start >= 0) {
                if (start == end) {
                    mEditText.getText().insert(start, (CharSequence) "> ");
                } else {
                    ArrayList<Integer> newLines = (new ArrayList<>(3));
                    newLines.add(start);
                    String text = mEditText.getText().subSequence(start, end).toString();
                    int index = text.indexOf('\n');
                    while (index != -1) {
                        newLines.add(start + index + 1);
                        index = text.indexOf('\n', index + 1);
                    }
                    for (int i = newLines.size() - 1; i >= 0; --i) {
                        mEditText.getText().insert(newLines.get(i), (CharSequence) "> ");
                    }
                }

            }
        }));

        // Получаем экземпляр элемента Spinner
        Spinner spinner = view.findViewById(R.id.spinner4);

        // Настраиваем адаптер
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(context, R.array.sort_vars,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Вызываем адаптер
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.sort_vars);
                if (selectedItemPosition == 0) {
                    sortABC = false;
                }
                else if (selectedItemPosition == 1){
                    sortABC = true;
                }
                updateLinksRV();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SwitchCompat switchCompat = view.findViewById(R.id.switch2);
        if (switchCompat != null) {
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sortOrderReverse = !sortOrderReverse;
                updateLinksRV();
            });
        }


        //textView = (TextView) view.findViewById(R.id.textView);
        linksRecyclerView = view.findViewById(R.id.recyclerViewTaged);
        linksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mPresenter.presentNoteContent(context, filename);
        MarkwonET.render(context, mEditText);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        Objects.requireNonNull(((MainActivity) requireActivity()).
                        getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        menuInflater.inflate(R.menu.menu_main, menu);
        Log.d(TAG, "ContextMenu created");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_open:
                try {
                    //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompt, null);
                    //Создаем AlertDialog
                    AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
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
                                        String dialogInput = String.valueOf(userInput.getText());
                                        String filename = mPresenter.addFileExtensions(dialogInput);
                                        //textView.setText(dialogInput);
                                        mPresenter.findNoteOrTag(context, filename);
                                        //mPresenter.presentNoteContent(context, filename);
                                        dialog.cancel();
                                    })
                            .setNegativeButton("Отмена",
                                    (dialog, id) -> dialog.cancel());
                    //Создаем AlertDialog:
                    AlertDialog alertDialog = mDialogBuilder.create();
                    //и отображаем его:
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.
                            TYPE_APPLICATION_PANEL);
                    alertDialog.show();
                    //Открываем файл
                    //mPresenter.presentNoteContent(context, filename);
                } catch (Throwable t) {
                    Toast.makeText(context.getApplicationContext(),
                            "Exception: " + t,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,"OpenErr " + filename , t);
                    t.printStackTrace();
                }
                //openFile(FILENAME);
                return true;
            case R.id.action_save:
                mPresenter.saveContent(context, filename);
                return true;
            case R.id.action_delete:
                try {
                    LayoutInflater lif = LayoutInflater.from(context);
                    View proView = lif.inflate(R.layout.prompt_del, null);

                    //Создаем AlertDialog
                    AlertDialog.Builder dDialogBuilder = new AlertDialog.Builder(getActivity());

                    //Настраиваем prompt.xml для нашего AlertDialog:
                    dDialogBuilder.setView(proView);

                    dDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id) -> {
                                        //OpenCatalogUseCase.openCatalog(context);
                                        Navigation.findNavController(mView).navigate(R.id.action_tag_frag_to_catalogFragment);
                                        mPresenter.deleteTag(context, filename);
                                        dialog.cancel();
                                    })
                            .setNegativeButton("Отмена",
                                    (dialog, id) -> dialog.cancel());
                    //Создаем AlertDialog:
                    AlertDialog alertDialog = dDialogBuilder.create();

                    //и отображаем его:
                    alertDialog.show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            case R.id.action_list_files:
                Navigation.
                        findNavController(mView).navigate
                                (R.id.action_tag_frag_to_catalogFragment);
            default:
                return true;
        }
    }

    @Override
    public void showText(String fileName, String content) {
        filename = fileName;
        Markwon markwon = Markwon.create(context);
        //textView.setText(fileName);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).
                getSupportActionBar()).setTitle(filename); //Уставаливаем имя записи в экшнбаре
        MarkwonEditor editor = MarkwonEditor.create(markwon);
        mEditText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                mEditText));
        mEditText.setText(content);
    }

    @Override
    public String getEditTextAsString() {
        String content = mEditText.getText().toString();
        return content;
    }

    @Override
    public void createTagRV() {}

    @Override
    public void updateTagRV() {}

    @Override
    public void openByLink(String fileName){
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_tag_frag_to_noteFragment, bundle); //Переход к другому экрану
        Log.d(TAG, "Tag passes the bundle");
    }

    public class OnLinkListener implements LinksAdapter.OnLinkListener {
        @Override
        public void onLinkClick(int position) {
            openByLink(links_list.get(position));
        }
        @SuppressLint("SetTextI18n")
        @Override
        public boolean onLinkLongClick(int position) {
            Log.d(TAG, "|" + links_list.get(position) + "|Long Pressed");
            try {
                LayoutInflater lif = LayoutInflater.from(context);
                View proView = lif.inflate(R.layout.prompt_del, null);
                //Создаем AlertDialog
                AlertDialog.Builder dDialogBuilder =
                        new AlertDialog.Builder(getActivity());
                //Настраиваем prompt.xml для нашего AlertDialog:
                dDialogBuilder.setView(proView);
                TextView tv = proView.findViewById(R.id.tv);
                tv.setText("Убрать ссылку на " +
                        links_list.get(position) + "?");
                dDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    mPresenter.removeTagfromNote(context,
                                            filename,
                                            links_list.get(position));
                                    updateLinksRV();
                                    dialog.cancel();
                                })
                        .setNegativeButton("Отмена",
                                (dialog, id) -> dialog.cancel());
                //Создаем AlertDialog:
                AlertDialog alertDialog = dDialogBuilder.create();
                //и отображаем его:
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    @Override
    public void createLinksRV(){
        links_list.clear();
        links_list.addAll(mPresenter.getNotesTaged(context, filename));
        if (sortABC) Collections.sort(links_list);
        //tag_list.removeAll(Arrays.asList("", " ", "  ", "   ", null));
        TagFragment.OnLinkListener onLinkListener = new TagFragment.OnLinkListener();
        linkAdapter = new LinksAdapter(links_list, onLinkListener);
        linksRecyclerView.setAdapter(linkAdapter);
        //DividerItemDecoration dividerItemDecorationH = new DividerItemDecoration(linksRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration dividerItemDecorationV =
                new DividerItemDecoration(linksRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
        //linksRecyclerView.addItemDecoration(dividerItemDecorationH);
        linksRecyclerView.addItemDecoration(dividerItemDecorationV);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void updateLinksRV() {
        if (linkAdapter == null) {
            createLinksRV();
            Log.d(TAG, "Taged_Link_RV_was_created");
        } else {
            links_list.clear();
            links_list.addAll(mPresenter.getNotesTaged(context, filename));
            if (sortABC) Collections.sort(links_list);
            if (sortOrderReverse) Collections.reverse(links_list);
            linkAdapter.notifyDataSetChanged();
            Log.d(TAG, "Taged_Link_RV_was_updated");
        }
    }



    @Override
    public void openTag(String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_tag_frag_self, bundle); //Переход к другому экрану
        Log.d(TAG, "Tag passes the bundle");
    }

    public void addSpan(TextView textView, Object... spans) {
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        int end = builder.length();
        //int var8 = spans.length;
        for (Object span : spans) {
            builder.setSpan(span, 0, end, 33);
        }
        textView.setText((CharSequence)builder);
    }
}