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

package com.pdfdesigner.xml.encrytion;

import java.io.File;
import java.io.FileOutputStream;

import java.security.Key;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.EncryptionConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;

/**
 * The DecryptTool class reads an encrypted file from disk, decrypts the
 * contents of the file using a previously-stored key, and then stores the
 * decrypted file to disk.
 * 
 */

public class DecryptTool {
	static {
		org.apache.xml.security.Init.init();
	}

	private static Document loadEncryptedFile(String fileName) throws Exception {
		File encryptedFile = new File(fileName);
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		dbf.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder builder = dbf.newDocumentBuilder();
		Document document = builder.parse(encryptedFile);

		System.out.println("Encryption document loaded from: "
				+ encryptedFile.toURL().toString());
		return document;
	}

	private static SecretKey loadKeyEncryptionKey(String fileName) throws Exception {
		String jceAlgorithmName = "DESede";
		fileName ="keyEncryptKey";
		File kekFile = new File(fileName);

		DESedeKeySpec keySpec = new DESedeKeySpec(
				JavaUtils.getBytesFromFile(fileName));
		SecretKeyFactory skf = SecretKeyFactory.getInstance(jceAlgorithmName);
		SecretKey key = skf.generateSecret(keySpec);

		System.out.println("Key encryption key loaded from: "
				+ new String(key.getEncoded()));
		return key;
	}

	private static void writeDecryptedDocToFile(Document doc, String fileName)
			throws Exception {
		File encryptionFile = new File(fileName);
		FileOutputStream outStream = new FileOutputStream(encryptionFile);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outStream);
		transformer.transform(source, result);

		outStream.close();

		System.out.println("Decrypted data written to: "
				+ encryptionFile.toURL().toString());
	}

	private static void usage() {
		System.err.println("usage - java DecryptTool " + "infile  keyEncryptKey outfile");
		System.err.println("example - java DecryptTool "
				+ "encrypted.xml keyEncryptKey original.xml");
	}

	public static void main(String args[]) throws Exception {
		if (args.length < 3) {
			usage();
			System.exit(1);
		}

		// load the encrypted file into a Document
		Document document = loadEncryptedFile(args[0]);

		// Load the key encryption key.
		Key keyEncryptKey = loadKeyEncryptionKey(args[1]);

		// initialize cipher
		XMLCipher xmlCipher = XMLCipher.getInstance();
		xmlCipher.init(XMLCipher.DECRYPT_MODE, null);

		xmlCipher.setKEK(keyEncryptKey);

		// get the encrypted data element
		String namespaceURI = EncryptionConstants.EncryptionSpecNS;
		String localName = EncryptionConstants._TAG_ENCRYPTEDDATA;

		NodeList elementsToDecrypt = document.getElementsByTagNameNS(
				namespaceURI, localName);
		while (elementsToDecrypt.getLength() != 0) {
			Element encryptedDataElement = (Element) elementsToDecrypt.item(0);
			// do the actual decryption
			xmlCipher.doFinal(document, encryptedDataElement);
			System.out.println(encryptedDataElement);
		}
		
		
		
		/*
		 * Element encryptedDataElement = (Element) document
		 * .getElementsByTagNameNS(namespaceURI, localName).item(0);
		 */

		// write the results to a file
		writeDecryptedDocToFile(document, args[2]);
	}
}
