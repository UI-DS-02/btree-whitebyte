package ds;

import java.util.Comparator;

abstract class AbstractBPTree<K, V> implements BPTree<K, V> {

    private final Comparator<Entry<K, V>> comp;

    protected AbstractBPTree(Comparator<K> c) {
        comp = new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return c.compare(o1.getKey(), o2.getKey());
            }
        };
    }

    public Comparator<Entry<K, V>> getComp() {
        return comp;
    }

}
