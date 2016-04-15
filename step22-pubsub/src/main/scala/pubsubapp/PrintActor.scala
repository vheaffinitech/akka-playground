package pubsubapp

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

class PrintActor extends Actor {
  val mediator = DistributedPubSub(context.system).mediator
  val topic = "feed"
  mediator ! Subscribe(topic, self)

  def receive = {
    case MsgInt(i) =>
      println(s"Received : $i")
    case MsgSum(i) =>
      println(s"Current total : $i")
    case MsgAvg(i) =>
      println(s"Current average : $i")
  }

}
