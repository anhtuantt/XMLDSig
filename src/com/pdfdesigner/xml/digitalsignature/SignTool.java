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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SignTool {
	static String ALGO_SIGNATURE_NAME = XMLSignature.ALGO_ID_SIGNATURE_RSA;

	static String KEYSTORETYPE = "JKS";
	static String KEYSTORE = null;
	static String KEYSTOREPASS = null;
	static String KEYNAME = null;
	static String KEYPASS = null;

	public static void loadConfigurations() throws FileNotFoundException,
			IOException {
		Properties props = new Properties();
		props.load(new FileInputStream("keystore/key.properties"));

		KEYSTORE = (String) props.get("keystore");
		KEYSTOREPASS = (String) props.get("keystorepass");
		KEYNAME = (String) props.get("keyname");
		KEYPASS = (String) props.get("keypass");
	}

	public static void main(String args[]) throws Exception {
		if (args.length < 2) {
			System.err.println("usage - java SignTool infilename outfilename");
			System.err
					.println("example - java SignTool XMLPOC_Encrypted_v01.xml XMLPOC_Signed_v01.xml");
			System.exit(1);
		}

		// Initialize the library
		org.apache.xml.security.Init.init();

		// Load properties
		loadConfigurations();

		// The file from which we will load
		String fileName = args[0];
		// Store the signed request here
		File signatureFile = new File(args[1]);
		
		// Load the keystore
		KeyStore ks = KeyStore.getInstance(KEYSTORETYPE);
		FileInputStream fis = new FileInputStream(KEYSTORE);
		ks.load(fis, KEYSTOREPASS.toCharArray());
		// And get the private key that will be used to sign the request
		PrivateKey privateKey = (PrivateKey) ks.getKey(KEYNAME,
				KEYPASS.toCharArray());

		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		// XML Signature needs to be namespace aware
		dbf.setNamespaceAware(true);

		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		db.setErrorHandler(new org.apache.xml.security.utils.IgnoreAllErrorHandler());
		org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(
				new File(fileName)));
		Element root = doc.getDocumentElement();

		// The BaseURI is the URI that's used to prepend to relative URIs
		String BaseURI = signatureFile.toURI().toURL().toString();
		// Create an XMLSignature instance
		XMLSignature sig = new XMLSignature(doc, BaseURI, ALGO_SIGNATURE_NAME);
		root.appendChild(sig.getElement());

		// Specify the transforms
		Transforms transforms = new Transforms(doc);
		transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
		transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
		sig.addDocument("", transforms,
				org.apache.xml.security.utils.Constants.ALGO_ID_DIGEST_SHA1);
		// Add the certificate and public key information from the keystore;
		// this will be needed by the verifier
		X509Certificate cert = (X509Certificate) ks.getCertificate(KEYNAME);
		sig.addKeyInfo(cert);
		sig.addKeyInfo(cert.getPublicKey());
		System.out.println("Start signing");
		sig.sign(privateKey);
		System.out.println("Finished signing");
		// Dump the signed request to file
		FileOutputStream f = new FileOutputStream(signatureFile);
		XMLUtils.outputDOMc14nWithComments(doc, f);
		f.close();
		System.out.println("Wrote signature to " + BaseURI);
	}
}
