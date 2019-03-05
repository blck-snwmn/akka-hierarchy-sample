package hierarchy

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated}

object AuthorActor {
  def props(titles: Vector[String]) = Props(new AuthorActor(titles))

  def name(name: String) = s"Author:$name"

  case object Request

  case object WantStopAll

  def CreateContents(): Vector[EditorSoftActor.WriteLine] = {
    val list = List("aaaaa")
    list.map(EditorSoftActor.WriteLine(_)).toVector
  }
}

class AuthorActor(titles: Vector[String]) extends Actor with ActorLogging {
  require(titles.nonEmpty)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: StopException => Stop
    case _: RestartException => Restart
    case _: ResumeException => Resume
  }

  var editor = context.actorOf(
    EditorSoftActor.props(titles.head),
    EditorSoftActor.name(titles.head)
  )
  context.watch(editor)

  import AuthorActor._

  var alternativeTitles = titles.tail

  override def receive: Receive = {
    case Request =>
      CreateContents().map(editor ! _)
    case WantStopAll => throw new StopError("from AuthorActor")
    case Terminated(_) =>
      if (alternativeTitles.nonEmpty) {
        val newTitle = alternativeTitles.head
        log.info(s" -> $newTitle")
        alternativeTitles = alternativeTitles.tail
        editor = context.actorOf(
          EditorSoftActor.props(newTitle),
          EditorSoftActor.name(newTitle)
        )
        context.watch(editor)
      }
      else {
        log.error("no titles")
        self ! PoisonPill
      }
    //ただ伝播させる。
    case message: AnyRef =>
      editor ! message
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("AuthorActor:PreRestart")
    super.preRestart(reason, message)
  }

  override def postStop(): Unit = {
    log.info("AuthorActor:Close")
  }
}
