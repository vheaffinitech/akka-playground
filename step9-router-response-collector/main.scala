import akka.actor._
import scala.concurrent.Await
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import akka.routing.{ ActorRefRoutee, RoundRobinRouter, Router }


case class Msg(text : String)
case class Response(text : String)

class FirstActor() extends Actor {
  val nextStage = context.actorOf(Props[SecondActor])

  def receive = {
    case Msg(t)  => {
      println(s" $self received '$t' sending to next step")
      Thread.sleep(util.Random.nextInt(10))
      nextStage ! Msg(s"(FirstActor:$t)")
    }
  }
}

class SecondActor extends Actor {
  def receive = {
    case Msg(t)  => {
      println(s" $self received '$t'")
      Thread.sleep(util.Random.nextInt(10))
      context.actorSelection("/user/collector") ! Response(s"(SecondActor:$t)")
    }
  }
}

class CollectorActor extends Actor {
  def receive = {
    case Response(t) => {
      println(s" $self received '$t'")
    }
  }
}


object Main extends App {
  val system = ActorSystem("HelloSystem")

  val collector = system.actorOf(Props[CollectorActor], name = "collector")

  val a1 = system.actorOf(Props[FirstActor], name = "a1")
  val a2 = system.actorOf(Props[FirstActor], name = "a2")
  val a3 = system.actorOf(Props[FirstActor], name = "a3")
  val a4 = system.actorOf(Props[FirstActor], name = "a4")
  val a5 = system.actorOf(Props[FirstActor], name = "a5")
  val routees = Vector(a1,a2,a3,a4,a5)

  val routerProps = Props.empty.withRouter(RoundRobinRouter(routees = routees))
  val router = system.actorOf(routerProps)

  for (i <- 1 to 200) {
    router ! Msg(s"message $i")
  }

  Thread.sleep(15000L)
  system.shutdown()
}
