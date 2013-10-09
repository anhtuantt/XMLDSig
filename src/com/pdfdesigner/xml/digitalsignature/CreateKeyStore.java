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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Properties;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class CreateKeyStore {
	static {
		// adds the Bouncy castle provider to java security
		Security.addProvider(new BouncyCastleProvider());
	}

	static String KEYSTORETYPE = "JKS";
	static String KEYSTORE, KEYSTOREPASS, KEYNAME, KEYPASS, SUBJECT, ISSUER;
	static int VALIDITY;

	public static void loadConfigurations() throws FileNotFoundException,
			IOException {
		Properties props = new Properties();
		props.load(new FileInputStream("keystore/key.properties"));

		KEYSTORE = (String) props.get("keystore");
		KEYSTOREPASS = (String) props.get("keystorepass");
		KEYNAME = (String) props.get("keyname");
		KEYPASS = (String) props.get("keypass");
		SUBJECT = (String) props.get("subject");
		ISSUER = (String) props.get("issuer");
		VALIDITY = Integer.parseInt((String) props.get("validity"));
	}

	public static void main(String[] args) throws Exception {
		loadConfigurations();

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// yesterday
		Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60
				* 60 * 1000);
		// in validity days
		Date validityEndDate = new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * VALIDITY));

		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		v3CertGen
				.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		v3CertGen.setIssuerDN(new X509Principal(ISSUER));
		v3CertGen.setNotBefore(validityBeginDate);
		v3CertGen.setNotAfter(validityEndDate);
		v3CertGen.setSubjectDN(new X509Principal(SUBJECT));

		v3CertGen.setPublicKey(keyPair.getPublic());
		v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		X509Certificate PKCertificate = v3CertGen
				.generate(keyPair.getPrivate());
		// FileOutputStream fos = new
		// FileOutputStream("keystore/testCert.cert");
		// fos.write(PKCertificate.getEncoded());
		// fos.close();

		KeyStore keyStore = KeyStore.getInstance(KEYSTORETYPE);
		keyStore.load(null, null);

		keyStore.setKeyEntry(KEYNAME, keyPair.getPrivate(),
				KEYPASS.toCharArray(),
				new java.security.cert.Certificate[] { PKCertificate });
		keyStore.store(new FileOutputStream(KEYSTORE),
				KEYSTOREPASS.toCharArray());

	}

}
