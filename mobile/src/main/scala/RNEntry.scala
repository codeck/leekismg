/**
  * Created by kring on 2017/4/11.
  */

import scala.scalajs.js
import js.annotation.{JSImport, JSName, ScalaJSDefined}
import js.Dynamic.{literal => lit}

@js.native
@JSImport("react-native", JSImport.Namespace)
object RN extends js.Object {
  def View :js.Object = js.native
  def Text :js.Object = js.native
}

@js.native
@JSImport("react-native", "AppRegistry")
object AppRegistry extends js.Object {
  def registerComponent(x :js.Any*) :Unit = js.native
}

@js.native
@JSImport("react-native", "NativeModules")
object NativeModules extends js.Object {
  val CoreSMSModule :js.Dynamic = js.native
}

@js.native
@JSImport("react", JSImport.Default)
object React extends js.Object {
  @JSName("createElement")
  def <(x :js.Any*) :js.Object = js.native
}

@js.native
@JSImport("react", "Component")
class Component(val props :js.Dynamic) extends js.Object {
  def setState(nextStateDiff :js.Object) :Unit = js.native
  def setState(nextStateDiffFunc :(js.Dynamic /*previousState*/, js.Dynamic /*currentProps*/) => js.Object /*nextState*/) :Unit = js.native
  def replaceState(nextStateFull :js.Object) :Unit = js.native

  def forceUpdate() :Unit = js.native
  def isMounted() :Boolean = js.native

  def state :js.Dynamic = js.native
  def render() :js.Object = js.native
}

@ScalaJSDefined
class Hello(p :js.Dynamic) extends Component(p) {
  override def render() = {
    React.<(RN.Text, null, "From Hello Component, arg = " + props.arg)
  }
}

object RNEntry extends js.JSApp {

  var somemsg = "..."
  NativeModules.CoreSMSModule.show((msg :String)=>somemsg = msg)

  @ScalaJSDefined
  class EntryComponent (p :js.Dynamic) extends Component(p) {
    override def render() = {
      React.<(RN.View, null,
        React.<(RN.Text, null, "Welcome to React Native!"),
        React.<(js.constructorOf[Hello], lit(arg="<good>")),
        React.<(RN.Text, null, somemsg)
      )
    }
  }

  override def main() :Unit = {
    AppRegistry.registerComponent("HelloWorldApp", () => js.constructorOf[EntryComponent]);
  }
}