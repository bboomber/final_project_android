package my.calendar.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import my.calendar.myapplication2.Adapter.MyAdapter;
import my.calendar.myapplication2.Model.NoteItem;
import my.calendar.myapplication2.Model.ToDoItem;


public class MainActivity extends Activity implements MyAdapter.ClickListener {
    private List<ToDoItem> toDoItems;
    private MyAdapter adapter;
    private Realm realm;

    private SimpleDateFormat simpleDateFormat;
    private String date;
    private TextView noteTv;
    ImageView feelingView;
    int myfeeling = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar calendar = Calendar.getInstance();
        noteTv = findViewById(R.id.noteTV);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = simpleDateFormat.format(calendar.getTime());
        noteTv.setText(date);
        feelingView = findViewById(R.id.feelingView);

        CalendarView mCalendarView = (CalendarView) findViewById(R.id.calendarView);
//        mCalendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + "/" + (i1+1) + "/" + i;
                noteTv.setText(date);
                String mynote = String.valueOf(realm.where(NoteItem.class).equalTo("date", date).findFirst());
                if (mynote.equals("null")){
                    feelingView.setImageResource(R.drawable.happy);
                } else{
                    setNoteView(date);
                    setfeeling(date);
                }
            }
        });
        myfeeling =0;
        realm = Realm.getDefaultInstance();
        setUpView();

    }

    private void setfeeling(String date) {
        myfeeling = realm.where(NoteItem.class).equalTo("date", date).findFirst().getFeeling();
        if (myfeeling == 0){
            feelingView.setImageResource(R.drawable.happy);
        }
        if (myfeeling == 1){
            feelingView.setImageResource(R.drawable.cry);
        }
        if (myfeeling == 2){
            feelingView.setImageResource(R.drawable.bad);
        }
        if (myfeeling == 3){
            feelingView.setImageResource(R.drawable.love);
        }
        if (myfeeling == 4){
            feelingView.setImageResource(R.drawable.omg);
        }
        if (myfeeling == 5){
            feelingView.setImageResource(R.drawable.sick);
        }
    }


    private void setNoteView(String date) {
        String mynote = String.valueOf(realm.where(NoteItem.class).equalTo("date", date).findFirst().getNote());
        noteTv.setText(mynote);
    }

    private void setUpView() {
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 1));

        toDoItems = new ArrayList<>();
        adapter = new MyAdapter(toDoItems, this);

        recycler.setAdapter(adapter);
        setUpItems();
    }

    private void setUpItems() {
        List<ToDoItem> items = realm.copyFromRealm(realm.where(ToDoItem.class).findAll());
        toDoItems.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void addButtonPressed(View view) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .typeface(Typeface.SANS_SERIF, Typeface.SANS_SERIF)
                .title("ADD TODO")
                .autoDismiss(false)
                .customView(R.layout.dialog_add_todo, true)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorRed)
                .positiveText("ADD")
                .negativeText("Cancel");

        MaterialDialog dialog = builder.build();
        final EditText name_input = (EditText) dialog.findViewById(R.id.name_input);

        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                realm.beginTransaction();
                Number currentIdNum = realm.where(ToDoItem.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.setText(String.valueOf(name_input.getText()));
                toDoItem.setId(nextId);
                realm.copyToRealmOrUpdate(toDoItem);
                realm.commitTransaction();
                toDoItems.add(toDoItem);

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onCLick(ToDoItem item, int position) {
        realm.beginTransaction();
        realm.where(ToDoItem.class).equalTo("id", item.getId()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        toDoItems.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void noteBtnclick(View view) {
        Intent intent = new Intent(MainActivity.this, AddNote.class);
        intent = intent.putExtra("date", date);
        startActivity(intent);
    }

    public void todayBtnClick(View view) {
        CalendarView mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);

    }
}
