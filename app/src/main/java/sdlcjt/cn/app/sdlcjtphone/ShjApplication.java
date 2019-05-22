package sdlcjt.cn.app.sdlcjtphone;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import sdlcjt.cn.app.sdlcjtphone.utils.FileCenterTab;
import sdlcjt.cn.app.sdlcjtphone.utils.ULogger;

/**
 * Created by tab on 2018/3/15.
 */
public class ShjApplication extends Application {
    public static ShjApplication app;
    public static Object object;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        init();
    }

    private void init() {

        initOkGo();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 获取Gson实例
     *
     * @return
     */
    public static Gson createGson() {
        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //全局的连接超时时间
        builder.connectTimeout(50000, TimeUnit.MILLISECONDS);
        //全局的读取超时时间
        builder.readTimeout(50000, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(50000, TimeUnit.MILLISECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }

        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        OkGo.getInstance().setOkHttpClient(builder.build()).setRetryCount(1);
    }

    /**
     * 获取包信息
     *
     * @param context context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取VersionName
     *
     * @param context context
     * @return String
     */
    public static String getVersionName(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null && packInfo.versionName != null) {
            return packInfo.versionName;
        }
        return "";
    }

    /**
     * 获取VersionCode
     *
     * @param context context
     * @return int
     */
    public static int getVersionCode(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null && packInfo.versionCode > 0) {
            return packInfo.versionCode;
        }
        return 0;
    }


    @SuppressLint("SimpleDateFormat")
    public void WriteLog(Context context, String log) {
        StringBuffer sb = new StringBuffer();
        String logPath = FileCenterTab.getInstence().getDiskCacheDir(context) + "errorLog.txt";
        File f = new File(logPath);
        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            sb.append("\r\n\r\n\r\n\r\ncrash==================================================\r\n");

            sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date()) + ":\r\n\r\n");
            sb.append(log.replace("\n", "\r\n"));
            sb.append("\r\n\r\n");

            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(logPath, true);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            ULogger.e(e);
        }
    }
}
