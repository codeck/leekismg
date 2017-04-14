package org.codeck

import android.app.Application
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.shell.MainReactPackage
import java.util

import android.content.Context
import android.support.multidex.MultiDex

class MainApplication extends Application with ReactApplication {
  final private val mReactNativeHost = new ReactNativeHost(this) {
    override protected def getUseDeveloperSupport = BuildConfig.DEBUG

    override protected def getPackages: util.List[ReactPackage] = util.Arrays.asList[ReactPackage](new MainReactPackage)
  }

  override def getReactNativeHost: ReactNativeHost = mReactNativeHost

  override protected def attachBaseContext(context: Context): Unit = {
    super.attachBaseContext(context)
    MultiDex.install(this)
  }
}