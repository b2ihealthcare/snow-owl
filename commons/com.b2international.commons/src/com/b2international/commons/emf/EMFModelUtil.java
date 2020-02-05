/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.emf;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @since 3.3
 */
public class EMFModelUtil {

	/**
	 * Convenience method to load EMF models from archives based on a path and
	 * an entry fileName.
	 * 
	 * @param archiveFilePath
	 * @param entryName
	 * @param resSet
	 * @return
	 */
	public static final EObject loadFromArchive(final String archiveFilePath, final String entryName,
			final ResourceSet resSet) {
		return load(generateArchiveUri(archiveFilePath, entryName), resSet);
	}

	/**
	 * Generates the following URI format for zip entries based on the given
	 * parameters.
	 * <p>
	 * archive:file:/archiveFilePath!/entryName
	 * </p>
	 * 
	 * @param archiveFilePath
	 * @param entryName
	 * @return an URI to load EMF models from ZIP content.
	 */
	public static final URI generateArchiveUri(final String archiveFilePath, final String entryName) {
		final StringBuilder builder = new StringBuilder();
		builder.append("archive:file:/").append(archiveFilePath).append("!/").append(entryName);
		return URI.createURI(builder.toString());
	}

	/**
	 * Loads the resource specified by the {@link URI} as model, and returns the
	 * root {@link EObject} element.
	 * 
	 * @param uri
	 * @param resourceSet
	 * @return the root {@link EObject} element, or <code>null</code> if the
	 *         resource was empty.
	 * @throws IllegalArgumentException
	 *             - if either of the specified input was invalid.
	 */
	public static final EObject load(URI uri, ResourceSet resourceSet) {
		checkArgument(uri != null, "URI must be specified");
		checkArgument(resourceSet != null, "ResourceSet must be specified");
		final Resource resource = resourceSet.getResource(uri, true);
		if (resource.getContents() != null && !resource.getContents().isEmpty()) {
			return resource.getContents().get(0);
		}
		throw new IllegalArgumentException("The specified input path does not contain any model element: "
				+ uri.toFileString());
	}

}