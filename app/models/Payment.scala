package models
import play.api.data._
import play.api.data.Forms._

case class Payment(name: String, number: String, expiryMonth: Int, expiryYear: Int, csv: Int) {

}

object Payment {

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "number" -> nonEmptyText(minLength = 16, maxLength = 16),
      "expiryMonth" -> number(min = 1, max = 12),
      "expiryYear" -> number(min= 2018),
      "csv" -> number(min = 100, max = 999)
  )(Payment.apply)(Payment.unapply)
  )




}