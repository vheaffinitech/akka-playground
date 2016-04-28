import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn

object Main extends App {
    implicit val system = ActorSystem("HelloSystem")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
        path("hello") {
          get {
            parameter("name") { (name) =>
              complete(s"Hello $name !")
            }
          }
        }

    Http().bindAndHandle(route, "localhost", 8888)
}
