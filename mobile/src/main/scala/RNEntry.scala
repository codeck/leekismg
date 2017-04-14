/**
  * Created by kring on 2017/4/11.
  */

import scala.scalajs.js
import js.annotation.{JSExportDescendentClasses, JSExportDescendentObjects, JSImport}
import js.Dynamic.{global => g}
import js.Dynamic.{literal => lit}

@js.native
@JSImport("react-native", "View")
object View extends js.Object

@js.native
@JSImport("react-native", "Text")
object Text extends js.Object

@js.native
@JSImport("react-native", "AppRegistry")
object AppRegistry extends js.Object {
  def registerComponent(x :js.Any*) :Unit = js.native
}

@js.native
@JSImport("react", JSImport.Default)
object React extends js.Object {
  def createClass(x :js.Object) :js.Object = js.native
  def createElement(x :js.Any*) :js.Object = js.native
}

@js.native
@JSImport("react-native", "NativeModules")
object NativeModules extends js.Object {
  val CoreSMSModule :js.Dynamic = js.native
}

object RNEntry extends js.JSApp {

  var somemsg = "..."
  NativeModules.CoreSMSModule.show((msg :String)=>somemsg = msg)

    override def main() :Unit = {

      val entry = React.createClass(lit(render = () =>
        React.createElement(View, null,
          React.createElement(Text, null, "Welcome to React Native!"),
          React.createElement(Text, null, somemsg)
        )
      ));

      AppRegistry.registerComponent("HelloWorldApp", () => entry);

    }
}