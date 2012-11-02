package nachos.threads;

public class MultilevelFeedbackSchedulerTest {

  public static void runTest() {
    System.out.println("#########################");
    System.out.println("## MLFQ testing begins ##");
    System.out.println("#########################");

    /* TODO: test the MLFQ scheduler */
    // runTest1();
    // runTest2();



    System.out.println("#######################");
    System.out.println("## MLFQ testing ends ##");
    System.out.println("#######################\n");
  }

    private static void runBasicTest() {
    System.out.println("\n### Running basic RR test ###");

    // create workers to specify what the threads will do
    ThreadTask worker1 = new ThreadTask(1, false);
    ThreadTask worker2 = new ThreadTask(2, false);
    ThreadTask worker3 = new ThreadTask(3, false);

    // initialize the new threads to run the workers
    KThread thread1 = new KThread(worker1);
    KThread thread2 = new KThread(worker2);
    KThread thread3 = new KThread(worker3);

    // name the threads for debugging and identification
    thread1.setName("t1");
    thread2.setName("t2");
    thread3.setName("t3");

    // start running the threads in parallel
    thread1.fork();
    thread2.fork();
    thread3.fork();

    // let the threads run for 5000 ticks
    ThreadedKernel.alarm.waitUntil(5000);

    // signal the threads to exit
    worker1.terminate();
    worker2.terminate();
    worker3.terminate();

    // wait until all threads are finished
    thread1.join();
    thread2.join();
    thread3.join();

    System.out.println("\n### Basic RR test finished ###\n");

    ///LOOK I"M ADDING STUFF
  }

}
