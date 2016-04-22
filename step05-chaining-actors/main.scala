import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.Await
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._


// chaining actors

case object StopMsg;
case class Msg(text : String)

class FirstActor extends Actor {

  val nextStage = context.actorOf(Props[SecondActor], name = "secondactor")

  def receive = {
    case Msg(t)  => {
      // if (t == "hello") sender ! s"  $t back to you"
      // else sender ! s"  does '$t' mean hello ?"
      println(s" FirstActor received '$t' sending to next step")
      Thread.sleep(util.Random.nextInt(10))
      nextStage ! Msg(s"(FirstActor:$t)")
    }
    case StopMsg => context.stop(self)
    case _       => sender ! "  huh?"
  }
}

class SecondActor extends Actor {
  def receive = {
    case Msg(t)  => {
      // if (t == "hello") sender ! s"  $t back to you"
      // else sender ! s"  does '$t' mean hello ?"
      println(s" SecondActor received '$t'")
      Thread.sleep(util.Random.nextInt(10))
      println(s"     SecondActor processed '$t'")
    }
    case StopMsg => context.stop(self)
    case _       => sender ! "  huh?"
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  val firstActor = system.actorOf(Props[FirstActor], name = "firstactor")

  implicit val timeout = Timeout(1 seconds)

  for (i <- 1 to 200) {
    firstActor ! Msg(s"message $i")
  }

  Thread.sleep(15000L)
  firstActor ! StopMsg
  Thread.sleep(50L)
  system.shutdown()
}
