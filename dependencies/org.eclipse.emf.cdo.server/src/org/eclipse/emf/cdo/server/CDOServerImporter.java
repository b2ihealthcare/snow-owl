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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.ExtResourceSet;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.AsyncOutputStream;
import org.eclipse.net4j.util.io.AsyncWriter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Imports the complete contents of a {@link IRepository repository} from the output created by a
 * {@link CDOServerExporter exporter} into a new repository.
 * <p>
 * Subtypes specifiy the actual exchange format.
 *
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.has {@link CDOServerImporter.Handler}
 */
public abstract class CDOServerImporter
{
  private InternalRepository repository;

  public CDOServerImporter(IRepository repository)
  {
    this.repository = (InternalRepository)repository;
    init();
  }

  private void init()
  {
    LifecycleUtil.checkInactive(repository);
    repository.setSkipInitialization(true);
    repository.getStore().setDropAllDataOnActivate(true);
    LifecycleUtil.activate(repository);
  }

  protected final InternalRepository getRepository()
  {
    return repository;
  }

  public void importRepository(InputStream in) throws Exception
  {
    try
    {
      FlushHandler handler = new FlushHandler();
      importAll(in, handler);
      handler.flush();
    }
    finally
    {
      StoreThreadLocal.release();
      repository = null;
    }
  }

  protected abstract void importAll(InputStream in, Handler handler) throws Exception;

  /**
   * Persists the data that has been read by a {@link CDOServerImporter importer} into a new {@link IRepository
   * repository}.
   *
   * @author Eike Stepper
   */
  public static interface Handler extends CDORevisionHandler, CDOLobHandler
  {
    public void handleRepository(String name, String uuid, CDOID root, long created, long committed);

    public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data);

    public InternalCDOPackageInfo handlePackageInfo(String packageURI);

    public InternalCDOPackageRegistry handleModels();

    public InternalCDOBranch handleBranch(int id, String name, long time, int parentID);

    public void handleCommitInfo(long time, long previous, int branch, String user, String comment);

    public void flush();
  }

  /**
   * @author Eike Stepper
   */
  private final class FlushHandler implements Handler
  {
    private OMMonitor monitor = new Monitor();

    private IStoreAccessor.Raw accessor;

    private Map<String, String> models = new HashMap<String, String>();

    private LinkedList<InternalCDOPackageUnit> packageUnits = new LinkedList<InternalCDOPackageUnit>();

    private List<InternalCDOPackageInfo> packageInfos;

    private InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry(false);

    public FlushHandler()
    {
    }

    public void handleRepository(String name, String uuid, CDOID root, long created, long committed)
    {
      repository.getStore().setCreationTime(created);
      repository.getStore().setLastCommitTime(committed);

      InternalCDOBranchManager branchManager = repository.getBranchManager();
      repository.initMainBranch(branchManager, created);
      LifecycleUtil.activate(branchManager);

      repository.initSystemPackages();
      repository.setRootResourceID(root);

      // InternalSession session = repository.getSessionManager().openSession(null);
      // StoreThreadLocal.setSession(session);

      accessor = (IStoreAccessor.Raw)repository.getStore().getWriter(null);
      StoreThreadLocal.setAccessor(accessor);
    }

    public InternalCDOPackageUnit handlePackageUnit(String id, Type type, long time, String data)
    {
      collectPackageInfos();

      InternalCDOPackageUnit packageUnit = packageRegistry.createPackageUnit();
      packageUnit.setOriginalType(type);
      packageUnit.setTimeStamp(time);

      models.put(id, data);
      packageUnits.add(packageUnit);
      packageInfos = new ArrayList<InternalCDOPackageInfo>();
      return packageUnit;
    }

    public InternalCDOPackageInfo handlePackageInfo(String packageURI)
    {
      InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
      packageInfo.setPackageURI(packageURI);
      packageInfos.add(packageInfo);
      return packageInfo;
    }

    public InternalCDOPackageRegistry handleModels()
    {
      collectPackageInfos();
      InternalCDOPackageUnit[] array = packageUnits.toArray(new InternalCDOPackageUnit[packageUnits.size()]);
      packageUnits = null;

      final ExtResourceSet resourceSet = EMFUtil.createExtResourceSet(packageRegistry, false, false);

      PackageLoader loader = new PackageLoader()
      {
        public EPackage[] loadPackages(CDOPackageUnit packageUnit)
        {
          String id = packageUnit.getID();
          String data = models.get(id);

          EPackage ePackage = EMFUtil.createEPackage(id, data.getBytes(), false, resourceSet, true);
          return EMFUtil.getAllPackages(ePackage);
        }
      };

      packageRegistry.putPackageUnits(array, CDOPackageUnit.State.PROXY);
      for (InternalCDOPackageUnit packageUnit : array)
      {
        packageUnit.load(loader, false);
      }

      // Before we resolve, we configure the resourceSet to start delegating, which means
      // it will consult the packageRegistry for packages it didn't just load -- such as the Ecore
      // package
      resourceSet.setDelegating(true);
      EMFUtil.safeResolveAll(resourceSet);

      accessor.rawStore(array, monitor);

      return packageRegistry;
    }

    public InternalCDOBranch handleBranch(int id, String name, long time, int parentID)
    {
      InternalCDOBranchManager branchManager = repository.getBranchManager();
      if (id == CDOBranch.MAIN_BRANCH_ID)
      {
        return branchManager.getMainBranch();
      }

      InternalCDOBranch parent = branchManager.getBranch(parentID);
      return branchManager.createBranch(id, name, parent, time);
    }

    public boolean handleRevision(CDORevision revision)
    {
      accessor.rawStore((InternalCDORevision)revision, monitor);
      return true;
    }

    public OutputStream handleBlob(final byte[] id, final long size) throws IOException
    {
      return new AsyncOutputStream()
      {
        @Override
        protected void asyncWrite(InputStream in) throws IOException
        {
          accessor.rawStore(id, size, in);
        }
      };
    }

    public Writer handleClob(final byte[] id, final long size) throws IOException
    {
      return new AsyncWriter()
      {
        @Override
        protected void asyncWrite(Reader in) throws IOException
        {
          accessor.rawStore(id, size, in);
        }
      };
    }

    public void handleCommitInfo(long time, long previous, int branchID, String user, String comment)
    {
      CDOBranch branch = repository.getBranchManager().getBranch(branchID);
      accessor.rawStore(branch, time, previous, user, comment, monitor);
    }

    public void flush()
    {
      accessor.rawCommit(1.0, monitor);
    }

    private void collectPackageInfos()
    {
      if (packageInfos != null)
      {
        InternalCDOPackageUnit packageUnit = packageUnits.getLast();
        packageUnit.setPackageInfos(packageInfos.toArray(new InternalCDOPackageInfo[packageInfos.size()]));
        packageInfos = null;
      }
    }
  }

  /**
   * An {@link CDOServerImporter importer} that reads and interprets XML output created by an
   * {@link CDOServerExporter.XML XML exporter}.
   *
   * @author Eike Stepper
   */
  public static class XML extends CDOServerImporter implements CDOServerExporter.XMLConstants
  {
    public XML(IRepository repository)
    {
      super(repository);
    }

    @Override
    protected void importAll(InputStream in, final Handler handler) throws Exception
    {
      DefaultHandler xmlHandler = new XMLHandler(handler);

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(in, xmlHandler);
    }

    /**
     * @author Eike Stepper
     */
    private static final class XMLHandler extends DefaultHandler
    {
      private Handler handler;

      private InternalCDOPackageRegistry packageRegistry;

      private InternalCDOBranch branch;

      private InternalCDORevision revision;

      private Character blobChar;

      private OutputStream blob;

      private Writer clob;

      private XMLHandler(Handler handler)
      {
        this.handler = handler;
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
      {
        if (REPOSITORY.equals(qName))
        {
          String name = attributes.getValue(REPOSITORY_NAME);
          String uuid = attributes.getValue(REPOSITORY_UUID);
          CDOID root = id(attributes.getValue(REPOSITORY_ROOT));
          long created = Long.parseLong(attributes.getValue(REPOSITORY_CREATED));
          long committed = Long.parseLong(attributes.getValue(REPOSITORY_COMMITTED));
          handler.handleRepository(name, uuid, root, created, committed);
        }
        else if (PACKAGE_UNIT.equals(qName))
        {
          String id = attributes.getValue(PACKAGE_UNIT_ID);
          Type type = CDOPackageUnit.Type.valueOf(attributes.getValue(PACKAGE_UNIT_TYPE));
          long time = Long.parseLong(attributes.getValue(PACKAGE_UNIT_TIME));
          String data = attributes.getValue(PACKAGE_UNIT_DATA);
          handler.handlePackageUnit(id, type, time, data);
        }
        else if (PACKAGE_INFO.equals(qName))
        {
          String packageURI = attributes.getValue(PACKAGE_INFO_URI);
          handler.handlePackageInfo(packageURI);
        }
        else if (BRANCH.equals(qName))
        {
          int id = Integer.parseInt(attributes.getValue(BRANCH_ID));
          String name = attributes.getValue(BRANCH_NAME);
          long time = Long.parseLong(attributes.getValue(BRANCH_TIME));
          String parent = attributes.getValue(BRANCH_PARENT);
          int parentID = parent == null ? 0 : Integer.parseInt(parent);
          branch = handler.handleBranch(id, name, time, parentID);
        }
        else if (REVISION.equals(qName))
        {
          CDOClassifierRef classifierRef = new CDOClassifierRef(attributes.getValue(REVISION_CLASS));
          EClass eClass = (EClass)classifierRef.resolve(packageRegistry);
          revision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(eClass);
          revision.setID(id(attributes.getValue(REVISION_ID)));
          revision.setBranchPoint(branch.getPoint(Long.parseLong(attributes.getValue(REVISION_TIME))));
          revision.setVersion(Integer.parseInt(attributes.getValue(REVISION_VERSION)));
          String revised = attributes.getValue(REVISION_REVISED);
          if (revised != null)
          {
            revision.setRevised(Long.parseLong(revised));
          }

          String resourceID = attributes.getValue(REVISION_RESOURCE);
          if (resourceID != null)
          {
            revision.setResourceID(id(resourceID));
          }

          String containerID = attributes.getValue(REVISION_CONTAINER);
          if (containerID != null)
          {
            revision.setContainerID(id(containerID));
          }

          String featureID = attributes.getValue(REVISION_FEATURE);
          if (featureID != null)
          {
            revision.setContainingFeatureID(Integer.parseInt(featureID));
          }
        }
        else if (FEATURE.equals(qName))
        {
          String name = attributes.getValue(FEATURE_NAME);
          Object value = value(attributes);

          EClass eClass = revision.getEClass();
          EStructuralFeature feature = eClass.getEStructuralFeature(name);
          if (feature == null)
          {
            throw new IllegalStateException("Feature " + name + " not found in class " + eClass.getName());
          }

          if (feature.isMany())
          {
            CDOList list = revision.getList(feature);
            list.add(value);
          }
          else
          {
            if (value != null)
            {
              revision.setValue(feature, value);
            }
          }
        }
        else if (BLOB.equals(qName))
        {
          try
          {
            byte[] id = HexUtil.hexToBytes(attributes.getValue(LOB_ID));
            long size = Long.parseLong(attributes.getValue(LOB_SIZE));
            blob = handler.handleBlob(id, size);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (CLOB.equals(qName))
        {
          try
          {
            byte[] id = HexUtil.hexToBytes(attributes.getValue(LOB_ID));
            long size = Long.parseLong(attributes.getValue(LOB_SIZE));
            clob = handler.handleClob(id, size);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (COMMIT.equals(qName))
        {
          long time = Long.parseLong(attributes.getValue(COMMIT_TIME));

          String value = attributes.getValue(COMMIT_PREVIOUS);
          long previous = value == null ? CDOBranchPoint.UNSPECIFIED_DATE : Long.parseLong(value);

          value = attributes.getValue(COMMIT_BRANCH);
          int branch = value == null ? CDOBranch.MAIN_BRANCH_ID : Integer.parseInt(value);

          String user = attributes.getValue(COMMIT_USER);
          String comment = attributes.getValue(COMMIT_COMMENT);

          handler.handleCommitInfo(time, previous, branch, user, comment);
        }
      }

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException
      {
        if (blob != null)
        {
          try
          {
            if (blobChar != null)
            {
              char[] firstChars = { blobChar, ch[start] };
              blobChar = null;

              byte[] firstByte = HexUtil.hexToBytes(new String(firstChars));
              blob.write(firstByte, 0, 1);

              ++start;
              --length;
            }

            if ((length & 1) == 1) // odd length?
            {
              --length;
              blobChar = ch[length];
            }

            if (start != 0 || length != ch.length)
            {
              char[] tmp = new char[length];
              System.arraycopy(ch, start, tmp, 0, length);
              ch = tmp;
            }

            byte[] buf = HexUtil.hexToBytes(new String(ch));
            blob.write(buf);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
        else if (clob != null)
        {
          try
          {
            clob.write(ch, start, length);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }

      @Override
      public void endElement(String uri, String localName, String qName) throws SAXException
      {
        if (MODELS.equals(qName))
        {
          packageRegistry = handler.handleModels();
        }
        else if (BRANCH.equals(qName))
        {
          branch = null;
        }
        else if (REVISION.equals(qName))
        {
          handler.handleRevision(revision);
          revision = null;
        }
        else if (BLOB.equals(qName))
        {
          IOUtil.close(blob);
          blob = null;
        }
        else if (CLOB.equals(qName))
        {
          IOUtil.close(clob);
          clob = null;
        }
      }

      protected final CDOID id(String str)
      {
        return CDOIDUtil.read(str);
      }

      protected Object value(Attributes attributes)
      {
        String type = attributes.getValue(FEATURE_TYPE);

        if (TYPE_BLOB.equals(type))
        {
          byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
          long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
          return CDOLobUtil.createBlob(id, size);
        }

        if (TYPE_CLOB.equals(type))
        {
          byte[] id = HexUtil.hexToBytes(attributes.getValue(FEATURE_ID));
          long size = Long.parseLong(attributes.getValue(FEATURE_SIZE));
          return CDOLobUtil.createClob(id, size);
        }

        if (TYPE_FEATURE_MAP.equals(type))
        {
          String innerFeatureName = attributes.getValue(FEATURE_INNER_FEATURE);
          EStructuralFeature innerFeature = revision.getEClass().getEStructuralFeature(innerFeatureName);

          String innerType = attributes.getValue(FEATURE_INNER_TYPE);
          Object innerValue = value(attributes, innerType);

          return CDORevisionUtil.createFeatureMapEntry(innerFeature, innerValue);
        }

        return value(attributes, type);
      }

      protected Object value(Attributes attributes, String type)
      {
        String str = attributes.getValue(FEATURE_VALUE);
        if (str == null)
        {
          return null;
        }

        if (type == null || String.class.getSimpleName().equals(type))
        {
          return str;
        }

        if (Object.class.getSimpleName().equals(type))
        {
          return id(str);
        }

        if (Boolean.class.getSimpleName().equals(type))
        {
          return Boolean.valueOf(str);
        }

        if (Character.class.getSimpleName().equals(type))
        {
          return str.charAt(0);
        }

        if (Byte.class.getSimpleName().equals(type))
        {
          return Byte.valueOf(str);
        }

        if (Short.class.getSimpleName().equals(type))
        {
          return Short.valueOf(str);
        }

        if (Integer.class.getSimpleName().equals(type))
        {
          return Integer.valueOf(str);
        }

        if (Long.class.getSimpleName().equals(type))
        {
          return Long.valueOf(str);
        }

        if (Float.class.getSimpleName().equals(type))
        {
          return Float.valueOf(str);
        }

        if (Double.class.getSimpleName().equals(type))
        {
          return Double.valueOf(str);
        }

        if (Date.class.getSimpleName().equals(type))
        {
          return new Date(Long.valueOf(str));
        }

        if (BigDecimal.class.getSimpleName().equals(type))
        {
          return new BigDecimal(str);
        }

        if (BigInteger.class.getSimpleName().equals(type))
        {
          return new BigInteger(str);
        }

        throw new IllegalArgumentException("Invalid type: " + type);
      }
    }
  }
}
