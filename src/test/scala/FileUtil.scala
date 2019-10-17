package EffectBox

import scala.io.Source

object FileUtils {
  def getLines(source: String) = Source.fromFile(source).getLines
}
