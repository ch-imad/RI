package Moteur;

import java.io.File;

public class Globals {

	protected static File dfDir = new File("poids/");
	protected static File dfFile = new File(dfDir,"words.df");
	
	
	public static File getDfDir()
	{
		return dfDir;
	}
	
	public static File getDfFile()
	{
		return dfFile;
	}

}
