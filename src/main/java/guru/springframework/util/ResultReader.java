/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guru.springframework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Component;

import guru.springframework.domain.DtaFileDetails;

/**
 *
 * @author rohanrampuria
 */
@Component
public class ResultReader {
    
    public DtaFileDetails reader(File file){
    	DtaFileDetails dtaFileDetails = new DtaFileDetails();
        BufferedReader br = null;
        try{
        	br = new BufferedReader(new FileReader(file));
            String eachLine = null;
            while((eachLine=br.readLine()) != null){
                if(eachLine.startsWith(" -p")){
                	dtaFileDetails.setFileParameters(eachLine);
                }
                else if(eachLine.startsWith("Forward matches")){
                    String [] words = eachLine.split("\t");
                    dtaFileDetails.setProteinIds(Long.parseLong(words[1]));
                    dtaFileDetails.setPeptideIds(Long.parseLong(words[2]));
                }
                else if(eachLine.startsWith("Forward FDR")){
                    String [] words = eachLine.split("\t");
                    dtaFileDetails.setProteinFdrs(Double.parseDouble(words[1]));
                    dtaFileDetails.setPeptiedFdrs(Double.parseDouble(words[2]));
                    dtaFileDetails.setSpectrumFdrs(Double.parseDouble(words[3]));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	try{
        		if(br != null){
        			br.close();	
        		}
        		
        	}catch(IOException io){
        		io.printStackTrace();
        	}
        	
        }
       return dtaFileDetails;
    } 
}
