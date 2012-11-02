package nachos.threads;

import nachos.machine.Config;
import nachos.machine.FileSystem;
import nachos.machine.Kernel;
import nachos.machine.Lib;
import nachos.machine.Machine;

/**
 * A multi-threaded OS kernel.
 */
public class ThreadedKernel extends Kernel {

  /**
   * Allocate a new multi-threaded kernel.
   */
  public ThreadedKernel() {
    super();
  }

  /**
   * Initialize this kernel. Creates a scheduler, the first thread, and an
   * alarm, and enables interrupts. Creates a file system if necessary.
   */
  @Override
  public void initialize(String[] args) {
    // set scheduler
    String schedulerName = Config.getString("ThreadedKernel.scheduler");
    scheduler = (Scheduler) Lib.constructObject(schedulerName);

    // set fileSystem
    String fileSystemName = Config.getString("ThreadedKernel.fileSystem");
    if (fileSystemName != null)
      fileSystem = (FileSystem) Lib.constructObject(fileSystemName);
    else if (Machine.stubFileSystem() != null)
      fileSystem = Machine.stubFileSystem();
    else
      fileSystem = null;

    // start threading
    new KThread(null);

    alarm = new Alarm();

    Machine.interrupt().enable();
  }

  /**
   * Test this kernel. Test various classes.
   */
  @Override
  public void selfTest() {

    System.out.println("\n*** Nachos Kernel Successfully Started ***\n");

    String schedulerName = Config.getString("ThreadedKernel.scheduler");
    if (schedulerName.equals("nachos.threads.RoundRobinScheduler")) {
      RoundRobinScheduler.selfTest();
    } else if (schedulerName.equals("nachos.threads.MultilevelFeedbackScheduler")) {
      MultilevelFeedbackScheduler.selfTest();
    }

    // KThread.simpleSelfTest();
    // KThread.selfTest();
    // Semaphore.selfTest();
    // Condition.selfTest();
    // Condition2.selfTest();
    // Alarm.selfTest();
    // Communicator.selfTest();
  }

  /**
   * A threaded kernel does not run user programs, so this method does nothing.
   */
  @Override
  public void run() {
  }

  /**
   * Terminate this kernel. Never returns.
   */
  @Override
  public void terminate() {
    Machine.halt();
  }

  /** Globally accessible reference to the scheduler. */
  public static Scheduler scheduler = null;

  /** Globally accessible reference to the alarm. */
  public static Alarm alarm = null;

  /** Globally accessible reference to the file system. */
  public static FileSystem fileSystem = null;

  // dummy variables to make javac smarter
  @SuppressWarnings("unused")
  private static RoundRobinScheduler dummy1 = null;

  @SuppressWarnings("unused")
  private static MultilevelFeedbackScheduler dummy2 = null;

  @SuppressWarnings("unused")
  private static Condition2 dummy3 = null;

  @SuppressWarnings("unused")
  private static Communicator dummy4 = null;

  @SuppressWarnings("unused")
  private static Rider dummy5 = null;

  @SuppressWarnings("unused")
  private static ElevatorController dummy6 = null;

}
