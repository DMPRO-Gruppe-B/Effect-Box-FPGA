package signalOperations

import Chisel.Module
import chisel3._

// This module will take in two operands, and the numerator and denominator
// representing the mix ratio of signal 1. (This is done to be able to keep best presicion).
// Important that denominator1 never is 0, and that numerator1<denominator1
class MixSignals extends Module{
  val io = IO(new Bundle() {
    val operand1  = Input(SInt(32.W))
    val operand2 = Input(SInt(32.W))
    val numerator1 = Input(UInt(8.W))
    val denominator1 = Input(UInt(8.W))
    val result = Output(SInt(32.W))
  })


  // Numerator for the second operand:
  val numerator2 = io.denominator1 - io.numerator1

  // Pad both operands
  var pad1 = SInt(42.W)
  pad1 = io.operand1.asSInt()

  var pad2 = SInt(42.W)
  pad2 = io.operand2.asSInt()

  // Multiply by each numerator:
  val mul1  =  pad1 * io.numerator1
  val mul2  = pad2 * numerator2

  // Add the signals together
  val add  = mul1 + mul2

  // Convert denominator to SInt
  var sint_denominator = SInt(16.W)
  sint_denominator= io.denominator1.asSInt()

  // Divide the added signals on the denominator
  val result = add/sint_denominator


  io.result := result

}