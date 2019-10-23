package EffectBox

import scala.io.Source

object FileUtils {
  def getLines(source: String) = Source.fromFile(source).getLines.toList
  
  def readWrite(from: String, to: String, poke: BigInt => Unit, peek: () => BigInt, step: Int => Unit) {
      import java.io.PrintWriter

      val pw = new PrintWriter(to)
      val lines = FileUtils.getLines(from)
      val sampleRate = lines.head

      pw.write(s"$sampleRate\n")

      for (line <- lines.drop(1)) {
          val n = line.toShort

          poke(n)

          step(1)

          val a = peek()
          pw.write(s"${a.toLong.toHexString}\n")

      }
      pw.close()
  
  }
}
