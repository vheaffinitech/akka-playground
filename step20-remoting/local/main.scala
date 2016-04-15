import akka.actor._

object Local extends App {

  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor")  // the local actor
  localActor ! "START"

  Thread.sleep(10000L)
  system.shutdown()                                                  // start the action

}

class LocalActor extends Actor {

  val remote = context.actorSelection("akka.tcp://RemoteSystem@127.0.0.1:5555/user/RemoteActor")
  var counter = 0

  def receive = {
    case "START" =>
        remote ! "Hello from the LocalActor"
    case msg: String =>
        println(s"LocalActor received message: '$msg'")
        if (counter < 5) {
            sender ! "Hello back to you"
            counter += 1
        }
  }
}
