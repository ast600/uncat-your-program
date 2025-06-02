/*
rule = UncatYourProgram
 */
package fix

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
}
