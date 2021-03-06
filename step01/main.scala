import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.Await
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

// using Ask & Future

case object StopMsg;
case class Msg(text : String)

class HelloActor extends Actor {
  def receive = {
    case Msg(t)  => {
      if (t == "hello") sender ! s"  $t back to you"
      else sender ! s"  does '$t' mean hello ?"
    }    
    case StopMsg => context.stop(self)
    case _       => sender ! "  huh?"
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  implicit val timeout = Timeout(5 seconds)

  val future = helloActor ? Msg("hello")
  //val result = Await.result(future, timeout.duration).asInstanceOf[String]
  val result = Await.result(future, 1 second).asInstanceOf[String]
  println(result)

  //another way
  val future2: Future[String] = ask(helloActor, Msg("bonjour")).mapTo[String]
  val result2 = Await.result(future2, 1 second)
  println(result2)

  helloActor ! StopMsg
  Thread.sleep(5L)
  system.shutdown()
}
