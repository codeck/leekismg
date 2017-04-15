package org.codeck

/**
  * Created by kring on 2017/4/14.
  */

import com.facebook.react.ReactPackage
import com.facebook.react.bridge._
import java.util
import java.util.logging.{Level, Logger}

import android.net.Uri
import android.provider.Telephony.Sms.{Inbox => SmsInbox}
import android.telephony.SmsManager
import com.facebook.react.uimanager.ViewManager
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object SMSModel {

  // Table definition
  case class CheckedSMS()
  case class HandledSMS()
  case class CheckedTx()
  case class HandledTx()

  class InMessage(tag: Tag) extends Table[(Int, Long, String, String)](tag, "CMD_MSG") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def date = column[Long]("DATE")
    def mobile = column[String]("MOBILE")
    def msg = column[String]("BODY")
    def * = (id, date, mobile, msg)
  }

  class AccountData(tag: Tag) extends Table[(Int, String, String, String)](tag, "ACCT_DATA") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def addr = column[String]("ACCOUNT_ID")
    def seed = column[String]("ACCOUNT_SEED")
    def mobi = column[String]("MOBILE") //copy of InMessage.mobile
    def * = (id, addr, seed, mobi)
  }

  class InTransaction(tag: Tag) extends Table[(Int, Int, Int, Int)](tag, "CMD_TXS") {
    def ledger = column[Int]("LEDGER")
    def txn = column[Int]("TXN")
    def opn = column[Int]("OPN")
    def account = column[Int]("ACCT") //TODO: foreign key of AccountData.id
    def * = (ledger, txn, opn, account)
  }

  //TODO:
  // class Backlog(tag: Tag) extends Table[(Int, Int, Int, Int)](tag, "BACKLOG") {
  // }

  val messages = TableQuery[InMessage]
  val accounts = TableQuery[AccountData]
  val txs = TableQuery[InTransaction]
  val tables = List(messages, accounts, txs)

  def createSubscriber(): Unit = {

  }
}

class SMSPackage(val applicationContext: ReactApplicationContext) extends ReactPackage {

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

  def sendMessage() = {
    val sms = SmsManager.getDefault()
    //sms.sendTextMessage()
  }
  def pollLedger() = {

  }
  def pollSMS() = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val SMS_URI_INBOX = "content://sms/inbox"
    import android.database.Cursor
    val projection = Array[String]("_id", "address", "body", "date", "type")
    val where = "date >  ? AND protocol = 0"
    val lastcheck = (System.currentTimeMillis() - (10 hours).toMillis).toString;
    val cur = applicationContext.getContentResolver().query(Uri.parse(SMS_URI_INBOX), projection, where, Array(lastcheck), SmsInbox.DEFAULT_SORT_ORDER);

    val index_Address = cur.getColumnIndex("address");
    val index_Body = cur.getColumnIndex("body");
    val index_Date = cur.getColumnIndex("date");

    while (cur.moveToNext()) {
      val strAddress = cur.getString(index_Address);
      val strbody = cur.getString(index_Body);
      val longDate = cur.getLong(index_Date);
      db.run(
        SMSModel.messages.map(m => (m.mobile, m.date, m.msg)) += (strAddress, longDate, strbody)
      )
    }
  }

  class SMSModule(reactApplicationContext: ReactApplicationContext) extends ReactContextBaseJavaModule(reactApplicationContext) {
    override def getName: String = "CoreSMSModule"
    @ReactMethod
    def show(cb :Callback) :Unit = {
      import scala.concurrent.ExecutionContext.Implicits.global

      pollSMS()
      val newacct = org.stellar.sdk.KeyPair.random();

      val fid = db.run(
        (SMSModel.accounts.map(acct => (acct.addr, acct.seed, acct.mobi)) //.returning(SMSModel.accounts.map(_.id)) //not available in sqlite
         += (newacct.getAccountId, newacct.getSecretSeed.mkString, "10086")).andThen(
          SMSModel.messages.result
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