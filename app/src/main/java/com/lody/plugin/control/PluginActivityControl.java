package com.lody.plugin.control;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lody.plugin.reflect.Reflect;
import com.lody.plugin.reflect.ReflectException;

/**
 * Created by lody  on 2015/3/26.
 * <p/>
 * 插件的控制器<br>
 * 派发插件事件和控制插件生命周期
 */
public class PluginActivityControl implements PluginActivityCallback {

    Activity proxy;//代理Activity
    Activity plugin;//插件Activity
    Reflect proxyRef;//指向代理Activity的反射工具类
    Reflect pluginRef;//指向插件Activity的反射工具类
    Application app;//分派给插件的Application


    /**
     *
     * @param proxy 代理Activity
     * @param plugin 插件Activity
     */
    public PluginActivityControl(Activity proxy, Activity plugin) {
        this(proxy, plugin,null);

    }

    /**
     *
     * @param proxy 代理Activity
     * @param plugin 插件Activity
     * @param app 分派给插件的Application
     */
    public PluginActivityControl(Activity proxy, Activity plugin,Application app) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.app = app;

        //使反射工具类指向相应的对象
        proxyRef = Reflect.on(proxy);
        pluginRef = Reflect.on(plugin);
    }

    public void dispatchProxyToPlugin() {
        try {
            //TODO:开始伪装插件为实体Activity
            pluginRef.set("mDecor", proxyRef.get("mDecor"));
            pluginRef.set("mTitleColor", proxyRef.get("mTitleColor"));
            pluginRef.set("mWindowManager", proxyRef.get("mWindowManager"));
            pluginRef.set("mWindow", proxy.getWindow());
            pluginRef.set("mManagedDialogs", proxyRef.get("mManagedDialogs"));
            pluginRef.set("mCurrentConfig", proxyRef.get("mCurrentConfig"));
            pluginRef.set("mSearchManager", proxyRef.get("mSearchManager"));
            pluginRef.set("mMenuInflater", proxyRef.get("mMenuInflater"));
            pluginRef.set("mConfigChangeFlags", proxyRef.get("mConfigChangeFlags"));
            pluginRef.set("mIntent", proxyRef.get("mIntent"));
            pluginRef.set("mToken", proxyRef.get("mToken"));
            //TODO:用于改变插件的跳转目标
            Instrumentation instrumentation = proxyRef.get("mInstrumentation");
            pluginRef.set("mInstrumentation", new LPluginInstrument(instrumentation));

            pluginRef.set("mMainThread", proxyRef.get("mMainThread"));
            pluginRef.set("mEmbeddedID", proxyRef.get("mEmbeddedID"));
            //TODO:支持插件自定义Application特征
            pluginRef.set("mApplication",app == null ? proxy.getApplication() : app);
            pluginRef.set("mComponent", proxyRef.get("mComponent"));
            pluginRef.set("mActivityInfo", proxyRef.get("mActivityInfo"));
            pluginRef.set("mAllLoaderManagers", proxyRef.get("mAllLoaderManagers"));
            pluginRef.set("mLoaderManager", proxyRef.get("mLoaderManager"));
            //TODO:支持插件的Fragment
            if (Build.VERSION.SDK_INT >= 13) {
                //在android 3.2 以后，Android引入了Fragment.
                FragmentManager mFragments = proxy.getFragmentManager();
                pluginRef.set("mFragments", mFragments);
                pluginRef.set("mContainer", proxyRef.get("mContainer"));
            }
            //TODO:支持ActionBar
            if (Build.VERSION.SDK_INT >= 12) {
                //在android 3.0 以后，Android引入了ActionBar.
                pluginRef.set("mActionBar", proxyRef.get("mActionBar"));
            }

            pluginRef.set("mUiThread", proxyRef.get("mUiThread"));
            pluginRef.set("mHandler", proxyRef.get("mHandler"));
            pluginRef.set("mInstanceTracker", proxyRef.get("mInstanceTracker"));
            pluginRef.set("mTitle", proxyRef.get("mTitle"));
            pluginRef.set("mResultData", proxyRef.get("mResultData"));
            pluginRef.set("mDefaultKeySsb", proxyRef.get("mDefaultKeySsb"));
            //TODO:改变Window回调目标
            plugin.getWindow().setCallback(plugin);

        } catch (ReflectException e) {
            //Ignore
        }

    }


    /**
     * @return 插件的Activity
     */
    public Activity getPlugin() {
        return plugin;
    }

    /**
     * 设置插件的Activity
     * @param plugin
     */
    public void setPlugin(Activity plugin) {
        this.plugin = plugin;
        proxyRef = Reflect.on(plugin);
    }

    /**
     * 得到代理的Activity
     * @return
     */
    public Activity getProxy() {
        return proxy;
    }

    /**
     * 设置代理的Activity
     * @param proxy
     */
    public void setProxy(Activity proxy) {
        this.proxy = proxy;
        proxyRef = Reflect.on(proxy);
    }

    /**
     * @return 代理Activity的反射工具类
     */
    public Reflect getProxyRef() {
        return proxyRef;
    }

    /**
     *
     * @return 插件Activity的反射工具类
     */
    public Reflect getPluginRef() {
        return pluginRef;
    }


    /**
     * 执行插件的onCreate方法
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * @param saveInstance
     */
    @Override
    public void callOnCreate(Bundle saveInstance) {
        getPluginRef().call("onCreate", saveInstance);
    }

    /**
     * 执行插件的onStart方法
     * @see android.app.Activity#onStart()
     */
    @Override
    public void callOnStart() {
        getPluginRef().call("onStart");
    }

    /**
     * 执行插件的onResume方法
     * @see android.app.Activity#onResume()
     */
    @Override
    public void callOnResume() {
        getPluginRef().call("onResume");
    }

    /**
     * 执行插件的onDestroy方法
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void callOnDestroy() {
        getPluginRef().call("onDestroy");
    }

    /**
     * 执行插件的onStop方法
     * @see android.app.Activity#onStop()
     */
    @Override
    public void callOnStop() {
        getPluginRef().call("onStop");
    }

    /**
     * 执行插件的onRestart方法
     * @see android.app.Activity#onRestart()
     */
    @Override
    public void callOnRestart() {
        getPluginRef().call("onRestart");
    }

    /**
     * 执行插件的onSaveInstanceState方法
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     * @param outState
     */
    @Override
    public void callOnSaveInstanceState(Bundle outState) {
        getPluginRef().call("onSaveInstanceState", outState);
    }

    /**
     * 执行插件的onRestoreInstanceState方法
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     * @param savedInstanceState
     */
    @Override
    public void callOnRestoreInstanceState(Bundle savedInstanceState) {
        getPluginRef().call("onRestoreInstanceState", savedInstanceState);
    }

    /**
     * 执行插件的onStop方法
     * @see android.app.Activity#onStop()
     */
    @Override
    public void callOnPause() {
        getPluginRef().call("onStop");
    }

    /**
     * 执行插件的onBackPressed方法
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void callOnBackPressed() {
        getPluginRef().call("onBackPressed");
    }

    /**
     * 执行插件的onKeyDown方法
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean callOnKeyDown(int keyCode, KeyEvent event) {
        return getPluginRef().call("onKeyDown",keyCode,event).get();
    }

}
