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
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.xml.bind.DatatypeConverter;
import sun.security.util.ManifestDigester;

/**
 *
 * @author smehta
 */
public class createSignJar {

    //  The alias for the signing key.     
    private String _alias;
    
    /**
     * Empty constructor
     */
    public createSignJar() {
    }
    
    /**
     * Will call other methods and functions in this program to do what is 
     * required
     * @param jarFileName
     * @param signblock
     * @return
     * @throws Exception 
     */
    public String createsignjar(String jarFileName, String signblock) throws Exception {

        //House Keeping
        File FileIn_JarFile = new File(jarFileName);
        if (!FileIn_JarFile.exists()) {
            System.out.println("Input file " + jarFileName + " not found");
            System.exit(1);
        } else if (!FileIn_JarFile.isFile()) {
            System.out.println("Input file " + jarFileName + " not a file");
            System.exit(1);
        } else if (!FileIn_JarFile.canRead()) {
            System.out.println("Input file " + jarFileName + " not readable");
            System.exit(1);
        } else if (FileIn_JarFile.length() <= 0) {
            System.out.println("Input file " + jarFileName + " size is zero");
            System.exit(1);
        }

        String signedJarFileName;
        signedJarFileName = jarFileName + "_signed.jar";

        /**
         * Using try-with-resources; which will take care of closing the 
         * jarFile in any case (success or failure)
         */
        try (JarFile jarFile = new JarFile(jarFileName)) {
            OutputStream outStream = new FileOutputStream(signedJarFileName);
            _createsignjar(jarFile, outStream, signblock);
        }

        return signedJarFileName;
    }

    /**
     * If the Manifest already exists, then make sure all the entries are valid.
     * This is required so that any new malicious additions at a later point can
     * be caught. This method will create the entries that are supposed to go
     * into the Manifest file.
     *
     * ?? Read the standard in more detail to figure out if this step is
     * required. Also using StrongAuth as the vendor name, will not give credit
     * to Oracle for this
     *
     * If the Manifest file is not present, one will be created and the entries
     * populated.
     *
     *
     * @param manifest
     * @param jarFile
     * @return A Map data structure containing the entries that should go into
     * the manifest file.
     * @throws IOException
     */
    private Map _create_Manifest_Entries(Manifest manifest, JarFile jarFile) throws IOException {
        Map entries;
        if (manifest.getEntries().size() > 0) {
            entries = _clean_And_Return_Manifest_Contents(manifest, jarFile);
        } else {
            // if there are no pre-existing entries in the manifest,
            // then we put a few default ones in
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
            attributes.putValue("Created-By", System.getProperty("java.version") + " (" + "StrongAuth Inc." + ")");
            entries = manifest.getEntries();
        }
        return entries;
    }

    /**
     * Create a signature file object out of the manifest and the message
     * digest.
     *
     *
     * @param manifest
     * @param messageDigest
     * @return new SignatureFile Object
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private createSignJar.SignatureFile _create_Signature_File(Manifest manifest, MessageDigest messageDigest) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {

        // Potential bug might be seen : Should be a  inner class.  However, this method references this._alias and returns a new object.

        // construct the signature file and the signature block for this manifest
        ManifestDigester manifestDigester = new ManifestDigester(_serialise_Manifest_File(manifest));
        return new createSignJar.SignatureFile(new MessageDigest[]{messageDigest}, manifest, manifestDigester, this._alias, true);

    }

    /**
     * Returns a Constructor object that reflects the specified constructor of
     * the class or interface represented by this Class object. The
     * parameterTypes parameter is an array of Class objects that identify the
     * constructor's formal parameter types, in declared order. If this Class
     * object represents an inner class declared in a non- context, the formal
     * parameter types include the explicit enclosing instance as the first
     * parameter.
     *
     *
     * @param Class c
     * @param argTypes
     * @return The constructor obviously
     * @throws NoSuchMethodException
     */
    private Constructor findConstructor(Class c, Class... argTypes)
            throws NoSuchMethodException {
        Constructor ct = c.getDeclaredConstructor(argTypes);
        if (ct == null) {
            throw new RuntimeException(c.getName());
        }
        ct.setAccessible(true);
        return ct;
    }

    /**
     * Returns a Method object that reflects the specified declared method of
     * the class or interface represented by this Class object. The name
     * parameter is a String that specifies the simple name of the desired
     * method, and the parameterTypes parameter is an array of Class objects
     * that identify the method's formal parameter types, in declared order. If
     * more than one method with the same parameter types is declared in a
     * class, and one of these methods has a return type that is more specific
     * than any of the others, that method is returned; otherwise one of the
     * methods is chosen arbitrarily. If the name is "<init>"or "<clinit>" a
     * NoSuchMethodException is raised.
     *
     * @param Class c
     * @param methodName
     * @param argTypes
     * @return The method obviously
     * @throws NoSuchMethodException
     */
    private Method findMethod(Class c, String methodName,
            Class... argTypes) throws NoSuchMethodException {
        Method m = c.getDeclaredMethod(methodName, argTypes);
        if (m == null) {
            throw new RuntimeException(c.getName());
        }
        m.setAccessible(true);
        return m;
    }

    /**
     * Retrieve the manifest from a jar file -- this will either load a
     * pre-existing META-INF/MANIFEST.MF, or create a new one.
     *
     * @param jarFile
     * @return Manifest entry in the Jar file
     * @throws IOException
     */
    private Manifest _get_Manifest_File(JarFile jarFile)
            throws IOException {
        JarEntry je = jarFile.getJarEntry("META-INF/MANIFEST.MF");
        if (je != null) {
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                je = (JarEntry) entries.nextElement();
                if ("META-INF/MANIFEST.MF".equalsIgnoreCase(je.getName())) {
                    break;
                } else {
                    je = null;
                }
            }
        }
        // create the manifest object
        Manifest manifest = new Manifest();
        if (je != null) {
            manifest.read(jarFile.getInputStream(je));
        }
        return manifest;

    }

    /**
     * Given a manifest file and given a jar file, make sure that the contents
     * of the manifest file is correct and return a map of all the valid entries
     * from the manifest.
     *
     *
     * @param manifest
     * @param jarFile
     * @return Map of all valid entries
     * @throws IOException
     */
    private Map _clean_And_Return_Manifest_Contents(Manifest manifest, JarFile jarFile)
            throws IOException {
        Map map = manifest.getEntries();
        Iterator elements = map.keySet().iterator();
        while (elements.hasNext()) {
            String element = (String) elements.next();
            if (jarFile.getEntry(element) == null) {
                elements.remove();
            }

        }
        return map;

    }

    /**
     * Will convert a Manifest into a ByteArray.
     *
     * @param manifest
     * @return byte array of the Manifest
     * @throws IOException
     */
    private byte[] _serialise_Manifest_File(Manifest manifest)
            throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            manifest.write(baos);
            baos.flush();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        
        if ( baos != null ) 
            return baos.toByteArray();
        else
            return null;
    }

    /**
     * The actual JAR signing method.
     *
     * @param jarFile
     * @param outputStream
     * @param signblock
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws CertificateException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws NoSuchProviderException
     */
    public void _createsignjar(JarFile jarFile, OutputStream outputStream, String signblock) 
            throws NoSuchAlgorithmException, 
                    InvalidKeyException, 
                    SignatureException, 
                    IOException, 
                    IllegalAccessException,
                    InvocationTargetException, 
                    NoSuchMethodException, 
                    CertificateException, 
                    InstantiationException, 
                    ClassNotFoundException, 
                    NoSuchProviderException 
    {
        // calculate the necessary files for the signed jAR
        // get the manifest out of the jar and verify that all the entries in the manifest are correct
        Manifest manifest = _get_Manifest_File(jarFile);
        Map entries = _create_Manifest_Entries(manifest, jarFile);

        // create the message digest and start updating the attributes in the manifest to contain the SHA256 digests
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256", "BCFIPS");
        _update_Manifest_Digest(manifest, jarFile, messageDigest, entries);

        // construct the signature file object and the signature block objects
        createSignJar.SignatureFile signatureFile = _create_Signature_File(manifest, messageDigest);

        // start writing out the signed JAR file
        // write out the manifest to the output jar stream
        String manifestFileName = "META-INF/MANIFEST.MF";
        JarOutputStream jos = new JarOutputStream(outputStream);
        JarEntry manifestFile = new JarEntry(manifestFileName);
        jos.putNextEntry(manifestFile);
        byte manifestBytes[] = _serialise_Manifest_File(manifest);
        jos.write(manifestBytes, 0, manifestBytes.length);
        jos.closeEntry();

        // write out the signature file
        String signatureFileName = signatureFile.getMetaName().toUpperCase();
        JarEntry signatureFileEntry = new JarEntry(signatureFileName);
        jos.putNextEntry(signatureFileEntry);
        signatureFile.write(jos);
        jos.closeEntry();
        
        // write out the signature block file
        String sigbn = signatureFile.getMetaName().toUpperCase();
        String signrsafile = sigbn.substring(0, (sigbn.length() - 3)) + ".RSA";
        JarEntry signatureBlockEntry = new JarEntry(signrsafile);
        jos.putNextEntry(signatureBlockEntry);
        jos.write(DatatypeConverter.parseBase64Binary(signblock));
        //block.write(jos);
        jos.closeEntry();
        
        Enumeration metaEntries = jarFile.entries();
        while (metaEntries.hasMoreElements()) {
            JarEntry metaEntry = (JarEntry) metaEntries.nextElement();
            if (metaEntry.getName().startsWith("META-INF")
                    && !(manifestFileName.equalsIgnoreCase(metaEntry.getName())
                    || signatureFileName.equalsIgnoreCase(metaEntry
                    .getName()) || signrsafile
                    .equalsIgnoreCase(metaEntry.getName()))) {
                _writeJarEntry(metaEntry, jarFile, jos);
            }
        }

        // now write out the rest of the files to the stream
        Enumeration allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            if (!entry.getName().startsWith("META-INF")) {
                _writeJarEntry(entry, jarFile, jos);
            }
        }

        // finish the stream that we have been writing to
        jos.flush();
        jos.finish();
    }

    /**
     * Helper function to update the digest. The inputStream is always closed
     * upon exit. --DO NOT CLOSE IN THE CALLER
     *
     * @param digest
     * @param inputStream
     * @return base64 encoded digest
     * @throws IOException
     */
    private String _update_Digest(MessageDigest digest, InputStream inputStream) throws IOException {
        try { // not required, but let this be here for now
            byte[] buffer = new byte[2048];
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        } finally {
            inputStream.close();
        }
        return DatatypeConverter.printBase64Binary(digest.digest());
    }

    /**
     * Update the attributes in the manifest to have the appropriate message
     * digests. I am storing the new entries into the entries Map and returning
     * it (Not computing the digests for those entries in the META-INF
     * directory)
     *
     * @param manifest
     * @param jarFile
     * @param messageDigest
     * @param entries
     * @return Map of all entries with "SHA256" and the digest appended
     * @throws IOException
     */
    private Map _update_Manifest_Digest(Manifest manifest, JarFile jarFile, MessageDigest messageDigest, Map entries)
            throws IOException {
        Enumeration jarElements = jarFile.entries();
        while (jarElements.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) jarElements.nextElement();
            if (jarEntry.getName().startsWith("META-INF")) {
            } else if (manifest.getAttributes(jarEntry.getName()) != null) {

                // Update the digest and record the base 64 version of it into the attribute list
                Attributes attributes = manifest.getAttributes(jarEntry
                        .getName());
                attributes.putValue("SHA-256-Digest", _update_Digest(messageDigest,
                        jarFile.getInputStream(jarEntry)));

            } else if (!jarEntry.isDirectory()) {
                // Store away the digest into a new Attribute because we don't already have an attribute list for this entry.
                // Not storing the attributes for directories within the JAR.
                Attributes attributes = new Attributes();
                attributes.putValue("SHA-256-Digest", _update_Digest(messageDigest,
                        jarFile.getInputStream(jarEntry)));
                entries.put(jarEntry.getName(), attributes);
            }
        }
        
        return entries;
    }

    /**
     * A function that can take entries from one jar file and write it to
     * another jar stream.
     *
     * @param jarEntry The entry in the jar file to be added to the jar output
     * stream.
     * @param jarFile The jar file that contains the jarEntry.
     * @param jarOutputStream The output stream that the jarEntry from the
     * jarFile to which to write.
     * @throws IOException
     */
    protected void _writeJarEntry(JarEntry jarEntry, JarFile jarFile, JarOutputStream jarOutputStream) throws IOException {

        jarOutputStream.putNextEntry(jarEntry);
        byte[] buffer = new byte[2048];
        int read;
        try {
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            while ((read = inputStream.read(buffer)) > 0) {
                jarOutputStream.write(buffer, 0, read);
            }
        } finally {
            jarOutputStream.closeEntry();
        }
    }

    /**
     * Inner Class that uses the SignatureFile.class from the tools.jar file in
     * the JDK distro
     */
    private class SignatureFile {

        private Object sigFile;
        private Class JDKsfClass;
        private Method getMetaNameMethod;
        private Method writeMethod;
        private final String JDK_SIGNATURE_FILE = "sun.security.tools.SignatureFile";
        private final String GETMETANAME_METHOD = "getMetaName";
        private final String WRITE_METHOD = "write";

        public SignatureFile(MessageDigest digests[], Manifest mf, ManifestDigester md, String baseName, boolean signManifest) throws ClassNotFoundException, NoSuchMethodException,
                InstantiationException, IllegalAccessException, InvocationTargetException {

            JDKsfClass = Class.forName(JDK_SIGNATURE_FILE);

            Constructor constructor = findConstructor(JDKsfClass,
                    MessageDigest[].class, Manifest.class,
                    ManifestDigester.class, String.class, Boolean.TYPE);

            sigFile = constructor.newInstance(digests, mf, md, baseName,
                    signManifest);

            getMetaNameMethod = findMethod(JDKsfClass, GETMETANAME_METHOD);
            writeMethod = findMethod(JDKsfClass, WRITE_METHOD,
                    OutputStream.class);
        }

        public Class getJDKSignatureFileClass() {
            return JDKsfClass;
        }

        public Object getJDKSignatureFile() {
            return sigFile;
        }

        public String getMetaName() throws IllegalAccessException,
                InvocationTargetException {
            return (String) getMetaNameMethod.invoke(sigFile);
        }

        public void write(OutputStream os) throws IllegalAccessException,
                InvocationTargetException {
            writeMethod.invoke(sigFile, os);
        }
    }  
}
