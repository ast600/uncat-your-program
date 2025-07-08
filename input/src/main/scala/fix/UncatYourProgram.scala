/*
rule = UncatYourProgram
 */
package fix

import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.validated._
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

  def prettyZIOEitherTest(): Unit = {
    val l = ZIO.succeed(Left("Hey"))
    val r = ZIO.succeed(Right(42))
    val lf = ZIO.succeed("Yay").map(Left.apply)
    val rf = ZIO.succeed(3).map(Right(_))
    print(s"Apply left: $l")
    print(s"Apply right: $r")
    print(s"Map left: $lf")
    print(s"Map right: $rf")
  }

  def unvalidatedTest(): Unit = {
    val inv = 42.validNec[String]
    val v = "hey".invalidNec[Int]
    println(s"Invalid: $inv")
    println(s"Valid: $v")
  }

  def unpureTest(): Unit = {
    val pure = 42.pure[List]
    println(s"Pure: $pure")
  }
}
