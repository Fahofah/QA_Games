package models

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class Suggestions (name: String,email: String, message: String) {



}

object Suggestions {
  val suggestionList = ArrayBuffer[Suggestions]()

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "email" -> nonEmptyText.verifying( _ contains "@").verifying(_ contains ".com"),
      "message" -> nonEmptyText
    )(Suggestions.apply)(Suggestions.unapply)
  )
}
