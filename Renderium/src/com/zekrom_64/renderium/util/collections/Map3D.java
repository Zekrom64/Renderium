package com.zekrom_64.renderium.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.zekrom_64.renderium.util.TypeUtils;

/** A specialized hash map that uses 3-component integer vectors as keys.
 * 
 * 
 * @author Zekrom_64
 *
 * @param <V> The mapped value type
 */
public class Map3D<V> implements Map<Vector3ic, V> {
	
	/** A function which computes the hash of a 3D integer coordinate.
	 * 
	 * @author Zekrom_64
	 *
	 */
	@FunctionalInterface
	public static interface HashInt3D {
		
		public int hash(int x, int y, int z);
		
		public default int hash(Vector3ic v) {
			return hash(v.x(), v.y(), v.z());
		}
		
	}
	
	/** The default hashing function, which exclusive-ORs X, Y left rotated by 4, and Z left rotated by 8. */
	public static final HashInt3D DEFAULT_HASH = (int x, int y, int z) -> x ^ Integer.rotateLeft(y, 4) ^ Integer.rotateLeft(z, 8);
	/** A "16-value aligned" hash function, which ORs the lower five bits of X, Y, and Z, where Y is left shifted 4 and Z left shifted 8. */
	public static final HashInt3D ALIGN16_HASH = (int x, int y, int z) -> (x & 0xF) | ((y & 0xF) << 4) | ((z & 0xF) << 8);
	
	private class Node implements Entry<Vector3ic,V> {
		
		public Node prev;
		public Node next;
		
		public final Vector3i key;
		public @NonNull V value;
		
		public Node(int x, int y, int z, @NonNull V value) {
			key = new Vector3i(x, y, z);
			this.value = value;
		}
		
		public void remove() {
			modcount++;
			// Update the next node to point to the previous node
			if (next != null) next.prev = prev;
			// Update the previous node to point to the next node
			// Else, if the first node in a bucket, update the bucket
			if (prev != null) prev.next = next;
			else hashtable[getBucketIndex(key.x, key.y, key.z)] = next;
		}
		
		public void append(@NonNull Node n) {
			if (next != null) next.append(n);
			else {
				next = n;
				n.prev = this;
			}
		}

		@Override
		public Vector3ic getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			Objects.requireNonNull(value);
			V old = this.value;
			this.value = value;
			return old;
		}
		
		@Override
		public String toString() {
			return "{" + key + "=" + value + "}";
		}
		
		@Override
		public int hashCode() {
			return key.hashCode() ^ value.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (o == null) return false;
			if (o instanceof Entry) {
				@SuppressWarnings("unchecked")
				Entry<Vector3ic,V> e = (Entry<Vector3ic,V>)o;
				return key.equals(e.getKey()) && value.equals(e.getValue());
			} else return false;
		}
		
	}
	
	// The hashing function
	private final HashInt3D hashfn;
	// The hashtable array
	private final Node[] hashtable;
	// Modulus value for hash computation
	private final int hashmod;
	// If a "fast" hash modulus can be done by bitmasking
	private final boolean fasthash;
	
	// Modification count
	private volatile int modcount = 0;
	// Entry count
	private int entrycount = 0;
	
	private int getBucketIndex(int x, int y, int z) {
		int hashi = hashfn.hash(x, y, z);
		if (fasthash) return hashi & hashmod;
		else return Math.abs(hashi % hashmod);
	}
	
	/** Creates a new 3D map with the given number of buckets and hashing function.
	 * 
	 * @param nbuckets Number of hash buckets
	 * @param hashfn Hashing function
	 */
	public Map3D(int nbuckets, HashInt3D hashfn) {
		this.hashfn = hashfn;
		hashtable = TypeUtils.createGenericArray(nbuckets, Node.class);
		if (Integer.bitCount(nbuckets) == 1) {
			hashmod = nbuckets - 1;
			fasthash = true;
		} else {
			hashmod = nbuckets;
			fasthash = false;
		}
	}
	
	/** Creates a new 3D map with the given number of buckets.
	 * 
	 * @param nbuckets Number of hash buckets
	 */
	public Map3D(int nbuckets) {
		this(nbuckets, DEFAULT_HASH);
	}
	
	/** Creates a new 3D map with 100 buckets.
	 * 
	 */
	public Map3D() {
		this(100);
	}
	
	/** Creates a new map with the 'align-16' hash and 4096 buckets.
	 * 
	 * @param <V> Map value type
	 * @return New align-16 map
	 */
	public static <@NonNull V> Map3D<V> newAlign16() {
		return new Map3D<V>(4096, ALIGN16_HASH);
	}
	
	/** Gets a value from this 3D map, or <b>null</b> if no such element exists.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Value at these coordinates, or <b>null</b>
	 */
	public @Nullable V get(int x, int y, int z) {
		Node n = hashtable[getBucketIndex(x, y, z)];
		while(n != null && (n.key.x != x || n.key.y != y || n.key.z != z)) n = n.next;
		if (n == null) return null;
		return n.value;
	}
	
	/** Gets a value from this 3D map, or <b>null</b> if no such element exists.
	 * 
	 * @param v Vector coordinate
	 * @return Value at these coordinates, or <b>null</b>
	 */
	public @Nullable V get(Vector3ic v) {
		return get(v.x(), v.y(), v.z());
	}
	
	/** Sets a value in the 3D map.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param value Value to set
	 * @return The existing value at these coordinates, or <b>null</b>
	 */
	public @Nullable V put(int x, int y, int z, @NonNull V value) {
		modcount++;
		int i = getBucketIndex(x, y, z);
		Node n = hashtable[i];
		if (n == null) {
			n = new Node(x, y, z, value);
			entrycount++;
			hashtable[i] = n;
			return null;
		} else {
			Node n2 = n;
			while(n2 != null && (n2.key.x != x || n2.key.y != y || n2.key.z != z)) n2 = n2.next;
			if (n2 == null) {
				n2 = new Node(x, y, z, value);
				entrycount++;
				n.append(n2);
				return null;
			} else {
				V old = n2.value;
				n2.value = value;
				return old;
			}
		}
	}
	
	/** Removes a value from the 3D map.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Removed value at these coordinates, or <b>null</b>
	 */
	public @Nullable V remove(int x, int y, int z) {
		modcount++;
		Node n = hashtable[getBucketIndex(x, y, z)];
		while(n != null && (n.key.x != x || n.key.y != y || n.key.z != z)) n = n.next;
		if (n != null) {
			V val = n.value;
			n.remove();
			entrycount--;
			return val;
		} else return null;
	}
	
	/** Removes a value from the 3D map.
	 * 
	 * @param v Vector coordinates
	 * @return Removed value at these coordinates, or <b>null</b>
	 */
	public @Nullable V remove(Vector3ic v) {
		return remove(v.x(), v.y(), v.z());
	}
	
	@Override
	public void clear() {
		modcount++;
		Arrays.fill(hashtable, null);
	}

	@Override
	public int size() {
		return entrycount;
	}

	@Override
	public boolean isEmpty() {
		return entrycount == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return get((Vector3ic)key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		for(Entry<Vector3ic, V> e : entrySet) if (Objects.equals(value, e.getValue())) return true;
		return false;
	}

	@SuppressWarnings("null")
	@Override
	public V get(Object key) {
		return get((Vector3ic)key);
	}

	@SuppressWarnings("null")
	@Override
	public V put(Vector3ic key, V value) {
		return put(key.x(), key.y(), key.z(), value);
	}

	@SuppressWarnings("null")
	@Override
	public V remove(Object key) {
		return remove((Vector3ic)key);
	}

	@Override
	public void putAll(Map<? extends Vector3ic, ? extends V> m) {
		for(var entry : m.entrySet()) put(entry.getKey(), entry.getValue());
	}

	
	private class NodeIterator implements Iterator<Entry<Vector3ic, V>> {

		private int hashtableIndex = 0;
		private Node currentNode = null;
		private int modid = modcount;
		private Node lastNode = null;
		
		private void checkComodified() {
			if (modid != modcount) throw new ConcurrentModificationException();
		}
		
		private void seekToNode() {
			if (currentNode == null) {
				do {
					currentNode = hashtable[hashtableIndex++];
				} while(currentNode != null && hashtableIndex < hashtable.length);
			}
		}
		
		@Override
		public boolean hasNext() {
			checkComodified();
			seekToNode();
			return hashtableIndex < hashtable.length || currentNode != null;
		}

		@Override
		public Map3D<V>.Node next() {
			checkComodified();
			seekToNode();
			Node n = currentNode;
			currentNode = currentNode.next;
			return n;
		}

		@Override
		public void remove() {
			if (lastNode == null) throw new IllegalStateException();
			lastNode.remove();
			modcount++;
			modid = modcount;
		}
		
	}
	
	private class KeyIterator implements Iterator<Vector3ic> {

		private final NodeIterator ni = new NodeIterator();
		
		@Override
		public boolean hasNext() {
			return ni.hasNext();
		}

		@Override
		public Vector3ic next() {
			return ni.next().key;
		}

		@Override
		public void remove() {
			ni.remove();
		}
		
	}
	
	private class KeySet implements Set<Vector3ic> {

		@Override
		public int size() {
			return Map3D.this.size();
		}

		@Override
		public boolean isEmpty() {
			return Map3D.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return Map3D.this.containsKey(o);
		}

		@Override
		public Iterator<Vector3ic> iterator() {
			return new KeyIterator();
		}

		@Override
		public Object[] toArray() {
			return toArray(new Vector3ic[size()]);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length < size()) a = Arrays.copyOf(a, size());
			int i = 0;
			for(var e : entrySet) a[i++] = (T)e.getKey();
			return a;
		}

		@Override
		public boolean add(Vector3ic e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			return Map3D.this.remove(o) != null;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for(Object o : c) if (!contains(o)) return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Vector3ic> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			NodeIterator ni = new NodeIterator();
			boolean modified = false;
			while(ni.hasNext()) {
				if (!c.contains(ni.next().key)) {
					ni.remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean removed = false;
			for(Object o : c) removed |= remove(o);
			return removed;
		}

		@Override
		public void clear() {
			Map3D.this.clear();
		}
		
	}
	
	private final KeySet keySet = new KeySet();
	
	@Override
	public Set<Vector3ic> keySet() {
		return keySet;
	}

	private class ValueIterator implements Iterator<V> {

		private final NodeIterator ni = new NodeIterator();
		
		@Override
		public boolean hasNext() {
			return ni.hasNext();
		}

		@Override
		public V next() {
			return ni.next().value;
		}

		@Override
		public void remove() {
			ni.remove();
		}
		
	}
	
	private class ValueCollection implements Collection<V> {
		
		@Override
		public int size() {
			return Map3D.this.size();
		}

		@Override
		public boolean isEmpty() {
			return Map3D.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return Map3D.this.containsValue(o);
		}

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public Object[] toArray() {
			return toArray(new Object[0]);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length < size()) a = Arrays.copyOf(a, size());
			int i = 0;
			for(var e : entrySet) a[i++] = (T)e.getValue();
			return a;
		}

		@Override
		public boolean add(V e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			boolean modified = false;
			NodeIterator ni = new NodeIterator();
			while(ni.hasNext()) {
				if (Objects.equals(ni.next().value, o)) {
					modified = true;
					ni.remove();
				}
			}
			return modified;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for(Object o : c) if (!contains(o)) return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean removed = false;
			for(Object o : c) removed |= remove(o);
			return removed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			Map3D.this.clear();
		}
		
	}
	
	private final ValueCollection values = new ValueCollection();

	@Override
	public Collection<V> values() {
		return values;
	}
	
	private class EntrySet implements Set<Entry<Vector3ic, V>> {

		@Override
		public int size() {
			return Map3D.this.size();
		}

		@Override
		public boolean isEmpty() {
			return Map3D.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			for(var e : this)
				if (e.equals(o)) return true;
			return false;
		}

		@Override
		public Iterator<Entry<Vector3ic, V>> iterator() {
			return new NodeIterator();
		}

		@Override
		public Object[] toArray() {
			return toArray(new Entry[size()]);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length < size()) a = Arrays.copyOf(a, size());
			int i = 0;
			for(var e : entrySet) a[i++] = (T)e;
			return a;
		}

		@Override
		public boolean add(Entry<Vector3ic, V> e) {
			return !Objects.equals(Map3D.this.put(e.getKey(), e.getValue()), e.getValue());
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for(Object o : c) if (!contains(o)) return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<Vector3ic, V>> c) {
			boolean modified = false;
			for(var e : c) modified |= add(e);
			return modified;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean modified = false;
			for(var o : c) modified |= remove(o);
			return modified;
		}

		@Override
		public void clear() {
			Map3D.this.clear();
		}
		
	}
	
	private final EntrySet entrySet = new EntrySet();

	@Override
	public Set<Entry<Vector3ic, V>> entrySet() {
		return entrySet;
	}
	
}
