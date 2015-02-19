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
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class IOUtil
{
  /**
   * @since 3.1
   */
  public static final int EOF = -1;

  /**
   * @since 3.1
   */
  public static final long DEFAULT_TIMEOUT = 2500;

  public static final int DEFAULT_BUFFER_SIZE = 8192;

  /**
   * @since 2.0
   */
  public static final String WILDCARD_SINGLE_CHAR = "?"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String WILDCARD_MULTI_CHARS = "*"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String WILDCARD_MULTI_DIRS = "**"; //$NON-NLS-1$

  private static final char SEP = File.separatorChar;

  private static final char SEP_UNIX = '/';

  private static final char SEP_WINDOWS = '\\';

  private IOUtil()
  {
  }

  public static InputStream IN()
  {
    return System.in;
  }

  public static PrintStream OUT()
  {
    return System.out;
  }

  public static PrintStream ERR()
  {
    return System.err;
  }

  /**
   * @since 3.1
   */
  public static void print(StackTraceElement[] elements)
  {
    print(elements, System.err);
  }

  /**
   * @since 3.1
   */
  public static void print(StackTraceElement[] elements, PrintStream stream)
  {
    synchronized (stream)
    {
      for (int i = 0; i < elements.length; i++)
      {
        stream.println("\tat " + elements[i]);
      }
    }
  }

  public static void print(Throwable t, PrintStream stream)
  {
    t.printStackTrace(stream);
  }

  public static void print(Throwable t)
  {
    print(t, System.err);
  }

  /**
   * @since 2.0
   */
  public static String toString(Throwable t)
  {
    try
    {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      String message = t.getMessage() + "\n"; //$NON-NLS-1$
      bytes.write(message.getBytes());
      print(t, new PrintStream(bytes));

      return bytes.toString();
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static FileInputStream openInputStream(String fileName) throws IORuntimeException
  {
    return openInputStream(new File(fileName));
  }

  public static FileInputStream openInputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileInputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileOutputStream openOutputStream(String fileName) throws IORuntimeException
  {
    return openOutputStream(new File(fileName));
  }

  public static FileOutputStream openOutputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileOutputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileReader openReader(String fileName) throws IORuntimeException
  {
    return openReader(new File(fileName));
  }

  public static FileReader openReader(File file) throws IORuntimeException
  {
    try
    {
      return new FileReader(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileWriter openWriter(String fileName) throws IORuntimeException
  {
    return openWriter(new File(fileName));
  }

  public static FileWriter openWriter(File file) throws IORuntimeException
  {
    try
    {
      return new FileWriter(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static void close(Closeable closeable) throws IORuntimeException
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(org.eclipse.net4j.util.collection.Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static void close(org.eclipse.net4j.util.collection.Closeable closeable) throws IORuntimeException
  {
    if (closeable != null)
    {
      closeable.close();
    }
  }

  /**
   * @since 2.0
   */
  public static String makeRelative(File file, File toFolder)
  {
    String fileName = normalizeSeparator(file.getAbsolutePath());
    String folderName = normalizeSeparator(toFolder.getAbsolutePath());
    if (fileName.startsWith(folderName))
    {
      String relative = fileName.substring(folderName.length());
      if (relative.startsWith(File.separator))
      {
        relative = relative.substring(1);
      }

      return relative;
    }

    throw new IllegalArgumentException("Different prefixes: " + fileName + " != " + folderName); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @since 2.0
   */
  public static String normalizeSeparator(String string)
  {
    if (SEP == SEP_UNIX)
    {
      return string.replace(SEP_WINDOWS, SEP_UNIX);
    }
    else if (SEP == SEP_WINDOWS)
    {
      return string.replace(SEP_UNIX, SEP_WINDOWS);
    }

    return string;
  }

  public static void mkdirs(File folder)
  {
    if (!folder.exists())
    {
      if (!folder.mkdirs())
      {
        throw new IORuntimeException("Unable to create directory " + folder.getAbsolutePath()); //$NON-NLS-1$
      }
    }
  }

  public static int delete(File file)
  {
    if (file == null)
    {
      return 0;
    }

    int deleted = 0;
    if (file.isDirectory())
    {
      for (File child : file.listFiles())
      {
        deleted += delete(child);
      }
    }

    if (file.delete())
    {
      return deleted + 1;
    }

    file.deleteOnExit();
    return deleted;
  }

  public static void copyTree(File source, File target) throws IORuntimeException
  {
    if (source.isDirectory())
    {
      mkdirs(target);
      File[] files = source.listFiles();
      for (File file : files)
      {
        String name = file.getName();
        copyTree(new File(source, name), new File(target, name));
      }
    }
    else
    {
      copyFile(source, target);
    }
  }

  public static void copyTrees(Collection<File> sources, File target) throws IORuntimeException
  {
    for (File source : sources)
    {
      copyTree(source, target);
    }
  }

  public static void copyText(File source, File target, IOFilter<String>... lineFilters) throws IORuntimeException
  {
    BufferedReader reader = null;
    BufferedWriter writer = null;

    try
    {
      reader = new BufferedReader(openReader(source));
      writer = new BufferedWriter(openWriter(target));
      copyText(reader, writer, lineFilters);
    }
    finally
    {
      closeSilent(reader);
      closeSilent(writer);
    }
  }

  public static void copyText(BufferedReader reader, BufferedWriter writer, IOFilter<String>... lineFilters)
  {
    try
    {
      String line;
      while ((line = reader.readLine()) != null)
      {
        for (IOFilter<String> lineFilter : lineFilters)
        {
          line = lineFilter.filter(line);
        }

        writer.write(line);
        writer.newLine();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @since 3.1
   */
  public static long copyBinary(InputStream inputStream, OutputStream outputStream) throws IOException
  {
    if (!(inputStream instanceof BufferedInputStream) && !(inputStream instanceof ByteArrayInputStream))
    {
      inputStream = new BufferedInputStream(inputStream);
    }

    if (!(outputStream instanceof BufferedOutputStream) && !(outputStream instanceof ByteArrayOutputStream))
    {
      outputStream = new BufferedOutputStream(outputStream);
    }

    long size = 0;
    int b;
    while ((b = inputStream.read()) != EOF)
    {
      outputStream.write(b);
      ++size;
    }

    outputStream.flush();
    return size;
  }

  /**
   * @since 3.1
   */
  public static void copyBinary(InputStream inputStream, OutputStream outputStream, long size) throws IOException
  {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

    while (size > 0L)
    {
      int bytesToCopy = (int)Math.min(size, buffer.length);

      int bytesRead = inputStream.read(buffer, 0, bytesToCopy);
      if (bytesRead < bytesToCopy)
      {
        throw new EOFException();
      }

      outputStream.write(buffer, 0, bytesToCopy);
      size -= bytesRead;
    }

    outputStream.flush();
  }

  /**
   * @since 3.1
   */
  public static long copyCharacter(Reader reader, Writer writer) throws IOException
  {
    if (!(reader instanceof BufferedReader) && !(reader instanceof CharArrayReader))
    {
      reader = new BufferedReader(reader);
    }

    if (!(writer instanceof BufferedWriter) && !(writer instanceof CharArrayWriter))
    {
      writer = new BufferedWriter(writer);
    }

    long size = 0;
    int c;
    while ((c = reader.read()) != EOF)
    {
      writer.write(c);
      ++size;
    }

    writer.flush();
    return size;
  }

  /**
   * @since 3.1
   */
  public static void copyCharacter(Reader reader, Writer writer, long size) throws IOException
  {
    char[] buffer = new char[DEFAULT_BUFFER_SIZE];

    while (size > 0L)
    {
      int charsToCopy = (int)Math.min(size, buffer.length);

      int charsRead = reader.read(buffer, 0, charsToCopy);
      if (charsRead < charsToCopy)
      {
        throw new EOFException();
      }

      writer.write(buffer, 0, charsRead);
      size -= charsRead;
    }

    writer.flush();
  }

  public static int copy(InputStream input, OutputStream output, int size, byte buffer[]) throws IORuntimeException
  {
    try
    {
      int written = 0;
      int bufferSize = buffer.length;
      int n = Math.min(size, bufferSize);
      while (n > 0 && (n = input.read(buffer, 0, n)) != -1)
      {
        output.write(buffer, 0, n);
        written += n;
        size -= n;
        n = Math.min(size, bufferSize);
      }

      return written;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, byte buffer[]) throws IORuntimeException
  {
    try
    {
      int n;
      while ((n = input.read(buffer)) != -1)
      {
        output.write(buffer, 0, n);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IORuntimeException
  {
    copy(input, output, new byte[bufferSize]);
  }

  public static void copy(InputStream input, OutputStream output) throws IORuntimeException
  {
    copy(input, output, DEFAULT_BUFFER_SIZE);
  }

  /**
   * @see NIOUtil#copyFile(File, File)
   */
  public static void copyFile(File source, File target) throws IORuntimeException
  {
    mkdirs(target.getParentFile());
    FileInputStream input = null;
    FileOutputStream output = null;

    try
    {
      input = openInputStream(source);
      output = openOutputStream(target);
      copy(input, output);
    }
    finally
    {
      closeSilent(input);
      closeSilent(output);
    }
  }

  /**
   * @since 3.1
   */
  public static String readTextFile(File file) throws IORuntimeException
  {
    Reader input = openReader(file);

    try
    {
      CharArrayWriter output = new CharArrayWriter();
      copyCharacter(input, output);
      return output.toString();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static byte[] readFile(File file) throws IORuntimeException
  {
    if (file.length() > Integer.MAX_VALUE)
    {
      throw new IllegalArgumentException("File too long: " + file.length()); //$NON-NLS-1$
    }

    int size = (int)file.length();
    FileInputStream input = openInputStream(file);

    try
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream(size);
      copy(input, output);
      return output.toByteArray();
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static void writeFile(File file, byte[] bytes) throws IORuntimeException
  {
    FileOutputStream output = openOutputStream(file);

    try
    {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      copy(input, output);
    }
    finally
    {
      closeSilent(output);
    }
  }

  public static List<File> listDepthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitDepthFirst(file, collector);
    return collector.getFiles();
  }

  public static List<File> listBreadthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitBreadthFirst(file, collector);
    return collector.getFiles();
  }

  public static void visitDepthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean recurse = visitor.visit(file);
      if (recurse && file.isDirectory())
      {
        visitDepthFirst(file.listFiles(), visitor);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void visitDepthFirst(File[] files, IOVisitor visitor)
  {
    for (File file : files)
    {
      visitDepthFirst(file, visitor);
    }
  }

  public static void visitBreadthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    File[] files = { file };
    visitBreadthFirst(files, visitor);
  }

  public static void visitBreadthFirst(File[] files, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean[] recurse = new boolean[files.length];
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        recurse[i] = visitor.visit(file);
      }

      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        if (file.isDirectory() && recurse[i])
        {
          File[] children = file.listFiles();
          for (File child : children)
          {
            visitBreadthFirst(child, visitor);
          }
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static <IO extends Closeable> void safeRun(IO io, IORunnable<IO> runnable) throws IORuntimeException
  {
    try
    {
      runnable.run(io);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      close(io);
    }
  }

  public static void safeInput(File file, IORunnable<FileInputStream> runnable) throws IORuntimeException
  {
    safeRun(openInputStream(file), runnable);
  }

  public static void safeOutput(File file, IORunnable<FileOutputStream> runnable) throws IORuntimeException
  {
    safeRun(openOutputStream(file), runnable);
  }

  public static void safeRead(File file, IORunnable<FileReader> runnable) throws IORuntimeException
  {
    safeRun(openReader(file), runnable);
  }

  public static void safeWrite(File file, IORunnable<FileWriter> runnable) throws IORuntimeException
  {
    safeRun(openWriter(file), runnable);
  }

  public static boolean equals(InputStream stream1, InputStream stream2) throws IORuntimeException
  {
    try
    {
      for (;;)
      {
        int byte1 = stream1.read();
        int byte2 = stream2.read();

        if (byte1 != byte2)
        {
          return false;
        }

        if (byte1 == -1)// Implies byte2 == -1
        {
          return true;
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @since 3.2
   */
  public static boolean equals(Reader reader1, Reader reader2) throws IORuntimeException
  {
    try
    {
      for (;;)
      {
        int char1 = reader1.read();
        int char2 = reader2.read();

        if (char1 != char2)
        {
          return false;
        }

        if (char1 == -1)// Implies char2 == -1
        {
          return true;
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static boolean equals(File file1, File file2) throws IORuntimeException
  {
    if (file1.length() != file2.length())
    {
      return false;
    }

    FileInputStream stream1 = null;
    FileInputStream stream2 = null;

    try
    {
      stream1 = new FileInputStream(file1);
      stream2 = new FileInputStream(file2);
      return equals(stream1, stream2);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(stream1);
      closeSilent(stream2);
    }
  }

  /**
   * @since 2.0
   */
  public static List<File> glob(String pattern, File folder)
  {
    List<File> result = new ArrayList<File>();
    pattern = normalizeSeparator(pattern);
    if (pattern.endsWith(File.separator))
    {
      pattern += WILDCARD_MULTI_DIRS;
    }

    globRecurse(pattern, folder, result);
    return result;
  }

  private static void globRecurse(String pattern, File folder, List<File> result)
  {
    int sep = pattern.indexOf(SEP);
    if (sep != -1)
    {
      globSegment(pattern.substring(0, sep), pattern.substring(sep + 1), folder, result);
    }
    else
    {
      globSegment(pattern, null, folder, result);
    }
  }

  private static void globSegment(String segment, String pattern, File folder, List<File> result)
  {
    boolean multiDirs = false;
    if (segment.contains(WILDCARD_MULTI_DIRS))
    {
      if (!segment.equals(WILDCARD_MULTI_DIRS))
      {
        throw new IllegalArgumentException("Invalid pattern segment: " + segment); //$NON-NLS-1$
      }

      multiDirs = true;
    }

    for (File file : folder.listFiles())
    {
      String tmp = segment;
      if (multiDirs && file.isDirectory())
      {
        globRecurse(WILDCARD_MULTI_DIRS + File.separator + pattern, file, result);
        tmp = WILDCARD_MULTI_CHARS;
      }

      if (StringUtil.glob(tmp, file.getName()))
      {
        if (pattern == null)
        {
          // Match
          result.add(file);
        }
        else if (file.isDirectory())
        {
          // Recurse
          globRecurse(pattern, file, result);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FileCollector implements IOVisitor
  {
    private List<File> files = new ArrayList<File>();

    public FileCollector()
    {
    }

    public List<File> getFiles()
    {
      return files;
    }

    public boolean visit(File file) throws IOException
    {
      files.add(file);
      return true;
    }
  }
}
