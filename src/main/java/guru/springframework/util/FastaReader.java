package guru.springframework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Component;

/**
 *
 * @author rohanrampuria
 */
@Component
public class FastaReader {

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        String filename="C:/Users/user/Desktop/upload files/uniprot-yeast.fasta";
        FastaReader fr = new FastaReader();
        System.out.println("Protein Num "+fr.getproteinCount(filename));
    }*/
    public int getproteinCount (String filename){
        File file = new File(filename);
        int proteinCount =0;
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));
            String eachLine = null;
            while((eachLine=br.readLine()) != null){
                if(eachLine.startsWith(">")){
                    proteinCount++;
                }
            }  
        }catch (Exception e){
            e.printStackTrace();
        }finally{
        	try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return proteinCount;
    }
}
