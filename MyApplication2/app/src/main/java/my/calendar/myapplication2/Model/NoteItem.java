package my.calendar.myapplication2.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class NoteItem extends RealmObject {

    @PrimaryKey
    private long id;
    private String note;
    private String date;
    private int feeling;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}
