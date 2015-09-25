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
package org.eclipse.emf.cdo.common.revision;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetImpl;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.CDOFeatureMapEntryImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisableImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheBranching;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheNonAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionKeyImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * Various static helper methods for dealing with {@link CDORevision revisions}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public final class CDORevisionUtil
{
  public static final Object UNINITIALIZED = new Uninitialized();

  private CDORevisionUtil()
  {
  }

  /**
   * Creates and returns a new memory sensitive revision cache.
   * 
   * @since 4.0
   */
  public static CDORevisionCache createRevisionCache(boolean supportingAudits, boolean supportingBranches)
  {
    if (supportingBranches)
    {
      return new CDORevisionCacheBranching();
    }

    if (supportingAudits)
    {
      return new CDORevisionCacheAuditing();
    }

    return new CDORevisionCacheNonAuditing();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionManager createRevisionManager()
  {
    return new CDORevisionManagerImpl();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionManager createRevisionManager(CDORevisionCache cache)
  {
    InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)createRevisionManager();
    revisionManager.setCache(cache);
    return revisionManager;
  }

  /**
   * @since 4.0
   */
  public static CDORevisable copyRevisable(CDORevisable source)
  {
    return new CDORevisableImpl(source);
  }

  /**
   * @since 4.0
   */
  public static CDORevisable createRevisable(CDOBranch branch, int version, long timeStamp, long revised)
  {
    return new CDORevisableImpl(branch, version, timeStamp, revised);
  }

  /**
   * @since 4.0
   */
  public static CDORevisionKey copyRevisionKey(CDORevisionKey source)
  {
    return new CDORevisionKeyImpl(source.getID(), source.getBranch(), source.getVersion());
  }

  /**
   * @since 3.0
   */
  public static CDORevisionKey createRevisionKey(CDOID id, CDOBranch branch, int version)
  {
    return new CDORevisionKeyImpl(id, branch, version);
  }

  /**
   * @since 4.0
   */
  public static String formatRevisionKey(CDORevisionKey key)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, key.getID());
    builder.append(":");
    builder.append(key.getBranch().getID());
    builder.append(":");
    builder.append(key.getVersion());
    return builder.toString();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionKey parseRevisionKey(String source, CDOBranchManager branchManager)
  {
    StringTokenizer tokenizer = new StringTokenizer(source, ":");
    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No ID segment");
    }

    String idSegment = tokenizer.nextToken();
    CDOID id = CDOIDUtil.read(idSegment);

    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No branch segment");
    }

    String branchSegment = tokenizer.nextToken();
    CDOBranch branch = branchManager.getBranch(Integer.parseInt(branchSegment));

    if (!tokenizer.hasMoreTokens())
    {
      throw new IllegalArgumentException("No version segment");
    }

    String versionSegment = tokenizer.nextToken();
    int version = Integer.parseInt(versionSegment);

    return new CDORevisionKeyImpl(id, branch, version);
  }

  /**
   * @since 2.0
   */
  public static FeatureMap.Entry createFeatureMapEntry(EStructuralFeature feature, Object value)
  {
    return new CDOFeatureMapEntryImpl(feature, value);
  }

  /**
   * @since 3.0
   */
  public static CDOFeatureMapEntry createCDOFeatureMapEntry()
  {
    return new CDOFeatureMapEntryImpl();
  }

  /**
   * @since 4.0
   */
  public static CDORevisionDelta createDelta(CDORevision revision)
  {
    return new CDORevisionDeltaImpl(revision);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, final CDOBranchPoint startPoint,
      final CDOBranchPoint endPoint, final CDORevisionManager revisionManager)
  {
    CDORevisionProvider startProvider = new ManagedRevisionProvider(revisionManager, startPoint);
    CDORevisionProvider endProvider = new ManagedRevisionProvider(revisionManager, endPoint);
    return createChangeSetData(ids, startProvider, endProvider);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, CDORevisionProvider startProvider,
      CDORevisionProvider endProvider)
  {
    return createChangeSetData(ids, startProvider, endProvider, false);
  }

  /**
   * @since 4.1
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, CDORevisionProvider startProvider,
      CDORevisionProvider endProvider, boolean useStartVersions)
  {
    List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>();
    List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<CDOIDAndVersion>();
    for (CDOID id : ids)
    {
      CDORevision startRevision = startProvider.getRevision(id);
      CDORevision endRevision = endProvider.getRevision(id);

      if (startRevision == null && endRevision != null)
      {
        if (useStartVersions)
        {
          ((InternalCDORevision)endRevision).setVersion(0);
        }

        newObjects.add(endRevision);
      }
      else if (startRevision != null && endRevision == null)
      {
        detachedObjects.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }
      else if (startRevision != null && endRevision != null)
      {
        if (!startRevision.equals(endRevision))
        {
          if (useStartVersions)
          {
            ((InternalCDORevision)endRevision).setVersion(startRevision.getVersion());
          }

          CDORevisionDelta delta = endRevision.compare(startRevision);
          if (!delta.isEmpty())
          {
            changedObjects.add(delta);
          }
        }
      }
    }

    return createChangeSetData(newObjects, changedObjects, detachedObjects);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSetData createChangeSetData(List<CDOIDAndVersion> newObjects,
      List<CDORevisionKey> changedObjects, List<CDOIDAndVersion> detachedObjects)
  {
    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSet createChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    return new CDOChangeSetImpl(startPoint, endPoint, data);
  }

  /**
   * @since 3.0
   */
  public static Object remapID(Object value, Map<CDOID, CDOID> idMappings, boolean allowUnmappedTempIDs)
  {
    return CDORevisionImpl.remapID(value, idMappings, allowUnmappedTempIDs);
  }

  /**
   * @since 4.0
   */
  public static String getResourceNodePath(CDORevision revision, CDORevisionProvider provider)
  {
    EAttribute nameFeature = (EAttribute)revision.getEClass().getEStructuralFeature("name");

    StringBuilder builder = new StringBuilder();
    getResourceNodePath((InternalCDORevision)revision, provider, nameFeature, builder);

    builder.insert(0, "/");
    return builder.toString();
  }

  private static void getResourceNodePath(InternalCDORevision revision, CDORevisionProvider provider,
      EAttribute nameFeature, StringBuilder result)
  {
    String name = (String)revision.get(nameFeature, 0);
    if (name != null)
    {
      if (result.length() != 0)
      {
        result.insert(0, "/");
      }

      result.insert(0, name);
    }

    CDOID folder = (CDOID)revision.getContainerID();
    if (!CDOIDUtil.isNull(folder))
    {
      InternalCDORevision container = (InternalCDORevision)provider.getRevision(folder);
      getResourceNodePath(container, provider, nameFeature, result);
    }
  }

  /**
   * @since 3.0
   */
  public static String dumpAllRevisions(Map<CDOBranch, List<CDORevision>> map)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(baos);
    dumpAllRevisions(map, out);
    return baos.toString();
  }

  /**
   * @since 3.0
   */
  public static void dumpAllRevisions(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
  {
    new AllRevisionsDumper.Stream.Plain(map, out).dump();
  }

  /**
   * Dumps {@link CDORevision revisions}, sorted and grouped by {@link CDOBranch branch}, to various output formats and
   * targets. Concrete output formats and targets are implemented by subclasses.
   * 
   * @since 4.0
   * @apiviz.exclude
   */
  public static abstract class AllRevisionsDumper
  {
    private Map<CDOBranch, List<CDORevision>> map;

    public AllRevisionsDumper(Map<CDOBranch, List<CDORevision>> map)
    {
      this.map = map;
    }

    public Map<CDOBranch, List<CDORevision>> getMap()
    {
      return map;
    }

    public void dump()
    {
      ArrayList<CDOBranch> branches = new ArrayList<CDOBranch>(map.keySet());
      Collections.sort(branches);

      dumpStart(branches);
      for (CDOBranch branch : branches)
      {
        dumpBranch(branch);

        List<CDORevision> revisions = map.get(branch);
        Collections.sort(revisions, new CDORevisionComparator());

        for (CDORevision revision : revisions)
        {
          dumpRevision(revision);
        }
      }

      dumpEnd(branches);
    }

    protected void dumpStart(List<CDOBranch> branches)
    {
    }

    protected void dumpEnd(List<CDOBranch> branches)
    {
    }

    protected abstract void dumpBranch(CDOBranch branch);

    protected abstract void dumpRevision(CDORevision revision);

    /**
     * A {@link AllRevisionsDumper revision dumper} that directs all output to a stream. The concrete output format is
     * implemented by subclasses.
     * 
     * @author Eike Stepper
     * @apiviz.exclude
     */
    public static abstract class Stream extends AllRevisionsDumper
    {
      private PrintStream out;

      public Stream(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
      {
        super(map);
        this.out = out;
      }

      public PrintStream out()
      {
        return out;
      }

      /**
       * A {@link Stream revision dumper} that directs all output as plain text to a stream.
       * 
       * @author Eike Stepper
       * @apiviz.exclude
       */
      public static class Plain extends Stream
      {
        public static final int pad = 48;

        public Plain(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
        {
          super(map, out);
        }

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          out().println();
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          out().println(
              padTimeRange(branch.getName() + "[" + branch.getID() + "]", pad, branch.getBase().getTimeStamp(), //$NON-NLS-1$ //$NON-NLS-2$
                  CDORevision.UNSPECIFIED_DATE));
        }

        @Override
        protected void dumpRevision(CDORevision revision)
        {
          out().println(padTimeRange("  " + revision, pad, revision.getTimeStamp(), revision.getRevised())); //$NON-NLS-1$
        }

        private static String padTimeRange(String s, int pos, long t1, long t2)
        {
          StringBuffer buffer = new StringBuffer(s);
          while (buffer.length() < pos)
          {
            buffer.append(' ');
          }

          buffer.append(CDOCommonUtil.formatTimeStamp(t1));
          buffer.append("/");
          buffer.append(CDOCommonUtil.formatTimeStamp(t2));
          return buffer.toString();
        }
      }

      /**
       * A {@link Stream revision dumper} that directs all output as HTML text to a stream.
       * 
       * @author Eike Stepper
       * @apiviz.exclude
       */
      public static class Html extends Stream
      {
        public Html(Map<CDOBranch, List<CDORevision>> map, PrintStream out)
        {
          super(map, out);
        }

        @Override
        protected void dumpStart(List<CDOBranch> branches)
        {
          out().println("<table border=\"0\">");
        }

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          out().println("</table>");
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          PrintStream out = out();
          if (!branch.isMainBranch())
          {
            out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
          }

          out.println("<tr>");
          out.println("<td>");
          out.println("<h4>" + branch.getName() + "[" + branch.getID() + "]</h4>");
          out.println("</td>");
          out.println("<td>");
          out.println("<h4>" + CDOCommonUtil.formatTimeStamp(branch.getBase().getTimeStamp()) + " / "
              + CDOCommonUtil.formatTimeStamp(CDORevision.UNSPECIFIED_DATE) + "</h4>");
          out.println("</td>");
          out.println("</tr>");
        }

        @Override
        protected void dumpRevision(CDORevision revision)
        {
          PrintStream out = out();

          out.println("<tr>");
          out.println("<td>&nbsp;&nbsp;&nbsp;&nbsp;");
          dumpRevision(revision, out);
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

          out.println("<td>");
          out.println(CDOCommonUtil.formatTimeStamp(revision.getTimeStamp()) + " / "
              + CDOCommonUtil.formatTimeStamp(revision.getRevised()));
          out.println("</td>");
          out.println("</tr>");
        }

        protected void dumpRevision(CDORevision revision, PrintStream out)
        {
          out.println(revision);
        }
      }
    }
  }

  /**
   * Compares {@link CDORevisionKey revision keys} by {@link CDORevision#getID() ID} and
   * {@link CDORevision#getVersion() version}.
   * 
   * @author Eike Stepper
   * @since 4.0
   * @apiviz.exclude
   */
  public static class CDORevisionComparator implements Comparator<CDORevisionKey>
  {
    public CDORevisionComparator()
    {
    }

    public int compare(CDORevisionKey rev1, CDORevisionKey rev2)
    {
      int result = rev1.getID().compareTo(rev2.getID());
      if (result == 0)
      {
        int version1 = rev1.getVersion();
        int version2 = rev2.getVersion();
        result = version1 < version2 ? -1 : version1 == version2 ? 0 : 1;
      }

      return result;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Uninitialized
  {
    public Uninitialized()
    {
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDORevisionUtil.0"); //$NON-NLS-1$
    }
  }
}
