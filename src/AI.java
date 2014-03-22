import java.util.*;

/** An instance represents a Solver that intelligently determines 
 *  Moves using algorithm Minimax. */
public class AI implements Solver {

    private Board.Player player; // the current player

    /** The depth of the search in the game space when evaluating moves. */
    private int depth;

    /** Constructor: an instance with player p who searches to depth d
     * when searching the game space for moves. */
    public AI(Board.Player p, int d) {
        player= p;
        depth= d;
    }

    /** See Solver.getMoves for the specification. */
    public @Override Move[] getMoves(Board b) {
    	assert b != null;
    	
        // Set up current state
    	State currentState= new State(player, b, null);
    	
    	// Construct the game tree
    	createGameTree(currentState, depth);
    	
    	// Assign a value to every tree node
    	minimax(currentState);
    	
    	// Select the child states sharing same value of s and 
    	// construct the output Move[].
    	List<Move> preferredMoves= new ArrayList<Move>();
    	for (State childState : currentState.getChildren())
    		if (childState.getValue() == currentState.getValue())
    			preferredMoves.add(childState.getLastMove());
    	return preferredMoves.toArray(Move.length0);
    }

    /** Generate the game tree with root s of depth d.
     * The game tree's nodes are State objects that represent the state of a game
     * and whose children are all possible States that can result from the next move.
     * NOTE: this method runs in exponential time with respect to d.
     * With d around 5 or 6, it is extremely slow and will start to take a very
     * long time to run.
     * Note: If s has a winner (4 in a row), it should be a leaf. */
    public static void createGameTree(State s, int d) {
        // Initialize the children field of State s
    	s.initializeChildren();
    	
    	// If the children of s is an array of length 0 or d is less than or 
    	// equal to 1, the recursion terminates.
    	if ((s.getChildren() == State.length0) || d <= 1)
    		return;
    	else
    		for (State childState : s.getChildren())
    			createGameTree(childState, d - 1);
    }
    
    /** Call minimax in ai with state s. */
    public static void minimax(AI ai, State s) {
        ai.minimax(s);
    }

    /** State s is a node of a game tree (i.e. the current State of the game).
     * Use the Minimax algorithm to assign a numerical value to each State of the
     * tree rooted at s, indicating how desirable that State is to this player. */
    public void minimax(State s) {
    	// If s is a leaf node of the game tree, call the evaluateBoard() function.
    	if (s.getChildren() == State.length0)
    		s.setValue(evaluateBoard(s.getBoard()));
    	else if (s.getPlayer() == player) {
    		// Choose the maximum value of the children as the value of s
    		int maxValue= Integer.MIN_VALUE;
    		for (State childState : s.getChildren()) {
    			minimax(childState);
    			if (childState.getValue() > maxValue)
    				maxValue= childState.getValue();
    		}
    		s.setValue(maxValue);
    	}
    	else {
    		// Choose the minimum value of the children as the value of s
    		int minValue= Integer.MAX_VALUE;
    		for (State childState : s.getChildren()) {
    			minimax(childState);
    			if (childState.getValue() < minValue)
    				minValue= childState.getValue();
    		}
    		s.setValue(minValue);
    	}
    }

    /** Evaluates the desirability of a given Board. This should only be
     * called at leaf nodes in a game tree, as it is most effective when
     * looking several moves into the future. */
    public int evaluateBoard(Board b) {
        Board.Player winner= b.hasConnectFour();
        if (winner == null) {
            // Store in sum the value of board b. 
            int sum= 0;
            List<Board.Player[]> locs= b.winLocations();
            for (Board.Player[] loc : locs) {
                for (Board.Player p : loc) {
                    sum= sum + (p == player ? 1 : p != null ? -1 : 0);
                }
            }
            return sum;
        }
        // There is a winner
        int numEmpty= 0;
        for (int r= 0; r < Board.NUM_ROWS; r= r+1) {
            for (int c= 0; c < Board.NUM_COLS; c= c+1) {
                if (b.getTile(r, c) == null) numEmpty += 1;
            }
        }
        return (winner == player ? 1 : -1) * 10000 * numEmpty;

    }

}
