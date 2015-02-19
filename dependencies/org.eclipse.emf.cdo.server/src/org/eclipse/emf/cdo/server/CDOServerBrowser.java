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
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil.AllRevisionsDumper;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple HTTP server that web browsers can connect to in order to render internal server data for debugging purposes.
 * <p>
 * Actual content is contributed through pluggable {@link CDOServerBrowser.Page pages}.
 * <p>
 * <b>Note:</b> Don't use this server in production, it's unsecure and does not perform or scale!
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOServerBrowser extends Worker
{
  private static final String REQUEST_PREFIX = "GET ";

  private static final String REQUEST_SUFFIX = " HTTP/1.1";

  private ThreadLocal<Map<String, String>> params = new InheritableThreadLocal<Map<String, String>>()
  {
    @Override
    protected Map<String, String> initialValue()
    {
      return new HashMap<String, String>();
    }
  };

  private int port = 7777;

  private ServerSocket serverSocket;

  private Map<String, InternalRepository> repositories;

  private List<Page> pages = new ArrayList<Page>();

  public CDOServerBrowser(Map<String, InternalRepository> repositories)
  {
    this.repositories = repositories;
    setDaemon(true);
  }

  public Map<String, InternalRepository> getRepositories()
  {
    return repositories;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Socket socket = null;

    try
    {
      socket = serverSocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      OutputStream out = new BufferedOutputStream(socket.getOutputStream());
      PrintStream pout = new PrintStream(out);
      printHeader(pout);

      String line;
      while ((line = in.readLine()) != null)
      {
        if (line.startsWith(REQUEST_PREFIX) && line.endsWith(REQUEST_SUFFIX))
        {
          String request = line.substring(REQUEST_PREFIX.length(), line.length() - REQUEST_SUFFIX.length()).trim();
          String resource = request;
          String params = "";
          int pos = request.indexOf('?');
          if (pos != -1)
          {
            resource = request.substring(0, pos);
            params = request.substring(pos + 1);
          }

          initParams(params);
          if ("/".equals(resource))
          {
            showMenu(pout);
          }
          else
          {
            String pageName = resource.substring(1);
            for (Page page : pages)
            {
              if (page.getName().equals(pageName))
              {
                showPage(pout, page);
              }
            }
          }
        }

        out.flush();
        return;
      }
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        ex.printStackTrace();
      }
    }
    finally
    {
      params.remove();
      if (socket != null)
      {
        socket.close();
      }
    }
  }

  protected void initParams(String params)
  {
    Map<String, String> map = this.params.get();
    for (String param : params.split("&"))
    {
      if (param.length() != 0)
      {
        String[] keyValue = param.split("=");
        map.put(keyValue[0], keyValue[1]);
      }
    }
  }

  protected void clearParams()
  {
    Map<String, String> map = params.get();
    map.clear();
  }

  public void removeParam(String key)
  {
    Map<String, String> map = params.get();
    map.remove(key);
  }

  public String getParam(String key)
  {
    Map<String, String> map = params.get();
    return map.get(key);
  }

  public String href(String label, String resource, String... params)
  {
    Map<String, String> map = new HashMap<String, String>(this.params.get());
    for (int i = 0; i < params.length;)
    {
      map.put(params[i++], params[i++]);
    }

    List<String> list = new ArrayList<String>(map.keySet());
    Collections.sort(list);

    StringBuilder builder = new StringBuilder();
    for (String key : list)
    {
      String value = map.get(key);
      if (value != null)
      {
        if (builder.length() != 0)
        {
          builder.append("&");
        }

        builder.append(key);
        builder.append("=");
        builder.append(value);
      }
    }

    return "<a href=\"/" + escape(resource) + "?" + escape(builder.toString()) + "\">" + escape(label) + "</a>";
  }

  public String escape(String raw)
  {
    if (raw == null)
    {
      return "null";
    }

    return raw.replace("<", "&lt;");
  }

  protected void printHeader(PrintStream pout)
  {
    pout.print("HTTP/1.1 200 OK\r\n");
    pout.print("Content-Type: text/html\r\n");
    pout.print("Date: " + new Date() + "\r\n");
    pout.print("Server: DBBrowser 3.0\r\n");
    pout.print("\r\n");
  }

  protected void showMenu(PrintStream pout)
  {
    clearParams();
    pout.print("<h1>CDO Server Browser 4.0</h1><hr>\r\n");

    for (Page page : pages)
    {
      pout.println("<h3>" + href(page.getLabel(), page.getName()) + "</h3>");
    }
  }

  protected void showPage(PrintStream pout, Page page)
  {
    String repo = getParam("repo");

    List<String> repoNames = new ArrayList<String>(getRepositoryNames());
    Collections.sort(repoNames);

    pout.print("<h3><a href=\"/\">" + page.getLabel() + "</a>:&nbsp;&nbsp;");
    for (String repoName : repoNames)
    {
      InternalRepository repository = getRepository(repoName);
      if (!page.canDisplay(repository))
      {
        continue;
      }

      if (repo == null)
      {
        repo = repoName;
      }

      if (repoName.equals(repo))
      {
        pout.print("<b>" + escape(repoName) + "</b>&nbsp;&nbsp;");
      }
      else
      {
        pout.print(href(repoName, page.getName(), "repo", repoName) + "&nbsp;&nbsp;");
      }
    }

    pout.print("</h3>");

    InternalRepository repository = getRepository(repo);
    if (repository != null)
    {
      pout.print("<p>\r\n");
      page.display(this, repository, pout);
    }
  }

  protected Set<String> getRepositoryNames()
  {
    return repositories.keySet();
  }

  protected InternalRepository getRepository(String name)
  {
    return repositories.get(name);
  }

  @Override
  protected String getThreadName()
  {
    return "CDOServerBrowser";
  }

  protected void initPages(List<Page> pages)
  {
    pages.add(new PackagesPage());
    pages.add(new RevisionsPage.FromCache());
    pages.add(new RevisionsPage.FromStore());
    pages.add(new LobsPage());
    pages.add(new HistoryPage());

    IManagedContainer container = getPagesContainer();
    for (String factoryType : container.getFactoryTypes(Page.PRODUCT_GROUP))
    {
      try
      {
        Page page = (Page)container.getElement(Page.PRODUCT_GROUP, factoryType, null);
        pages.add(page);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @since 4.1
   */
  protected IManagedContainer getPagesContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected void doActivate() throws Exception
  {
    initPages(pages);

    try
    {
      serverSocket = new ServerSocket(port);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not open socket on port " + port, ex);
    }

    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSocket.close();
    super.doDeactivate();
  }

  /**
   * A {@link CDOServerBrowser server browser} for the repositories in a {@link IManagedContainer managed container}.
   *
   * @author Eike Stepper
   */
  public static class ContainerBased extends CDOServerBrowser
  {
    private IContainer<?> container;

    private IListener containerListener = new ContainerEventAdapter<Object>()
    {
      @Override
      protected void onAdded(IContainer<Object> container, Object element)
      {
        addElement(element);
      }

      @Override
      protected void onRemoved(IContainer<Object> container, Object element)
      {
        removeElement(element);
      }
    };

    public ContainerBased(IContainer<?> container)
    {
      super(new HashMap<String, InternalRepository>());
      this.container = container;
    }

    public ContainerBased()
    {
      this(IPluginContainer.INSTANCE);
    }

    public IContainer<?> getContainer()
    {
      return container;
    }

    @Override
    protected IManagedContainer getPagesContainer()
    {
      if (container instanceof IManagedContainer)
      {
        return (IManagedContainer)container;
      }

      return IPluginContainer.INSTANCE;
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      for (Object element : container.getElements())
      {
        addElement(element);
      }

      container.addListener(containerListener);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      container.removeListener(containerListener);
      super.doDeactivate();
    }

    private void addElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().put(repository.getName(), repository);
      }
    }

    private void removeElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().remove(repository.getName());
      }
    }

    /**
     * Creates {@link CDOServerBrowser server browsers} for the repositories in a {@link IManagedContainer managed
     * container}.
     *
     * @author Eike Stepper
     */
    public static class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.browsers";

      public static final String TYPE = "default";

      private IContainer<?> container;

      public Factory()
      {
        this(IPluginContainer.INSTANCE);
      }

      public Factory(IContainer<?> container)
      {
        super(PRODUCT_GROUP, TYPE);
        this.container = container;
      }

      public CDOServerBrowser.ContainerBased create(String description) throws ProductCreationException
      {
        CDOServerBrowser.ContainerBased browser = new CDOServerBrowser.ContainerBased(container);

        try
        {
          if (!StringUtil.isEmpty(description))
          {
            browser.setPort(Integer.valueOf(description));
          }
        }
        catch (Exception ex)
        {
          OM.LOG.warn(ex);
        }

        return browser;
      }
    }
  }

  /**
   * Represents pluggable content for a {@link CDOServerBrowser server browser}.
   *
   * @author Eike Stepper
   */
  public static interface Page
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.browserPages";

    public String getName();

    public String getLabel();

    public boolean canDisplay(InternalRepository repository);

    public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out);
  }

  /**
   * An abstract base implementation of a {@link Page server browser page}.
   *
   * @author Eike Stepper
   */
  public static abstract class AbstractPage implements Page
  {
    private String name;

    private String label;

    public AbstractPage(String name, String label)
    {
      this.name = name;
      this.label = label;
    }

    public String getName()
    {
      return name;
    }

    public String getLabel()
    {
      return label;
    }
  }

  /**
   * A {@link Page server browser page} that renders the package registry contents of a repository.
   *
   * @author Eike Stepper
   */
  public static class PackagesPage extends AbstractPage
  {
    public static final String NAME = "packages";

    public PackagesPage()
    {
      super(NAME, "Packages and Classes");
    }

    public boolean canDisplay(InternalRepository repository)
    {
      return true;
    }

    public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
    {
      String param = browser.getParam("classifier");
      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
      for (InternalCDOPackageUnit unit : packageRegistry.getPackageUnits())
      {
        param = showPackage(unit.getTopLevelPackageInfo(), packageRegistry, browser, param, out, "&nbsp;&nbsp;");
      }
    }

    protected String showPackage(InternalCDOPackageInfo info, InternalCDOPackageRegistry packageRegistry,
        CDOServerBrowser browser, String param, PrintStream out, String prefix)
    {
      EPackage ePackage = info.getEPackage();
      out.println("<h3>" + prefix + ePackage.getName() + "&nbsp;&nbsp;[" + ePackage.getNsURI() + "]</h3>");

      for (EClassifier classifier : ePackage.getEClassifiers())
      {
        String name = classifier.getName();
        if (param == null)
        {
          param = name;
        }

        String label = name.equals(param) ? name : browser.href(name, getName(), "classifier", name);
        out.print(prefix + "&nbsp;&nbsp;<b>" + label);

        if (classifier instanceof EEnum)
        {
          EEnum eenum = (EEnum)classifier;
          out.print("&nbsp;&nbsp;" + eenum.getELiterals());
        }
        else if (classifier instanceof EDataType)
        {
          EDataType eDataType = (EDataType)classifier;
          out.print("&nbsp;&nbsp;" + eDataType.getInstanceClassName());
        }

        out.println("</b><br>");
      }

      for (EPackage sub : ePackage.getESubpackages())
      {
        InternalCDOPackageInfo subInfo = packageRegistry.getPackageInfo(sub);
        param = showPackage(subInfo, packageRegistry, browser, param, out, prefix + "&nbsp;&nbsp;");
      }

      return param;
    }
  }

  /**
   * A {@link Page server browser page} that renders {@link CDORevision revisions}.
   *
   * @author Eike Stepper
   */
  public static abstract class RevisionsPage extends AbstractPage
  {
    public RevisionsPage(String name, String label)
    {
      super(name, label);
    }

    public void display(final CDOServerBrowser browser, InternalRepository repository, PrintStream out)
    {
      Map<CDOBranch, List<CDORevision>> allRevisions = getAllRevisions(repository);
      Map<CDOID, List<CDORevision>> ids = getAllIDs(allRevisions);

      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");

      out.print("<td valign=\"top\">\r\n");
      out.print("<table border=\"1\" cellpadding=\"2\"><tr><td>\r\n");
      final String[] revision = { browser.getParam("revision") };
      new AllRevisionsDumper.Stream.Html(allRevisions, out)
      {
        private StringBuilder versionsBuilder;

        private CDORevision lastRevision;

        @Override
        protected void dumpEnd(List<CDOBranch> branches)
        {
          dumpLastRevision();
          super.dumpEnd(branches);
        }

        @Override
        protected void dumpBranch(CDOBranch branch)
        {
          dumpLastRevision();
          super.dumpBranch(branch);
        }

        @Override
        protected void dumpRevision(CDORevision rev)
        {
          CDOID id = rev.getID();
          if (lastRevision != null && !id.equals(lastRevision.getID()))
          {
            dumpLastRevision();
          }

          if (versionsBuilder == null)
          {
            versionsBuilder = new StringBuilder();
          }
          else
          {
            versionsBuilder.append(" ");
            if (versionsBuilder.length() > 64)
            {
              versionsBuilder.append("<br>");
            }
          }

          String key = CDORevisionUtil.formatRevisionKey(rev);
          if (revision[0] == null)
          {
            revision[0] = key;
          }

          String version = getVersionPrefix(rev) + rev.getVersion();
          if (key.equals(revision[0]))
          {
            versionsBuilder.append("<b>" + version + "</b>");
          }
          else
          {
            versionsBuilder.append(browser.href(version, getName(), "revision", key));
          }

          lastRevision = rev;
        }

        protected void dumpLastRevision()
        {
          if (versionsBuilder != null)
          {
            PrintStream out = out();

            out.println("<tr>");
            out.println("<td valign=\"top\">&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println(getCDOIDLabel(lastRevision));
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

            out.println("<td>");
            out.println(versionsBuilder.toString());
            out.println("</td>");
            out.println("</tr>");

            lastRevision = null;
            versionsBuilder = null;
          }
        }
      }.dump();

      out.print("</td></tr></table></td>\r\n");
      out.print("<td>&nbsp;&nbsp;&nbsp;</td>\r\n");

      if (revision[0] != null)
      {
        out.print("<td valign=\"top\">\r\n");
        showRevision(out, browser, allRevisions, ids, revision[0], repository);
        out.print("</td>\r\n");
      }

      out.print("</tr>\r\n");
      out.print("</table>\r\n");
    }

    /**
     * @since 4.0
     */
    protected void showRevision(PrintStream pout, CDOServerBrowser browser,
        Map<CDOBranch, List<CDORevision>> allRevisions, Map<CDOID, List<CDORevision>> ids, String key,
        InternalRepository repository)
    {
      CDORevisionKey revisionKey = CDORevisionUtil.parseRevisionKey(key, repository.getBranchManager());
      for (CDORevision revision : allRevisions.get(revisionKey.getBranch()))
      {
        if (revision.getVersion() == revisionKey.getVersion() && revision.getID().equals(revisionKey.getID()))
        {
          showRevision(pout, browser, ids, (InternalCDORevision)revision);
          return;
        }
      }
    }

    /**
     * @since 4.0
     */
    protected void showRevision(PrintStream pout, CDOServerBrowser browser, Map<CDOID, List<CDORevision>> ids,
        InternalCDORevision revision)
    {
      String className = revision.getEClass().toString();
      className = className.substring(className.indexOf(' '));
      className = StringUtil.replace(className, new String[] { "(", ")", "," }, new String[] { "<br>", "", "<br>" });
      className = className.substring("<br>".length() + 1);

      String created = CDOCommonUtil.formatTimeStamp(revision.getTimeStamp());
      String commitInfo = browser.href(created, HistoryPage.NAME, "time", String.valueOf(revision.getTimeStamp()));

      pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      showKeyValue(pout, true, "type", "<b>" + revision.getClass().getSimpleName() + "</b>");
      showKeyValue(pout, true, "class", className);
      showKeyValue(pout, true, "id", getRevisionValue(revision.getID(), browser, ids, revision));
      showKeyValue(pout, true, "branch", revision.getBranch().getName() + "[" + revision.getBranch().getID() + "]");
      showKeyValue(pout, true, "version", revision.getVersion());
      showKeyValue(pout, true, "created", commitInfo);
      showKeyValue(pout, true, "revised", CDOCommonUtil.formatTimeStamp(revision.getRevised()));
      if (revision instanceof SyntheticCDORevision)
      {
        if (revision instanceof PointerCDORevision)
        {
          PointerCDORevision pointer = (PointerCDORevision)revision;
          CDOBranchVersion target = pointer.getTarget();
          CDOBranch branch = target.getBranch();
          int version = target.getVersion();

          String label = getVersionLabel("v", version, branch);
          CDORevisionKey targetKey = CDORevisionUtil.createRevisionKey(pointer.getID(), branch, version);
          String value = CDORevisionUtil.formatRevisionKey(targetKey);
          showKeyValue(pout, true, "target", browser.href(label, getName(), "revision", value));
        }
      }
      else
      {
        showKeyValue(pout, true, "resource", getRevisionValue(revision.getResourceID(), browser, ids, revision));
        showKeyValue(pout, true, "container", getRevisionValue(revision.getContainerID(), browser, ids, revision));
        showKeyValue(pout, true, "feature", revision.getContainingFeatureID());

        for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentFeatures())
        {
          Object value = revision.getValue(feature);
          showKeyValue(pout, false, feature.getName(), getRevisionValue(value, browser, ids, revision));
        }
      }

      pout.print("</table>\r\n");
    }

    /**
     * @since 4.0
     */
    protected Object getRevisionValue(Object value, CDOServerBrowser browser, Map<CDOID, List<CDORevision>> ids,
        InternalCDORevision context)
    {
      if (value instanceof CDOID)
      {
        List<CDORevision> revisions = ids.get(value);
        if (revisions != null)
        {
          StringBuilder builder = new StringBuilder();
          builder.append(getCDOIDLabel(revisions.get(0)));

          if (browser != null)
          {
            builder.append("&nbsp;&nbsp;");
            for (CDORevision revision : revisions)
            {
              String versionPrefix = getVersionPrefix(revision);
              int version = revision.getVersion();
              CDOBranch branch = revision.getBranch();
              String label = getVersionLabel(versionPrefix, version, branch);

              builder.append(" ");
              if (revision == context)
              {
                builder.append(label);
              }
              else
              {
                builder.append(browser.href(label, getName(), "revision", CDORevisionUtil.formatRevisionKey(revision)));
              }
            }
          }

          return builder.toString();
        }
      }

      if (value instanceof Collection)
      {
        StringBuilder builder = new StringBuilder();
        for (Object element : (Collection<?>)value)
        {
          builder.append(builder.length() == 0 ? "" : "<br>");
          builder.append(getRevisionValue(element, browser, ids, context));
        }

        return builder.toString();
      }

      return value;
    }

    private String getVersionLabel(String versionPrefix, int version, CDOBranch branch)
    {
      String label = versionPrefix + version;
      String branchName = branch.getName();
      if (!CDOBranch.MAIN_BRANCH_NAME.equals(branchName))
      {
        label += "[" + branchName + "]";
      }
      return label;
    }

    private String getVersionPrefix(CDORevision revision)
    {
      if (revision instanceof PointerCDORevision)
      {
        return "p";
      }

      if (revision instanceof DetachedCDORevision)
      {
        return "d";
      }

      return "v";
    }

    /**
     * @since 4.0
     */
    protected void showKeyValue(PrintStream pout, boolean bg, String key, Object value)
    {
      String color = bg ? "EEEEEE" : "FFFFFF";
      pout.print("<tr bgcolor=\"" + color + "\">\r\n");
      pout.print("<td valign=\"top\"><b>" + key + "</b></td>\r\n");
      pout.print("<td valign=\"top\">");
      pout.print(value);
      pout.print("</td>\r\n");
      pout.print("</tr>\r\n");
    }

    protected abstract Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository);

    private Map<CDOID, List<CDORevision>> getAllIDs(Map<CDOBranch, List<CDORevision>> allRevisions)
    {
      Map<CDOID, List<CDORevision>> ids = new HashMap<CDOID, List<CDORevision>>();
      for (List<CDORevision> list : allRevisions.values())
      {
        for (CDORevision revision : list)
        {
          CDOID id = revision.getID();
          List<CDORevision> revisions = ids.get(id);
          if (revisions == null)
          {
            revisions = new ArrayList<CDORevision>();
            ids.put(id, revisions);
          }

          revisions.add(revision);
        }
      }

      return ids;
    }

    protected String getCDOIDLabel(CDORevision revision)
    {
      String label = revision.toString();
      return label.substring(0, label.indexOf(':'));
    }

    /**
     * A {@link Page server browser page} that renders the {@link CDORevision revisions} in a revision cache.
     *
     * @author Eike Stepper
     */
    public static class FromCache extends RevisionsPage
    {
      public static final String NAME = "crevisions";

      public FromCache()
      {
        super(NAME, "Revisions From Cache");
      }

      public boolean canDisplay(InternalRepository repository)
      {
        return true;
      }

      @Override
      protected Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository)
      {
        return repository.getRevisionManager().getCache().getAllRevisions();
      }
    }

    /**
     * A {@link Page server browser page} that renders the {@link CDORevision revisions} in a {@link IStore store}.
     *
     * @author Eike Stepper
     */
    public static class FromStore extends RevisionsPage
    {
      public static final String NAME = "srevisions";

      public FromStore()
      {
        super(NAME, "Revisions From Store");
      }

      public boolean canDisplay(InternalRepository repository)
      {
        return repository.getStore() instanceof CDOAllRevisionsProvider;
      }

      @Override
      protected Map<CDOBranch, List<CDORevision>> getAllRevisions(InternalRepository repository)
      {
        return ((CDOAllRevisionsProvider)repository.getStore()).getAllRevisions();
      }
    }
  }

  /**
   * A {@link Page server browser page} that renders {@link CDOLobInfo large object infos}.
   *
   * @author Eike Stepper
   */
  public static class LobsPage extends AbstractPage
  {
    public static final String NAME = "lobs";

    public LobsPage()
    {
      super(NAME, "Large Objects");
    }

    public boolean canDisplay(InternalRepository repository)
    {
      return true;
    }

    public void display(final CDOServerBrowser browser, InternalRepository repository, final PrintStream out)
    {
      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");
      out.print("<td valign=\"top\">\r\n");

      IStoreAccessor accessor = repository.getStore().getReader(null);
      StoreThreadLocal.setAccessor(accessor);

      final String param = browser.getParam("id");
      final Object[] details = { null, null, null };

      try
      {
        repository.handleLobs(0, 0, new CDOLobHandler()
        {
          public OutputStream handleBlob(byte[] id, long size)
          {
            if (showLob(out, "Blob", id, size, browser, param))
            {
              ByteArrayOutputStream result = new ByteArrayOutputStream();
              details[0] = result;
              details[1] = param;
              details[2] = size;
              return result;
            }

            return null;
          }

          public Writer handleClob(byte[] id, long size)
          {
            if (showLob(out, "Clob", id, size, browser, param))
            {
              CharArrayWriter result = new CharArrayWriter();
              details[0] = result;
              details[1] = param;
              details[2] = size;
              return result;
            }

            return null;
          }
        });
      }
      catch (IOException ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        StoreThreadLocal.release();
      }

      out.print("</td>\r\n");

      if (details[0] != null)
      {
        out.print("<td>&nbsp;&nbsp;&nbsp;</td>\r\n");
        out.print("<td valign=\"top\">\r\n");
        if (details[0] instanceof ByteArrayOutputStream)
        {
          ByteArrayOutputStream baos = (ByteArrayOutputStream)details[0];
          String hex = HexUtil.bytesToHex(baos.toByteArray());

          out.println("<h3>Blob " + details[1] + " (" + details[2] + ")</h3>");
          out.println("<pre>\r\n");
          for (int i = 0; i < hex.length(); i++)
          {
            out.print(hex.charAt(i));
            if ((i + 1) % 32 == 0)
            {
              out.print("\r\n");
            }
            else if ((i + 1) % 16 == 0)
            {
              out.print("  ");
            }
            else if ((i + 1) % 2 == 0)
            {
              out.print(" ");
            }
          }

          out.println("</pre>\r\n");
        }
        else
        {
          CharArrayWriter caw = (CharArrayWriter)details[0];
          out.println("<h3>Clob " + details[1] + " (" + details[2] + ")</h3>");
          out.println("<pre>" + caw + "</pre>");
        }

        out.print("</td>\r\n");
      }

      out.print("</tr>\r\n");
      out.print("</table>\r\n");
    }

    protected boolean showLob(PrintStream out, String type, byte[] id, long size, CDOServerBrowser browser, String param)
    {
      String hex = HexUtil.bytesToHex(id);
      boolean selected = hex.equals(param);
      String label = selected ? hex : browser.href(hex, getName(), "id", hex);
      out.println(type + " " + label + " (" + size + ")");
      return selected;
    }
  }

  /**
   * A {@link Page server browser page} that renders {@link CDOCommitInfo commit infos}.
   *
   * @author Eike Stepper
   */
  public static class HistoryPage extends AbstractPage
  {
    public static final String NAME = "history";

    public HistoryPage()
    {
      super(NAME, "Commit Infos");
    }

    public boolean canDisplay(InternalRepository repository)
    {
      return true;
    }

    public void display(final CDOServerBrowser browser, InternalRepository repository, final PrintStream out)
    {
      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");
      out.print("<td valign=\"top\">\r\n");

      IStoreAccessor accessor = repository.getStore().getReader(null);
      StoreThreadLocal.setAccessor(accessor);

      final String param = browser.getParam("time");

      out.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      out.print("<tr>\r\n");
      out.print("<td valign=\"top\">Time</td>\r\n");
      out.print("<td valign=\"top\">Branch</td>\r\n");
      out.print("<td valign=\"top\">User</td>\r\n");
      out.print("<td valign=\"top\">Comment</td>\r\n");
      out.print("</tr>\r\n");

      final CDOCommitInfo[] details = { null };

      try
      {
        final boolean auditing = repository.isSupportingAudits();
        repository.getCommitInfoManager().getCommitInfos(null, 0L, 0L, new CDOCommitInfoHandler()
        {
          public void handleCommitInfo(CDOCommitInfo commitInfo)
          {
            if (showCommitInfo(out, commitInfo, browser, param, auditing))
            {
              details[0] = commitInfo;
            }
          }
        });

        out.print("</table>\r\n");
        out.print("</td>\r\n");
        out.print("<td>&nbsp;&nbsp;&nbsp;</td>\r\n");
        out.print("<td valign=\"top\">\r\n");

        if (auditing)
        {
          CDOCommitInfo commitInfo = details[0];
          if (commitInfo != null)
          {
            out.print("<h3>Commit Info " + commitInfo.getTimeStamp() + "</h3>\r\n");
            showCommitData(out, commitInfo, browser);
          }
        }
        else
        {
          out.print("<h3>No audit data available in this repository.</h3>\r\n");
        }

        out.print("</td>\r\n");
        out.print("</tr>\r\n");
        out.print("</table>\r\n");
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    protected boolean showCommitInfo(PrintStream out, CDOCommitInfo commitInfo, CDOServerBrowser browser, String param,
        boolean auditing)
    {
      String timeStamp = String.valueOf(commitInfo.getTimeStamp());
      boolean selected = timeStamp.equals(param);

      String formatted = CDOCommonUtil.formatTimeStamp(commitInfo.getTimeStamp()).replaceAll(" ", "&nbsp;");
      String label = formatted;
      if (!selected && auditing)
      {
        label = browser.href(formatted, getName(), "time", timeStamp);
      }

      out.print("<tr>\r\n");
      out.print("<td valign=\"top\">\r\n");
      out.print(label);
      out.print("</td>\r\n");

      CDOBranch branch = commitInfo.getBranch();
      out.print("<td valign=\"top\">\r\n");
      out.print(branch.getName() + "[" + branch.getID() + "]");
      out.print("</td>\r\n");

      String userID = commitInfo.getUserID();
      out.print("<td valign=\"top\">\r\n");
      out.print(StringUtil.isEmpty(userID) ? "&nbsp;" : browser.escape(userID));
      out.print("</td>\r\n");

      String comment = commitInfo.getComment();
      out.print("<td valign=\"top\">\r\n");
      out.print(StringUtil.isEmpty(comment) ? "&nbsp;" : browser.escape(comment));
      out.print("</td>\r\n");

      out.print("</tr>\r\n");
      return selected;
    }

    protected void showCommitData(PrintStream out, CDOCommitInfo commitInfo, CDOServerBrowser browser)
    {
      out.print("<h4>New Objects:</h4>\r\n");
      out.print("<ul>\r\n");
      for (CDOIDAndVersion key : commitInfo.getNewObjects())
      {
        CDORevision newObject = (CDORevision)key;
        out.print("<li>"
            + browser.href(newObject.toString(), RevisionsPage.FromStore.NAME, "revision",
                CDORevisionUtil.formatRevisionKey(newObject)) + "<br>\r\n");
      }

      out.print("</ul>\r\n");
      out.print("<h4>Changed Objects:</h4>\r\n");
      out.print("<ul>\r\n");
      for (CDORevisionKey key : commitInfo.getChangedObjects())
      {
        CDORevisionDelta changedObject = (CDORevisionDelta)key;
        out.print("<li>" + changedObject.toString() + "<br>\r\n");
      }

      out.print("</ul>\r\n");
      out.print("<h4>Detached Objects:</h4>\r\n");
      out.print("<ul>\r\n");
      for (CDOIDAndVersion key : commitInfo.getDetachedObjects())
      {
        out.print("<li>" + key.toString() + "<br>\r\n");
      }

      out.print("</ul>\r\n");
    }
  }
}
