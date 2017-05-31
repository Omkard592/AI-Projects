import java.util.*;

/* @author James Spargo */
/* modifications made by Omkar Deshmukh */
/*
***********Evaluation function references***********
I have referred this URL 'http://programmers.stackexchange.com/questions/263514/why-does-this-evaluation-function-work-in-a-connect-four-game-in-java'
to get a somewhat admissible heuristic which I have used to play for the initial empty game board.

I have also read the paper: Heuristics in the game of Connect-K by Jenny Lam (Department of Computer Science, UC Irvine, California) from which I learned a lot.
I have adopted the 'Move ordering preferring to move to the center' from the paper which gives
preference to moves closer to the center of the board. Hence the use of while loop.

I have developed a gameplay strategy 'AWESOME THREESOME' which stops tree searching (rather ignores other moves) when a triplet 
is encountered and then plays on that triplet to either make own's quad or block the opponent's quad. Else
it continues tree search to find the best strategic location as per the evaluation table.

*/


public class AiPlayer 
{

	int failSafe[]=new int[7];
	int safeindx=0;
	private int dd;
	private int[][] evaluationTable={{3, 4,  5,  7,  5, 4, 3}, 
                                         {4, 6,  8, 10,  8, 6, 4},
                                         {5, 8, 11, 13, 11, 8, 5}, 
                                         {5, 8, 11, 13, 11, 8, 5},
                                         {4, 6,  8, 10,  8, 6, 4},
                                         {3, 4,  5,  7,  5, 4, 3}};
	

    	public AiPlayer(int dep) 
    	{
		this.dd=dep;
    	}

    	//Following method is the brain of the AI player. It uses a depth limited minimax algorithm with alpha beta pruning
	


	public int alphabeta(int node[][], int depth, boolean maxPlayer, int pieces, int br, int turn, int a, int b) throws InterruptedException
	{

		if(depth==0 || pieces>41)
		{		
			int p1is=0;
			int p2is=0;
			
			if(turn==1)
			{
				p1is=1;
				p2is=2;
			}

			if(turn==2)
			{
				p1is=2;
				p2is=1;
			}


			int utility=138;
        		int sum=0;

       			for(int i=0;i<6;i++)
            		{
				for(int j=0;j<7;j++)
				{
                			if(node[i][j]==p1is)
					{
                    				sum+=evaluationTable[i][j];
					}
               				if(node[i][j]==p2is)
					{
                    				sum-=evaluationTable[i][j];
					}
				}
			}		


			for(int x=0;x<6;x++)
			{
				if(node[x][br]>0)
				{
					node[x][br]=0;
					pieces--;
					break;
				}
			}
			
			return (utility+sum);
			
		}

		if(maxPlayer==true)
		{
			int val=-99999;
			pieces++;
			int branches=3;
			int adder=1;
			int ans=0;
			int p1is=0;
			int p2is=0;
			
			if(turn==1)
			{
				p1is=1;
				p2is=2;
			}

			if(turn==2)
			{
				p1is=2;
				p2is=1;
			}


/////////////////////////////threesome awesome begins/////////////////////////////

			int zi=0;
			
			//check horizontally
			for(int i=5;i>=0;i--) 
        		{
	    			for(int j=0;j<4;j++) 
	    			{
					int z=0,one=0,two=0,check=0;

					if((node[i][j]==0) && (node[i][j+1]==0) &&
		    		  	(node[i][j+2]==0) && (node[i][j+3]==0))
					{
						break;
					}

					if(node[i][j]==p1is)
					one++;
					if(node[i][j]==p2is)
					two++;
					if(node[i][j]==0)
					z++;

					if(node[i][j+1]==p1is)
					one++;
					if(node[i][j+1]==p2is)
					two++;
					if(node[i][j+1]==0)
					z++;

					if(node[i][j+2]==p1is)
					one++;
					if(node[i][j+2]==p2is)
					two++;
					if(node[i][j+2]==0)
					z++;

					if(node[i][j+3]==p1is)
					one++;
					if(node[i][j+3]==p2is)
					two++;
					if(node[i][j+3]==0)
					z++;

					if((one==3 && z==1) || (two==3 && z==1))  //0111,1011,1101,1110
					{					  //0222,2022,2202,2220		  
						if(node[i][j]==0)
						{
							if(i==5)
							{
								zi=j;
								check=1;
							}
							if(i<5 && node[i+1][j]>0)
							{
								zi=j;
								check=1;
							}
						}
						if(node[i][j+1]==0)
						{
							if(i==5)
							{
								zi=j+1;
								check=1;
							}
							if(i<5 && node[i+1][j+1]>0)
							{
								zi=j+1;
								check=1;
							}
						}
						if(node[i][j+2]==0)
						{
							if(i==5)
							{
								zi=j+2;
								check=1;
							}
							if(i<5 && node[i+1][j+2]>0)
							{
								zi=j+2;
								check=1;
							}
						}
						if(node[i][j+3]==0)
						{
							if(i==5)
							{
								zi=j+3;
								check=1;
							}
							if(i<5 && node[i+1][j+3]>0)
							{
								zi=j+3;
								check=1;
							}
						}
					}

					if(check==1)
					{
						if(depth==dd)
						{
							return zi;
						}
						else
						{
							a=99999;
						}
					}
	    			}
			
			} // end horizontal

			zi=0;
			//check vertically
			for(int i=0;i<7;i++) 
        		{
	    			for(int j=2;j>=0;j--) 
	    			{
					int z=0,one=0,two=0,check=0;

					if((node[j][i]==0) && (node[j+1][i]==0) &&
		    		   	(node[j+2][i]==0) && (node[j+3][i]==0)) 
					{
		    				break;
					}

					if(node[j][i]==p1is)
					one++;
					if(node[j][i]==p2is)
					two++;
					if(node[j][i]==0)
					z++;

					if(node[j+1][i]==p1is)
					one++;
					if(node[j+1][i]==p2is)
					two++;
					if(node[j+1][i]==0)
					z++;

					if(node[j+2][i]==p1is)
					one++;
					if(node[j+2][i]==p2is)
					two++;
					if(node[j+2][i]==0)
					z++;

					if(node[j+3][i]==p1is)
					one++;
					if(node[j+3][i]==p2is)
					two++;
					if(node[j+3][i]==0)
					z++;

					if((one==3 && z==1) || (two==3 && z==1))  		//0	  0
					{							//1   O   2
						check=1;					//1   R   2	
						zi=i;						//1       2
					}

					if(check==1)
					{
						if(depth==dd)
						{
							return zi;
						}
						else
						{
							a=99999;
						}
					}
					
	    			}
			}// end vertical
	
			zi=0;
			//check diagonally - backs lash ->	\
	    		for(int i=0;i<4;i++)
			{
				for(int j=2;j>=0;j--)
				{
					int z=0,one=0,two=0,check=0;

					if((node[j][i]==0) && (node[j+1][i+1]==0) &&
		    		   	(node[j+2][i+2]==0) && (node[j+3][i+3]==0)) 
					{
		    				break;
					}

					if(node[j][i]==p1is)
					one++;
					if(node[j][i]==p2is)
					two++;
					if(node[j][i]==0)
					z++;

					if(node[j+1][i+1]==p1is)
					one++;
					if(node[j+1][i+1]==p2is)
					two++;
					if(node[j+1][i+1]==0)
					z++;

					if(node[j+2][i+2]==p1is)
					one++;
					if(node[j+2][i+2]==p2is)
					two++;
					if(node[j+2][i+2]==0)
					z++;

					if(node[j+3][i+3]==p1is)
					one++;
					if(node[j+3][i+3]==p2is)
					two++;
					if(node[j+3][i+3]==0)
					z++;

					if((one==3 && z==1) || (two==3 && z==1))  	// ALL \ COMBINATIONS
					{					  						
						if(node[j][i]==0 && node[j+1][i]>0)	
						{					
							zi=i;
							check=1;
						}
						if(node[j+1][i+1]==0 && node[j+2][i+1]>0)
						{
							zi=i+1;
							check=1;
						}
						if(node[j+2][i+2]==0 && node[j+3][i+2]>0)
						{
							zi=i+2;
							check=1;
						}
						if(node[j+3][i+3]==0)
						{
							if(j+3==5)
							{
								zi=i+3;
								check=1;
							}
							if(j+3<5 && node[j+4][i+3]>0)
							{
								zi=i+3;
								check=1;
							}
						}				  	
						
					}

					if(check==1)
					{
						if(depth==dd)
						{
							return zi;
						}
						else
						{
							a=99999;
						}
					}
	
		  		}
			
	    		}
	    
			zi=0;
	    		//check diagonally - forward slash -> /
	    		for(int i=0;i<4;i++)
			{
				for(int j=2;j>=0;j--)
				{
					int z=0,one=0,two=0,check=0;

					if((node[j][i+3]==0) && (node[j+1][i+2]==0) &&
		    		   	(node[j+2][i+1]==0) && (node[j+3][i]==0)) 
					{
		    				break;
					}

					if(node[j][i+3]==p1is)
					one++;
					if(node[j][i+3]==p2is)
					two++;
					if(node[j][i+3]==0)
					z++;

					if(node[j+1][i+2]==p1is)
					one++;
					if(node[j+1][i+2]==p2is)
					two++;
					if(node[j+1][i+2]==0)
					z++;

					if(node[j+2][i+1]==p1is)
					one++;
					if(node[j+2][i+1]==p2is)
					two++;
					if(node[j+2][i+1]==0)
					z++;

					if(node[j+3][i]==p1is)
					one++;
					if(node[j+3][i]==p2is)
					two++;
					if(node[j+3][i]==0)
					z++;

					if((one==3 && z==1) || (two==3 && z==1))  		// ALL / COMBINATIONS
					{					  						
						if(node[j][i+3]==0 && node[j+1][i+3]>0)	
						{					
							zi=i+3;
							check=1;
						}
						if(node[j+1][i+2]==0 && node[j+2][i+2]>0)
						{
							zi=i+2;
							check=1;
						}
						if(node[j+2][i+1]==0 && node[j+3][i+1]>0)
						{
							zi=i+1;
							check=1;
						}
						if(node[j+3][i]==0)
						{
							if(j+3==5)
							{
								zi=i;
								check=1;
							}
							if(j+3<5 && node[j+4][i]>0)
							{
								zi=i;
								check=1;
							}
						}				  	
						
					}

					if(check==1)
					{
						if(depth==dd)
						{
							return zi;
						}
						else
						{
							a=99999;
						}
					}
					
				}
	    		}// end player score check


/////////////////////////////threesome awesome ends/////////////////////////////

			while(branches!=7)
			{
				for(int x=5;x>=0;x--)
				{
					if(node[x][branches]==0)
					{
						if(pieces%2==0)
						{
							node[x][branches]=1;			
		    				}
				
						else
						{ 
							node[x][branches]=2;
		    				}
						
						val=alphabeta(node,depth-1,false,pieces,branches,turn,a,b);

						if(a<val)
						{
							a=val;
							ans=branches;
						}

						break;
					}
				}

				if(adder%2==1)	
				branches=branches+adder;
	
				else
				branches=branches+(-1*adder);

				adder++;

				if(a>=b)
				{
					break;
				}
			}

			for(int xy=0;xy<6;xy++)
			{
				if(node[xy][br]>0)
				{
					node[xy][br]=0;
					pieces--;
					break;
				}
			}

			if(depth==dd)
			{
				return ans;
			}
			return a;
		}

		if(maxPlayer==false)
		{
			int val=-99999;
			pieces++;
			int minbranches=3;
			int minadder=1;
	
			while(minbranches!=7)
			{
				for(int x=5;x>=0;x--)
				{
					if(node[x][minbranches]==0)
					{
						if(pieces%2==0)
						{
							node[x][minbranches]=1;
		    				}
				
						else
						{ 
							node[x][minbranches]=2;
		    				}
						
						val=alphabeta(node,depth-1,true,pieces,minbranches,turn,a,b);
				
						b=(b<val)?b:val;
						break;
					}
				}

				if(minadder%2==1)	
				minbranches=minbranches+minadder;
	
				else
				minbranches=minbranches+(-1*minadder);

				minadder++;

				if(a>=b)
				{
					break;
				}	
			}

			for(int xy=0;xy<6;xy++)
			{
				if(node[xy][br]>0)
				{
					node[xy][br]=0;
					pieces--;
					break;
				}
			}
			return b;
		}

		return -1;
	}


    	public int findBestPlay(GameBoard currentGame) throws InterruptedException
    	{

		int playChoice=alphabeta(currentGame.playBoardCopy,currentGame.depth,true,currentGame.pieceCount-1,0,currentGame.getCurrentTurn(),-99999,99999);
		
		//I have implemented a failsafe just in case if the alphabeta method returns any unwanted column value
		//It checks if the column value is within limits (i know one other method checks as well, but I 
		//have had some cases during the code development).
		//If the returned column value is of a column which is already full, the code snippet randomly
		//generates a column value so as to save from code crash for a penalty of a random move and a bit of lag. :/
		//99.9% the method will return a valid column but I like to be on the safe side.

		if(playChoice<0 || playChoice>6)
		{
			Random r=new Random();
			playChoice=r.nextInt(6);
		}
		else
		{
			do
			{
				if(currentGame.playBoardCopy[0][playChoice]>0)
				{
					failSafe[playChoice]=1;
					Random r=new Random();
					playChoice=r.nextInt(6);
				}
			}while(failSafe[playChoice]!=0);
		}

		return (playChoice);
    	}

}