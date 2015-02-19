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
 */
package org.eclipse.emf.cdo.eresource.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOModificationTrackingAdapter;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ReadOnlyException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifyingListImpl;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMIHelperImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>CDO Resource</b></em>'.
 *
 *
 * @extends Resource.Internal
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 *           <p>
 *           The following features are implemented:
 *           <ul>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getResourceSet <em>Resource Set</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getURI <em>URI</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getContents <em>Contents</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isModified <em>Modified</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isLoaded <em>Loaded</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#isTrackingModification <em>Tracking
 *           Modification</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getErrors <em>Errors</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getWarnings <em>Warnings</em>}</li>
 *           <li>{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl#getTimeStamp <em>Time Stamp</em>}</li>
 *           </ul>
 *           </p>
 * @generated
 */
public class CDOResourceImpl extends CDOResourceLeafImpl implements CDOResource, Resource.Internal
{
  private static final EReference CDO_RESOURCE_CONTENTS = EresourcePackage.eINSTANCE.getCDOResource_Contents();

  /**
   * The default URI converter when there is no resource set.
   *
   * @ADDED
   */
  private static URIConverter defaultURIConverter;

  /**
   * @ADDED
   */
  private boolean root;

  /**
   * @ADDED
   */
  private URI initialURI;

  /**
   * TODO Set to true in commit()?
   *
   * @ADDED
   */
  private boolean existing;

  /**
   * @ADDED
   */
  private boolean loading;

  /**
   * @ADDED
   */
  private boolean loaded;

  /**
   * @ADDED
   */
  private boolean modified;

  /**
   * @ADDED
   */
  private EList<Diagnostic> errors;

  /**
   * @ADDED
   */
  private EList<Diagnostic> warnings;

  /**
   * @ADDED
   */
  private transient CDOViewProvider viewProvider;

  /**
   * @ADDED
   * @since 2.0
   */
  public CDOResourceImpl(URI initialURI)
  {
    this.initialURI = initialURI;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected CDOResourceImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EresourcePackage.Literals.CDO_RESOURCE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   * @since 2.0
   */
  @Override
  public Resource.Internal eDirectResource()
  {
    if (isRoot())
    {
      return this;
    }

    return super.eDirectResource();
  }

  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == CDOResource.class)
    {
      return baseFeatureID;
    }

    if (baseClass == Resource.class)
    {
      return baseFeatureID + EresourcePackage.CDO_RESOURCE_NODE_FEATURE_COUNT;
    }

    return super.eBaseStructuralFeatureID(baseFeatureID, baseClass);
  }

  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == CDOResource.class)
    {
      return derivedFeatureID;
    }

    if (baseClass == Resource.class)
    {
      return derivedFeatureID - EresourcePackage.CDO_RESOURCE_NODE_FEATURE_COUNT;
    }

    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * @since 2.0
   */
  public boolean isRoot()
  {
    return root;
  }

  /**
   * @since 3.0
   */
  public void setRoot(boolean root)
  {
    this.root = root;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ResourceSet getResourceSet()
  {
    return (ResourceSet)eGet(EresourcePackage.Literals.CDO_RESOURCE__RESOURCE_SET, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setResourceSet(ResourceSet newResourceSet)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__RESOURCE_SET, newResourceSet);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 2.0 <!-- end-user-doc -->
   * @generated
   */
  public URI getURIGen()
  {
    return (URI)eGet(EresourcePackage.Literals.CDO_RESOURCE__URI, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public URI getURI()
  {
    if (cdoID() == null && initialURI != null)
    {
      return initialURI;
    }

    if (viewProvider != null)
    {
      URI uri = viewProvider.getResourceURI(cdoView(), getPath());
      if (uri != null)
      {
        return uri;
      }
    }

    return super.getURI();
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 2.0 <!-- end-user-doc -->
   * @generated
   */
  public void setURIGen(URI newURI)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__URI, newURI);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public void setURI(URI newURI)
  {
    String newPath = CDOURIUtil.extractResourcePath(newURI);
    setPath(newPath);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    return (EList<EObject>)eGet(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS, true);
  }

  /**
   * @since 2.0
   */
  @Override
  public void cdoInternalPostDetach(boolean remote)
  {
    super.cdoInternalPostDetach(remote);
    if (remote)
    {
      existing = false;
    }

    removeFromResourceSet();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public boolean isModified()
  {
    return modified;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public void setModified(boolean newModified)
  {
    boolean oldModified = modified;
    modified = newModified;
    if (oldModified != newModified && eNotificationRequired())
    {
      Notification notification = new NotificationImpl(Notification.SET, oldModified, newModified)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return RESOURCE__IS_MODIFIED;
        }
      };

      eNotify(notification);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public boolean isLoaded()
  {
    return loaded;
  }

  /**
   * @see ResourceImpl#setLoaded(boolean)
   * @ADDED
   */
  private Notification setLoaded(boolean isLoaded)
  {
    boolean oldIsLoaded = loaded;
    loaded = isLoaded;

    if (eNotificationRequired())
    {
      Notification notification = new NotificationImpl(Notification.SET, oldIsLoaded, isLoaded)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          // TODO FIX bug 265136
          return Resource.RESOURCE__IS_LOADED;
        }
      };

      return notification;
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public boolean isTrackingModification()
  {
    return (Boolean)eGet(EresourcePackage.Literals.CDO_RESOURCE__TRACKING_MODIFICATION, true);
  }

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @generated
   */
  public void setTrackingModificationGen(boolean newTrackingModification)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__TRACKING_MODIFICATION, newTrackingModification);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public void setTrackingModification(boolean newTrackingModification)
  {
    if (cdoView().isReadOnly())
    {
      throw new ReadOnlyException("Underlying view is read-only");
    }

    if (newTrackingModification == isTrackingModification())
    {
      return;
    }

    EList<Adapter> adapters = eAdapters();
    if (newTrackingModification)
    {
      adapters.add(new CDOModificationTrackingAdapter(this));
    }
    else
    {
      for (Adapter adapter : adapters)
      {
        if (adapter instanceof CDOModificationTrackingAdapter)
        {
          adapters.remove(adapter);
          break;
        }
      }
    }

    setTrackingModificationGen(newTrackingModification);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public EList<Diagnostic> getErrors()
  {
    if (errors == null)
    {
      errors = new NotifyingListImpl<Diagnostic>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean isNotificationRequired()
        {
          return CDOResourceImpl.this.eNotificationRequired();
        }

        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID()
        {
          return EresourcePackage.CDO_RESOURCE__ERRORS;
        }
      };
    }

    return errors;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public EList<Diagnostic> getWarnings()
  {
    if (warnings == null)
    {
      warnings = new NotifyingListImpl<Diagnostic>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean isNotificationRequired()
        {
          return CDOResourceImpl.this.eNotificationRequired();
        }

        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID()
        {
          return EresourcePackage.CDO_RESOURCE__WARNINGS;
        }
      };
    }

    return warnings;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public long getTimeStamp()
  {
    return (Long)eGet(EresourcePackage.Literals.CDO_RESOURCE__TIME_STAMP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setTimeStamp(long newTimeStamp)
  {
    eSet(EresourcePackage.Literals.CDO_RESOURCE__TIME_STAMP, newTimeStamp);
  }

  /**
   * @ADDED
   * @see ResourceImpl#getAllContents()
   */
  public TreeIterator<EObject> getAllContents()
  {
    return new AbstractTreeIterator<EObject>(this, false)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public Iterator<EObject> getChildren(Object object)
      {
        return object == CDOResourceImpl.this ? CDOResourceImpl.this.getContents().iterator() : ((EObject)object)
            .eContents().iterator();
      }
    };
  }

  /**
   * <b>Note:</b> URI from temporary objects are going to changed when we commit the CDOTransaction. Objects will not be
   * accessible from their temporary URI once CDOTransaction is committed.
   * <p>
   * <b>Note:</b> This resource is not actually used to lookup the resulting object in CDO. Only the CDOView is used for
   * this lookup! This means that this resource can be used to resolve <em>any</em> fragment with a CDOID of the
   * associated CDOView.
   *
   * @ADDED
   */
  public EObject getEObject(String uriFragment)
  {
    if (uriFragment == null)
    {
      return null;
    }

    try
    {
      EObject eObjectByFragment = getEObjectByFragment(uriFragment);

      if (eObjectByFragment != null)
      {
        return eObjectByFragment;
      }

      CDOID id = CDOIDUtil.read(uriFragment);
      InternalCDOView view = cdoView();
      if (CDOIDUtil.isNull(id) || view.isObjectNew(id) && !view.isObjectRegistered(id))
      {
        return null;
      }

      if (id.isObject())
      {
        CDOObject object = view.getObject(id, true);
        return CDOUtil.getEObject(object);
      }
    }
    catch (Exception ex)
    {
      // Do nothing
      // Return null if the object cannot be resolved.
    }

    // If it doesn't match to anything we return null like ResourceImpl.getEObject
    return null;
  }

  private EObject getEObjectByFragment(String uriFragment)
  {
    int length = uriFragment.length();
    if (length > 0)
    {
      if (uriFragment.charAt(0) == '/')
      {
        ArrayList<String> uriFragmentPath = new ArrayList<String>(4);
        int start = 1;
        for (int i = 1; i < length; ++i)
        {
          if (uriFragment.charAt(i) == '/')
          {
            uriFragmentPath.add(start == i ? "" : uriFragment.substring(start, i));
            start = i + 1;
          }
        }

        uriFragmentPath.add(uriFragment.substring(start));
        return getEObject(uriFragmentPath);
      }
      else if (uriFragment.charAt(length - 1) == '?')
      {
        int index = uriFragment.lastIndexOf('?', length - 2);
        if (index > 0)
        {
          uriFragment = uriFragment.substring(0, index);
        }
      }
    }

    return null;
  }

  private EObject getEObject(List<String> uriFragmentPath)
  {
    int size = uriFragmentPath.size();
    EObject eObject = getEObjectForURIFragmentRootSegment(size == 0 ? "" : uriFragmentPath.get(0));
    for (int i = 1; i < size && eObject != null; ++i)
    {
      eObject = ((InternalEObject)eObject).eObjectForURIFragmentSegment(uriFragmentPath.get(i));
    }

    return eObject;
  }

  private EObject getEObjectForURIFragmentRootSegment(String uriFragmentRootSegment)
  {
    int position = 0;
    if (uriFragmentRootSegment.length() > 0)
    {
      try
      {
        position = Integer.parseInt(uriFragmentRootSegment);
      }
      catch (NumberFormatException exception)
      {
        throw new RuntimeException(exception);
      }
    }

    List<EObject> contents = getContents();
    if (position < contents.size() && position >= 0)
    {
      return contents.get(position);
    }

    return null;
  }

  /**
   * @ADDED
   */
  public String getURIFragment(EObject object)
  {
    // TODO if object == this ??? what we do. Is it wanted ? How we handle them ?
    InternalCDOObject internalCDOObject = FSMUtil.adapt(object, cdoView());
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, internalCDOObject.cdoID());
    return builder.toString();
  }

  /**
   * @since 2.0
   */
  @Override
  public void cdoInternalPreLoad()
  {
    try
    {
      load(null);
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @ADDED
   */
  public void load(InputStream inputStream, Map<?, ?> options) throws IOException
  {
    // final String baseURI = getBaseURIOption(options);
    // final Map<Resource, CDOResource> resourceMappings = new HashMap<Resource, CDOResource>();
    //
    // class ImportResource extends XMIResourceImpl
    // {
    // private CDOResource delegate;
    //
    // public ImportResource(CDOResource delegate)
    // {
    // super(URI.createURI(baseURI + delegate.getPath()));
    // this.delegate = delegate;
    // }
    //
    // @Override
    // public EList<EObject> getContents()
    // {
    // return delegate.getContents();
    // }
    //
    // @Override
    // public String getURIFragment(EObject eObject)
    // {
    // String id = EcoreUtil.getID(eObject);
    // if (id != null)
    // {
    // return id;
    // }
    //
    // InternalEObject internalEObject = (InternalEObject)eObject;
    // if (getMappedResource(internalEObject.eDirectResource()) == this)
    // {
    // return "/" + getURIFragmentRootSegment(eObject);
    // }
    //
    // List<String> uriFragmentPath = new ArrayList<String>();
    // boolean isContained = false;
    // for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container =
    // internalEObject
    // .eInternalContainer())
    // {
    // uriFragmentPath.add(container.eURIFragmentSegment(internalEObject.eContainingFeature(), internalEObject));
    // internalEObject = container;
    // if (getMappedResource(container.eDirectResource()) == this)
    // {
    // isContained = true;
    // break;
    // }
    // }
    //
    // if (!isContained)
    // {
    // return "/-1";
    // }
    //
    // StringBuilder result = new StringBuilder("/");
    // result.append(getURIFragmentRootSegment(internalEObject));
    //
    // for (int i = uriFragmentPath.size() - 1; i >= 0; --i)
    // {
    // result.append('/');
    // result.append(uriFragmentPath.get(i));
    // }
    //
    // return result.toString();
    // }
    //
    // @Override
    // protected XMLHelper createXMLHelper()
    // {
    // return new XMIHelperImpl(this)
    // {
    // @Override
    // public String getHREF(EObject obj)
    // {
    // InternalEObject o = (InternalEObject)obj;
    //
    // URI objectURI = o.eProxyURI();
    // if (objectURI == null)
    // {
    // Resource otherResource = obj.eResource();
    // otherResource = getMappedResource(otherResource);
    // objectURI = getHREF(otherResource, obj);
    // }
    //
    // objectURI = deresolve(objectURI);
    // return objectURI.toString();
    // }
    //
    // @Override
    // protected URI getHREF(Resource otherResource, EObject obj)
    // {
    // String uriFragment = getURIFragment(otherResource, obj);
    // if (otherResource == ImportResource.this)
    // {
    // return URI.createURI(uriFragment);
    // }
    //
    // return otherResource.getURI().appendFragment(uriFragment);
    // }
    // };
    // }
    //
    // private Resource getMappedResource(Resource otherResource)
    // {
    // Resource resource = resourceMappings.get(otherResource);
    // if (resource != null)
    // {
    // return resource;
    // }
    //
    // if (otherResource instanceof CDOResource)
    // {
    // CDOResource cdoResource = (CDOResource)otherResource;
    // otherResource = new ImportResource(cdoResource);
    // resourceMappings.put(cdoResource, otherResource);
    // }
    //
    // return otherResource;
    // }
    // }
    //
    // XMIResource xmiResource = new ImportResource(this);
    // resourceMappings.put(this, xmiResource);
    // xmiResource.save(outputStream, options);

    throw new UnsupportedOperationException();
  }

  /**
   * @ADDED
   */
  public void load(Map<?, ?> options) throws IOException
  {
    if (!isLoaded())
    {
      InternalCDOView view = cdoView();
      if (!FSMUtil.isTransient(this))
      {
        CDOID id = cdoID();
        if (id == null)
        {
          registerProxy(view);
        }
        else
        {
          synchronized (view)
          {
            if (!view.isObjectRegistered(id))
            {
              registerProxy(view);
            }
          }
        }
      }

      Notification notification = setLoaded(true);
      if (notification != null)
      {
        eNotify(notification);
      }

      // URIConverter uriConverter = getURIConverter();
      //
      // // If an input stream can't be created, ensure that the resource is still considered loaded after the failure,
      // // and do all the same processing we'd do if we actually were able to create a valid input stream.
      // //
      // InputStream inputStream = null;
      //
      // try
      // {
      // inputStream = uriConverter.createInputStream(getURI(), options);
      // }
      // catch (IOException exception)
      // {
      // Notification notification = setLoaded(true);
      // loading = true;
      // if (errors != null)
      // {
      // errors.clear();
      // }
      //
      // if (warnings != null)
      // {
      // warnings.clear();
      // }
      //
      // loading = false;
      // if (notification != null)
      // {
      // eNotify(notification);
      // }
      //
      // setModified(false);
      // throw exception;
      // }
      //
      // try
      // {
      // load(inputStream, options);
      // }
      // finally
      // {
      // inputStream.close();
      // // TODO Handle timeStamp
      // // Long timeStamp = (Long)response.get(URIConverter.RESPONSE_TIME_STAMP_PROPERTY);
      // // if (timeStamp != null)
      // // {
      // // setTimeStamp(timeStamp);
      // // }
      // }
    }
  }

  private void registerProxy(InternalCDOView view) throws IOWrappedException
  {
    try
    {
      view.registerProxyResource(this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      setExisting(false);
      cdoInternalSetState(CDOState.TRANSIENT);
      throw new IOWrappedException(ex);
    }
  }

  /**
   * Returns the URI converter. This typically gets the {@link ResourceSet#getURIConverter converter} from the
   * {@link #getResourceSet containing} resource set, but it calls {@link #getDefaultURIConverter} when there is no
   * containing resource set.
   *
   * @return the URI converter.
   * @ADDED
   */
  @SuppressWarnings("unused")
  private URIConverter getURIConverter()
  {
    return getResourceSet() == null ? getDefaultURIConverter() : getResourceSet().getURIConverter();
  }

  /**
   * Returns the default URI converter that's used when there is no resource set.
   *
   * @return the default URI converter.
   * @see #getURIConverter
   * @ADDED
   */
  private static synchronized URIConverter getDefaultURIConverter()
  {
    if (defaultURIConverter == null)
    {
      defaultURIConverter = new ExtensibleURIConverterImpl();
    }

    return defaultURIConverter;
  }

  /**
   * @ADDED
   */
  public void save(Map<?, ?> options) throws IOException
  {
    CDOTransaction transaction = getTransaction(options);
    IProgressMonitor progressMonitor = options != null ? (IProgressMonitor)options
        .get(CDOResource.OPTION_SAVE_PROGRESS_MONITOR) : null;

    try
    {
      transaction.commit(progressMonitor);
    }
    catch (CommitException ex)
    {
      throw new TransactionException(ex);
    }

    setModified(false);
  }

  /**
   * @ADDED
   */
  private CDOTransaction getTransaction(Map<?, ?> options)
  {
    CDOTransaction transaction = options != null ? (CDOTransaction)options
        .get(CDOResource.OPTION_SAVE_OVERRIDE_TRANSACTION) : null;

    if (transaction == null)
    {
      CDOView view = cdoView();
      if (view instanceof CDOTransaction)
      {
        transaction = (CDOTransaction)view;
      }
      else
      {
        throw new IllegalStateException("No transaction available");
      }
    }

    return transaction;
  }

  /**
   * @ADDED
   */
  public void save(OutputStream outputStream, Map<?, ?> options) throws IOException
  {
    final String baseURI = getBaseURIOption(options);
    final Map<CDOResource, Resource> resourceMappings = new HashMap<CDOResource, Resource>();

    class ExportResource extends XMIResourceImpl
    {
      private CDOResource delegate;

      public ExportResource(CDOResource delegate)
      {
        super(URI.createURI(baseURI + delegate.getPath()));
        this.delegate = delegate;
      }

      @Override
      public EList<EObject> getContents()
      {
        return delegate.getContents();
      }

      // @Override
      // public String getURIFragment(EObject eObject)
      // {
      // String id = EcoreUtil.getID(eObject);
      // if (id != null)
      // {
      // return id;
      // }
      //
      // InternalEObject internalEObject = (InternalEObject)eObject;
      // if (getMappedResource(internalEObject.eDirectResource()) == this)
      // {
      // return "/" + getURIFragmentRootSegment(eObject);
      // }
      //
      // List<String> uriFragmentPath = new ArrayList<String>();
      // boolean isContained = false;
      // for (InternalEObject container = internalEObject.eInternalContainer(); container != null; container =
      // internalEObject
      // .eInternalContainer())
      // {
      // uriFragmentPath.add(container.eURIFragmentSegment(internalEObject.eContainingFeature(), internalEObject));
      // internalEObject = container;
      // if (getMappedResource(container.eDirectResource()) == this)
      // {
      // isContained = true;
      // break;
      // }
      // }
      //
      // if (!isContained)
      // {
      // return "/-1";
      // }
      //
      // StringBuilder result = new StringBuilder("/");
      // result.append(getURIFragmentRootSegment(internalEObject));
      //
      // for (int i = uriFragmentPath.size() - 1; i >= 0; --i)
      // {
      // result.append('/');
      // result.append(uriFragmentPath.get(i));
      // }
      //
      // return result.toString();
      // }

      @Override
      protected boolean useUUIDs()
      {
        return true;
      }

      @Override
      protected boolean useIDAttributes()
      {
        return false;
      }

      @Override
      public String getID(EObject eObject)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
        StringBuilder builder = new StringBuilder();
        CDOIDUtil.write(builder, cdoObject.cdoID());
        return builder.toString();
      }

      @Override
      protected XMLHelper createXMLHelper()
      {
        return new XMIHelperImpl(this)
        {
          @Override
          public String getHREF(EObject obj)
          {
            InternalEObject o = (InternalEObject)obj;

            URI objectURI = o.eProxyURI();
            if (objectURI == null)
            {
              Resource otherResource = obj.eResource();
              otherResource = getMappedResource(otherResource);
              objectURI = getHREF(otherResource, obj);
            }

            objectURI = deresolve(objectURI);
            return objectURI.toString();
          }

          @Override
          protected URI getHREF(Resource otherResource, EObject obj)
          {
            String uriFragment = getURIFragment(otherResource, obj);
            if (otherResource == ExportResource.this)
            {
              return URI.createURI(uriFragment);
            }

            return otherResource.getURI().appendFragment(uriFragment);
          }
        };
      }

      private Resource getMappedResource(Resource otherResource)
      {
        Resource resource = resourceMappings.get(otherResource);
        if (resource != null)
        {
          return resource;
        }

        if (otherResource instanceof CDOResource)
        {
          CDOResource cdoResource = (CDOResource)otherResource;
          otherResource = new ExportResource(cdoResource);
          resourceMappings.put(cdoResource, otherResource);
        }

        return otherResource;
      }
    }

    XMIResource xmiResource = new ExportResource(this);
    resourceMappings.put(this, xmiResource);
    xmiResource.save(outputStream, options);
  }

  private String getBaseURIOption(Map<?, ?> options)
  {
    if (options != null)
    {
      String uri = (String)options.get(OPTION_SAVE_BASE_URI);
      if (uri != null)
      {
        return uri;
      }
    }

    return "cdo://";
  }

  /**
   * @ADDED
   */
  public void unload()
  {
    // Do nothing
  }

  /**
   * @ADDED
   */
  public void delete(Map<?, ?> options) throws IOException
  {
    if (FSMUtil.isTransient(this))
    {
      removeFromResourceSet();
    }
    else
    {
      if (isRoot())
      {
        throw new UnsupportedOperationException();
      }

      if (getFolder() == null)
      {
        InternalCDOView view = cdoView();
        view.getRootResource().getContents().remove(this);
      }
      else
      {
        basicSetFolder(null, false);
      }
    }
  }

  private void removeFromResourceSet()
  {
    final ResourceSet resourceSet = getResourceSet();
    if (resourceSet != null)
    {
      InternalCDOViewSet viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(resourceSet);
      viewSet.executeWithoutNotificationHandling(new Callable<Boolean>()
      {
        public Boolean call() throws Exception
        {
          resourceSet.getResources().remove(CDOResourceImpl.this);
          return true;
        }
      });
    }
  }

  /**
   * @ADDED
   */
  public void attached(EObject object)
  {
    if (!FSMUtil.isTransient(this))
    {
      InternalCDOView view = cdoView();
      if (view instanceof InternalCDOTransaction) // Bug 376075
      {
        InternalCDOTransaction transaction = (InternalCDOTransaction)view;
        InternalCDOObject cdoObject = FSMUtil.adapt(object, transaction);

        if (CDOUtil.isLegacyObject(cdoObject) && FSMUtil.isClean(cdoObject))
        {
          // Bug 352204
          return;
        }

        attached(cdoObject, transaction);
      }
    }
  }

  /**
   * @ADDED
   */
  private void attached(InternalCDOObject cdoObject, InternalCDOTransaction transaction)
  {
    CDOStateMachine.INSTANCE.attach(cdoObject, transaction);
  }

  /**
   * @ADDED
   */
  public void detached(EObject object)
  {
    if (!FSMUtil.isTransient(this))
    {
      InternalCDOView view = cdoView();
      if (view instanceof InternalCDOTransaction) // Bug 376075
      {
        InternalCDOObject cdoObject = FSMUtil.adapt(object, view);
        CDOStateMachine.INSTANCE.detach(cdoObject);
      }
    }
  }

  /**
   * @ADDED
   * @see ResourceImpl#basicSetResourceSet(ResourceSet, NotificationChain)
   */
  public NotificationChain basicSetResourceSet(ResourceSet resourceSet, NotificationChain notifications)
  {
    final ResourceSet oldResourceSet = getResourceSet();
    if (oldResourceSet != null)
    {
      final NotificationChain finalNotifications = notifications;

      InternalCDOViewSet viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(oldResourceSet);
      notifications = viewSet.executeWithoutNotificationHandling(new Callable<NotificationChain>()
      {
        public NotificationChain call() throws Exception
        {
          return ((InternalEList<Resource>)oldResourceSet.getResources()).basicRemove(this, finalNotifications);
        }
      });
    }

    setResourceSet(resourceSet);

    if (resourceSet != null)
    {
      InternalCDOView view = cdoView();
      if (view == null)
      {
        URI uri = getURI();
        Pair<CDOView, CDOViewProvider> pair = CDOViewProviderRegistry.INSTANCE.provideViewWithInfo(uri, resourceSet);
        if (pair != null)
        {
          view = (InternalCDOView)pair.getElement1();
          view.attachResource(this);

          viewProvider = pair.getElement2();
        }
      }

      String query = getURI().query();
      if (query != null && query.length() != 0)
      {
        Map<String, String> parameters = CDOURIUtil.getParameters(query);
        String value = parameters.get(CDOResource.PREFETCH_PARAMETER);
        if (value != null)
        {
          boolean prefetch = Boolean.parseBoolean(value);
          if (prefetch)
          {
            cdoPrefetch(CDORevision.DEPTH_INFINITE);
          }
        }
      }
    }

    if (eNotificationRequired())
    {
      if (notifications == null)
      {
        notifications = new NotificationChainImpl(2);
      }

      notifications.add(new NotificationImpl(Notification.SET, oldResourceSet, resourceSet)
      {
        @Override
        public Object getNotifier()
        {
          return CDOResourceImpl.this;
        }

        @Override
        public int getFeatureID(Class<?> expectedClass)
        {
          return RESOURCE__RESOURCE_SET;
        }
      });
    }

    return notifications;
  }

  /**
   * @ADDED
   */
  public boolean isLoading()
  {
    return loading;
  }

  /**
   * @ADDED
   */
  public boolean isExisting()
  {
    return existing;
  }

  /**
   * @ADDED
   * @since 3.0
   */
  protected void setExisting(boolean existing)
  {
    this.existing = existing;
  }

  /**
   * @ADDED
   */
  @Override
  protected EList<?> createList(EStructuralFeature eStructuralFeature)
  {
    if (eStructuralFeature == CDO_RESOURCE_CONTENTS)
    {
      return new ContentsCDOList(CDO_RESOURCE_CONTENTS);
      // return new _ContentsCDOList<EObject>();
    }

    return super.createList(eStructuralFeature);
  }

  // /**
  // * A notifying list implementation for supporting {@link Resource#getContents}.
  // */
  // protected class _ContentsCDOList<E extends Object & EObject> extends ResourceContentsEList<E>
  // {
  // private static final long serialVersionUID = 1L;
  //
  // @Override
  // public int getFeatureID()
  // {
  // return CDO_RESOURCE_CONTENTS.getFeatureID();
  // }
  //
  // @Override
  // protected CDOResourceImpl getResource()
  // {
  // return CDOResourceImpl.this;
  // }
  //
  // @Override
  // protected Notification setLoaded(boolean loaded)
  // {
  // return getResource().setLoaded(loaded);
  // }
  //
  // @Override
  // protected boolean isNotificationRequired()
  // {
  // return getResource().eNotificationRequired();
  // }
  //
  // @Override
  // public NotificationChain inverseAdd(E object, NotificationChain notifications)
  // {
  // if (FSMUtil.isTransient(getResource()))
  // {
  // InternalEObject eObject = (InternalEObject)object;
  // return eObject.eSetResource(CDOResourceImpl.this, notifications);
  // // return super.inverseAdd(object, notifications);
  // }
  //
  // InternalCDOTransaction transaction = cdoView().toTransaction();
  // InternalCDOObject cdoObject = FSMUtil.adapt(object, transaction);
  // notifications = cdoObject.eSetResource(getResource(), notifications);
  //
  // // Attach here instead of in CDOObjectImpl.eSetResource because EMF does it also here
  // if (FSMUtil.isTransient(cdoObject))
  // {
  // attached(cdoObject, transaction);
  // }
  //
  // return notifications;
  // }
  //
  // @Override
  // public NotificationChain inverseRemove(E object, NotificationChain notifications)
  // {
  // if (FSMUtil.isTransient(getResource()))
  // {
  // InternalEObject eObject = (InternalEObject)object;
  // return eObject.eSetResource(null, notifications);
  // // return super.inverseRemove(object, notifications);
  // }
  //
  // InternalEObject eObject = (InternalEObject)object;
  // detached(eObject);
  // return eObject.eSetResource(null, notifications);
  // }
  // }

  /**
   * An implementation of a CDO specific '<em><b>contents</b></em>' list.
   *
   * @ADDED
   * @author Eike Stepper
   * @since 2.0
   */
  protected class ContentsCDOList extends BasicEStoreEList<Object>
  {
    private static final long serialVersionUID = 1L;

    public ContentsCDOList(EStructuralFeature eStructuralFeature)
    {
      super(CDOResourceImpl.this, eStructuralFeature);
    }

    /**
     * Optimization taken from ResourceImpl.EContentList.contains.
     *
     * @since 2.0
     */
    @Override
    public boolean contains(Object object)
    {
      if (size() <= 4)
      {
        return super.contains(object);
      }

      return object instanceof InternalEObject && ((InternalEObject)object).eDirectResource() == CDOResourceImpl.this;
    }

    /**
     * @since 2.0
     */
    @Override
    public NotificationChain inverseAdd(Object object, NotificationChain notifications)
    {
      if (FSMUtil.isTransient(CDOResourceImpl.this))
      {
        InternalEObject eObject = (InternalEObject)object;
        notifications = eObject.eSetResource(CDOResourceImpl.this, notifications);
      }
      else
      {
        InternalCDOTransaction transaction = cdoView().toTransaction();
        InternalCDOObject cdoObject = FSMUtil.adapt(object, transaction);
        notifications = cdoObject.eSetResource(CDOResourceImpl.this, notifications);

        // Attach here instead of in CDOObjectImpl.eSetResource because EMF does it also here
        if (FSMUtil.isTransient(cdoObject))
        {
          attached(cdoObject, transaction);
        }
      }

      return notifications;
    }

    /**
     * @since 2.0
     */
    @Override
    public NotificationChain inverseRemove(Object object, NotificationChain notifications)
    {
      if (FSMUtil.isTransient(CDOResourceImpl.this))
      {
        InternalEObject eObject = (InternalEObject)object;
        notifications = eObject.eSetResource(null, notifications);
      }
      else
      {
        InternalEObject eObject = (InternalEObject)object;
        detached(eObject);
        notifications = eObject.eSetResource(null, notifications);
      }

      return notifications;
    }

    /**
     * @since 2.0
     */
    protected void loaded()
    {
      Notification notification = setLoaded(true);
      if (notification != null)
      {
        eNotify(notification);
      }
    }

    /**
     * @since 2.0
     */
    protected void modified()
    {
      if (isTrackingModification())
      {
        setModified(true);
      }
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean useEquals()
    {
      return false;
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean hasInverse()
    {
      return true;
    }

    /**
     * @since 2.0
     */
    @Override
    protected boolean isUnique()
    {
      return true;
    }

    /**
     * @since 4.0
     */
    @Override
    protected void didAdd(int index, Object newObject)
    {
      super.didAdd(index, newObject);

      if (!isExisting() && !isLoaded())
      {
        loaded();
      }
    }

    /**
     * @since 4.0
     */
    @Override
    protected void didClear(int size, Object[] oldObjects)
    {
      super.didClear(size, oldObjects);

      if (!isExisting() && !isLoaded())
      {
        loaded();
      }
    }
  }
} // CDOResourceImpl
