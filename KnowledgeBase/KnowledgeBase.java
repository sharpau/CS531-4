package KnowledgeBase;

import java.util.ArrayList;
import java.io.File;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.net.URISyntaxException;

import util.InputCreator;

public class KnowledgeBase 
{
	private static final int TIMEOUT_SECONDS = 10;
	private static final String PROVER9_EXE = "\\src\\LADR\\bin\\Prover9.exe";
	private static final String QUERY_DIR = "\\QFolder\\"; 
	private String sProver9FullPath;
	private String sQueryDirectory;
	private ArrayList<String> KnowledgePool = new ArrayList<String>();
	private Boolean bUseTimer = false;
 
	// Constructor
	public KnowledgeBase()
	{
		SetPaths();
		AddWumpusWorldAxioms();
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
	// Set Prover9 and QueryFolder (folder for input file(s)) path
	private void SetPaths()
	{
		try 
		{
			File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			String sParentFullPath = file.getParent();
			
			file = new File(sParentFullPath + PROVER9_EXE);
			if(file.exists() && file.isFile())
				this.sProver9FullPath = sParentFullPath + PROVER9_EXE;
			else
				System.out.println("\nProver9 not found in: " + file.getParent());
			
			file = new File(sParentFullPath + QUERY_DIR);
			if(!file.exists())
				file.mkdir();
			this.sQueryDirectory = sParentFullPath + QUERY_DIR;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Formats the command to call Prover9
	private String BuildCommand(String input)
	{
		String command;
		if(!bUseTimer)
		{
			command = getsProver9FullPath() + " -f " + input + " > " + sQueryDirectory + "result.out";
		}
		else
		{
			command = getsProver9FullPath() + " -t " + TIMEOUT_SECONDS + " -f " + input + " > " + sQueryDirectory + "result.out";
		}
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
			
			// Wait for Prover9 process to complete
			result = process.waitFor();
		}
		catch(Throwable t)
		{
			// Report Run Process error
			System.out.println("\nError in calling Prover9: " + t.getMessage());
		}
		return result;
	}
	
	void AddWumpusWorldAxioms()
	{
		// Adjacency axioms
		Tell("succ(2,1)");
		Tell("succ(3,2)");
		Tell("succ(4,3)");
		Tell("pred(1,2)");
		Tell("pred(2,3)");
		Tell("pred(3,4)");
		Tell("Adjacent([x,y],[u,v]) <-> x=u & (succ(v,y) | pred(v,y)) | y=v & (succ(u,x) | pred(u,x))");
		
		// Breezy Square
		Tell("At(Agent, [x,y], u) & Breeze(u) -> Breezy([x,y])");
		
		// Visited Square
		Tell("At(Agent, [x,y], u) <-> Visited([x,y])");
		
		// Wumpus might be at any adjacent square if the square is smelly
		// Uncertain assertion (Incorrect)
		Tell("Smelly([x,y]) -> Adjacent([x,y],[u,v]) & At(Wumpus, [u,v])");
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
		// All assertions for Prover9 must end with '.' (dot)
		KnowledgePool.add(assertion + ".");
	}
}
