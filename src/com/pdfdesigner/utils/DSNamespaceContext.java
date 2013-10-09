/**
 * The MIT License (MIT)

Copyright (c) 2013 pdfformdesigner.com
Lead developer Nguyen Anh Tuan -anhtuantt@gmail.com / admin@pdfformsdesigner.com

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

package com.pdfdesigner.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * A NamespaceContext implementation for digital signatures
 */
public class DSNamespaceContext implements NamespaceContext {

	private Map<String, String> namespaceMap = new HashMap<String, String>();

	public DSNamespaceContext() {
		namespaceMap.put("ds", "http://www.w3.org/2000/09/xmldsig#");
	}

	public DSNamespaceContext(Map<String, String> namespaces) {
		this();
		namespaceMap.putAll(namespaces);
	}

	public String getNamespaceURI(String arg0) {
		return namespaceMap.get(arg0);
	}

	public void putPrefix(String prefix, String namespace) {
		namespaceMap.put(prefix, namespace);
	}

	public String getPrefix(String arg0) {
		for (String key : namespaceMap.keySet()) {
			String value = namespaceMap.get(key);
			if (value.equals(arg0)) {
				return key;
			}
		}
		return null;
	}

	public Iterator<String> getPrefixes(String arg0) {
		return namespaceMap.keySet().iterator();
	}
}
