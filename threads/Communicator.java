package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {	
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
	this.hasSpeaker = false;
	this.hasListener = false;
	this.isSpoken = false;
	
	this.mutex = new Lock();
		
	this.hasSpeakerCond = new Condition2(mutex);
	this.hasListenerCond = new Condition2(mutex);
	this.wordConsumedCond = new Condition2(mutex);
	this.wordProducedCond = new Condition2(mutex);	
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
	mutex.acquire();
	/* level-1: multiple speaker waiting */
	while (hasSpeaker) {
		hasSpeakerCond.sleep();
	}	
	hasSpeaker = true;
	/* level-2: speak and sync with listener */
	//while (!hasListener) {
	//	listenerArrivedCond.sleep();
	//}
	this.word = word;
	isSpoken = true;
	wordProducedCond.wake();
	while (isSpoken) {
		wordConsumedCond.sleep();
	}
	/* signal that the speaker is leaving */
	hasSpeaker = false;
	hasSpeakerCond.wake();
	mutex.release();
    }
	
    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
	int word;
	mutex.acquire();
	while (hasListener) {
		hasListenerCond.sleep();
	}
	hasListener = true;
	//listenerArrivedCond.wake();
	while (!isSpoken) {
		wordProducedCond.sleep();
	}
	/* consume the word and signal */
	word = this. word;
	isSpoken = false;
	wordConsumedCond.wake();
	/* signal that the listener is leaving */
	hasListener = false;
	hasListenerCond.wake();
	mutex.release();
	return word;
    }
	
    /**
     * Tests whether this module is working.
     */
    public static void selfTest() {
        CommunicatorTest.runTest();
    }
 
    // Tingxin
    private boolean hasSpeaker;		
    private boolean hasListener;		
    private boolean isSpoken;

    private Condition2 hasSpeakerCond;
    private Condition2 hasListenerCond;
    private Condition2 wordConsumedCond;
    private Condition2 wordProducedCond;

    private Lock mutex;
    private int word;	
    // end	
}

