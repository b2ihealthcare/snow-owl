/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent;
import org.eclipse.emf.cdo.view.CDOViewEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOLegacyAdapter;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * 
 */
public abstract class AbstractCDOView extends Lifecycle implements InternalCDOView
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_VIEW, AbstractCDOView.class);

  private final boolean legacyModeEnabled;

  private CDOBranchPoint branchPoint;

  private final CDOURIHandler uriHandler = new CDOURIHandler(this);

  private InternalCDOViewSet viewSet;

  private Map<CDOID, InternalCDOObject> objects;

  private CDOStore store = new CDOStoreImpl(this);

  private CDOResourceImpl rootResource;

  private final ConcurrentArray<CDOObjectHandler> objectHandlers = new ConcurrentArray<CDOObjectHandler>()
  {
    @Override
    protected CDOObjectHandler[] newArray(int length)
    {
      return new CDOObjectHandler[length];
    }
  };

  @ExcludeFromDump
  private transient CDOID lastLookupID;

  @ExcludeFromDump
  private transient InternalCDOObject lastLookupObject;

  public AbstractCDOView(CDOBranchPoint branchPoint, boolean legacyModeEnabled)
  {
    this(legacyModeEnabled);
    basicSetBranchPoint(branchPoint);
  }

  public AbstractCDOView(boolean legacyModeEnabled)
  {
    this.legacyModeEnabled = legacyModeEnabled;
  }

  public boolean isReadOnly()
  {
    return true;
  }

  public boolean isLegacyModeEnabled()
  {
    return legacyModeEnabled;
  }

  protected synchronized final Map<CDOID, InternalCDOObject> getModifiableObjects()
  {
    return objects;
  }

  public synchronized Map<CDOID, InternalCDOObject> getObjects()
  {
    if (objects == null)
    {
      return Collections.emptyMap();
    }

    return Collections.unmodifiableMap(objects);
  }

  protected synchronized final void setObjects(Map<CDOID, InternalCDOObject> objects)
  {
    this.objects = objects;
  }

  public CDOStore getStore()
  {
    checkActive();
    return store;
  }

  public ResourceSet getResourceSet()
  {
    return getViewSet().getResourceSet();
  }

  /**
   * @since 2.0
   */
  public InternalCDOViewSet getViewSet()
  {
    return viewSet;
  }

  /**
   * @since 2.0
   */
  public void setViewSet(InternalCDOViewSet viewSet)
  {
    this.viewSet = viewSet;
    if (viewSet != null)
    {
      viewSet.getResourceSet().getURIConverter().getURIHandlers().add(0, getURIHandler());
    }
  }

  public synchronized CDOResourceImpl getRootResource()
  {
    checkActive();
    if (rootResource == null)
    {
      CDOID rootResourceID = getSession().getRepositoryInfo().getRootResourceID();
      if (rootResourceID == null || rootResourceID.isNull())
      {
        throw new IllegalStateException("RootResourceID is null; is the repository not yet initialized?");
      }

      getObject(rootResourceID);
      CheckUtil.checkState(rootResource, "rootResource");
    }

    return rootResource;
  }

  private synchronized void setRootResource(CDOResourceImpl resource)
  {
    rootResource = resource;
    rootResource.setRoot(true);
    registerObject(rootResource);
  }

  public CDOURIHandler getURIHandler()
  {
    return uriHandler;
  }

  protected synchronized CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public synchronized boolean setBranch(CDOBranch branch)
  {
    return setBranchPoint(branch, getTimeStamp());
  }

  public synchronized boolean setTimeStamp(long timeStamp)
  {
    return setBranchPoint(getBranch(), timeStamp);
  }

  public synchronized boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return setBranchPoint(branch.getPoint(timeStamp));
  }

  protected synchronized void basicSetBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
  }

  public void waitForUpdate(long updateTime)
  {
    waitForUpdate(updateTime, NO_TIMEOUT);
  }

  public synchronized CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public synchronized long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  protected void fireViewTargetChangedEvent(IListener[] listeners)
  {
    fireEvent(new ViewTargetChangedEvent(branchPoint), listeners);
  }

  public boolean isDirty()
  {
    return false;
  }

  public boolean hasConflict()
  {
    return false;
  }

  public synchronized boolean hasResource(String path)
  {
    try
    {
      checkActive();
      getResourceNodeID(path);
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  public synchronized CDOQueryImpl createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null);
  }

  public synchronized CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    checkActive();
    return new CDOQueryImpl(this, language, queryString, context);
  }

  public synchronized CDOResourceNode getResourceNode(String path)
  {
    CDOID id = getResourceNodeID(path);
    if (id != null) // Should always be true
    {

      InternalCDOObject object = getObject(id);
      if (object instanceof CDOResourceNode)
      {
        return (CDOResourceNode)object;
      }
    }

    throw new CDOException("Resource node not found: " + path);
  }

  /**
   * @return never <code>null</code>
   */
  public synchronized CDOID getResourceNodeID(String path)
  {
    if (StringUtil.isEmpty(path))
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.1")); //$NON-NLS-1$
    }

    CDOID folderID = null;
    if (CDOURIUtil.SEGMENT_SEPARATOR.equals(path))
    {
      folderID = getResourceNodeIDChecked(null, null);
    }
    else
    {
      List<String> names = CDOURIUtil.analyzePath(path);
      for (String name : names)
      {
        folderID = getResourceNodeIDChecked(folderID, name);
      }
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   */
  private CDOID getResourceNodeIDChecked(CDOID folderID, String name)
  {
    folderID = getResourceNodeID(folderID, name);
    if (folderID == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.2"), name)); //$NON-NLS-1$
    }

    return folderID;
  }

  /**
   * @return never <code>null</code>
   */
  protected synchronized CDOResourceNode getResourceNode(CDOID folderID, String name)
  {
    try
    {
      CDOID id = getResourceNodeID(folderID, name);
      return (CDOResourceNode)getObject(id);
    }
    catch (CDOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new CDOException(ex);
    }
  }

  protected synchronized CDOID getResourceNodeID(CDOID folderID, String name)
  {
    if (folderID == null)
    {
      return getRootOrTopLevelResourceNodeID(name);
    }

    if (name == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOViewImpl.3")); //$NON-NLS-1$
    }

    InternalCDORevision folderRevision = getLocalRevision(folderID);
    EClass resourceFolderClass = EresourcePackage.eINSTANCE.getCDOResourceFolder();
    if (folderRevision.getEClass() != resourceFolderClass)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.4"), folderID)); //$NON-NLS-1$
    }

    EReference nodesFeature = EresourcePackage.eINSTANCE.getCDOResourceFolder_Nodes();
    EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();
    int size = folderRevision.data().size(nodesFeature);
    for (int i = 0; i < size; i++)
    {
      Object value = folderRevision.data().get(nodesFeature, i);
      value = getStore().resolveProxy(folderRevision, nodesFeature, i, value);

      CDORevision childRevision = getLocalRevision((CDOID)convertObjectToID(value));
      if (name.equals(childRevision.data().get(nameFeature, 0)))
      {
        return childRevision.getID();
      }
    }

    throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.5"), name)); //$NON-NLS-1$
  }

  protected synchronized CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    CDOQuery resourceQuery = createResourcesQuery(null, name, true);
    resourceQuery.setMaxResults(1);
    List<CDOID> ids = resourceQuery.getResult(CDOID.class);
    if (ids.isEmpty())
    {
      if (name == null)
      {
        throw new CDOException(Messages.getString("CDOViewImpl.6")); //$NON-NLS-1$
      }

      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.7"), name)); //$NON-NLS-1$
    }

    if (ids.size() > 1)
    {
      // TODO is this still needed since the is resourceQuery.setMaxResults(1) ??
      throw new ImplementationError(Messages.getString("CDOViewImpl.8")); //$NON-NLS-1$
    }

    return ids.get(0);
  }

  private InternalCDORevision getLocalRevision(CDOID id)
  {
    InternalCDORevision revision = null;
    InternalCDOObject object = getObject(id, false);
    if (object != null && object.cdoState() != CDOState.PROXY)
    {
      revision = object.cdoRevision();
    }

    if (revision == null)
    {
      revision = getRevision(id, true);
    }

    if (revision == null)
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOViewImpl.9"), id)); //$NON-NLS-1$
    }

    return revision;
  }

  public synchronized List<InternalCDOObject> getObjectsList()
  {
    List<InternalCDOObject> result = new ArrayList<InternalCDOObject>();
    for (InternalCDOObject value : objects.values())
    {
      if (value != null)
      {
        result.add(value);
      }
    }

    return result;
  }

  public synchronized CDOResource getResource(String path)
  {
    return getResource(path, true);
  }

  public synchronized CDOResource getResource(String path, boolean loadInDemand)
  {
    checkActive();
    URI uri = CDOURIUtil.createResourceURI(this, path);
    ResourceSet resourceSet = getResourceSet();
    ensureURIs(resourceSet); // Bug 337523

    try
    {
      return (CDOResource)resourceSet.getResource(uri, loadInDemand);
    }
    catch (RuntimeException ex)
    {
      EList<Resource> resources = resourceSet.getResources();
      for (int i = resources.size() - 1; i >= 0; --i)
      {
        Resource resource = resources.get(i);
        if (uri.equals(resource.getURI()))
        {
          resources.remove(i);
          break;
        }
      }

      throw ex;
    }
  }

  /**
   * Ensures that the URIs of all resources in this resourceSet, can be fetched without triggering the loading of
   * additional resources. Without calling this first, it is dangerous to iterate over the resources to collect their
   * URI's, because
   */
  private void ensureURIs(ResourceSet resourceSet)
  {
    EList<Resource> resources = resourceSet.getResources();
    Resource[] resourceArr = null;

    int size = 0;
    int i;

    do
    {
      i = size;
      size = resources.size();
      if (size == 0)
      {
        break;
      }

      if (resourceArr == null || resourceArr.length < size)
      {
        resourceArr = new Resource[size * 2];
      }

      resourceArr = resources.toArray(resourceArr);
      for (; i < size; i++)
      {
        resourceArr[i].getURI();
      }
    } while (resources.size() > size);
  }

  public synchronized List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResult(CDOResourceNode.class);
  }

  public synchronized CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch)
  {
    CDOQuery resourceQuery = createResourcesQuery(folder, name, exactMatch);
    return resourceQuery.getResultAsync(CDOResourceNode.class);
  }

  private CDOQuery createResourcesQuery(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    checkActive();
    CDOQueryImpl query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, name);
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID, folder == null ? null : folder.cdoID());
    query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_EXACT_MATCH, exactMatch);
    return query;
  }

  public synchronized List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences)
  {
    return queryXRefs(Collections.singleton(targetObject), sourceReferences);
  }

  public synchronized List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    CDOQuery xrefsQuery = createXRefsQuery(targetObjects, sourceReferences);
    return xrefsQuery.getResult(CDOObjectReference.class);
  }

  public synchronized CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects,
      EReference... sourceReferences)
  {
    CDOQuery xrefsQuery = createXRefsQuery(targetObjects, sourceReferences);
    return xrefsQuery.getResultAsync(CDOObjectReference.class);
  }

  private CDOQuery createXRefsQuery(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    checkActive();

    String string = createXRefsQueryString(targetObjects);
    CDOQuery query = createQuery(CDOProtocolConstants.QUERY_LANGUAGE_XREFS, string);

    if (sourceReferences.length != 0)
    {
      string = createXRefsQueryParameter(sourceReferences);
      query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_XREFS_SOURCE_REFERENCES, string);
    }

    return query;
  }

  private String createXRefsQueryString(Set<CDOObject> targetObjects)
  {
    StringBuilder builder = new StringBuilder();
    for (CDOObject target : targetObjects)
    {
      CDOID id = getXRefTargetID(target);
      if (isObjectNew(id))
      {
        throw new IllegalArgumentException("Cross referencing for uncommitted new objects not supported " + target);
      }

      if (builder.length() != 0)
      {
        builder.append("|");
      }

      builder.append(id.toURIFragment());

      if (!(id instanceof CDOClassifierRef.Provider))
      {
        builder.append("|");
        CDOClassifierRef classifierRef = new CDOClassifierRef(target.eClass());
        builder.append(classifierRef.getURI());
      }
    }

    return builder.toString();
  }

  private String createXRefsQueryParameter(EReference[] sourceReferences)
  {
    StringBuilder builder = new StringBuilder();
    for (EReference sourceReference : sourceReferences)
    {
      if (builder.length() != 0)
      {
        builder.append("|");
      }

      CDOClassifierRef classifierRef = new CDOClassifierRef(sourceReference.getEContainingClass());
      builder.append(classifierRef.getURI());
      builder.append("|");
      builder.append(sourceReference.getName());
    }

    return builder.toString();
  }

  protected synchronized CDOID getXRefTargetID(CDOObject target)
  {
    if (FSMUtil.isTransient(target))
    {
      throw new IllegalArgumentException("Cross referencing for transient objects not supported " + target);
    }

    return target.cdoID();
  }

  public synchronized CDOResourceImpl getResource(CDOID resourceID)
  {
    if (CDOIDUtil.isNull(resourceID))
    {
      throw new IllegalArgumentException("resourceID: " + resourceID); //$NON-NLS-1$
    }

    return (CDOResourceImpl)getObject(resourceID);
  }

  public synchronized InternalCDOObject newInstance(EClass eClass)
  {
    EObject eObject = EcoreUtil.create(eClass);
    return FSMUtil.adapt(eObject, this);
  }

  public synchronized InternalCDORevision getRevision(CDOID id)
  {
    return getRevision(id, true);
  }

  public synchronized InternalCDOObject getObject(CDOID id)
  {
    return getObject(id, true);
  }

  public synchronized InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (rootResource != null && rootResource.cdoID().equals(id))
    {
      return rootResource;
    }

    if (id.equals(lastLookupID))
    {
      return lastLookupObject;
    }

    lastLookupID = null;
    lastLookupObject = null;
    InternalCDOObject localLookupObject = null;

    if (id.isExternal())
    {
      URI uri = URI.createURI(((CDOIDExternal)id).getURI());
      ResourceSet resourceSet = getResourceSet();

      localLookupObject = (InternalCDOObject)CDOUtil.getCDOObject(resourceSet.getEObject(uri, loadOnDemand));
      if (localLookupObject == null)
      {
        if (!loadOnDemand)
        {
          return null;
        }

        throw new ObjectNotFoundException(id, this);
      }
    }
    else
    {
      // Needed for recursive call to getObject. (from createObject/cleanObject/getResource/getObject)
      localLookupObject = objects.get(id);
      if (localLookupObject == null)
      {
        if (!loadOnDemand)
        {
          return null;
        }

        excludeNewObject(id);
        localLookupObject = createObject(id);

        // CDOResource have a special way to register to the view.
        if (!CDOModelUtil.isResource(localLookupObject.eClass()))
        {
          registerObject(localLookupObject);
        }
        else if (id.equals(getSession().getRepositoryInfo().getRootResourceID()))
        {
          setRootResource((CDOResourceImpl)localLookupObject);
        }
      }
    }

    lastLookupID = id;
    lastLookupObject = localLookupObject;
    return lastLookupObject;
  }

  protected synchronized void excludeNewObject(CDOID id)
  {
    if (isObjectNew(id))
    {
      throw new ObjectNotFoundException(id, this);
    }
  }

  public boolean isObjectNew(CDOID id)
  {
    return id.isTemporary();
  }

  /**
   * @since 2.0
   */
  public synchronized <T extends EObject> T getObject(T objectFromDifferentView)
  {
    checkActive();
    CDOObject object = CDOUtil.getCDOObject(objectFromDifferentView);
    CDOView view = object.cdoView();
    if (view != this)
    {
      if (!view.getSession().getRepositoryInfo().getUUID().equals(getSession().getRepositoryInfo().getUUID()))
      {
        throw new IllegalArgumentException(MessageFormat.format(
            Messages.getString("CDOViewImpl.11"), objectFromDifferentView)); //$NON-NLS-1$
      }

      CDOID id = object.cdoID();
      InternalCDOObject contextified = getObject(id, true);

      @SuppressWarnings("unchecked")
      T cast = (T)CDOUtil.getEObject(contextified);
      return cast;
    }

    return objectFromDifferentView;
  }

  public synchronized boolean isObjectRegistered(CDOID id)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return false;
    }

    return objects.containsKey(id);
  }

  public synchronized InternalCDOObject removeObject(CDOID id)
  {
    if (id.equals(lastLookupID))
    {
      lastLookupID = null;
      lastLookupObject = null;
    }

    return objects.remove(id);
  }

  /**
   * @return Never <code>null</code>
   */
  private InternalCDOObject createObject(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating object for " + id); //$NON-NLS-1$
    }

    InternalCDORevision revision = getRevision(id, true);
    if (revision == null)
    {
      throw new ObjectNotFoundException(id, this);
    }

    EClass eClass = revision.getEClass();
    InternalCDOObject object;
    if (CDOModelUtil.isResource(eClass) && !id.equals(getSession().getRepositoryInfo().getRootResourceID()))
    {
      object = (InternalCDOObject)newResourceInstance(revision);
      // object is PROXY
    }
    else
    {
      object = newInstance(eClass);
      // object is TRANSIENT
    }

    cleanObject(object, revision);
    return object;
  }

  private CDOResource newResourceInstance(InternalCDORevision revision)
  {
    String path = getResourcePath(revision);
    URI uri = CDOURIUtil.createResourceURI(this, path);

    // Bug 334995: Check if locally there is already a resource with the same URI
    CDOResource resource1 = (CDOResource)getResourceSet().getResource(uri, false);
    String oldName = null;
    if (resource1 != null && !isReadOnly())
    {
      // We have no other option than to change the name of the local resource
      oldName = resource1.getName();
      resource1.setName(oldName + ".renamed");
      OM.LOG.warn("URI clash: resource being instantiated had same URI as a resource already present "
          + "locally; local resource was renamed from " + oldName + " to " + resource1.getName());
    }

    CDOResource resource2 = getResource(path, true);

    return resource2;
  }

  private String getResourcePath(InternalCDORevision revision)
  {
    EAttribute nameFeature = EresourcePackage.eINSTANCE.getCDOResourceNode_Name();

    CDOID folderID = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(nameFeature, 0);
    if (CDOIDUtil.isNull(folderID))
    {
      if (name == null)
      {
        return CDOURIUtil.SEGMENT_SEPARATOR;
      }

      return name;
    }

    InternalCDOObject object = getObject(folderID, true);
    if (object instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)object;
      String path = folder.getPath();
      return path + CDOURIUtil.SEGMENT_SEPARATOR + name;
    }

    throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.14"), object)); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  protected synchronized void cleanObject(InternalCDOObject object, InternalCDORevision revision)
  {
    object.cdoInternalSetView(this);
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetState(CDOState.CLEAN);
    object.cdoInternalPostLoad();
  }

  public synchronized CDOID provideCDOID(Object idOrObject)
  {
    Object shouldBeCDOID = convertObjectToID(idOrObject);
    if (shouldBeCDOID instanceof CDOID)
    {
      CDOID id = (CDOID)shouldBeCDOID;
      if (TRACER.isEnabled() && id != idOrObject)
      {
        TRACER.format("Converted object to CDOID: {0} --> {1}", idOrObject, id); //$NON-NLS-1$
      }

      return id;
    }

    if (idOrObject instanceof InternalEObject)
    {
      InternalEObject eObject = (InternalEObject)idOrObject;
      if (eObject instanceof InternalCDOObject)
      {
        InternalCDOObject object = (InternalCDOObject)idOrObject;
        if (object.cdoView() != null && FSMUtil.isNew(object))
        {
          String uri = EcoreUtil.getURI(eObject).toString();
          if (object.cdoID().isTemporary())
          {
            return CDOIDUtil.createTempObjectExternal(uri);
          }

          // New objects with non-temporary IDs are possible. Likely UUIDs
          return CDOIDUtil.createExternal(uri);
        }
      }

      Resource eResource = eObject.eResource();
      if (eResource != null)
      {
        // Check if eObject is contained by a deleted resource
        if (!(eResource instanceof CDOResource) || ((CDOResource)eResource).cdoState() != CDOState.TRANSIENT)
        {
          String uri = EcoreUtil.getURI(eObject).toString();
          return CDOIDUtil.createExternal(uri);
        }
      }

      throw new DanglingReferenceException(eObject);
    }

    throw new IllegalStateException(MessageFormat.format(
        Messages.getString("CDOViewImpl.16"), idOrObject.getClass().getName())); //$NON-NLS-1$
  }

  public synchronized Object convertObjectToID(Object potentialObject)
  {
    return convertObjectToID(potentialObject, false);
  }

  /**
   * @since 2.0
   */
  public synchronized Object convertObjectToID(Object potentialObject, boolean onlyPersistedID)
  {
    if (potentialObject instanceof CDOID)
    {
      return potentialObject;
    }

    if (potentialObject instanceof InternalEObject)
    {
      if (potentialObject instanceof InternalCDOObject)
      {
        InternalCDOObject object = (InternalCDOObject)potentialObject;
        CDOID id = getID(object, onlyPersistedID);
        if (id != null)
        {
          return id;
        }
      }
      else
      {
        InternalCDOObject object = (InternalCDOObject)EcoreUtil.getAdapter(
            ((InternalEObject)potentialObject).eAdapters(), CDOLegacyAdapter.class);
        if (object != null)
        {
          CDOID id = getID(object, onlyPersistedID);
          if (id != null)
          {
            return id;
          }

          potentialObject = object;
        }
      }
    }

    return potentialObject;
  }

  protected synchronized CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    if (onlyPersistedID)
    {
      if (FSMUtil.isTransient(object) || FSMUtil.isNew(object))
      {
        return null;
      }
    }

    CDOView view = object.cdoView();
    if (view == this)
    {
      return object.cdoID();
    }

    if (view != null && view.getSession() == getSession())
    {
      boolean sameTarget = view.getBranch().equals(getBranch()) && view.getTimeStamp() == getTimeStamp();
      if (sameTarget)
      {
        return object.cdoID();
      }

      throw new IllegalArgumentException("Object " + object + " is managed by a view with different target: " + view);
    }

    return null;
  }

  public synchronized Object convertIDToObject(Object potentialID)
  {
    if (potentialID instanceof CDOID)
    {
      if (potentialID == CDOID.NULL)
      {
        return null;
      }

      CDOID id = (CDOID)potentialID;
      if (id.isExternal())
      {
        return getResourceSet().getEObject(URI.createURI(id.toURIFragment()), true);
      }

      InternalCDOObject result = getObject(id, true);
      if (result == null)
      {
        throw new ImplementationError(MessageFormat.format(Messages.getString("CDOViewImpl.17"), id)); //$NON-NLS-1$
      }

      return result.cdoInternalInstance();
    }

    return potentialID;
  }

  /**
   * @since 2.0
   */
  public synchronized void attachResource(CDOResourceImpl resource)
  {
    if (!resource.isExisting())
    {
      throw new ReadOnlyException(MessageFormat.format(Messages.getString("CDOViewImpl.18"), this)); //$NON-NLS-1$
    }

    // ResourceSet.getResource(uri, true) was called!!
    resource.cdoInternalSetView(this);
    resource.cdoInternalSetState(CDOState.PROXY);
  }

  /**
   * @since 2.0
   */
  public synchronized void registerProxyResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    String path = CDOURIUtil.extractResourcePath(uri);
    boolean isRoot = "/".equals(path); //$NON-NLS-1$

    try
    {
      CDOID id = isRoot ? getSession().getRepositoryInfo().getRootResourceID() : getResourceNodeID(path);
      resource.cdoInternalSetID(id);
      registerObject(resource);
      if (isRoot)
      {
        resource.setRoot(true);
        rootResource = resource;
      }
    }
    catch (Exception ex)
    {
      throw new InvalidURIException(uri, ex);
    }
  }

  public synchronized void registerObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", object); //$NON-NLS-1$
    }

    InternalCDOObject old = objects.put(object.cdoID(), object);
    if (old != null)
    {
      if (old != object)
      {
        throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOViewImpl.30"), object.cdoID())); //$NON-NLS-1$
      }

      if (TRACER.isEnabled())
      {
        TRACER.format(Messages.getString("CDOViewImpl.20"), old); //$NON-NLS-1$
      }
    }
  }

  public synchronized void deregisterObject(InternalCDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Deregistering {0}", object); //$NON-NLS-1$
    }

    removeObject(object.cdoID());
  }

  public synchronized void remapObject(CDOID oldID)
  {
    CDOID newID;
    InternalCDOObject object = objects.remove(oldID);
    newID = object.cdoID();

    objects.put(newID, object);

    if (lastLookupID == oldID)
    {
      lastLookupID = null;
      lastLookupObject = null;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Remapping {0} --> {1}", oldID, newID); //$NON-NLS-1$
    }
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.add(handler);
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    objectHandlers.remove(handler);
  }

  public CDOObjectHandler[] getObjectHandlers()
  {
    return objectHandlers.get();
  }

  public synchronized void handleObjectStateChanged(InternalCDOObject object, CDOState oldState, CDOState newState)
  {
    CDOObjectHandler[] handlers = getObjectHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      CDOObjectHandler handler = handlers[i];
      handler.objectStateChanged(this, object, oldState, newState);
    }
  }

  /*
   * Synchronized through InvlidationRunner.run()
   */
  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate(long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, List<CDORevisionDelta> deltas,
      Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
  {
    Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts = null;
    for (CDORevisionKey key : allChangedObjects)
    {
      CDORevisionDelta delta = null;
      if (key instanceof CDORevisionDelta)
      {
        delta = (CDORevisionDelta)key;
        // Copy the revision delta if we are a transaction, so that conflict resolvers can modify it.
        if (this instanceof CDOTransaction)
        {
          delta = new CDORevisionDeltaImpl(delta, true);
        }

        deltas.add(delta);
      }

      CDOObject changedObject = objects.get(key.getID());

      if (changedObject != null)
      {
        Pair<CDORevision, CDORevisionDelta> oldInfo = new Pair<CDORevision, CDORevisionDelta>(
            changedObject.cdoRevision(), delta);
        // if (!isLocked(changedObject))
        {
          CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)changedObject, key, lastUpdateTime);
        }

        revisionDeltas.put(changedObject, delta);
        if (changedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
          }

          conflicts.put(changedObject, oldInfo);
        }
      }
    }

    for (CDOIDAndVersion key : allDetachedObjects)
    {
      InternalCDOObject detachedObject = removeObject(key.getID());
      if (detachedObject != null)
      {
        Pair<CDORevision, CDORevisionDelta> oldInfo = new Pair<CDORevision, CDORevisionDelta>(
            detachedObject.cdoRevision(), CDORevisionDelta.DETACHED);
        // if (!isLocked(detachedObject))
        {
          CDOStateMachine.INSTANCE.detachRemote(detachedObject);
        }

        detachedObjects.add(detachedObject);
        if (detachedObject.cdoConflict())
        {
          if (conflicts == null)
          {
            conflicts = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>();
          }

          conflicts.put(detachedObject, oldInfo);
        }
      }
    }

    return conflicts;
  }

  protected synchronized void handleConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas)
  {
    // Do nothing
  }

  public void fireAdaptersNotifiedEvent(long timeStamp)
  {
    fireEvent(new AdaptersNotifiedEvent(timeStamp));
  }

  /**
   * TODO For this method to be useable locks must be cached locally!
   */
  @SuppressWarnings("unused")
  private boolean isLocked(InternalCDOObject object)
  {
    if (object.cdoWriteLock().isLocked())
    {
      return true;
    }

    if (object.cdoReadLock().isLocked())
    {
      return true;
    }

    return false;
  }

  public synchronized int reload(CDOObject... objects)
  {
    Collection<InternalCDOObject> internalObjects;
    // TODO Should objects.length == 0 reload *all* objects, too?
    if (objects != null && objects.length != 0)
    {
      internalObjects = new ArrayList<InternalCDOObject>(objects.length);
      for (CDOObject object : objects)
      {
        if (object instanceof InternalCDOObject)
        {
          internalObjects.add((InternalCDOObject)object);
        }
      }
    }
    else
    {
      internalObjects = new ArrayList<InternalCDOObject>(this.objects.values());
    }

    int result = internalObjects.size();
    if (result != 0)
    {
      CDOStateMachine.INSTANCE.reload(internalObjects.toArray(new InternalCDOObject[result]));
    }

    return result;
  }

  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    if (isReadOnly())
    {
      builder.append("View");
    }
    else
    {
      builder.append("Transaction");
    }

    builder.append(" "); //$NON-NLS-1$
    builder.append(getViewID());

    if (branchPoint != null)
    {
      boolean brackets = false;
      if (getSession().getRepositoryInfo().isSupportingBranches())
      {
        brackets = true;
        builder.append(" ["); //$NON-NLS-1$
        builder.append(branchPoint.getBranch().getPathName()); // Do not synchronize on this view!
      }

      long timeStamp = branchPoint.getTimeStamp(); // Do not synchronize on this view!
      if (timeStamp != CDOView.UNSPECIFIED_DATE)
      {
        if (brackets)
        {
          builder.append(", "); //$NON-NLS-1$
        }
        else
        {
          builder.append(" ["); //$NON-NLS-1$
          brackets = true;
        }

        builder.append(CDOCommonUtil.formatTimeStamp(timeStamp));
      }

      if (brackets)
      {
        builder.append("]"); //$NON-NLS-1$
      }
    }

    return builder.toString();
  }

  protected String getClassName()
  {
    return "CDOView"; //$NON-NLS-1$
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return getResourceSet();
  }

  public synchronized void collectViewedRevisions(Map<CDOID, InternalCDORevision> revisions)
  {
    for (InternalCDOObject object : objects.values())
    {
      CDOState state = object.cdoState();
      if (state != CDOState.CLEAN && state != CDOState.DIRTY && state != CDOState.CONFLICT)
      {
        continue;
      }

      CDOID id = object.cdoID();
      if (revisions.containsKey(id))
      {
        continue;
      }

      InternalCDORevision revision = getViewedRevision(object);
      if (revision == null)
      {
        continue;
      }

      revisions.put(id, revision);
    }
  }

  protected InternalCDORevision getViewedRevision(InternalCDOObject object)
  {
    return CDOStateMachine.INSTANCE.readNoLoad(object);
  }

  /**
   * @since Snow Owl 2.6
   */
  public synchronized CDOChangeSetData compareRevisions(CDOBranchPoint source, final String... nsURIs)
  {
    CDOSession session = getSession();
    return session.compareRevisions(source, this, nsURIs);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    viewSet = null;
    objects = null;
    store = null;
    lastLookupID = null;
    lastLookupObject = null;
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class Event extends org.eclipse.net4j.util.event.Event implements CDOViewEvent
  {
    private static final long serialVersionUID = 1L;

    public Event()
    {
      super(AbstractCDOView.this);
    }

    @Override
    public AbstractCDOView getSource()
    {
      return (AbstractCDOView)super.getSource();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdaptersNotifiedEvent extends Event implements CDOViewAdaptersNotifiedEvent
  {
    private static final long serialVersionUID = 1L;

    private long timeStamp;

    public AdaptersNotifiedEvent(long timeStamp)
    {
      this.timeStamp = timeStamp;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    @Override
    public String toString()
    {
      return "CDOViewAdaptersNotifiedEvent: " + timeStamp; //$NON-NLS-1$
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class ViewTargetChangedEvent extends Event implements CDOViewTargetChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOBranchPoint branchPoint;

    public ViewTargetChangedEvent(CDOBranchPoint branchPoint)
    {
      this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewTargetChangedEvent: {0}", branchPoint); //$NON-NLS-1$
    }

    public CDOBranchPoint getBranchPoint()
    {
      return branchPoint;
    }
  }
}
