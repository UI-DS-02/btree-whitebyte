package ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Array;

class ArrayBPTree<K, V> extends AbstractBPTree<K, V> {
    private int m;
    private InternalNode root;
    private LeafNode firstLeaf;
    private K key;

    /**
     * Constructor
     * @param m: the order (fanout) of the B+ tree
     */
    public ArrayBPTree(int m, Comparator<K> comp,K key) {
        super(comp);
        this.m = m;
        this.root = null;
        this.key=key;
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
        K[] keys;
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
        private InternalNode(int m, K[] keys) {
            this.maxDegree = m;
            this.minDegree = (int)Math.ceil(m/2.0);
            this.degree = 0;
            this.keys = keys;
            this.childPointers = (Node[]) Array.newInstance(new Node().getClass(), this.maxDegree + 1);
        }

        /**
         * Constructor
         * @param m: the max degree of the InternalNode
         * @param keys: the list of keys that InternalNode is initialized with
         * @param pointers: the list of pointers that InternalNode is initialized with
         */
        private InternalNode(int m, K[] keys, Node[] pointers) {
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
//                    (DictionaryPair[])Array.newInstance(new DictionaryPair<>(key,value).getClass(),m);
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

    /*~~~~~~~~~~~~~~~~ HELPER FUNCTIONS ~~~~~~~~~~~~~~~~*/

    /**
     * This method performs a standard binary search on a sorted
     * DictionaryPair[] and returns the index of the dictionary pair
     * with target key t if found. Otherwise, this method returns a negative
     * value.
     * @param dps: list of dictionary pairs sorted by key within leaf node
     * @param t: target key value of dictionary pair being searched for
     * @return index of the target value if found, else a negative value
     */
    private int binarySearch(Entry<K,V>[] dps, int numPairs, K t) {
        return Arrays.binarySearch(dps, 0, numPairs, dps[0], this.getComp());
    }

    /**
     * This method starts at the root of the B+ tree and traverses down the
     * tree via key comparisons to the corresponding leaf node that holds 'key'
     * within its dictionary.
     * @param key: the unique key that lies within the dictionary of a LeafNode object
     * @return the LeafNode object that contains the key within its dictionary
     */
    private LeafNode findLeafNode(K key) {

        // Initialize keys and index variable
        K[] keys = this.root.keys;
        int i;

        // Find next node on path to appropriate leaf node
        for (i = 0; i < this.root.degree - 1; i++) {
            if (getKeyComp().compare(key,keys[i])<0) { break; }
        }

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
        Node child = this.root.childPointers[i];
        if (child instanceof LeafNode) {
            return (LeafNode)child;
        } else {
            return findLeafNode((InternalNode)child, key);
        }
    }

    private LeafNode findLeafNode(InternalNode node, K key) {

        // Initialize keys and index variable
        K[] keys = node.keys;
        int i;

        // Find next node on path to appropriate leaf node
        for (i = 0; i < node.degree - 1; i++) {
            if (getKeyComp().compare(key,keys[i])<0) { break; }
        }

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
        Node childNode = node.childPointers[i];
        if (childNode instanceof LeafNode) {
            return (LeafNode)childNode;
        } else {
            return findLeafNode((InternalNode)node.childPointers[i], key);
        }
    }

    /**
     * Given a list of pointers to Node objects, this method returns the index of
     * the pointer that points to the specified 'node' LeafNode object.
     * @param pointers: a list of pointers to Node objects
     * @param node: a specific pointer to a LeafNode
     * @return (int) index of pointer in list of pointers
     */
    private int findIndexOfPointer(Node[] pointers, LeafNode node) {
        int i;
        for (i = 0; i < pointers.length; i++) {
            if (pointers[i] == node) { break; }
        }
        return i;
    }

    /**
     * This is a simple method that returns the midpoint (or lower bound
     * depending on the context of the method invocation) of the max degree m of
     * the B+ tree.
     * @return (int) midpoint/lower bound
     */
    private int getMidpoint() {
        return (int)Math.ceil((this.m + 1) / 2.0) - 1;
    }

    /**
     * Given a deficient InternalNode in, this method remedies the deficiency
     * through borrowing and merging.
     * @param in: a deficient InternalNode
     */
    private void handleDeficiency(InternalNode in) {

        InternalNode sibling;
        InternalNode parent = in.parent;

        // Remedy deficient root node
        if (this.root == in) {
            for (int i = 0; i < in.childPointers.length; i++) {
                if (in.childPointers[i] != null) {
                    if (in.childPointers[i] instanceof InternalNode) {
                        this.root = (InternalNode)in.childPointers[i];
                        this.root.parent = null;
                    } else if (in.childPointers[i] instanceof LeafNode) {
                        this.root = null;
                    }
                }
            }
        }

        // Borrow:
        else if (in.leftSibling != null && in.leftSibling.isLendable()) {
            sibling = in.leftSibling;
        } else if (in.rightSibling != null && in.rightSibling.isLendable()) {
            sibling = in.rightSibling;

            // Copy 1 key and pointer from sibling (atm just 1 key)
            K borrowedKey = sibling.keys[0];
            Node pointer = sibling.childPointers[0];

            // Copy root key and pointer into parent
            in.keys[in.degree - 1] = parent.keys[0];
            in.childPointers[in.degree] = pointer;

            // Copy borrowedKey into root
            parent.keys[0] = borrowedKey;

            // Delete key and pointer from sibling
            sibling.removePointer(0);
            Arrays.sort(sibling.keys);
            sibling.removePointer(0);
            shiftDown(in.childPointers, 1);
        }

        // Merge:
        else if (in.leftSibling != null && in.leftSibling.isMergeable()) {

        } else if (in.rightSibling != null && in.rightSibling.isMergeable()) {
            sibling = in.rightSibling;

            // Copy rightmost key in parent to beginning of sibling's keys &
            // delete key from parent
            sibling.keys[sibling.degree - 1] = parent.keys[parent.degree - 2];
            Arrays.sort(sibling.keys, 0, sibling.degree);
            parent.keys[parent.degree - 2] = null;

            // Copy in's child pointer over to sibling's list of child pointers
            for (int i = 0; i < in.childPointers.length; i++) {
                if (in.childPointers[i] != null) {
                    sibling.prependChildPointer(in.childPointers[i]);
                    in.childPointers[i].parent = sibling;
                    in.removePointer(i);
                }
            }

            // Delete child pointer from grandparent to deficient node
            parent.removePointer(in);

            // Remove left sibling
            sibling.leftSibling = in.leftSibling;
        }

        // Handle deficiency a level up if it exists
        if (parent != null && parent.isDeficient()) {
            handleDeficiency(parent);
        }
    }

    /**
     * This is a simple method that determines if the B+ tree is empty or not.
     * @return a boolean indicating if the B+ tree is empty or not
     */
    private boolean isEmpty() {
        return firstLeaf == null;
    }

    /**
     * This method is used to shift down a set of pointers that are prepended
     * by null values.
     * @param pointers: the list of pointers that are to be shifted
     * @param amount: the amount by which the pointers are to be shifted
     */
    private void shiftDown(Node[] pointers, int amount) {
        Node[] newPointers = (Node[]) Array.newInstance(new Node().getClass(),this.m + 1);
        for (int i = amount; i < pointers.length; i++) {
            newPointers[i - amount] = pointers[i];
        }
        pointers = newPointers;
    }

    /**
     * This is a specialized sorting method used upon lists of DictionaryPairs
     * that may contain interspersed null values.
     * @param dictionary: a list of DictionaryPair objects
     */
    private void sortDictionary(Entry<K,V>[] dictionary) {
        Arrays.sort(dictionary, new Comparator<Entry<K,V>>() {
            @Override
            public int compare(Entry<K,V> o1, Entry<K,V> o2) {
                if (o1 == null && o2 == null) { return 0; }
                if (o1 == null) { return 1; }
                if (o2 == null) { return -1; }
                return getComp().compare(o1,o2);
            }
        });
    }

    /**
     * This method modifies the InternalNode 'in' by removing all pointers within
     * the childPointers after the specified split. The method returns the removed
     * pointers in a list of their own to be used when constructing a new
     * InternalNode sibling.
     * @param in: an InternalNode whose childPointers will be split
     * @param split: the index at which the split in the childPointers begins
     * @return a Node[] of the removed pointers
     */
    private Node[] splitChildPointers(InternalNode in, int split) {

        Node[] pointers = in.childPointers;
        Node[] halfPointers = (Node[])Array.newInstance(new Node().getClass(),this.m + 1);

        // Copy half of the values into halfPointers while updating original keys
        for (int i = split + 1; i < pointers.length; i++) {
            halfPointers[i - split - 1] = pointers[i];
            in.removePointer(i);
        }

        return halfPointers;
    }

    /**
     * This method splits a single dictionary into two dictionaries where all
     * dictionaries are of equal length, but each of the resulting dictionaries
     * holds half of the original dictionary's non-null values. This method is
     * primarily used when splitting a node within the B+ tree. The dictionary of
     * the specified LeafNode is modified in place. The method returns the
     * remainder of the DictionaryPairs that are no longer within ln's dictionary.
     * @param ln: list of DictionaryPairs to be split
     * @param split: the index at which the split occurs
     * @return DictionaryPair[] of the two split dictionaries
     */
    private Entry<K,V>[] splitDictionary(LeafNode ln, int split) {

        Entry<K,V>[] dictionary = ln.dictionary;

		/* Initialize two dictionaries that each hold half of the original
		   dictionary values */
        Entry[] halfDict =new Entry[this.m];

        // Copy half of the values into halfDict
        for (int i = split; i < dictionary.length; i++) {
            halfDict[i - split] = dictionary[i];
            ln.delete(i);
        }

        return halfDict;
    }

    /**
     * When an insertion into the B+ tree causes an overfull node, this method
     * is called to remedy the issue, i.e. to split the overfull node. This method
     * calls the sub-methods of splitKeys() and splitChildPointers() in order to
     * split the overfull node.
     * @param in: an overfull InternalNode that is to be split
     */
    private void splitInternalNode(InternalNode in) {

        // Acquire parent
        InternalNode parent = in.parent;

        // Split keys and pointers in half
        int midpoint = getMidpoint();
        K newParentKey = in.keys[midpoint];
        K[] halfKeys = splitKeys(in.keys, midpoint);
        Node[] halfPointers = splitChildPointers(in, midpoint);

        // Change degree of original InternalNode in
        in.degree = linearNullSearch(in.childPointers);

        // Create new sibling internal node and add half of keys and pointers
        InternalNode sibling = new InternalNode(this.m, halfKeys, halfPointers);
        for (Node pointer : halfPointers) {
            if (pointer != null) { pointer.parent = sibling; }
        }

        // Make internal nodes siblings of one another
        sibling.rightSibling = in.rightSibling;
        if (sibling.rightSibling != null) {
            sibling.rightSibling.leftSibling = sibling;
        }
        in.rightSibling = sibling;
        sibling.leftSibling = in;

        if (parent == null) {

            // Create new root node and add midpoint key and pointers
            K[] keys = (K[])Array.newInstance(key.getClass(),this.m);
            keys[0] = newParentKey;
            InternalNode newRoot = new InternalNode(this.m, keys);
            newRoot.appendChildPointer(in);
            newRoot.appendChildPointer(sibling);
            this.root = newRoot;

            // Add pointers from children to parent
            in.parent = newRoot;
            sibling.parent = newRoot;

        } else {

            // Add key to parent
            parent.keys[parent.degree - 1] = newParentKey;
            Arrays.sort(parent.keys, 0, parent.degree);

            // Set up pointer to new sibling
            int pointerIndex = parent.findIndexOfPointer(in) + 1;
            parent.insertChildPointer(sibling, pointerIndex);
            sibling.parent = parent;
        }
    }

    /**
     * This method modifies a list of Integer-typed objects that represent keys
     * by removing half of the keys and returning them in a separate Integer[].
     * This method is used when splitting an InternalNode object.
     * @param keys: a list of Integer objects
     * @param split: the index where the split is to occur
     * @return Integer[] of removed keys
     */
    private K[] splitKeys(K[] keys, int split) {

        K[] halfKeys = (K[])Array.newInstance(key.getClass(),this.m);

        // Remove split-indexed value from keys
        keys[split] = null;

        // Copy half of the values into halfKeys while updating original keys
        for (int i = split + 1; i < keys.length; i++) {
            halfKeys[i - split - 1] = keys[i];
            keys[i] = null;
        }

        return halfKeys;
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
