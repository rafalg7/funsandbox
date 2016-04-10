import org.scalatest.{FreeSpec, MustMatchers}

import scalaz.Monoid
import scalaz.syntax.monoid._

class MonoidSpec extends FreeSpec with MustMatchers {

  implicit val intSumMonoid = new Monoid[Int]{
    override def zero: Int = 0
    override def append(f1: Int, f2: => Int): Int = f1 + f2
  }
  implicit val stringSumMonoid = new Monoid[String]{
    override def zero: String = ""
    override def append(f1: String, f2: => String): String = f1 + " " + f2
  }
  implicit def listSumMonoid[T] = new Monoid[List[T]] {
    override def zero: List[T] = Nil
    override def append(f1: List[T], f2: => List[T]): List[T] = f1 ++ f2
  }
  implicit def setSumMonoid[T] = new Monoid[Set[T]] {
    override def zero: Set[T] = Set.empty
    override def append(f1: Set[T], f2: => Set[T]): Set[T] = f1 ++ f2
  }


  def sum[T](t: T*)(implicit monoidT: Monoid[T]): T = {
    t.foldRight(monoidT.zero)((b, a) => b |+| a)
  }

  "sum method should sum correctly" in {
    sum("siala baba mak", "nie wiedziala jak") mustBe "siala baba mak nie wiedziala jak "
    sum(1,2,3,4) mustBe 10
//    sum(List("siala", "baba", "mak"), List("i"), List("nie wiedziala jak")) mustBe "siala baba mak i nie wiedziala jak"
    sum(Set('boom, 'srum), Set('boom, 'kaboom)) mustBe Set('boom, 'srum, 'kaboom)
    sum(List(0, 1), List(1,0,0,1), List(9,9,9)) mustBe List(0, 1, 1, 0, 0, 1, 9, 9, 9)
  }

}
