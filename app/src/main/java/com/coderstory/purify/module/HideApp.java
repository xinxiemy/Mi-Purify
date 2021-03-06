package com.coderstory.purify.module;

import android.content.ComponentName;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HideApp extends XposedHelper implements IModule {


    /**
     * mSkippedItems.add(new ComponentName("com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsActivity"));
     *
     * @param loadPackageParam
     */
    @Override
    @SuppressWarnings("unchecked")
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final String value = prefs.getString("Hide_App_List", "");
        if (!value.equals("")) {
            final List<String> hideAppList = Arrays.asList(value.split(":"));
            if (loadPackageParam.packageName.equals("com.miui.home")) {
                XposedBridge.log("load config" + value);
                findAndHookMethod("com.miui.home.launcher.AppFilter", loadPackageParam.classLoader, "newInstance", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        HashSet<ComponentName> mSkippedItems = (HashSet<ComponentName>) XposedHelpers.getStaticObjectField(findClass("com.miui.home.launcher.AppFilter", loadPackageParam.classLoader), "mSkippedItems");
                        for (int i = 1; i < hideAppList.size(); i++) {
                            mSkippedItems.add(new ComponentName(hideAppList.get(i).split("&")[0], hideAppList.get(i).split("&")[1]));
                        }
                    }
                });
            }
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }
}
