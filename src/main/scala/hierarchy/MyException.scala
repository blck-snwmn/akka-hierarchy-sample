package hierarchy

@SerialVersionUID(1L)
class StopError(msg: String) extends Error(msg) with Serializable

@SerialVersionUID(1L)
class StopException(msg: String) extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class ResumeException(msg: String) extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class RestartException(msg: String) extends Exception(msg) with Serializable