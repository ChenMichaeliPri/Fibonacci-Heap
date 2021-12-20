/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    private HeapNode minNode;
    private int size;
    private int marksCounter;
    private static int cutsCounter;
    private static int linksCounter;

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Complexity O(1).
    */
    public boolean isEmpty() {
    	return this.minNode == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.
    * Complexity O(1).
    * Returns the newly created node.
    */
    public HeapNode insert(int key) {
    	HeapNode newNode = new HeapNode(key);
    	this.size++;

    	if (isEmpty()) {
    	    DoublyLinkedList nodeList = new DoublyLinkedList();
    	    nodeList.add(newNode);
    	    this.minNode = newNode;
    	    newNode.nodeList = nodeList;
        }
    	else {
    	    DoublyLinkedList rootsList = this.minNode.nodeList;
    	    rootsList.add(newNode);
    	    newNode.nodeList = rootsList;
            // Updating new minimum node if needed
    	    if (newNode.key < this.minNode.key) this.minNode = newNode;
        }
    	return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key, and consolidates the heap.
    *Complexity O(log n amortized), O(n) WC.
    *
    */
    public void deleteMin() {
        if (this.size == 1) this.minNode = null;
        else {
            deleteMinCut(this.minNode);
            HeapNode iterNode = this.minNode;

            for (int i=0; i < this.minNode.nodeList.size; i++) {
                if (iterNode.key < this.minNode.key) this.minNode = iterNode;
                iterNode = iterNode.next;
            }
            consolidate();
        }
    }

    /**
     * private void deleteMinCut(HeapNode node)
     *
     * Cuts the node from the current tree, supports deleteMinCut method.
     * Complexity O(1 amortized).
     *
     */
    private void deleteMinCut(HeapNode node) {
        cutsCounter++;
        this.minNode.nodeList.size += node.child.nodeList.size - 1;
        HeapNode leftFromNode = node.prev;
        HeapNode rightFromNode = node.next;
        HeapNode leftmostChild = node.child.nodeList.firstNode;
        HeapNode rightmostChild = node.child.nodeList.firstNode.prev;
        leftFromNode.next = leftmostChild;
        leftmostChild.prev = leftFromNode;
        rightFromNode.prev = rightmostChild;
        rightmostChild.next = rightFromNode;

        // change min
        this.minNode = node.next;
        node.child = null;
        node.next = node.prev = node;
    }

    /**
     * public void findMaxRank()
     *
     * Returns the maximum rank binomial tree from the heap.
     * Complexity O(log n) amortized, O(n) WC.
     *
     */
    private int findMaxRank() {
        int maxRank = 0;
        HeapNode iterNode = this.minNode;

        for (int i=0; i < this.minNode.nodeList.size; i++) {
            maxRank = Math.max(iterNode.rank, maxRank);
            iterNode = iterNode.next;
        }
        return maxRank;
    }

    /**
     * private void changeParentToNull(DoublyLinkedList nodeDll)
     *
     * Supports deleteMin method. Changes the parents of the sons of deleted node to null,
     * changes the mark of the nodes to false;
     * Complexity O(Log n amortized).
     *
     */
    private void changeParentToNull(DoublyLinkedList nodeDll) {
        HeapNode iterNode = nodeDll.firstNode;

        for (int i=0; i < nodeDll.size; i++) {
            iterNode.parent = null;
            iterNode.nodeList = this.minNode.nodeList; // now belongs to root node list.
            iterNode = iterNode.next;
            iterNode.mark = false;
        }
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Complexity O(1).
    *
    */
    public HeapNode findMin() {
    	return this.minNode;
    }
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Complexity O(1).
    *
    */
    public void meld (FibonacciHeap heap2) {
    	  if (this.isEmpty()) this.minNode = heap2.minNode;
    	  else if (!heap2.isEmpty()) {
    	      this.minNode.nodeList.concatenate(heap2.minNode.nodeList);
          }
    	  this.size += heap2.size();
    	  this.marksCounter += heap2.marksCounter;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    * Complexity O(1).
    *
    */
    public int size() {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep()
    {
    	int[] arr = new int[100];
        return arr; //	 to be replaced by student code
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) {
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    * Complexity O(1).
    */
    public void decreaseKey(HeapNode x, int delta) {
        if (x.parent == null) x.key -= delta; // node is root, doesn't harm heap invariants -> no cut
        else {
            x.key -= delta;
            if (x.key < x.parent.key) cascadingCut(x); // heap order violation -> cascading cut
        }
    }

    /**
     * private void cascadingCut(HeapNode node)
     *
     * Cuts the node from the current tree, supports decreaseKey method.
     * Complexity O(1 amortized).
     *
     */
    private void cascadingCut(HeapNode node) {
        cutsCounter++;
        HeapNode parent = node.parent;
        if (node.nodeList.size == 1) { // has no siblings
            node.parent.child = null;
        }
        else {
            node.parent.child = node.next;
        }
        node.nodeList.delete(node);
        node.parent = null;
        this.minNode.nodeList.add(node);
        node.mark = false;

        if (parent.mark) cascadingCut(parent);
        else {
            parent.mark = true;
            this.marksCounter++;
        }
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    * Complexity O(1).
    *
    */
    public int potential() {
    	return this.minNode.nodeList.size + this.marksCounter*2;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    * Complexity O(1).
    */
    public static int totalLinks() {
    	return linksCounter;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
    * Complexity O(1)
    */
    public static int totalCuts() {
    	return cutsCounter;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] arr = new int[k];
        FibonacciHeap helperHeap = new FibonacciHeap();
        helperHeap.insert(H.minNode.key);
        helperHeap.minNode.kMinPointer = H.minNode;

        for (int i=0; i<k; i++) {
            HeapNode iterNode = helperHeap.minNode;
            arr[i] = iterNode.key;
            iterNode = iterNode.kMinPointer.child; // check if there is a child
            helperHeap.deleteMin();

            for (int j=0; j < iterNode.nodeList.size; j++) {
                helperHeap.insert(iterNode.key);

            }
        }
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
        private int key;
        private HeapNode kMinPointer;
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        private HeapNode child;
        private  int rank;
        private boolean mark;
        private DoublyLinkedList nodeList;

    	public HeapNode(int key) {
    		this.key = key;
    	}
    }

    public static class DoublyLinkedList {
        private HeapNode firstNode;
        private int size;

        public DoublyLinkedList() {
            this.firstNode = null;
            this.size = 0;
        }

        public boolean isEmpty() {
            return this.size == 0;
        }

        /**
         * public void concatenate(DoublyLinkedList otherDll)
         *
         * Method for concatenating two Dlls.
         * Complexity O(1).
         */

         public void concatenate(DoublyLinkedList otherDll) {
             this.size += otherDll.size;

             if (otherDll.isEmpty()) {}

             else if (isEmpty()) this.firstNode = otherDll.firstNode;

             else {
                 HeapNode lastNodeOfHeap1 = this.firstNode.prev;
                 lastNodeOfHeap1.next = otherDll.firstNode;
                 HeapNode lastNodeOfHeap2 = otherDll.firstNode.prev;
                 otherDll.firstNode.prev = lastNodeOfHeap1;
                 lastNodeOfHeap2.next = this.firstNode;
                 this.firstNode.prev = lastNodeOfHeap2;
             }
         }

        /**
         * public void add (HeapNode node)
         *
         * Method for adding a node to a Dll.
         * Complexity: O(1).
         */

        public void add(HeapNode node) {
            if (this.size == 0) {
                this.firstNode = node;
                size++;
                node.next = node;
                node.prev = node;
            }
            else {
                HeapNode lastNode = this.firstNode.prev;
                lastNode.next = node;
                node.next = this.firstNode;
                lastNode.next.prev = lastNode;
                this.firstNode.prev = node;
                this.firstNode = node;
            }
        }

        /**
         * public void delete(HeapNode node)
         *
         * Method for deleting a node from a Dll.
         * Complexity: O(1).
         */

        public void delete(HeapNode node) {
            this.size--;
            if (this.size == 1) {
                this.firstNode = null;
            }
            else {
                HeapNode prevNode = node.prev;
                HeapNode nextNode = node.next;
                // Changing pointers in order to delete the node
                nextNode.prev = prevNode;
                prevNode.next = nextNode;

                // if node's the new first node
                if (node == this.firstNode) {
                    this.firstNode = node.next;
                }
                // detaching the node from the tree
                node.next = node;
                node.prev = node;
            }
        }
    }
}
