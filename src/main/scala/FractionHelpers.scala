package EffectBox

import chisel3._
import scala.collection.BitSet
import chisel3.core.Wire


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

object Multiply{
    def apply(numerator: UInt,denominator: UInt, number: SInt) = {
        val m = Module(new Multiply)
        m.io.numerator := numerator
        m.io.denominator := denominator
        m.io.number    := number
        m.io.out

    }
}

class Multiply extends Module{
    val io = IO(new Bundle {
        val numerator   = Input(UInt(8.W))
        val denominator = Input(UInt(8.W))
        val number      = Input(SInt(32.W))

        val out         = Output(SInt(32.W))
      })
    
    io.out := (io.number.asSInt*io.numerator.asSInt)/io.denominator.asSInt
}

object OneMinusMultiply{
    def apply(numerator: UInt,denominator: UInt, number: SInt) = {
        val m = Module(new OneMinusMultiply)
        m.io.numerator := numerator
        m.io.denominator := denominator
        m.io.number    := number
        m.io.out

    }
}

class OneMinusMultiply extends Module{
    val io = IO(new Bundle {
        val numerator   = Input(UInt(8.W))
        val denominator = Input(UInt(8.W))
        val number      = Input(SInt(32.W))

        val out         = Output(SInt(32.W))
      })

    io.out := (io.number.asSInt*(io.denominator-io.numerator).asSInt)/io.denominator.asSInt
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
