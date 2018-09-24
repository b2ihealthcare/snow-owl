/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons.platform;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.CommonsActivator;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

/**
 * @since 3.1
 */
public class PlatformUtil {

	public static final String UNQUALIFIED = "qualifier";

	private PlatformUtil() {
	}
	
	/**
	 * @return <code>true</code> if the platform is running in dev mode, otherwise <code>false</code>.
	 */
	public static final boolean isDevVersion() {
		return isDevVersion(CommonsActivator.PLUGIN_ID);
	}

	/**
	 * Returns <code>true</code> if the given bundle is available and has the
	 * qualifier {@link #UNQUALIFIED}, otherwise <code>false</code>.
	 * 
	 * @param bundleName
	 * @return
	 * @throws IllegalArgumentException
	 *             - if the given bundleName is <code>null</code> or empty.
	 */
	public static final boolean isDevVersion(String bundleName) {
		checkArgument(!Strings.isNullOrEmpty(bundleName), "BundleName should not be null or empty");
		final Bundle bundle = Platform.getBundle(bundleName);
		return bundle != null && UNQUALIFIED.equals(bundle.getVersion().getQualifier());
	}

	/**
	 * Returns the {@link PreferencesService} from the OSGi service registry.
	 * 
	 * @param bundleContext
	 * @return
	 * @throws NullPointerException
	 *             - if the given {@link BundleContext} is <code>null</code>
	 * @throws IllegalStateException
	 *             - if the {@link PreferencesService} is not found in the OSGI
	 *             service registry
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static PreferencesService getPreferencesService(BundleContext bundleContext) {
		checkNotNull(bundleContext, "bundleContext");
		final ServiceReference serviceReference = bundleContext.getServiceReference(PreferencesService.class.getName());
		if (serviceReference != null) {
			return (PreferencesService) bundleContext.getService(serviceReference);
		}
		throw new IllegalStateException("PreferencesService not found in OSGI service registry.");
	}

	/**
	 * Enables the native system proxies on the client. Since Eclipse uses SOCKS
	 * proxy before HTTP, and client does not have a SOCKS proxy provider, we
	 * have to enable system proxies instead. See:
	 * https://github.com/b2ihealthcare/mohh/issues/188
	 */
	public static void enableSystemProxies(BundleContext context) {
		final ServiceReference<IProxyService> proxyServiceReference = context
				.getServiceReference(org.eclipse.core.net.proxy.IProxyService.class);
		checkNotNull(proxyServiceReference,
				"Error while enabling system proxies. Reason: proxy service reference was null.");
		final IProxyService proxyService = context.getService(proxyServiceReference);
		checkNotNull(proxyService, "Error while enabling system proxies. Reason: proxy service was null.");
		proxyService.setSystemProxiesEnabled(true);
	}

	/**
	 * Converts the given bundle based URL to a file based one.
	 * 
	 * @param url
	 * @return an URL instance with protocol of file.
	 */
	public static URL getBundleFileURL(URL url) {
		try {
			return FileLocator.toFileURL(url);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to convert %s to file URL", url), e);
		}
	}

	/**
	 * Returns a file based URL for a given resource in the given contextClass'
	 * bundle.
	 * 
	 * @param contextClass
	 * @param resourceClassPathLocation
	 * @return an URL instance with protocol of file
	 * @throws IllegalArgumentException
	 *             - if the resource is not found
	 * @throws RuntimeException
	 *             - if it is impossible to convert to a file based URL
	 */
	public static URL toFileURL(Class<?> contextClass, String resourceClassPathLocation) {
		final URL bundleURL = Resources.getResource(contextClass, resourceClassPathLocation);
		return getBundleFileURL(bundleURL);
	}

	/**
	 * Returns the absolute path of a bundle resource based on the given
	 * contextClass and location parameter.
	 * 
	 * @param contextClass
	 * @param resourceClassPathLocation
	 * @return the absolute file path of a bundled resource
	 * @throws RuntimeException
	 *             - if something happens during conversion.
	 */
	public static Path toAbsolutePath(Class<?> contextClass, String resourceClassPathLocation) {
		return toAbsolutePath(toFileURL(contextClass, resourceClassPathLocation));
	}

	/**
	 * Returns the absolute file path from the given fileURL.
	 * 
	 * @param fileURL
	 * @return
	 */
	public static Path toAbsolutePath(URL fileURL) {
		try {
			fileURL = new URL(fileURL.toString().replaceAll(" ", "%20"));
			return Paths.get(fileURL.toURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param url
	 * @return
	 */
	public static Path toAbsoluteBundlePath(URL bundleUrl) {
		return toAbsolutePath(getBundleFileURL(bundleUrl));
	}

	/**
	 * Returns the absolute file path for an entry denoted by the given relative
	 * path in the given class' bundle.
	 * 
	 * @param contextClass
	 * @param path
	 * @return
	 */
	public static Path toAbsolutePathBundleEntry(Class<?> contextClass, String path) {
		final Bundle bundle = checkNotNull(FrameworkUtil.getBundle(contextClass), "Bundle not found for %s", contextClass);
		return toAbsoluteBundlePath(checkNotNull(bundle.getEntry(path), "Bundle entry not found at %s in bundle %s", path, bundle.getSymbolicName()));
	}
	
	/**
	 * Returns <code>true</code> if the underlying system is a Mac OS X, <code>false</code> otherwise. 
	 * @return
	 */
	public static boolean isOSX() {
		return Platform.OS_MACOSX.equals(Platform.getOS());
	}

}