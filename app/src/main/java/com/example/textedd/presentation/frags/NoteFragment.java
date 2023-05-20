package com.example.textedd.presentation.frags;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textedd.MainContract;
import com.example.textedd.MarkwonET;
import com.example.textedd.R;
import com.example.textedd.TagsOfNote_Adapter;
import com.example.textedd.domain.NotePresenter;
import com.example.textedd.presentation.MainActivity;
import com.example.textedd.shared.LinksAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements MainContract.View, MenuProvider {
    private static final String TAG = "NoteFragment";



    private View mView;

    private static String filename = "sample.md"; // имя файла
    private EditText mEditText;
    MenuItem item;

    private MainContract.Presenter mPresenter;

    private Button bold, italic, strike, quote, code, head1, head2, tagButton;
    private RecyclerView tagRecyclerView, linksRecyclerView, backLinkRecyclerView;
    private List<String> tag_list = new ArrayList();
    private List<String> links_list = new ArrayList();
    private List<String> b_links_list = new ArrayList();
    List<String> suggests_list = new ArrayList();
    boolean focusOnET;

    TagsOfNote_Adapter tagAdapter;

    LinksAdapter linkAdapter;

    LinksAdapter backLinkAdapter;

    //Для диалогового окна
    private String dialogInput;
    Context context;

    SharedPreferences pref;

    //private Button button;
    private TextView textView;
    private FloatingActionButton fab;

    public NoteFragment() {
        // Required empty public constructor
    }
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getApplicationContext();
        Log.d(TAG, "Note Fragment Was Created");
        //setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {// Inflate the layout for this fragment
        assert getArguments() != null;
        String arg = getArguments().getString("fn"); //получить bundle и использовать его содержимое
        if(arg != null && !arg.isEmpty()){
            filename = arg;
        }
        Log.d(TAG, "Note Got the bundle " + filename);
        context = requireActivity().getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        mPresenter = new NotePresenter(this);
        Log.d(TAG, "Note View Create");
        //setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_note, container, false);

    }
    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        Objects.requireNonNull(((MainActivity) requireActivity()).
                getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        menuInflater.inflate(R.menu.menu_main, menu);
        Log.d(TAG, "ContextMenu created");
    }


    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Log.d(TAG, "Note View Was Created");
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        //Создаём Presenter и в аргументе передаём ему this - эта Activity расширяет интерфейс MainContract.View
        //Инициализируем элементы:
        mEditText = view.findViewById(R.id.editText);
        head1 = (Button) view.findViewById(R.id.heading1);
        head2 = (Button) view.findViewById(R.id.heading2);
        bold = (Button) view.findViewById(R.id.bold);
        italic = (Button) view.findViewById(R.id.italic);
        strike = (Button) view.findViewById(R.id.strike);
        quote = (Button) view.findViewById(R.id.quote);
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
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
        this.mPresenter.addSpan(bold, new StrongEmphasisSpan());
        this.mPresenter.addSpan(italic, new EmphasisSpan());
        this.mPresenter.addSpan(strike, new StrikethroughSpan());

        head1.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "# "));
        head2.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "## "));
        bold.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "**"));
        italic.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "_"));
        strike.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "~~"));
        //code.setOnClickListener(new MarkwonET.InsertOrWrapClickListener(mEditText, "`"));
        quote.setOnClickListener((View.OnClickListener)(it -> {
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            if (start >= 0) {
                if (start == end) {
                    mEditText.getText().insert(start, (CharSequence)"> ");
                } else {
                    ArrayList<Integer> newLines = (new ArrayList<>(3));
                    newLines.add(start);
                    String text = mEditText.getText().subSequence(start, end).toString();
                    int index = text.indexOf('\n');
                    while (index != -1) {
                        newLines.add(start + index + 1);
                        index = text.indexOf('\n', index + 1);
                    }
                    for(int i = newLines.size() - 1; i >= 0; --i) {
                        mEditText.getText().insert(newLines.get(i), (CharSequence)"> ");
                    }
                }

            }
        }));

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.saveContent(context, filename);

            }
        });
        textView = (TextView) view.findViewById(R.id.textView);

        tagRecyclerView = view.findViewById(R.id.recyclerView);
        tagRecyclerView.setLayoutManager(horizontalLayoutManager);

        linksRecyclerView = view.findViewById(R.id.recyclerView2);
        linksRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        backLinkRecyclerView = view.findViewById(R.id.recyclerView3);
        backLinkRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mPresenter.presentNoteContent(context, filename);
        MarkwonET.render(context, mEditText);

        tagButton = view.findViewById(R.id.new_tag_butt);
        View.OnClickListener tbListener = v -> {
            try {
                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);
                TextView tv = promptsView.findViewById(R.id.tv);
                tv.setText("Добавить в запись тег:");
                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);

                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    //Вводим текст и получаем имя файла
                                    String dialogInput = String.valueOf(userInput.getText());
                                    String tagFileName = dialogInput + ".md";
                                    //textView.setText(dialogInput);
                                    mPresenter.addNewTagToANote(context, tagFileName, filename);
                                    tag_list.add(tagFileName);
                                    updateTagRV();
                                    //tagRecyclerView.setAdapter(tagAdapter);
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
                t.printStackTrace();
            }
        };
        tagButton.setOnClickListener(tbListener);

        fab = view.findViewById(R.id.fab_add_link);

        View.OnClickListener fabListener = v -> {
            suggests_list = mPresenter.suggestLinks(context);
            Log.d(TAG, suggests_list.toString());
            try {

                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt_link, null);
                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);
                TextView tv = promptsView.findViewById(R.id.tv);
                tv.setText("Добавить ссылку на запись:");
                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
                RecyclerView suggLinkRecyclerView = promptsView.findViewById(R.id.sug_link_rv);
                suggLinkRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                OnSuggLinkListener onSuggLinkListener = new OnSuggLinkListener();
                LinksAdapter suggLinkAdapter = new LinksAdapter(suggests_list, onSuggLinkListener);
                suggLinkRecyclerView.setAdapter(suggLinkAdapter);
                DividerItemDecoration dividerItemDecorationH = new DividerItemDecoration(suggLinkRecyclerView.getContext(),
                        DividerItemDecoration.HORIZONTAL);
                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    //Вводим текст и получаем имя файла
                                    String dialogInput = String.valueOf(userInput.getText());
                                    if (!dialogInput.isEmpty()) {
                                        String newLink = dialogInput + ".md";
                                        mPresenter.addNewLinkToANote(context, newLink, filename);
                                        mPresenter.createNote(context, newLink);
                                    }
                                    updateLinksRV();
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
                t.printStackTrace();
            }
        };
        fab.setOnClickListener(fabListener);

        // получение вью нижнего экрана
        LinearLayout llBottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);

        // настройка поведения нижнего экрана
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // настройка состояний нижнего экрана
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // настройка максимальной высоты
        //bottomSheetBehavior.setPeekHeight(204);

        // настройка возможности скрыть элемент при свайпе вниз
        bottomSheetBehavior.setHideable(false);

        // настройка колбэков при изменениях
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    //Toast.makeText(context, "Got the focus", Toast.LENGTH_LONG).show();
                    fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                    focusOnET = true;
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    //Toast.makeText(context, "Lost the focus", Toast.LENGTH_LONG).show();
                    fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                    focusOnET = false;
                }
            }
        });
    }



    public void onStart() {
        super.onStart();
        //filename = getIntent().getStringExtra("fn");
        mPresenter.presentNoteContent(context, filename);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showText(String fileName, String content) {
        filename = fileName;
        Markwon markwon = Markwon.create(context);
        //textView.setText(fileName);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(filename); //Уставаливаем имя записи в экшнбаре
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
                                        if (!dialogInput.isEmpty()) {
                                            String filename = dialogInput + ".md";
                                            mPresenter.findNoteOrTag(context, filename);}
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
                                        Navigation.findNavController(mView).navigate(R.id.action_noteFragment_to_catalogFragment2);
                                        mPresenter.deleteNote(context, filename);
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
                                (R.id.action_noteFragment_to_catalogFragment2);
            default:
                return true;
        }
    }

    public class OnTagListener implements TagsOfNote_Adapter.OnTagListener {
        @Override
        public void onTagClick(int position) {
            Toast.makeText(context.getApplicationContext(),
                    tag_list.get(position),
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "|" + tag_list.get(position) + "|Clicked");

            openTag(tag_list.get(position));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public boolean onTagLongClick(int position) {
            // Handle long click
            // Return true to indicate the click was handled
            Toast.makeText(context.getApplicationContext(),
                    tag_list.get(position) + "Long Pressed",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "|" + tag_list.get(position) + "|Long Pressed");
            try {
                LayoutInflater lif = LayoutInflater.from(context);
                View proView = lif.inflate(R.layout.prompt_del, null);
                //Создаем AlertDialog
                AlertDialog.Builder dDialogBuilder = new AlertDialog.Builder(getActivity());
                //Настраиваем prompt.xml для нашего AlertDialog:
                dDialogBuilder.setView(proView);
                TextView tv = proView.findViewById(R.id.tv);
                tv.setText("Убрать тег " + tag_list.get(position) + " из текущей записи?");
                dDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    //OpenCatalogUseCase.openCatalog(context);
                                    //mPresenter.deleteTag(context, tag_list.get(position));
                                    mPresenter.removeTagfromNote(context, tag_list.get(position), filename);
                                    updateTagRV();
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
    public void createTagRV(){
        OnTagListener onTagListener = new OnTagListener();
        tag_list.clear();
        tag_list.addAll(mPresenter.getAllTagsOfNote(context, filename));
        tagAdapter = new TagsOfNote_Adapter(tag_list, onTagListener);
        tagRecyclerView.setAdapter(tagAdapter);
        DividerItemDecoration dividerItemDecorationH = new DividerItemDecoration(tagRecyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration dividerItemDecorationV = new DividerItemDecoration(tagRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        tagRecyclerView.addItemDecoration(dividerItemDecorationH);
        tagRecyclerView.addItemDecoration(dividerItemDecorationV);
        Log.d(TAG, "Tag_RV_was_created");


    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void updateTagRV(){
        //tag_list.removeAll(Arrays.asList("", " ", "  ", "   ", null));
        if (tagAdapter == null) {
            createTagRV();
            Log.d(TAG, "Tag_RV_was_created");
        } else {
            tag_list.clear();
            tag_list.addAll(mPresenter.getAllTagsOfNote(context, filename));
            tagAdapter.notifyDataSetChanged();
            Log.d(TAG, "Tag_RV_was_updated");
        }
    }

    @Override
    public void createLinksRV(){
        links_list.clear();
        links_list.addAll(mPresenter.getAllLinksOfNote(context, filename));
        OnLinkListener onLinkListener = new OnLinkListener();
        linkAdapter = new LinksAdapter(links_list, onLinkListener);
        linksRecyclerView.setAdapter(linkAdapter);
        DividerItemDecoration dividerItemDecorationH =
                new DividerItemDecoration(linksRecyclerView.getContext(),
                        DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration dividerItemDecorationV =
                new DividerItemDecoration(linksRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
        linksRecyclerView.addItemDecoration(dividerItemDecorationH);
        linksRecyclerView.addItemDecoration(dividerItemDecorationV);

        b_links_list.clear();
        b_links_list.addAll(mPresenter.getBackLinksOfNote(context, filename));
        OnBackLinkListener onBackLinkListener = new OnBackLinkListener();
        backLinkAdapter = new LinksAdapter(b_links_list, onBackLinkListener);
        backLinkRecyclerView.setAdapter(backLinkAdapter);
        DividerItemDecoration backDividerItemDecorationH =
                new DividerItemDecoration(backLinkRecyclerView.getContext(),
                        DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration backDividerItemDecorationV =
                new DividerItemDecoration(backLinkRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
        backLinkRecyclerView.addItemDecoration(backDividerItemDecorationH);
        backLinkRecyclerView.addItemDecoration(backDividerItemDecorationV);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void updateLinksRV(){
        if (linkAdapter == null) {
            createLinksRV();
            Log.d(TAG, "Link_RV_was_created");
        } else {
            links_list.clear();
            links_list.addAll(mPresenter.getAllLinksOfNote(context, filename));
            linkAdapter.notifyDataSetChanged();
            Log.d(TAG, "Link_RV_was_updated");
        }
    }
    public class OnBackLinkListener implements LinksAdapter.OnLinkListener{

        @Override
        public void onLinkClick(int position) {
            openByLink(b_links_list.get(position));
        }

        @Override
        public boolean onLinkLongClick(int position) {
            openByLink(b_links_list.get(position));
            return false;
        }
    }

    public class OnSuggLinkListener implements LinksAdapter.OnLinkListener{
        @Override
        public void onLinkClick(int position) {
            mPresenter.addNewLinkToANote(context, suggests_list.get(position), filename);
            updateLinksRV();
        }

        @Override
        public boolean onLinkLongClick(int position) {
            mPresenter.addNewLinkToANote(context, suggests_list.get(position), filename);
            updateLinksRV();
            return true;
        }
    }


    public class OnLinkListener implements LinksAdapter.OnLinkListener {
        @Override
        public void onLinkClick(int position) {
            //openByLink(links_list.get(position));
            mPresenter.findNoteOrTag(context, links_list.get(position));
        }
        @Override
        public boolean onLinkLongClick(int position) {
            Toast.makeText(context.getApplicationContext(),
                    links_list.get(position) + "Long Pressed",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "|" + links_list.get(position) + "|Long Pressed");
            try {
                LayoutInflater lif = LayoutInflater.from(context);
                View proView = lif.inflate(R.layout.prompt_del, null);
                //Создаем AlertDialog
                AlertDialog.Builder dDialogBuilder = new AlertDialog.Builder(getActivity());
                //Настраиваем prompt.xml для нашего AlertDialog:
                dDialogBuilder.setView(proView);
                TextView tv = proView.findViewById(R.id.tv);
                tv.setText("Убрать ссылку на " + links_list.get(position) + "?");
                dDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    mPresenter.removeLinkFromNote(context, links_list.get(position), filename);
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
    public void openByLink(String fileName){
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_noteFragment_self, bundle); //Переход к другому экрану
        Log.d(TAG, "Note passes the bundle");
    }
    @Override
    public void openTag(String fileName){
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_noteFragment_to_tag_frag, bundle); //Переход к другому экрану
        Log.d(TAG, "Note passes the bundle");
    }
}