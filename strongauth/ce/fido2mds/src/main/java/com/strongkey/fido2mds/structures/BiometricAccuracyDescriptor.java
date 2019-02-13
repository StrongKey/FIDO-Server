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

public class BiometricAccuracyDescriptor {
    private Double FAR;
    private Double FRR;
    private Double EER;
    private Double FAAR;
    private Integer maxRegerenceDataSets;
    private Integer maxRetries;
    private Integer blockSlowdown;

    public Double getFAR() {
        return FAR;
    }

    public void setFAR(Double FAR) {
        this.FAR = FAR;
    }

    public Double getFRR() {
        return FRR;
    }

    public void setFRR(Double FRR) {
        this.FRR = FRR;
    }

    public Double getEER() {
        return EER;
    }

    public void setEER(Double EER) {
        this.EER = EER;
    }

    public Double getFAAR() {
        return FAAR;
    }

    public void setFAAR(Double FAAR) {
        this.FAAR = FAAR;
    }

    public Integer getMaxRegerenceDataSets() {
        return maxRegerenceDataSets;
    }

    public void setMaxRegerenceDataSets(Integer maxRegerenceDataSets) {
        this.maxRegerenceDataSets = maxRegerenceDataSets;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getBlockSlowdown() {
        return blockSlowdown;
    }

    public void setBlockSlowdown(Integer blockSlowdown) {
        this.blockSlowdown = blockSlowdown;
    }
}
