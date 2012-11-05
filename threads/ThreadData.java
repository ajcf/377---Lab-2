package nachos.threads;

import nachos.machine.Lib;
import nachos.machine.Machine;

public class ThreadData {


	private Integer currentQueue;
	private int waitingTime;
	private int timesInterrupted; 

	public ThreadData()
	{
		currentQueue = null;
		waitingTime = null;
		timesInterrupted = 0;
		iWasInturrupted = false; 
	}

	public Integer getCurrentQueue()
	{
		return currentQueue;
	}

	public void setCurrentQueue(Integer currQ)
	{
		currentQueue = currQ;
	}

	public int getWatingTime()
	{
		return waitingTime;
	}

	public int setWaitingTime(int time)
	{
		waitingTime = time;
	}

	public int getTimesInterrupted()
	{
		return timesInterrupted;
	}

	public void setTimesInterrupted( int timesInt)
	{
		timesInterrupted = timesInt;
	}

	public boolean getIWasInturrupted()
	{
		return iWasInturrupted;
	}

	public void setIWasInturrupted(boolean wasInter)
	{
		iWasInturrupted = wasInter;
	}
}
