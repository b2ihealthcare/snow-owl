/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Generates 16 byte UUID values and can encode them to Strings, decode from Strings respectively.
 * 
 * @author Eike Stepper
 * @since 3.2
 */
public final class UUIDGenerator
{
  public static final int NODE_ADDRESS_BYTES = 6;

  public static final UUIDGenerator DEFAULT = new UUIDGenerator();

  public UUIDGenerator(byte[] nodeAddress)
  {
    Random random = new SecureRandom();

    clockSequence = (short)random.nextInt(16384);
    updateClockSequence();

    if (nodeAddress == null)
    {
      try
      {
        nodeAddress = getHardwareAddress();
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      if (nodeAddress == null || nodeAddress.length != NODE_ADDRESS_BYTES)
      {
        // Generate a 48 bit node identifier;
        // This is an alternative to the IEEE 802 host address, which is not available in Java.
        nodeAddress = new byte[NODE_ADDRESS_BYTES];
        random.nextBytes(nodeAddress);
      }
    }

    setNodeAddress(nodeAddress);
  }

  public UUIDGenerator()
  {
    this(null);
  }

  public synchronized String generate()
  {
    updateCurrentTime();
    encode(uuid, buffer);
    return new String(buffer);
  }

  public synchronized void generate(byte[] uuid)
  {
    updateCurrentTime();

    for (int i = 0; i < 16; i++)
    {
      uuid[i] = this.uuid[i];
    }
  }

  public String encode(byte[] uuid)
  {
    char[] buffer = createBuffer();
    encode(uuid, buffer);
    return new String(buffer);
  }

  public byte[] decode(String string)
  {
    byte[] uuid = createUUID();

    char c1;
    char c2;
    char c3;
    char c4;

    int i1;
    int i2;
    int i3;
    int i4;

    for (int i = 0; i < 5; ++i)
    {
      c1 = string.charAt(4 * i + 1);
      c2 = string.charAt(4 * i + 2);
      c3 = string.charAt(4 * i + 3);
      c4 = string.charAt(4 * i + 4);

      i1 = BASE64_INDEX[c1 - BASE64_INDEX_OFFSET];
      i2 = BASE64_INDEX[c2 - BASE64_INDEX_OFFSET];
      i3 = BASE64_INDEX[c3 - BASE64_INDEX_OFFSET];
      i4 = BASE64_INDEX[c4 - BASE64_INDEX_OFFSET];

      uuid[3 * i] = (byte)(i1 << 2 | i2 >>> 4);
      uuid[3 * i + 1] = (byte)((i2 & 0xF) << 4 | i3 >>> 2);
      uuid[3 * i + 2] = (byte)((i3 & 0x3) << 6 | i4);
    }

    // Handle the last chars at the end.
    //
    c1 = string.charAt(21);
    c2 = string.charAt(22);

    i1 = BASE64_INDEX[c1 - BASE64_INDEX_OFFSET];
    i2 = BASE64_INDEX[c2 - BASE64_INDEX_OFFSET];

    uuid[15] = (byte)(i1 << 2 | i2 >>> 4);

    return uuid;
  }

  private static final char[] BASE64_DIGITS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
      'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
      '6', '7', '8', '9', '-', '_' };

  private static final byte[] BASE64_INDEX;

  private static final int BASE64_INDEX_OFFSET;

  /**
   * An adjustment to convert the Java epoch of Jan 1, 1970 00:00:00 to the epoch required by the IETF specification,
   * Oct 15, 1582 00:00:00.
   */
  private static final long EPOCH_ADJUSTMENT = new GregorianCalendar(1970, 0, 1, 0, 0, 0).getTime().getTime()
      - new GregorianCalendar(1582, 9, 15, 0, 0, 0).getTime().getTime();

  private long lastTime = System.currentTimeMillis() + EPOCH_ADJUSTMENT;

  private short clockSequence;

  private short timeAdjustment;

  private int sleepTime = 1;

  private final char[] buffer = createBuffer();

  /**
   * A cached array of bytes representing the UUID. The second 8 bytes will be kept the same unless the clock sequence
   * has changed.
   */
  private final byte[] uuid = createUUID();

  static
  {
    byte[] index = new byte[256];
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;

    for (byte i = 0; i < BASE64_DIGITS.length; i++)
    {
      char digit = BASE64_DIGITS[i];
      if (digit < min)
      {
        min = digit;
      }

      if (digit > max)
      {
        max = digit;
      }

      index[digit] = i;
    }

    int length = max - min + 1;
    BASE64_INDEX = new byte[length];
    BASE64_INDEX_OFFSET = min;
    System.arraycopy(index, BASE64_INDEX_OFFSET, BASE64_INDEX, 0, length);
  }

  private byte[] getHardwareAddress() throws Throwable
  {
    // getHardwareAddress is a JRE 1.6 method and must be called reflectiviely
    Method method = ReflectUtil.getMethod(NetworkInterface.class, "getHardwareAddress");

    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    while (networkInterfaces.hasMoreElements())
    {
      NetworkInterface networkInterface = networkInterfaces.nextElement();

      try
      {
        byte[] nodeAddress = (byte[])ReflectUtil.invokeMethod(method, networkInterface);
        if (nodeAddress != null && nodeAddress.length == NODE_ADDRESS_BYTES)
        {
          return nodeAddress;
        }
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }
    }

    throw new SocketException("Hardware address could not be determined");
  }

  private void setNodeAddress(byte[] nodeAddress)
  {
    // Set the most significant bit of the first octet to 1 so as to distinguish it from IEEE node addresses
    //
    nodeAddress[0] |= (byte)0x80;

    // The node identifier is already in network byte order,
    // so there is no need to do any byte order reversing.
    //
    for (int i = 0; i < NODE_ADDRESS_BYTES; ++i)
    {
      uuid[i + 10] = nodeAddress[i];
    }
  }

  /**
   * Updates the clock sequence portion of the UUID. The clock sequence portion may seem odd, but in the specification,
   * the high order byte comes before the low order byte. The variant is multiplexed into the high order octet of
   * clockseq_hi.
   */
  private void updateClockSequence()
  {
    // clockseq_hi
    uuid[8] = (byte)(clockSequence >> 8 & 0x3F | 0x80);
    // clockseq_low
    uuid[9] = (byte)(clockSequence & 0xFF);
  }

  /**
   * Updates the UUID with the current time, compensating for the fact that the clock resolution may be less than 100
   * ns. The byte array will have its first eight bytes populated with the time in the correct sequence of bytes, as per
   * the specification.
   */
  private void updateCurrentTime()
  {
    // Get the current time in milliseconds since the epoch
    // and adjust it to match the epoch required by the specification.
    //
    long currentTime = System.currentTimeMillis() + EPOCH_ADJUSTMENT;

    if (lastTime > currentTime)
    {
      // The system clock has been rewound so the clock sequence must be incremented
      // to ensure that a duplicate UUID is not generated.
      //
      ++clockSequence;

      if (16384 == clockSequence)
      {
        clockSequence = 0;
      }

      updateClockSequence();
    }
    else if (lastTime == currentTime)
    {
      // The system time hasn't changed so add some increment of 100s of nanoseconds to guarantee uniqueness.
      //
      ++timeAdjustment;

      if (timeAdjustment > 9999)
      {
        // Wait so that the clock can catch up and the time adjustment won't overflow.
        try
        {
          Thread.sleep(sleepTime);
        }
        catch (InterruptedException exception)
        {
          // We just woke up.
        }

        timeAdjustment = 0;
        currentTime = System.currentTimeMillis() + EPOCH_ADJUSTMENT;

        while (lastTime == currentTime)
        {
          try
          {
            ++sleepTime;
            Thread.sleep(1);
          }
          catch (InterruptedException exception)
          {
            // We just woke up.
          }
          currentTime = System.currentTimeMillis() + EPOCH_ADJUSTMENT;
        }
      }
    }
    else
    {
      timeAdjustment = 0;
    }

    lastTime = currentTime;

    // Since the granularity of time in Java is only milliseconds,
    // add an adjustment so that the time is represented in 100s of nanoseconds.
    // The version number (1) is multiplexed into the most significant hex digit.
    //
    currentTime *= 10000;
    currentTime += timeAdjustment;
    currentTime |= 0x1000000000000000L;

    // Place the time into the byte array in network byte order.
    //
    for (int i = 0; i < 4; ++i)
    {
      // time_low
      //
      uuid[i] = (byte)(currentTime >> 8 * (3 - i) & 0xFFL);
    }

    for (int i = 0; i < 2; ++i)
    {
      // time_mid
      //
      uuid[i + 4] = (byte)(currentTime >> 8 * (1 - i) + 32 & 0xFFL);
    }

    for (int i = 0; i < 2; ++i)
    {
      // time_hi
      //
      uuid[i + 6] = (byte)(currentTime >> 8 * (1 - i) + 48 & 0xFFL);
    }
  }

  private void encode(byte[] uuid, char[] buffer)
  {
    for (int i = 0; i < 5; ++i)
    {
      buffer[4 * i + 1] = BASE64_DIGITS[uuid[i * 3] >> 2 & 0x3F];
      buffer[4 * i + 2] = BASE64_DIGITS[uuid[i * 3] << 4 & 0x30 | uuid[i * 3 + 1] >> 4 & 0xF];
      buffer[4 * i + 3] = BASE64_DIGITS[uuid[i * 3 + 1] << 2 & 0x3C | uuid[i * 3 + 2] >> 6 & 0x3];
      buffer[4 * i + 4] = BASE64_DIGITS[uuid[i * 3 + 2] & 0x3F];
    }

    // Handle the last byte at the end.
    //
    buffer[21] = BASE64_DIGITS[uuid[15] >> 2 & 0x3F];
    buffer[22] = BASE64_DIGITS[uuid[15] << 4 & 0x30];
  }

  private byte[] createUUID()
  {
    return new byte[16];
  }

  private char[] createBuffer()
  {
    char[] buffer = new char[23];
    buffer[0] = '_';
    return buffer;
  }
}
