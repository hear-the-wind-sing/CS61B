package deque;

public interface Deque<T> {
    public void addFirst(T t);
    public void addLast(T t);
    public int size();
    public void printDeque();
    public T removeFirst();
    public T removeLast();
    public T get(int index);
    default  boolean isEmpty() {
        return size() == 0;
    }
}
