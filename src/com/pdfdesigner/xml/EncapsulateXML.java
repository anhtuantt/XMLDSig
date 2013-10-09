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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;

import eu.medsea.mimeutil.MimeUtil;

/**
 * 
 * @author nnhan script 1 : base64 input file
 */

public class EncapsulateXML {

	public EncapsulateXML() {

	}

	public void runScriptOne(String xmlFileName, String base64File, String outputFile)
			throws Exception {
		byte result[] = StringUtil.getFileContent(base64File);
		String payLoadContent = StringUtil.encodeString(result);
		FileInputStream url = new FileInputStream(xmlFileName);
		SAXBuilder builder = new SAXBuilder(); // build a JDOM tree from a SAX
												// stream provided by tagsoup
		Document doc = builder.build(url);

		XPath xPath = XPath.newInstance("/TMFFile/PayloadFile");
		Element elem = (Element) xPath.selectSingleNode(doc);

		System.out.println(elem.getAttributeValue("MetadataAccess"));
		elem.setText(payLoadContent);
		
	// Filetype and Filename
		if(outputFile != null  && !outputFile.trim().equals(""))
			elem.setAttribute("Filename",outputFile);
		else
			elem.setAttribute("Filename",base64File);
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        Collection<?> mimeTypes = MimeUtil.getMimeTypes(new File (base64File));
        String mimeType = mimeTypes.iterator().next().toString();
        System.out.println(mimeType);
		elem.setAttribute("Filetype",mimeType);
		
		// Output
		FileOutputStream fos = new FileOutputStream(
				"XMLPOC_Encapsulated_v01.xml");
		XMLOutputter outputter = new XMLOutputter();
		try {
			outputter.output(doc, fos);
		} catch (IOException e) {
			System.err.println(e);
		}
		url.close();

		
		
       // }
        //  output : application/msword
        
        /*Magic parser = new Magic() ;
     // getMagicMatch accepts Files or byte[],
     // which is nice if you want to test streams
     System.out.println(match.getMimeType()) ;*/
     
       
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		 if(args.length <2) return;
		 String xmlFileName = args[0];
		 String base64File =args[1];
		 
		 String outputFile = null;
		 if(args.length >= 3)
			 outputFile = args[2];
			 
		new EncapsulateXML().runScriptOne(xmlFileName, base64File, outputFile);
	}

}
