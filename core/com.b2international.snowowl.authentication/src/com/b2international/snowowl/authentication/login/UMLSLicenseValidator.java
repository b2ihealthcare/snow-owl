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
package com.b2international.snowowl.authentication.login;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.b2international.commons.StringUtils;
import com.google.common.io.Closeables;

public class UMLSLicenseValidator {

	private static final String ENCODING = "UTF-8";
	private static final String UMLS_REST_SERVICE_URL = "https://uts-ws.nlm.nih.gov/restful/isValidUMLSUser";
	private static final String DEFAULT_LICENSE = "NLM-9318116480";

	public boolean validate(String username, String password) {
		checkArgument(StringUtils.isEmpty(username), "Username must be specified.");
		checkArgument(StringUtils.isEmpty(username), "Username must be specified.");
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		try {
			String data = URLEncoder.encode("licenseCode", ENCODING) + "=" + URLEncoder.encode(DEFAULT_LICENSE, ENCODING);
			data += "&" + URLEncoder.encode("user", ENCODING) + "=" + URLEncoder.encode(username, ENCODING);
			data += "&" + URLEncoder.encode("password", ENCODING) + "=" + URLEncoder.encode(password, ENCODING);
			
			URL url = new URL(UMLS_REST_SERVICE_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				// TODO implement validation evaluation
				System.out.println(line);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Closeables.close(writer, true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Closeables.closeQuietly(reader);
		}
		return false;
	}

}