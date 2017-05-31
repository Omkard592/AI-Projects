import java.util.*;
import java.io.*;

class bnet
{
	double computeProbability(ArrayList<String> seq)
	{	
		if(seq.size()==5)
		{
			double burg,earth,alarm,john,mary;
			ArrayList<String> newseq = new ArrayList<String>();
			newseq.addAll(seq);

			for(int i=0;i<1;i++)
			{
				if(!Character.toString(newseq.get(i).charAt(0)).equalsIgnoreCase("B"))
				{
					for(int j=0;j<5;j++)
					{
						if(Character.toString(seq.get(j).charAt(0)).equalsIgnoreCase("B"))
						{
							newseq.add(0,seq.get(j));
							newseq.remove(j+1);
						}
			
					}
				}
				if(!Character.toString(newseq.get(i+1).charAt(0)).equalsIgnoreCase("E"))
				{
					for(int j=0;j<5;j++)
					{
						if(Character.toString(seq.get(j).charAt(0)).equalsIgnoreCase("E"))
						{
							newseq.add(1,seq.get(j));
							newseq.remove(j+1);
						}
			
					}
				}
				if(!Character.toString(newseq.get(i+2).charAt(0)).equalsIgnoreCase("A"))
				{
					for(int j=0;j<5;j++)
					{
						if(Character.toString(seq.get(j).charAt(0)).equalsIgnoreCase("A"))
						{
							newseq.add(2,seq.get(j));
							newseq.remove(j+1);
						}
			
					}
				}
				if(!Character.toString(newseq.get(i+3).charAt(0)).equalsIgnoreCase("J"))
				{
					for(int j=0;j<5;j++)
					{
						if(Character.toString(seq.get(j).charAt(0)).equalsIgnoreCase("J"))
						{
							newseq.add(3,seq.get(j));
							newseq.remove(j+1);
						}
			
					}
				}
				if(!Character.toString(newseq.get(i+4).charAt(0)).equalsIgnoreCase("M"))
				{
					for(int j=0;j<5;j++)
					{
						if(Character.toString(seq.get(j).charAt(0)).equalsIgnoreCase("M"))
						{
							newseq.add(4,seq.get(j));
							newseq.remove(j+1);
						}
			
					}
				}

				i++;
			}
		
		
			if(Character.toString(newseq.get(0).charAt(1)).equalsIgnoreCase("f"))
		{	
			burg=1-0.001;
		}
		else
		{
			burg=0.001;
		}
		
		if(Character.toString(newseq.get(1).charAt(1)).equalsIgnoreCase("f"))
		{
			earth=1-0.002;
		}
		else
		{
			earth=0.002;
		}
		
		if(Character.toString(newseq.get(0).charAt(1)).equalsIgnoreCase("t"))
		{
			if(Character.toString(newseq.get(1).charAt(1)).equalsIgnoreCase("t"))
			{
				if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
				{
					alarm=0.95;
				}
				else
				{
					alarm=1-0.95;
				}
			}
			else
			{
				if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
				{
					alarm=0.94;
				}
				else
				{
					alarm=1-0.94;
				}
			}
		}
		else
		{
			if(Character.toString(newseq.get(1).charAt(1)).equalsIgnoreCase("t"))
			{
				if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
				{
					alarm=0.29;
				}
				else
				{
					alarm=1-0.29;
				}
			}
			else
			{
				if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
				{
					alarm=0.001;
				}
				else
				{
					alarm=1-0.001;
				}
			}
		}
		
		
		
		if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
		{
			if(Character.toString(newseq.get(3).charAt(1)).equalsIgnoreCase("t"))
			{
			john=0.90;
			}
			else
			{
				john=1-0.90;
			}
		}
		else
		{
			if(Character.toString(newseq.get(3).charAt(1)).equalsIgnoreCase("t"))
			{
			john=0.05;
			}
			else
			{
			john=1-0.05;
			}
		}
		
		
		if(Character.toString(newseq.get(2).charAt(1)).equalsIgnoreCase("t"))
		{
			if(Character.toString(newseq.get(4).charAt(1)).equalsIgnoreCase("t"))
			{
			mary=0.70;
			}
			else
			{
				mary=1-0.70;
			}
		}
		else
		{
			if(Character.toString(newseq.get(3).charAt(1)).equalsIgnoreCase("t"))
			{
			mary=0.01;
			}
			else
			{
			mary=1-0.01;
			}
		}
			
			return burg*earth*alarm*john*mary;	
		}
		
		else
		{
			ArrayList<String> str1= new ArrayList<String>();
			str1.addAll(seq);
			ArrayList<String> str2= new ArrayList<String>();
			str2.addAll(seq);
			int flag=0;
			
			for(int i=0;i<seq.size();i++)
			{
				if(Character.toString(seq.get(i).charAt(0)).equalsIgnoreCase("B"))
				{
					flag=1;
				}
		
			}
	
			if(flag==1)
			{
				for(int i=0;i<seq.size();i++)
				{
					if(Character.toString(seq.get(i).charAt(0)).equalsIgnoreCase("E"))
					{
						flag=2;
					}
			
				}
				
				if(flag==2)
				{
					for(int i=0;i<seq.size();i++)
					{
						if(Character.toString(seq.get(i).charAt(0)).equalsIgnoreCase("A"))
						{
							flag=3;
						}
					
					}
				
					if(flag==3)
					{
						for(int i=0;i<seq.size();i++)
						{
							if(Character.toString(seq.get(i).charAt(0)).equalsIgnoreCase("J"))
							{
								flag=4;
							}
							
						}
						
						if(flag==4)
						{
							for(int i=0;i<seq.size();i++)
							{
								if(Character.toString(seq.get(i).charAt(0)).equalsIgnoreCase("M"))
								{
									flag=5;
								}
									
							}
							
							if(flag==5)
							{
								
							}
							else
							{
								str1.add("Mf");
								str2.add("Mt");
								return computeProbability(str2)+computeProbability(str1);
							}
						}
						else
						{
							str1.add("Jf");
							str2.add("Jt");
							return computeProbability(str2)+computeProbability(str1);
						}
					}
					else
					{
						str1.add("Af");
						str2.add("At");
						return computeProbability(str2)+computeProbability(str1);
					}
				}		
				else
				{
					str1.add("Ef");
					str2.add("Et");
					return computeProbability(str2)+computeProbability(str1);
				}
		
			}
			else
			{
				str1.add("Bf");
				str2.add("Bt");
				return computeProbability(str2)+computeProbability(str1);
			}
		}
		return -1;
	}
	
	
	public static void main(String[] args) throws Exception
	{
	
		int givenindex=0;
		boolean given=false;
		Scanner sc=new Scanner(System.in);
		int n=args.length;
		bnet obj=new bnet();
		
		for(int i=0;i<n;i++)
		{
			if(args[i].equalsIgnoreCase("given"))
			{
				given=true;
				givenindex=i;
			}
		}
		
		if(!given)
		{
			ArrayList<String> a = new ArrayList<String>();
			for(int i=0;i<n;i++)
			{
				a.add(args[i]);
			}
			
			System.out.print("Probability of ");
			for(int i=0;i<n;i++)
			{
				System.out.print(args[i]+" ");
			}
			System.out.println("is "+obj.computeProbability(a));
		}
		if(given)
		{
			ArrayList<String> b = new ArrayList<String>();
			for(int i=0;i<givenindex;i++)
			{
				b.add(args[i]);
			}
			ArrayList<String> c = new ArrayList<String>();
			for(int i=givenindex+1;i<n;i++)
			{
				c.add(args[i]);
				b.add(args[i]);
			}
			
			System.out.print("Probability of ");
			for(int i=0;i<n;i++)
			{
				System.out.print(args[i]+" ");
			}
			System.out.println("is "+obj.computeProbability(b)/obj.computeProbability(c));
		}
	
	}
  
}