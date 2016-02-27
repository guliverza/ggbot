package controllers

import org.eclipse.jetty.websocket.client.WebSocketClient
import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok("")
  }

  def q = {

  }

}