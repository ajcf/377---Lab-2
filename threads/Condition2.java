package nachos.threads;

import nachos.machine.*;

// Tingxin
import java.util.LinkedList;
import java.util.Iterator;
// end

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
	// Tingxin
	waitThreadsQueue = new LinkedList<KThread>();
	// end
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {

        /* If the current thread doesn't hold the lock, then abort */
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Tingxin - where to put interupt disable and enable?
	boolean intStatus = Machine.interrupt().disable();	
	waitThreadsQueue.add(KThread.currentThread());
	conditionLock.release();
	KThread.sleep();
	conditionLock.acquire();
	Machine.interrupt().restore(intStatus);
	// end	
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Tingxin
	Machine.interrupt().disable();          
	if(!waitThreadsQueue.isEmpty()){
		KThread thread = waitThreadsQueue.removeFirst();
		thread.ready();
	}
	Machine.interrupt().enable();  
	// end
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	// Tingxin
	while (!waitThreadsQueue.isEmpty())
		wake();
	// end
    }

    /**
     * Tests whether this module is working.
     */
    public static void selfTest() {
	Condition2Test.runTest();
    }

    private static final char dbgCondition = 'c';

    private Lock conditionLock;
    // Tingxin
    private LinkedList<KThread> waitThreadsQueue;
}
