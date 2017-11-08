package models
import java.util.Date
import play.api.data._
import play.api.data.Forms._

case class Payment(name: String, number: String, expiry: Date, csv: Int) {

}

object Payment {

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "number" -> nonEmptyText(minLength = 16, maxLength = 16),
      "expiry" -> date(pattern = "mm/yy"),
      "csv" -> number(min = 100, max = 999)
  )(Payment.apply)(Payment.unapply)
  )




}