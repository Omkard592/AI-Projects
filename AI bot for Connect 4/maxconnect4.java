import java.io.*;
import java.util.*;

/* @author James Spargo */
/* modifications made by Omkar Deshmukh */

public class maxconnect4
{
	public static void main(String[] args)throws InterruptedException 
  	{
    		// check for the correct number of arguments
    		if(args.length!=4) 
    		{
      			System.out.println("Four command-line arguments are needed:\n"
                        + "Usage: java [program name] interactive [input_file] [computer-next / human-next] [depth]\n"
                        + " or:  java [program name] one-move [input_file] [output_file] [depth]\n");
			exit_function(0);
     		}
		
    		// parse the input arguments
    		String game_mode=args[0].toString();				// the game mode
    		String input=args[1].toString();				// the input game file
    		int depthLevel=Integer.parseInt(args[3]);  			// the depth level of the ai search
		
    		// create and initialize the game board
    		GameBoard currentGame=new GameBoard(input,game_mode,depthLevel);
    
    		// create the Ai Player
    		AiPlayer calculon=new AiPlayer(depthLevel);
		
    		//  variables to keep up with the game
    		int playColumn=99;						//  the players choice of column to play
    		boolean playMade=false;						//  set to true once a play has been made

   		if(game_mode.equalsIgnoreCase("interactive")) 
    		{
			
			String current_player=args[2].toString();

			if(!((current_player.equalsIgnoreCase("computer-next")) || (current_player.equalsIgnoreCase("human-next"))))
			{
				System.out.print("\nInvalid next player choice\n");
				exit_function(0);
			}

			System.out.print("\nMaxConnect-4 game\n");
    			System.out.print("Game state before move:\n");
    
    			//print the current game board
    			currentGame.printGameBoard();
    			// print the current scores
    			System.out.println("Score: Player 1 = "+currentGame.getScore(1)+", Player2 = " +currentGame.getScore(2)+"\n");

			if(current_player.equalsIgnoreCase("computer-next"))
			{
				current_player="Computer";
			}
			else
			{
				
				current_player="Human";
			}
			
			while(currentGame.getPieceCount()<42) 
    			{
        			if(current_player.equalsIgnoreCase("Computer"))
				{
	
					// AI play - random play
					do
					{
						playColumn=calculon.findBestPlay(currentGame);
					}while(!(currentGame.isValidPlay(playColumn)));
					
					
					// play the piece
					currentGame.playPiece(playColumn);
        	
        				// display the current game board
        				System.out.println("Move "+currentGame.getPieceCount()+": Player "+current_player+", column "+(playColumn+1));
        				System.out.print("Game state after move:\n");
        				currentGame.printGameBoard();
    
        				// print the current scores
        				System.out.println("Score: Player 1= "+currentGame.getScore(1)+", Player2= "+currentGame.getScore(2)+"\n");
        
        				currentGame.printGameBoardToFile("computer.txt");
					current_player="Human";
				}

				else
				{
					// Human play
					Scanner sc=new Scanner(System.in);

					System.out.println("Its your turn human, enter the column no. (1-7) to place your piece");
					playColumn=sc.nextInt();	
					playColumn--;

					while(!(currentGame.isValidPlay(playColumn)))
					{
						System.out.println("The column no. you entered is either full or invalid");
						System.out.println("Try again");
						playColumn=sc.nextInt();
						playColumn--;
					}

					// play the piece
					currentGame.playPiece(playColumn);
        	
        				// display the current game board
        				System.out.println("Move "+currentGame.getPieceCount()+": Player "+current_player+", column "+(playColumn+1));
        				System.out.print("Game state after move:\n");
        				currentGame.printGameBoard();
    
        				// print the current scores
        				System.out.println("Score: Player 1= "+currentGame.getScore(1)+", Player2= "+currentGame.getScore(2)+"\n");
        
        				currentGame.printGameBoardToFile("human.txt");
					current_player="Computer";

				}
	
				
    			} 

    			if(!(currentGame.getPieceCount()<42)) 
    			{
				// print the current scores
        			System.out.println("Score: Player 1 = "+currentGame.getScore(1)+", Player2 = "+currentGame.getScore(2)+"\n");
				System.out.println("\nThe Board is Full\n\nGame Over");
    			}
	    
    			return;


		} 
				
    		else if(!game_mode.equalsIgnoreCase("one-move")) 
    		{
     			System.out.println("\n"+game_mode+" is an unrecognized game mode\ntry again.\n");
      			return;
    		}

    		/////////////   one-move mode ///////////
   	 	// get the output file name
    		String output=args[2].toString();				// the output game file
    
    		System.out.print("\nMaxConnect-4 game\n");
    		System.out.print("Game state before move:\n");
    
    		//print the current game board
    		currentGame.printGameBoard();
    		// print the current scores
    		System.out.println("Score: Player 1 = "+currentGame.getScore(1)+", Player2 = " +currentGame.getScore(2)+"\n");
    
    		// ****************** this chunk of code makes the computer play

    		if(currentGame.getPieceCount()<42) 
    		{
        		int current_player=currentGame.getCurrentTurn();
	
			// AI play - random play
			do
			{
			playColumn=calculon.findBestPlay(currentGame);
			}while(!(currentGame.isValidPlay(playColumn)));
	
			// play the piece
			currentGame.playPiece(playColumn);
        	
        		// display the current game board
        		System.out.println("Move "+currentGame.getPieceCount()+": Player "+current_player+", column "+(playColumn+1));
        		System.out.print("Game state after move:\n");
        		currentGame.printGameBoard();
    
        		// print the current scores
        		System.out.println("Score: Player 1 = "+currentGame.getScore(1)+", Player2 = "+currentGame.getScore(2)+"\n");
        
        		currentGame.printGameBoardToFile(output);
    		} 

    		else 
    		{
			System.out.println("\nI can't play.\nThe Board is Full\n\nGame Over");
    		}
	
    		//************************** end computer play
			
    
    		return;
    
	} // end of main()
	
  	/**
   	* This method is used when to exit the program prematurly.
   	* @param value an integer that is returned to the system when the program exits.
   	*/
  	private static void exit_function(int value)
  	{
      		System.out.println("exiting from MaxConnectFour.java!\n\n");
      		System.exit(value);
  	}

} // end of class connectFour