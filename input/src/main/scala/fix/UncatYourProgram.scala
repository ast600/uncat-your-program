/*
rule = UncatYourProgram
 */
package fix

import cats.syntax.either._
import mouse.boolean._
import mouse.ignore
import zio.ZIO

import scala.util.Try

object UncatYourProgram {
  def booleanUnfoldTest(): Unit = {
    val x = true.fold(Option(1), Option(2))
    println(Try(x))
  }

  def unignoreTest(): Unit = {
    ignore(1 + 1)
    ZIO.succeed(1).ignore: Unit
  }

  def uneitherTest(): Unit = {
    val l = "no".asLeft
    val r = "yes".asRight
    val x = Either.cond(false, 1, "no").leftMap(_ + "!!1")
    ZIO.succeed(1).asLeft: Unit
    print(s"left: $l")
    print(s"right: $r")
    print(s"x is: $x")
  }
}
