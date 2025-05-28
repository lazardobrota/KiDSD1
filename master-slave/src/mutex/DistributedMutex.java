package mutex;

public interface DistributedMutex {

    void lock(Object object);
    void unlock(Object object);
}
