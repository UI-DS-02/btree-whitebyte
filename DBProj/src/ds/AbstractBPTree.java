package ds;

import java.util.Comparator;

abstract class AbstractBPTree<K, V> implements BPTree<K, V> {

    private final Comparator<Entry<K, V>> comp;
    private final Comparator<K> keyComp;

    protected AbstractBPTree(Comparator<K> c) {
        comp = new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                if (o1==null&&o2==null) return 0;
                else if (o1==null && o2!=null) return -1;
                else if (o1!=null && o2==null) return 1;
                return c.compare(o1.getKey(), o2.getKey());
            }
        };
        keyComp = new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                if (o1==null||o2==null) return 0;
                return c.compare(o1, o2);
            }
        };
    }

    public Comparator<Entry<K, V>> getComp() {
        return comp;
    }

    public Comparator<K> getKeyComp() {
        return keyComp;
    }
}
