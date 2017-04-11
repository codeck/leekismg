package org.codeck

import android.app.Activity
import android.os.Bundle
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage

class MainActivity extends Activity with DefaultHardwareBackBtnHandler {
  private var mReactRootView :Option[ReactRootView] = None
  private var mReactInstanceManager :Option[ReactInstanceManager] = None

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    mReactRootView = Option(new ReactRootView(this))
    mReactInstanceManager = Option(ReactInstanceManager.builder
      .setApplication(getApplication)
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage)
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED).build)
    
    for (view <- mReactRootView;
         mgr <- mReactInstanceManager
    ) {
      view.startReactApplication(mgr, "HelloWorldApp", null)
      setContentView(view)
    }

//    import android.content.Intent
//    import android.net.Uri
//    import android.os.Build
//    import android.provider.Settings
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) if (!Settings.canDrawOverlays(this)) {
//      val intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName))
//      startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
//    }
  }

  override def invokeDefaultOnBackPressed(): Unit = {
    super.onBackPressed()
  }

  override protected def onPause(): Unit = {
    super.onPause
    mReactInstanceManager.map(_.onHostPause(this))
  }

  override protected def onResume(): Unit = {
    super.onResume
    mReactInstanceManager.map(_.onHostResume(this, this))
  }

  override protected def onDestroy(): Unit = {
    super.onDestroy
    mReactInstanceManager.map(_.onHostDestroy)
  }

  override def onBackPressed(): Unit = {
    if (mReactInstanceManager != None)
      mReactInstanceManager.get.onBackPressed
    else super.onBackPressed
  }

  import android.view.KeyEvent

  override def onKeyUp(keyCode: Int, event: KeyEvent): Boolean = {
    if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != None) {
      mReactInstanceManager.get.showDevOptionsDialog
      return true
    }
    super.onKeyUp(keyCode, event)
  }

  import android.content.Intent
  import android.os.Build
  import android.provider.Settings

  override protected def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
//    if (requestCode == OVERLAY_PERMISSION_REQ_CODE) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) if (!Settings.canDrawOverlays(this)) {
//      // SYSTEM_ALERT_WINDOW permission not granted...
//    }
  }
}