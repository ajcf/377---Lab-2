package nachos.threads;

import nachos.machine.*;
import java.util.Random;


/**
 * A Tester for the Alarm class
 */
public class AlarmTest {
	
    private static class HiccupThread implements Runnable {
		
		public HiccupThread(String name, int numHiccups, int maxHiccupLength) {
			this.name = name;
			this.numHiccups = numHiccups;
			this.maxHiccupLength = maxHiccupLength;
		} 
		
		public void run() {
			
			System.out.println("** "+ name +": starting.");
			for (int i=0; i < numHiccups; i++) {
				int untilHiccup = AlarmTest.rng.nextInt(this.maxHiccupLength);
				System.out.println("** "+ name +": next hiccup at approximately time "+
                                   (Machine.timer().getTime() + untilHiccup));
				ThreadedKernel.alarm.waitUntil(untilHiccup);
				System.out.println("** "+ name +": hiccup! (time = "+Machine.timer().getTime()+")");
			}
			System.out.println("** "+ name +": ended.");
		}
		
		
		private String name;
		private int numHiccups;
		private int maxHiccupLength; 
    }
	
    /**
     * Tests whether this module is working.
     */
    public static void runTest() {
		System.out.println("**** Alarm testing begins ****");
		
		/* Create the Random Number Generator */
        rng = new Random();
		
		/* Create 5 hiccupThreads */
        KThread hiccupThreads[] = new KThread[numHiccupThreads];
        for (int i=0; i < numHiccupThreads; i++) {
			hiccupThreads[i] = new KThread(new HiccupThread("HiccupThread #"+i, 
															(i+1)*2, Math.max(1000,10000 - 1000*i)));
			hiccupThreads[i].setName("HiccupThread #"+i);
			hiccupThreads[i].fork();  
        }
		
        /* suspend the main thread until the maximum possible time */
        ThreadedKernel.alarm.waitUntil(10000 * (numHiccupThreads+1) * 2);
		
		System.out.println("**** Alarm testing end ****");
    }
	
    private static Random rng;
    private static final int numHiccupThreads = 2;
	
	
}

