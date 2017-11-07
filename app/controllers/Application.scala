package controllers


import javax.inject.Inject

import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection
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


class Application extends Controller {

  val game1info = ("Game1","Party shooter", "+18", "£24.99")
  val game2info = ("Game2","Role Play", "+18", "£44.99")
  val basket = scala.collection.mutable.MutableList[String]()

  def index = Action {
    Ok(views.html.index("QA Games"))
  }

  def displayGameInfo(game: String) = Action {
    game match {
      case "game1" => Ok(views.html.gameScreen(game1info.toString,"images/overwatch.jpg"))
      case "game2" => Ok(views.html.gameScreen(game2info.toString,"images/CallofDuty.jpg"))
    }
  }

  def shoppingCart() = Action {
    Ok("Added to basket!")
  }
}