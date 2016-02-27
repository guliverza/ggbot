import play.api.libs.json.{JsString, JsObject, Json}

object Main {
  def main(args: Array[String]): Unit = {
    val json = Json.obj("type" -> "a", "num" -> 2)
  }
}
