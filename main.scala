import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

case object StopMsg;
case class Msg(text : String)

class HelloActor extends Actor {
  def receive = {
    case Msg(t) => {
      if (t == "hello") println(s"  $t back to you")
      else println(s"  does '$t' mean hello ?")
    }
    case StopMsg => context.stop(self)
    case _       => println("  huh?")
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  helloActor ! Msg("hello")
  helloActor ! Msg("bonjour")
  helloActor ! Msg("gutentag")

  helloActor ! StopMsg

  system.shutdown()
}
