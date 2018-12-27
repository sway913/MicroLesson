package east.orientation.microlesson.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import me.yokeyword.fragmentation.BuildConfig;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * @author ljq
 * @date 2018/11/27
 */

public class MicroApp extends Application {
    private static MicroApp instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

    }

    public static synchronized MicroApp getInstance() {
        return instance;
    }
}
