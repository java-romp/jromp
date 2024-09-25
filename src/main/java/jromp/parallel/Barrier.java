package jromp.parallel;

/**
 * A barrier is a synchronization construct that allows multiple threads to wait
 * for each other at a common point.
 */
public class Barrier {
    /**
     * The name of the barrier.
     */
    private final String name;

    /**
     * The number of threads that must reach the barrier before they can continue.
     */
    private final int count;

    /**
     * The number of threads that have reached the barrier.
     */
    private int currentCount = 0;

    /**
     * A flag to indicate whether the threads are waiting at the barrier.
     */
    private boolean isWaiting = false;

    /**
     * The lock object to synchronize the threads.
     */
    private final Object lock = new Object();

    /**
     * A flag to indicate whether the threads should wait (or not) at the barrier.
     */
    private boolean nowait = false;

    /**
     * Constructs a barrier with the specified name and count.
     *
     * @param name  the name of the barrier.
     * @param count the number of threads that must reach the barrier before they can continue.
     */
    public Barrier(String name, int count) {
        this.name = name;
        this.count = count;
    }

    /**
     * Returns the name of the barrier.
     *
     * @return the name of the barrier.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of threads that must reach the barrier before they can continue.
     *
     * @return the number of threads that must reach the barrier before they can continue.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the number of threads that have reached the barrier.
     *
     * @return the number of threads that have reached the barrier.
     */
    public int getCurrentCount() {
        return currentCount;
    }

    /**
     * Returns a flag to indicate whether the threads are waiting at the barrier.
     *
     * @return a flag to indicate whether the threads are waiting at the barrier.
     */
    public boolean isWaiting() {
        return isWaiting;
    }

    /**
     * Returns a flag to indicate whether the threads should wait (or not) at the barrier.
     *
     * @return a flag to indicate whether the threads should wait (or not) at the barrier.
     */
    public boolean isNowait() {
        return nowait;
    }

    /**
     * Sets the nowait flag.
     *
     * @param nowait the nowait flag to set.
     */
    public void setNowait(boolean nowait) {
        this.nowait = nowait;
    }

    /**
     * Resets the barrier. This method is called after all threads have reached the barrier.
     * It resets the current count and the waiting flag.
     */
    public void reset() {
        currentCount = 0;
        isWaiting = false;
    }

    /**
     * Causes the current thread to wait until all threads have reached the barrier.
     */
    public void await() {
        if (nowait) {
            return;
        }

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
