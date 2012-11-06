package nachos.threads;

public class ThreadData {

	private Integer currentQueue;
	private int waitingTime;
	private int timesInterrupted;
	private boolean iWasInterrupted;

	public ThreadData()
	{
		currentQueue = 0;
		waitingTime = 0;
		timesInterrupted = 0;
		iWasInterrupted = false; 
	}

	public Integer getCurrentQueue()
	{
		return currentQueue;
	}

	public void setCurrentQueue(Integer currQ)
	{
		currentQueue = currQ;
	}

	public int getWaitingTime()
	{
		return waitingTime;
	}

	public void setWaitingTime(int time)
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

	public boolean getIWasInterrupted()
	{
		return iWasInterrupted;
	}

	public void setIWasInterrupted(boolean wasInter)
	{
		iWasInterrupted = wasInter;
	}
}
