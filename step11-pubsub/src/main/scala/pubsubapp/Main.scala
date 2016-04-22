package pubsubapp

import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster

object Main {
  def main(args: Array[String]): Unit = {
    val systemName = "pusubapp"
    val system1 = ActorSystem(systemName)
    val joinAddress = Cluster(system1).selfAddress
    println("===============")
    println(joinAddress)
    println("===============")

    // First Host
    Cluster(system1).join(joinAddress)
    system1.actorOf(Props[MemberListener], "memberListener")
    val feed = system1.actorOf(Props[FeedActor], "feed")

    Thread.sleep(2000)
    val system2 = ActorSystem(systemName)
    Cluster(system2).join(joinAddress)
    //system2.actorOf(Props[PrintActor], "Printer")
    system2.actorOf(Props[SumActor], "Sum")
    system2.actorOf(Props[AvgActor], "Avg")

    Thread.sleep(2000)
    val system3 = ActorSystem(systemName)
    Cluster(system3).join(joinAddress)
    system3.actorOf(Props[PrintActor], "Printer")
  }
}
