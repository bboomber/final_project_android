package my.calendar.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import my.calendar.myapplication2.Model.NoteItem;

public class AddNote extends Activity{

    private Realm realm;
    private int feeling = 0;
    private Intent intent;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        date =  intent.getStringExtra("date");
        TextView tv = findViewById(R.id.yournoteTv);
        tv.setText(date);

        final ImageView imselect = findViewById(R.id.imtestim);
        imselect.setImageResource(R.drawable.happy);
        final ImageView imfeelhappy = findViewById(R.id.feelHappy);
        imfeelhappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.happy);
                feeling = 0;
            }
        });
        final ImageView imfeelcry = findViewById(R.id.feelCry);
        imfeelcry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.cry);
                feeling = 1;
            }
        });
        final ImageView imfeelbad = findViewById(R.id.feelbad);
        imfeelbad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.bad);
                feeling = 2;
            }
        });
        final ImageView imfeellove = findViewById(R.id.feelLove);
        imfeellove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.love);
                feeling = 3;
            }
        });
        final ImageView imfeelomg = findViewById(R.id.feelOmg);
        imfeelomg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.omg);
                feeling = 4;
            }
        });
        final ImageView imfeelsick = findViewById(R.id.feelsick);
        imfeelsick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imselect.setImageResource(R.drawable.sick);
                feeling = 5;
            }
        });

    }

    public void saveBtnClick(View view) {
        EditText noteEt = findViewById(R.id.noteEt);
        //now can get all data


        String mynote = String.valueOf(realm.where(NoteItem.class).equalTo("date", date).findFirst());
        if (mynote.equals("null")){

            Log.e("dbError","in-null");
            realm.beginTransaction();
            Number autoIdNum = realm.where(NoteItem.class).max("id");
            int nextId;
            if(autoIdNum == null) {
                nextId = 1;
            } else {
                nextId = autoIdNum.intValue() + 1;
            }
            String mynoteEt = noteEt.getText().toString();
            NoteItem noteItem = new NoteItem();
            noteItem.setNote(mynoteEt);
            noteItem.setDate(date);
            noteItem.setFeeling(feeling);
            noteItem.setId(nextId);
            realm.copyToRealmOrUpdate(noteItem);
            realm.commitTransaction();

        } else{
            Log.e("dbError","in-not-null");
            realm.beginTransaction();
            Number autoIdNum = realm.where(NoteItem.class).max("id");
            int nextId;
            nextId = (int) realm.where(NoteItem.class).equalTo("date", date).findFirst().getId();
            String mynoteEt = noteEt.getText().toString();
            NoteItem noteItem = new NoteItem();
            noteItem.setNote(mynoteEt);
            noteItem.setDate(date);
            noteItem.setFeeling(feeling);
            noteItem.setId(nextId);
            realm.copyToRealmOrUpdate(noteItem);
            realm.commitTransaction();
        }


        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void backBtnClick(View view) {
        EditText et = findViewById(R.id.noteEt);
        TextView younoteTv = findViewById(R.id.yournoteTv);
        younoteTv.setText(et.getText().toString());
    }
}
