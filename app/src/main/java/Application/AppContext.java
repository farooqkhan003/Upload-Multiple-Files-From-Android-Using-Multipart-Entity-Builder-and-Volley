package Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by Farooq Khan on 14-Jul-16.
 */
public class AppContext extends Application {
    private static AppContext myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static AppContext getInstance() {
        return myApplication;
    }

    public static Context getContext() {
        return myApplication.getBaseContext();
    }
}
