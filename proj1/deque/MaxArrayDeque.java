package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), maxItem) > 0) {
                maxItem = get(i);
            }
        }
        return maxItem;
    }

    //    @Override
    //    public boolean equals(Object o) {
    //        if (o == null) {
    //            return false;
    //        }
    //        if (o == this) {
    //            return true;
    //        }
    //        if (!(o instanceof MaxArrayDeque)) {
    //            return false;
    //        }
    //        if (((MaxArrayDeque<T>)o).max() != max()) {
    //            return false;
    //        }
    //        return super.equals(o);
    //    }
}
