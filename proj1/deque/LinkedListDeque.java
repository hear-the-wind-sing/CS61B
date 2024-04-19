package deque;

public class LinkedListDeque<Item> {

      private class itemNode {
            public Item item;
            public itemNode next;
            public  itemNode prev;
            public itemNode(Item i, itemNode n,itemNode p) {
                  item = i;
                  next = n;
                  prev = p;
            }
      }
      private int size;
      private  itemNode sentinal;

      public LinkedListDeque() {
            sentinal = new itemNode(null,null,null);
            sentinal.prev = sentinal;
            sentinal.next = sentinal;
            size = 0;
      }

      public int size () {
            return size;
      }

      public boolean isEmpty() {
            if (size == 0) {
                  return true;
            } else {
                  return false;
            }
      }

      public void printDeque() {
            itemNode p = sentinal.next;
            while(p != sentinal) {
                  System.out.print(p.item + " ");
                  p = p.next;
            }
            System.out.println(" ");
      }

      public void addFirst(Item item) {
            sentinal.next = new itemNode(item, sentinal.next, sentinal);
            sentinal.next.next.prev = sentinal.next;
            size++;
      }

      public void addLast(Item item) {
            sentinal.prev = new itemNode(item, sentinal, sentinal.prev);
            sentinal.prev.prev.next = sentinal.prev;
            size++;
      }

      public Item removeFirst() {
            if (size == 0) {
                  return null;
            }

            Item firstItem = sentinal.next.item;

            sentinal.next = sentinal.next.next;
            sentinal.next.prev = sentinal;
            size--;

            return firstItem;
      }

      public Item removeLast() {
            if (size == 0) {
                  return null;
            }

            Item lastItem = sentinal.prev.item;

            sentinal.prev = sentinal.prev.prev;
            sentinal.prev.next = sentinal;
            size--;

            return lastItem;
      }

      public Item getIndex(int index){
            if (index > size - 1 || index < 0) {
                  return null;
            }
            int curIndex = 0;
            itemNode p = sentinal.next;
            while(curIndex != index) {
                  p = p.next;
                  curIndex++;
            }
            return p.item;
      }
}
