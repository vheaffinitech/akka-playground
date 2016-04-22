import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._

case object DoWork

class Supervisor extends Actor {

  val child = context.actorOf(Props[Child], "child1")

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  def receive = {
    case DoWork => {
      println("Doing work")
      child ! 42
      child ! "get"

      //child ! new ArithmeticException
      //child ! "get"

      //child ! new NullPointerException
      //child ! "get"

      //child ! new IllegalArgumentException
      //child ! "get"
    }
    case x:Int => println(s"State = $x")
  }



}

class Child extends Actor {
  var state = 0
  def receive = {
    case ex: Exception => throw ex
    case x: Int        => state = x
    case "get"         => sender() ! state
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  val supervisor = system.actorOf(Props[Supervisor], "supervisor")

  supervisor ! DoWork

  // Sleep 1 sec to avoid deadletters
  Thread.sleep(1000L)
  system.shutdown()
}
