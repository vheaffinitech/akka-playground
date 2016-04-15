package pubsubapp

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

case class MsgAvg(i:Int)

class AvgActor extends Actor {
  val mediator = DistributedPubSub(context.system).mediator
  val topic = "feed"
  mediator ! Subscribe(topic, self)

  var sum = 0
  var count = 0

  def receive = {
    case MsgInt(i) =>
      //println(s"Sum Received : $i")
      count = count + 1
      sum = sum + i
      val avg = sum / count
      mediator ! Publish(topic, MsgAvg(avg))
  }

}
