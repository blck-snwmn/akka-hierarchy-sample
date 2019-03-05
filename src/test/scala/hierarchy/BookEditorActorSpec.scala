package hierarchy

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class BookEditorActorSpec extends TestKit(ActorSystem("BookmakerActor"))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  "BookEditorActor" must {
    "send message" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec1")
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "stop all child Actor when send message 'AuthorActor.WantStopAll'" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec2")
      bookEditor ! AuthorActor.WantStopAll
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "stop all child Actor when send message 'EditorActor.WantStopAll'" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec3")
      bookEditor ! EditorSoftActor.WantStopAll
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "create alternative EditorSoftActor when send message 'EditorActor.WantStop'" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec4")
      bookEditor ! EditorSoftActor.WantStop
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "stop AuthorActor when send message 'EditorActor.WantStop' Twice" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec5")
      bookEditor ! EditorSoftActor.WantStop
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
      bookEditor ! EditorSoftActor.WantStop
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "restart EditorSoftActor when send message 'EditorActor.WantRestart'" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec6")
      bookEditor ! EditorSoftActor.WantRestart
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
    "resume EditorSoftActor when send message 'EditorActor.WantResume'" in {
      val bookEditor = system.actorOf(BookEditorActor.props, "spec7")
      bookEditor ! EditorSoftActor.WantResume
      Thread.sleep(1000)
      bookEditor ! BookEditorActor.Publish
      Thread.sleep(1000)
    }
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}
