package EffectBox

import chisel3._
import chisel3.util.DecoupledIO
import freechips.asyncqueue.{AsyncQueue, AsyncQueueParams}


/**
  * A helper for creating AsyncQueues with depth of 1
  */
object SingleAsyncQueue {
  def apply[T <: Data](t: T, in_clock: Clock, out_clock: Clock, in: DecoupledIO[T], out: DecoupledIO[T]) {
    val queue = Module(new AsyncQueue(t, AsyncQueueParams.singleton()))

    queue.io.enq_clock := in_clock
    queue.io.enq <> in
    queue.io.deq_clock := out_clock
    queue.io.deq <> out
    queue.io.enq_reset := DontCare
    queue.io.deq_reset := DontCare
  }
}
