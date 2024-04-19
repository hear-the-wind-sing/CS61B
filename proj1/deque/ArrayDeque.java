package deque;

public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int startIndex;
    //private int endIndex;
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        startIndex = 0;
        //endIndex = 0;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }
    public void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
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
    public void addFirst(Item x) {
        if(size == items.length) {
            resize(size * 2);
        }

        items[(startIndex + items.length - 1) % items.length] = x;
        startIndex = (startIndex + items.length - 1) % items.length;
        size++;
    }
    public void addLast(Item x) {
        if (size == items.length) {
            resize(size * 2);
        }

        items[(startIndex + size ) % items.length] = x;
        size++;
    }
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }

        if(items.length >= 16 && size < items.length / 4) {
            resize(items.length / 4);
        }

        Item firstItem = items[startIndex];
        items[startIndex] = null;
        size--;
        startIndex++;
        return firstItem;
    }
    public Item removeLast() {
        if (size == 0) {
            return null;
        }

        if(items.length >= 16 && size < items.length / 4) {
            resize(items.length / 4);
        }

        Item lastItem = items[(startIndex + size - 1) % items.length];
        items[(startIndex + size - 1) % items.length] = null;
        size--;
        return lastItem;
    }
    public Item get(int index) {
        return items[(startIndex + index) % items.length];
    }
}
