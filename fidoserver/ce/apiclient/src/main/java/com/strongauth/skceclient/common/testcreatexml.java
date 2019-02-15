/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2019 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skceclient/src/main/java/com/strongauth/skceclient/common/testcreatexml.java $
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 */
package com.strongauth.skceclient.common;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class testcreatexml {

    public String xmldoc() throws TransformerConfigurationException, TransformerException, UnsupportedEncodingException, ParserConfigurationException {
        testbean tb1 = new testbean();
        tb1.setTid(1);
        tb1.setName("test");
        tb1.setEmail("test@strongauth.com");
        tb1.setStatus("Active");
        tb1.setCreatedate(new Date());
        /**
         * Create XMLEncryption document
         */
        // Create the document factory, builder and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xencdoc = builder.newDocument();

        // Create root element - EncryptedData
        String documentid = "ID1111";//.concat(Long.toString(new Date().getTime()));
        Element root = (Element) xencdoc.createElement("Data");
        root.setAttribute("id", documentid);
        root.setIdAttribute("id", Boolean.TRUE);
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation", "http://www.w3.org/2001/04/xmlenc# http://www.w3.org/TR/xmlenc-core/xenc-schema.xsd");
        root.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        root.setAttribute("xmlns:cda", "http://cda.strongauth.com/CDA201401");
        root.setAttribute("xmlns:skce", "http://cryptoengine.strongauth.com/SKCE201401");
        org.w3c.dom.Node encdatanode = (org.w3c.dom.Node) xencdoc.appendChild(root);

        // Create tid
        Element domid = (Element) xencdoc.createElement("tid");
        org.w3c.dom.Node didnode = (org.w3c.dom.Node) encdatanode.appendChild(domid);
        org.w3c.dom.Text didtext = xencdoc.createTextNode(String.valueOf(tb1.getTid()));
        didnode.appendChild(didtext);

        // Create name
        Element nameid = (Element) xencdoc.createElement("name");
        org.w3c.dom.Node namenode = (org.w3c.dom.Node) encdatanode.appendChild(nameid);
        org.w3c.dom.Text nametext = xencdoc.createTextNode(tb1.getName());
        namenode.appendChild(nametext);

        // Create name
        Element emailid = (Element) xencdoc.createElement("email");
        org.w3c.dom.Node emailnode = (org.w3c.dom.Node) encdatanode.appendChild(emailid);
        org.w3c.dom.Text emailtext = xencdoc.createTextNode(tb1.getEmail());
        emailnode.appendChild(emailtext);
        
        // Create name
//        Element dateid = (Element) xencdoc.createElement("createdate");
//        org.w3c.dom.Node datenode = (org.w3c.dom.Node) encdatanode.appendChild(dateid);
//        org.w3c.dom.Text datetext = xencdoc.createTextNode(tb1.getCreatedate().toString());
//        datenode.appendChild(datetext);
        
        // Create name
        Element statusid = (Element) xencdoc.createElement("status");
        org.w3c.dom.Node statusnode = (org.w3c.dom.Node) encdatanode.appendChild(statusid);
        org.w3c.dom.Text statustext = xencdoc.createTextNode(tb1.getStatus());
        statusnode.appendChild(statustext);
        
        // Stream out XML
        DOMSource source = new DOMSource(xencdoc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.transform(source, result);

        // Sign the XML and check if we succeeded          
        String xencxml = baos.toString("UTF-8");
        System.out.println(xencxml);
        return xencxml;
    }
}
