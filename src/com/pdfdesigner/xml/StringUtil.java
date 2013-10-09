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


package com.pdfdesigner.xml;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class StringUtil {
	// ================================================================

	/**
	 * Encode a string using algorithm specified in web.xml and return the
	 * resulting encrypted password. If exception, the plain credentials string
	 * is returned
	 * 
	 * @param password
	 *            Password or other credentials to use in authenticating this
	 *            username
	 * @param algorithm
	 *            Algorithm used to do the digest
	 * 
	 * @return encypted password based on the algorithm.
	 */
	public static String encodePassword(String password, String algorithm) {
		byte[] unencodedPassword = password.getBytes();

		MessageDigest md = null;

		try {
			// first create an instance, given the provider
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			return password;
		}

		md.reset();

		// call the update method one or more times
		// (useful when you don't know the size of your data, eg. stream)
		md.update(unencodedPassword);

		// now calculate the hash
		byte[] encodedPassword = md.digest();
		System.out.println("rawString:" + new String(encodedPassword));
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}

			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		return buf.toString();
	}
	
	
	/**
	 * Encode a string using Base64 encoding. Used when storing passwords as
	 * cookies.
	 * 
	 * This is weak encoding in that anyone can use the decodeString routine to
	 * reverse the encoding.
	 * 
	 * @param str
	 * @return String
	 * @throws IOException
	 */
	public static String encodeString(String str) throws IOException {
		Base64 encoder = new org.apache.commons.codec.binary.Base64();
		
		String encodedStr = new String(encoder.encodeBase64(str.getBytes()), "UTF-8");

		return (encodedStr.trim());
	}

	
	public static String encodeString(byte bytes[]) throws IOException {
		Base64 encoder = new org.apache.commons.codec.binary.Base64();
		
		String encodedStr = new String(encoder.encodeBase64(bytes));

		return (encodedStr.trim());
	}

	
	/**
	 * Decode a string using Base64 encoding.
	 * 
	 * @param str
	 * @return String
	 * @throws IOException
	 */
	public static String decodeString(String str) throws IOException {
		Base64 encoder = new org.apache.commons.codec.binary.Base64();
		
		String encodedStr = new String(encoder.decode(str));

		return (encodedStr.trim());

	}

	
	public static String encodePassword(String pwd) {
		return encodePassword(pwd, "MD5");
	}

	
    /**
* Calculate content MD5 header values for feeds stored on disk.
*/
public static String computeContentMD5HeaderValue( InputStream fis ) 
throws IOException, NoSuchAlgorithmException {
	DigestInputStream dis = new DigestInputStream( fis, 
	MessageDigest.getInstance( "MD5" ));
	byte[] buffer = new byte[8192];
	while( dis.read( buffer ) > 0 );
	String md5Content = new String( 
	org.apache.commons.codec.binary.Base64.encodeBase64(dis.getMessageDigest().digest())
	 ); 
	// Effectively resets the stream to be beginning of the file via a 
	//FileChannel.
	//fis.getChannel().position( 0 );
	dis.close();
	return md5Content;
}     

/**
* Consume the stream and return its Base-64 encoded MD5 checksum.
*/
public static String computeContentMD5Header(InputStream inputStream) {
// Consume the stream to compute the MD5 as a side effect.
	DigestInputStream s;
	try {
	s = new DigestInputStream( inputStream,
	MessageDigest.getInstance("MD5"));
	// drain the buffer, as the digest is computed as a side-effect
	byte[] buffer = new byte[8192];
	while(s.read(buffer) > 0);
	
	byte digest[] = s.getMessageDigest().digest();
	System.out.println("message digest:" + new String(digest));
	//return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s.getMessageDigest().digest()),	"UTF-8");
	return new String(org.apache.commons.codec.binary.Base64.encodeBase64(digest));
	} catch (NoSuchAlgorithmException e) {
		throw new RuntimeException(e);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}

public static byte[] getFileContent(String fileName) {
		byte[] nextBytes = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream inStream = null;
		int nBytesRead = 0;
		try {
			inStream = new FileInputStream(fileName);
			while ((nBytesRead = inStream.read(nextBytes)) != -1) {
				baos.write(nextBytes, 0, nBytesRead);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
}


	public static void main(String args[]) throws Exception {
//		for (int i = 0; i < 100; i++) {
//			// System.out.println(getRandomString(8));
//			System.out.println(getRandomString2());
//		}
		//System.out.println(encodePassword("U1pMdKj51loRRAz8QV+UWg=="));
		System.out.println(encodeString("1"));
		//System.out.println(decodeString("ZDVhZjQyYTkyMzgyYWNjMjRiMTZlMzFmMGY4NzQyMTQ="));
		//InputStream is = new FileInputStream("XMLFileTest.txt");
		//System.out.println(computeContentMD5Header(is));
		//System.out.println(computeContentMD5HeaderValue(is));
		//is.close();
		
	}
}
