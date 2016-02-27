package actors

import java.net.URI

import akka.actor.{ActorLogging, Actor}
import org.eclipse.jetty.websocket.client.WebSocketClient
import play.api.Play
import play.api.libs.json.{JsValue, Json}
import play.api.Play.current

/**
 * Created by guliver on 2016.02.27..
 */
class ChatBotActor extends Actor with ActorLogging {

  val uri = new URI(Play.configuration.getString("gg.chat.url") getOrElse "ws://localhost")
  var socket: MyWebSocket = _

  override def preStart() = {
    socket = new MyWebSocket(self)
    val client = new WebSocketClient
    client.start
    client.connect(socket, uri)
  }

  override def receive: Receive = {
    case json: JsValue if (json \ "type").as[String] == "welcome" =>
      val version = json \ "data" \ "protocolVersion"
      log.info(s"Welcome message received, protocol version '$version'")
      socket.send(Json.obj("type" -> "get_channels_list", "data" -> Json.obj("start" -> 0, "count" -> 10)))

    case json: JsValue if (json \ "type").as[String] == "channels_list" =>

    case m =>
      log.info(s"Unknown message '$m'")

  }
}
