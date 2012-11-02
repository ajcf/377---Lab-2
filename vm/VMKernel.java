package nachos.vm;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import nachos.vm.*;

import java.util.*;

/**
 * A kernel that can support multiple demand-paging user processes.
 */
public class VMKernel extends UserKernel {
	/**
	 * Stores a list of available swap pages in the swap file
	 * so that pages can be written to disk.
	 */
	protected LinkedList<Integer> freeSwapPages;
	
	/**
	 * Keeps track of where a given page is stored in the swap file.
	 */
	protected HashMap<PageId, Integer> swapPagePositions;

	/**
	 * A list of physical pages that should not be swapped out
	 * as they are being used in a user process read/write operation.
	 */
	protected ArrayList<Integer> pinnedPages;

	/**
	 * Locks to protect the pagetable and pinned pages.
	 */
	protected Lock pinnedPagesLock;
	protected Lock pageTableLock;

	/**
	 * An inverted page table that maps PageIds (a pid and a virtual page
	 * number) to the TranslationEntry for that page (storing the physical
	 * page number).
	 */
	public HashMap<PageId, TranslationEntry> invertedPageTable;

	/**
	 * Stores a list of swappable pages (swap pages and read/write executable pages).
	 * These pages are the ones that can be written to disk, as opposed
	 * to read-only executable pages.
	 */
	protected ArrayList<TranslationEntry> swappablePages;
	
	/**
	 * Stores pages that needed to be written to disk. 
	 */
	protected OpenFile swapFile;
	
	/**
	 * Stores all of the pages that are currently swapped out to disk.
	 */
	protected HashMap<PageId, TranslationEntry> pagesInSwap;

	protected final int pageSize = Processor.pageSize;

	/**
	 * Allocate a new VM kernel.
	 */
	public VMKernel() {
		super();
		
		// Initialize all local storage.
		invertedPageTable = new HashMap<PageId, TranslationEntry>();
		swappablePages = new ArrayList<TranslationEntry>();
		pagesInSwap = new HashMap<PageId, TranslationEntry>();
		pinnedPages = new ArrayList<Integer>();
		swapPagePositions = new HashMap<PageId, Integer>();

		freeSwapPages = new LinkedList<Integer>();
		for(int i=0; i<64; i++){
			freeSwapPages.add(i);
		}
	}

	/**
	 * Initialize this kernel.
	 */
	public void initialize(String[] args) {
		super.initialize(args);

		// Create the swap file upon kernel initialization.
		// Must be done here because the filesystem needs to be ready
		// beforehand.
		swapFile = ThreadedKernel.fileSystem.open("nachos.swp", true);
		pinnedPagesLock = new Lock();
		pageTableLock = new Lock();
	}

	/**
	 * Test this kernel.
	 */	
	public void selfTest() {
		super.selfTest();
	}

	/**
	 * Start running user programs.
	 */
	public void run() {
		super.run();
	}

	/**
	 * Terminate this kernel. Never returns.
	 */
	public void terminate() {
		// Close and delete the swap file.
		swapFile.close();
		ThreadedKernel.fileSystem.remove(swapFile.getName());
		super.terminate();
	}

	/**
	 * Called by a process when it needs access to a page that is currently
	 * in swap. This function will find the position of this page in the swap
	 * file, read it in and copy it to memory, and then place a new entry
	 * in the global pagetable for this page. If there is not enough physical
	 * memory available, a page will be swapped to disk by getFreePage().
	 * @param pid the process id of the process requesting the page.
	 * @param vpn the virtual page number in that process for the page.
	 * @return the physical page number of the page that was swapped in.
	 */
	public int swapInPage(int pid, int vpn){
		// Find the file position in swapPagePositions.
		Integer pos = swapPagePositions.get(new PageId(pid,vpn));
		
		// Read the page from the file.
		int pageSize = Processor.pageSize;
		byte[] buf = new byte[pageSize];
		swapFile.seek(pos*pageSize);
		int bytesRead = swapFile.read(buf, 0, pageSize);
		if (bytesRead < Processor.pageSize){
			// Not reading a full page from the file is a fatal error.
			terminate();
		}

		// Get a new free physical page, swapping something else out if needed.	
		TranslationEntry freePage = getFreePage(pid, vpn, true, false);

		// Copy contents of the read-in page to memory.
		byte[] memory = Machine.processor().getMemory();
		System.arraycopy(buf, 0, memory, freePage.ppn*pageSize, pageSize); 

		// Place a new entry in the global page table for the to new page.
		if(!swappablePages.contains(freePage)){
			swappablePages.add(freePage);
		}

		// Remove the page from the list of swapped out pages, since
		// it just came back in.
		pagesInSwap.remove(new PageId(pid, vpn));
		swapPagePositions.remove(new PageId(pid, vpn));
		freeSwapPages.add(pos);

		// Return the address of the new physical page.
		return freePage.ppn;
	}
	
	/**
	 * Pins a page so that it cannot be swapped out.
	 * @param e a page to pin.
	 */
	public void pinPage(Integer e){
		pinnedPagesLock.acquire();
		pinnedPages.add(e);
		pinnedPagesLock.release();
	}
	
	/**
	 * Removes a page from the list of pinned pages.
	 * @param e the physical page to unpin.
	 */
	public void unPinPage(Integer e){
		pinnedPagesLock.acquire();
		pinnedPages.remove(e);
		pinnedPagesLock.release();
	}

	/**
	 * Given a pid and vpn, finds a physical page to swap out
	 * and replace with the page for that pid/vpn combo.
	 * @param pid the pid of the process owning the page.
	 * @param vpn the virtual page number of the page inside that process.
	 * @return the physical page number of the newly empty page.
	 */
	private int swapOutPage(){
		pageTableLock.acquire();
		pinnedPagesLock.acquire();

		// Perform the second-chance (clock) algorithm on the list of
		// swappable pages to find one to replace. The loop will iterate
		// over the pages until it finds one that is not used, while also
		// setting the used bits to 0 each time it finds a used page
		TranslationEntry entry = null;
		
		// i goes up to twice the size of the page list because in the worst
		// case we need to loop over the whole queue once.
		int i;
		for(i=0; i<swappablePages.size()*2; i++){
			// i%size so that i stays within range.
			TranslationEntry e = swappablePages.get(i%swappablePages.size());
			
			// Don't swap out pinned physical pages.
			if(pinnedPages.contains(e.ppn)){
				continue;
			}

			// Use the 'used' bit as a reference bit for the clock algorithm.
			// If it is not set, replace this page as it isn't used. If it is set,
			// unset it and continue.
			if(e.used == false){
				entry = e;
				break;
			}else{
				e.used = false;
			}
		}

		// Remove the entry from swappable pages.
		swappablePages.remove(i%swappablePages.size());

		// Find and remove the entry in the global pagetable.
		PageId id = null; 
		for(Map.Entry<PageId, TranslationEntry> e : invertedPageTable.entrySet()){
			if(e.getValue().vpn == entry.vpn && e.getValue().ppn == entry.ppn){
				id = e.getKey();
				break;
			}
		}
		invertedPageTable.remove(id);

		byte[] pageToSwap = new byte[pageSize];
		byte[] memory = Machine.processor().getMemory();

		// Copy the contents of the page in memory to a buffer for writing.
		System.arraycopy(memory, entry.ppn*pageSize, pageToSwap, 0, pageSize);
		
		// Write the page to a free position in the swap file.
		int swapPagePos = freeSwapPages.removeFirst();
		swapFile.seek(swapPagePos*pageSize);
		int bytesWritten = swapFile.write(pageToSwap, 0, pageSize);
		if(bytesWritten != pageSize){
			Lib.debug(dbgVM, "Fatal error: failed to write page to swap.");
			terminate();
		}

		// Store the location of the swapped page so that we can find it later.
		pagesInSwap.put(id, entry);
		swapPagePositions.put(id, swapPagePos);

		// Zero out the page.
		Arrays.fill(pageToSwap, (byte)0);
		System.arraycopy(pageToSwap, 0, memory, entry.ppn*pageSize, pageSize);

		pageTableLock.release();
		pinnedPagesLock.release();

		// Return the newly freed physical page number.
		return entry.ppn;
	}

	/**
	 * Gets a free physical page. If necessary (out of physical pages),
	 * the kernel will swap out a page to disk.
	 * @param pid the process id of the process requesting a new page.
	 * @param vpn the virtual page number of the page requested.
	 * @param swappable if the page should be swappable or not.
	 * @param readOnly if the new page is read only.
	 * @return a TranslationEntry corresponding to the new page.
	 */
	public TranslationEntry getFreePage(
			int pid, int vpn,
			boolean swappable, boolean readOnly){

		int freePage;
		// If there is any free space available, just take a page and set it up
		// in the pagetable, otherwise we need to swap something out first.
		if(super.numFreePages() > 0){
			freePage = super.getFreePage();
		}else{
			freePage = swapOutPage();
		}

		// Create a pagetable entry for the new free page.
		TranslationEntry newPage = new TranslationEntry(
				vpn,
				freePage,
				true,
				readOnly,
				false,
				false);

		// If the process requested a page that we are allowed to swap out,
		// add this page to the list of swap-capable pages.
		if(swappable){
			swappablePages.add(newPage);
		}

		pageTableLock.acquire();
		invertedPageTable.put(new PageId(pid, vpn), newPage);
		pageTableLock.release();
		return newPage;
	}

	public TranslationEntry lookupAddress(int pid, int vpn){
		TranslationEntry page = invertedPageTable.get(new PageId(pid, vpn));
		return page;
	}

	/**
	 * Checks if a page resides in the swap file.
	 * @param pid the process ID requesting the check.
	 * @param vpn the virtual page # being checked.
	 * @return true if the page is in the swapfile, false otherwise.
	 */
	public boolean isPageInSwap(int pid, int vpn){
		return pagesInSwap.containsKey(new PageId(pid, vpn));
	}

	/**
	 * Frees a physical page so that it may be reallocated to another process.
	 */
	public void free(int ppn){
		freeListLock.acquire();
		if(freePhysicalPages.contains(ppn)){
			freeListLock.release();
			return;
		}

		freePhysicalPages.add(ppn);

		// Un-map this page in our inverted page table.
		PageId toRemove = null;
		for(Map.Entry<PageId, TranslationEntry> e :
			invertedPageTable.entrySet()){
			if(e.getValue().ppn == ppn){
				toRemove = e.getKey();
				if(swappablePages.contains(e.getValue())){
					swappablePages.remove(e.getValue());
				}
				break;
			}
		}

		if(toRemove != null){
			invertedPageTable.remove(toRemove);
		}

		freeListLock.release();
	}

	// dummy variables to make javac smarter
	private static VMProcess dummy1 = null;

	private static final char dbgVM = 'v';

	/**
	 * This class is used as a key into the inverted pagetable.
	 * It overrides required functions equals() and hashCode()
	 * so that it can be used properly as a key in the Java
	 * hashmap class.
	 */
	public class PageId {
		public int pid;
		public int vpn;

		public PageId(int pid, int vpn){
			this.pid = pid;
			this.vpn = vpn;
		}

		/**
		 * Uses the pre-made hashCode() function of the java String class
		 * to generate a unique hash for this key.
		 */
		public int hashCode(){
			return new String(pid + "#" + vpn).hashCode();
		}

		/**
		 * Compares two PageIds to each other. Returns true if the two
		 * IDs have the same page number and PID.
		 */
		public boolean equals(Object obj){
			if(!(obj instanceof PageId)){
				return false;
			}

			PageId p = (PageId) obj;
			return (p.pid == pid) && (p.vpn == vpn);
		}

		/**
		 * Used help debugging, converts a PageId to a string.
		 */
		public String toString(){
			return new String(pid + "," + vpn);
		}
	}
}
