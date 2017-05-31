import java.util.*;
import java.io.*;

/////////////////////
//Used minimum and maximum in no more than 3n/2 comparisons from CLRS 9.1 pg214 to speed up min-max calculation as compared to 
//traditional 2n comparisons
/////////////////////

///////////////////////////////////////////////////////////////////////////////////////////OK
class naive_bayes
{
	static double training[][];
	static double testing[][];
	static int train_rows=0;
	static int train_cols=0;
	static int test_rows=0;
	static int test_cols=0;

	static double cls[];
	static int class_count=0;
	double count_stats[];

	double bin[][];
	double bin_stats[][];
	double minmax[][];
	double imp_binstats[][];
	double sorted_cls[];
	double sorted_imp_binstats[][];
	double gold[][];
	int goldindx;

	double sorted_training[][];
	double gauss_stats[][];
	int gauss_indx;
	

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
	void print_data(String type)
	{
		int r=0,c=0;
		
		if(type.equals("train"))
		{
			r=train_rows;
			c=train_cols;
		}
		if(type.equals("test"))
		{
			r=test_rows;
			c=test_cols;
		}
		
		for(int i=0;i<r;i++)
		{
			for(int j=0;j<c;j++)
			{
				if(type.equals("train"))
				{
					System.out.print(training[i][j]+" ");
				}
				if(type.equals("test"))
				{
					System.out.print(testing[i][j]+" ");
				}
			}
			System.out.println();
		}
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
	double[] getminmax(double[] inpcol)
	{
		double mins[]=new double[(inpcol.length/2)+1];
		double maxs[]=new double[(inpcol.length/2)+1];
		int minindx=0;
		int maxindx=0;

		if(inpcol.length%2==0)
		{
			for(int i=0;i<inpcol.length;i+=2)
			{
				if(inpcol[i]<inpcol[i+1])
				{
					mins[minindx++]=inpcol[i];
					maxs[maxindx++]=inpcol[i+1];
				}
				else
				{
					maxs[maxindx++]=inpcol[i];
					mins[minindx++]=inpcol[i+1];
				}
			}
		}

		if(inpcol.length%2!=0)
		{
			for(int i=0;i<(inpcol.length)-1;i+=2)
			{
				if(inpcol[i]<inpcol[i+1])
				{
					mins[minindx++]=inpcol[i];
					maxs[maxindx++]=inpcol[i+1];
				}
				else
				{
					maxs[maxindx++]=inpcol[i];
					mins[minindx++]=inpcol[i+1];
				}
			}
			mins[minindx++]=inpcol[inpcol.length-1];
			maxs[maxindx++]=inpcol[inpcol.length-1];
		}

		double tempmin=mins[0];
		double tempmax=maxs[0];

		for(int i=1;i<minindx;i++)
		{
			if(mins[i]<tempmin)
			{
				tempmin=mins[i];
			}
		}
		for(int i=1;i<maxindx;i++)
		{
			if(maxs[i]>tempmax)
			{
				tempmax=maxs[i];
			}
		}
			
		double minmax[]=new double[2];
		minmax[0]=tempmin;
		minmax[1]=tempmax;

		return minmax;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_minmax()
	{
		minmax=new double[2][train_cols-1];
		double xyz[]=new double[training.length];
		double ans[]=new double[2];
		for(int i=0;i<train_cols-1;i++)
		{
			for(int j=0;j<training.length;j++)
			{
				xyz[j]=training[j][i];
			}
			ans=getminmax(xyz);
			minmax[0][i]=ans[0];
			minmax[1][i]=ans[1];
		}

	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	

	///////////////////////////////////////////////////////////////////////////////////////////OK
	void fill_bins(double bin_size)
	{
		bin=new double[(int)bin_size][train_cols-1];
		bin_stats=new double[(int)bin_size][class_count];
		imp_binstats=new double[train_rows][train_cols];

		double low;
		double high;
		double g;
		double brow;	
		for(int i=0;i<train_rows;i++)
		{
			for(int j=0;j<train_cols-1;j++)
			{
				//System.out.println("j is "+j);
				g=(minmax[1][j]-minmax[0][j])/bin_size;
				low=-1;
				high=minmax[0][j]+g;
				brow=0;

				while(!(training[i][j]>low && training[i][j]<=high))
				{
					low=high;
					high+=g;
					brow++;					
				}
				if(brow==bin_size)
				brow--;
				bin[(int)brow][j]++;

				imp_binstats[i][j]=brow;

				for(int k=0;k<class_count;k++)
				{
					if(cls[k]==training[i][train_cols-1])
					{
						bin_stats[(int)brow][k]++;
					}
				}

			}
			imp_binstats[i][train_cols-1]=training[i][train_cols-1];
		}
						
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void sort_imp_binstats()
	{
		sorted_cls=new double[class_count];
		double temp;
		sorted_imp_binstats=new double[train_rows][train_cols];

		for(int i=0;i<class_count;i++)
		{
			sorted_cls[i]=cls[i];
		}
		for(int i=0;i<class_count;i++)
		{
			for(int j=0;j<class_count;j++)
			{
				if(sorted_cls[i]<sorted_cls[j])
				{
					temp=sorted_cls[i];
					sorted_cls[i]=sorted_cls[j];
					sorted_cls[j]=temp;
				}
			}
		}

		int x=0;
		for(int k=0;k<class_count;k++)
		{
			for(int i=0;i<train_rows;i++)
			{	
				if(imp_binstats[i][train_cols-1]==sorted_cls[k])
				{
					for(int j=0;j<train_cols-1;j++)
					{
						sorted_imp_binstats[x][j]=imp_binstats[i][j];
					}
					sorted_imp_binstats[x][train_cols-1]=sorted_cls[k];
					x++;
				}
				
			}
		}

	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void print_imp_binstats(double bin_size)
	{
		double unique_bins[];
		double temp_bins[];
		int count=0;
		gold=new double[(int)bin_size*(train_cols-1)*class_count][4];
		goldindx=0;
		for(int k=0;k<class_count;k++)
		{
			for(int j=0;j<train_cols-1;j++)
			{
				int i=0;
				int x=0;
				int indx=0;
				unique_bins=new double[sorted_imp_binstats.length];
				temp_bins=new double[sorted_imp_binstats.length];
				while(sorted_imp_binstats[i][train_cols-1]!=sorted_cls[k])
				{
					i++;
				}
				unique_bins[x++]=sorted_imp_binstats[i][j];
				
				while(sorted_imp_binstats[i][train_cols-1]==sorted_cls[k])
				{	
					
					temp_bins[indx++]=sorted_imp_binstats[i][j];
					int exists=0;
					for(int y=0;y<x;y++)
					{
						if(sorted_imp_binstats[i][j]==unique_bins[y])
						{
							exists=1;
							break;
						}
					}
					if(exists==0)
					{
						unique_bins[x++]=sorted_imp_binstats[i][j];
					}
					i++;
					if(i==sorted_imp_binstats.length)
					{
						break;
					}
				}
				
				double temp;
				
				for(int outer=0;outer<x;outer++)
				{
					for(int inner=0;inner<x;inner++)
					{
						if(unique_bins[outer]<unique_bins[inner])
						{
							temp=unique_bins[outer];
							unique_bins[outer]=unique_bins[inner];
							unique_bins[inner]=temp;
						}
					}
				}
				for(int outer=0;outer<indx;outer++)
				{
					for(int inner=0;inner<indx;inner++)
					{
						if(temp_bins[outer]<temp_bins[inner])
						{
							temp=temp_bins[outer];
							temp_bins[outer]=temp_bins[inner];
							temp_bins[inner]=temp;
						}
					}
				}

				double count_of_bins[]=new double[x];

				for(int yo=0;yo<x;yo++)
				{					
					for(int oy=0;oy<indx;oy++)
					{
						if(temp_bins[oy]==unique_bins[yo])
						{
							count_of_bins[yo]++;
						}
					}
					
					System.out.println("Class "+sorted_cls[k]+", attribute "+j+", bin "+unique_bins[yo]+", P(bin|class) = "+count_of_bins[yo]/(double)indx);
					gold[goldindx][0]=sorted_cls[k];
					gold[goldindx][1]=j;
					gold[goldindx][2]=unique_bins[yo];
					gold[goldindx][3]=count_of_bins[yo]/(double)indx;
					goldindx++;

				}
			}
		}			
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofx(double[] x,double bin_size)
	{
		double adder=0;
		for(int k=0;k<class_count;k++)
		{
			adder+=calc_pofxgivenc(x,cls[k],bin_size)*(count_stats[k]/(double)train_rows);
		}
		return adder;
			
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofxgivenc(double[] x,double c,double bin_size)
	{
		double low;
		double high;
		double g;
		double brow;
		double multi=1;
		double pofxigivenc=1;
		boolean failsafe=false;

		for(int i=0;i<x.length;i++)
		{
			multi=1;
			g=(minmax[1][i]-minmax[0][i])/bin_size;
			low=-1;
			high=minmax[0][i]+g;
			brow=0;
			
			while(!(x[i]>low && x[i]<=high))
			{
				low=high;
				high+=g;
				brow++;					
			}
			if(brow==bin_size)
			brow--;

			for(int y=0;y<goldindx;y++)
			{
				if(gold[y][0]==c && gold[y][1]==i && gold[y][2]==brow)
				{
					failsafe=true;
					multi*=gold[y][3];
				}
				
			}
			pofxigivenc*=multi;

		}
		if(failsafe==false)
		return 0;
		else
		return pofxigivenc;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK	


	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofcgivenx(double[] x,double c,double bin_size)
	{	
		double pofc=0;
		for(int k=0;k<class_count;k++)
		{
			if(c==cls[k])
			{
				pofc=count_stats[k];
			}
		}

		pofc=pofc/(double)train_rows;

		double numerator=calc_pofxgivenc(x,c,bin_size)*pofc;
		double denominator=calc_pofx(x,bin_size);
		return numerator/denominator;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_stats(double[] inp,int limit)
	{
		count_stats=new double[class_count];

		for(int i=0;i<limit;i++)
		{
			for(int j=0;j<class_count;j++)
			{
				if(inp[i]==cls[j])
				{
					count_stats[j]++;
				}
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void histogramTesting(double[][] test,double bin_size)
	{
		double x[];
		double result_probs[]=new double[class_count];
		double ties=0;
		int truetie=0;
		double accu=0;	
		double totalaccu=0;

		for(int i=0;i<test.length;i++)
		{
			accu=0;
			ties=0;
			x=new double[test_cols-1];
			truetie=0;
			for(int j=0;j<test_cols-1;j++)
			{
				x[j]=test[i][j];
			}
			double argmax=-1;
			double maxclass=0;
			for(int k=0;k<class_count;k++)
			{
				double dummy=calc_pofcgivenx(x,cls[k],bin_size);
				
				if(dummy>argmax)
				{
					argmax=dummy;
					maxclass=cls[k];
				}
				result_probs[k]=dummy;
			}
			for(int k=0;k<class_count;k++)
			{
				if(result_probs[k]==argmax)
				{
					ties++;
				
					if(cls[k]==test[i][test_cols-1])
					{
						truetie=1;
					}
				}
			}
		
			if(ties==1 && test[i][test_cols-1]==maxclass)
			{
				accu=1;
			}
			if(ties==1 && test[i][test_cols-1]!=maxclass)
			{
				accu=0;
			}
			if(ties>1 && truetie==1)
			{
				accu=1/(ties);
			}
			if(ties>1 && truetie==0)
			{
				accu=0;
			}
			totalaccu+=accu;
			System.out.println("ID="+i+", predicted="+maxclass+", probability="+argmax+", true="+test[i][test_cols-1]+", accuracy="+accu);	
		}
		System.out.println("classification accuracy="+totalaccu/(double)test_rows);	
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	/////////////////////////////////////////////GAUSSIANS///////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////OK
	void sort_training(double[][] training)
	{
		sorted_cls=new double[class_count];
		double temp;
		sorted_training=new double[train_rows][train_cols];

		for(int i=0;i<class_count;i++)
		{
			sorted_cls[i]=cls[i];
		}

		for(int k=0;k<class_count;k++)
		{
			for(int l=0;l<class_count;l++)
			{
				if(sorted_cls[k]<sorted_cls[l])
				{
					temp=sorted_cls[k];
					sorted_cls[k]=sorted_cls[l];
					sorted_cls[l]=temp;
				}
			}
		}

		int x=0;
		
		for(int k=0;k<class_count;k++)
		{
			for(int i=0;i<train_rows;i++)
			{	
				if(training[i][train_cols-1]==sorted_cls[k])
				{
					for(int j=0;j<train_cols-1;j++)
					{
						sorted_training[x][j]=training[i][j];
					}
					sorted_training[x][train_cols-1]=sorted_cls[k];
					x++;
				}
				
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_gaussstats()throws Exception
	{
		gauss_stats=new double[sorted_cls.length*(train_cols-1)][4];
		double temp_bins[];
		double meanstd[]=new double[2];
		gauss_indx=0;
		
		for(int k=0;k<sorted_cls.length;k++)
		{
			for(int j=0;j<train_cols-1;j++)
			{
				int i=0;
				int indx=0;
				temp_bins=new double[sorted_training.length];
				while(sorted_training[i][train_cols-1]!=sorted_cls[k])
				{
					i++;
				}
				while(sorted_training[i][train_cols-1]==sorted_cls[k])
				{	
					temp_bins[indx++]=sorted_training[i][j];
					i++;
					if(i==sorted_training.length)
					{
						break;
					}
				}
				
				meanstd=calc_mean_and_std(temp_bins,indx);
				gauss_stats[gauss_indx][0]=sorted_cls[k];
				gauss_stats[gauss_indx][1]=(double)j;
				gauss_stats[gauss_indx][2]=meanstd[0];
				gauss_stats[gauss_indx][3]=meanstd[1];
				System.out.println("Class "+gauss_stats[gauss_indx][0]+", attribute "+gauss_stats[gauss_indx][1]+", mean = "+gauss_stats[gauss_indx][2]+", std = "+gauss_stats[gauss_indx][3]);
				gauss_indx++;
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[] calc_mean_and_std(double[] temp_bins, int indx)
	{
		double mean=0;
		double meanstd[]=new double[2];
		
		for(int i=0;i<indx;i++)
		{
			mean+=temp_bins[i];			
		}
		mean/=indx;
		meanstd[0]=mean;
		
		double x=0;
		double y=0;
		for(int i=0;i<indx;i++)
		{
			x=temp_bins[i]-mean;
			x*=x;
			y+=x;
		}
		double std=0;
		std=(1.0/(indx-1))*y;
		std=Math.sqrt(std);  
		meanstd[1]=std;
		
		return meanstd;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofxgivencgauss(double[] x,double class1)
	{
		double mean=0;
		double std=0;
		double nx=1;
		double numer,deno,tempnx;
		
		for(int j=0;j<x.length;j++)
		{
			for(int g=0;g<gauss_indx;g++)
			{
				if(class1==gauss_stats[g][0] && j==gauss_stats[g][1])
				{
					mean=gauss_stats[g][2];
					std=gauss_stats[g][3];
					break;
				}
			}
			
			numer=Math.exp(-((x[j]-mean)*(x[j]-mean))/(2.0*std*std));
			deno=std*Math.sqrt(2.0*3.14);
			tempnx=numer/deno;
			nx*=tempnx;
		}
		return nx;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofcgivenxgauss(double[] x,double c)
	{
		double pofc=0;
		
		for(int k=0;k<class_count;k++)
		{
			if(c==cls[k])
			{
				pofc=count_stats[k];
			}
		}
	
		pofc=pofc/(double)train_rows;
		double numerator=calc_pofxgivencgauss(x,c)*pofc;
		double denominator=calc_pofxgauss(x);
		return numerator/denominator;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_pofxgauss(double[] x)
	{
		double adder=0;
		for(int k=0;k<class_count;k++)
		{
			adder+=calc_pofxgivencgauss(x,cls[k])*(count_stats[k]/(double)train_rows);
		}
		return adder;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void gaussianTesting(double[][] test)
	{
		double x[];
		double result_probs[]=new double[class_count];
		double ties=0;
		int truetie=0;
		double accu=0;	
		double totalaccu=0;
		double dummy;

		for(int i=0;i<test.length;i++)
		{
			accu=0;
			ties=0;
			x=new double[test_cols-1];
			truetie=0;
			
			for(int j=0;j<test_cols-1;j++)
			{
				x[j]=test[i][j];
			}
			double argmax=-1;
			double maxclass=0;
			for(int k=0;k<class_count;k++)
			{
				dummy=calc_pofcgivenxgauss(x,cls[k]);
				
				if(dummy>argmax)
				{
					argmax=dummy;
					maxclass=cls[k];
				}
				result_probs[k]=dummy;
			}
			for(int k=0;k<class_count;k++)
			{
				if(result_probs[k]==argmax)
				{
					ties++;
				
					if(cls[k]==test[i][test_cols-1])
					{
						truetie=1;
					}
				}
			}
		
			if(ties==1 && test[i][test_cols-1]==maxclass)
			{
				accu=1;
			}
			if(ties==1 && test[i][test_cols-1]!=maxclass)
			{
				accu=0;
			}
			if(ties>1 && truetie==1)
			{
				accu=1/(ties);
			}
			if(ties>1 && truetie==0)
			{
				accu=0;
			}
			totalaccu+=accu;
			System.out.println("ID="+i+", predicted="+maxclass+", probability="+argmax+", true="+test[i][test_cols-1]+", accuracy="+accu);	
		}
		System.out.println("classification accuracy="+totalaccu/(double)test_rows);		
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void make_mixturestats()throws Exception
	{
		gauss_stats=new double[sorted_cls.length*(train_cols-1)][4];
		double temp_bins[];
		double meanstd[]=new double[2];
		gauss_indx=0;
		
		for(int k=0;k<sorted_cls.length;k++)
		{
			
			for(int j=0;j<train_cols-1;j++)
			{
				int i=0;
				int indx=0;
				temp_bins=new double[sorted_training.length];
				while(sorted_training[i][train_cols-1]!=sorted_cls[k])
				{
					i++;
				}
				while(sorted_training[i][train_cols-1]==sorted_cls[k])
				{	
					temp_bins[indx++]=sorted_training[i][j];
					i++;
					if(i==sorted_training.length)
					{
						break;
					}
				}
				
				meanstd=calc_mean_and_std(temp_bins,indx);
				gauss_stats[gauss_indx][0]=sorted_cls[k];
				gauss_stats[gauss_indx][1]=(double)j;
				gauss_stats[gauss_indx][2]=meanstd[0];
				gauss_stats[gauss_indx][3]=meanstd[1];
				
				System.out.println("Class "+gauss_stats[gauss_indx][0]+", attribute "+gauss_stats[gauss_indx][1]+", Gaussian "+gauss_stats[gauss_indx][3]/(gauss_stats[gauss_indx][3]+1)+" mean = "+gauss_stats[gauss_indx][2]+", std = "+gauss_stats[gauss_indx][3]);
				gauss_indx++;
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	void mixtureTesting(double[][] test)
	{
		double x[];
		double result_probs[]=new double[class_count];
		double ties=0;
		int truetie=0;
		double accu=0;	
		double totalaccu=0;
		double dummy;

		for(int i=0;i<test.length;i++)
		{
			accu=0;
			ties=0;
			x=new double[test_cols-1];
			truetie=0;
			
			for(int j=0;j<test_cols-1;j++)
			{
				x[j]=test[i][j];
			}
			double argmax=-1;
			double maxclass=0;
			for(int k=0;k<class_count;k++)
			{
				dummy=calc_pofcgivenxgauss(x,cls[k]);
				
				if(dummy>argmax)
				{
					argmax=dummy;
					maxclass=cls[k];
				}
				result_probs[k]=dummy;
			}
			for(int k=0;k<class_count;k++)
			{
				if(result_probs[k]==argmax)
				{
					ties++;
				
					if(cls[k]==test[i][test_cols-1])
					{
						truetie=1;
					}
				}
			}
		
			if(ties==1 && test[i][test_cols-1]==maxclass)
			{
				accu=1;
			}
			if(ties==1 && test[i][test_cols-1]!=maxclass)
			{
				accu=0;
			}
			if(ties>1 && truetie==1)
			{
				accu=1/(ties);
			}
			if(ties>1 && truetie==0)
			{
				accu=0;
			}
			totalaccu+=accu;
			System.out.println("ID="+i+", predicted="+maxclass+", probability="+argmax+", true="+test[i][test_cols-1]+", accuracy="+accu);	
		}
		System.out.println("classification accuracy="+totalaccu/(double)test_rows);		
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK
	
	
	///////////////////////////////////////////////////////////////////////////////////////////OK
	public static void main(String args[])throws Exception
	{
		String train=args[0];
		String test=args[1];

		if((!args[2].equalsIgnoreCase("histograms")) && (!args[2].equalsIgnoreCase("gaussians")) && (!args[2].equalsIgnoreCase("mixtures")))
		{
			System.out.println("Invalid option. Exiting program...");
			System.exit(0);
		}
		else	//valid options
		{
			if((args[2].equalsIgnoreCase("histograms")))
			{
				if(args.length!=4)
				{
					System.out.println("Invalid option. Exiting program...");
					System.exit(0);
				}
				else
				{
					naive_bayes obj=new naive_bayes();
					System.out.println("Reading data...");
					training=obj.acceptAndProcessInput(train,"train");
					obj.extract_classes(training);
					double xyz[]=new double[training.length];
					
					for(int j=0;j<training.length;j++)
					{
						xyz[j]=training[j][train_cols-1];
					}
					obj.make_stats(xyz,xyz.length);
					System.out.println("Training...");
					String bin_size=args[3];
					obj.make_minmax();
					obj.fill_bins(Double.parseDouble(bin_size));
					obj.sort_imp_binstats();
					obj.print_imp_binstats(Double.parseDouble(bin_size));
					testing=obj.acceptAndProcessInput(test,"test");
					obj.histogramTesting(testing,Double.parseDouble(bin_size));

				}
			}
			if((args[2].equalsIgnoreCase("mixtures")))
			{
				if(args.length!=4)
				{
					System.out.println("Invalid option. Exiting program...");
					System.exit(0);
				}
				else
				{
					naive_bayes obj=new naive_bayes();
					System.out.println("Reading data...");
					training=obj.acceptAndProcessInput(train,"train");
					obj.extract_classes(training);
					double xyz[]=new double[training.length];
					
					for(int j=0;j<training.length;j++)
					{
						xyz[j]=training[j][train_cols-1];
					}
					obj.make_stats(xyz,xyz.length);
					obj.sort_training(training);
					System.out.println("Training...");
					obj.make_mixturestats();
					testing=obj.acceptAndProcessInput(test,"test");
					obj.mixtureTesting(testing);
				}
			}
			if((args[2].equalsIgnoreCase("gaussians")))
			{				
				naive_bayes obj=new naive_bayes();
				System.out.println("Reading data...");
				training=obj.acceptAndProcessInput(train,"train");
				obj.extract_classes(training);
				double xyz[]=new double[training.length];
					
				for(int j=0;j<training.length;j++)
				{
					xyz[j]=training[j][train_cols-1];
				}
				obj.make_stats(xyz,xyz.length);
				obj.sort_training(training);
				System.out.println("Training...");
				obj.make_gaussstats();
				testing=obj.acceptAndProcessInput(test,"test");
				obj.gaussianTesting(testing);
			}
		}
	}//psvm
	///////////////////////////////////////////////////////////////////////////////////////////OK
}//class naive_bayes
///////////////////////////////////////////////////////////////////////////////////////////OK