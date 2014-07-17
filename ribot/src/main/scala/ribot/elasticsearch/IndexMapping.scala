package ribot.elasticsearch

import play.api.libs.json.{Json, JsValue}

trait IndexMapping {

  val mapping: JsValue

  lazy val json = Json.stringify(mapping)

  protected val useDocValues: (String, Json.JsValueWrapper) =
    "fielddata" -> Json.obj("format" -> "doc_values")

  protected val nonAnalysedString = Json.obj(
    "type" -> "string",
    "index" -> "not_analyzed",
    useDocValues
  )

  // see http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-root-object-type.html:
  // this tells elasticsearch that for any new fields seen, if number and date identification fails,
  // always use a non-analysed string
  protected val defaultToNonAnalysedString: (String, Json.JsValueWrapper) =
    "dynamic_templates" -> Json.arr(
      Json.obj(
        "strings_not_analyzed" -> Json.obj(
          "match" -> "*",
          "match_mapping_type" -> "string",
          "mapping" -> nonAnalysedString
        )
      )
    )

  protected val disableAll: (String, Json.JsValueWrapper) = "_all" -> Json.obj("enabled" -> "false")

  protected def parent(parent: String): (String, Json.JsValueWrapper) = "_parent" -> Json.obj("type" -> parent)

  protected val date = Json.obj("type" -> "date", useDocValues)
  protected val long = Json.obj("type" -> "long", useDocValues)
  protected val double = Json.obj("type" -> "double")
  protected val boolean = Json.obj("type" -> "boolean")
  protected val ipAddress = Json.obj("type" -> "ip")


  // see http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-point-type.html
  protected val geo = Json.obj(
    "type" -> "geo_point",
    "fielddata" -> Json.obj(
      "format" -> "compressed",
      "precision" -> "3m"
    )
  )
}

