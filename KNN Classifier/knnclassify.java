import java.util.*;
import java.io.*;

//Quick sort code taken from http://www.newthinktank.com/2013/03/java-quick-sort/ and then tweaked


///////////////////////////////////////////////////////////////////////////////////////////OK
class knnclassify
{
	static double training[][];
	static double testing[][];
	static int train_rows=0;
	static int train_cols=0;
	static int test_rows=0;
	static int test_cols=0;

	static double trainmeanstd[][];
	static double testmeanstd[][];

	static double normalizedtraining[][];
	static double normalizedtesting[][];

	double distance[][];
	double resultd[][];
	
	static double cls[];
	static int class_count=0;
	double count_stats[][];
	

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[][] acceptAndProcessInput(String input_filename,String type)throws IOException
	{
		double data[][];
		int row_count=0;
		int col_count=0;
		int i=0,j;

		FileReader fr1=new FileReader(input_filename);
		Scanner sa1=new Scanner(fr1);

		while(sa1.hasNextLine())
 		{
			col_count=0;
			Scanner sa2 = new Scanner(sa1.nextLine());
			boolean b1;

 			while(b1=sa2.hasNext())
 			{	
 				String str=sa2.next();	
				col_count++;			
 			}
			row_count++;
		}

		if(type.equals("train"))
		{
			train_rows=row_count;
			train_cols=col_count;
		}

		if(type.equals("test"))
		{
			test_rows=row_count;
			test_cols=col_count;
		}
		
		data=new double[row_count][col_count];

		FileReader fr2=new FileReader(input_filename);
		Scanner sb1=new Scanner(fr2);
		while(sb1.hasNextLine())
 		{
			Scanner sb2 = new Scanner(sb1.nextLine());
 			boolean b2;
			j=0;

 			while(b2=sb2.hasNext())
 			{	
 				data[i][j]=Double.parseDouble(sb2.next());
				j++;
 			}
			i++;
		}
		return data;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	

	///////////////////////////////////////////////////////////////////////////////////////////OK
	void extract_classes(double[][] inp_data)
	{
		cls=new double[inp_data.length];
		int j=0;
		class_count=0;
		cls[class_count++]=inp_data[0][inp_data[0].length-1];

		for(int i=0;i<inp_data.length;i++)
		{
			for(j=0;j<class_count;j++)
			{
				if(inp_data[i][inp_data[i].length-1]==cls[j])
				{
					break;
				}
			}
			if(j==class_count)
			{
				cls[class_count++]=inp_data[i][inp_data[i].length-1];
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[] calc_mean_and_std(double[] inpcol)
	{
		double mean=0;
		double ms[]=new double[2];
		
		for(int i=0;i<inpcol.length;i++)
		{
			mean+=inpcol[i];			
		}
		mean/=inpcol.length;
		ms[0]=mean;

		double x=0;
		double y=0;
		for(int i=0;i<inpcol.length;i++)
		{
			x=inpcol[i]-mean;
			x*=x;
			y+=x;
		}

		double std=0;
		std=(1.0/(inpcol.length-1))*y;
		std=Math.sqrt(std);  
		ms[1]=std;

		return ms;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_meanstd(double[][] inp,double[][] resms)
	{
		double xyz[]=new double[inp.length];
		double mnstd[]=new double[2];

		for(int i=0;i<inp[0].length-1;i++)
		{
			for(int j=0;j<inp.length;j++)
			{
				xyz[j]=inp[j][i];
			}

			mnstd=calc_mean_and_std(xyz);
			resms[0][i]=mnstd[0];
			resms[1][i]=mnstd[1];			
		}

	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	void normalizedata(double[][] inp,double[][] normalizedinp,double[][] meanstd)
	{
		for(int j=0;j<(inp[0].length)-1;j++)
		{
			for(int i=0;i<inp.length;i++)
			{
				normalizedinp[i][j]=(inp[i][j]-meanstd[0][j])/meanstd[1][j];
			}
		}
		for(int i=0;i<inp.length;i++)
		{
			normalizedinp[i][normalizedinp[i].length-1]=inp[i][inp[0].length-1];
		}
	
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[][] calcdistance(double[] testrow)
	{
		double tempdist=0;
		distance=new double[normalizedtraining.length][3];

		for(int i=0;i<normalizedtraining.length;i++)
		{
			for(int j=0;j<normalizedtraining[0].length-1;j++)
			{
				tempdist+=((normalizedtraining[i][j]-testrow[j])*(normalizedtraining[i][j]-testrow[j]));
			}

			tempdist=Math.sqrt(tempdist);

			distance[i][0]=tempdist;							//distance
			distance[i][1]=normalizedtraining[i][normalizedtraining[i].length-1];		//class
			distance[i][2]=i;								//row no
		}	
	
		return distance;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	void quicksortdistances(double left,double right,double[][] inparray)
	{
		//System.out.println("left is "+left+" right is "+right);
		if(right-left<=0)
		{
			return; // Everything is sorted
		}
		else
		{
			// It doesn't matter what the pivot is, but it must
			// be a value in the array

			double pivot=inparray[(int)right][0];

			double pivotLocation=partitionArray(left,right,pivot,inparray);

			quicksortdistances(left,pivotLocation-1,inparray); 	// Sorts the left side

			quicksortdistances(pivotLocation+1,right,inparray);
		}	
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	double partitionArray(double left,double right,double pivot,double[][] inparray)
	{
		double leftPointer=left-1;

		double rightPointer=right;

		while(true)
		{
			while(inparray[(int)++leftPointer][0]<pivot)
			{
			}

			while(rightPointer>0 && inparray[(int)--rightPointer][0]>pivot)
			{
			}

			if(leftPointer>=rightPointer)
			{
				break;
			}
			else
			{
				double temp;
				temp=inparray[(int)leftPointer][0];
				inparray[(int)leftPointer][0]=inparray[(int)rightPointer][0];
				inparray[(int)rightPointer][0]=temp;

				temp=inparray[(int)leftPointer][1];
				inparray[(int)leftPointer][1]=inparray[(int)rightPointer][1];
				inparray[(int)rightPointer][1]=temp;
				
				temp=inparray[(int)leftPointer][2];
				inparray[(int)leftPointer][2]=inparray[(int)rightPointer][2];
				inparray[(int)rightPointer][2]=temp;
			}
		}

		double temp;
		temp=inparray[(int)leftPointer][0];
		inparray[(int)leftPointer][0]=inparray[(int)right][0];
		inparray[(int)right][0]=temp;

		temp=inparray[(int)leftPointer][1];
		inparray[(int)leftPointer][1]=inparray[(int)right][1];
		inparray[(int)right][1]=temp;
		
		temp=inparray[(int)leftPointer][2];
		inparray[(int)leftPointer][2]=inparray[(int)right][2];
		inparray[(int)right][2]=temp;
		
		return leftPointer;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_stats(double[][] inp,int limit)
	{
		count_stats=new double[class_count][2];

		for(int j=0;j<class_count;j++)
		{
			count_stats[j][0]=cls[j];
		}

		for(int i=0;i<limit;i++)
		{
			for(int j=0;j<class_count;j++)
			{
				if(inp[i][1]==cls[j])
				{
					count_stats[j][1]++;
				}
			}
		}
			
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK


	///////////////////////////////////////////////////////////////////////////////////////////OK
	void knntesting(int k)
	{
		double totalaccu=0;

		for(int i=0;i<normalizedtesting.length;i++)
		{
			double[] inp_row=new double[normalizedtesting[0].length];
			for(int j=0;j<normalizedtesting[i].length;j++)
			{
				inp_row[j]=normalizedtesting[i][j];
			}

			resultd=calcdistance(inp_row);

			quicksortdistances(0,resultd.length-1,resultd);

			double topkclasses[][]=new double[k][3];

			for(int x=0;x<k;x++)
			{
				topkclasses[x][0]=resultd[x][0];
				topkclasses[x][1]=resultd[x][1];
				topkclasses[x][2]=resultd[x][2];		
			}	

			make_stats(topkclasses,topkclasses.length);

			double maxclass=count_stats[0][0];
			double maxcount=count_stats[0][1];

			for(int x=0;x<class_count;x++)
			{
				if(count_stats[x][1]>maxcount)
				{
					maxcount=count_stats[x][1];
					maxclass=count_stats[x][0];
				}
			}
					
			double maxrow=-1;
			double maxdist=-999;

			for(int x=0;x<k;x++)
			{
				if(topkclasses[x][1]==maxclass)
				{
					maxdist=topkclasses[x][0];
					maxrow=topkclasses[x][2];
					break;
				}
			}

			double tiecount=0;
			int truetie=0;

			for(int x=0;x<class_count;x++)
			{
				if(count_stats[x][1]==maxcount)
				{
					tiecount++;
					if(count_stats[x][0]==normalizedtesting[i][normalizedtesting[i].length-1])
					{
						truetie=1;
					}
				}
			}

			double accu=0;

			if(tiecount==1 && maxclass==normalizedtesting[i][normalizedtesting[i].length-1])
			{
				accu=1;
			}
			if(tiecount==1 && maxclass!=normalizedtesting[i][normalizedtesting[i].length-1])
			{
				accu=0;
			}
			if(tiecount>1 && truetie==1)
			{
				accu=1/tiecount;
			}
			if(tiecount>1 && truetie==0)
			{
				accu=0;
			}

			totalaccu+=accu;

			System.out.print("ID="+i+", predicted="+maxclass+", true="+normalizedtesting[i][normalizedtesting[i].length-1]);
			System.out.println(", nn="+maxrow+", distance="+maxdist+", accuracy="+accu);

		}
		System.out.println("classification accuracy="+totalaccu/normalizedtesting.length);
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK	

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	public static void main(String args[])throws Exception
	{

		if(args.length!=3)
		{
			System.out.println("Invalid input arguments. Exiting program...");
			System.exit(0);
		}
		else	//valid input
		{
			String train=args[0];
			String test=args[1];
			int k=Integer.parseInt(args[2]);
			
			knnclassify obj=new knnclassify();
			System.out.println("Reading data...");
			training=obj.acceptAndProcessInput(train,"train");
			obj.extract_classes(training);
		
			if(k<1 || k>train_rows)
			{
				System.out.println("Invalid value of 'k'. Exiting program...");
				System.exit(0);
			}
			else
			{
				System.out.println("Training...");
				trainmeanstd=new double[2][train_cols-1];
				obj.make_meanstd(training,trainmeanstd);

				normalizedtraining=new double[train_rows][train_cols];
				obj.normalizedata(training,normalizedtraining,trainmeanstd);
			
				testing=obj.acceptAndProcessInput(test,"test");
				testmeanstd=new double[2][test_cols-1];
				obj.make_meanstd(testing,testmeanstd);
				System.out.println();
				normalizedtesting=new double[test_rows][test_cols];
				obj.normalizedata(testing,normalizedtesting,testmeanstd);
				obj.knntesting(k);
				
			}
			
		}
	}//psvm
	///////////////////////////////////////////////////////////////////////////////////////////OK
}//class knnclassify
///////////////////////////////////////////////////////////////////////////////////////////OK