package org.codeck

/**
  * Created by kring on 2017/4/14.
  */

import com.facebook.react.ReactPackage
import com.facebook.react.bridge._
import java.util

import com.facebook.react.uimanager.ViewManager

class SMSPackage extends ReactPackage {

  class SMSModule(reactApplicationContext: ReactApplicationContext) extends ReactContextBaseJavaModule(reactApplicationContext) {
    override def getName: String = "CoreSMSModule"
    @ReactMethod
    def show(cb :Callback) :Unit = cb.invoke(org.stellar.sdk.KeyPair.random().getAccountId)
  }

  override def createJSModules(): util.List[Class[_ <: JavaScriptModule]] = util.Collections.emptyList()

  override def createViewManagers(reactContext: ReactApplicationContext): util.List[ViewManager[_,_]] = util.Collections.emptyList()

  override def createNativeModules(reactContext: ReactApplicationContext): util.List[NativeModule] = {
    val modules = new util.ArrayList[NativeModule]
    modules.add(new SMSModule(reactContext))
    modules
  }
}