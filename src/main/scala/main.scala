package EffectBox

import java.io.File

object main {
  def main(args: Array[String]): Unit = {
    if (args.length > 1) {
      val top_module = args(0)
      val out_path = args(1)
      val f = new File(out_path)
      chisel3.Driver.dumpFirrtl(args(0) match {
        case "EffectBox"       => chisel3.Driver.elaborate(() => new EffectBox)
        case "FPGATest"        => chisel3.Driver.elaborate(() => new FPGATest)
        case "BRAMTest"        => chisel3.Driver.elaborate(() => new BRAMTest)
        case "SPITest"         => chisel3.Driver.elaborate(() => new SPITest)
      }, Option(f))
      println("Results written to " + out_path)
    } else {
      val s = """
      | Attempting to "run" a chisel program alone is rather meaningless.
      | Pass in as a parameter which top level module you'd like to dump as firrtl and where to dump it:
      |     run EffectBox synthesizer/EffectBox.fir
      |
      | Otherwise, try running the tests, for instance with "test" or "testOnly Examples.MyIncrementTest
      """.stripMargin
      println(s)
    }
  }
}
