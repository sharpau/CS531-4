package KnowledgeBase;

import java.io.IOException;
import java.util.ArrayList;
import util.InputCreator;

public class KnowledgeBase 
{
	private String sProver9FullPath;
	private String sQueryDirectory;
	private ArrayList<String> KnowledgePool = new ArrayList<String>();
 
	// Constructor
	public KnowledgeBase()
	{
		// Set Prover9 path and a folder for input file(s)
		this.sProver9FullPath = "C:\\Vikedo\\CS531\\Assignment_#4\\WumpusAgent\\LADR\\bin\\Prover9.exe";
		this.sQueryDirectory = "C:\\Vikedo\\CS531\\Assignment_#4\\WumpusAgent\\JavaWumpus\\Wumpus\\QFolder\\";
	}
	
	public KnowledgeBase(String prover9Path)
	{
		this.sProver9FullPath = prover9Path;
		this.sQueryDirectory = "C:\\Temp\\";
	}
	
	// Get Property
	public String getsProver9FullPath()
	{
		return sProver9FullPath;
	}
		
	// Private Methods	
	// Formats the command to call Prover9
	private String BuildCommand(String input)
	{
		String command = getsProver9FullPath() + " -f " + input + " > " + sQueryDirectory + "result.out";
		return command;
	}
	
	// Calls the Prover9 executable
	// Arguments: Input and Output file
	private int ExecuteCommand(String cmd)
	{
		int result = -1;
		try
		{
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			result = process.exitValue();
		}
		catch(IOException ex)
		{
			// Report Error
		}
		return result;
	}		
	
	// Public Methods
	public Boolean Ask(String query)
	{
		// Build input file
		InputCreator iC = new InputCreator(sQueryDirectory);
		String inputFile = iC.CreateInputFile(KnowledgePool, query + ".");
		
		// Build Command - call Prover9
		String command = BuildCommand(inputFile);
		
		// Execute command
		int exitCode = ExecuteCommand(command);
		
		if(exitCode == 0)
			return true;
		return false;
	}
	
	public void Tell(String assertion)
	{
		KnowledgePool.add(assertion + ".");
	}
}
