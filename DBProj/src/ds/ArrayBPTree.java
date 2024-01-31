package ds;

import db.Record;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ArrayBPTree<K, V> extends AbstractBPTree<K, V> {

    private static class Node<K, V> {

        int maxDegree;
        int minDegree;
        int midPoint;
        int degree;

        Node<K, V> leftSibling;
        Node<K, V> rightSibling;
        InternalNode<K, V> parent;

        public Node(int max, int min, int dg) {
            maxDegree = max;
            minDegree = min;
            midPoint =  (int) Math.ceil((this.maxDegree + 1) / 2.0) - 1;
            degree = dg;
        }

    }

    private static class InternalNode<K, V> extends Node<K, V> {

        K[] keys;
        Node<K, V>[] childPointers;


        private InternalNode(int m, K[] keys) {
            super(m, (int) Math.ceil(m / 2.0), 0);
            this.keys = keys;
            this.childPointers = (Node[]) Array.newInstance(Node.class, this.maxDegree + 1);;
            //this.childPointers = (Node[]) Array.newInstance(new ArrayBPTree.Node().getClass(), this.maxDegree + 1);
        }


        private InternalNode(int m, K[] keys, Node<K, V>[] pointers, int dg) {
            super(m, (int) Math.ceil(m / 2.0), dg);
            this.keys = keys;
            this.childPointers = pointers;
        }

        private void appendChildPointer(Node<K, V> pointer) {
            this.childPointers[degree] = pointer;
            this.degree++;
        }

        private int findIndexOfPointer(Node<K, V> pointer) {
            for (int i = 0; i < childPointers.length; i++) {
                if (childPointers[i] == pointer) {
                    return i;
                }
            }
            return -1;
        }

        private void insertChildPointer(Node<K, V> pointer, int index) {
            for (int i = degree - 1; i >= index; i--) {
                childPointers[i + 1] = childPointers[i];
            }
            this.childPointers[index] = pointer;
            this.degree++;
        }

        private boolean isOverfull() {
            return this.degree == maxDegree + 1;
        }


        private void removePointer(int index) {
            this.childPointers[index] = null;
            this.degree--;
        }

        private void removeKey(int index) { this.keys[index] = null; }

        private void removePointer(Node<K, V> pointer) {
            for (int i = 0; i < childPointers.length; i++) {
                if (childPointers[i] == pointer) { this.childPointers[i] = null; }
            }
            this.degree--;
        }

        private boolean isDeficient() {
            return this.degree < this.minDegree;
        }

        private boolean isLendable() { return this.degree > this.minDegree; }

        private boolean isMergeable() { return this.degree == this.minDegree; }

        private void prependChildPointer(Node<K, V> pointer) {
            for (int i = degree - 1; i >= 0 ;i--) {
                childPointers[i + 1] = childPointers[i];
            }
            this.childPointers[0] = pointer;
            this.degree++;
        }
    }

    public static class ExternalNode<K, V> extends Node<K, V> {

        protected static class MapEntry<K, V> implements Entry<K, V> {   //

            private K k;
            private V v;

            public MapEntry(K key, V value) {
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

        MapEntry<K, V>[] map;
        private Comparator<Entry<K, V>> comp;

        public ExternalNode(int m, K k, V v,Comparator<Entry<K, V>> comp) {  // insert dp in map and then pass it here
            super(m - 1, (int) (Math.ceil(m / 2) - 1), 0);
            this.map = new MapEntry[m];
            this.insertExternal(k, v,comp);  //
            this.comp=comp;
        }


        public ExternalNode(int m, Entry<K, V>[] dps, InternalNode<K, V> parent, int dg) {  // calc dg using linear
            super(m - 1, (int) (Math.ceil(m / 2) - 1), dg);
            this.map = (MapEntry<K, V>[]) dps;  //
            this.parent = parent;
        }

        public boolean insertExternal(K k, V v,Comparator<Entry<K, V>> comp) {
            MapEntry<K, V> newEntry = new MapEntry<>(k, v);

            if (this.isFull()) {
                /* Flow of execution goes here when numPairs == maxNumPairs */

                return false;
            } else {
                // Insert dictionary pair, increment numPairs, sort dictionary
                this.map[degree] = newEntry;
                degree++;

                Arrays.sort(this.map, 0, degree,comp);

                return true;
            }
        }

        public boolean isFull() {
            return degree == maxDegree;
        }

        public void insertExternal1(K k, V v, Comparator<Entry<K, V>> comp) {

            // Sort all the dictionary pairs with the included pair to be inserted
            map[degree] = new MapEntry<>(k, v);
            this.degree++;

            this.sortMap(comp);

            // Split the sorted pairs into two halves
            int midpoint = midPoint;

            MapEntry<K, V>[] halfDict = splitDictionary(midpoint);

            if (this.parent == null) {

                /* Flow of execution goes here when there is 1 node in tree */

                // Create internal node to serve as parent, use dictionary midpoint key
                K[] parent_keys = (K[])Array.newInstance(k.getClass(),this.maxDegree+1);  //
                parent_keys[0] = halfDict[0].getKey();
                InternalNode<K, V> parent = new InternalNode<K, V>(this.maxDegree+1, parent_keys);
                this.parent = parent;
                parent.appendChildPointer(this);   //

            } else {

                /* Flow of execution goes here when parent exists */

                // Add new key to parent for proper indexing
                K newParentKey = halfDict[0].getKey();
                this.parent.keys[this.parent.degree - 1] = newParentKey;
                Arrays.sort(this.parent.keys, 0, this.parent.degree);
            }

            // Create new LeafNode that holds the other half
            ExternalNode<K, V> newLeafNode = new ExternalNode<K, V>(this.maxDegree+1, halfDict, this.parent, linearNullSearch(halfDict));


            // Update child pointers of parent node
            int pointerIndex = this.parent.findIndexOfPointer(this) + 1;
            this.parent.insertChildPointer(newLeafNode, pointerIndex);

            // Make leaf nodes siblings of one another
            newLeafNode.rightSibling = this.rightSibling;
            if (newLeafNode.rightSibling != null) {
                newLeafNode.rightSibling.leftSibling = newLeafNode;
            }
            this.rightSibling = newLeafNode;
            newLeafNode.leftSibling = this;
        }


        private void sortMap(Comparator<Entry<K, V>> comp) { //

            Arrays.sort(map, comp);
        }


        private MapEntry<K, V>[] splitDictionary(int split) {

            MapEntry<K, V>[] dictionary = this.map;

		/* Initialize two dictionaries that each hold half of the original
		   dictionary values */
            MapEntry<K, V>[] halfDict = new MapEntry[this.maxDegree+1];

            // Copy half of the values into halfDict
            for (int i = split; i < dictionary.length; i++) {
                halfDict[i - split] = dictionary[i];
                this.delete(i);
            }

            return halfDict;
        }

        public Entry<K, V> delete(int index) {

            Entry<K, V> rem = this.map[index];

            // Delete dictionary pair from leaf
            this.map[index] = null;

            // Decrement numPairs
            degree--;

            return rem;
        }

        public boolean isDeficient() {
            return degree < minDegree;
        }

        public boolean isLendable() {
            return degree > minDegree;
        }

        public void deleteExternal1(Comparator<K> keyComp, ExternalNode<K, V> exn, InternalNode<K, V> root, Comparator<Entry<K, V>> comp) {

            ExternalNode<K, V> sibling;
            InternalNode<K, V> parent = this.parent;

            // Borrow: First, check the left sibling, then the right sibling
            if (this.leftSibling != null &&
                    this.leftSibling.parent == this.parent &&
                    ((ExternalNode<K, V>)(this.leftSibling)).isLendable()) {

                sibling = (ExternalNode<K, V>) this.leftSibling;
                MapEntry<K, V> borrowedDP = sibling.map[sibling.degree - 1];

						/* Insert borrowed dictionary pair, sort dictionary,
						   and delete dictionary pair from sibling */
                this.insertExternal(borrowedDP.getKey(), borrowedDP.getValue(),comp);
                this.sortMap(comp);
                sibling.delete(sibling.degree - 1);

                // Update key in parent if necessary
                int pointerIndex = findIndexOfPointer(parent.childPointers, this);
                if(keyComp.compare(borrowedDP.getKey(), parent.keys[pointerIndex - 1]) < 0) {
                    parent.keys[pointerIndex - 1] = this.map[0].getKey();
                }

            } else if (this.rightSibling != null &&
                    this.rightSibling.parent == this.parent &&
                    ((ExternalNode<K, V>)(this.rightSibling)).isLendable()) {

                sibling = (ExternalNode<K, V>) this.rightSibling;
                MapEntry<K, V> borrowedDP = sibling.map[0];

						/* Insert borrowed dictionary pair, sort dictionary,
					       and delete dictionary pair from sibling */
                this.insertExternal(borrowedDP.getKey(), borrowedDP.getValue(),comp);  /////
                sibling.delete(0);
                sibling.sortMap(comp);

                // Update key in parent if necessary
                int pointerIndex = findIndexOfPointer(parent.childPointers, this);
                if(keyComp.compare(borrowedDP.getKey(), parent.keys[pointerIndex]) >= 0) {
                    parent.keys[pointerIndex] = sibling.map[0].getKey();
                }

            }

            // Merge: First, check the left sibling, then the right sibling
            else if (this.leftSibling != null &&
                    this.leftSibling.parent == this.parent &&
                    ((ExternalNode<K, V>)this.leftSibling).isMergeable()) {

                sibling = (ExternalNode<K, V>) this.leftSibling;
                int pointerIndex = findIndexOfPointer(parent.childPointers, this);

                // Remove key and child pointer from parent
                parent.removeKey(pointerIndex - 1);
                parent.removePointer(this);

                // Update sibling pointer
                sibling.rightSibling = this.rightSibling;

                // Check for deficiencies in parent
                if (parent.isDeficient()) {
                    handleDeficiency(parent, root);
                }

            } else if (this.rightSibling != null &&
                    this.rightSibling.parent == this.parent &&
                    ((ExternalNode<K, V>)this.rightSibling).isMergeable()) {

                sibling = (ExternalNode<K, V>) this.rightSibling;
                int pointerIndex = findIndexOfPointer(parent.childPointers, this);

                // Remove key and child pointer from parent
                parent.removeKey(pointerIndex);
                parent.removePointer(pointerIndex);

                // Update sibling pointer
                sibling.leftSibling = this.leftSibling;
                if (sibling.leftSibling == null) {
                    exn = sibling;
                }

                if (parent.isDeficient()) {
                    handleDeficiency(parent, root);
                }

            }
        }

        private int findIndexOfPointer(Node<K, V>[] pointers, ExternalNode<K, V> node) {
            int i;
            for (i = 0; i < pointers.length; i++) {
                if (pointers[i] == node) { break; }
            }
            return i;
        }

        public boolean isMergeable() {
            return degree == minDegree;
        }

        private void handleDeficiency(InternalNode<K, V> in, InternalNode<K, V> root) {

            InternalNode<K, V> sibling;
            InternalNode<K, V> parent = in.parent;

            // Remedy deficient root node
            if (root == in) {
                for (int i = 0; i < in.childPointers.length; i++) {
                    if (in.childPointers[i] != null) {
                        if (in.childPointers[i] instanceof InternalNode<K, V>) {
                            root = (InternalNode<K, V>) in.childPointers[i];
                            root.parent = null;
                        } else if (in.childPointers[i] instanceof ExternalNode<K, V>) {
                            root = null;
                        }
                    }
                }
            }

            // Borrow:
            else if (in.leftSibling != null && ((InternalNode<K, V>) in.leftSibling).isLendable()) {
                sibling = (InternalNode<K, V>) in.leftSibling;
            } else if (in.rightSibling != null && ((InternalNode<K, V>) in.rightSibling).isLendable()) {
                sibling = (InternalNode<K, V>) in.rightSibling;

                // Copy 1 key and pointer from sibling (atm just 1 key)
                K borrowedKey = sibling.keys[0];
                Node<K, V> pointer = sibling.childPointers[0];

                // Copy root key and pointer into parent
                in.keys[in.degree - 1] = parent.keys[0];
                in.childPointers[in.degree] = pointer;

                // Copy borrowedKey into root
                parent.keys[0] = borrowedKey;

                // Delete key and pointer from sibling
                sibling.removePointer(0);
                Arrays.sort(sibling.keys);
                sibling.removePointer(0);
                shiftDown(in.childPointers);
            }

            // Merge:
            else if (in.leftSibling != null && ((InternalNode<K, V>) in.leftSibling).isMergeable()) {

            } else if (in.rightSibling != null && ((InternalNode<K, V>) in.rightSibling).isMergeable()) {
                sibling = (InternalNode<K, V>) in.rightSibling;

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
                handleDeficiency(parent, root);
            }

        }
        private void shiftDown(Node<K, V>[] pointers) {  //
            Node<K, V>[] newPointers = new Node[this.maxDegree + 1];
            for (int i = 1; i < pointers.length; i++) {
                newPointers[i - 1] = pointers[i];
            }
            pointers = newPointers;
        }

        private int linearNullSearch(MapEntry<K, V>[] dps) {
            for (int i = 0; i <  dps.length; i++) {
                if (dps[i] == null) { return i; }
            }
            return -1;
        }

        public Entry<K, V> findEntry(K k, V v) {
            for (int i=0; i< map.length; i++) {
                if (map[i]!=null){
                    if(map[i].getKey() == k && map[i].getValue() == v) {
                        return map[i];
                    }
                }
            } return null;
        }
        // is full & is lendable & .. can be here
    }
    private int m;
    private InternalNode<K, V> root;
    private ExternalNode<K, V> firstEx;
    private K key;
    public ArrayBPTree(int m, K k, Comparator<K> comp) {
        super(comp);
        this.m = m;
        this.root = null;
        this.key = k;
    }
    @Override
    public V search(K key) {

        // If B+ tree is completely empty, simply return null
        if (isEmpty()) {
            return null;
        }

        // Find leaf node that holds the dictionary key
        ExternalNode<K, V> ln = (this.root == null) ? this.firstEx : findExternalNode(key);

        // Perform binary search to find index of key within dictionary
        Entry<K, V>[] dps = ln.map;
        int index = binarySearch(dps, ln.degree, key);

        // If index negative, the key doesn't exist in B+ tree
        if (index < 0) {
            return null;
        } else {
            return dps[index].getValue();
        }
    }

    private boolean isEmpty() {
        return firstEx == null;
    }

    private ExternalNode<K, V> findExternalNode(K key) {

        // Initialize keys and index variable
        K[] keys = this.root.keys;
        int i;

        // Find next node on path to appropriate leaf node
        for (i = 0; i < this.root.degree - 1; i++) {
            if (getKeyComp().compare(key, keys[i]) < 0) {
                break;
            }
        }

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
        Node<K, V> child = this.root.childPointers[i];
        if (child instanceof ExternalNode<K, V>) {
            return (ExternalNode<K, V>) child;
        } else {
            return findExternalNode((InternalNode<K, V>) child, key);
        }
    }

    private ExternalNode<K, V> findExternalNode(InternalNode<K, V> node, K key) {

        // Initialize keys and index variable
        K[] keys = node.keys;
        int i;

        // Find next node on path to appropriate leaf node
        for (i = 0; i < node.degree - 1; i++) {
            if (getKeyComp().compare(key, keys[i]) < 0) {
                break;
            }
        }

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
        Node<K, V> childNode = node.childPointers[i];
        if (childNode instanceof ExternalNode<K, V>) {
            return (ExternalNode<K, V>) childNode;
        } else {
            return findExternalNode((InternalNode<K, V>) node.childPointers[i], key);
        }
    }
    private int binarySearch(Entry<K, V>[] dps, int numPairs, K t) {
        return Arrays.binarySearch(dps, 0, numPairs, dps[0], this.getComp());
    }

    public ArrayList<V> search(K lowerBound, K upperBound) {

        // Instantiate Double array to hold values
        ArrayList<V> values = new ArrayList<V>();

        // Iterate through the doubly linked list of leaves
        ExternalNode<K, V> currNode = this.firstEx;
        while (currNode != null) {

            // Iterate through the dictionary of each node
            ExternalNode.MapEntry<K, V> dps[] = currNode.map;
            for (ExternalNode.MapEntry<K, V> dp : dps) {

				/* Stop searching the dictionary once a null value is encountered
				   as this the indicates the end of non-null values */
                if (dp == null) {
                    break;
                }

                // Include value if its key fits within the provided range
                if (super.getKeyComp().compare(lowerBound, dp.getKey()) <= 0 && super.getKeyComp().compare(dp.getKey(), upperBound) <= 0) {
                    values.add(dp.getValue());
                }
            }

			/* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            currNode = (ExternalNode<K, V>) currNode.rightSibling;

        }
        return values;
    }




    public Entry<K, V> insert(K key, V value) {

        Entry<K, V> newEntry = null;

        if (isEmpty()) {

            /* Flow of execution goes here only when first insert takes place */

            // Create leaf node as first node in B plus tree (root is null)

            // Set as first leaf node (can be used later for in-order leaf traversal)
            ExternalNode<K, V> ln = new ExternalNode<K, V>(this.m, key, value, getComp());

            // Set as first leaf node (can be used later for in-order leaf traversal)
            this.firstEx = ln;

        } else {


            // Find leaf node to insert into
            ExternalNode<K, V> ln = (this.root == null) ? this.firstEx : findExternalNode(key);

            // Insert into leaf node fails if node becomes overfull
            if (!ln.insertExternal(key, value,getComp())) {
                ln.insertExternal1(key, value, super.getComp());

                if (this.root == null) {

                    // Set the root of B+ tree to be the parent
                    this.root = ln.parent;

                } else {
					/* If parent is overfull, repeat the process up the tree,
			   		   until no deficiencies are found */
                    InternalNode<K, V> in = ln.parent;
                    while (in != null) {
                        System.out.println(1);
                        if (in.isOverfull()) {
                            splitInternalNode(in);
                        } else {
                            break;
                        }
                        in = in.parent;
                    }
                }
            }
            newEntry = ln.findEntry(key, value);
        } return newEntry;
    }
    private void splitInternalNode(InternalNode<K, V> in) {

        // Acquire parent
        InternalNode<K, V> parent = in.parent;

        // Split keys and pointers in half
        int midpoint = getMidpoint();
        K newParentKey = in.keys[midpoint];
        K[] halfKeys = splitKeys(in.keys, midpoint);
        Node<K, V>[] halfPointers = splitChildPointers(in, midpoint);

        // Change degree of original InternalNode in
        in.degree = linearNullSearch(in.childPointers);

        // Create new sibling internal node and add half of keys and pointers
        InternalNode<K, V> sibling = new InternalNode<K, V>(this.m, halfKeys, halfPointers, linearNullSearch(halfPointers));
        for (Node<K, V> pointer : halfPointers) {
            if (pointer != null) {
                pointer.parent = sibling;
            }
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
            K[] keys = (K[])Array.newInstance(this.key.getClass(),this.m);
            keys[0] = newParentKey;
            InternalNode<K, V> newRoot = new InternalNode<K, V>(this.m, keys);
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

    private K[] splitKeys(K[] keys, int split) {

        K[] halfKeys = (K[])Array.newInstance(this.key.getClass(),this.m);

        // Remove split-indexed value from keys
        keys[split] = null;

        // Copy half of the values into halfKeys while updating original keys
        for (int i = split + 1; i < keys.length; i++) {
            halfKeys[i - split - 1] = keys[i];
            keys[i] = null;
        }

        return halfKeys;
    }

    private Node<K, V>[] splitChildPointers(InternalNode<K, V> in, int split) {

        Node<K, V>[] pointers = in.childPointers;
        Node<K, V>[] halfPointers = new Node[this.m + 1];

        // Copy half of the values into halfPointers while updating original keys
        for (int i = split + 1; i < pointers.length; i++) {
            halfPointers[i - split - 1] = pointers[i];
            in.removePointer(i);
        }
        return halfPointers;
    }

    private int linearNullSearch(Node<K, V>[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public Entry<K, V> delete(K key) {
        Entry<K, V> rem = null;

        if (isEmpty()) {

            /* Flow of execution goes here when B+ tree has no dictionary pairs */

            System.err.println("Invalid Delete: The B+ tree is currently empty.");

        } else {

            // Get leaf node and attempt to find index of key to delete
            ExternalNode<K, V> ln = (this.root == null) ? this.firstEx : findExternalNode(key);
            int dpIndex = binarySearch(ln.map, ln.degree, key);
            if (dpIndex < 0) {

                /* Flow of execution goes here when key is absent in B+ tree */

                System.err.println("Invalid Delete: Key unable to be found.");

            }
            else {

                // Successfully delete the dictionary pair
                rem = ln.delete(dpIndex);

                // Check for deficiencies
                if (ln.isDeficient()) {
                    ln.deleteExternal1(super.getKeyComp(), this.firstEx, root, super.getComp());
                }
                else if (this.root == null && this.firstEx.degree == 0) {

					/* Flow of execution goes here when the deleted dictionary
					   pair was the only pair within the tree */

                    // Set first leaf as null to indicate B+ tree is empty
                    this.firstEx = null;

                } else {

					/* The dictionary of the LeafNode object may need to be
					   sorted after a successful delete */
                    ln.sortMap(super.getComp());

                }


            }
        } return rem;
    }
    public void delete(ArrayList<Record> records,K key) {
        // Iterate through the doubly linked list of leaves
        ExternalNode<K, V> currNode = this.firstEx;
        while (currNode != null) {
            Entry<K, V> rem = null;
            int dpIndex = binarySearch(currNode.map, currNode.degree, key);

            if (dpIndex>=0){
                // Iterate through the dictionary of each node
                ExternalNode.MapEntry<K, V> dps[] = currNode.map;
                for (ExternalNode.MapEntry<K, V> dp : dps) {

				/* Stop searching the dictionary once a null value is encountered
				   as this the indicates the end of non-null values */
                    if (dp == null) {
                        break;
                    }

                    // Include value if its key fits within the provided range
                    if (records.contains(dp.getValue())) {
                        rem = currNode.delete(dpIndex);

                        // Check for deficiencies
                        if (currNode.isDeficient()) {
                            currNode.deleteExternal1(super.getKeyComp(), this.firstEx, root, super.getComp());
                        }
                        else if (this.root == null && this.firstEx.degree == 0) {

					/* Flow of execution goes here when the deleted dictionary
					   pair was the only pair within the tree */

                            // Set first leaf as null to indicate B+ tree is empty
                            this.firstEx = null;

                        } else {

					/* The dictionary of the LeafNode object may need to be
					   sorted after a successful delete */
                            currNode.sortMap(super.getComp());

                        }
                    }
                }
            }

			/* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            currNode = (ExternalNode<K, V>) currNode.rightSibling;

        }
    }

    private int getMidpoint() {
        return (int)Math.ceil((this.m + 1) / 2.0) - 1;
    }

    public K getKey() {
        return key;
    }

    public static void main(String[] args) {
        ArrayBPTree<Integer, Integer> it = new ArrayBPTree<>(3, 1, new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        it.insert(1, 1);
        it.insert(28, 1);
        it.insert(2, 1);
        it.insert(4, 1);

    }


}
