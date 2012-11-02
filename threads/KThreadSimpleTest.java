package nachos.threads;

import nachos.machine.*;

// Tingxin
class HelloWorld  implements Runnable {
	HelloWorld() {
		this.printStr = "Hello, World!\n";
	}
	HelloWorld(String s) {
		this.printStr = s;
	}
	public void run() {
		System.out.println(printStr);
	}
	private String printStr;
}
// end

/**
 * A Simple Tester for the KThread class. 
 */
public class KThreadSimpleTest {

    /**
     * Tests whether this module is working.
     */
    public static void runTest() {

	System.out.println("**** Simple KThread testing begins ****");

	// Tingxin
	HelloWorld h1 = new HelloWorld("Hello, World 1.\n");
	HelloWorld h2 = new HelloWorld("Hello, World 2.\n");
	KThread kt1 = new KThread(h1);
	KThread kt2 = new KThread(h2);
	kt1.fork();
	kt2.fork();
	// end
        
	//KThread.yield(); // Q: why yoeld() here?
	System.out.println("**** Simple KThread testing ends ****");
    }

}
