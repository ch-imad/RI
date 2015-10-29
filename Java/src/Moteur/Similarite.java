package Moteur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Similarite {
	
	
	
	public double getSimilarity(File file1,File file2)
	{
		// Poids du premiers fichier
		HashMap<String,Double> weights_file1 = new HashMap<String,Double>(); 
		
		// Poids du second fichier
		HashMap<String,Double> weights_file2 = new HashMap<String,Double>();
		
		// on récupère les poids du premier fichier
		
		try {
			InputStream fis = new FileInputStream(file1);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String ligne;
			
			while((ligne = br.readLine() )!= null)
			{
				String[] element = ligne.split("\t");
				String key = element[0];
				double value = Double.valueOf(element[1]);
				weights_file1.put(key, value);
				
				
			}
			br.close();
			isr.close();
			fis.close();
			// On récupère les poids du deuxième fichier
			
			fis = new FileInputStream(file2);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			
			while((ligne = br.readLine() )!= null)
			{
				String[] element = ligne.split("\t");
				String key = element[0];
				double value = Double.valueOf(element[1]);
				weights_file2.put(key, value);
				
				
			}
			br.close();
			
			
			
			// calcul de somme(wij * wik)
			double s1 = 0;
			for(String w : weights_file1.keySet())
			{
				if(weights_file2.containsKey(w))
					s1 += weights_file1.get(w) * weights_file2.get(w);
			}
			
			// calcul de somme(wij²) et somme(wik²)
			double sqwij = 0;
			double sqwik = 0;
			for(String w : weights_file1.keySet())
			{
				sqwij += Math.pow(weights_file1.get(w), 2);
				
			}
			
			for(String w : weights_file2.keySet())
			{
				
				sqwik += Math.pow(weights_file2.get(w), 2);
			}
			double similarity = (s1/(Math.sqrt(sqwij)*Math.sqrt(sqwik)));
			
			
			
			return similarity;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
		
	}
	
	
	public void getSimilarDocuments(String filename,String dirname)
	{
		File dir = new File(dirname);
		HashMap<String, Double> similarities = new HashMap<String, Double>();
		
		if(dir.isDirectory())
		{
			File[] files = dir.listFiles();
			File file = new File(filename);
			
			for(File f : files){
				similarities.put(f.getName(), getSimilarity(file, f));
				
			}
			
			Comparator<Double> valueComparator = new Comparator<Double>() {
				
				@Override
				public int compare(Double o1, Double o2) {
					// TODO Auto-generated method stub
					return o2.compareTo(o1);
				}
			};
			
			// Tri du dictionaire de similarité en ordre décroissant
			
			MapValueComparator mapc = new MapValueComparator(similarities, valueComparator);			
			
			Map<String,Double>s = new TreeMap<>(mapc);
			s.putAll(similarities);
			
			// enregistrement des résultat dans un fichier
			File resultDir = new File("similarity");
			File filedir = new File(resultDir.getName() + "/" + file.getName().replace(".poids", "") + "_Results");
			
			if(!resultDir.exists())
			{
				resultDir.mkdir();
			}
			
			if(!filedir.exists())
			{
				filedir.mkdir();
			}
			
			try {
				
				FileWriter fw = new FileWriter(new File(resultDir.getName() + "/" + filedir.getName() + "/" + file.getName().replaceAll("poids", "similarity")));
				for(String w : s.keySet())
				{
					fw.write(w + "\t" + s.get(w) + "\t\n");
				}
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}


	
	
	
	

	

}
