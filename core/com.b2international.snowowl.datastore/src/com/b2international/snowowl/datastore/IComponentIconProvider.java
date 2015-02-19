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
package com.b2international.snowowl.datastore;

import java.io.File;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.IDisposableService;

/**
 * Interface for UI independent terminology independent component icon provider.
 * 
 * 
 * @see IDisposableService
 * @param <K>
 *            - type of the unique ID of the terminology independent component.
 */
public interface IComponentIconProvider<K> extends IDisposableService {

	/**
	 * Refreshes the state of the current {@link IComponentIconProvider component icon provider} instance.
	 */
	void refresh();

	/**
	 * Returns with the closest parent component from the taxonomy which determines the icon/image for a component
	 * specified by its unique ID.
	 * 
	 * @param componentId
	 *            the unique ID of the component.
	 * @return the unique ID of the closest parent.
	 */
	K getIconComponentId(final K componentId);

	/**
	 * The physical file pointing at the icon for the terminology component identified by the specified unique ID.
	 * 
	 * @param componentId
	 *            the unique ID of the component.
	 * @return the file pointing to the icon for the component.
	 */
	File getIconFile(final K componentId);

	/**
	 * The file for the specified terminology independent component.
	 * <p>
	 * <b>NOTE:&nbsp;</b>file may *NOT* exist if the component specified by its ID does not exist in the taxonomy.
	 * 
	 * @param componentId
	 *            the ID of the component.
	 * @return the icon file. May *NOT* exist.
	 */
	File getExactFile(final K componentId);

	/**
	 * The file for the specified terminology independent component.
	 * <p>
	 * <b>NOTE:&nbsp;</b>file may *NOT* exist if the component specified by its ID does not exist in the taxonomy. In
	 * this case the supplied defaultComponentId is used to return an icon.
	 * 
	 * @param componentId
	 *            - the ID of the component.
	 * @param defaultComponentId
	 *            - the default ID.
	 * @return the icon file for the componentId or the defaultComponentId.
	 */
	File getExactFile(final K componentId, final K defaultComponentId);

	/**
	 * Returns with the file name and the extension of physical file for the icon.
	 * <p>
	 * <b>NOTE:&nbsp;</b>may return with {@code null}. Clients have to make sure before referencing the file name.
	 * 
	 * @param componentId
	 *            the unique ID of the component.
	 * @return the icon file name and the extension.
	 */
	@Nullable
	String getIconFileName(final K componentId);

}