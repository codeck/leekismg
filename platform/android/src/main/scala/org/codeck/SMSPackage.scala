package org.codeck

/**
  * Created by kring on 2017/4/14.
  */

import com.facebook.react.ReactPackage
import com.facebook.react.bridge._
import java.util
import java.util.logging.{Level, Logger}

import com.facebook.react.uimanager.ViewManager
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object SMSModel {


  final val acctTableName = "ISMG_DATA"

  // Table definition
  class AccountData(tag: Tag) extends Table[(Int, String, String)](tag, acctTableName) {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def addr = column[String]("ACCOUNT_ID")
    def seed = column[String]("ACCOUNT_SEED")
    def * = (id, addr, seed)
  }

  val accounts = TableQuery[AccountData]
  val tables = List(accounts)
}

class SMSPackage(applicationContext: ReactApplicationContext) extends ReactPackage {

  lazy val db = Database.forURL("jdbc:sqlite:" +
    applicationContext.getDatabasePath("ismg-sandbox.db"), driver = "org.sqldroid.SQLDroidDriver")

  def initDB() = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val existing = db.run(MTable.getTables("%")) //name pattern is required in sqlite
    val f = existing.flatMap( v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = SMSModel.tables.filter( table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    //Logger.getLogger("SLICK").warning("SLICK GOOD")
    Await.result(f, 3 seconds)
  }

  initDB();

  class SMSModule(reactApplicationContext: ReactApplicationContext) extends ReactContextBaseJavaModule(reactApplicationContext) {
    override def getName: String = "CoreSMSModule"
    @ReactMethod
    def show(cb :Callback) :Unit = {
      import scala.concurrent.ExecutionContext.Implicits.global

      val newacct = org.stellar.sdk.KeyPair.random();

      val fid = db.run(
        (SMSModel.accounts.map(acct => (acct.addr, acct.seed)) //.returning(SMSModel.accounts.map(_.id)) //not available in sqlite
         += (newacct.getAccountId, newacct.getSecretSeed.mkString)).andThen(
          SMSModel.accounts.result
        )
      ).map(xs => xs.toString)

      fid.onComplete( ret =>
        cb.invoke(ret.toString)
      )
      Await.result(fid, 3 seconds)
    }
  }

  override def createJSModules(): util.List[Class[_ <: JavaScriptModule]] = util.Collections.emptyList()

  override def createViewManagers(reactContext: ReactApplicationContext): util.List[ViewManager[_,_]] = util.Collections.emptyList()

  override def createNativeModules(reactContext: ReactApplicationContext): util.List[NativeModule] = {
    val modules = new util.ArrayList[NativeModule]
    modules.add(new SMSModule(reactContext))
    modules
  }
}