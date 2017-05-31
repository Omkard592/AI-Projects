import java.util.*;
import java.io.*;

class compute_a_posteriori
{

	void p_of_q_tplus1(double[] ph,double[][] distribution,double[] pc)
	{
		for(int i=0;i<2;i++)
		{
			pc[i]=(distribution[i+1][0]*ph[0])+(distribution[i+1][1]*ph[1])+(distribution[i+1][2]*ph[2])+(distribution[i+1][3]*ph[3])+(distribution[i+1][4]*ph[4]);
		}
	}
  
	double pofhi(int obser,int h,int c,String observations,double[] pc,double[] ph,double[][] distribution)
	{
		double pq=0;
		
		for(int i=0;i<5;i++)
		{
			pq+=(basecase(i,c,observations,pc,ph,distribution)*(distribution[obser][i]));
		}
		return pq;
	}
	
	double logicc(int obser,int h,double[] pc,double[] ph,double[][] distribution)
	{
		double pq=0;
		
		for(int i=0;i<5;i++)
		{
			pq+=(ph[i]*(distribution[obser][i]));
		}
		return pq;
	}
  
	double basecase(int h,int c,String observations,double[] pc,double[] ph,double[][] distribution)
	{
		int obser;
		char at=observations.charAt(c-1);
		String ab=Character.toString(at);
		
		if(ab.equalsIgnoreCase("c"))
		{
			obser=1;
		}
		else
		{
			obser=2;  
		}
		if(c==1)
		{
			return (distribution[obser][h]*ph[h])/pc[obser-1];
		}
		else
		{
			return (distribution[obser][h]*basecase(h,c-1,observations,pc,ph,distribution))/pofhi(obser,h,c-1,observations,pc,ph,distribution);
		}
	}
  
	void printResultToTextFile(String observations,double[] hypo,double cherry,double lime) 
	{
		try
		{
			BufferedWriter outputStream = new BufferedWriter(new FileWriter("result.txt"));
			outputStream.write("Observation sequence Q: "+observations+"\r\n");
			if(observations.equals("No Observation"))
			observations="";
			outputStream.write("Length of Q: "+observations.length()+"\r\n\r\n");
			outputStream.write("P(h1 | Q) = "+hypo[0]+"\r\n");
			outputStream.write("P(h2 | Q) = "+hypo[1]+"\r\n");
			outputStream.write("P(h3 | Q) = "+hypo[2]+"\r\n");
			outputStream.write("P(h4 | Q) = "+hypo[3]+"\r\n");
			outputStream.write("P(h5 | Q) = "+hypo[4]+"\r\n\r\n");
			outputStream.write("Probability that the next candy we pick will be C, given Q: "+cherry+"\r\n");
			outputStream.write("Probability that the next candy we pick will be L, given Q: "+lime);
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
		double[] ph={0.1,0.2,0.4,0.2,0.1};
		double[] h=new double[5];
		double[] pc=new double[2];
		int length;
		double[][] distribution=new double[3][5]; 
		
		distribution[1][0]=1;
		distribution[1][1]=0.75;
		distribution[1][2]=0.5;
		distribution[1][3]=0.25;
		distribution[1][4]=0;
		distribution[2][0]=0;		
		distribution[2][1]=0.25;		
		distribution[2][2]=0.5;		
		distribution[2][3]=0.75;		
		distribution[2][4]=1;
		
		compute_a_posteriori obj=new compute_a_posteriori();
    
		Scanner sc=new Scanner(System.in);
		
		if(args.length==0) 
		{
			obj.p_of_q_tplus1(ph,distribution,pc);		
			double cherry=obj.logicc(1,0,pc,ph,distribution);
			double lime=obj.logicc(2,0,pc,ph,distribution);
			obj.printResultToTextFile("No Observation",ph,cherry,lime);
		}
		else
		{
			String input=args[0];
			input=input.toUpperCase();
			
			for(int i=0;i<input.length();i++)
			{
				if(input.charAt(i)!='C' && input.charAt(i)!='L')
				{
					System.out.print("\nYou have entered an invalid input. Exiting the program...");
					System.exit(0);
				}
			}
			
			obj.p_of_q_tplus1(ph,distribution,pc);
		
			for(int i=0;i<5;i++)
			{
				h[i]=obj.basecase(i,input.length(),input,pc,ph,distribution);
			}
		
			double cherry=obj.pofhi(1,0,input.length(),input,pc,ph,distribution);
			double lime=obj.pofhi(2,0,input.length(),input,pc,ph,distribution);
			obj.printResultToTextFile(input.toUpperCase(),h,cherry,lime);
		}
	}
}