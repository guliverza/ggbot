package actors

/**
 * Created by guliver on 2016.02.27..
 */
import akka.actor._
import play.api.Play.current
import play.api.libs.concurrent.Akka


class MyExtension extends Extension {

  val chatBot = Akka.system.actorOf(Props[ChatBotActor])

}

object MyExtension
  extends ExtensionId[MyExtension]
  with ExtensionIdProvider {

  override def lookup = MyExtension

  override def createExtension(system: ExtendedActorSystem) = new MyExtension
}
