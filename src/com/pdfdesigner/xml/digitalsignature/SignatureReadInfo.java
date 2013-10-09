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

package com.pdfdesigner.xml.digitalsignature;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Enumeration;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Element;

import com.pdfdesigner.utils.DSNamespaceContext;

public class SignatureReadInfo {

	public static void main(String args[]) {
		if (args.length < 1) {
			System.err.println("usage - java SignatureReadInfo " + "infile");
			System.err.println("example - java SignatureReadInfo "
					+ "XMLPOC_Signed_v01.xml");
			System.exit(1);
		}

		org.apache.xml.security.Init.init();
		String signatureFileName = args[0];
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		dbf.setNamespaceAware(true);

		try {
			File f = new File(signatureFileName);
			System.out.println("Try to read signature from  " + f.toURI().toURL().toString());

			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());
			org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));

			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new DSNamespaceContext());

			String expression = "//ds:Signature[1]";
			Element sigElement = (Element) xpath.evaluate(expression, doc,
					XPathConstants.NODE);
			XMLSignature signature = new XMLSignature(sigElement, f.toURI()
					.toURL().toString());

			KeyInfo ki = signature.getKeyInfo();
			if (ki != null) {
				if (ki.containsX509Data()) {
					System.out
							.println("Could find a X509Data element in the  KeyInfo");
				}
				X509Certificate cert = signature.getKeyInfo()
						.getX509Certificate();
				System.out.println("-- issuer " + cert.getIssuerDN().getName());
				System.out.println("-- valid from " + cert.getNotBefore());
				System.out.println("-- expires " + cert.getNotAfter());

			} else {
				System.out.println("Did not find a KeyInfo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
