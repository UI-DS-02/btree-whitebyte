package ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class ArrayBPTree<K, V> extends AbstractBPTree<K, V> {

    int m;
    InternalNode root;
    LeafNode firstLeaf;

    /**
     * Constructor
     * @param m: the order (fanout) of the B+ tree
     */
    public ArrayBPTree(int m, Comparator<K> comp) {
        super(comp);
        this.m = m;
        this.root = null;
    }


    /**
     * This class represents a general node within the B+ tree and serves as a
     * superclass of InternalNode and LeafNode.
     */
    public class Node {
        InternalNode parent;
    }

    /**
     * This class represents the internal nodes within the B+ tree that traffic
     * all search/insert/delete operations. An internal node only holds keys; it
     * does not hold dictionary pairs.
     */
    private class InternalNode extends Node {
        int maxDegree;
        int minDegree;
        int degree;
        InternalNode leftSibling;
        InternalNode rightSibling;
        Integer[] keys;
        Node[] childPointers;

        /**
         * This method appends 'pointer' to the end of the childPointers
         * instance variable of the InternalNode object. The pointer can point to
         * an InternalNode object or a LeafNode object since the formal
         * parameter specifies a Node object.
         * @param pointer: Node pointer that is to be appended to the
         *                    childPointers list
         */
        private void appendChildPointer(Node pointer) {
            this.childPointers[degree] = pointer;
            this.degree++;
        }

        /**
         * Given a Node pointer, this method will return the index of where the
         * pointer lies within the childPointers instance variable. If the pointer
         * can't be found, the method returns -1.
         * @param pointer: a Node pointer that may lie within the childPointers
         *                     instance variable
         * @return the index of 'pointer' within childPointers, or -1 if
         * 'pointer' can't be found
         */
        private int findIndexOfPointer(Node pointer) {
            for (int i = 0; i < childPointers.length; i++) {
                if (childPointers[i] == pointer) { return i; }
            }
            return -1;
        }

        /**
         * Given a pointer to a Node object and an integer index, this method
         * inserts the pointer at the specified index within the childPointers
         * instance variable. As a result of the insert, some pointers may be
         * shifted to the right of the index.
         * @param pointer: the Node pointer to be inserted
         * @param index: the index at which the insert is to take place
         */
        private void insertChildPointer(Node pointer, int index) {
            for (int i = degree - 1; i >= index ;i--) {
                childPointers[i + 1] = childPointers[i];
            }
            this.childPointers[index] = pointer;
            this.degree++;
        }

        /**
         * This simple method determines if the InternalNode is deficient or not.
         * An InternalNode is deficient when its current degree of children falls
         * below the allowed minimum.
         * @return a boolean indicating whether the InternalNode is deficient
         * or not
         */
        private boolean isDeficient() {
            return this.degree < this.minDegree;
        }

        /**
         * This simple method determines if the InternalNode is capable of
         * lending one of its dictionary pairs to a deficient node. An InternalNode
         * can give away a dictionary pair if its current degree is above the
         * specified minimum.
         * @return a boolean indicating whether or not the InternalNode has
         * enough dictionary pairs in order to give one away.
         */
        private boolean isLendable() { return this.degree > this.minDegree; }

        /**
         * This simple method determines if the InternalNode is capable of being
         * merged with. An InternalNode can be merged with if it has the minimum
         * degree of children.
         * @return a boolean indicating whether or not the InternalNode can be
         * merged with
         */
        private boolean isMergeable() { return this.degree == this.minDegree; }

        /**
         * This simple method determines if the InternalNode is considered overfull,
         * i.e. the InternalNode object's current degree is one more than the
         * specified maximum.
         * @return a boolean indicating if the InternalNode is overfull
         */
        private boolean isOverfull() {
            return this.degree == maxDegree + 1;
        }

        /**
         * Given a pointer to a Node object, this method inserts the pointer to
         * the beginning of the childPointers instance variable.
         * @param pointer: the Node object to be prepended within childPointers
         */
        private void prependChildPointer(Node pointer) {
            for (int i = degree - 1; i >= 0 ;i--) {
                childPointers[i + 1] = childPointers[i];
            }
            this.childPointers[0] = pointer;
            this.degree++;
        }

        /**
         * This method sets keys[index] to null. This method is used within the
         * parent of a merging, deficient LeafNode.
         * @param index: the location within keys to be set to null
         */
        private void removeKey(int index) { this.keys[index] = null; }

        /**
         * This method sets childPointers[index] to null and additionally
         * decrements the current degree of the InternalNode.
         * @param index: the location within childPointers to be set to null
         */
        private void removePointer(int index) {
            this.childPointers[index] = null;
            this.degree--;
        }

        /**
         * This method removes 'pointer' from the childPointers instance
         * variable and decrements the current degree of the InternalNode. The
         * index where the pointer node was assigned is set to null.
         * @param pointer: the Node pointer to be removed from childPointers
         */
        private void removePointer(Node pointer) {
            for (int i = 0; i < childPointers.length; i++) {
                if (childPointers[i] == pointer) { this.childPointers[i] = null; }
            }
            this.degree--;
        }

        /**
         * Constructor
         * @param m: the max degree of the InternalNode
         * @param keys: the list of keys that InternalNode is initialized with
         */
        private InternalNode(int m, Integer[] keys) {
            this.maxDegree = m;
            this.minDegree = (int)Math.ceil(m/2.0);
            this.degree = 0;
            this.keys = keys;
            //this.childPointers = new Node[this.maxDegree+1];
        }

        /**
         * Constructor
         * @param m: the max degree of the InternalNode
         * @param keys: the list of keys that InternalNode is initialized with
         * @param pointers: the list of pointers that InternalNode is initialized with
         */
        private InternalNode(int m, Integer[] keys, Node[] pointers) {
            this.maxDegree = m;
            this.minDegree = (int)Math.ceil(m/2.0);
            this.degree = linearNullSearch(pointers);
            this.keys = keys;
            this.childPointers = pointers;
        }
    }

    /**
     * This class represents the leaf nodes within the B+ tree that hold
     * dictionary pairs. The leaf node has no children. The leaf node has a
     * minimum and maximum number of dictionary pairs it can hold, as specified
     * by m, the max degree of the B+ tree. The leaf nodes form a doubly linked
     * list that, i.e. each leaf node has a left and right sibling*/
    public class LeafNode extends Node {


        /**
         * This class represents a dictionary pair that is to be contained within the
         * leaf nodes of the B+ tree. The class implements the Comparable interface
         * so that the DictionaryPair objects can be sorted later on.
         */
        protected static class DictionaryPair<K, V> implements Entry<K, V> {
            private K k;
            private V v;

            /**
             * Constructor
             * @param key: the key of the key-value pair
             * @param value: the value of the key-value pair
             */
            public DictionaryPair(K key, V value) {
                k = key;
                v = value;
            }

            @Override
            public K getKey() {
                return k;
            }

            @Override
            public V getValue() {
                return v;
            }

            public void setK(K k) {
                this.k = k;
            }

            public void setV(V v) {
                this.v = v;
            }

        }

        int maxNumPairs;
        int minNumPairs;
        int numPairs;
        LeafNode leftSibling;
        LeafNode rightSibling;
        DictionaryPair<K, V>[] dictionary;

        /**
         * Given an index, this method sets the dictionary pair at that index
         * within the dictionary to null.
         * @param index: the location within the dictionary to be set to null
         */
        public void delete(int index) {

            // Delete dictionary pair from leaf
            this.dictionary[index] = null;

            // Decrement numPairs
            numPairs--;
        }

        /**
         * This method attempts to insert a dictionary pair within the dictionary
         * of the LeafNode object. If it succeeds, numPairs increments, the
         * dictionary is sorted, and the boolean true is returned. If the method
         * fails, the boolean false is returned.
         * @param dp: the dictionary pair to be inserted
         * @return a boolean indicating whether or not the insert was successful
         */
        public boolean insert(Entry<K, V> dp) {
            if (this.isFull()) {

                /* Flow of execution goes here when numPairs == maxNumPairs */

                return false;
            } else {

                // Insert dictionary pair, increment numPairs, sort dictionary
                this.dictionary[numPairs] = (DictionaryPair<K, V>) dp;
                numPairs++;
                Arrays.sort(this.dictionary, 0, numPairs);

                return true;
            }
        }


        /**
         * This simple method determines if the LeafNode is deficient, i.e.
         * the numPairs within the LeafNode object is below minNumPairs.
         * @return a boolean indicating whether or not the LeafNode is deficient
         */
        public boolean isDeficient() { return numPairs < minNumPairs; }

        /**
         * This simple method determines if the LeafNode is full, i.e. the
         * numPairs within the LeafNode is equal to the maximum number of pairs.
         * @return a boolean indicating whether or not the LeafNode is full
         */
        public boolean isFull() { return numPairs == maxNumPairs; }

        /**
         * This simple method determines if the LeafNode object is capable of
         * lending a dictionary pair to a deficient leaf node. The LeafNode
         * object can lend a dictionary pair if its numPairs is greater than
         * the minimum number of pairs it can hold.
         * @return a boolean indicating whether or not the LeafNode object can
         * give a dictionary pair to a deficient leaf node
         */
        public boolean isLendable() { return numPairs > minNumPairs; }

        /**
         * This simple method determines if the LeafNode object is capable of
         * being merged with, which occurs when the number of pairs within the
         * LeafNode object is equal to the minimum number of pairs it can hold.
         * @return a boolean indicating whether or not the LeafNode object can
         * be merged with
         */
        public boolean isMergeable() {
            return numPairs == minNumPairs;
        }

        /**
         * Constructor
         * @param m: order of B+ tree that is used to calculate maxNumPairs and
         *           minNumPairs
         * @param dp: first dictionary pair insert into new node
         */
        public LeafNode(int m, Entry<K, V> dp) {
            this.maxNumPairs = m - 1;
            this.minNumPairs = (int)(Math.ceil(m/2.0) - 1);
            this.dictionary = new DictionaryPair[m];
            this.numPairs = 0;
            this.insert(dp);
        }

        /**
         * Constructor
         * @param dps: list of DictionaryPair objects to be immediately inserted
         *             into new LeafNode object
         * @param m: order of B+ tree that is used to calculate maxNumPairs and
         * 		     minNumPairs
         * @param parent: parent of newly created child LeafNode
         */
        public LeafNode(int m, Entry<K, V>[] dps, InternalNode parent) {
            this.maxNumPairs = m - 1;
            this.minNumPairs = (int)(Math.ceil(m/2.0) - 1);
            this.dictionary = (DictionaryPair<K, V>[]) dps;
            this.numPairs = linearNullSearch(dps);
            this.parent = parent;
        }
    }

    /**
     * This method performs a standard linear search on a sorted
     * DictionaryPair[] and returns the index of the first null entry found.
     * Otherwise, this method returns a -1. This method is primarily used in
     * place of binarySearch() when the target t = null.
     * @param dps: list of dictionary pairs sorted by key within leaf node
     * @return index of the target value if found, else -1
     */
    private int linearNullSearch(Entry<K, V>[] dps) {
        for (int i = 0; i <  dps.length; i++) {
            if (dps[i] == null) { return i; }
        }
        return -1;
    }
    /**
     * This method performs a standard linear search on a list of Node[] pointers
     * and returns the index of the first null entry found. Otherwise, this
     * method returns a -1. This method is primarily used in place of
     * binarySearch() when the target t = null.
     * @param pointers: list of Node[] pointers
     * @return index of the target value if found, else -1
     */
    private int linearNullSearch(Node[] pointers) {
        for (int i = 0; i <  pointers.length; i++) {
            if (pointers[i] == null) { return i; }
        }
        return -1;
    }

    /*~~~~~~~~~~~~~~~~ API: DELETE, INSERT, SEARCH ~~~~~~~~~~~~~~~~*/



    @Override
    public Entry<K, V> insert(K key, V value) {
        return null;
    }

    @Override
    public Entry<K, V> delete(K key) {
        return null;
    }

    @Override
    public V search(K key) {
        return null;
    }

    @Override
    public ArrayList<V> search(K lowerBound, K upperBound) {
        return null;
    }


    public static void main(String[] args) {
    }
}
