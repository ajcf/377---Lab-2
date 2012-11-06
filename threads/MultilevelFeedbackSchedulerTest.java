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
    System.out.println("\n### CPU test for MLFQ ###");

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

  private static void runAgingTest(){
     System.out.println("\n### Running MLFQ test for Aging ###");

    // create workers to specify what the threads will do
    ThreadTask worker1 = new ThreadTask(1, false);
    ThreadTask worker2 = new ThreadTask(2, true);
    ThreadTask worker3 = new ThreadTask(3, true);
    ThreadTask worker4 = new ThreadTask(4, true);
    ThreadTask worker5 = new ThreadTask(5, true);
    ThreadTask worker6 = new ThreadTask(6, true);
    ThreadTask worker7 = new ThreadTask(7, true);
    ThreadTask worker8 = new ThreadTask(8, true);
    ThreadTask worker9 = new ThreadTask(9, true);
    ThreadTask worker10 = new ThreadTask(10, true);
    ThreadTask worker11 = new ThreadTask(11, true);
    ThreadTask worker12 = new ThreadTask(12, true);
    ThreadTask worker13 = new ThreadTask(13, true);
    ThreadTask worker14 = new ThreadTask(14, true);
    ThreadTask worker15 = new ThreadTask(15, true);
    ThreadTask worker16 = new ThreadTask(16, true);
    ThreadTask worker17 = new ThreadTask(17, true);
    ThreadTask worker18 = new ThreadTask(18, true);
    ThreadTask worker19 = new ThreadTask(19, true);
    ThreadTask worker20 = new ThreadTask(20, true);
    ThreadTask worker21 = new ThreadTask(21, true);
    ThreadTask worker22 = new ThreadTask(22, true);
    ThreadTask worker23 = new ThreadTask(23, true);
    ThreadTask worker24 = new ThreadTask(24, true);
    ThreadTask worker25 = new ThreadTask(25, true);
    ThreadTask worker26 = new ThreadTask(26, true);
    ThreadTask worker27 = new ThreadTask(27, true);
    ThreadTask worker28 = new ThreadTask(28, true);
    ThreadTask worker29 = new ThreadTask(29, true);
    ThreadTask worker30 = new ThreadTask(30, true);
    ThreadTask worker31 = new ThreadTask(31, true);
    ThreadTask worker32 = new ThreadTask(32, true);
    ThreadTask worker33 = new ThreadTask(33, true);

    System.out.println("Initialized Tasks, 1 is CPU and 2-33 are I/O");

    // initialize the new threads to run the workers
    KThread thread1 = new KThread(worker1);
    KThread thread2 = new KThread(worker2);
    KThread thread3 = new KThread(worker3);
    KThread thread4 = new KThread(worker4);
    KThread thread5 = new KThread(worker5);
    KThread thread6 = new KThread(worker6);
    KThread thread7 = new KThread(worker7);
    KThread thread8 = new KThread(worker8);
    KThread thread9 = new KThread(worker9);
    KThread thread10 = new KThread(worker10);
    KThread thread11 = new KThread(worker11);
    KThread thread12 = new KThread(worker12);
    KThread thread13 = new KThread(worker13);
    KThread thread14 = new KThread(worker14);
    KThread thread15 = new KThread(worker15);
    KThread thread16 = new KThread(worker16);
    KThread thread17 = new KThread(worker17);
    KThread thread18 = new KThread(worker18); 
    KThread thread19 = new KThread(worker19); 
    KThread thread20 = new KThread(worker20); 
    KThread thread21 = new KThread(worker21); 
    KThread thread22 = new KThread(worker22); 
    KThread thread23 = new KThread(worker23); 
    KThread thread24 = new KThread(worker24); 
    KThread thread25 = new KThread(worker25); 
    KThread thread26 = new KThread(worker26); 
    KThread thread27 = new KThread(worker27); 
    KThread thread28 = new KThread(worker28); 
    KThread thread29 = new KThread(worker29); 
    KThread thread30 = new KThread(worker30); 
    KThread thread31 = new KThread(worker31); 
    KThread thread32 = new KThread(worker32); 
    KThread thread33 = new KThread(worker33); 

    System.out.println("Initialized Workers");

    // name the threads for debugging and identification
    thread1.setName("t1");
    thread2.setName("t2");
    thread3.setName("t3");
    thread4.setName("t4");
    thread5.setName("t5");
    thread6.setName("t6");
    thread7.setName("t7");
    thread8.setName("t8");
    thread9.setName("t9");
    thread10.setName("t10");
    thread11.setName("t11");
    thread12.setName("t12");
    thread13.setName("t13");
    thread14.setName("t14");
    thread15.setName("t15");
    thread16.setName("t16");
    thread17.setName("t17");
    thread18.setName("t18");
    thread19.setName("t19");
    thread20.setName("t20");
    thread21.setName("t21");
    thread22.setName("t22");
    thread23.setName("t23");
    thread24.setName("t24");
    thread25.setName("t25");
    thread26.setName("t26");
    thread27.setName("t27");
    thread28.setName("t28");
    thread29.setName("t29");
    thread30.setName("t30");
    thread31.setName("t31");
    thread32.setName("t32");
    thread33.setName("t33");

    System.out.println("Named Threads");

    // start running the threads in parallel
    thread1.fork();
    thread2.fork();
    thread3.fork();
    thread4.fork();
    thread5.fork();
    thread6.fork();
    thread7.fork();
    thread8.fork();
    thread9.fork();
    thread10.fork();
    thread11.fork();
    thread12.fork();
    thread13.fork();
    thread14.fork();
    thread15.fork();
    thread16.fork();
    thread17.fork();
    thread18.fork();
    thread19.fork();
    thread20.fork();
    thread21.fork();
    thread22.fork();
    thread23.fork();
    thread24.fork();
    thread25.fork();
    thread26.fork();
    thread27.fork();
    thread28.fork();
    thread29.fork();
    thread30.fork();
    thread31.fork();
    thread32.fork();
    thread33.fork();


    System.out.println("Forked Threads");

    // let the threads run for 5000 ticks
    ThreadedKernel.alarm.waitUntil(5000);

    System.out.println("Alarm Rang");

    // signal the threads to exit
    worker1.terminate();
    worker2.terminate();
    worker3.terminate();
    worker4.terminate();
    worker5.terminate();
    worker6.terminate();
    worker7.terminate();
    worker8.terminate();
    worker9.terminate();
    worker10.terminate();
    worker11.terminate();
    worker12.terminate();
    worker13.terminate();
    worker14.terminate();
    worker15.terminate();
    worker16.terminate();
    worker17.terminate();
    worker18.terminate();
    worker19.terminate();
    worker20.terminate();
    worker21.terminate();
    worker22.terminate();
    worker23.terminate();
    worker24.terminate();
    worker25.terminate();
    worker26.terminate();
    worker27.terminate();
    worker28.terminate();
    worker29.terminate();
    worker30.terminate();
    worker31.terminate();
    worker32.terminate();
    worker33.terminate();

    // wait until all threads are finished
    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();
    thread5.join();
    thread6.join();
    thread7.join();
    thread8.join();
    thread9.join();
    thread10.join();
    thread11.join();
    thread12.join();
    thread13.join();
    thread14.join();
    thread15.join();
    thread16.join();
    thread17.join();
    thread18.join();
    thread19.join();
    thread20.join();
    thread21.join();
    thread22.join();
    thread23.join();
    thread24.join();
    thread25.join();
    thread26.join();
    thread27.join();
    thread28.join();
    thread29.join();
    thread30.join();
    thread31.join();
    thread32.join();
    thread33.join();

    System.out.println("Thread 1 should have been promoted from RQ2. IF so, test passes!");


    System.out.println("\n### Mixed I/O and CPU test for MLFQ finished ###\n");

  }

}
