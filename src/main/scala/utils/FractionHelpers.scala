package EffectBox

import chisel3._

class Fraction extends Bundle(){
    val numerator = UInt(8.W)
    val denominator = UInt(8.W)
}

class SignedDoubleFraction extends Bundle() {
    val numerator = SInt(16.W)
    val denominator = UInt(16.W)
//    val negative = Bool()
}

object Fraction {
    def nop: Fraction = {
      val b = Wire(new Fraction)
      b.numerator   := 0.U
      b.denominator := 0.U
      b
    }
  }

class FractionReduce(fractionInput : Double) extends Bundle(){
    var localFraction = fractionInput
    var denom : Int = 1
    var num   : Int = 1

    while (localFraction < scala.math.abs(1)){
        localFraction *= 10
        denom         *= 10
    }

    val gcd : Int = gcd(localFraction.toInt,denom.toInt)

    denom /= gcd
    num = (localFraction.toInt)/gcd

    // This is dumb
    try{
        denom.toByte
        num.toByte
    }
    catch{
        case _: Throwable => println("Fraction could not be represented as 8 bit")
    }

    val numUInt = num.asUInt
    val denomUInt = denom.asUInt

    def gcd(a: Int,b: Int): Int = {
        if(b ==0) a else gcd(b, a%b)
     }
  }
