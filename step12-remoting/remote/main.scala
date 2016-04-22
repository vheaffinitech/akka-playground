import akka.actor._


class RemoteActor extends Actor {
  def receive = {
    case msg: String =>
        println(s"RemoteActor received message '$msg'")
        sender ! "Hello from the RemoteActor"
  }
}


case class Test(text : String)

class testRemoteActor extends Actor {
  def receive = {
    case msg: String =>
        println(s"test received message '$msg'")
    case Test(t) => {
      val selection = context.actorSelection("akka.tcp://RemoteSystem@127.0.0.1:5555/user/RemoteActor")
      println(selection)
    }
  }
}


object RemoteServer extends App  {
  val system = ActorSystem("RemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  println(remoteActor)

  val testActor = system.actorOf(Props[testRemoteActor], name = "testactor")
  testActor ! Test("hello")


  Thread.sleep(100000L)
  system.shutdown()
}
