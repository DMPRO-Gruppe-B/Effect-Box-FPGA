package EffectBox

import scala.io.Source

object TestUtils {
    def toBinaryString[A <: AnyVal](value: A, size: Int = 0): String = {
        val zerosX64: String = //maximum possible number of leading zeros
          "0" * 64
    
        val (valueAsBinaryString, typeSize) =
          value match {
            case valueAlmostTyped: Boolean =>
              (if (valueAlmostTyped) "1" else "0", 1)
            case valueAlmostTyped: Byte =>
              (valueAlmostTyped.toByte.toBinaryString.takeRight(8), 8) //take() fixes hidden upcast to Int in Byte.toBinaryString
            case valueAlmostTyped: Short =>
              (valueAlmostTyped.toShort.toBinaryString.takeRight(16), 16) //take() fixes hidden upcast to Int in Short.toBinaryString
            case valueAlmostTyped: Char =>
              (valueAlmostTyped.toChar.toBinaryString, 16)
            case valueAlmostTyped: Int =>
              (valueAlmostTyped.toInt.toBinaryString, 32)
            case valueAlmostTyped: Long =>
              (valueAlmostTyped.toLong.toBinaryString, 64)
            case _ =>
              throw new IllegalArgumentException(s"toBinaryString not implemented for this type [${value.getClass.getSimpleName}] - only implemented for Boolean, Byte, Short, Char, Int, and Long")
          }
    
        val newSize =
          if (size < 0) //model and fix the behavior of existing toBinaryString function on Byte, Short, Char, Int, and Long, and add for Binary
            valueAsBinaryString.length
          else
            if (size == 0) //zero fill to the bit size of the containing type
              typeSize
            else
              if (valueAsBinaryString.length > size) //possibly override the caller specified custom size value as it is smaller than the resulting valueAsBinaryString itself
                if (valueAsBinaryString.take(valueAsBinaryString.length - size + 1).exists(_ == '0')) //only override if there isn't a zero dropped (which includes protecting the sign by ensuring if all 1s preceded the 0, at least a single one is preserved
                  valueAsBinaryString.length
                else //caller specified custom value
                  size
              else //caller specified custom value
                size
        ( (
              if (newSize > valueAsBinaryString.length)
                zerosX64.take(newSize - valueAsBinaryString.length)
              else
                ""
          )
          + valueAsBinaryString.takeRight(newSize)
        )
      }
}
