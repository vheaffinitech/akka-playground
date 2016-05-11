import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.Future
import akka.pattern.ask

case class Req(text : String)

class RequestActor extends Actor {
  def receive = {
    case Req(t)  => {
      //println(s" RequestActor received '$t'")
    }
  }
}

class RequestResponseActor extends Actor {
  def receive = {
    case Req(t)  => {
      //println(s"RequestResponseActor received '$t'")
      sender ! s"RequestResponseActor received '$t'"
    }
  }
}

object Main extends App {
    implicit val system = ActorSystem("HelloSystem")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(1 seconds)

    val requestActor = system.actorOf(Props[RequestActor], name = "RequestActor")
    val requestResponseActor = system.actorOf(Props[RequestResponseActor], name = "RequestResponseActor")

    val route =
        path("req") {
          get {
            parameter("name") { name =>
              requestActor ! Req(name)
              complete(s"requestActor : $name ! ")
            }
          }
        } ~
        path("reqresp") {
          get {
            parameter("name") { name =>
              val future = requestResponseActor ? Req(name)
              val s = Await.result(future, timeout.duration).asInstanceOf[String]
              complete(s"requestResponseActor : $s")
            }
          }
        }

    Http().bindAndHandle(route, "localhost", 8888)
}
