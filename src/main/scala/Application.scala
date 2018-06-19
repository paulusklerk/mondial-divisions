import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import endpoints._
import models.repository._
import mongodb.Mongo

object Application extends App {
  implicit val sys: ActorSystem = ActorSystem("akka-http-mongodb-microservice")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  val log = sys.log

  val routes =
    new UserEndpoint(new UserRepository(Mongo.userCollection)).userRoutes //you can add more routes using the '~' to concatenate: val routes = route1 ~ route2 ~ route3

  Http().bindAndHandle(routes, "0.0.0.0", 8080).onComplete {
    case Success(b) => log.info(s"application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
  }
}