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
	private static final String PROVER9_EXE = "\\LADR\\bin\\Prover9.exe";
	private static final String QUERY_DIR = "\\QFolder\\"; 
	private String sProver9FullPath;
	private String sQueryDirectory;
	private ArrayList<String> KnowledgePool = new ArrayList<String>();
	private Boolean bUseTimer = false;
 
	// Constructor
	public KnowledgeBase(int size)
	{
		SetPaths();
		AddWumpusWorldFacts(size);
		AddWumpusWorldRules();
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
			//String sParentFullPath = file.getParent();
			String sParentFullPath = file.getAbsolutePath();
			
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
	
	// Add facts to KB
	private void AddWumpusWorldFacts(int size)
	{
		// Adjacency axioms
		// Facts
		
		for(int i = 0; i < size-1; i++)
		{
			Tell(String.format("succ(%d,%d)", i+1, i));
		}
		
//		Tell("succ(1,0)");
//		Tell("succ(2,1)");
//		Tell("succ(3,2)");
		Tell("At(Agent, [0,0], 0)");
	}
	
	// Add rules to KB
	private void AddWumpusWorldRules()
	{		
		// Adjacency 
		Tell("Adjacent([x,y],[u,v]) <-> x=u & (succ(v,y) | succ(y,v)) | y=v & (succ(u,x) | succ(x,u))");
		
		// Breezy Square
		Tell("At(Agent, [x,y], u) & -Breeze(u) -> -Breezy([x,y])");
		
		// Smelly Square
		Tell("At(Agent, [x,y], u) & -Smelly(u) -> -Stench([x,y])");
		
		// Visited Square
		Tell("At(Agent, [x,y], u) -> Visited([x,y])");
		
		// Pit-less Square
		Tell("-Breezy([x,y]) & Adjacent([x,y],[u,v]) -> -Pit([u,v])");
		
		// Wumpus-free Square
		Tell("-Stench([x,y]) & Adjacent([x,y],[u,v]) -> -Wumpus([u,v])");
		
		// Safe Square
		Tell("Safe([x,y]) <-> ((-Wumpus([x,y]) | Scream(u)) & -Pit([x,y])) | Visited([x,y])");
		
		// Visited Square has no wumpus
		Tell("Visited([x,y]) -> -Wumpus([x,y])");

		// Visited Square has no pit
		Tell("Visited([x,y]) -> -Pit([x,y])");
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
