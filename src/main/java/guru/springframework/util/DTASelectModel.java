/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guru.springframework.util;

/**
 *
 * @author rohanrampuria
 */
public class DTASelectModel {
    
    private int proteinId;
    private int peptideId;
    private double proteinFDR;
    private double peptideFDR;
    private double spectrumFDR;
    private String parameters;

    public int getProteinId() {
        return proteinId;
    }

    public void setProteinId(int proteinId) {
        this.proteinId = proteinId;
    }

    public int getPeptideId() {
        return peptideId;
    }

    public void setPeptideId(int peptideId) {
        this.peptideId = peptideId;
    }

    public double getProteinFDR() {
        return proteinFDR;
    }

    public void setProteinFDR(double proteinFDR) {
        this.proteinFDR = proteinFDR;
    }

    public double getPeptideFDR() {
        return peptideFDR;
    }

    public void setPeptideFDR(double peptideFDR) {
        this.peptideFDR = peptideFDR;
    }

    public double getSpectrumFDR() {
        return spectrumFDR;
    }

    public void setSpectrumFDR(double spectrumFDR) {
        this.spectrumFDR = spectrumFDR;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    
}
