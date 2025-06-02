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
}
