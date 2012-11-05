package nachos.threads;

import nachos.machine.Machine;

public class MultilevelFeedbackSchedulerTest {

  public static void runTest() {
    System.out.println("#########################");
    System.out.println("## MLFQ testing begins ##");
    System.out.println("#########################");

    /* Run a simple test of the scheduler */
    runBasicTest();

    /* Run a test with mixed CPU and I/O simulation */
    runMixedTest();

    /* Run any other tests */
    // runComplexTest();
    // runAnotherComplexTest();
    // ...

    System.out.println("#######################");
    System.out.println("## MLFQ testing ends ##");
    System.out.println("#######################\n");
  }

  private static class ThreadTask implements Runnable {

    private final int id;

    private boolean terminated;

    private boolean io;

    public ThreadTask(int id, boolean io) {
      this.id = id;
      this.terminated = false;
      this.io = io;
    }

    public void terminate() {
      this.terminated = true;
    }

    @Override
    public void run() {
      System.out.println("thread " + KThread.currentThread().getName()
          + " started");
      while (!terminated) {

        // Remember this is a thread running inside the kernel,
        // so there's no automatic preemption -- you need to
        // manually yield to other threads.

        if (io) { // simulate I/O job
          // do I/O lasting 200 ticks
          long ioTime = 200;
          // this blocks the thread until ioTime has passed
          ThreadedKernel.alarm.waitUntil(ioTime);
          System.out.print("i" + id);

        } else { // simulate CPU job
          // do 800 ticks of 'computation'
          Machine.interrupt().tick(800);
          System.out.print("c" + id);
        }

      }
    }
  }

  private static void runBasicTest() {
    System.out.println("\n### Running basic CPU test for MLFQ ###");

    // create workers to specify what the threads will do
    ThreadTask worker1 = new ThreadTask(1, false);
    ThreadTask worker2 = new ThreadTask(2, false);
    ThreadTask worker3 = new ThreadTask(3, false);

    System.out.println("Initialized Tasks");

    // initialize the new threads to run the workers
    KThread thread1 = new KThread(worker1);
    KThread thread2 = new KThread(worker2);
    KThread thread3 = new KThread(worker3);

    System.out.println("Initialized Workers");

    // name the threads for debugging and identification
    thread1.setName("t1");
    thread2.setName("t2");
    thread3.setName("t3");

    // start running the threads in parallel
    thread1.fork();
    thread2.fork();
    thread3.fork();

    System.out.println("Forked Threads");

    // let the threads run for 5000 ticks
    ThreadedKernel.alarm.waitUntil(5000);

    System.out.println("Alarm Rang");

    // signal the threads to exit
    worker1.terminate();
    worker2.terminate();
    worker3.terminate();

    // wait until all threads are finished
    thread1.join();
    thread2.join();
    thread3.join();

    System.out.println("\n### Basic CPU test for MLFQ finished ###\n");
  }

  private static void runMixedTest() {
    System.out.println("\n### Running mixed (CPU and I/O) MLFQ test ###");

    // create workers to specify what the threads will do
    ThreadTask worker1 = new ThreadTask(1, false);
    ThreadTask worker2 = new ThreadTask(2, true);
    ThreadTask worker3 = new ThreadTask(3, false);
    ThreadTask worker4 = new ThreadTask(4, true);

    System.out.println("Initialized Tasks, 2 and 4 are I/o");

    // initialize the new threads to run the workers
    KThread thread1 = new KThread(worker1);
    KThread thread2 = new KThread(worker2);
    KThread thread3 = new KThread(worker3);
    KThread thread4 = new KThread(worker4);
    
    System.out.println("Initialized Workers");

    // name the threads for debugging and identification
    thread1.setName("t1");
    thread2.setName("t2");
    thread3.setName("t3");
    thread4.setName("t4");

    // start running the threads in parallel
    thread1.fork();
    thread2.fork();
    thread3.fork();
    thread4.fork();

    System.out.println("Forked Threads");

    // let the threads run for 5000 ticks
    ThreadedKernel.alarm.waitUntil(5000);

    System.out.println("Alarm Rang");

    // signal the threads to exit
    worker1.terminate();
    worker2.terminate();
    worker3.terminate();
    worker4.terminate();

    // wait until all threads are finished
    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();

    System.out.println("\n### Mixed I/O and CPU test for MLFQ finished ###\n");
  }

}
