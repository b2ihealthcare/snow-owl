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
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public final class ExtendedIOUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ExtendedIOUtil.class);

  private static final int UTF_HEADER_SIZE = 2;

  private static final int MAX_16_BIT = (1 << 16) - 1;

  private static final int MAX_UTF_LENGTH = MAX_16_BIT - UTF_HEADER_SIZE;

  private static final int MAX_UTF_CHARS = MAX_UTF_LENGTH / 3;

  private static final int MAX_ENUM_LITERALS = Byte.MAX_VALUE - Byte.MIN_VALUE;

  private static final byte NO_ENUM_LITERAL = Byte.MIN_VALUE;

  private ExtendedIOUtil()
  {
  }

  public static void writeByteArray(DataOutput out, byte[] b) throws IOException
  {
    if (b != null)
    {
      out.writeInt(b.length);
      out.write(b);
    }
    else
    {
      out.writeInt(-1);
    }
  }

  public static byte[] readByteArray(DataInput in) throws IOException
  {
    int length = in.readInt();
    if (length < 0)
    {
      return null;
    }

    byte[] b;
    try
    {
      b = new byte[length];
    }
    catch (Throwable t)
    {
      throw new IOException("Unable to allocate " + length + " bytes"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    in.readFully(b);
    return b;
  }

  public static void writeObject(final DataOutput out, Object object) throws IOException
  {
    ObjectOutput wrapper = null;
    if (out instanceof ObjectOutput)
    {
      wrapper = (ObjectOutput)out;
    }
    else
    {
      wrapper = new ObjectOutputStream(new OutputStream()
      {
        @Override
        public void write(int b) throws IOException
        {
          out.writeByte((b & 0xff) + Byte.MIN_VALUE);
        }
      });
    }

    wrapper.writeObject(object);
  }

  public static Object readObject(final DataInput in) throws IOException
  {
    return readObject(in, (ClassResolver)null);
  }

  public static Object readObject(final DataInput in, ClassLoader classLoader) throws IOException
  {
    return readObject(in, new ClassLoaderClassResolver(classLoader));
  }

  public static Object readObject(final DataInput in, final ClassResolver classResolver) throws IOException
  {
    ObjectInput wrapper = null;
    if (in instanceof ObjectInput)
    {
      wrapper = (ObjectInput)in;
    }
    else
    {
      wrapper = new ObjectInputStream(new InputStream()
      {
        @Override
        public int read() throws IOException
        {
          return in.readByte() - Byte.MIN_VALUE;
        }
      })

      {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
        {
          if (classResolver != null)
          {
            if (TRACER.isEnabled())
            {
              TRACER.format("Deserializing class {0}", desc.getName()); //$NON-NLS-1$
            }

            Class<?> c = classResolver.resolveClass(desc);
            if (c != null)
            {
              return c;
            }
          }

          return super.resolveClass(desc);
        }
      };
    }

    try
    {
      return wrapper.readObject();
    }
    catch (ClassNotFoundException ex)
    {
      OM.LOG.error(ex);
      throw WrappedException.wrap(ex);
    }
  }

  public static void writeString(DataOutput out, String str) throws IOException
  {
    if (str != null)
    {
      int size = str.length();
      int start = 0;
      do
      {
        out.writeBoolean(true);
        int chunk = Math.min(size, MAX_UTF_CHARS);
        int end = start + chunk;
        out.writeUTF(str.substring(start, end));
        start = end;
        size -= chunk;
      } while (size > 0);
    }

    out.writeBoolean(false);
  }

  public static String readString(DataInput in) throws IOException
  {
    boolean more = in.readBoolean();
    if (!more)
    {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    do
    {
      String chunk = in.readUTF();
      builder.append(chunk);
      more = in.readBoolean();
    } while (more);

    return builder.toString();
  }

  /**
   * @since 3.0
   */
  public static void writeEnum(DataOutput out, Enum<?> literal) throws IOException
  {
    if (literal == null)
    {
      out.writeByte(NO_ENUM_LITERAL);
    }
    else
    {
      getEnumLiterals(literal.getDeclaringClass()); // Check valid size

      int ordinal = literal.ordinal();
      out.writeByte(ordinal + Byte.MIN_VALUE + 1);
    }
  }

  /**
   * @since 3.0
   */
  public static <T extends Enum<?>> T readEnum(DataInput in, Class<T> type) throws IOException
  {
    T[] literals = getEnumLiterals(type);

    int ordinal = in.readByte();
    if (ordinal == NO_ENUM_LITERAL)
    {
      return null;
    }

    return literals[ordinal - Byte.MIN_VALUE - 1];
  }

  private static <T> T[] getEnumLiterals(Class<T> type)
  {
    T[] literals = type.getEnumConstants();

    int size = literals.length;
    if (size > MAX_ENUM_LITERALS)
    {
      throw new AssertionError("Enum too large: " + size + " literals");
    }

    return literals;
  }

  /**
   * @author Eike Stepper
   */
  public interface ClassResolver
  {
    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException;
  }

  /**
   * @author Eike Stepper
   */
  public static class ClassLoaderClassResolver implements ClassResolver
  {
    private static final String STACK_TRACE_ELEMENT = StackTraceElement[].class.getName();

    private ClassLoader classLoader;

    public ClassLoaderClassResolver(ClassLoader classLoader)
    {
      this.classLoader = classLoader;
    }

    public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException
    {
      String className = v.getName();

      try
      {
        return classLoader.loadClass(className);
      }
      catch (ClassNotFoundException ex)
      {
        if (!STACK_TRACE_ELEMENT.equals(className))
        {
          OM.LOG.error(ex);
        }

        return null;
      }
    }
  }
}
