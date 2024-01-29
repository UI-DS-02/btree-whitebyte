package ds;

import java.util.ArrayList;

interface BPTree<K, V> {
    Entry<K, V> insert(K key, V value);
    Entry<K, V> delete(K key);
    V search(K key);
    ArrayList<V> search(K lowerBound, K upperBound);
}
