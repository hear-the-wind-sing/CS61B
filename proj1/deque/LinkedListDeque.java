package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{
      private class TNode{
            public T item;
            public TNode next;
            public  TNode prev;
            public TNode(T i, TNode n,TNode p) {
                  item = i;
                  next = n;
                  prev = p;
            }
            public T get(int index) {
                  if (index == 0) {
                        return item;
                  }
                  return this.next.get(index - 1);
            }
      }

      private class LinkedListDequeIterator implements Iterator<T> {
            private  TNode pointer;
            public LinkedListDequeIterator(){
                  pointer = sentinal.next;
            }

            public boolean hasNext() {
                  return pointer != sentinal;
            }

            public T next() {
                  T returnItem = pointer.item;
                  pointer = pointer.next;
                  return returnItem;
            }
      }

      private int size;
      private  TNode sentinal;

      public Iterator<T> iterator() {
            return new LinkedListDequeIterator();
      }

      public LinkedListDeque() {
            sentinal = new TNode(null,null,null);
            sentinal.prev = sentinal;
            sentinal.next = sentinal;
            size = 0;
      }

      public int size () {
            return size;
      }

//      public boolean isEmpty() {
//            if (size == 0) {
//                  return true;
//            } else {
//                  return false;
//            }
//      }

      public void printDeque() {
            TNode p = sentinal.next;
            while (p != sentinal) {
                  System.out.print(p.item + " ");
                  p = p.next;
            }
            System.out.println(" ");
      }

      public void addFirst(T t) {
            sentinal.next = new TNode(t, sentinal.next, sentinal);
            sentinal.next.next.prev = sentinal.next;
            size++;
      }

      public void addLast(T t) {
            sentinal.prev = new TNode(t, sentinal, sentinal.prev);
            sentinal.prev.prev.next = sentinal.prev;
            size++;
      }

      public T removeFirst() {
            if (size == 0) {
                  return null;
            }

            T firstT = sentinal.next.item;

            sentinal.next = sentinal.next.next;
            sentinal.next.prev = sentinal;
            size--;

            return firstT;
      }

      public T removeLast() {
            if (size == 0) {
                  return null;
            }

            T lastT = sentinal.prev.item;

            sentinal.prev = sentinal.prev.prev;
            sentinal.prev.next = sentinal;
            size--;

            return lastT;
      }

      public T get(int index) {
            if (index > size - 1 || index < 0) {
                  return null;
            }
            int curIndex = 0;
            TNode p = sentinal.next;
            while(curIndex != index) {
                  p = p.next;
                  curIndex++;
            }
            return p.item;
      }

      public T getRecursive(int index) {
            if (index > size - 1 || index < 0) {
                  return null;
            }
            TNode firstNode = sentinal.next;
            return firstNode.get(index);
      }

      @Override
      public boolean equals(Object o) {
            if (o == null) {
                  return false;
            }
            if (o == this) {
                  return true;
            }
            if (!(o instanceof LinkedListDeque)) {
                  return false;
            }
            LinkedListDeque<T> lld = (LinkedListDeque<T>) o;
            if (lld.size() != size) {
                  return false;
            }
            for (int i = 0; i < size; i++) {
                  if (lld.get(i) != get(i)) {
                        return false;
                  }
            }
            return true;
      }
}
