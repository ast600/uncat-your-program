package fix




import zio.ZIO

import scala.util.Try

object UncatYourProgram {
  def booleanUnfoldTest(): Unit = {
    val x = (if (true) Option(1) else Option(2))
    println(Try(x))
  }

  def unignoreTest(): Unit = {
    (1 + 1): Unit
    ZIO.succeed(1).ignore: Unit
  }

  def uneitherTest(): Unit = {
    val l = Left("no")
    val r = Right("yes")
    val x = Either.cond(false, 1, "no").left.map(_ + "!!1")
    ZIO.succeed(1).asLeft: Unit
    print(s"left: $l")
    print(s"right: $r")
    print(s"x is: $x")
  }
}
