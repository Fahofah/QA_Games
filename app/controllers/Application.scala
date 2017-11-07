package controllers


import javax.inject.Inject

import models.Game
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

  var gamesList = scala.collection.mutable.Map[String, Game]()
  val basket = scala.collection.mutable.MutableList[Game]()

  val game1 =  Game("1","Overwatch","Party shooter", "+18", 24.99,"overwatch.jpg")
  val game2 =  Game("2","Call of Duty","Role Play", "+18", 44.99,"CallofDuty.jpg")
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

  def calPrice(sum: Double, price: Double): Double ={
    sum + price
  }
  def shoppingCart(gameId: String) = Action {
    basket += gamesList(gameId)
    val theGame = gamesList(gameId)
    Ok(s"${theGame.name} added to basket!\nYour cart now: ${basket.map(_.toString)}\nTotal Price: ${basket.foldLeft(0.0)((sum, game) => calPrice(sum,game.price))}")
  }
}