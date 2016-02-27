import actors.MyExtension
import play.api.libs.concurrent.Akka
import play.api.{Logger, Application, GlobalSettings}
import play.api.Play.current

/**
 * Created by guliver on 2016.02.27..
 */
object Global extends GlobalSettings {
  override def onStart(app: Application) = {
    Logger.info("Starting GG Chat Bot client...")
    MyExtension(Akka.system).chatBot

  }

}