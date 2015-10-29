package Moteur;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import tools.*;

/*
 * 
 * Le role de cette classe est de :
 * 
 * Calculer les df
 * Calculet les tfidf
 * Construire le fichier index
 * Construire le fichier index inversé
 * 
 */

public class Indexer {

	
	
	
	public HashMap<String, Integer> getDocumentFrequency(File[] files, Normalizer normalizer) throws IOException {
		
		HashMap<String, Integer> hits = new HashMap<String, Integer>();
		
		//Pour chaqie fichier de la liste :
		for (File f : files)
		{
			// On récupère la liste de mot dans le fichier;
			ArrayList<String> words = normalizer.normalize(f);
			
			// On supprime les doublons;
			Set<String> ws = new HashSet<>(words);
			words = new ArrayList<String>(ws);
			
			// Pour chaque mot on compte le nombre de fois qu'il apparait dans le fichier:
			for(String w : words)
			{
				w = w.toLowerCase();
				if(!hits.containsKey(w))
				{
					hits.put(w, 0);
				}
				
				hits.put(w, hits.get(w) + 1);
			}
			
			
		}
		
		
		// Si le fichier poids n'existe pas on le crée
		if(!Globals.getDfDir().exists())
		{
			Globals.getDfDir().mkdir();
			Globals.getDfFile().createNewFile();
			
			FileWriter fw = new FileWriter(Globals.getDfFile());
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			
			
			for(String key : hits.keySet())
			{
				pw.println(key + "\t" + hits.get(key));
			}
		}
		
		
		return hits;
	}
	
	
	
	// Calcul des TfIdf des mots d'un fichier
	public HashMap<String, Double> getTfIdf(File file, HashMap<String, Integer> dfs, int documentNumber, Normalizer normalizer) throws IOException {
		
		HashMap<String, Double> tfIdfs = new HashMap<String, Double>();
	
			// On calcul la fréquence des mots dans le fichier
			HashMap<String,Integer> tf = new HashMap<String,Integer>();
			ArrayList<String> words = normalizer.normalize(file);
			
			for (String w : words){
				w = w.toLowerCase();
				if(!tf.containsKey(w))
				{
					tf.put(w,0);
				}
				
				tf.put(w, tf.get(w) + 1);
			}
			
			
			// On calcul les tfIdf de chaque mot
			for(String k:tf.keySet())
			{
				double v = (double)tf.get(k)*Math.log((double)documentNumber/(double)dfs.get(k));
				tfIdfs.put(k, v);
			}
		
		return tfIdfs;
	}
	
	
	private  void getWeightFiles(File[] files, File outDir, Normalizer normalizer) throws IOException {
		
		
		// On calcul les dfs
		HashMap<String, Integer> dfs = getDocumentFrequency(files, normalizer);
		
		// Nombre de documents
		int documentNumber = files.length;
		
		//Pour chaque fichier
		for (File file : files) {
			// On calcul les tfIdf
			HashMap<String, Double> tfIdfs = getTfIdf(file, dfs, documentNumber, normalizer);
			
			// On trie les tfIdfs
			
			// On crée un comparateur de valeurs
			Comparator<Double> valueComparator = new Comparator<Double>() {
				
				@Override
				public int compare(Double o1, Double o2) {
					// TODO Auto-generated method stub
					return o2.compareTo(o1);
				}
			};
			
			// Tri du dictionaire de tfIdf en ordre décroissant
			
			// On crée une nouvelle e map en integrant le comparateur de valeurs
			MapValueComparator mapc = new MapValueComparator(tfIdfs, valueComparator);			
			
			//On crée la map finale triée
			Map<String,Double> tfidfsTriées = new TreeMap<>(mapc);
			tfidfsTriées.putAll(tfIdfs);
			
			// On récupére les mots
			TreeSet<String> words = new TreeSet<String>(tfidfsTriées.keySet());
			
			// on sauvegarde le mot et sa tfIdf dans le fichier qui correspond
			try {
				
				FileWriter fw = new FileWriter (outDir + "" + file.getName().replaceAll(".txt$", ".poids"));
				BufferedWriter bw = new BufferedWriter (fw);
				PrintWriter out = new PrintWriter (bw);
				
				// Ecriture des mots et de leurs poids
				for (String word : words) {
					out.println(word + "\t" + tfidfsTriées.get(word)); 
				}
				out.close();
			}
			catch (Exception e){
				System.out.println(e.toString());
			}		
		}
			
		
	}
	
	
	

}
