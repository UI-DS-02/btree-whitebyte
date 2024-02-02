package ds;

import exception.EmptyTreeException;
import exception.KeyNotFoundException;

import java.util.ArrayList;

interface BPTree<K, V> {
    Entry<K, V> insert(K key, V value);
    Entry<K, V> delete(K key) throws EmptyTreeException, KeyNotFoundException;
    V search(K key);
    ArrayList<V> search(K lowerBound, K upperBound);
}
