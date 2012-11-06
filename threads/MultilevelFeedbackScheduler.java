package nachos.threads;
import nachos.threads.*;
import java.util.*;
import nachos.machine.Lib;
import nachos.machine.Machine;

/**
 * Implements MLFQ CPU scheduling for Nachos threads.
 */
public class MultilevelFeedbackScheduler extends Scheduler {

  MultilevelFeedbackQueue myQueue; 
  //The three prioity levels of the scheduler.
  //LinkedList<KThread> RQ0;
  //LinkedList<KThread> RQ1;
  //LinkedList<KThread> RQ2;
  /**
   * Initialize a new thread queue for managing access to the CPU using MLFQ.
   */
  @Override
  public ThreadQueue newThreadQueue(boolean transferPriority) {
    // you can ignore the transferPriority parameter for MLFQ
    myQueue = new MultilevelFeedbackQueue();
    return myQueue;
  }

  /**
   * Called every scheduling time slice. Decides whether the current thread
   * needs to yield (and performs any other required processing).
   * 
   * @return True if the current thread should yield
   */
  @Override
  public boolean timerInterrupt() {
    Lib.assertTrue(Machine.interrupt().disabled());

    //return myQueue.timerInterrupt();
    //AGE
    //Increment all the threads that aren't being run to keep track of aging. And change the queue levels of things that are really old.
    for(int x = 0; x < myQueue.getQueue(1).size(); x++)
    {
      KThread thread = myQueue.getQueue(1).get(x);
      if(((ThreadData)thread.schedulingState).getWaitingTime() >= 31)
      {
        myQueue.getQueue(0).remove(thread);
        myQueue.getQueue(1).add(thread);
        ((ThreadData)thread.schedulingState).setCurrentQueue(0);
        ((ThreadData)thread.schedulingState).setWaitingTime((Integer)0);
        //System.out.println(thread.getName() + " has been promoted from RQ1.");
      }
      else
      {
        int newWaitingTime = ((ThreadData)thread.schedulingState).getWaitingTime() + 1;
        ((ThreadData)thread.schedulingState).setWaitingTime(newWaitingTime);
        //System.out.println("Incremented waiting time of " + thread.getName() + " to " + newWaitingTime);
      }
    }

     for(int x = 0; x < myQueue.getQueue(2).size(); x++)
    {
      KThread thread = myQueue.getQueue(2).get(x);
      if(((ThreadData)thread.schedulingState).getWaitingTime() >= 31)
      {
        myQueue.getQueue(1).remove(thread);
        myQueue.getQueue(2).add(thread);
        ((ThreadData)thread.schedulingState).setCurrentQueue(1);
        ((ThreadData)thread.schedulingState).setWaitingTime(0);
        //System.out.println(thread.getName() + " has been promoted from RQ2.");
      }
      else
      {
        int newWaitingTime = ((int)((ThreadData)thread.schedulingState).getWaitingTime()) + 1;
        ((ThreadData)thread.schedulingState).setWaitingTime(newWaitingTime);
         //System.out.println("Incremented waiting time of " + thread.getName() + " to " + newWaitingTime);
      }
    }
    
  
    // timerInterrupt puts a flag down to indicate that the thread has been interrepted. 
    KThread curThread = KThread.currentThread();
    System.out.println("\n Thread in timerInterrupt(): " + curThread.getName() + "\n");
    int timesInt = ((ThreadData)curThread.schedulingState).getTimesInterrupted();
    switch(((ThreadData)curThread.schedulingState).getCurrentQueue())
        {
      case 0:
        if(timesInt >= 1 && !curThread.isBlocked()) //then this is the second interrupt
        {
          myQueue.getQueue(0).remove(curThread);
          myQueue.getQueue(1).add(curThread);
          ((ThreadData)curThread.schedulingState).setCurrentQueue(1);
          ((ThreadData)curThread.schedulingState).setIWasInterrupted(true);
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(0);
          //System.out.println("Thread " + curThread.getName() + " should be demoted to RQ1");
          return true;
        }
        else
        {
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(timesInt+1);
          //System.out.println(curThread.getName() + " has been interrupted " + (timesInt+1) + " times.");
          return false;
        }
      case 1:
        if(((ThreadData)curThread.schedulingState).getTimesInterrupted() >= 3 && !curThread.isBlocked()) //then this is the fourth interrupt
        {
          myQueue.getQueue(1).remove(curThread);
          myQueue.getQueue(2).add(curThread);
          ((ThreadData)curThread.schedulingState).setCurrentQueue(2);
          ((ThreadData)curThread.schedulingState).setIWasInterrupted(true);
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(0);
          //System.out.println("Thread " + curThread.getName() + " should be demoted to RQ2");
          return true;
        }
        else
        {
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(timesInt+1);
          //System.out.println(curThread.getName() + " has been interrupted " + (timesInt+1) + " times.");
          return false;
        }
      case 2:
        if(((ThreadData)curThread.schedulingState).getTimesInterrupted() >= 7 && !curThread.isBlocked()) //then this is the eighth interrupt
        {
          ((ThreadData)curThread.schedulingState).setIWasInterrupted(true);
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(0);
          return true;
        }
        else
        {
          ((ThreadData)curThread.schedulingState).setTimesInterrupted(timesInt+1);
          //System.out.println(curThread.getName() + " has been interrupted " + (timesInt+1) + " times.");
          return false;
        }
    }

    return true;
  
  }

  /**
   * The ThreadQueue class is a general-purpose class for a thread of queues
   * waiting on some resource. In our case, this queue will manage access to the
   * CPU under MLFQ.
   */
  private static class MultilevelFeedbackQueue extends ThreadQueue {
    //PriorityQueue<LinkedList<KThread>> mlfq;
    LinkedList<KThread> RQ0;
    LinkedList<KThread> RQ1;
    LinkedList<KThread> RQ2;
    //RoundRobinScheduler currentRunning;
    /**
     * Initialize the queue.
     */
    public MultilevelFeedbackQueue() {

      // TODO
      //mlfq = new PriorityQueue <LinkedList<KThread>>();
      RQ0 = new LinkedList<KThread>();
      RQ1 = new LinkedList<KThread>();
      RQ2 = new LinkedList<KThread>();
      //mlfq.add(RQ0);
      //mlfq.add(RQ1);
      //mlfq.add(RQ2);
      int timeCounter = 0;
      //Alarm myAlarm = new Alarm();

    }

    public LinkedList<KThread> getQueue(int i)
    {
      if( i == 0)
        return RQ0;
      if(i == 1)
        return RQ1;
      if(i == 2)
        return RQ2;
      return null;
    }
  
    /**
     * Called when a thread needs to be put into the waiting queue to acquire
     * the resource (in this case, the resource is the CPU).
     * 
     * @param thread
     *          The thread to insert into the queue.
     */
    @Override
    public void waitForAccess(KThread thread) {


      //System.out.println("Added thread " + thread.getName() + " to ready queue");
      //Will only be called when the thread is coming back from I/O or is just being created. 

      Lib.assertTrue(Machine.interrupt().disabled());

      //If it is a new thread (if it is null), assign the scheduling State to be a thread data object.
      if(thread.schedulingState == null)
      {
        thread.schedulingState = new ThreadData();
      }

      //Add the thread to a queue that is appropreate due to the fact that it is coming back from I/O
      // or when it is a new thread being created, so always go into a higher priority queue. 
      Integer curQueue = ((ThreadData)thread.schedulingState).getCurrentQueue();
      if(curQueue == null)
      {
        ((ThreadData)thread.schedulingState).setCurrentQueue((Integer)0); 
          //System.out.println("Placed thread " + thread.getName() + " in RQ0, since it's a new thread.");
          RQ0.add(thread);
      }
      else{
      switch(curQueue)
      {
        case 0:
            RQ0.add(thread);
            //System.out.println("Placed thread " + thread.getName() + " in RQ0");
        break;
        case 1:
            ((ThreadData)thread.schedulingState).setCurrentQueue((Integer)1); 
            //System.out.println("Placed thread " + thread.getName() + " in RQ1");
            RQ1.add(thread);
        break;
        case 2:
            ((ThreadData)thread.schedulingState).setCurrentQueue((Integer)2);
            //System.out.println("Placed thread " + thread.getName() + " in RQ2");
            RQ1.add(thread);
        break;
        default: 
          ((ThreadData)thread.schedulingState).setCurrentQueue((Integer)0); 
          //System.out.println("Placed thread " + thread.getName() + " in RQ0, since it's a new thread.");
          RQ0.add(thread);
          break;
      }
    }

      // TODO

    }

    /**
     * Called when we need to select the next thread to acquire the resource
     * (which in this case, means getting to run on the CPU).
     * 
     * @return The next thread to schedule.
     */
    @Override
    public KThread nextThread() {
      Lib.assertTrue(Machine.interrupt().disabled());
      
      KThread next = null;
      if(!RQ0.isEmpty())
      {
        next = RQ0.pop();
      }     
      else if(!RQ1.isEmpty())
      {
        next = RQ1.pop();
      }
      else if(!RQ2.isEmpty())
      {
        next = RQ2.pop();
      }
     
      if(next != null)
      {
        ((ThreadData)next.schedulingState).setWaitingTime(0);
        ((ThreadData)next.schedulingState).setTimesInterrupted(0);
      }
      // TODO
      return next;
    }

    /**
     * Called when a thread can immediately take possession of the resource
     * (that is, without having to go through waitForAccess() first). In our
     * case, this means the thread will immediately be able to run on the CPU.
     * Depending on your implementation, you may or may not actually need to do
     * anything here.
     * 
     * @param thread
     *          The thread to run on the CPU.
     */
    @Override
    public void acquire(KThread thread) {
      Lib.assertTrue(Machine.interrupt().disabled());

      // TODO

    }

    /**
     * Print out the threads waiting on the CPU. Helpful in debugging.
     */
    @Override
    public void print() {
      Lib.assertTrue(Machine.interrupt().disabled());

      // TODO

    }

  }

  /**
   * Execute tests of the MLFQ scheduler. Don't modify this method; instead, put
   * your test code inside the MultilevelFeedbackSchedulerTest class.
   */
  public static void selfTest() {
    MultilevelFeedbackSchedulerTest.runTest();
  }

}
