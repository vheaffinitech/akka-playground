package pubsubapp

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

case class MsgSum(i:Int)

class SumActor extends Actor {
  val mediator = DistributedPubSub(context.system).mediator
  val topic = "feed"
  mediator ! Subscribe(topic, self)

  var s = 0

  def receive = {
    case MsgInt(i) =>
      //println(s"Sum Received : $i")
      s = s + i
      mediator ! Publish(topic, MsgSum(s))
  }

}
