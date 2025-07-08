package fix

import cats.Applicative

import cats.data.Validated


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

  def prettyZIOEitherTest(): Unit = {
    val l = ZIO.left("Hey")
    val r = ZIO.right(42)
    val lf = ZIO.succeed("Yay").asLeft
    val rf = ZIO.succeed(3).asRight
    print(s"Apply left: $l")
    print(s"Apply right: $r")
    print(s"Map left: $lf")
    print(s"Map right: $rf")
  }

  def unvalidatedTest(): Unit = {
    val inv = Validated.validNec(42)
    val v = Validated.invalidNec("hey")
    println(s"Invalid: $inv")
    println(s"Valid: $v")
  }

  def unpureTest(): Unit = {
    val pure = Applicative[List].pure(42)
    println(s"Pure: $pure")
  }
}
