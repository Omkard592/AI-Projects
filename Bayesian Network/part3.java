import java.util.*;
import java.io.*;

class part3
{
	static int data[][];
	static int row_count=0;
	static float pOfGame[];
	static float pOfNoFood[];
	static float pOfWatchesGivenGame[];
	static float pOfFeedsGivenWatchesAndNoFood[];

	void acceptAndProcessInput(String input_filename)throws Exception
	{
		int i=0,j;

		FileReader fr1=new FileReader(input_filename);
		Scanner sa1=new Scanner(fr1);

		while(sa1.hasNextLine())				//loop to count the total number of entry lines in the input text file
 		{
			Scanner sa2 = new Scanner(sa1.nextLine());
 			boolean b1;

 			while(b1=sa2.hasNext())
 			{	
 				String str=sa2.next();				
 			}
			
			row_count++;

		}

		data=new int[row_count][4];				//create an array to store data from the input file

		FileReader fr2=new FileReader(input_filename);
		Scanner sb1=new Scanner(fr2);
		while(sb1.hasNextLine())
 		{
			Scanner sb2 = new Scanner(sb1.nextLine());
 			boolean b2;
			j=0;

 			while(b2=sb2.hasNext())
 			{	
 				data[i][j]=Integer.parseInt(sb2.next());
				j++;
				
 			}
		
			i++;

		}
		

		//data[i][0] is baseball game on TV
		//data[i][1] is George watches TV
		//data[i][2] is George is out of cat food
		//data[i][3] is George feeds the cat
	}

	void calculateTables()
	{
		pOfGame=new float[2];
		float kgame=0;

		pOfWatchesGivenGame=new float[2];
		float kwatchesandgame=0;
		float kwatchesandnotgame=0;
		float knotgame=0;

		pOfNoFood=new float[2];
		float knofood=0;

		pOfFeedsGivenWatchesAndNoFood=new float[4];
		float kfeedsandwatchesandnofood=0;
		float kwatchesandnofood=0;
		float kfeedsandwatchesandnotnofood=0;
		float kwatchesandnotnofood=0;
		float kfeedsandnotwatchesandnofood=0;
		float knotwatchesandnofood=0;
		float kfeedsandnotwatchesandnotnofood=0;
		float knotwatchesandnotnofood=0;

		//data[i][0] is baseball game on TV
		//data[i][1] is George watches TV
		//data[i][2] is George is out of cat food
		//data[i][3] is George feeds the cat

		for(int i=0;i<row_count;i++)
		{
			////////////////////////////////////////////
			if(data[i][0]==1)
			{
				kgame++;
			}
			////////////////////////////////////////////
			if(data[i][2]==1)
			{
				knofood++;
			}
			////////////////////////////////////////////
			if(data[i][0]==1 && data[i][1]==1)
			{
				kwatchesandgame++;
			}
			if(data[i][0]==0 && data[i][1]==1)
			{
				kwatchesandnotgame++;
			}
			////////////////////////////////////////////
			if(data[i][3]==1 && data[i][1]==1 && data[i][2]==1)
			{
				kfeedsandwatchesandnofood++;
			}
			if(data[i][1]==1 && data[i][2]==1)
			{
				kwatchesandnofood++;
			}
			if(data[i][3]==1 && data[i][1]==1 && data[i][2]==0)
			{
				kfeedsandwatchesandnotnofood++;
			}
			if(data[i][1]==1 && data[i][2]==0)
			{
				kwatchesandnotnofood++;
			}	
			if(data[i][3]==1 && data[i][1]==0 && data[i][2]==1)
			{
				kfeedsandnotwatchesandnofood++;
			}
			if(data[i][1]==0 && data[i][2]==1)
			{
				knotwatchesandnofood++;
			}
			if(data[i][3]==1 && data[i][1]==0 && data[i][2]==0)
			{
				kfeedsandnotwatchesandnotnofood++;
			}
			if(data[i][1]==0 && data[i][2]==0)
			{
				knotwatchesandnotnofood++;
			}
			////////////////////////////////////////////
		}

		knotgame=row_count-kgame;

		pOfGame[0]=kgame/row_count;
		pOfGame[1]=1-pOfGame[0];

		pOfNoFood[0]=knofood/row_count;
		pOfNoFood[1]=1-pOfNoFood[0];

		pOfWatchesGivenGame[0]=kwatchesandgame/kgame;
		pOfWatchesGivenGame[1]=kwatchesandnotgame/knotgame;

		pOfFeedsGivenWatchesAndNoFood[0]=kfeedsandwatchesandnofood/kwatchesandnofood;
		pOfFeedsGivenWatchesAndNoFood[1]=kfeedsandwatchesandnotnofood/kwatchesandnotnofood;
		pOfFeedsGivenWatchesAndNoFood[2]=kfeedsandnotwatchesandnofood/knotwatchesandnofood;
		pOfFeedsGivenWatchesAndNoFood[3]=kfeedsandnotwatchesandnotnofood/knotwatchesandnotnofood;
		
		System.out.println("Probability Tables written to part3result.txt");
	}

	void printResultToTextFile()
	{
		try
		{
			BufferedWriter outputStream = new BufferedWriter(new FileWriter("part3result.txt"));
			outputStream.write("Probability Tables are as follows\r\n\r\n");
			outputStream.write("baseball_game_on_TV\r\n");
			outputStream.write("      P(game)\r\n");
			outputStream.write("T     "+pOfGame[0]+"\r\n");
			outputStream.write("F     "+pOfGame[1]+"\r\n\r\n");
			
			outputStream.write("out_of_cat_food\r\n");
			outputStream.write("      P(nofood)\r\n");
			outputStream.write("T     "+pOfNoFood[0]+"\r\n");
			outputStream.write("F     "+pOfNoFood[1]+"\r\n\r\n");

			outputStream.write("George_watches_TV\r\n");
			outputStream.write("game  P(watches)\r\n");
			outputStream.write("T     "+pOfWatchesGivenGame[0]+"\r\n");
			outputStream.write("F     "+pOfWatchesGivenGame[1]+"\r\n\r\n");

			outputStream.write("George_feeds_cat\r\n");
			outputStream.write("watches  nofood	P(feeds)\r\n");
			outputStream.write("T	 T      "+pOfFeedsGivenWatchesAndNoFood[0]+"\r\n");
			outputStream.write("T	 F      "+pOfFeedsGivenWatchesAndNoFood[1]+"\r\n");
			outputStream.write("F	 T      "+pOfFeedsGivenWatchesAndNoFood[2]+"\r\n");
			outputStream.write("F	 F      "+pOfFeedsGivenWatchesAndNoFood[3]);
			
			outputStream.close();
		}
		catch(IOException e)
		{
	    		System.out.println("\nProblem writing to the output file!");
	    		e.printStackTrace();
		}
	}

	public static void main(String args[])throws Exception
	{

		part3 obj=new part3();
		obj.acceptAndProcessInput("training_data.txt");
		obj.calculateTables();
		obj.printResultToTextFile();

	}//psvm

}//class