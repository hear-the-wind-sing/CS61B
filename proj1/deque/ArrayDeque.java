package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    private T[] items;
    private int size;
    private int startIndex;

    private class ArrayDequeIterator implements Iterator<T> {
        private int pointer;
        public ArrayDequeIterator(){
            pointer = startIndex;
        }

        public boolean hasNext() {
            return pointer != (size + startIndex) % items.length;
        }

        public T next() {
            T returnItem = items[pointer];
            pointer = (pointer + 1) % items.length;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDeque.ArrayDequeIterator();
    }
    //private int endIndex;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        startIndex = 0;
        //endIndex = 0;
    }
    public int size() {
        return size;
    }
//    public boolean isEmpty() {
//        if (size == 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    public void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int curIndex = startIndex % items.length;
        for (int i = 0; i < size ; i++) {
            a[i] = items[curIndex];
            curIndex = (curIndex + 1) % items.length;
        }
        items = a;
        startIndex = 0;
    }
    public void printDeque() {
        int i = startIndex % items.length;
        int cnt = 0;
        while(cnt != size) {
            System.out.print(items[i] + " ");
            i = (i + 1) % items.length;
            cnt++;
        }
        System.out.println(" ");
    }
    public void addFirst(T x) {
        if(size == items.length) {
            resize(size * 2);
        }

        items[(startIndex + items.length - 1) % items.length] = x;
        startIndex = (startIndex + items.length - 1) % items.length;
        size++;
    }
    public void addLast(T x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[(startIndex + size ) % items.length] = x;
        size++;
    }
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if(items.length >= 16 && size < items.length / 4) {
            resize(items.length / 4);
        }

        T firstT = items[startIndex];
        items[startIndex] = null;
        size--;
        startIndex++;
        return firstT;
    }
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if(items.length >= 16 && size < items.length / 4) {
            resize(items.length / 4);
        }

        T lastT = items[(startIndex + size - 1) % items.length];
        items[(startIndex + size - 1) % items.length] = null;
        size--;
        return lastT;
    }
    public T get(int index) {
        return items[(startIndex + index) % items.length];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque<T> ad = (ArrayDeque<T>) o;
        if (ad.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (ad.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }
}
