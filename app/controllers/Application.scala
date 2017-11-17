package controllers

import javax.inject.Inject

import akka.stream.impl.io.OutputStreamSourceStage
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.collection.mutable.ArrayBuffer
import models.GamesDB
import models.BasketDB
import play.api.libs.json.Json
import reactivemongo.api.Cursor

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.play.json._
import models.JsonFormats._

import play.api.libs.mailer.MailerClient
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument


class Application @Inject() (val messagesApi: MessagesApi)  (val mailerClient: MailerClient)(
  val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController with ReactiveMongoComponents with I18nSupport {

  var gamesList = ArrayBuffer[GamesDB]()
  var basket = ArrayBuffer[BasketDB]()


  def gamesCollection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("play"))

  def basketCollection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("basket"))

  def suggestionsCollection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("suggestions"))


  def index = Action.async { implicit request =>
    val cursor: Future[Cursor[GamesDB]] = gamesCollection.map {
      _.find(Json.obj()).cursor[GamesDB]
    }
    val futureGameList: Future[ArrayBuffer[GamesDB]] = cursor.flatMap(_.collect[ArrayBuffer]())
    futureGameList.map { games =>
      gamesList = games
      if (request.session.get("user").isEmpty) {
        Ok(views.html.index("Hello First Comer", games)).withSession("user" -> randomUserId)

      } else {
        Ok(views.html.index("Welcome Again", games))
      }
    }
  }

  def randomUserId: String = {
    val id = scala.util.Random
    id.nextInt().toString
  }

  def displayGameInfo(game: String) = Action {
    Ok(views.html.gameScreen(gamesList.filter(_._id == game.toInt).head))
  }


  def addToBasket(gameId: String) = Action.async { implicit request =>
    val currentUserID = request.session.get("user").head.toInt
    val selector = BSONDocument("_id" -> currentUserID)
    val newItem = Json.obj(
      "$push" -> Json.obj(
        "gameIDs" -> gameId.toInt
      )
    )
    val futureUpdate = basketCollection.map(_.findAndUpdate(selector, newItem, upsert = true))
    futureUpdate.map { result =>

      Ok(views.html.addedToBasket(gamesList.filter(_._id == gameId.toInt).head))
    }
  }


  def listPaymentForm = Action.async { implicit request =>
    val currentUserID = request.session.get("user").head.toInt
    val cursor: Future[Cursor[BasketDB]] = basketCollection.map {
      _.find(Json.obj("_id" -> currentUserID)).cursor[BasketDB]
    }
    val futureBasketList: Future[ArrayBuffer[BasketDB]] = cursor.flatMap(_.collect[ArrayBuffer]())
    futureBasketList.map { userBasket =>
      basket = userBasket
      Ok(views.html.checkOut(gamesList, basket, Payment.createForm))
    }
  }

  //
  def getBasketAction = Action { implicit request =>

    val formValidationResult = Payment.createForm.bindFromRequest()
    val action = request.body.asFormUrlEncoded.get("action").head

    if (formValidationResult.hasErrors) {
      if (action == "empty") {
        val currentUserID = request.session.get("user").head.toInt
        val selector = BSONDocument("_id" -> currentUserID)

        val futureDelete = basketCollection.map(_.remove(selector))
        futureDelete
        Ok(views.html.index("Welcome Again", gamesList))
      }
      else BadRequest(views.html.checkOut(gamesList, basket, Payment.createForm, "Please Enter values Correctly"))
    }
    else {
      action match {
        case "pay" => Ok(views.html.checkOut(gamesList, basket, Payment.createForm, "Thanks for you purchase! You will soon have your games"))
        case "empty" =>
          val currentUserID = request.session.get("user").head.toInt
          val selector = BSONDocument("_id" -> currentUserID)

          val futureDelete = basketCollection.map(_.remove(selector))
          futureDelete
          Ok(views.html.index("Welcome Again", gamesList))
      }
    }

  }

  def displayContactUs = Action {
    Ok(views.html.contactUs(Suggestions.createForm))
  }

  def createSuggestionForm = Action {
    Ok(views.html.contactUs( Suggestions.createForm))
  }

  def getSuggestions = Action {

    implicit request =>

      val mail = new MailerService(mailerClient)

      val formValidationResult = Suggestions.createForm.bindFromRequest()

      formValidationResult.fold({ formWithErrors =>
        BadRequest(views.html.contactUs(Suggestions.createForm, "Please Enter values Correctly"))
      }, { suggestion =>
        suggestion

        mail.sendEmail(suggestion.name, suggestion.email, suggestion.message)

        Ok(views.html.contactUs( Suggestions.createForm, "Thanks for your feedback!"))
      })
  }

  def addSuggestiontoDB = Action.async { implicit request: Request[AnyContent] =>
    val currentUserID = request.session.get("user").head.toInt
    val selector = BSONDocument("_id" -> currentUserID)
    val newItem = Json.obj(
      "$set" -> Json.obj(
        "name" -> Suggestions.suggestionList.head.name,
        "email" -> Suggestions.suggestionList.head.email
      ),
      "$push" -> Json.obj(
        "suggestion" -> Suggestions.suggestionList.head.message
      )
    )
    val futureUpdate = basketCollection.map(_.findAndUpdate(selector, newItem, upsert = true))
    futureUpdate.map { result =>
      Ok
    }
  }
}