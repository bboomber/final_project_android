package my.calendar.myapplication2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import my.calendar.myapplication2.Adapter.MyAdapter;
import my.calendar.myapplication2.Model.NoteItem;
import my.calendar.myapplication2.Model.ToDoItem;

public class MyCalendar extends AppCompatActivity implements MyAdapter.ClickListener {

    private List<ToDoItem> toDoItems;
    private MyAdapter adapter;
    private Realm realm;
    private FirebaseUser mUser;

    private SimpleDateFormat simpleDateFormat;
    private String date;
    private TextView noteTv, fbName;
    private ConstraintLayout myCalendarView;
    ImageView feelingView;
    int myfeeling = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);
        Calendar calendar = Calendar.getInstance();
        noteTv = findViewById(R.id.noteTV);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = simpleDateFormat.format(calendar.getTime());
        noteTv.setText(date);
        feelingView = findViewById(R.id.feelingView);

        this.myCalendarView = findViewById(R.id.myCalendarView);

        CalendarView mCalendarView = (CalendarView) findViewById(R.id.calendarView);
//        mCalendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + "/" + (i1 + 1) + "/" + i;
                noteTv.setText(date);
                String mynote = String.valueOf(realm.where(NoteItem.class).equalTo("date", date).findFirst());
                if (mynote.equals("null")) {
                    feelingView.setImageResource(R.drawable.happy);
                } else {
                    setNoteView(date);
                    setfeeling(date);
                }
            }
        });
        myfeeling = 0;
        realm = Realm.getDefaultInstance();

        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
        setUpView();

    }

    private void setfeeling(String date) {
        myfeeling = realm.where(NoteItem.class).equalTo("date", date).findFirst().getFeeling();
        if (myfeeling == 0) {
            feelingView.setImageResource(R.drawable.happy);
        }
        if (myfeeling == 1) {
            feelingView.setImageResource(R.drawable.cry);
        }
        if (myfeeling == 2) {
            feelingView.setImageResource(R.drawable.bad);
        }
        if (myfeeling == 3) {
            feelingView.setImageResource(R.drawable.love);
        }
        if (myfeeling == 4) {
            feelingView.setImageResource(R.drawable.omg);
        }
        if (myfeeling == 5) {
            feelingView.setImageResource(R.drawable.sick);
        }
    }


    private void setNoteView(String date) {
        String mynote = String.valueOf(realm.where(NoteItem.class).equalTo("date", date).findFirst().getNote());
        noteTv.setText(mynote);
    }

    private void setUpView() {
        fbName = findViewById(R.id.fbName);
        fbName.setText(this.mUser.getDisplayName() + "'s Calendar");
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
                if (currentIdNum == null) {
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
        Intent intent = new Intent(MyCalendar.this, AddNote.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    public void todayBtnClick(View view) {
        CalendarView mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);
    }

    public void logoutBtnPressed(View view) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .typeface(Typeface.SANS_SERIF, Typeface.SANS_SERIF)
                .title("Logout")
                .autoDismiss(true)
                .positiveColorRes(R.color.colorRed)
                .negativeColorRes(R.color.colorPrimaryDark)
                .positiveText("Logout")
                .negativeText("Cancel");

        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
                backToLoginPage();
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

    private void backToLoginPage() {
        Intent intent = new Intent(MyCalendar.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    private Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        Bitmap screenShot = Bitmap.createBitmap(screenView.getWidth(), screenView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenShot);
        screenView.draw(canvas);

        return screenShot;

    }

    public Uri getImageURI(Context inContext, Bitmap bm) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bm, "Title", null);

        return Uri.parse(path);
    }

    private void shareImage(Bitmap bm) {
        Uri bmURI = getImageURI(this.getApplicationContext(), bm);
        shareMenu(bmURI);
    }

    private void shareMenu(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareImage(getScreenShot(this.myCalendarView.getRootView()));
                }
            }
        }
    }

    public void shareBtnPressed(View view) {
        if (requestPermission()) {
            shareImage(getScreenShot(this.myCalendarView.getRootView()));
        }
    }
}
