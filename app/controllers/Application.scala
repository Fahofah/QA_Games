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

  def index = Action {
    Ok(views.html.index("QA Games"))
  }

}