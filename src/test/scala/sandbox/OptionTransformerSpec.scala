package sandbox

import org.scalatest.{FreeSpec, MustMatchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class OptionTransformerSpec extends FreeSpec with MustMatchers {

  "monad transformer should be correctly applied" in {
    val fOne = OptionTransformer(Future(Option(1)))
    val fTwo = OptionTransformer(Future(Option(2)))

    val sum = for {
      first <- fOne
      second <- fTwo
    } yield {
      first + second
    }

    Await.result(sum.value, Duration.Inf) mustBe Some(3)
  }

  trait Monad[T[_]] {
    def map[A, B](value: T[A])(f: A => B): T[B]

    def flatMap[A, B](value: T[A])(f: A => T[B]): T[B]

    def pure[A](x: A): T[A]
  }

  implicit val futureMonad = new Monad[Future] {
    def map[A, B](value: Future[A])(f: (A) => B): Future[B] = value.map(f)
    def flatMap[A, B](value: Future[A])(f: (A) => Future[B]): Future[B] = value.flatMap(f)
    def pure[A](x: A): Future[A] = Future(x)
  }

  case class OptionTransformer[T[_], A](value: T[Option[A]])(implicit monad: Monad[T]) {
    def map[B](f: A => B): OptionTransformer[T, B] = {
      val result = monad.map(value)(_.map(f))
      OptionTransformer[T, B](result)
    }

    def flatMap[B](f: A => OptionTransformer[T, B]): OptionTransformer[T, B] = {
      val result = monad.flatMap(value)(a => a.map(b => f(b).value).getOrElse(monad.pure(None)))
      OptionTransformer[T, B](result)
    }
  }
}
