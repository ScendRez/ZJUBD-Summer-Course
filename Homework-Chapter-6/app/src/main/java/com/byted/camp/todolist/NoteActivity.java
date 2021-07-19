package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.SpDemoActivity;

import java.io.File;


public class NoteActivity extends AppCompatActivity {

    private static final String KEY_DRAFT = "note_draft";

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton lowRadio, midRadio, highRadio;
    private String mFileName = null;

    private final String NOTE_ADD = "note_add";
    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadio = findViewById(R.id.btn_low);
        midRadio = findViewById(R.id.btn_medium);
        highRadio = findViewById(R.id.btn_high);


        addBtn = findViewById(R.id.btn_add);


        SharedPreferences sp = NoteActivity.this.getSharedPreferences(NOTE_ADD, MODE_PRIVATE);
        String value_content = sp.getString("editContent", "");
        Integer value_prior = sp.getInt("selected_prior", 0);
        lowRadio.setChecked(false);
        midRadio.setChecked(false);
        highRadio.setChecked(false);
        switch (value_prior) {
            case 0:
                lowRadio.setChecked(true);
                break;
            case 1:
                midRadio.setChecked(true);
                break;
            case 2:
                highRadio.setChecked(true);
                break;
        }
        editText.setText(value_content);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),
                        getSelectedPriority());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;

        String value_content = editText.getText().toString();
        Integer value_prior = getSelectedPriority().intValue;
        SharedPreferences sp = NoteActivity.this.getSharedPreferences(NOTE_ADD, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("editContent", value_content);
        editor.putInt("selected_prior", value_prior);
        editor.apply();
    }

    private boolean saveNote2Database(String content, Priority priority) {
        // TODO: 2021/7/19 8. 这里插入数据库
        ContentValues cv = new ContentValues();
        cv.put(TodoNote.COLUMN_CONTENT, content);
        cv.put(TodoNote.COLUMN_PRIORITY, priority.intValue);
        cv.put(TodoNote.COLUMN_ISFINISHED, 0);
        cv.put(TodoNote.COLUMN_DATE, System.currentTimeMillis());
        long rowID = database.insert(TodoNote.TABLE_NAME, null, cv);
//        Log.i("savee", String.valueOf(rowID));
        return rowID != -1;
    }

    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.High;
            case R.id.btn_medium:
                return Priority.Medium;
            default:
                return Priority.Low;
        }
    }
}
