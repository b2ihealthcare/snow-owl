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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Eike Stepper
 */
public final class ZIPUtil
{
  public static final int DEFALULT_BUFFER_SIZE = 4096;

  private static final int ORDER_KEEP = -1;

  private static final int ORDER_SWAP = 1;

  private ZIPUtil()
  {
  }

  public static void zip(ZipEntryHandler handler, File zipFile) throws IORuntimeException
  {
    final byte[] buffer = new byte[DEFALULT_BUFFER_SIZE];
    final EntryContext context = new EntryContext();

    FileOutputStream fos = IOUtil.openOutputStream(zipFile);
    ZipOutputStream zos = null;
    InputStream input = null;
    ZipEntry entry = null;

    try
    {
      zos = new ZipOutputStream(new BufferedOutputStream(fos, DEFALULT_BUFFER_SIZE));
      for (;;)
      {
        handler.handleEntry(context);
        if (context.isEmpty())
        {
          break;
        }

        try
        {
          String name = context.getName().replace(File.separatorChar, '/');
          entry = new ZipEntry(name);
          zos.putNextEntry(entry);

          if (!context.isDirectory())
          {
            input = context.getInputStream();
            if (input == null)
            {
              throw new IllegalStateException("Input is null for zip entry " + name); //$NON-NLS-1$
            }

            IOUtil.copy(input, zos, buffer);
          }
        }
        finally
        {
          IOUtil.closeSilent(input);
          if (entry != null)
          {
            zos.closeEntry();
          }

          context.reset();
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(zos);
      IOUtil.closeSilent(fos);
    }
  }

  public static void zip(File sourceFolder, boolean excludeRoot, File zipFile)
  {
    zip(new FileSystemZipHandler(sourceFolder, excludeRoot), zipFile);
  }

  public static void unzip(File zipFile, UnzipHandler handler) throws IORuntimeException
  {
    FileInputStream fis = IOUtil.openInputStream(zipFile);
    ZipInputStream zis = null;

    try
    {
      zis = new ZipInputStream(new BufferedInputStream(fis, DEFALULT_BUFFER_SIZE));

      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null)
      {
        if (entry.isDirectory())
        {
          handler.unzipDirectory(entry.getName());
        }
        else
        {
          // TODO Provide delegating InputStream that ignores close()
          handler.unzipFile(entry.getName(), zis);
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(zis);
      IOUtil.closeSilent(fis);
    }
  }

  public static void unzip(File zipFile, File targetFolder) throws IORuntimeException
  {
    unzip(zipFile, new FileSystemUnzipHandler(targetFolder, DEFALULT_BUFFER_SIZE));
  }

  /**
   * @author Eike Stepper
   */
  public interface ZipEntryHandler
  {
    public void handleEntry(EntryContext context) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  public interface UnzipHandler
  {
    public void unzipDirectory(String name) throws IOException;

    public void unzipFile(String name, InputStream zipStream) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class EntryContext
  {
    private static final String EMPTY = new String();

    private String name = EMPTY;

    private InputStream inputStream;

    private boolean directory;

    EntryContext()
    {
    }

    void reset()
    {
      name = null;
      inputStream = null;
    }

    boolean isEmpty()
    {
      return name == null;
    }

    boolean isDirectory()
    {
      return directory;
    }

    String getName()
    {
      return name;
    }

    InputStream getInputStream()
    {
      return inputStream;
    }

    public void setName(String name, boolean directory)
    {
      this.name = name + (directory ? "/" : ""); //$NON-NLS-1$ //$NON-NLS-2$
      this.directory = directory;
    }

    public void setInputStream(InputStream inputStream)
    {
      this.inputStream = inputStream;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FileSystemZipHandler implements ZipEntryHandler
  {
    private int sourceFolderLength;

    private transient Iterator<File> files;

    public FileSystemZipHandler(File sourceFolder, boolean excludeRoot)
    {
      File root = excludeRoot ? sourceFolder : sourceFolder.getParentFile();
      sourceFolderLength = root.getAbsolutePath().length();
      if (excludeRoot)
      {
        ++sourceFolderLength;
      }

      final int baseLength = sourceFolder.getAbsolutePath().length();
      List<File> list = IOUtil.listBreadthFirst(sourceFolder);
      Collections.sort(list, new Comparator<File>()
      {
        public int compare(File f1, File f2)
        {
          String path1 = getPath(f1, baseLength);
          String path2 = getPath(f2, baseLength);
          if (path1.length() == 0)
          {
            return ORDER_KEEP;
          }

          if (path2.length() == 0)
          {
            return ORDER_SWAP;
          }

          if (f1.isDirectory())
          {
            if (f2.isDirectory())
            {
              // f1=dir, f2=dir
              if (path1.equalsIgnoreCase("/META-INF")) //$NON-NLS-1$
              {
                return ORDER_KEEP;
              }

              if (path2.equalsIgnoreCase("/META-INF")) //$NON-NLS-1$
              {
                return ORDER_SWAP;
              }

              return path1.compareTo(path2);
            }

            // f1=dir, f2=file
            if (path1.equalsIgnoreCase("/META-INF")) //$NON-NLS-1$
            {
              return ORDER_KEEP;
            }

            if (path2.equalsIgnoreCase("/META-INF/MANIFEST.MF")) //$NON-NLS-1$
            {
              return ORDER_SWAP;
            }

            return ORDER_KEEP;
          }

          if (f2.isDirectory())
          {
            // f1=file, f2=dir
            if (path2.equalsIgnoreCase("/META-INF")) //$NON-NLS-1$
            {
              return ORDER_SWAP;
            }

            if (path1.equalsIgnoreCase("/META-INF/MANIFEST.MF")) //$NON-NLS-1$
            {
              return ORDER_KEEP;
            }

            return ORDER_SWAP;
          }

          // f1=file, f2=file
          if (path1.equalsIgnoreCase("/META-INF/MANIFEST.MF")) //$NON-NLS-1$
          {
            return ORDER_KEEP;
          }

          if (path2.equalsIgnoreCase("/META-INF/MANIFEST.MF")) //$NON-NLS-1$
          {
            return ORDER_SWAP;
          }

          return path1.compareTo(path2);
        }

        private String getPath(File file, int baseLength)
        {
          String absolutePath = file.getAbsolutePath();
          String substring = absolutePath.substring(baseLength);
          String replace = substring.replace(File.separatorChar, '/');
          return replace;
        }
      });

      files = list.iterator();
      if (excludeRoot)
      {
        files.next();
      }
    }

    public void handleEntry(EntryContext context) throws IOException
    {
      if (files.hasNext())
      {
        File file = files.next();
        String name = getName(file);
        if (name.length() != 0)
        {
          context.setName(name, file.isDirectory());

          if (file.isFile())
          {
            context.setInputStream(IOUtil.openInputStream(file));
          }
        }
      }
    }

    protected String getName(File file)
    {
      return file.getAbsolutePath().substring(sourceFolderLength);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FileSystemUnzipHandler implements UnzipHandler
  {
    private File targetFolder;

    private transient byte[] buffer;

    public FileSystemUnzipHandler(File targetFolder, int bufferSize)
    {
      this.targetFolder = targetFolder;
      buffer = new byte[bufferSize];
    }

    public File getTargetFolder()
    {
      return targetFolder;
    }

    public void unzipDirectory(String name)
    {
      File directory = new File(targetFolder, name);
      if (!directory.exists())
      {
        directory.mkdirs();
      }
    }

    public void unzipFile(String name, InputStream zipStream)
    {
      File targetFile = new File(targetFolder, name);
      if (!targetFile.getParentFile().exists())
      {
        targetFile.getParentFile().mkdirs();
      }

      FileOutputStream out = IOUtil.openOutputStream(targetFile);

      try
      {
        IOUtil.copy(zipStream, out, buffer);
      }
      finally
      {
        IOUtil.closeSilent(out);
      }
    }
  }
}
