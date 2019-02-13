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
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date: 
 * $Revision:
 * $Author: mishimoto $
 * $URL: 
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
 *
 *
 */

package com.strongkey.fido2mds.structures;

import java.math.BigInteger;

public class DisplayPNGCharacteristicsDescriptor {
    private BigInteger width;
    private BigInteger height;
    private Short bitDepth;
    private Short colorType;
    private Short compression;
    private Short filter;
    private Short interlace;
    private RGBPaletteEntry plte;

    public BigInteger getWidth() {
        return width;
    }

    public void setWidth(BigInteger width) {
        this.width = width;
    }

    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public Short getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(Short bitDepth) {
        this.bitDepth = bitDepth;
    }

    public Short getColorType() {
        return colorType;
    }

    public void setColorType(Short colorType) {
        this.colorType = colorType;
    }

    public Short getCompression() {
        return compression;
    }

    public void setCompression(Short compression) {
        this.compression = compression;
    }

    public Short getFilter() {
        return filter;
    }

    public void setFilter(Short filter) {
        this.filter = filter;
    }

    public Short getInterlace() {
        return interlace;
    }

    public void setInterlace(Short interlace) {
        this.interlace = interlace;
    }

    public RGBPaletteEntry getPlte() {
        return plte;
    }

    public void setPlte(RGBPaletteEntry plte) {
        this.plte = plte;
    }
}
