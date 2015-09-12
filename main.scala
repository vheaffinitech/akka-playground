import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

case object StopMsg;


class HelloActor extends Actor {
  def receive = {
    case "hello" => println("  hello back at you")
    case StopMsg => context.stop(self)
    case _       => println("  huh?")
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  helloActor ! "hello"
  helloActor ! "bonjour"
  helloActor ! "gutentag"
  helloActor ! StopMsg

  system.shutdown()
}
