package hierarchy

import akka.actor.SupervisorStrategy.{Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, AllForOneStrategy, Props, SupervisorStrategy, Terminated}

object BookEditorActor {
  def props = Props(new BookEditorActor())

  case object Publish

}

class BookEditorActor extends Actor with ActorLogging {

  override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
    case _: StopError => Stop
  }

  var authors: Vector[ActorRef] = Map(
    "author1" -> Vector(
      "title1-1",
      "title1-2",
    ),
    "author2" -> Vector(
      "title2-1",
      "title2-1",
    ),
    "author3" -> Vector(
      "title3-1",
      "title3-2",
    ),
    "author4" -> Vector(
      "title4-1",
      "title4-2",
    ),
  ).map {
    authorTitlesMap =>
      val author = context.actorOf(
        AuthorActor.props(authorTitlesMap._2),
        AuthorActor.name(authorTitlesMap._1)
      )
      context.watch(author)
      author
  }.toVector

  import BookEditorActor._

  override def receive: Receive = {

    case Publish =>
      authors.map(_ ! AuthorActor.Request)
    case Terminated(author) =>
      authors = authors.filterNot(_ == author)
      if (authors.isEmpty) {
        log.info("author empty")
        context.system.terminate()
      }
    //ただ伝播させる。
    case message: AnyRef =>
      authors.head ! message
  }

  override def postStop(): Unit = {
    log.info("BookmakerActor:Close")
  }
}
