package hierarchy

import akka.actor.{Actor, ActorLogging, Props}

object EditorSoftActor {
  def props(title: String) = Props(new EditorSoftActor(title))

  def name(title: String) = s"title:$title"

  case class WriteLine(line: String)

  case object WantStop

  case object WantRestart

  case object WantResume

  case object WantStopAll

}

class EditorSoftActor(title: String) extends Actor with ActorLogging {

  import EditorSoftActor._

  override def receive: Receive = {
    case WriteLine(line) => log.info(s"wrote: $line")
    case WantStopAll => throw new StopError("from EditorActor")
    case WantStop => throw new StopException("from EditorActor")
    case WantRestart => throw new RestartException("from EditorActor")
    case WantResume => throw new ResumeException("from EditorActor")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info("EditorActor:PreRestart")
    super.preRestart(reason, message)
  }

  override def postStop(): Unit = {
    log.info("EditorActor:Close")
  }
}
