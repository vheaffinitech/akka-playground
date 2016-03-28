import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.Await
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

// playing with timeout

case object StopMsg;
case class Msg(text : String)
case class MsgLong(text :String)

class HelloActor extends Actor {
  def receive = {
    case Msg(t)  => {
      if (t == "hello") sender ! s"  $t back to you"
      else sender ! s"  does '$t' mean hello ?"
    }
    case MsgLong(t) => {
      println(s"Received message $t and waiting 2 seconds before answer")
      Thread.sleep(2000L)
      sender ! s"ok $t received"
    }
    case StopMsg => context.stop(self)
    case _       => sender ! "  huh?"
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  // change timeout from 5 sec to 1 sec to see
  implicit val timeout = Timeout(1 seconds)

  val future = helloActor ? Msg("hello")
  //val result = Await.result(future, timeout.duration).asInstanceOf[String]
  val result = Await.result(future, 1 second).asInstanceOf[String]
  println(result)

  //another way
  val future2 = helloActor ? MsgLong("hello again")
  val result2 = Await.result(future2, 4 second).asInstanceOf[String]
  println(result2)

  helloActor ! StopMsg
  Thread.sleep(5L)
  system.shutdown()
}
