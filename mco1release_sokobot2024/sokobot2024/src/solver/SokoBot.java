package solver;

import java.util.*;

public class SokoBot {
  private char[][] mapData;
  private char[][] itemsData;
  private int width, height;
  private int[] posPlayer;
  private int[][] posBoxes;
  private int[][] posWalls;
  private int[][] posGoals;
  
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;  
    gatherPositions();
    
    return aStarSearch(posPlayer, posBoxes);
  }

  /* 
   * This method is used to gather the positions of the player, boxes, walls, and goals
   *  and assign them to the global variables posPlayer, posBoxes, posWalls, and posGoals
   * 
   *  @param: none
   *  @return: none
   */
  private void gatherPositions() {
    List<int[]> boxes = new ArrayList<>();
    List<int[]> walls = new ArrayList<>();
    List<int[]> goals = new ArrayList<>();

    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            char item = itemsData[i][j];
            char map = mapData[i][j];

            if (item == '@' || item == '+') {
                posPlayer = new int[]{i, j};
            }

            if (item == '$' || item == '*') {
                boxes.add(new int[]{i, j});
            }

            if (map == '#') {
                walls.add(new int[]{i, j});
            }

            if (map == '.' || item == '*') {
                goals.add(new int[]{i, j});
            }
        }
    }

    posBoxes = boxes.toArray(new int[0][]);
    posWalls = walls.toArray(new int[0][]);
    posGoals = goals.toArray(new int[0][]);
  }

  /*
   * this method checks if the current state is the end state
   *  
   *  @param posBoxes: the current position of the boxes
   *  @return boolean: true if the goalcount is equal to the number of goals
   */
  private boolean isEndState(int[][] posBoxes){
    Set<String> boxSet = new HashSet<>();
    for (int[] box : posBoxes) {
        boxSet.add(box[0] + "," + box[1]); 
    }

    int goalCount = 0;
    for (int[] goal : posGoals) {
        String goalPos = goal[0] + "," + goal[1];
        if (boxSet.contains(goalPos)) {
            goalCount++;
        }
    }

    return goalCount == posGoals.length;
  }

  /*
   * This method checks if the action is a legal move. 
   * It first checks if the action is a push or move,
   * then it uses the isPositionLegal method to check 
   * if the position is legal.
   * 
   * @param action: the action to be checked
   * @param posPlayer: the current position of the player
   * @param posBoxes: the current position of the boxes
   * 
   * @return boolean: true if the move is legal
   */
  private Boolean isLegalMove(String[] action, int[] posPlayer, int[][] posBoxes) {
    int xplayer = posPlayer[0];
    int yplayer = posPlayer[1];
    int x, y;
    int xAction = Integer.parseInt(action[0]);
    int yAction = Integer.parseInt(action[1]);
    if (action[2].equals(action[2].toUpperCase())){
      x = xplayer + 2 * xAction;
      y = yplayer + 2 * yAction;
    }
    else{
      x = xplayer + xAction;
      y = yplayer + yAction;
    }
    return isPositionLegal(x, y, posBoxes);
  }

  /*
   * This method checks if the position is legal by
   * checking if the position is a box or a wall
   * 
   * @param x: the x coordinate 
   * @param y: the y coordinate 
   * @param posBoxes: the current position of the boxes
   * 
   * @return boolean: true if the position is legal
   */
  private boolean isPositionLegal(int x, int y, int[][] posBoxes) {
    for (int[] box : posBoxes) {
        if (box[0] == x && box[1] == y) {
            return false; 
        }
    }
    
    for (int[] wall : posWalls) {
        if (wall[0] == x && wall[1] == y) {
            return false;
        }
    }
    return true;
  }

  /*
   * This method returns the legal moves of the player.
   * generates all possible moves (up, down, left, right),
   * checks if it is a push or move and adjust the actions 
   * arraylist, and finally checks if it is a legal move.
   * 
   * @param posPlayer: the current position of the player
   * @param posBoxes: the current position of the boxes
   * 
   * @return String[][]: the legal moves of the player
   */
  private String[][] legalMoves(int[] posPlayer, int[][] posBoxes){
    ArrayList<String[]> actions = new ArrayList<>();
    actions.add(new String[]{"-1", "0", "u", "U"});
    actions.add(new String[]{"1", "0", "d", "D"});  
    actions.add(new String[]{"0", "-1", "l", "L"}); 
    actions.add(new String[]{"0", "1", "r", "R"});  
    int xplayer = posPlayer[0];
    int yplayer = posPlayer[1];
    int x, y;
    ArrayList<String[]> legalMoves = new ArrayList<>();

    for (String[] action : actions) {
      x = xplayer + Integer.parseInt(action[0]);
      y = yplayer + Integer.parseInt(action[1]);

      if (isInPosBox(x, y, posBoxes)) {
        ArrayList<String> newAction = new ArrayList<>(Arrays.asList(action));
        newAction.remove(2);
        if (isLegalMove(newAction.toArray(new String[0]), posPlayer, posBoxes)){
          legalMoves.add(newAction.toArray(new String[0]));
        }
        else
          continue;
      }
      else{
        ArrayList<String> newAction = new ArrayList<>(Arrays.asList(action));
        newAction.remove(3);
        if (isLegalMove(newAction.toArray(new String[0]), posPlayer, posBoxes)){
            legalMoves.add(newAction.toArray(new String[0]));
          } 
          else {
            continue;
          }
      }
    }
    return legalMoves.toArray(new String[0][]);
  }

  /*
   * This method checks if there is a box in the
   * given coordinates
   * 
   * @param x1: the x coordinate
   * @param y1: the y coordinate 
   * @param posBox: the current position of the boxes
   * 
   * @return boolean: true if there is a box 
   */
  private boolean isInPosBox(int x1, int y1, int[][] posBox) {
    for (int[] box : posBox) {
        if (box[0] == x1 && box[1] == y1) {
            return true; 
        }
    }
    return false; 
}

  /*
   * This method updates the state of the player and the boxes
   * by checking if the action is a push or move, then it updates
   * the position of the player and the boxes accordingly.
   * 
   * @param posPlayer: the current position of the player
   * @param posBoxes: the current position of the boxes
   * @param action: the action to be taken
   * 
   * @return Object[]: an array of the updated position of the player and the boxes
   */
  private Object[] updateState(int[] posPlayer, int[][] posBoxes, String[] action){
    int xplayer = posPlayer[0];
    int yplayer = posPlayer[1];
    int x, y;
    int xAction = Integer.parseInt(action[0]);
    int yAction = Integer.parseInt(action[1]);
    ArrayList<int[]> newPosBoxes = new ArrayList<>();
    for (int[] row : posBoxes) {
      newPosBoxes.add(Arrays.copyOf(row, row.length));
    }
    int[] newPosPlayer = {xplayer + xAction, yplayer + yAction};
    if (action[2] == action[2].toUpperCase()){
      newPosBoxes.removeIf(box -> Arrays.equals(box, newPosPlayer));
      x = xplayer + 2 * xAction;
      y = yplayer + 2 * yAction;
      newPosBoxes.add(new int[]{x, y});
    }
    int[][] updatedPosBoxes = newPosBoxes.toArray(new int[0][]);
    int[] updatedPosPlayer = newPosPlayer;
    return new Object[]{updatedPosPlayer, updatedPosBoxes};
  } 
  
  /*
   * This method checks if the state is failed by checking
   * if the boxes are in a failed state. it usses the rotateFlipPattern
   * as basis so the index of failed states does not need to be changed 
   * per iteration.
   * 
   * Failed state 1: there is a box to the top and a wall on the right
   * Failed state 2: there is a box to the top and a wall on the top right and right
   * Failed state 3: there is a box to the top and to the right and a wall on the top right
   * Failed state 4: there is a box to the top, to the top right, and to the right
   * Failed state 5: there is a box to the top and to the bottom left; and a wall to the top right, left, and bottom right
   * 
   * @param posBoxes: the current position of the boxes
   * 
   * @return boolean: true if the state is failed
   */
  private boolean isFailed(int[][] posBoxes){
    int[][] rotateFlipPattern = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8},
      {2, 5, 8, 1, 4, 7, 0, 3, 6},
      {8, 7, 6, 5, 4, 3, 2, 1, 0}, 
      {6, 3, 0, 7, 4, 1, 8, 5, 2},
      {2, 1, 0, 5, 4, 3, 8, 7, 6},
      {0, 3, 6, 1, 4, 7, 2, 5, 8},
      {6, 7, 8, 3, 4, 5, 0, 1, 2}, 
      {8, 5, 2, 7, 4, 1, 6, 3, 0}  
    };
    List<int[]> allPatterns = new ArrayList<>();
    allPatterns.addAll(Arrays.asList(rotateFlipPattern));
    

    for (int[] box : posBoxes) {
      if (!isGoal(box)) {
          int[][] board = {
              {box[0] - 1, box[1] - 1}, {box[0] - 1, box[1]}, {box[0] - 1, box[1] + 1},
              {box[0], box[1] - 1}, {box[0], box[1]}, {box[0], box[1] + 1},
              {box[0] + 1, box[1] - 1}, {box[0] + 1, box[1]}, {box[0] + 1, box[1] + 1}
          };

          for (int[] pattern : allPatterns) {
              List<int[]> newBoard = new ArrayList<>();
              
              for (int index : pattern) {
                  newBoard.add(Arrays.copyOf(board[index], board[index].length));
              }

              if (isWall(newBoard.get(1)) && isWall(newBoard.get(5))) {
                  //System.out.println("failed 1");
                  return true;
              } else if (isBox(newBoard.get(1), posBoxes) && isWall(newBoard.get(2)) && isWall(newBoard.get(5))) {
                  //System.out.println("failed 2");
                  return true;
              } else if (isBox(newBoard.get(1), posBoxes) && isWall(newBoard.get(2)) && isBox(newBoard.get(5), posBoxes)) {
                  //System.out.println("failed 3");
                  return true;
              } else if (isBox(newBoard.get(1), posBoxes) && isBox(newBoard.get(2), posBoxes) && isBox(newBoard.get(5), posBoxes)) {
                  //System.out.println("failed 4");
                  /*
                   * System.err.println("[" + newBoard.get(0)[0] + "," + newBoard.get(0)[1] + "], [" + newBoard.get(1)[0] + "," + newBoard.get(1)[1] + "], [" + newBoard.get(2)[0] + "," + newBoard.get(2)[1] + "]\n"
                  + "[" + newBoard.get(3)[0] + "," + newBoard.get(3)[1] + "], [" + newBoard.get(4)[0] + "," + newBoard.get(4)[1] + "], [" + newBoard.get(5)[0] + "," + newBoard.get(5)[1] + "]\n" 
                  + "[" + newBoard.get(6)[0] + "," + newBoard.get(6)[1] + "], [" + newBoard.get(7)[0] + "," + newBoard.get(7)[1] + "], [" + newBoard.get(8)[0] + "," + newBoard.get(8)[1] + "]\n");
                   */                  
                  return true;
              } else if (isBox(newBoard.get(1), posBoxes) && isBox(newBoard.get(6), posBoxes) && isWall(newBoard.get(2))
                      && isWall(newBoard.get(3)) && isWall(newBoard.get(8))) {
                  //System.out.println("failed 5"); 
                  return true;
              }
          }
      }
      
    }
    return false;
  }

  /*
   * This method checks if the given position (box) is in the goal
   * 
   * @param pos: the current position of the box
   * 
   * @return boolean: true if the box is in the goal
   */
  private boolean isGoal(int[] pos) {
    return Arrays.stream(posGoals).anyMatch(goal -> Arrays.equals(goal, pos));
  }

  /*
   * This method checks if the given position is a wall
   * 
   * @param pos: the current position
   * 
   * @return boolean: true if the position is a wall
   */
  private boolean isWall(int[] pos) {
      return Arrays.stream(posWalls).anyMatch(wall -> Arrays.equals(wall, pos));
  }

  /*
   * This method checks if the given position is a box
   * 
   * @param pos: the current position
   * @param posBoxes: the current position of the boxes
   * 
   * @return boolean: true if the position is a box
   */
  private boolean isBox(int[] pos, int[][] posBoxes) {
      return Arrays.stream(posBoxes).anyMatch(box -> Arrays.equals(box, pos));
  }

  /*
   * This method calculates the heuristic of the current state
   * by calculating the distance of the boxes to the goals.
   * It starts by sorting the box and goals then it 
   * calculates the distance of the boxes to the goals.
   * 
   * @param posPlayer: the current position of the player
   * @param posBoxes: the current position of the boxes
   * 
   * @return int: the heuristic of the current state
   */
  private int heuristic(int[] posPlayer, int[][] posBoxes){
    int distance = 0;
    int tempdist = 0;
    int minDist = Integer.MAX_VALUE;
    
    Set<String> posBoxSet = new HashSet<>();
    for (int[] box : posBoxes) {
        posBoxSet.add(box[0] + "," + box[1]); 
    }
    Set<String> posGoalSet = new HashSet<>();
    for (int[] goal : posGoals) {
        posGoalSet.add(goal[0] + "," + goal[1]); 
    }
   
    List<String> sortposBox = new ArrayList<>(posBoxSet);
    List<String> sortposGoal = new ArrayList<>(posGoalSet);

    int[][] finalSortPosBox = new int[sortposBox.size()][2];
    for (int i = 0; i < sortposBox.size(); i++) {
      String[] coords = sortposBox.get(i).split(","); 
      finalSortPosBox[i][0] = Integer.parseInt(coords[0]); 
      finalSortPosBox[i][1] = Integer.parseInt(coords[1]); 
    }
    int[][] finalSortPosGoal = new int[sortposGoal.size()][2];
    for (int i = 0; i < sortposGoal.size(); i++) {
      String[] coords = sortposGoal.get(i).split(","); 
      finalSortPosGoal[i][0] = Integer.parseInt(coords[0]); 
      finalSortPosGoal[i][1] = Integer.parseInt(coords[1]);
    }
    /*
    System.out.println("Box Set: " + posBoxSet);
    System.out.println("Goal Set: " + posGoalSet);
    System.out.println("Complete Set: " + completeSet);
     */
    
    for (int i = 0; i < finalSortPosBox.length; i++) {
      for (int j = 0; j < finalSortPosGoal.length; j++) {
        tempdist += Math.abs(finalSortPosBox[i][0] - finalSortPosGoal[i][0]) +
                    Math.abs(finalSortPosBox[i][1] - finalSortPosGoal[i][1]);
        minDist = Math.min(minDist, tempdist);
      }
      distance += minDist;
    }
  

    return distance;
  }
  
 
  /*
   * This method is the main method for the A* search algorithm.
   * It starts by creating the start state and pushing it to the frontier.
   * Then it creates a map of the explored states and a priority queue for the actions.
   * It then loops through the frontier and pops the current state and action.
   * If the current state is the end state, it returns the action.
   * If the current state is already explored, it skips it.
   * It then generates the legal moves and updates the state accordingly.
   * If the state is failed, it skips that state.
   * It then calculates the heuristic of the updated state and updates the frontier 
   * and action queues.
   * 
   * @param beginPlayer: the starting position of the player
   * @param beginBoxes: the starting position of the boxes
   * 
   * @return String: the action to be taken
   */
  private String aStarSearch(int[] beginPlayer, int[][] beginBoxes) {
    State startState = new State(beginPlayer, beginBoxes, heuristic(beginPlayer, beginBoxes));
    MyPriorityQueue<State> frontier = new MyPriorityQueue<>();
    frontier.push(startState, heuristic(beginPlayer, beginBoxes));

    Map<State, Integer> explored = new HashMap<>();

    MyPriorityQueue<String> actions = new MyPriorityQueue<>();
    actions.push("", heuristic(beginPlayer, beginBoxes));

    while (!frontier.isEmpty()) {
      State currentState = frontier.pop();
      String currentAction = actions.pop();

      /*
       * System.out.println("currentState: " + currentState.getPlayer()[0] + " " + currentState.getPlayer()[1]);
        System.out.println("currentAction: " + currentAction + "| Heuristic:  " + currentState.getHCost());
       */
      
      

      if (isEndState(currentState.getBoxes())) {
          //System.out.println("Final Action: " + currentAction );
          return currentAction.toLowerCase();
          
      }

      if (explored.containsKey(currentState) && explored.get(currentState) <= currentAction.length()) {
        continue;
      }
      explored.put(currentState, currentAction.length());
      int currentCost = currentAction.length();
    
      String[][] legalMoves = legalMoves(currentState.getPlayer(), currentState.getBoxes());
      /* 
        for(int i = 0; i < legalMoves.length; i++){
        System.err.println("Legal Moves: " + legalMoves[i][0] + " " + legalMoves[i][1] + " " + legalMoves[i][2]);
      }
      */
      
      for (String[] action : legalMoves) {
        Object[] updatedState = updateState(currentState.getPlayer(), currentState.getBoxes(), action);
        int[] updatedPlayer = (int[]) updatedState[0];
        int[][] updatedBoxes = (int[][]) updatedState[1];
          
          
          /*
            * System.out.println(updatedBoxes[0][0] + " " + updatedBoxes[0][1] + "\n" + updatedBoxes[1][0] + " " + updatedBoxes[1][1] 
            + "\n" + updatedBoxes[2][0] + " " + updatedBoxes[2][1] + "\n" + updatedBoxes[3][0] + " " + updatedBoxes[3][1] + "\n" 
            );     
            */
          
        if(isFailed(updatedBoxes)){
          //System.err.println("\n Failed state: " + updatedPlayer[0] + "," + updatedPlayer[1] + "| From: " + currentState.getPlayer()[0] + "," + currentState.getPlayer()[1]);
        
          continue;
        }
        int heuristic = heuristic(updatedPlayer, updatedBoxes);
        State nextState = new State(updatedPlayer, updatedBoxes, heuristic + currentCost);
        //System.err.println("\n Potential action: " + currentAction + action[2] + "  Potential state: " + updatedPlayer[0] + "," + updatedPlayer[1] + "| COST: " + (heuristic + currentCost) + "| From: " + currentState.getPlayer()[0] + "," + currentState.getPlayer()[1]);
        frontier.push(nextState, nextState.getHCost());
        actions.push(currentAction + action[2], heuristic + currentCost);
          
        
      }
    }
    System.err.println("no solution found");
    return "";
  }

  /*
   * This class is used to store the state of the player, boxes, and the heuristic cost
   * has methods that compare the states and hash the states
   */
  private class State{
    private int[] player;
    private int[][] boxes;
    private int hcost;
  
    public State(int[] player, int[][] boxes, int hcost) {
      this.player = player;
      this.boxes = boxes;
      this.hcost = hcost;
    }
  
    public int[] getPlayer() {
        return player;
    }
  
    public int[][] getBoxes() {
        return boxes;
    }
  
    public int getHCost() {
      return hcost;
    }
  
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof State)) return false;
        State other = (State) obj;
        return Arrays.equals(player, other.player) && Arrays.deepEquals(boxes, other.boxes);
    }
  
    @Override
    public int hashCode() {
        return Arrays.hashCode(player) + Arrays.deepHashCode(boxes);
    }
  }

  /*
   * This class is used to create a custom priority queue for the states
   * has methods to push, pop the states and check if the queue is empty. 
   * Automatically sorts the states
   * when pushed
   */
  public class MyPriorityQueue<T> {
    private List<PriorityItem<T>> heap;
    private int count;

    public MyPriorityQueue() {
        heap = new ArrayList<>();
        count = 0;
    }

    private static class PriorityItem<T> {
        int priority;
        int count;
        T item;

        PriorityItem(int priority, int count, T item) {
            this.priority = priority;
            this.count = count;
            this.item = item;
        }
    }

    public void push(T item, int priority) {
        PriorityItem<T> entry = new PriorityItem<>(priority, count, item);
        heap.add(entry);
        count++;
        Collections.sort(heap, (a, b) -> {
            if (a.priority == b.priority) {
                return Integer.compare(a.count, b.count);
            }
            return Integer.compare(a.priority, b.priority);
        });
    }

    public T pop() {
        if (isEmpty()) {
            return null; 
        }
        return heap.remove(0).item; 
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
  }
}
