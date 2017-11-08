package controllers

import javax.inject.Inject

import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.collection.mutable.ArrayBuffer
//import models.{Feed, User}
import play.api.libs.json.Json
import reactivemongo.api.Cursor
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.play.json._
//import models.JsonFormats._
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument


class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  var gamesList = scala.collection.mutable.Map[String, Game]()
  val basket = ArrayBuffer[Game]()

  val game1 = Game("1", "Overwatch", "Party shooter", "+18", 24.99, "overwatch.jpg", "https://www.youtube.com/embed/FqnKB22pOC0")
  val game2 = Game("2", "Call of Duty", "Role Play", "+18", 44.99, "CallofDuty.jpg", "https://www.youtube.com/embed/a9ITIaKzG3A")
  gamesList += (game1._id -> game1)
  gamesList += (game2._id -> game2)

  def index = Action {
    Ok(views.html.index("QA Games"))
  }

  def displayGameInfo(game: String) = Action {
    game match {
      case "game1" => Ok(views.html.gameScreen(game1))
      case "game2" => Ok(views.html.gameScreen(game2))
    }
  }

  def addToBasket(gameId: String) = Action {
    basket += gamesList(gameId)
    val theGame = gamesList(gameId)
    Ok(views.html.addedToBasket(basket, theGame))
  }


  def listPaymentForm = Action { implicit request =>
    Ok(views.html.checkOut(basket, Payment.createForm))
  }

  def getBasketAction = Action { implicit request =>

    val formValidationResult = Payment.createForm.bindFromRequest()
    val action = request.body.asFormUrlEncoded.get("action").head

    if (formValidationResult.hasErrors) {
      if (action == "empty") {
        basket.clear()
        Ok(views.html.index("QA Games"))
      }
      else BadRequest(views.html.checkOut(basket, Payment.createForm, "Please Enter values Correctly"))
    }
    else {
      action match {
        case "pay" => Ok(views.html.checkOut(basket, Payment.createForm, "Thanks for you purchase! You will soon have your games"))
        case "empty" =>
          basket.clear()
          Ok(views.html.index("QA Games"))
      }
    }

  }

  def emptyBasket = Action {
    basket.clear()
    Ok(views.html.index("QA Games"))
  }

  def displayContactUs = Action {
    Ok(views.html.contactUs(Suggestions.suggestionList,Suggestions.createForm))
  }

  def createSuggestionForm = Action {
    Ok(views.html.contactUs(Suggestions.suggestionList,Suggestions.createForm))
  }

  def getSuggestions = Action {

    implicit request =>

      val formValidationResult = Suggestions.createForm.bindFromRequest()

      formValidationResult.fold({ formWithErrors =>
        BadRequest(views.html.contactUs(Suggestions.suggestionList,Suggestions.createForm, "Please Enter values Correctly"))
      }, { suggestion =>
        suggestion
        Suggestions.suggestionList.append(suggestion)
        Ok(views.html.contactUs(Suggestions.suggestionList,Suggestions.createForm, "Thanks for your feedback!"))
      })
  }
}