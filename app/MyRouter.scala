import controllers.Assets
import play.api.mvc._
import play.api.routing._
import play.api.routing.sird.PathExtractor
import play.api.routing.sird.RequestMethodExtractor
import play.api.routing.sird._

/**
 * Created by guliver on 2016.02.27..
 */
class MyRouter extends SimpleRouter {
  def routes = {
    case GET(p"/") => Assets.at(path = "/public", "index.html")
  }
}
