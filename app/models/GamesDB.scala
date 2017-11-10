package models

import scala.collection.mutable.ArrayBuffer

case class GamesDB (
                   _id: Int,
                   name: String,
                   category: String,
                   ageRating: String,
                   price: Double,
                   image: String,
                   trailer: String
                   )

case class BasketDB (
                      _id: Int,
                      gameIDs: ArrayBuffer[Int]
                    )
case class SuggestionsDB (
                      _id: Int,
                      name: String,
                      email: String,
                      suggestion: ArrayBuffer[String]
                    )

object JsonFormats {
  import play.api.libs.json.Json

  implicit val gameFormat = Json.format[GamesDB]
  implicit val basketFormat = Json.format[BasketDB]
  implicit val suggestionFormat = Json.format[SuggestionsDB]
}


