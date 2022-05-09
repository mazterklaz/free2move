package data.error

case class Message(message: String,
                   id: String)

object Message{
  def apply(header: String): Message = Message(s"header: $header", "")
}