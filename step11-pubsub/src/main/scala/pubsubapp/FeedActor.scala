package pubsubapp

import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

case object Tick
case class MsgInt(i:Int)

class FeedActor extends Actor {
  import context.dispatcher
  val mediator = DistributedPubSub(context.system).mediator
  val topic = "feed"

  def scheduler = context.system.scheduler
  def rnd = ThreadLocalRandom.current

  override def preStart(): Unit =
    scheduler.scheduleOnce(2.seconds, self, Tick)

  // override postRestart so we don't call preStart and schedule a new Tick
  override def postRestart(reason: Throwable): Unit = ()

  def receive = {
    case Tick =>
      val i = rnd.nextInt(0, 1000)
      scheduler.scheduleOnce(1.seconds, self, Tick)
      //println(s"Feeding : $i")
      mediator ! Publish(topic, MsgInt(i))
  }
}
