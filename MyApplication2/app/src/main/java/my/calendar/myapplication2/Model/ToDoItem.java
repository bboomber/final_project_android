package my.calendar.myapplication2.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by calam on 11/28/2017.
 */

public class ToDoItem extends RealmObject {
    @PrimaryKey
    private long id;
    private String text;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
