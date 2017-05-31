import java.io.*;
import java.util.*;

/**
 * @author james spargo
 *
 */
 
public class CheckTrueFalse
{

	/**
	*@param args
	*/
	public static HashMap<String,Boolean> model=new HashMap();
	public static List<String> akbsyms=new ArrayList<String>(64);
	
	public static void main(String[] args)
	{
		
		if(args.length!=3)
		{
			//takes three arguments
			System.out.println("Usage: "+args[0]+" [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}
		
		//create some buffered IO streams
		String buffer;
		BufferedReader inputStream;
		BufferedWriter outputStream;
		
		//create the knowledge base and the statement
		LogicalExpression knowledge_base=new LogicalExpression();
		LogicalExpression statement=new LogicalExpression();
		LogicalExpression not_statement=new LogicalExpression();
		
		//HashMap<String,Boolean> model=new HashMap();

		//open the wumpus_rules.txt
		try
		{
			inputStream=new BufferedReader(new FileReader(args[0]));
			
			//load the wumpus rules
			//System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");
		
			while((buffer=inputStream.readLine())!=null) 
            {
				if(!(buffer.startsWith("#") || (buffer.equals("")))) 
				{
					//the line is not a comment
					LogicalExpression subExpression=readExpression(buffer);
					knowledge_base.setSubexpression(subExpression);
				} 
                else 
                {
					//the line is a comment. do nothing and read the next line
				}
			}		
			
			//close the input file
			inputStream.close();

		}
		catch(Exception e) 
        {
			System.out.println("failed to open " +args[0]);
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules
		
		
		//read the additional knowledge file
		
		try
		{
			inputStream=new BufferedReader(new FileReader(args[1]));
			
			//load the additional knowledge
			//System.out.println("loading the additional knowledge...");
			
			//the connective for knowledge_base is already set.  no need to set it again.
			//i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");
			
			while((buffer=inputStream.readLine())!=null) 
            {
                if(!(buffer.startsWith("#") || (buffer.equals("")))) 
                {
					String putmodel;
					if(buffer.startsWith("("))
					{
						putmodel=buffer.substring((buffer.indexOf(" ")+1),buffer.length()-1);
						model.put(putmodel,false);
						//System.out.println("PUTMODEL from if IS "+putmodel);
						akbsyms.add(putmodel);
					}
					else
					{
						putmodel=buffer;
						model.put(putmodel,true);
						//System.out.println("PUTMODEL from if else "+putmodel);
						akbsyms.add(putmodel);
					}
					LogicalExpression subExpression=readExpression(buffer);
					knowledge_base.setSubexpression(subExpression);
                } 
                else 
                {
					//the line is a comment. do nothing and read the next line
                }
            }
			
			//close the input file
			inputStream.close();

		}
		catch(Exception e)
		{
			System.out.println("failed to open "+args[1]);
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge
		
		//System.out.println("akb sysm is "+akbsyms);
		//System.out.println("model is "+model);
		
		//check for a valid knowledge_base
		if(!valid_expression(knowledge_base))
		{
			System.out.println("invalid knowledge base");
			exit_function(0);
		}
		
		//print the knowledge_base
		//knowledge_base.print_expression("\n");
				
		//read the statement file
		try
		{
			inputStream=new BufferedReader(new FileReader(args[2]));
			
			//System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();
			
			//actually read the statement file
			//assuming that the statement file is only one line long
			while((buffer=inputStream.readLine())!=null)
			{
				if(!buffer.startsWith("#"))
				{
					//the line is not a comment
					statement=readExpression(buffer);
                    break;
				}
				else
				{
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		}
		catch(Exception e)
		{
			System.out.println("failed to open "+args[2]);
			e.printStackTrace();
			exit_function(0);
		}
		
		//end reading the statement file
		
		//check for a valid statement
		if(!valid_expression(statement))
		{
			System.out.println("invalid statement");
			exit_function(0);
		}
		
		//print the statement
		//statement.print_expression("");
		//print a new line
		//System.out.println("\n");
		
		//LogicalExpression not_st= new LogicalExpression();
		//LogicalExpression nots=readExpression("not");
		//not_statement.setSubexpression(nots);
		//not_statement.print_expression("");
		LogicalExpression negst= new LogicalExpression();
		negst.setConnective("not");
		try
		{
			inputStream=new BufferedReader(new FileReader(args[2]));
			
			//System.out.println("\n\nLoading the not statement file...");
			//buffer = inputStream.readLine();
			
			//actually read the statement file
			//assuming that the statement file is only one line long
			while((buffer=inputStream.readLine())!=null)
			{
				if(!buffer.startsWith("#"))
				{
					LogicalExpression subExpression=readExpression(buffer);
					//the line is not a comment
					negst.setSubexpression(subExpression);
					
                    break;
				}
				else
				{
					//the line is a commend. no nothing and read the next line
				}
			}
			
			//close the input file
			inputStream.close();

		}
		catch(Exception e)
		{
			System.out.println("failed to open "+args[2]);
			e.printStackTrace();
			exit_function(0);
		}
		//negst.print_expression("");
		
		boolean kbest=false;
		boolean kbenst=false;
		
		if(TTEntails(knowledge_base,statement))
		{
			kbest=true;
			
			//testing
			//System.out.println("I don't know if the statement is definitely true or definitely false.");
		}
		if(TTEntails(knowledge_base,negst))
		{
			kbenst=true;
			
		}
		try
		{
			outputStream = new BufferedWriter(new FileWriter("result.txt"));
			
			/*1. definitely true. This should be the output if the knowledge base entails the statement, and the knowledge base does not entail the negation of the statement.
			2. definitely false. This should be the output if the knowledge base entails the negation of the statement, and the knowledge base does not entail the statement.
			3. possibly true, possibly false. This should be the output if the knowledge base entails neither the statement nor the negation of the statement.
			4. both true and false. This should be the output if the knowledge base entails both the statement and the the negation of the statement.
			This happens when the knowledge base is always false (i.e., when the knowledge base is false for every single row of the truth table).
			*/
			if(kbest==true && kbenst==false)
			{
				outputStream.write("definitely true");
			}
			if(kbest==false && kbenst==true)
			{
				outputStream.write("definitely false");
			}
			if(kbest==false && kbenst==false)
			{
				outputStream.write("possibly true, possibly false");
			}
			if(kbest==true && kbenst==true)
			{
				outputStream.write("both true and false");
			}
			outputStream.close();
		}
		catch(IOException e)
		{
	    	System.out.println("\nProblem writing to the output file!");
	    	e.printStackTrace();
		}
		
	}//end of main

	/*this method reads logical expressions
	*if the next string is a:
	*- '(' => then the next 'symbol' is a subexpression
	*- else => it must be a unique_symbol
	* 
	*it returns a logical expression
	* 
	*notes: i'm not sure that I need the counter
	* 
	*/
	
	public static LogicalExpression readExpression(String input_string) 
    {
		LogicalExpression result=new LogicalExpression();
          
        //testing
        //System.out.println("readExpression() beginning -"+ input_string +"-");
        //testing
        //System.out.println("\nread_exp");
          
        //trim the whitespace off
        input_string=input_string.trim();
          
        if(input_string.startsWith("(")) 
        {
			//its a subexpression
          
            String symbolString = "";
            
            // remove the '(' from the input string
            symbolString=input_string.substring(1);
            //symbolString.trim();
            
            //testing
            //System.out.println("readExpression() without opening paren -"+ symbolString + "-");
				  
            if(!symbolString.endsWith(")")) 
            {
				//missing the closing paren - invalid expression
				System.out.println("missing ')' !!! - invalid expression! - readExpression():-"+symbolString);
				exit_function(0);
            }
            else 
            {
				//remove the last ')'
				//it should be at the end
				symbolString=symbolString.substring(0,(symbolString.length()-1));
				symbolString.trim();
              
				//testing
				//System.out.println("readExpression() without closing paren -"+ symbolString + "-");
              
				//read the connective into the result LogicalExpression object					  
				symbolString=result.setConnective(symbolString);
              
				//testing
				//System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
				//System.out.println("added connective:->" + result.getConnective() + "<-");
            }
            
            //read the subexpressions into a vector and call setSubExpressions( Vector );
            result.setSubexpressions(read_subexpressions(symbolString));
            
        } 
        else 
        {   	
			//the next symbol must be a unique symbol
            //if the unique symbol is not valid, the setUniqueSymbol will tell us.
            result.setUniqueSymbol(input_string);
          
            //testing
            //System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
        }
          
		return result;
    }

	/*this method reads in all of the unique symbols of a subexpression
	*the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	* 
	*each string is EITHER:
	*-a unique Symbol
	*-a subexpression
	*-Delineated by spaces, and paren pairs
	* 
	*it returns a vector of logicalExpressions
	* 
	* 
	*/
	
	public static Vector<LogicalExpression> read_subexpressions(String input_string)
	{

		Vector<LogicalExpression> symbolList=new Vector<LogicalExpression>();
		LogicalExpression newExpression;										//=new LogicalExpression();
		String newSymbol=new String();
	
		//testing
		//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
		//System.out.println("\nread_sub");

		input_string.trim();

		while(input_string.length()>0)
		{
		
			newExpression=new LogicalExpression();
		
			//testing
			//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

			if(input_string.startsWith("("))
			{
				//its a subexpression.
				//have readExpression parse it into a LogicalExpression object

				//testing
				//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");
			
				// find the matching ')'
				int parenCounter=1;
				int matchingIndex=1;
				while((parenCounter>0) && (matchingIndex<input_string.length()))
				{
					if(input_string.charAt(matchingIndex)=='(')
					{
						parenCounter++;
					}
					else if(input_string.charAt(matchingIndex)==')')
					{
						parenCounter--;
					}
					matchingIndex++;
				}
			
				//read untill the matching ')' into a new string
				newSymbol=input_string.substring(0,matchingIndex);
			
				//testing
				//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

				//pass that string to readExpression,
				newExpression=readExpression(newSymbol);

				//add the LogicalExpression that it returns to the vector symbolList
				symbolList.add(newExpression);

				//trim the logicalExpression from the input_string for further processing
				input_string=input_string.substring(newSymbol.length(),input_string.length());

			}
			else
			{
				//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

				//I only want the first symbol, so, create a LogicalExpression object and
				//add the object to the vector
			
				if(input_string.contains(" "))
				{
					//remove the first string from the string
					newSymbol=input_string.substring(0,input_string.indexOf(" "));
					input_string=input_string.substring((newSymbol.length()+1),input_string.length());
				
					//testing
					//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
				}
				else
				{
					newSymbol=input_string;
					input_string="";
				}
			
				//testing
				//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );
			
				newExpression.setUniqueSymbol(newSymbol);
			
				//testing
				//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

				symbolList.add(newExpression);
			
				//testing
				//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");
			
			}
		
			//testing
			//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");
		
			input_string.trim();
		
			if(input_string.startsWith(" "))
			{
				//remove the leading whitespace
				input_string=input_string.substring(1);
			}
		
			//testing
			//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
		}
		
		return symbolList;
	}


	/*this method checks to see if a logical expression is valid or not 
	 *a valid expression either:
	 *( this is an XOR )
	 *- is a unique_symbol
	 *- has:
	 *-- a connective
	 *-- a vector of logical expressions
	 *  
	 **/
	 
	public static boolean valid_expression(LogicalExpression expression)
	{
		
		//checks for an empty symbol
		//if symbol is not empty, check the symbol and
		//return the truthiness of the validity of that symbol

		if(!(expression.getUniqueSymbol()==null) && (expression.getConnective()==null))
		{
			//we have a unique symbol, check to see if its valid
			return valid_symbol(expression.getUniqueSymbol());

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
		}

		//symbol is empty, so
		//check to make sure the connective is valid
	  
		//check for 'if / iff'
		if((expression.getConnective().equalsIgnoreCase("if")) || (expression.getConnective().equalsIgnoreCase("iff")))
		{
			
			//the connective is either 'if' or 'iff' - so check the number of connectives
			if(expression.getSubexpressions().size()!=2)
			{
				System.out.println("error: connective \""+expression.getConnective()+"\" with "+expression.getSubexpressions().size()+" arguments\n");
				return false;
			}
		}
		//end 'if / iff' check
	  
		//check for 'not'
		else if(expression.getConnective().equalsIgnoreCase("not"))
		{
			//the connective is NOT - there can be only one symbol / subexpression
			if(expression.getSubexpressions().size()!=1)
			{
				System.out.println("error: connective \""+expression.getConnective()+"\" with "+expression.getSubexpressions().size()+" arguments\n"); 
				return false;
			}
		}
		//end check for 'not'
		
		//check for 'and / or / xor'
		else if((!expression.getConnective().equalsIgnoreCase("and")) && (!expression.getConnective().equalsIgnoreCase("or")) &&
				(!expression.getConnective().equalsIgnoreCase("xor" )))
		{
			System.out.println("error: unknown connective "+expression.getConnective()+"\n");
			return false;
		}
		//end check for 'and / or / not'
		//end connective check

	  
		//checks for validity of the logical_expression 'symbols' that go with the connective
		for(Enumeration e=expression.getSubexpressions().elements();e.hasMoreElements();)
		{
			LogicalExpression testExpression=(LogicalExpression)e.nextElement();
			
			//for each subExpression in expression,
			//check to see if the subexpression is valid
			if(!valid_expression(testExpression))
			{
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");
		
		//if the method made it here, the expression must be valid
		return true;
	}
	



	/**this function checks to see if a unique symbol is valid */
	////////////////////this function should be done and complete
	//originally returned a data type of long.
	//I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol(String symbol)
	{
		if(symbol==null || (symbol.length()==0))
		{
			
			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");
			
			return false;
		}

		for(int counter=0;counter<symbol.length();counter++)
		{
			if((symbol.charAt(counter)!='_') &&	(!Character.isLetterOrDigit(symbol.charAt(counter))))
			{
				
				System.out.println("String: "+symbol+" is invalid! Offending character:---"+symbol.charAt(counter)+ "---\n");
				
				return false;
			}
		}
		
		//the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

    private static void exit_function(int value)
	{
		System.out.println("exiting from checkTrueFalse");
        System.exit(value);
    }	
				
				
				
				
				
	/////////////////////////////////////////USER DEFINED FUNCTIONS START/////////////////////////////////////////		
				
	private static List<String> getSymbols(LogicalExpression list1,List<String> symlist)
	{
			
		if(!(list1.uniqueSymbol==null))
		{
			 symlist.add(list1.uniqueSymbol);
		}
		else
		{
			LogicalExpression next;
			List<String> useless=new ArrayList<String>(1);
			
			for(Enumeration e=list1.subexpressions.elements();e.hasMoreElements();)
			{
				next=(LogicalExpression)e.nextElement();
				useless=getSymbols(next,symlist);
			}
		}
		return symlist;
	}
	
	/*private static List<String> getSymbolsNew(LogicalExpression list1,List<String> symlist)
	{
			
		if(!(list1.uniqueSymbol==null))
		{
			 symlist.add(list1.uniqueSymbol);
		}
		else
		{
			LogicalExpression next;
			List<String> useless=new ArrayList<String>(1);
			for(Enumeration e=list1.subexpressions.elements();e.hasMoreElements();)
			{
				next=(LogicalExpression)e.nextElement();
				useless=getSymbols(next,symlist);
			}
		}
		return symlist;
	}*/


	private static List<String> joinSymbols(List<String> list1,List<String> list2)
	{
		List<String> newlist=new ArrayList<String>(64);
		List<String> finallist=new ArrayList<String>(64);
		
		newlist.add(list1.get(0));
		
		boolean exists=false;
		
		for(int i=0;i<list1.size();i++)
		{
			exists=false;

			for(int j=0;j<newlist.size();j++)
			{
				if(list1.get(i).equals(newlist.get(j)))
				{
					exists=true;
					break;
				}
			}
			if(exists==false)
			{
				newlist.add(list1.get(i));
			}
		}
		
		for(int i=0;i<list2.size();i++)
		{
			exists=false;
			
			for(int j=0;j<newlist.size();j++)
			{
				if(list2.get(i).equals(newlist.get(j)))
				{
					exists=true;
					break;
				}
			}
			if(exists==false)
			{
				newlist.add(list2.get(i));
			}
		}
		
		finallist.add(newlist.get(0));
		for(int i=0;i<newlist.size();i++)
		{
			exists=false;
			
			for(int j=0;j<finallist.size();j++)
			{
				if(newlist.get(i).equals(finallist.get(j)))
				{
					exists=true;
					break;
				}
			}
			if(exists==false)
			{
				finallist.add(newlist.get(i));
			}
		}
		
		
		return finallist;
	}
	
	private static boolean checkForCommonSymbols(List<String> list1,List<String> list2)
	{
		//boolean commonexist=false;
		
		for(int i=0;i<list1.size();i++)
		{
			for(int j=0;j<list2.size();j++)
			{
				if(list1.get(i).equalsIgnoreCase(list2.get(j)))
				{
					return true;
				}
			}
		}
		
		return false;
	}

	
	private static List<String> removeCommonSymbols(List<String> list1,List<String> list2)
	{
		
		for(int i=0;i<list2.size();i++)
		{
			for(int j=0;j<list1.size();j++)
			{
				if(list2.get(i).equalsIgnoreCase(list1.get(j)))
				{
					//System.out.println("removed this "+list1.get(i));
					list1.remove(j);
				}
			}
		}
		return list1;
	}
		
	private static boolean TTEntails(LogicalExpression knowledge_base,LogicalExpression statement)
	{
		//if(model.isEmpty())
		//System.out.println("HAsh is empty");
		List<String> KBSyms=new ArrayList<String>(64);
		KBSyms = getSymbols(knowledge_base,KBSyms);
		//System.out.println("KBSYMS is "+KBSyms);
		List<String> StatementSyms = new ArrayList<String>(64);
		StatementSyms = getSymbols(statement,StatementSyms);
		//System.out.println("STSYMS is "+StatementSyms);
		if(checkForCommonSymbols(KBSyms,StatementSyms))
		{	
			List<String> KBandStatementSyms = new ArrayList<String>(64);
			KBandStatementSyms=joinSymbols(KBSyms,StatementSyms);
			//System.out.println("after join both syms is "+KBandStatementSyms);
			KBandStatementSyms=removeCommonSymbols(KBandStatementSyms,akbsyms);
			//System.out.println("after remove both syms is "+KBandStatementSyms);
			//HashMap<String,Boolean> model=new HashMap();
		
			/*for(int i=0;i<KBandStatementSyms.size();i++)
			{
				System.out.println("both is "+KBandStatementSyms.get(i));
			}*/
			return TTCheckAll(knowledge_base,statement,KBandStatementSyms,model);
		}
		else
		{
			return false;
		}
	}
	
	
	private static boolean TTCheckAll(LogicalExpression kbase,LogicalExpression stmt,List<String> KBandSSyms,HashMap model)
	{
		/*for(int i=0;i<KBandSSyms.size();i++)
		{
			System.out.println("Model for "+KBandSSyms.get(i)+" is "+model.get(KBandSSyms.get(i)));
		}*/
		//System.out.println();
		//System.out.println("symbol size is"+KBandSSyms.size());
		if(KBandSSyms.size()==0)
		{
			//System.out.println("in if ttcheck all");
			if(PLTrue(kbase,model)) 
			{
				//System.out.println("pl true kb true");
				//System.out.println("alpha is "+PLTrue(stmt, model));
				return PLTrue(stmt,model);
			}
			else 
			{
				//System.out.println("pl true kb false");
				return true;
			}
		}	
		else
		{
			//System.out.println("in else ttcheckall");
			String P = KBandSSyms.get(0);
			List<String> rest=new ArrayList<String>(KBandSSyms.size()-1);
			for(int i=1;i<KBandSSyms.size();i++)
			{
				//System.out.println("rest is "+KBandSSyms.get(i));
				rest.add(KBandSSyms.get(i));
			}
			if(!(TTCheckAll(kbase,stmt,rest,Extend(P,true,model))))
			{
				return false;
			}
			else
			{
				return (TTCheckAll(kbase,stmt,rest,Extend(P,false,model)));
			}
		}
	}
	
	
	private static HashMap Extend(String P,Boolean val,HashMap mod)
	{
		//System.out.println("before remove "+mod);
		mod.remove(P);
		//System.out.println("after remove "+mod);
		mod.put(P,val);
		//System.out.println("after add "+mod);
		//System.out.println("choco for  "+P+" is "+mod.get(P));
		return mod;
	}
	
	
	private static boolean PLTrue(LogicalExpression st,HashMap model)
	{
		//System.out.println("***********************conn is "+st.connective);
		//System.out.println("***********************US is "+st.uniqueSymbol);
		if(st.uniqueSymbol!=null)
		{
			//System.out.println("INNNNNNNNNNNNNNNNNN US is "+st.uniqueSymbol);
			//Thread.sleep(500);
			//System.out.println("US is "+st.uniqueSymbol);
			String dumb=model.get(st.uniqueSymbol).toString();
			//System.out.println("dumb is "+dumb);
			if(dumb.equalsIgnoreCase("true"))
			{
				return true;
			}
			else
			{
				return false;
			}
			//System.out.println("chu is "+model.get(jhol));
			//return true;
		}
		else if(st.getConnective().equalsIgnoreCase("and"))
		{	
			//System.out.println("IN AND MOFO********************************************");
			LogicalExpression next;

			//enumerate over the 'symbols' ( LogicalExpression objects ) and print them
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();) 
			{
				next=(LogicalExpression)e.nextElement();
				//System.out.println("left and is ");
				//next.print_expression("");
				//System.out.println("right and is ");
				if(PLTrue(next,model)==false)
				{
					//System.out.println("returning false from and");
					return false;
				}
			}
			return true;
		}
		else if(st.getConnective().equalsIgnoreCase("or"))
		{
			//System.out.println("IN OR MOFO********************************************");
			LogicalExpression next;
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();)
			{
				next=(LogicalExpression)e.nextElement();
				//System.out.print("\nleft or is ");
				//next.print_expression("");
				//System.out.print("\nright or is ");
				//right.print_expression("");
				if(PLTrue(next,model)==true)
				{
					return true;
				}
			}
			return false;
		}
		else if(st.getConnective().equalsIgnoreCase("if"))
		{
			//System.out.println("IN IF MOFO********************************************");
			LogicalExpression left,right;
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();)
			{
				left=(LogicalExpression)e.nextElement();
				right=(LogicalExpression)e.nextElement();
				if(PLTrue(left,model)==false)
				{
					return true;
				}
				else if(PLTrue(left,model)==true)
				{
					return PLTrue(right,model);
				}
			}
		}
		else if(st.getConnective().equalsIgnoreCase("iff"))
		{
			//System.out.println("IN IFF MOFO********************************************");
			LogicalExpression left,right;
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();)
			{
				left=(LogicalExpression)e.nextElement();
				right=(LogicalExpression)e.nextElement();
				if(PLTrue(left,model)==PLTrue(right,model))
				{
					return true;
				}
				return false;
			}
		}
		else if(st.getConnective().equalsIgnoreCase("not"))
		{
			//System.out.println("IN NOT MOFO********************************************");
			LogicalExpression left;
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();)
			{
				left=(LogicalExpression)e.nextElement();
				//System.out.println("Pl true of left of not is "+PLTrue(left,model));
				if(PLTrue(left,model))
				{
					//System.out.println("returning false from not");
					return false;
				}
				else
				{
					//System.out.println("returning true from not");
					return true;
				}
			}
		}
		else if(st.getConnective().equalsIgnoreCase("xor"))
		{
			//System.out.println("IN XOR MOFO********************************************");
			LogicalExpression next;
			int count_trues=0;
			for(Enumeration e=st.subexpressions.elements();e.hasMoreElements();)
			{
				next=(LogicalExpression)e.nextElement();
				if(PLTrue(next,model))
				{
					count_trues++;
				}
			}
			if(count_trues==1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}
}