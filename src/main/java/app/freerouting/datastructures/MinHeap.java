
package app.freerouting.datastructures;

import java.util.Comparator;

/**
 * A generic MinHeap implementation for storing elements of type T.
 *
 * <p>
 * This class provides a basic MinHeap data structure, which is a binary tree
 * where the value of
 * each node is less than or equal to the values of its children. It supports
 * essential heap
 * operations such as add, poll, and peek.
 * </p>
 *
 * @param <T> the type of elements stored in the heap
 */
public class MinHeap<T> {

  private static final int DEFAULT_CAPACITY = 15000;
  private final Comparator<T> comparator;
  private int size;
  private T[] heap;

  /**
   * Constructs a MinHeap with the specified comparator and initial capacity.
   *
   * @param comparator the comparator to use for ordering elements
   * @param capacity   the initial capacity of the heap
   */
  @SuppressWarnings("unchecked")
  public MinHeap(Comparator<T> comparator, int capacity) {
    this.size = 0;
    this.heap = (T[]) new Object[capacity];
    this.comparator = comparator;
  }

  /**
   * Constructs a MinHeap with the specified comparator and a default initial
   * capacity.
   *
   * @param comparator the comparator to use for ordering elements
   */
  public MinHeap(Comparator<T> comparator) {
    this(comparator, DEFAULT_CAPACITY);
  }

  /**
   * Adds an element to the heap.
   *
   * @param element the element to add
   */
  public void add(T element) {
    if (size >= heap.length) {
      expandCapacity();
    }
    heap[size] = element;
    siftUp(size);
    size++;
  }

  /**
   * Retrieves and removes the smallest element from the heap.
   *
   * @return the smallest element, or null if the heap is empty
   */
  public T poll() {
    if (size == 0) {
      return null;
    }
    T smallest = heap[0];
    heap[0] = heap[size - 1];
    size--;
    if (size > 0) {
      siftDown(0);
    }
    return smallest;
  }

  /**
   * Retrieves, but does not remove, the smallest element from the heap.
   *
   * @return the smallest element, or null if the heap is empty
   */
  public T peek() {
    return size > 0 ? heap[0] : null;
  }

  /**
   * Returns the number of elements in the heap.
   *
   * @return the size of the heap
   */
  public int size() {
    return size;
  }

  /**
   * Checks if the heap is empty.
   *
   * @return true if the heap is empty, false otherwise
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Clears all elements from the heap.
   */
  public void clear() {
    for (int i = 0; i < size; i++) {
      heap[i] = null; // Help garbage collection
    }
    size = 0;
  }

  /**
   * Restores the heap property by moving an element up the tree.
   *
   * @param index the index of the element to sift up
   */
  private void siftUp(int index) {
    T element = heap[index];
    while (index > 0) {
      int parentIndex = (index - 1) / 2;
      T parent = heap[parentIndex];
      if (comparator.compare(element, parent) >= 0) {
        break;
      }
      heap[index] = parent;
      index = parentIndex;
    }
    heap[index] = element;
  }

  /**
   * Restores the heap property by moving an element down the tree.
   *
   * @param index the index of the element to sift down
   */
  private void siftDown(int index) {
    T element = heap[index];
    int half = size / 2;
    while (index < half) {
      int childIndex = 2 * index + 1;
      T child = heap[childIndex];
      int rightChildIndex = childIndex + 1;
      if (rightChildIndex < size && comparator.compare(heap[rightChildIndex], child) < 0) {
        childIndex = rightChildIndex;
        child = heap[childIndex];
      }
      if (comparator.compare(element, child) <= 0) {
        break;
      }
      heap[index] = child;
      index = childIndex;
    }
    heap[index] = element;
  }

  /**
   * Doubles the capacity of the heap.
   */
  @SuppressWarnings("unchecked")
  private void expandCapacity() {
    int newCapacity = heap.length * 2;
    T[] newHeap = (T[]) new Object[newCapacity];
    System.arraycopy(heap, 0, newHeap, 0, heap.length);
    heap = newHeap;
  }
}
