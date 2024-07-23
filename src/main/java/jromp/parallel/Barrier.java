package jromp.parallel;

public class Barrier {
	private final String name;
	private final int count;
	private int currentCount = 0;
	private boolean isWaiting = false;
	private final Object lock = new Object();

	public Barrier(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void reset() {
		currentCount = 0;
		isWaiting = false;
	}

	public void await() {
		synchronized (lock) {
			currentCount++;

			// If all threads have reached the barrier, notify all threads to continue.
			if (currentCount == count) {
				lock.notifyAll();
				reset();
			} else { // Otherwise, wait for all threads to reach the barrier.
				isWaiting = true;

				while (isWaiting) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Barrier [name=" + name + ", count=" + count + ", currentCount=" + currentCount + ", isWaiting=" + isWaiting + "]";
	}
}
