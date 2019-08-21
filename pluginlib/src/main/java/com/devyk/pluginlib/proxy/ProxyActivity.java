package com.devyk.pluginlib.proxy;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devyk.pluginlib.Constants;
import com.devyk.pluginlib.PluginManager;
import com.devyk.pluginlib.base.IActivity;

import java.lang.reflect.Constructor;

/**
 * <pre>
 *     author  : devyk on 2019-08-20 22:18
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ProxyActivity 代理插件加载 Activity
 * </pre>
 */
public class ProxyActivity extends AppCompatActivity {

    /**
     * 需要加载插件的全类名
     */
    protected String activityClassName ;

    private String TAG = this.getClass().getSimpleName();
    private IActivity iActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClassName = getLoadClassName();

        //拿到加载插件的的全类名 通过反射实例化
        try {
            Class<?> pluginClassName = getClassLoader().loadClass(activityClassName);
            //拿到构造函数
            Constructor<?> constructor = pluginClassName.getConstructor(new Class[]{});
            //实例化 拿到插件 UI
            Object pluginObj = constructor.newInstance(new Object[]{});
            if (pluginObj != null) {
                iActivity = (IActivity) pluginObj;
                iActivity.onActivityCreated(this,savedInstanceState);
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

    }


    private String getLoadClassName(){
        return getIntent().getStringExtra(Constants.ACTIVITY_CLASS_NAME);
    }

    /**
     * 这里的 startActivity 是插件调用的
     */
    @Override
    public void startActivity(Intent intent) {
        String className = getLoadClassName();
        Intent proxyIntent = new Intent(this,ProxyActivity.class);
        proxyIntent.putExtra(Constants.ACTIVITY_CLASS_NAME,className);
        super.startActivity(proxyIntent);
    }

    /**
     * 获得插件中的 ClassLoader
     * @return
     */
    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getPluginClassLoader();
    }

    /**
     * 获取插件中的资源
     * @return
     */
    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getPluginResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        iActivity.onActivityStarted(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        iActivity.onActivityResumed(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        iActivity.onActivityPaused(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iActivity.onActivityDestroyed(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iActivity.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        iActivity.onActivitySaveInstanceState(this,outState);
    }
}