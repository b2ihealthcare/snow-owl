/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.noggit.CharArr;

import com.google.common.cache.Cache;
import com.google.common.hash.Hashing;

/**
 * Defines a space-efficient serialization/deserialization format for transferring data.
 * <p>
 * JavaBinCodec has built in support many commonly used types.  This includes primitive types (boolean, byte,
 * short, double, int, long, float), common Java containers/utilities (Date, Map, Collection, Iterator, String,
 * Object[], byte[]), and frequently used Solr types ({@link NamedList}, {@link SolrDocument},
 * {@link SolrDocumentList}). Each of the above types has a pair of associated methods which read and write
 * that type to a stream.
 * <p>
 * Classes that aren't supported natively can still be serialized/deserialized by providing
 * an {@link JavaBinCodec.ObjectResolver} object that knows how to work with the unsupported class.
 * This allows {@link JavaBinCodec} to be used to marshall/unmarshall arbitrary content.
 * <p>
 * NOTE -- {@link JavaBinCodec} instances cannot be reused for more than one marshall or unmarshall operation.
 */

/*
 * Original version: https://github.com/apache/lucene-solr/blob/64093d6/solr/solrj/src/java/org/apache/solr/common/util/JavaBinCodec.java
 * 
 * - Removed Solr-specific methods
 * - Converted internal streams into a regular DataInputStream/DataOutputStream
 * - Converted Solr Cache and hashing methods to use Guava counterparts
 */
public class JavaBinCodec {

  public static final byte
          NULL = 0,
          BOOL_TRUE = 1,
          BOOL_FALSE = 2,
          BYTE = 3,
          SHORT = 4,
          DOUBLE = 5,
          INT = 6,
          LONG = 7,
          FLOAT = 8,
          DATE = 9,
          MAP = 10,
          SOLRDOC = 11,
          SOLRDOCLST = 12,
          BYTEARR = 13,
          ITERATOR = 14,
          /**
           * this is a special tag signals an end. No value is associated with it
           */
          END = 15,

          SOLRINPUTDOC = 16,
          MAP_ENTRY_ITER = 17,
          ENUM_FIELD_VALUE = 18,
          MAP_ENTRY = 19,
          // types that combine tag + length (or other info) in a single byte
          TAG_AND_LEN = (byte) (1 << 5),
          STR = (byte) (1 << 5),
          SINT = (byte) (2 << 5),
          SLONG = (byte) (3 << 5),
          ARR = (byte) (4 << 5), //
          ORDERED_MAP = (byte) (5 << 5), // SimpleOrderedMap (a NamedList subclass, and more common)
          NAMED_LST = (byte) (6 << 5), // NamedList
          EXTERN_STRING = (byte) (7 << 5);

  private static final int MAX_UTF8_SIZE_FOR_ARRAY_GROW_STRATEGY = 65536;


  private static byte VERSION = 2;
  private final ObjectResolver resolver;
  protected DataOutputStream daos;
  private StringCache stringCache;
  private WritableDocFields writableDocFields;
  private boolean alreadyMarshalled;
  private boolean alreadyUnmarshalled;

  public JavaBinCodec() {
    resolver =null;
    writableDocFields =null;
  }

  /**
   * Use this to use this as a PushWriter. ensure that close() is called explicitly after use
   *
   * @param os The output stream
   */
  public JavaBinCodec(OutputStream os, ObjectResolver resolver) throws IOException {
    this.resolver = resolver;
    initWrite(os);
  }

  public JavaBinCodec(ObjectResolver resolver) {
    this(resolver, null);
  }
  public JavaBinCodec setWritableDocFields(WritableDocFields writableDocFields){
    this.writableDocFields = writableDocFields;
    return this;

  }

  public JavaBinCodec(ObjectResolver resolver, StringCache stringCache) {
    this.resolver = resolver;
    this.stringCache = stringCache;
  }

  public ObjectResolver getResolver() {
    return resolver;
  }
  
  public void marshal(Object nl, OutputStream os) throws IOException {
    try {
      initWrite(os);
      writeVal(nl);
    } finally {
      alreadyMarshalled = true;
      daos.flush();
    }
  }

  protected void initWrite(OutputStream os) throws IOException {
    assert !alreadyMarshalled;
    init(wrapOutputStream(os));
    daos.writeByte(VERSION);
  }

  private static DataOutputStream wrapOutputStream(OutputStream os) {
	return os instanceof DataOutputStream ? (DataOutputStream) os : new DataOutputStream(os);
  }

  /** expert: sets a new output stream */
  public void init(DataOutputStream os) {
    daos = os;
  }

  byte version;

  public Object unmarshal(InputStream is) throws IOException {
    DataInputStream dis = initRead(is);
    return readVal(dis);
  }

  protected DataInputStream initRead(InputStream is) throws IOException {
    assert !alreadyUnmarshalled;
    DataInputStream dis = wrapInputStream(is);
    version = dis.readByte();
    if (version != VERSION) {
      throw new RuntimeException("Invalid version (expected " + VERSION +
          ", but " + version + ") or the data in not in 'javabin' format");
    }

    alreadyUnmarshalled = true;
    return dis;
  }

  private static DataInputStream wrapInputStream(InputStream is) {
	return is instanceof DataInputStream ? (DataInputStream) is : new DataInputStream(is);
  }

  public void writeVal(Object val) throws IOException {
    if (writeKnownType(val)) {
      return;
    } else {
      ObjectResolver resolver = null;
      if(val instanceof ObjectResolver) {
        resolver = (ObjectResolver)val;
      }
      else {
        resolver = this.resolver;
      }
      if (resolver != null) {
        Object tmpVal = resolver.resolve(val, this);
        if (tmpVal == null) return; // null means the resolver took care of it fully
        if (writeKnownType(tmpVal)) return;
      }
    }
    // Fallback to do *something*.
    // note: if the user of this codec doesn't want this (e.g. UpdateLog) it can supply an ObjectResolver that does
    //  something else like throw an exception.
    writeVal(val.getClass().getName() + ':' + val.toString());
  }

  protected static final Object END_OBJ = new Object();

  protected byte tagByte;

  public Object readVal(DataInputStream dis) throws IOException {
    tagByte = dis.readByte();
    return readObject(dis);
  }

  protected Object readObject(DataInputStream dis) throws IOException {
    // if ((tagByte & 0xe0) == 0) {
    // if top 3 bits are clear, this is a normal tag

    // OK, try type + size in single byte
    switch (tagByte >>> 5) {
      case STR >>> 5:
        return readStr(dis);
      case SINT >>> 5:
        return readSmallInt(dis);
      case SLONG >>> 5:
        return readSmallLong(dis);
      case ARR >>> 5:
        return readArray(dis);
      case EXTERN_STRING >>> 5:
        return readExternString(dis);
    }

    switch (tagByte) {
      case NULL:
        return null;
      case DATE:
        return new Date(dis.readLong());
      case INT:
        return dis.readInt();
      case BOOL_TRUE:
        return Boolean.TRUE;
      case BOOL_FALSE:
        return Boolean.FALSE;
      case FLOAT:
        return dis.readFloat();
      case DOUBLE:
        return dis.readDouble();
      case LONG:
        return dis.readLong();
      case BYTE:
        return dis.readByte();
      case SHORT:
        return dis.readShort();
      case MAP:
        return readMap(dis);
      case BYTEARR:
        return readByteArray(dis);
      case ITERATOR:
        return readIterator(dis);
      case END:
        return END_OBJ;
      case MAP_ENTRY:
        return readMapEntry(dis);
      case MAP_ENTRY_ITER:
        return readMapIter(dis);
    }

    throw new RuntimeException("Unknown type " + tagByte);
  }

  public boolean writeKnownType(Object val) throws IOException {
    if (writePrimitive(val)) return true;
    if (val instanceof Collection) {
      writeArray((Collection) val);
      return true;
    }
    if (val instanceof Object[]) {
      writeArray((Object[]) val);
      return true;
    }
    if (val instanceof Map) {
      writeMap((Map) val);
      return true;
    }
    if (val instanceof Iterator) {
      writeIterator((Iterator) val);
      return true;
    }
    if (val instanceof Path) {
      writeStr(((Path) val).toAbsolutePath().toString());
      return true;
    }
    if (val instanceof Iterable) {
      writeIterator(((Iterable) val).iterator());
      return true;
    }
    if (val instanceof Map.Entry) {
      writeMapEntry((Map.Entry)val);
      return true;
    }
    if (val instanceof AtomicInteger) {
      writeInt(((AtomicInteger) val).get());
      return true;
    }
    if (val instanceof AtomicLong) {
      writeLong(((AtomicLong) val).get());
      return true;
    }
    if (val instanceof AtomicBoolean) {
      writeBoolean(((AtomicBoolean) val).get());
      return true;
    }
    return false;
  }

  public void writeTag(byte tag) throws IOException {
    daos.writeByte(tag);
  }

  public void writeTag(byte tag, int size) throws IOException {
    if ((tag & 0xe0) != 0) {
      if (size < 0x1f) {
        daos.writeByte(tag | size);
      } else {
        daos.writeByte(tag | 0x1f);
        writeVInt(size - 0x1f, daos);
      }
    } else {
      daos.writeByte(tag);
      writeVInt(size, daos);
    }
  }

  public void writeByteArray(byte[] arr, int offset, int len) throws IOException {
    writeTag(BYTEARR, len);
    daos.write(arr, offset, len);
  }

  public byte[] readByteArray(DataInputStream dis) throws IOException {
    byte[] arr = new byte[readVInt(dis)];
    dis.readFully(arr);
    return arr;
  }
  //use this to ignore the writable interface because , child docs will ignore the fl flag
  // is it a good design?
  private boolean ignoreWritable =false;

  protected boolean toWrite(String key) {
    return writableDocFields == null || ignoreWritable || writableDocFields.isWritable(key);
  }

  public Map<Object, Object> readMapIter(DataInputStream dis) throws IOException {
    Map<Object, Object> m = newMap(-1);
    for (; ; ) {
      Object key = readVal(dis);
      if (key == END_OBJ) break;
      Object val = readVal(dis);
      m.put(key, val);
    }
    return m;
  }

  /**
   * create a new Map object
   * @param size expected size, -1 means unknown size
   */
  protected Map<Object, Object> newMap(int size) {
    return size < 0 ? new LinkedHashMap<>() : new LinkedHashMap<>(size);
  }

  public Map<Object,Object> readMap(DataInputStream dis)
          throws IOException {
    int sz = readVInt(dis);
    Map<Object, Object> m = newMap(sz);
    for (int i = 0; i < sz; i++) {
      Object key = readVal(dis);
      Object val = readVal(dis);
      m.put(key, val);

    }
    return m;
  }

  public void writeIterator(Iterator iter) throws IOException {
    writeTag(ITERATOR);
    while (iter.hasNext()) {
      writeVal(iter.next());
    }
    writeTag(END);
  }

  public List<Object> readIterator(DataInputStream fis) throws IOException {
    ArrayList<Object> l = new ArrayList<>();
    while (true) {
      Object o = readVal(fis);
      if (o == END_OBJ) break;
      l.add(o);
    }
    return l;
  }

  public void writeArray(List l) throws IOException {
    writeTag(ARR, l.size());
    for (int i = 0; i < l.size(); i++) {
      writeVal(l.get(i));
    }
  }

  public void writeArray(Collection coll) throws IOException {
    writeTag(ARR, coll.size());
    for (Object o : coll) {
      writeVal(o);
    }

  }

  public void writeArray(Object[] arr) throws IOException {
    writeTag(ARR, arr.length);
    for (int i = 0; i < arr.length; i++) {
      Object o = arr[i];
      writeVal(o);
    }
  }

  public List<Object> readArray(DataInputStream dis) throws IOException {
    int sz = readSize(dis);
    ArrayList<Object> l = new ArrayList<>(sz);
    for (int i = 0; i < sz; i++) {
      l.add(readVal(dis));
    }
    return l;
  }
  
  public void writeMapEntry(Entry<Object,Object> val) throws IOException {
    writeTag(MAP_ENTRY);
    writeVal(val.getKey());
    writeVal(val.getValue());
  }

  public Map.Entry<Object,Object> readMapEntry(DataInputStream dis) throws IOException {
    final Object key = readVal(dis);
    final Object value = readVal(dis);
    return new Map.Entry<Object,Object>() {

      @Override
      public Object getKey() {
        return key;
      }

      @Override
      public Object getValue() {
        return value;
      }

      @Override
      public String toString() {
        return "MapEntry[" + key + ":" + value + "]";
      }

      @Override
      public Object setValue(Object value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public int hashCode() {
        int result = 31;
        result *=31 + getKey().hashCode();
        result *=31 + getValue().hashCode();
        return result;
      }

      @Override
      public boolean equals(Object obj) {
        if(this == obj) {
          return true;
        }
        if(!(obj instanceof Entry)) {
          return false;
        }
        Map.Entry<Object, Object> entry = (Entry<Object, Object>) obj;
        return (this.getKey().equals(entry.getKey()) && this.getValue().equals(entry.getValue()));
      }
    };
  }

  /**
   * write the string as tag+length, with length being the number of UTF-8 bytes
   */
  public void writeStr(CharSequence s) throws IOException {
    if (s == null) {
      writeTag(NULL);
      return;
    }
    int end = s.length();
    int maxSize = end * ByteUtils.MAX_UTF8_BYTES_PER_CHAR;

    if (maxSize <= MAX_UTF8_SIZE_FOR_ARRAY_GROW_STRATEGY) {
      if (bytes == null || bytes.length < maxSize) bytes = new byte[maxSize];
      int sz = ByteUtils.UTF16toUTF8(s, 0, end, bytes, 0);
      writeTag(STR, sz);
      daos.write(bytes, 0, sz);
    } else {
      // double pass logic for large strings, see SOLR-7971
      int sz = ByteUtils.calcUTF16toUTF8Length(s, 0, end);
      writeTag(STR, sz);
      if (bytes == null || bytes.length < 8192) bytes = new byte[8192];
      ByteUtils.writeUTF16toUTF8(s, 0, end, daos, bytes);
    }
  }

  byte[] bytes;
  CharArr arr = new CharArr();
  private StringBytes bytesRef = new StringBytes(bytes,0,0);

  public String readStr(DataInputStream dis) throws IOException {
    return readStr(dis,null);
  }

  public String readStr(DataInputStream dis, StringCache stringCache) throws IOException {
    int sz = readSize(dis);
    if (bytes == null || bytes.length < sz) bytes = new byte[sz];
    dis.readFully(bytes, 0, sz);
    if (stringCache != null) {
      return stringCache.get(bytesRef.reset(bytes, 0, sz));
    } else {
      arr.reset();
      ByteUtils.UTF8toUTF16(bytes, 0, sz, arr);
      return arr.toString();
    }
  }

  public void writeInt(int val) throws IOException {
    if (val > 0) {
      int b = SINT | (val & 0x0f);

      if (val >= 0x0f) {
        b |= 0x10;
        daos.writeByte(b);
        writeVInt(val >>> 4, daos);
      } else {
        daos.writeByte(b);
      }

    } else {
      daos.writeByte(INT);
      daos.writeInt(val);
    }
  }

  public int readSmallInt(DataInputStream dis) throws IOException {
    int v = tagByte & 0x0F;
    if ((tagByte & 0x10) != 0)
      v = (readVInt(dis) << 4) | v;
    return v;
  }


  public void writeLong(long val) throws IOException {
    if ((val & 0xff00000000000000L) == 0) {
      int b = SLONG | ((int) val & 0x0f);
      if (val >= 0x0f) {
        b |= 0x10;
        daos.writeByte(b);
        writeVLong(val >>> 4, daos);
      } else {
        daos.writeByte(b);
      }
    } else {
      daos.writeByte(LONG);
      daos.writeLong(val);
    }
  }

  public long readSmallLong(DataInputStream dis) throws IOException {
    long v = tagByte & 0x0F;
    if ((tagByte & 0x10) != 0)
      v = (readVLong(dis) << 4) | v;
    return v;
  }

  public void writeFloat(float val) throws IOException {
    daos.writeByte(FLOAT);
    daos.writeFloat(val);
  }

  public boolean writePrimitive(Object val) throws IOException {
    if (val == null) {
      daos.writeByte(NULL);
      return true;
    } else if (val instanceof CharSequence) {
      writeStr((CharSequence) val);
      return true;
    } else if (val instanceof Number) {

      if (val instanceof Integer) {
        writeInt(((Integer) val).intValue());
        return true;
      } else if (val instanceof Long) {
        writeLong(((Long) val).longValue());
        return true;
      } else if (val instanceof Float) {
        writeFloat(((Float) val).floatValue());
        return true;
      } else if (val instanceof Double) {
        writeDouble(((Double) val).doubleValue());
        return true;
      } else if (val instanceof Byte) {
        daos.writeByte(BYTE);
        daos.writeByte(((Byte) val).intValue());
        return true;
      } else if (val instanceof Short) {
        daos.writeByte(SHORT);
        daos.writeShort(((Short) val).intValue());
        return true;
      }
      return false;

    } else if (val instanceof Date) {
      daos.writeByte(DATE);
      daos.writeLong(((Date) val).getTime());
      return true;
    } else if (val instanceof Boolean) {
      writeBoolean((Boolean) val);
      return true;
    } else if (val instanceof byte[]) {
      writeByteArray((byte[]) val, 0, ((byte[]) val).length);
      return true;
    } else if (val instanceof ByteBuffer) {
      ByteBuffer buf = (ByteBuffer) val;
      writeByteArray(buf.array(),buf.position(),buf.limit() - buf.position());
      return true;
    } else if (val == END_OBJ) {
      writeTag(END);
      return true;
    }
    return false;
  }

  protected void writeBoolean(boolean val) throws IOException {
    if (val) daos.writeByte(BOOL_TRUE);
    else daos.writeByte(BOOL_FALSE);
  }

  protected void writeDouble(double val) throws IOException {
    daos.writeByte(DOUBLE);
    daos.writeDouble(val);
  }


  public void writeMap(Map<?,?> val) throws IOException {
    writeTag(MAP, val.size());
    for (Map.Entry<?,?> entry : val.entrySet()) {
      Object key = entry.getKey();
      if (key instanceof String) {
        writeExternString((String) key);
      } else {
        writeVal(key);
      }
      writeVal(entry.getValue());
    }
  }


  public int readSize(DataInputStream in) throws IOException {
    int sz = tagByte & 0x1f;
    if (sz == 0x1f) sz += readVInt(in);
    return sz;
  }


  /**
   * Special method for variable length int (copied from lucene). Usually used for writing the length of a
   * collection/array/map In most of the cases the length can be represented in one byte (length &lt; 127) so it saves 3
   * bytes/object
   *
   * @throws IOException If there is a low-level I/O error.
   */
  public static void writeVInt(int i, DataOutputStream out) throws IOException {
    while ((i & ~0x7F) != 0) {
      out.writeByte((byte) ((i & 0x7f) | 0x80));
      i >>>= 7;
    }
    out.writeByte((byte) i);
  }

  /**
   * The counterpart for {@link #writeVInt(int, DataOutputStream)}
   *
   * @throws IOException If there is a low-level I/O error.
   */
  public static int readVInt(DataInputStream in) throws IOException {
    byte b = in.readByte();
    int i = b & 0x7F;
    for (int shift = 7; (b & 0x80) != 0; shift += 7) {
      b = in.readByte();
      i |= (b & 0x7F) << shift;
    }
    return i;
  }


  public static void writeVLong(long i, DataOutputStream out) throws IOException {
    while ((i & ~0x7F) != 0) {
      out.writeByte((byte) ((i & 0x7f) | 0x80));
      i >>>= 7;
    }
    out.writeByte((byte) i);
  }

  public static long readVLong(DataInputStream in) throws IOException {
    byte b = in.readByte();
    long i = b & 0x7F;
    for (int shift = 7; (b & 0x80) != 0; shift += 7) {
      b = in.readByte();
      i |= (long) (b & 0x7F) << shift;
    }
    return i;
  }

  private int stringsCount = 0;
  private Map<String, Integer> stringsMap;
  private List<String> stringsList;

  public void writeExternString(String s) throws IOException {
    if (s == null) {
      writeTag(NULL);
      return;
    }
    Integer idx = stringsMap == null ? null : stringsMap.get(s);
    if (idx == null) idx = 0;
    writeTag(EXTERN_STRING, idx);
    if (idx == 0) {
      writeStr(s);
      if (stringsMap == null) stringsMap = new HashMap<>();
      stringsMap.put(s, ++stringsCount);
    }

  }

  public String readExternString(DataInputStream fis) throws IOException {
    int idx = readSize(fis);
    if (idx != 0) {// idx != 0 is the index of the extern string
      return stringsList.get(idx - 1);
    } else {// idx == 0 means it has a string value
      tagByte = fis.readByte();
      String s = readStr(fis, stringCache);
      if (stringsList == null) stringsList = new ArrayList<>();
      stringsList.add(s);
      return s;
    }
  }

  /**
   * Allows extension of {@link JavaBinCodec} to support serialization of arbitrary data types.
   * <p>
   * Implementors of this interface write a method to serialize a given object using an existing {@link JavaBinCodec}
   */
  public interface ObjectResolver {
    /**
     * Examine and attempt to serialize the given object, using a {@link JavaBinCodec} to write it to a stream.
     *
     * @param o     the object that the caller wants serialized.
     * @param codec used to actually serialize {@code o}.
     * @return the object {@code o} itself if it could not be serialized, or {@code null} if the whole object was successfully serialized.
     * @see JavaBinCodec
     */
    Object resolve(Object o, JavaBinCodec codec) throws IOException;
  }

  public interface WritableDocFields {
    boolean isWritable(String name);
    boolean wantsAllFields();
  }


  public static class StringCache {
    private final Cache<StringBytes, String> cache;

    public StringCache(Cache<StringBytes, String> cache) {
      this.cache = cache;
    }

    public String get(StringBytes b) {
      String result = cache.getIfPresent(b);
      if (result == null) {
        //make a copy because the buffer received may be changed later by the caller
        StringBytes copy = new StringBytes(Arrays.copyOfRange(b.bytes, b.offset, b.offset + b.length), 0, b.length);
        CharArr arr = new CharArr();
        ByteUtils.UTF8toUTF16(b.bytes, b.offset, b.length, arr);
        result = arr.toString();
        cache.put(copy, result);
      }
      return result;
    }
  }

  public static class StringBytes {
    byte[] bytes;

    /**
     * Offset of first valid byte.
     */
    int offset;

    /**
     * Length of used bytes.
     */
    private int length;
    private int hash;

    public StringBytes(byte[] bytes, int offset, int length) {
      reset(bytes, offset, length);
    }

    StringBytes reset(byte[] bytes, int offset, int length) {
      this.bytes = bytes;
      this.offset = offset;
      this.length = length;
      hash = bytes == null ? 0 : Hashing.murmur3_32().hashBytes(bytes, offset, length).asInt();
      return this;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null) {
        return false;
      }
      if (other instanceof StringBytes) {
        return this.bytesEquals((StringBytes) other);
      }
      return false;
    }

    boolean bytesEquals(StringBytes other) {
      assert other != null;
      if (length == other.length) {
        int otherUpto = other.offset;
        final byte[] otherBytes = other.bytes;
        final int end = offset + length;
        for (int upto = offset; upto < end; upto++, otherUpto++) {
          if (bytes[upto] != otherBytes[otherUpto]) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  public void close() throws IOException {
    if (daos != null) {
      daos.flush();
    }
  }
}
