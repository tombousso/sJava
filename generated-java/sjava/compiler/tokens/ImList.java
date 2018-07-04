package sjava.compiler.tokens;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ImList<T> extends AbstractList<T> {
    public static ImList EMPTY_LIST;
    List<T> l;
    int i;
    int sz;

    static {
        EMPTY_LIST = new ImList(Collections.EMPTY_LIST);
    }

    public ImList(List<T> l, int i, int sz) {
        this.l = Collections.unmodifiableList(l);
        this.i = i;
        this.sz = sz;
        if(i < 0 || sz < 0 || i + sz > l.size()) {
            throw new RuntimeException();
        }
    }

    public ImList(List<T> l, int i) {
        this(l, i, l.size());
    }

    public ImList(List<T> l) {
        this(l, 0);
    }

    public ImList<T> skip(int k) {
        return new ImList(this.l, this.i + k, this.sz - k);
    }

    public ImList<T> take(int k) {
        return new ImList(this.l, this.i, k);
    }

    public ImList<T> skipLast(int k) {
        return new ImList(this.l, this.i, this.sz - k);
    }

    public T get(int i) {
        if(i >= 0 && i < this.sz) {
            return this.l.get(this.i + i);
        } else {
            throw new RuntimeException();
        }
    }

    public ImList<T> update(int i, T el) {
        if(i >= 0 && i < this.sz) {
            ArrayList l = new ArrayList(this.l);
            l.set(this.i + i, el);
            return new ImList(l, this.i, this.sz);
        } else {
            throw new RuntimeException();
        }
    }

    public int size() {
        return this.sz;
    }

    public Iterator<T> iterator() {
        return this.l.subList(this.i, this.i + this.sz).iterator();
    }
}
