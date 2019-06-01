import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFAnonymous {
	
	PDDocument document;
	Map<String,String> replacers;
	String output=null;
	Options options;
	String tokenString;
	boolean setts=false;
	String tokenFile;
	String outputFile;
	boolean setof=false;
	String inputPDF;
	boolean setstrict=false;

	
	/*****************************
	 * GO CONVERT
	 * @param args
	 *****************************/
	public PDFAnonymous(String[] args) 
	{
		parseCmdLine(args);
		
		 	//ouverture du fichier de remplacement
		 	try {
					if (setts==false) 
					{
						open_File_replacement(tokenFile);
					}
					else
					{
						open_String_replacement(tokenString);
					}
		 		} catch (IOException e1) 
		 		{
		 			System.err.println(e1.getMessage());
		 		}
		 
		 	//ouverture du PDF
	        try {document = PDDocument.load(new File(inputPDF));} catch(IOException io){System.err.println(io.getMessage());System.exit(1);}
	        try
	        {
	            AccessPermission ap = document.getCurrentAccessPermission();
	            if (!ap.canExtractContent())
	            {
	                throw new IOException("Ce fichier est crypté, impossible de l'extraire...");
	            }

	            PDFTextStripper stripper = new PDFTextStripper();

	            stripper.setSortByPosition(true);
	
	                //remplacer tous les tokens...
	            	output=replacers(stripper.getText(document)).trim();
	            	if (setof==false) 
	            	{
	            	System.out.println(output);
	            	}
	            	else
	            	{
	                ecrire_fichier(outputFile,output);
	            	}
	                
	        } catch (Exception e) {System.err.println(e.getMessage());System.exit(80);}
	        
	       System.out.println("Extraction Terminée : success"); 
	}
	
	
	/*************************************
	 * Ecrire l'extraction vers un fichier
	 * @param outputFile2
	 * @param output2
	 ************************************/
	private void ecrire_fichier(String outputFile2, String output2) {
		try 
		{
			BufferedWriter bw=new BufferedWriter(new FileWriter(outputFile2));
			bw.write(output2);
			bw.flush();
			bw.close();
		} catch (IOException e) 
		{
			System.err.println(e.getMessage());System.exit(4);
		}
		}


	/*******************************************
	 * parse la chaine des tokens de la ligne de cmd
	 * @param tokenString2
	 *******************************************/
	private void open_String_replacement(String tokenString2) 
	{
		try {
		replacers=new HashMap<>();
		String[] tokens=tokenString2.split("#");
		for (int i = 0; i < tokens.length; i++) 
		{			
			replacers.put(tokens[i].substring(0, tokens[i].indexOf(":")),tokens[i].substring(tokens[i].indexOf(":")+1));
		}
		} catch (Exception e) {e.printStackTrace();System.err.println(e.getMessage());System.exit(2);}
	}

	
	/*****************************************
	 * Parser la ligne de commande des options
	 * @param args
	 *****************************************/
	private void parseCmdLine(String[] args) {
		options=new Options();
		options.addOption("help", false, "Aide sur la ligne de commande");
		options.addOption("strict", false, "Respect stricte de la casse des tokens a remplacer.");
		options.addOption("ts", true, "Utilisation d'une chaine de tokens pour le remplacement  : au format \"t1:t2#t3:t4#tn:tm\", cette chaine doit etre delimite par des \"\" et est prioritaire sur la prise en charge du fichier de tokens...");
		options.addOption("of", true, "Chemin complet du fichier de sortie au format ASCII UTF-8...");
		options.addOption("pdf", true, "Chemin complet du fichier PDF en entree...");
		options.addOption("tf", true, "Chemin complet du fichier des tokens de remplacement (voir format attendu)...");
		
		try
		{
		CommandLineParser parser = new DefaultParser();
		//controler les tokens
			CommandLine cmd = parser.parse( options, args);
			//Afficher l'aide
			if(cmd.hasOption("help")) {
			    usage();
			    usageHelp();
			    System.exit(-1);
			}
			//Option Stricte
			if(cmd.hasOption("strict")) 
			{
				System.out.println("Strict mode replacement actived...");
			    setstrict=true;
			}
			//option PDF obligatoire
			if(cmd.hasOption("pdf")) {
			    //verifier que c'est bien un PDF en extension
				inputPDF = cmd.getOptionValue("pdf");
				System.out.println("File to extract : " + inputPDF);
			}
			else 
			{
			    System.err.println("Option -pdf obligatoire...");
			    usage();
			    System.exit(10);
			}
			//option of si pas mise sortie vers console
			if(cmd.hasOption("of")) {
			    //verifier que c'est bien un PDF en extension
				outputFile = cmd.getOptionValue("of");
				setof=true;
				System.out.println("Extract to text file : " + outputFile);
			}
			else 
			{
				setof=false;
				System.out.println("Extract to console: if you want to extract to a file, use -tf option...");
			}
			//option ts si existe alors tf pas pris en compte 
			if(cmd.hasOption("ts")) {
			    //verifier que c'est bien un PDF en extension
				tokenString = cmd.getOptionValue("ts");
				setts=true;
				System.out.println("Use Inline Token String : " + tokenString);
			}
			else 
			{
				if(cmd.hasOption("tf")) 
				{
				    //verifier que c'est bien un PDF en extension
					tokenFile = cmd.getOptionValue("tf");
					System.out.println("Use file Tokens : " + tokenFile);
				}
				else 
				{
					 System.err.println("Option -ts ou -tf obligatoire...");
					    usage();
					    System.exit(30);
				}   
			}
						
		} catch (ParseException e) {
			System.out.println(e.getMessage());usage();usageHelp();System.exit(70);
		}	
	}

	/***********************************
	 * Usage détaillé
	 ***********************************/
	private void usageHelp() {
		System.out.println("\r\nAide detaillee\r\n"
				+ "--------------\r\n"
				+ "L'option -pdf <PdfInputFile> est obligatoire \n\r\n\r"
				+ "L'option -of <ExtractDestFile> peut etre omise, dans ce cas l'extraction se fait directement sur la console.\r\n\n\r"
				+ "L'option -ts permet de définir une liste de tokens directement sur la ligne de commande\r\n"
				+ "le format de cette chaine de tokens doit respecter le syntaxe suivante :\r\n"
				+ "TokenARemplacer:TokenDeRemplacement (separateur : obligatoire)\r\n"
				+ "chaque couple de tokens doit etre separe par un caractere #\r\n"
				+ "la liste des tokens doit etre entoure des caracteres double quotes \"\r\n"
				+ "Exemple : \"2019:AAAA#Hello:Greating#today:tomorrow\"\r\n"
				+ "NB: Si l'option -ts n'est pas presente, il est obligatoire d'utiliser un fichier de tokens avec l'option -tf\r\n\r\n"
				+ "L'option -tf <nomDuFichier>\r\n"
				+ "Ce fichier doit comporter la liste des tokens (un couple par ligne) separe par un retour chariot\r\n"
				+ "exemple de contenu : \r\n"
				+ "2019:AAAA\r\n"
				+ "Hello:Greating\r\n"
				+ "today:tomorrow\r\n"
				+ "Le fichier doit etre enregistre au format ASCII UTF-8, aucune extension imposee\r\n\r\n"
				+ "L'option -strict est optionnelle, elle permet de forcer la correspondance stricte sur le token a remplacer\r\n"
				+ "si l'option n'est pas presente, l'application ne prend pas en compte la casse et remplace tous les tokens correspondant\r\n"
				+ "");
		
	}

	/**************************************************
	 * Remplacer tous les token du fichier
	 * @param text
	 * @return
	 **************************************************/
	private String replacers(String text) {
		String result;
		//pour chaque token
		Set<Entry<String, String>> setHm = replacers.entrySet();
		Iterator<Entry<String, String>> it = setHm.iterator();
	      while(it.hasNext())
	      {
	         Entry<String, String> e = it.next();
	         //System.out.println(e.getKey() + " : " + e.getValue());
	         if (setstrict==false) 
	         {
	        	 String patternReplace="(?i)"+e.getKey();
	        	 text=text.replaceAll(patternReplace, e.getValue());
	         }
	         else
	         {
	        	 text=text.replaceAll(e.getKey(), e.getValue());
	         }
	      }
	      result=text;
		return result;
	}

	
	/*****************************************
	 * Ouvrir le fichier des remplacements
	 * @param fileR
	 * @throws IOException 
	 *****************************************/
	private void open_File_replacement(String fileR) throws IOException 
	{
		replacers=new HashMap<>();
		BufferedReader bf=null;
		try {
			bf=new BufferedReader(new FileReader(fileR));
			while (bf.ready())
			{
				String line=bf.readLine();
				replacers.put(line.substring(0, line.indexOf(":")),line.substring(line.indexOf(":")+1));
			}
			} catch (IOException e) {
				bf.close();
				System.err.println(e.getMessage());
				System.exit(5);
			}
			finally
			{
				bf.close();
			}
		}
		
	/**************************
	 * MAIN ENTRY POINT
	 * @param args
	 **************************/
	public static void main(String[] args) {new PDFAnonymous(args);}

	/**************************
	 * USAGE 
	 **************************/
	 private void usage()
	    {
		 System.out.println("******************");
		 System.out.println("*  PDFAnonymous  *");
		 System.out.println("******************");
		 HelpFormatter formatter = new HelpFormatter();
		 formatter.printHelp( "PDFAnonymous", options,true );
		 System.out.println("Tondeur Herve (2019) sous Licence GPL V3.");
	     //   System.exit(-1);
	    }
	
}
