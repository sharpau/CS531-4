package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class InputCreator {
	private String fileName;
	private Boolean bAppend = false;;
	private final String sAssumptionsHeader = "formulas(assumptions).";
	private final String sGoalsHeader = "formulas(goals).";
	private final String sFooter = "end_of_list.";
	
	// Constructor
	public InputCreator(String sQueryDirectory, String fName)
	{
		fileName = sQueryDirectory + fName;
	}
	
	public InputCreator(String sQueryDirectory)
	{
		fileName = sQueryDirectory + "Query.in";
	}
	
	// Public Methods
	
	// Formats the First Order Logic Assumptions and Goals
	// Writes into the input (.in) file
	public void WriteAssumptionsGoalsToInputFile(BufferedWriter bw, ArrayList<String> kp, String sGoal)
	{
		// Not the most efficient method
		// Can modify to append new assertions to the assumptions list
		try
		{
			bw.write(sAssumptionsHeader);
			bw.newLine();
			for(int i = 0; i < kp.size(); i++)
			{
				bw.write(kp.get(i));
				bw.newLine();
			}
			bw.write(sFooter);
			bw.newLine();
			bw.newLine();
			
			bw.write(sGoalsHeader);
			bw.newLine();
			bw.write(sGoal);
			bw.newLine();
			bw.write(sFooter);			
			
		}
		catch (IOException ex)
		{
			// Do something
		}
	}
	
	// Creates the input file
	// Returns the input file full path
	public String CreateInputFile(ArrayList<String> knowledgePool, String sGoal)
	{
		FileWriter fw = null;
		try
		{
			File file = new File(fileName);
			if(!file.exists())
				file.createNewFile();
			
			fw = new FileWriter(file, bAppend);
			BufferedWriter bw =  new BufferedWriter(fw);
			WriteAssumptionsGoalsToInputFile(bw, knowledgePool, sGoal);
			bw.close();

		} 
		catch (IOException ex)
		{
			// Do something
		}
		finally
		{
			if(fw != null)
			{
				try
				{
					fw.close();
				}
				catch (IOException ex)
				{
					// Do something
				}
			}
		}
		return fileName;
	}
}
