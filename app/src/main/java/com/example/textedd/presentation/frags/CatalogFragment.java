package com.example.textedd.presentation.frags;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textedd.shared.adapters.NoteAdapter;
import com.example.textedd.R;
import com.example.textedd.domain.CatalogPresenter;
import com.example.textedd.presentation.MainActivity;
import com.example.textedd.shared.contracts.CatalogContract;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CatalogFragment extends Fragment implements NoteAdapter.OnNoteListener, CatalogContract.View, MenuProvider {
    private static final String TAG = "CatalogFragment";
    @SuppressLint("MissingInflatedId")
    private String dialogInput;
    private Context context;
    private View mView;
    private CatalogContract.Presenter mPresenter;
    List<File> textFiles = new ArrayList<>();
    NoteAdapter adapter;
    //File USER_DIR;

    private boolean sortABC;
    private boolean sortOrderReverse;

    public CatalogFragment() {
    }
    public static CatalogFragment newInstance(String param1, String param2) {
        CatalogFragment fragment = new CatalogFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getApplicationContext();
        /*
        assert getArguments() != null;
        File arg = (File) getArguments().getSerializable("dr"); //получить bundle и использовать его содержимое
        if(arg != null && arg.isDirectory()){
            USER_DIR = arg;
        }*/
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.catalog, container, false);
        mPresenter = new CatalogPresenter(this);
        context = requireActivity().getApplicationContext();
        //USER_DIR = context.getFilesDir();
        return mView;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        menuInflater.inflate(R.menu.menu_catalog, menu);
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
                                        dialogInput = String.valueOf(userInput.getText());
                                        String FILENAME = dialogInput + ".md";
                                        //textView.setText(dialogInput);
                                        mPresenter.findNoteOrTag(context, FILENAME);
                                        dialog.cancel();
                                    })
                            .setNegativeButton("Отмена",
                                    (dialog, id) -> dialog.cancel());
                    //Создаем AlertDialog:
                    AlertDialog alertDialog = mDialogBuilder.create();
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.
                            TYPE_APPLICATION_PANEL);
                    //и отображаем его:
                    alertDialog.show();
                } catch (Throwable t) {
                    Toast.makeText(requireActivity(),
                            "Exception: " + t,
                            Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
                return true;
            default:
                return true;
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        assert context != null;
        textFiles = mPresenter.getAllFilesList(context, textFiles);
        adapter = new NoteAdapter(textFiles, this);
        mRecyclerView.setAdapter(adapter);
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
                updateRV();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SwitchCompat switchCompat = view.findViewById(R.id.switch2);
        if (switchCompat != null) {
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sortOrderReverse = !sortOrderReverse;
                updateRV();
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRV(){
        textFiles.clear();
        textFiles.addAll(mPresenter.getAllFilesList(context, textFiles));
        if (sortABC) Collections.sort(textFiles);
        if (sortOrderReverse) Collections.reverse(textFiles);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Taged_Link_RV_was_updated");
    }
    @Override
    public void openFileAsNote(String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_catalogFragment_to_noteFragment, bundle); //Переход к другому экрану
        Log.d(TAG, "Catalog passes the bundle");
    }
    @Override
    public void openFileAsTag(String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString("fn", fileName); //Передача данных между экранами назначения
        Navigation.findNavController(mView).navigate(R.id.action_catalogFragment_to_tag_frag, bundle); //Переход к другому экрану
        Log.d(TAG, "Catalog passes the bundle");
    }

    @Override
    public void onNoteClick(int position) {
        mPresenter.findNoteOrTag(context, textFiles.get(position).getName());
    }
}
