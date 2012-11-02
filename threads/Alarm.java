package nachos.threads;

import java.util.PriorityQueue;

import nachos.machine.Machine;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {

  private PriorityQueue<WaitThread> waitQueue = new PriorityQueue<WaitThread>();

  /**
   * Allocate a new Alarm. Set the machine's timer interrupt handler to this
   * alarm's callback.
   * <p>
   * <b>Note</b>: Nachos will not function correctly with more than one alarm.
   */
  public Alarm() {
    Machine.timer().setInterruptHandler(new Runnable() {

      @Override
      public void run() {
        timerInterrupt();
      }
    });
  }

  /**
   * The timer interrupt handler. This is called by the machine's timer
   * periodically (approximately every 500 clock ticks). May cause the current
   * thread to yield, forcing a context switch if there is another thread that
   * should be run.
   */
  public void timerInterrupt() {
    boolean intStatus = Machine.interrupt().disable();
    long now = Machine.timer().getTime();
    WaitThread waiter = waitQueue.peek();
    while (waiter != null && now >= waiter.wakeupTime) {
      waitQueue.poll();
      waiter.thread.ready();
      waiter = waitQueue.peek();
    }
    boolean doYield = ThreadedKernel.scheduler.timerInterrupt();
    Machine.interrupt().restore(intStatus);
    if (doYield) {
      KThread.yield(); // cause the current thread to yield
    }
  }

  /**
   * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
   * in the timer interrupt handler. While the thread is asleep, its status is
   * blocked. The thread must be woken up (placed in the scheduler ready set)
   * during the first timer interrupt where
   * <p>
   * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
   * 
   * @param x
   *          the minimum number of clock ticks to wait.
   * @see nachos.machine.Timer#getTime()
   */
  public void waitUntil(long x) {
    if (x <= 0) {
      return;
    }
    long wakeTime = Machine.timer().getTime() + x;
    boolean intStatus = Machine.interrupt().disable();
    waitQueue.offer(new WaitThread(wakeTime, KThread.currentThread()));
    KThread.sleep();
    Machine.interrupt().restore(intStatus);
  }

  private class WaitThread implements Comparable<WaitThread> {

    public KThread thread;

    public long wakeupTime;

    public WaitThread(long l, KThread t) {
      this.wakeupTime = l;
      this.thread = t;
    }

    @Override
    public int compareTo(WaitThread o) {
      if (this.wakeupTime < o.wakeupTime)
        return -1;
      else if (this.wakeupTime > o.wakeupTime)
        return 1;
      return 0;
    }
  }

}
