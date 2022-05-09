package data.csv

import data.error.DataError

case class Result[+T](result: Option[T], errors: Seq[DataError]) { self ⇒

  def map[U](f: T ⇒ U): Result[U] = Result(result.map(f), errors)

  def flatMap[U](f: T ⇒ Result[U]): Result[U] = self match {
    case Result(Some(t), _) ⇒ f(t)
    case _ ⇒ Result(errors)
  }

  def flatten[B](implicit ev: T<:<Result[B]): Result[B] = flatMap(identity(_))

}

object Result{

  def empty[A]: Result[A] = Result(None, Nil)

  def pure[A](a: A): Result[A] = Result(Some(a), Nil)

  def apply[A](errors: Seq[DataError]): Result[A] = Result(Option.empty[A], errors)

  def apply[A](error: DataError): Result[A] = apply(error :: Nil)

  def apply[A](a: A): Result[A] = pure(a)

}
