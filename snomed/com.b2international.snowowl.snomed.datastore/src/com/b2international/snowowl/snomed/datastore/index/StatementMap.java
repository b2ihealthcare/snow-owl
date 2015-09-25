/*
 *  Primitive Collections for Java.
 *  Copyright (C) 2002, 2003  S?ren Bak
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.b2international.snowowl.snomed.datastore.index;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import bak.pcj.LongIterator;
import bak.pcj.hash.DefaultLongHashFunction;
import bak.pcj.hash.LongHashFunction;
import bak.pcj.map.AbstractLongKeyMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.set.AbstractLongSet;
import bak.pcj.set.LongSet;
import bak.pcj.util.Exceptions;

/**
 */
public class StatementMap extends AbstractLongKeyMap implements LongKeyMap, Cloneable, Serializable {

	private static final long serialVersionUID = 3322784991082490638L;

	/** Constant indicating relative growth policy. */
    private static final int    GROWTH_POLICY_RELATIVE      = 0;

    /** Constant indicating absolute growth policy. */
    private static final int    GROWTH_POLICY_ABSOLUTE      = 1;

    /**
     *  The default growth policy of this map.
     *  @see    #GROWTH_POLICY_RELATIVE
     *  @see    #GROWTH_POLICY_ABSOLUTE
     */
    private static final int    DEFAULT_GROWTH_POLICY       = GROWTH_POLICY_RELATIVE;

    /** The default factor with which to increase the capacity of this map. */
    public static final double DEFAULT_GROWTH_FACTOR        = 1.0;

    /** The default chunk size with which to increase the capacity of this map. */
    public static final int    DEFAULT_GROWTH_CHUNK         = 10;

    /** The default capacity of this map. */
    public static final int    DEFAULT_CAPACITY             = 11;

    /** The default load factor of this map. */
    public static final double DEFAULT_LOAD_FACTOR          = 0.75;

    /**
     *  The hash function used to hash keys in this map.
     *  @serial
     */
    private LongHashFunction keyhash;

    /**
     *  The size of this map.
     *  @serial
     */
    private int size;

    /**
     *  The keys of this map. Contains key values directly.
     *  Due to the use of a secondary hash function, the length of this
     *  array must be a prime.
     */
    private transient long[] keys;

    /**
     *  The values of this map. Contains values directly.
     *  Due to the use of a secondary hash function, the length of this
     *  array must be a prime.
     */
    private transient long[] valueIds;
    private transient long[] objectIds;

    /** The states of each cell in the keys[] and values[]. */
    private transient byte[] states;

    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte REMOVED = 2;

    /** The number of entries in use (removed or occupied). */
    private transient int used;

    /**
     *  The growth policy of this map (0 is relative growth, 1 is absolute growth).
     *  @serial
     */
    private int growthPolicy;

    /**
     *  The growth factor of this map, if the growth policy is
     *  relative.
     *  @serial
     */
    private double growthFactor;

    /**
     *  The growth chunk size of this map, if the growth policy is
     *  absolute.
     *  @serial
     */
    private int growthChunk;

    /**
     *  The load factor of this map.
     *  @serial
     */
    private double loadFactor;

    /**
     *  The next size at which to expand the data[].
     *  @serial
     */
    private int expandAt;

    /** A set view of the keys of this map. */
    private transient LongSet ckeys;

    /** A collection view of the values of this map. */
    private transient Collection cvalues;

    private StatementMap(LongHashFunction keyhash, int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        if (keyhash == null)
            Exceptions.nullArgument("hash function");
        if (capacity < 0)
            Exceptions.negativeArgument("capacity", String.valueOf(capacity));
        if (growthFactor <= 0.0)
            Exceptions.negativeOrZeroArgument("growthFactor", String.valueOf(growthFactor));
        if (growthChunk <= 0)
            Exceptions.negativeOrZeroArgument("growthChunk", String.valueOf(growthChunk));
        if (loadFactor <= 0.0)
            Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(loadFactor));
        this.keyhash = keyhash;
        capacity = bak.pcj.hash.Primes.nextPrime(capacity);
        keys = new long[capacity];
        valueIds = new long[capacity];
        objectIds = new long[capacity];
        this.states = new byte[capacity];
        size = 0;
        expandAt = (int)Math.round(loadFactor*capacity);
        this.used = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = growthFactor;
        this.growthChunk = growthChunk;
        this.loadFactor = loadFactor;
    }

    private StatementMap(int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        this(DefaultLongHashFunction.INSTANCE, capacity, growthPolicy, growthFactor, growthChunk, loadFactor);
    }

    /**
     *  Creates a new hash map with capacity 11, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     */
    public StatementMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     *  Creates a new hash map with the same mappings as a specified map.
     *
     *  @param      map
     *              the map whose mappings to put into the new map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public StatementMap(LongKeyMap map) {
        this();
        putAll(map);
    }

    /**
     *  Creates a new hash map with a specified capacity, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     */
    public StatementMap(int capacity) {
        this(capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash map with a capacity of 11, a relative
     *  growth factor of 1.0, and a specified load factor.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     */
    public StatementMap(double loadFactor) {
        this(DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity and
     *  load factor, and a relative growth factor of 1.0.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     */
    public StatementMap(int capacity, double loadFactor) {
        this(capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity,
     *  load factor, and relative growth factor.
     *
     *  <p>The map capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the map.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthFactor</tt> is not positive.
     */
    public StatementMap(int capacity, double loadFactor, double growthFactor) {
        this(capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity,
     *  load factor, and absolute growth factor.
     *
     *  <p>The map capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthChunk</tt> is not positive.
     */
    public StatementMap(int capacity, double loadFactor, int growthChunk) {
        this(capacity, GROWTH_POLICY_ABSOLUTE, DEFAULT_GROWTH_FACTOR, growthChunk, loadFactor);
    }

    // ---------------------------------------------------------------
    //      Constructors with hash function argument
    // ---------------------------------------------------------------

    /**
     *  Creates a new hash map with capacity 11, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash) {
        this(keyhash, DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash map with a specified capacity, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash, int capacity) {
        this(keyhash, capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash map with a capacity of 11, a relative
     *  growth factor of 1.0, and a specified load factor.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash, double loadFactor) {
        this(keyhash, DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity and
     *  load factor, and a relative growth factor of 1.0.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash, int capacity, double loadFactor) {
        this(keyhash, capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity,
     *  load factor, and relative growth factor.
     *
     *  <p>The map capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the map.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthFactor</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash, int capacity, double loadFactor, double growthFactor) {
        this(keyhash, capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash map with a specified capacity,
     *  load factor, and absolute growth factor.
     *
     *  <p>The map capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the map.
     *
     *  @param      loadFactor
     *              the load factor of the map.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthChunk</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public StatementMap(LongHashFunction keyhash, int capacity, double loadFactor, int growthChunk) {
        this(keyhash, capacity, GROWTH_POLICY_ABSOLUTE, DEFAULT_GROWTH_FACTOR, growthChunk, loadFactor);
    }

    // ---------------------------------------------------------------
    //      Hash table management
    // ---------------------------------------------------------------

    private void ensureCapacity(int elements) {
        if (elements >= expandAt) {
            int newcapacity;
            if (growthPolicy == GROWTH_POLICY_RELATIVE)
                newcapacity = (int)(keys.length * (1.0 + growthFactor));
            else
                newcapacity = keys.length + growthChunk;
            if (newcapacity*loadFactor < elements)
                newcapacity = (int)Math.round(((double)elements/loadFactor));
            newcapacity = bak.pcj.hash.Primes.nextPrime(newcapacity);
            expandAt = (int)Math.round(loadFactor*newcapacity);

            long[] newkeys = new long[newcapacity];
            long[] newvalueIds = new long[newcapacity];
            long[] newobjectIds = new long[newcapacity];
            byte[] newstates = new byte[newcapacity];

            used = 0;
            //  re-hash
            for (int i = 0; i < keys.length; i++) {
                if (states[i] == OCCUPIED) {
                    used++;
                    long k = keys[i];
                    long v = valueIds[i];
                    long o = objectIds[i];
                    //  first hash
                    int h = Math.abs(keyhash.hash(k));
                    int n = h % newcapacity;
                    if (newstates[n] == OCCUPIED) {
                        //  second hash
                        int c = 1 + (h % (newcapacity - 2));
                        for (;;) {
                            n -= c;
                            if (n < 0)
                                n += newcapacity;
                            if (newstates[n] == EMPTY)
                                break;
                        }
                    }
                    newstates[n] = OCCUPIED;
                    newvalueIds[n] = v;
                    newobjectIds[n] = o;
                    newkeys[n] = k;
                }
            }

            keys = newkeys;
            valueIds = newvalueIds;
            objectIds = newobjectIds;
            states = newstates;
        }
    }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract implementation
    // ---------------------------------------------------------------

    public LongSet keySet() {
        if (ckeys == null)
            ckeys = new KeySet();
        return ckeys;
    }

    /**
     * 
     * Value argument must be a two length primitive long array. First item is the value ID of the statement.
     * The second is the object ID of the statement.
     * {@inheritDoc}
     */
    public Object put(long key, Object value) {

        if (!value.getClass().isArray()) {
        	throw new IllegalArgumentException("Value argument must be an array of primitive longs.");
        }
        
        long[] valueArray = (long[]) value;
        
        //  first hash
        int h = Math.abs(keyhash.hash(key));
        int i = h % keys.length;
        if (states[i] == OCCUPIED) {
            if (keys[i] == key) {
                long oldValue = valueIds[i];
                long oldObject = objectIds[i];
                valueIds[i] = valueArray[0];
                objectIds[i] = valueArray[1];
                return new long[] { oldValue, oldObject };
            }
            //  second hash
            int c = 1 + (h % (keys.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += keys.length;
                //  Empty entries are re-used
                if (states[i] == EMPTY || states[i] == REMOVED)
                    break;
                if (states[i] == OCCUPIED && keys[i] == key) {
                    long oldValue = valueIds[i];
                    long oldObject = objectIds[i];
                    valueIds[i] = valueArray[0];
                    objectIds[i] = valueArray[1];
                    return new long[] { oldValue, oldObject };
                }
            }
        }

        if (states[i] == EMPTY)
            used++;
        states[i] = OCCUPIED;
        keys[i] = key;
        valueIds[i] = valueArray[0];
        objectIds[i] = valueArray[1];
        size++;
        ensureCapacity(used);
        return null;
    }

    public Collection values() {
        if (cvalues == null)
            cvalues = new ValueCollection();
        return cvalues;
    }

    /**
     *  Returns a clone of this hash map.
     *
     *  @return     a clone of this hash map.
     *
     *  @since      1.1
     */
    public Object clone() {
        try {
            StatementMap c = (StatementMap)super.clone();
            c.keys = new long[keys.length];
            System.arraycopy(keys, 0, c.keys, 0, keys.length);
            c.valueIds = new long[valueIds.length];
            System.arraycopy(valueIds, 0, c.valueIds, 0, valueIds.length);
            c.objectIds = new long[objectIds.length];
            System.arraycopy(objectIds, 0, c.objectIds, 0, objectIds.length);
            c.states = new byte[states.length];
            System.arraycopy(states, 0, c.states, 0, states.length);
            //  The views should not refer to this map's views
            c.cvalues = null;
            c.ckeys = null;
            return c;
        } catch (CloneNotSupportedException e) {
            Exceptions.cloning(); return null;
        }
    }

    public LongKeyMapIterator entries() {
        return new LongKeyMapIterator() {
            int nextEntry = nextEntry(0);
            int lastEntry = -1;

            int nextEntry(int index) {
                while (index < keys.length && states[index] != OCCUPIED)
                    index++;
                return index;
            }

            public boolean hasNext() {
                return nextEntry < keys.length;
            }

            public void next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                lastEntry = nextEntry;
                nextEntry = nextEntry(nextEntry+1);
            }

            public void remove() {
                if (lastEntry == -1)
                    Exceptions.noElementToRemove();
                states[lastEntry] = REMOVED;
                valueIds[lastEntry] = MapDefaults.defaultLong();
                objectIds[lastEntry] = MapDefaults.defaultLong();
                size--;
                lastEntry = -1;
            }

            public long getKey() {
                if (lastEntry == -1)
                    Exceptions.noElementToGet();
                return keys[lastEntry];
            }

            public Object getValue() {
                if (lastEntry == -1)
                    Exceptions.noElementToGet();
                return new long[] { valueIds[lastEntry], objectIds[lastEntry] };
            }
        };
    }

    private class KeySet extends AbstractLongSet {

        public void clear()
        { StatementMap.this.clear(); }

        public boolean contains(long v) {
            return containsKey(v);
        }

        public LongIterator iterator() {
            return new LongIterator() {
                int nextEntry = nextEntry(0);
                int lastEntry = -1;

                int nextEntry(int index) {
                    while (index < keys.length && states[index] != OCCUPIED)
                        index++;
                    return index;
                }

                public boolean hasNext() {
                    return nextEntry < keys.length;
                }

                public long next() {
                    if (!hasNext())
                        Exceptions.endOfIterator();
                    lastEntry = nextEntry;
                    nextEntry = nextEntry(nextEntry+1);
                    return keys[lastEntry];
                }

                public void remove() {
                    if (lastEntry == -1)
                        Exceptions.noElementToRemove();
                    states[lastEntry] = REMOVED;
                    valueIds[lastEntry] = MapDefaults.defaultLong();
                    objectIds[lastEntry] = MapDefaults.defaultLong();
                    size--;
                    lastEntry = -1;
                }
            };
        }

        public boolean remove(long v) {
            boolean result = containsKey(v);
            if (result)
                StatementMap.this.remove(v);
            return result;
        }

        public int size()
        { return size; }

    }


    private class ValueCollection extends AbstractCollection {

        public void clear()
        { StatementMap.this.clear(); }

        public boolean contains(Object v) {
            return containsValue(v);
        }

        public Iterator iterator() {
            return new Iterator() {
                int nextEntry = nextEntry(0);
                int lastEntry = -1;

                int nextEntry(int index) {
                    while (index < keys.length && states[index] != OCCUPIED)
                        index++;
                    return index;
                }

                public boolean hasNext() {
                    return nextEntry < keys.length;
                }

                public Object next() {
                    if (!hasNext())
                        Exceptions.endOfIterator();
                    lastEntry = nextEntry;
                    nextEntry = nextEntry(nextEntry+1);
                    return valueIds[lastEntry];
                }

                public void remove() {
                    if (lastEntry == -1)
                        Exceptions.noElementToRemove();
                    states[lastEntry] = REMOVED;
                    valueIds[lastEntry] = MapDefaults.defaultLong();
                    objectIds[lastEntry] = MapDefaults.defaultLong();
                    size--;
                    lastEntry = -1;
                }
            };
        }

        public int size()
        { return size; }

    }

    // ---------------------------------------------------------------
    //      Operations overwritten for efficiency
    // ---------------------------------------------------------------

    public void clear() {
        java.util.Arrays.fill(states, EMPTY);
        java.util.Arrays.fill(valueIds, 0);
        java.util.Arrays.fill(objectIds, 0);
        size = 0;
        used = 0;
    }

    public boolean containsKey(long key) {
        int h = Math.abs(keyhash.hash(key));
        int i = h % keys.length;
        if (states[i] != EMPTY) {
            if (states[i] == OCCUPIED && keys[i] == key)
                return true;

            //  second hash
            int c = 1 + (h % (keys.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += keys.length;
                if (states[i] == EMPTY)
                    return false;
                if (states[i] == OCCUPIED && keys[i] == key)
                    return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
    	throw new UnsupportedOperationException();
    }

    public Object get(long key) {
        int h = Math.abs(keyhash.hash(key));
        int i = h % keys.length;
        if (states[i] != EMPTY) {
            if (states[i] == OCCUPIED && keys[i] == key)
                return new long[] { valueIds[i], objectIds[i] };
            //  second hash

            int c = 1 + (h % (keys.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += keys.length;
                if (states[i] == EMPTY)
                    return null;
                if (states[i] == OCCUPIED && keys[i] == key)
                	return new long[] { valueIds[i], objectIds[i] };
            }
        }
        return null;
    }

    public boolean isEmpty()
    { return size == 0; }

    public Object remove(long key) {
        int h = Math.abs(keyhash.hash(key));
        int i = h % keys.length;
        if (states[i] != EMPTY) {
            if (states[i] == OCCUPIED && keys[i] == key) {
                Object oldValue = valueIds[i];
                valueIds[i] = MapDefaults.defaultLong();
                objectIds[i] = MapDefaults.defaultLong();
                states[i] = REMOVED;
                size--;
                return oldValue;
            }
            //  second hash
            int c = 1 + (h % (keys.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += keys.length;
                if (states[i] == EMPTY) {
                    return null;
                }
                if (states[i] == OCCUPIED && keys[i] == key) {
                    Object oldValue = valueIds[i];
                    valueIds[i] = MapDefaults.defaultLong();
                    objectIds[i] = MapDefaults.defaultLong();
                    states[i] = REMOVED;
                    size--;
                    return oldValue;
                }
            }
        }
        return null;
    }

    public int size()
    { return size; }

    /**
     *  Returns a string representation of this map.
     *
     *  @return     a string representation of this map.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('[');
        LongKeyMapIterator i = entries();
        while (i.hasNext()) {
            if (s.length() > 1)
                s.append(',');
            i.next();
            s.append(String.valueOf(i.getKey()));
            s.append("->");
            s.append(i.getValue().getClass().isArray() ? Arrays.toString((Object[]) i.getValue()) : String.valueOf(i.getValue()));
        }
        s.append(']');
        return s.toString();
    }
    
    // ---------------------------------------------------------------
    //      Serialization
    // ---------------------------------------------------------------

    /**
     *  @serialData     Default fields; the capacity of the
     *                  map (<tt>int</tt>); the maps's entries
     *                  (<tt>long</tt>, <tt>Object</tt>).
     *
     *  @since          1.1
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(keys.length);
        LongKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            s.writeLong(i.getKey());
            s.writeObject(i.getValue());
        }
    }

    /**
     *  @since          1.1
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        keys = new long[s.readInt()];
        states = new byte[keys.length];
        valueIds = new long[keys.length];
        objectIds = new long[keys.length];
        used = size;

        for (int n = 0; n < size; n++) {
            long key = s.readLong();
            long valueId = s.readLong();
            long objectId = s.readLong();

            //  first hash
            int h = Math.abs(keyhash.hash(key));
            int i = h % keys.length;
            if (states[i] != EMPTY) {
                //  second hash
                int c = 1 + (h % (keys.length - 2));
                for (;;) {
                    i -= c;
                    if (i < 0)
                        i += keys.length;
                    if (states[i] == EMPTY)
                        break;
                }
            }
            states[i] = OCCUPIED;
            keys[i] = key;
            valueIds[i] = valueId;
            objectIds[i] = objectId;
        }
    }

}