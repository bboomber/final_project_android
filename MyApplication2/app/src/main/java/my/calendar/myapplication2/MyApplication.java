package my.calendar.myapplication2;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by calam on 11/28/2017.
 */

public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
