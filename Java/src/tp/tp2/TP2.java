package tp.tp2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

/**
 * TP 2
 * @author xtannier
 *
 */
public class TP2 {
	
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
	 * exo 2.1 : Calcule le df, c'est-à-dire le nombre de documents
	 * pour chaque mot apparaissant dans la collection. Le mot
	 * "à" doit ainsi apparaître dans 88 documents, le mot
	 * "ministère" dans 4 documents.
	 */
	public static HashMap<String, Integer> getDocumentFrequency(File dir, Normalizer normalizer) throws IOException {
		HashMap<String, Integer> hits = new HashMap<String, Integer>();
		// TODO
		if (dir.isDirectory()) {
			// Liste des fichiers du répertoire
			// ajouter un filtre (FileNameFilter) sur les noms
			// des fichiers si nécessaire
			File[] files = dir.listFiles();
			
			// Parcours des fichiers et remplissage de la table			
			Integer number;
			for (File file : files) {
				//System.err.println("Analyse du fichier " + file.getAbsolutePath());
				//Get the words from the file
				ArrayList<String> words = normalizer.normalize(file);
				// Kill doublons
				Set<String> set = new HashSet();
		        set.addAll(words);
		        words.clear();
		        words.addAll(set);
		        
		        // For each word in file add it to the DocumentFrequency
				for (String word: words){
					if (hits.containsKey(word)) {
						hits.put(word, hits.get(word)+1);
					}
					else{
						hits.put(word, 1);
					}
				}
			}
		}
		
		return hits;
	}
	

	/**
	 * exo 2.4 : Calcule le tf.idf des mots d'un fichier en fonction
	 * des df déjà calculés, du nombre de documents et de
	 * la méthode de normalisation.
	 */
	public static HashMap<String, Double> getTfIdf(File file, HashMap<String, Integer> dfs, int documentNumber, Normalizer normalizer) throws IOException {
		HashMap<String, Integer> hits = new HashMap<String, Integer>();
		// Appel de la méthode de normalisation
		ArrayList<String> words = normalizer.normalize(file);
		Integer number;

		// Pour chaque mot de la liste, on remplit un dictionnaire
		// du nombre d'occurrences pour ce mot
		for (String word : words) {
			word = word.toLowerCase();
			// on récupère le nombre d'occurrences pour ce mot
			number = hits.get(word);
			// Si ce mot n'était pas encore présent dans le dictionnaire,
			// on l'ajoute (nombre d'occurrences = 1)
			if (number == null) {
				hits.put(word, 1);
			}
			// Sinon, on incrémente le nombre d'occurrence
			else {
				hits.put(word, ++number);
			}
		}
		
		Integer tf;
		Double tfIdf;
		String word;
		HashMap<String, Double> tfIdfs = new HashMap<String, Double>();

		// Calcul des tf.idf
		for (Map.Entry<String, Integer> hit : hits.entrySet()) {
			tf = hit.getValue();
			word = hit.getKey();
			tfIdf = (double)tf * Math.log((double)documentNumber / (double)dfs.get(word));
			tfIdfs.put(word, tfIdf);
		}
		return tfIdfs;
	}
	
	/**
	 * exo 2.5 : Crée, pour chaque fichier d'un répertoire, un nouveau
	 * fichier contenant les poids de chaque mot. Ce fichier prendra
	 * la forme de deux colonnes (mot et poids) séparées par une tabulation.
	 * Les mots devront être placés par ordre alphabétique.
	 * Les nouveaux fichiers auront pour extension .poids
	 * et seront écrits dans le répertoire outDirName.
	 */
	private static void getWeightFiles(File inDir, File outDir, Normalizer normalizer) throws IOException {
		// calcul des dfs
		HashMap<String, Integer> dfs = getDocumentFrequency(inDir, normalizer);
		// Nombre de documents
		File[] files = inDir.listFiles();
		int documentNumber = files.length;
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		
		// TfIdfs 
		for (File file : files) {
			HashMap<String, Double> tfIdfs = getTfIdf(file, dfs, documentNumber, normalizer);
			TreeSet<String> words = new TreeSet<String>(tfIdfs.keySet());
			// on écrit dans un fichier
			try {
				FileWriter fw = new FileWriter (new File(outDir, file.getName().replaceAll(".txt$", ".poids")));
				BufferedWriter bw = new BufferedWriter (fw);
				PrintWriter out = new PrintWriter (bw);
				// Ecriture des mots
				for (String word : words) {
					out.println(word + "\t" + tfIdfs.get(word)); 
				}
				out.close();
			}
			catch (Exception e){
				System.out.println(e.toString());
			}		
		}
	}

	// =============================== TP3 ====================================
	// getInvertedFile
	// saveInvertedFile
	// getInvertedFileWithWeights

	/**
	 * 
	 * @param dir
	 * @param normalizer
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, TreeSet<String>> getInvertedFile(File dir, Normalizer normalizer) throws IOException{
		TreeMap<String, TreeSet<String>> inverted = new TreeMap<String, TreeSet<String>>();
		
		if (dir.isDirectory()) {
			// Liste des fichiers du répertoire
			// ajouter un filtre (FileNameFilter) sur les noms
			// des fichiers si nécessaire
			File[] files = dir.listFiles();
			
			// Parcours des fichiers et remplissage de la table			
			for (File file : files) {
				//System.err.println("Analyse du fichier " + file.getAbsolutePath());
				//Get the words from the file
				ArrayList<String> words = normalizer.normalize(file);
				// Kill doublons
				Set<String> set = new HashSet();
		        set.addAll(words);
		        words.clear();
		        words.addAll(set);
		        // For each word in file add it to the DocumentFrequency
				for (String word: words){
					TreeSet<String> file_set = inverted.get(word);
					// Si ce mot n'était pas encore présent dans le dictionnaire,
					// on l'ajoute (nombre d'occurrences = 1)
					if (file_set == null) {
						file_set = new TreeSet<String>();
						file_set.add(file.getName());
						inverted.put(word, file_set);
					}
					// Sinon, on incrémente le nombre d'occurrence
					else {
						file_set.add(file.getName());
						inverted.put(word, file_set);
					}
				}
			}
		}
		return inverted;
	}
	
	/**
	 * 
	 * @param invertedFile
	 * @param outFile
	 * @throws IOException
	 */
	public static void saveInvertedFile(TreeMap<String, TreeSet<String>> invertedFile, File outFile) throws IOException{
		try {
			FileWriter fw = new FileWriter (outFile);
			BufferedWriter bw = new BufferedWriter (fw);
			PrintWriter out = new PrintWriter (bw);
			// Ecriture des mots
			for (Map.Entry<String, TreeSet<String>> hit : invertedFile.entrySet() ){
				String word = hit.getKey();
				TreeSet<String> file_set = hit.getValue();
				StringBuilder rString = new StringBuilder();
				for (String doc : file_set){
					rString.append(",").append(doc);
				}
				
				out.println(word + "\t" + file_set.size() + "\t" +rString.substring(1)); 
			}
			out.close();
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
	}
	
	/**
	 * 
	 * @param dir
	 * @param normalizer
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, TreeMap<String, Integer>> getInvertedFileWithWeights(File dir, Normalizer normalizer) throws IOException{
		TreeMap<String, TreeMap<String, Integer>> invertedWeightedSet = new TreeMap<String, TreeMap<String, Integer>>();
				
		if (dir.isDirectory()) {
			// Liste des fichiers du répertoire
			// ajouter un filtre (FileNameFilter) sur les noms
			// des fichiers si nécessaire
			File[] files = dir.listFiles();
			
			// Parcours des fichiers et remplissage de la table			
			for (File file : files) {
				//System.err.println("Analyse du fichier " + file.getAbsolutePath());
				//Get the term frequencies from this file
				HashMap<String, Integer> tfs = TP1.getTermFrequencies(file, normalizer);
				
				// For each word in file add the (file:freq) to the InvertedWeightedFile
				for (Map.Entry<String, Integer> hit: tfs.entrySet()){
					String word = hit.getKey();
					int freq = hit.getValue();
					
					TreeMap<String, Integer> file_set = invertedWeightedSet.get(word);
					// Si ce mot n'était pas encore présent dans le dictionnaire,
					// on le créé et on y ajoute qon premier (ficher:freq)
					if (file_set == null) {
						file_set = new TreeMap<String, Integer>();
						file_set.put(file.getName(), freq);
						invertedWeightedSet.put(word, file_set);
					}
					// Sinon, on ajoute simplement un nouveau (fichier:freq)
					else {
						file_set.put(file.getName(), freq);
						invertedWeightedSet.put(word, file_set);
					}
				}	
			}
		}
		return invertedWeightedSet;
	}
	
	
	/**
	 * Main, appels de toutes les méthodes des exercices du TP2 et du TP3.
	 */
	public static void main(String[] args) {
		try {
			String outDirName = "/home/tp-home004/vestrad/Documents/REI/Output";
			Normalizer stemmerAllWords = new FrenchStemmer();
			Normalizer stemmerNoStopWords = new FrenchStemmer(new File(STOPWORDS_FILENAME));
			Normalizer tokenizerAllWords = new FrenchTokenizer();
			Normalizer tokenizerNoStopWords = new FrenchTokenizer(new File(STOPWORDS_FILENAME));
			Normalizer[] normalizers = {stemmerAllWords, stemmerNoStopWords, 
					tokenizerAllWords, tokenizerNoStopWords};
			for (Normalizer normalizer : normalizers) {
				String name = normalizer.getClass().getName();
				if (!normalizer.getStopWords().isEmpty()) {
					name += "_noSW";
				}
				System.out.println("Normalisation avec " + name);
				System.out.println(getDocumentFrequency(new File(DIRNAME), normalizer).size());
				System.out.println("GetWeightFiles avec " + name);
				getWeightFiles(new File(DIRNAME), new File(new File(outDirName), name), normalizer);
				System.out.println("SaveInvertedFile avec " + name);
				saveInvertedFile(getInvertedFile(new File(DIRNAME), normalizer), new File(new File(outDirName), "Inverted"+name));
				
			}
			System.out.println("Get Inverted File Test :");
			for (Map.Entry<String, TreeSet<String>> hit : getInvertedFile(new File(DIRNAME), stemmerAllWords).entrySet() ){
				if (hit.getKey().equals("centralis")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				if (hit.getKey().equals("cepend")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				if (hit.getKey().equals("certain")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				
			}
			System.out.println("Get Inverted Weighted File Test :");
			for (Map.Entry<String, TreeMap<String, Integer>> hit : getInvertedFileWithWeights(new File(DIRNAME), stemmerAllWords).entrySet() ){
				if (hit.getKey().equals("centralis")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				if (hit.getKey().equals("cepend")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				if (hit.getKey().equals("certain")){
					System.out.println(hit.getKey()+" = "+hit.getValue());
				}
				
			}
						
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
