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
package com.b2international.snowowl.snomed.core.refset.automap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

/**
 * I/O utility class for deserializing and persisting values from/to XML (using XStream).
 * 
 */
public class RefSetAutoMapPersisterUtil {

	private static final String NATURE_ID = "com.b2international.snowowl.snomed.refset.ui.automappingnature";

	/**
	 * Persists the given model to XML file at the given location. Uses <code>UTF-8</code> character encoding.<br>
	 * This method creates a new file.
	 * 
	 * @param model {@link RefSetAutoMapperModel} to persist
	 * @param targetPath {@link IPath} the location where to persist the model
	 * @param fileName String the name of the persisted XML file
	 * 
	 * @return the newly created file, <b>null</b> if there was error during the process
	 * @throws SnowowlServiceException 
	 */
	public static IFile persist(RefSetAutoMapperModel model, IPath targetPath, String fileName) throws SnowowlServiceException {
		InputStream is = null;
		try {
			String builtXml = serializeToXml(model);

			is = new ByteArrayInputStream(builtXml.toString().getBytes("UTF-8"));

			IFile file = saveFile(is, targetPath, fileName);

			return file;
		}  catch (UnsupportedEncodingException e) {
			ApplicationContext.handleException(SnomedDatastoreActivator.getContext().getBundle(), e, e.getMessage());
		} finally {
			Closeables.closeQuietly(is);
		}
		return null;
	}

	/**
	 * Persists the given model to XML file at the given location. Uses <code>UTF-8</code> character encoding.<br>
	 * If the file already exists, it <b>overrides</b> with the new values, creates a new file otherwise.
	 * 
	 * @param model {@link RefSetAutoMapperModel} to persist
	 * @param file {@link IFile} the file to persist the serialized model
	 * 
	 * @return the newly created file, <b>null</b> if there was error during the creation
	 */
	public static File persist(RefSetAutoMapperModel model, File file) {
		InputStream is = null;
		try {
			String builtXml = serializeToXml(model);
			File savedFile = saveFile(builtXml, file);

			return savedFile;
		} catch (SnowowlServiceException e) {
			ApplicationContext.handleException(SnomedDatastoreActivator.getContext().getBundle(), e, e.getMessage());
		} finally {
			Closeables.closeQuietly(is);
		}
		return null;
	}

	/**
	 * Takes the model and serializes it to XML
	 * 
	 * @param model
	 * @return String representation of the passed {@link RefSetAutoMapperModel}
	 */
	public static String serializeToXml(RefSetAutoMapperModel model) {
		final XStream xStream = new XStream();
		xStream.processAnnotations(RefSetAutoMapperModel.class);

		String serializedToXml = xStream.toXML(model);

		return serializedToXml;
	}

	/**
	 * Takes the XML file and deserializes to {@link RefSetAutoMapperModel}.
	 * 
	 * @param file
	 * @return
	 * @throws SnowowlServiceException
	 */
	public static RefSetAutoMapperModel deSerializeFromXml(File file) throws SnowowlServiceException {
		BufferedInputStream is = null;
		try {
			final XStream xStream = new XStream(new Xpp3Driver());
			xStream.setClassLoader(RefSetAutoMapperModel.class.getClassLoader());
			xStream.processAnnotations(RefSetAutoMapperModel.class);
			RefSetAutoMapperModel model = (RefSetAutoMapperModel) xStream.fromXML(file);
			return model;
		} catch (Exception e) {
			throw new SnowowlServiceException(e);
		} finally {
			Closeables.closeQuietly(is);
		}
	}

	/**
	 * Saves the passed inputstream's contents to the filesystem with the location of <code>targetPath</code> and the name of <code>fileName</code>.<br>
	 * Please note this method will create a new file if does not exist overwrites otherwise and associate the corresponding project with the automapper nature id (<code>NATURE_ID</code>).
	 * 
	 * @param inputStream the contents of the new file
	 * @param targetPath the location of the new file
	 * @param fileName the name of the file
	 * 
	 * @return IFile pointing to the newly created file
	 * @throws SnowowlServiceException
	 */
	public static IFile saveFile(InputStream inputStream, IPath targetPath, String fileName) throws SnowowlServiceException {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(targetPath.segment(0));
			IPath fullPath = targetPath.removeFirstSegments(1).append(fileName);
			IFile file = project.getFile(fullPath);

			if (file.exists()) {
				file.setContents(inputStream, IResource.FORCE, null);
			} else {
				file.create(inputStream, true, null);
			}

			if (!project.getDescription().hasNature(NATURE_ID)) {
				String[] availNatures = project.getDescription().getNatureIds();

				String[] newNatures = new String[availNatures.length + 1];
				System.arraycopy(availNatures, 0, newNatures, 0, availNatures.length);

				newNatures[availNatures.length] = NATURE_ID;

				project.getDescription().setNatureIds(newNatures);
			}

			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			return file;
		} catch (CoreException e) {
			throw new SnowowlServiceException(e);
		}
	}

	/**
	 * Saves the passed inputstream's contents to the filesystem to the passed IFile object.<br>
	 * Please note this method will create a new file if does not exist overwrite otherwise and associate the corresponding project with the automapper nature id (<code>NATURE_ID</code>).
	 * 
	 * @param builtXml the contents of the new file
	 * @param file
	 * @return IFile pointing to the newly created file
	 * @throws SnowowlServiceException
	 */
	public static File saveFile(String builtXml, File file) throws SnowowlServiceException {
		try {

			if (!file.exists()) {
				file.createNewFile();
			}
			Files.write(builtXml, file, Charsets.UTF_8);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath location = Path.fromOSString(file.getAbsolutePath());
			if (null == location)
				return file;
			IFile ifile = workspace.getRoot().getFileForLocation(location);
			if (null == ifile)
				return file;

			IProject project = ifile.getProject();

			if (!project.getDescription().hasNature(NATURE_ID)) {
				String[] availNatures = project.getDescription().getNatureIds();

				List<String> natures = new ArrayList<String>(Arrays.asList(availNatures));
				natures.add(NATURE_ID);

				project.getDescription().setNatureIds(natures.toArray(new String[] {}));
			}

			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			return file;
		} catch (CoreException e) {
			throw new SnowowlServiceException(e);
		} catch (IOException e) {
			throw new SnowowlServiceException(e);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		final XStream xStream = new XStream();
		xStream.setClassLoader(RefSetAutoMapperModel.class.getClassLoader());
		xStream.processAnnotations(RefSetAutoMapperModel.class);
		RefSetAutoMapperModel model = (RefSetAutoMapperModel) xStream.fromXML(new BufferedInputStream(new FileInputStream(new File(
				"/home/bvizer/Terminology.setup/runtime-SnowOwl.product/automap1/nagy.automap"))));
	}
}