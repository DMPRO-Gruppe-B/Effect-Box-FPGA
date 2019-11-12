package EffectBox

import chisel3._
import scala.collection.BitSet
import chisel3.core.Wire

object Multiply{
    def apply(numerator: UInt,denominator: UInt, number: SInt) : SInt = {
        val m = Module(new Multiply)
        m.io.numerator := numerator
        m.io.denominator := denominator
        m.io.number    := number
        m.io.out
    }
}

class Multiply extends Module {
    val io = IO(new Bundle {
        val numerator   = Input(UInt(8.W))
        val denominator = Input(UInt(8.W))
        val number      = Input(SInt(32.W))

        val out         = Output(SInt(32.W))
      })


    var pad = SInt(40.W)
    pad = io.number.asSInt()
    val result = (pad*io.numerator.asSInt)/io.denominator.asSInt

    // Following running with DelaySpec shows that there are indeed 40 bits in the intermediate result
    //print("\n Width of intermidiate result: " + result.getWidth + " bits\n")

    io.out := result.asSInt()
}

object OneMinusMultiply{
    def apply(numerator: UInt,denominator: UInt, number: SInt) : SInt = {
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
    var pad = SInt(42.W)
    pad = io.number.asSInt()
    val result = (pad*(io.denominator-io.numerator).asSInt)/io.denominator.asSInt

    // Following running with DelaySpec shows that there are indeed 40 bits in the intermediate result
    //print("\n Width of intermidiate result: " + result.getWidth + " bits\n")

    io.out := result.asSInt()
}

object InverseMultiply{
    def apply(numerator: UInt,denominator: UInt, number: SInt, numberInverse: SInt) : SInt = {
        val m = Module(new InverseMultiply)
        m.io.numerator := numerator
        m.io.denominator := denominator
        m.io.number    := number
        m.io.numberInverse := numberInverse

        m.io.out
    }
}

class InverseMultiply extends Module {
    val io = IO(new Bundle {
        val numerator     = Input(UInt(8.W))
        val denominator   = Input(UInt(8.W))
        val number        = Input(SInt(32.W))
        val numberInverse = Input(SInt(32.W))

        val out         = Output(SInt(32.W))
      })


    val mul = Module(new Multiply)
    val invMul = Module(new OneMinusMultiply)

    mul.io.numerator := io.numerator
    mul.io.denominator := io.denominator
    mul.io.number := io.number

    invMul.io.numerator := io.numerator
    invMul.io.denominator := io.denominator
    invMul.io.number := io.numberInverse

    // Following running with DelaySpec shows that there are indeed 40 bits in the intermediate result
    //print("\n Width of intermidiate result: " + result.getWidth + " bits\n")

    io.out := mul.io.out + invMul.io.out
}

object WeightedSum{
    def apply(values: Vec[SInt], numberOfElements : Int, fraction : Fraction) : SInt = {

        val sum = Wire(SInt(32.W))
        sum := 0.S
        for(value <- values){
            val mul = Module(new Multiply).io
            mul.numerator := fraction.numerator
            mul.denominator := fraction.denominator
            mul.number := value

            sum := sum + mul.out
        }
        sum
    }
}

object RisingEdge{
    def apply(signal: Bool) : Bool = {
        val m = Module(new RisingEdge)
        m.io.signal := signal

        m.io.out
    }
}

class RisingEdge extends Module{
    val io = IO(new Bundle {
        val signal      = Input(Bool())

        val out         = Output(Bool())
      })
    io.out := io.signal && !RegNext(io.signal)
}

object FallingEdge{
    def apply(signal: Bool) : Bool = {
        val m = Module(new FallingEdge)
        m.io.signal := signal

        m.io.out
    }
}

class FallingEdge extends Module{
    val io = IO(new Bundle {
        val signal      = Input(Bool())

        val out         = Output(Bool())
      })
    io.out := !io.signal && RegNext(io.signal)
}