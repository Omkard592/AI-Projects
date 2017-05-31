import java.util.*;
import java.io.*;

/////////////////////
//Binary tree implementation taken from http://stackoverflow.com/questions/20731833/constructing-a-binary-tree-in-java
//Also used minimum and maximum in no more than 3n/2 comparisons from CLRS 9.1 pg214 to speed up min-max calculation as compared to 
//traditional 2n comparisons
//Queue implementation taken from http://www.tutorialspoint.com/javaexamples/data_queue.htm
//Breadth First traversal taken from http://stackoverflow.com/questions/5262308/how-do-implement-a-breadth-first-traversal
/////////////////////

///////////////////////////////////////////////////////////////////////////////////////////OK
class dtree
{
	static double training[][];
	static double testing[][];
	static int train_rows=0;
	static int train_cols=0;
	static int test_rows=0;
	static int test_cols=0;

	static double cls[];
	static int class_count=0;
	static double count_stats[];

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
	static double[][] distribution(double[][] inp_eg)
	{

		double dist[][]=new double[class_count][2];
		
		for(int i=0;i<class_count;i++)
		{
			dist[i][0]=cls[i];
		}

		int dist_total=0;
		for(int i=0;i<inp_eg.length;i++)
		{
			for(int j=0;j<class_count;j++)
			{	
				if(inp_eg[i][inp_eg[0].length-1]==cls[j])
				{
					dist[j][1]++;
					dist_total++;
				}	
			}	
		}

		for(int j=0;j<class_count;j++)
		{
			dist[j][1]=dist[j][1]/dist_total;
		}

		return dist;		
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	Binarytree dtl(int treeID,double nodeID,double inp_data[][],double[][] defaultclass,String option)
	{

		if(inp_data.length==0)		//examples empty
		{
			Binarytree leaf=new Binarytree(treeID,nodeID,-1,-1,-1,defaultclass,inp_data);
			return leaf;
		}
		else 
		{
			boolean allsamelabel=false;

			double compareclass=inp_data[0][inp_data[0].length-1];
			for(int i=0;i<inp_data.length;i++)
			{
				if(inp_data[i][inp_data[0].length-1]==compareclass)
				{
					allsamelabel=true;
				}
				else
				{
					allsamelabel=false;
					break;
				}
			}
				
			if(allsamelabel)		//all examples with same label
			{
				Binarytree leaf=new Binarytree(treeID,nodeID,-1,-1,-1,defaultclass,inp_data);
				return leaf;
			}
			else		//otherwise
			{
				double best[]=new double[3];
				best=choose_attribute(inp_data,option);			//0att 1thresh 2gain

				double[][] parent_dist=new double[class_count][2];
				parent_dist=distribution(inp_data);
				Binarytree parent=new Binarytree(treeID,nodeID,best[0],best[1],best[2],parent_dist,inp_data);

				double temp_left_data[][]=new double[inp_data.length][inp_data[0].length];
				double temp_right_data[][]=new double[inp_data.length][inp_data[0].length];
				int temp_left_indx=0;
				int temp_right_indx=0;

				for(int i=0;i<inp_data.length;i++)
				{
					if(inp_data[i][(int)best[0]]<best[1])
					{
						for(int j=0;j<inp_data[i].length;j++)
						{
							temp_left_data[temp_left_indx][j]=inp_data[i][j];
						}
						temp_left_indx++;
					}
					else
					{
						for(int j=0;j<inp_data[i].length;j++)
						{
							temp_right_data[temp_right_indx][j]=inp_data[i][j];
						}
						temp_right_indx++;
					}
				}

				double left_data[][]=new double[temp_left_indx][temp_left_data[0].length];
				double right_data[][]=new double[temp_right_indx][temp_right_data[0].length];

				for(int i=0;i<temp_left_indx;i++)
				{
					for(int j=0;j<temp_left_data[i].length;j++)
					{
						left_data[i][j]=temp_left_data[i][j];
					}
				}
				for(int i=0;i<temp_right_indx;i++)
				{
					for(int j=0;j<temp_right_data[i].length;j++)
					{
						right_data[i][j]=temp_right_data[i][j];	
					}
				}

				/////////////////////////////////////////////////////////////////////pruning
				if(left_data.length>=50 && right_data.length>=50)
				{
					Binarytree lefttree=dtl(treeID,2*nodeID,left_data,parent_dist,option);
					parent.add(parent.root,lefttree.root,"left");
					Binarytree righttree=dtl(treeID,(2*nodeID)+1,right_data,parent_dist,option);
					parent.add(parent.root,righttree.root,"right");
				}

				else
				{
					parent=new Binarytree(treeID,nodeID,-1,-1,-1,defaultclass,inp_data);
					
				}
				/////////////////////////////////////////////////////////////////////pruning
				return parent;
			}//inner else

		}//outer else			
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[] choose_attribute(double[][] inp,String option)
	{
		double bestatt=-1;
		double bestthresh=-1;
		double maxgain=-1;
		int i=0;
		int looplimit=0;
	
		if(option.equalsIgnoreCase("optimized"))
		{
			i=0;
			looplimit=inp[0].length-2;
		}
		if(option.equalsIgnoreCase("randomized"))
		{
			int randomchoice=0;
			Random r=new Random();
			randomchoice=r.nextInt(train_cols-1);
			i=randomchoice;
			looplimit=randomchoice;
		}

		for(;i<=looplimit;i++)
		{
			double xyz[]=new double[inp.length];

			for(int j=0;j<inp.length;j++)
			{
				xyz[j]=inp[j][i];
			}

			double ans[]=new double[2];
			ans=getminmax(xyz);

			for(int k=1;k<=50;k++)
			{
				double threshold=ans[0]+(k*(ans[1]-ans[0]))/51;
				double ig=calc_information_gain(inp,i,threshold);

				if(ig>maxgain)
				{
					maxgain=ig;
					bestatt=i;
					bestthresh=threshold;
				}
			}
		}
		
		double best[]=new double[3];
		best[0]=bestatt;
		best[1]=bestthresh;
		best[2]=maxgain;
		return best;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_information_gain(double[][] inp_data,int colnum,double thresh)
	{
		int parent_indexes[]=new int[inp_data.length];
		int left_indexes[]=new int[inp_data.length];
		int right_indexes[]=new int[inp_data.length];
		int li=0,ri=0;

		double parent_data[]=new double[inp_data.length];
	
		for(int i=0;i<inp_data.length;i++)
		{
			parent_data[i]=inp_data[i][inp_data[i].length-1];
		} 
	
		make_stats(parent_data,parent_data.length);

		double parent_weight=0;
		for(int i=0;i<class_count;i++)
		{
			parent_weight+=count_stats[i];
		}
		
		double parent_entropy=calc_entropy(count_stats,parent_weight);	

		double left_data[]=new double[inp_data.length];
		double right_data[]=new double[inp_data.length];
		
		for(int i=0;i<inp_data.length;i++)
		{
			if(inp_data[i][colnum]<thresh)
			{
				left_data[li++]=inp_data[i][inp_data[i].length-1];
			}
			else
			{
				right_data[ri++]=inp_data[i][inp_data[i].length-1];
			}
		}

		make_stats(left_data,li);

		double left_weight=0;
		for(int i=0;i<class_count;i++)
		{
			left_weight+=count_stats[i];
		}

		double left_entropy=calc_entropy(count_stats,left_weight);

		make_stats(right_data,ri);

		double right_weight=0;
		for(int i=0;i<class_count;i++)
		{
			right_weight+=count_stats[i];
		}
		
		double right_entropy=calc_entropy(count_stats,right_weight);

		double information_gain=(parent_entropy)-((left_weight/parent_weight)*left_entropy)-((right_weight/parent_weight)*right_entropy);

		return information_gain;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double calc_entropy(double[] stats,double weight)
	{
		double result=0;

		for(int i=0;i<class_count;i++)
		{
			if(stats[i]==0)
			{
				continue;
			}
			result=result+(-stats[i]/weight)*((Math.log(stats[i]/weight))/Math.log(2));
		}

		return result;
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
	void printTree(Node inptree)
	{
		GenQueue<Node> dtltree=new GenQueue<Node>();
   		if(inptree==null)
		{
        		return;  
		}
    				
		dtltree.enqueue(inptree);
      		
		while(dtltree.hasItems()) 
		{
        		Node temp=dtltree.dequeue();
        		System.out.println("tree="+temp.treeId+", node="+temp.nodeId+", feature="+temp.featureId+", thr="+temp.threshold+", gain="+temp.informationgain);
			if(temp.left!=null)
			{
				dtltree.enqueue(temp.left);
			}
       			if(temp.right!=null)
			{
				dtltree.enqueue(temp.right);
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double testing_Phase(int objID,double[][] dist_prob,double true_class)
	{
		double[] test_output=getResultClass(dist_prob,true_class);		//0best_class 1tieflag 2truetie 3tiecount;
		double accu=-99;

		if(test_output[1]==0 && test_output[0]==true_class)
		{
			accu=1;
		}
		else if(test_output[1]==0 && test_output[0]!=true_class)
		{
			accu=0;
		}
		else if(test_output[1]==1 && test_output[2]==1)
		{
			accu=1/test_output[3];
		}
		if(test_output[1]==1 && test_output[2]==0)
		{
			accu=0;
		}
		
		System.out.println("ID="+objID+", predicted="+test_output[0]+", true="+true_class+", accuracy="+accu);
		
		return accu;
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[][] traverseTree(double[] inp_row,Node treeroot)
	{
		while(treeroot.featureId!=-1)
		{
			double attribute=treeroot.featureId;
			double threshold=treeroot.threshold;
			if(inp_row[(int)attribute]<threshold)
			{
				treeroot=treeroot.left;
			}
			else
			{
				treeroot=treeroot.right;
			}
		}
		
		double[][] dist_prob=treeroot.distribution;

		return dist_prob;	
	}
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	double[] getResultClass(double[][] dist_prob,double true_class)
	{
		double max_prob=dist_prob[0][1];
		double best_class=dist_prob[0][0];
		double tieflag=0;
		double truetie=0;
		double tiecount=0;

		for(int i=1;i<dist_prob.length;i++)
		{
			if(dist_prob[i][1]>max_prob)
			{
				max_prob=dist_prob[i][1];
				best_class=dist_prob[i][0];
			}
		}

		for(int i=0;i<dist_prob.length;i++)
		{
			if(dist_prob[i][1]==max_prob)
			{
				tieflag=1;
				tiecount++;
				if(dist_prob[i][0]==true_class)
				{
					truetie=1;
				}
			}
		}

		double[] test_result=new double[4];
		test_result[0]=best_class;
		test_result[1]=tieflag;
		test_result[2]=truetie;
		test_result[3]=tiecount;
	
		return test_result;
	}		
	///////////////////////////////////////////////////////////////////////////////////////////OK

	///////////////////////////////////////////////////////////////////////////////////////////OK
	public static void main(String args[])throws IOException
	{

		if((!args[2].equalsIgnoreCase("optimized")) && (!args[2].equalsIgnoreCase("randomized")) && (!args[2].equalsIgnoreCase("forest3")) && (!args[2].equalsIgnoreCase("forest15")))
		{
			System.out.println("Invalid option. Exiting program...");
			System.exit(0);
		}
		else	//valid options
		{
			String train=args[0];
			String test=args[1];
		
			if(args[2].equalsIgnoreCase("optimized") || args[2].equalsIgnoreCase("randomized"))
			{
				dtree obj=new dtree();
				System.out.println("Reading data...");
				training=obj.acceptAndProcessInput(train,"train");
				obj.extract_classes(training);

				double init_dist[][]=distribution(training);
				System.out.println("Done!");
				System.out.println("Generating tree...");
				Binarytree output_tree=obj.dtl(0,1,training,init_dist,args[2]);
				obj.printTree(output_tree.root);

				testing=obj.acceptAndProcessInput(test,"test");

				double totalaccu=0;
				for(int i=0;i<testing.length;i++)
				{
					double[] inp_row=new double[testing[0].length];
					for(int j=0;j<testing[0].length;j++)
					{
						inp_row[j]=testing[i][j];
					}

					double[][] dist=obj.traverseTree(inp_row,output_tree.root);

					totalaccu+=obj.testing_Phase(i,dist,inp_row[testing[0].length-1]);
				}
				System.out.println("classification accuracy="+(totalaccu/testing.length));
			}
			else	//forests
			{
				double forestsize=0;
				if(args[2].equalsIgnoreCase("forest3"))
				{
					forestsize=3;
				}
				if(args[2].equalsIgnoreCase("forest15"))
				{
					forestsize=15;
				}
					
				int tid=0;

				dtree obj=new dtree();
				Binarytree[] output_tree=new Binarytree[(int)forestsize];

				System.out.println("Reading data...");
				training=obj.acceptAndProcessInput(train,"train");
						
				obj.extract_classes(training);
				double init_dist[][]=new double[class_count][2];
				init_dist=distribution(training);
	
				System.out.println("Done!");
				System.out.println("Generating trees...");
				for(int i=0;i<(int)forestsize;i++)
				{
					output_tree[i]=obj.dtl(tid,1,training,init_dist,"randomized");
					tid++;
					obj.printTree(output_tree[i].root);
				}
					
				testing=obj.acceptAndProcessInput(test,"test");
				double totalaccu=0;

				for(int i=0;i<testing.length;i++)
				{
					double[] inp_row=new double[testing[0].length];
					double[][] total_dist=new double[class_count][2];
					
					for(int j=0;j<testing[0].length;j++)
					{
						inp_row[j]=testing[i][j];
					
						for(int k=0;k<(int)forestsize;k++)
						{
							double[][] dist=obj.traverseTree(inp_row,output_tree[k].root);
							
							for(int l=0;l<class_count;l++)
							{
								total_dist[l][1]+=dist[l][1];
							}
						}

						for(int m=0;m<class_count;m++)
						{
							total_dist[m][0]=cls[m];
							total_dist[m][1]=total_dist[m][1]/forestsize;	
						}
					}
							
					totalaccu+=obj.testing_Phase(i,total_dist,inp_row[testing[0].length-1]);	
				}
				
				System.out.println("classification accuracy="+(totalaccu/testing.length));		
			}//forest else
		}//valid option else
	}//psvm
	///////////////////////////////////////////////////////////////////////////////////////////OK
}//class dtree
///////////////////////////////////////////////////////////////////////////////////////////OK

///////////////////////////////////////////////////////////////////////////////////////////OK
class Binarytree
{
	Node root;

	Binarytree(double treeId,double nodeId,double featureId,double threshold,double informationgain,double[][] distribution,double[][] examples)
	{
        	this.root=new Node(treeId,nodeId,featureId,threshold,informationgain,distribution,examples);
    	}

	public void add(Node parent,Node child,String orientation)
    	{
        	if(orientation=="left")
        	{
           		parent.setLeft(child);
        	}
        	if(orientation=="right")
        	{
           		parent.setRight(child);
        	}
    	}
}//class Binarytree
///////////////////////////////////////////////////////////////////////////////////////////OK

///////////////////////////////////////////////////////////////////////////////////////////OK
class Node
{
	double treeId,nodeId,featureId,threshold,informationgain;
	double distribution[][];
	double examples[][];
    	Node left;
    	Node right;
	
    	Node(double treeId,double nodeId,double featureId,double threshold,double informationgain,double[][] distribution,double[][] examples)
	{
		this.treeId=treeId;
		this.nodeId=nodeId;
		this.featureId=featureId;
		this.threshold=threshold;
		this.informationgain=informationgain;
		this.distribution=distribution;
		this.examples=examples;
        	this.right=null;
        	this.left=null;
   	}
		
	void setLeft(Node left)
	{
        	this.left=left;
   	}

	void setRight(Node right)
	{
        	this.right=right;
   	}
}//class Node
///////////////////////////////////////////////////////////////////////////////////////////OK

///////////////////////////////////////////////////////////////////////////////////////////OK
class GenQueue<E>
{
	LinkedList<E> list=new LinkedList<E>();
   	
	void enqueue(E item)
	{
      		list.addLast(item);
   	}
   	
	E dequeue()
	{
      		return list.poll();
   	}
   
	boolean hasItems()
	{
      		return !list.isEmpty();
   	}
}//class GenQueue<E>
///////////////////////////////////////////////////////////////////////////////////////////OK