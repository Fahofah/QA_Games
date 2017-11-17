package controllers

import play.api.libs.mailer._
import java.io.File
import org.apache.commons.mail.EmailAttachment
import javax.inject.Inject

class MailerService @Inject() (mailerClient: MailerClient) {

  def sendEmail = {
    val email = Email(
      "Test Receipent",
      "fahriulucaycy@gmail.com",
      Seq("fahri.ulucay@qa.com"),
      // adds attachment

      // sends text, HTML or both...
      bodyText = Some("who is who"),
      bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid </p></body></html>""")
    )
    mailerClient.send(email)
  }

}