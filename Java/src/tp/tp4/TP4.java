package tp.tp4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import java.lang.Math;
//import org.apache.commons.io.FileUtils;

import tools.FrenchStemmer;
import tools.FrenchTokenizer;
import tools.Normalizer;
import tp.tp1.TP1;
import tp.tp2.TP2;

public class TP4 {
	/**
	 * Le répertoire du corpus
	 */
	// TODO CHANGER LE CHEMIN
	protected static String DIRNAME = "/home/tp-home004/vestrad/Documents/REI/lemonde-utf8/";
	/**
	 * Le fichier contenant les mots vides
	 */
	private static String STOPWORDS_FILENAME = "/home/tp-home004/vestrad/Documents/REI/StopWords/frenchST.txt";

	/**
	 * 
	 * @param invertedFile1
	 * @param invertedFile2
	 * @param mergedInvertedFile
	 * @throws IOException
	 */
	public static void mergeInvertedFiles(File invertedFile1, File invertedFile2,
			File mergedInvertedFile) throws IOException{
		
		// out <- Fichier inverse de sortie
		FileWriter fw = new FileWriter (mergedInvertedFile);
		BufferedWriter bw = new BufferedWriter (fw);
		PrintWriter out = new PrintWriter (bw);
		
		// b1 <- Fichier inverse 1
		FileReader f1 = new FileReader(invertedFile1);
		BufferedReader b1 = new BufferedReader(f1);
		// b2 <- Fichier inverse 2
		FileReader f2 = new FileReader(invertedFile2);
		BufferedReader b2 = new BufferedReader(f2);
		
		String line1 = b1.readLine();
		String line2 = b2.readLine();
		
		while ((line1 != null)&(line2 != null)){
			String[] line1_splited = line1.split("\t");
			String[] line2_splited = line2.split("\t");
			if (line1_splited.length != 3){
				// TODO : Problème de format !
				throw new IOException("Wrong format in file :"+invertedFile1.toString());
			}
			if (line2_splited.length != 3){
				// TODO : Problème de format !
				throw new IOException("Wrong format in file :"+invertedFile2.toString());
			}
			String word1 = line1_splited[0];
			String word2 = line2_splited[0];
			
			int df1 = Integer.parseInt(line1_splited[1]);
			int df2 = Integer.parseInt(line2_splited[1]);
			int df = 0;
			
			//ArrayList<String> doc_list1 = new ArrayList<String>(Array.asList(line1_splited[2].split(",")));
			String[] doc_list1 = line1_splited[2].split(",");
			String[] doc_list2 = line2_splited[2].split(",");
			
			if (word1.equals(word2)){
			//if (word1.compareTo(word2)==0){
						Set<String> file_set = new TreeSet<String>();
				file_set.addAll(Arrays.asList(doc_list1));
				file_set.addAll(Arrays.asList(doc_list2));
				StringBuilder rString = new StringBuilder();
				for (String doc : file_set){
					rString.append(",").append(doc);
				}
				df = df1+df2;
				out.println(word1 + "\t" + df + "\t" +rString.substring(1));
				//System.out.println(word1 + "\t" + df + "\t" +rString.substring(1));
				line1 = b1.readLine();
				line2 = b2.readLine();
			}
			if (word1.compareTo(word2)<0){
				out.println(line1);
				//System.out.println(line1);
				line1 = b1.readLine();
			}
			if (word1.compareTo(word2)>0){
				out.println(line2);
				//System.out.println(line2);
				line2 = b2.readLine();
			}
			
		}
		while(line1 != null){
			out.println(line1);
			//System.out.println(line1);
			line1 = b1.readLine();
		}
		while(line2 != null){
			out.println(line2);
			//System.out.println(line2);
			line2 = b2.readLine();
		}
		b1.close();
		b2.close();
		out.close();
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String indexes_dir = "/home/tp-home004/vestrad/Documents/REI/Indexes/";
		File index1 = new File(indexes_dir+"index1.ind"); 
		File index2 = new File(indexes_dir+"index2.ind");
		//File merge = new File(indexes_dir+"Indexes/indexMerge.ind");
		
		try{
			mergeInvertedFiles(index1, index2, new File(new File(indexes_dir), "indexMerge.ind"));	
		}catch (IOException e) {
			e.printStackTrace();
		}		
		
	}

}
