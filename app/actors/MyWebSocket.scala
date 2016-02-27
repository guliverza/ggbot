package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.fasterxml.jackson.databind.JsonNode
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.{OnWebSocketClose, OnWebSocketConnect, OnWebSocketMessage, WebSocket}
import play.api.Logger
import play.api.libs.json.{JsValue, Json, JsObject}

/**
 * Basic Echo Client Socket
  */
@WebSocket(maxTextMessageSize = 64 * 1024)
class MyWebSocket(actor: ActorRef) {

  var session: Session = _

  @OnWebSocketClose def onClose(statusCode: Int, reason: String) {
    println(s"Connection closed: $statusCode - $reason")
    Logger.debug(s"Connection closed: $statusCode - $reason")
  }

  @OnWebSocketConnect def onConnect(session: Session) {
    println(s"Got connect: $session")
    Logger.debug(s"Got connect: $session")
    this.session = session
  }

  @OnWebSocketMessage def onMessage(msg: String) {
    println(s"Got msg: $msg")
    Logger.debug(s"Got msg: $msg")
    val json = Json.parse(msg)
    println(s"Parsed msg: $json")
    Logger.debug(s"Parsed msg: $json")
    actor ! json
  }

  def send(msg: String) = {
    println(s"send => $msg")
    Logger.debug(s"send => $msg")
    session.getRemote.sendString(msg)
  }

  def send(json: JsValue) = {
    println(s"send => $json")
    Logger.debug(s"send => $json")
    session.getRemote.sendString(json.toString)
  }


}