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
    this.posPlayer = PosOfPlayer();
    this.posBoxes = PosOfBoxes();
    this.posWalls = PosOfWalls();
    this.posGoals = PosOfGoals();

    posPlayer = PosOfPlayer();
    posBoxes = PosOfBoxes();
    return aStarSearch(posPlayer, posBoxes);
  }

  private int[] PosOfPlayer() {
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (itemsData[i][j] == '@' || itemsData[i][j] == '+') {
                return new int[]{i, j};
            }
        }
    }
    return null; 
  }

  private int[][] PosOfBoxes() {
    List<int[]> boxes = new ArrayList<>();
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (itemsData[i][j] == '$' || itemsData[i][j] == '*') {
                boxes.add(new int[]{i, j});
            }
        }
    }
    return boxes.toArray(new int[0][]);
  }

  private int[][] PosOfWalls() {
    List<int[]> walls = new ArrayList<>();
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (mapData[i][j] == '#') {
                walls.add(new int[]{i, j});
            }
        }
    }
    return walls.toArray(new int[0][]);
  }

  private int[][] PosOfGoals() {
    List<int[]> goals = new ArrayList<>();
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (mapData[i][j] == '.' || itemsData[i][j] == '*') {
                goals.add(new int[]{i, j});
            }
        }
    }
    return goals.toArray(new int[0][]);
  }

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

  private boolean isInPosBox(int x1, int y1, int[][] posBox) {
    for (int[] box : posBox) {
        if (box[0] == x1 && box[1] == y1) {
            return true; 
        }
    }
    return false; 
}

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
  
  private boolean isFailed(int[][] posBoxes){
    int[][] rotatePattern = {
      {0, 1, 2, 3, 4, 5, 6, 7, 8},
      {2, 5, 8, 1, 4, 7, 0, 3, 6},
      {8, 7, 6, 5, 4, 3, 2, 1, 0}, 
      {6, 3, 0, 7, 4, 1, 8, 5, 2}  
    };
    int[][] flipPattern = {
      {2, 1, 0, 5, 4, 3, 8, 7, 6},
      {0, 3, 6, 1, 4, 7, 2, 5, 8},
      {6, 7, 8, 3, 4, 5, 0, 1, 2}, 
      {8, 5, 2, 7, 4, 1, 6, 3, 0}  
    };
    List<int[]> allPatterns = new ArrayList<>();
    allPatterns.addAll(Arrays.asList(rotatePattern));
    allPatterns.addAll(Arrays.asList(flipPattern));

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
                  System.out.println("failed 1");
                  return true;
              } else if (isBox(newBoard.get(1)) && isWall(newBoard.get(2)) && isWall(newBoard.get(5))) {
                  System.out.println("failed 2");
                  return true;
              } else if (isBox(newBoard.get(1)) && isWall(newBoard.get(2)) && isBox(newBoard.get(5))) {
                  System.out.println("failed 3");
                  return true;
              } else if (isBox(newBoard.get(1)) && isBox(newBoard.get(2)) && isBox(newBoard.get(5))) {
                  System.out.println("failed 4");
                  return true;
              } else if (isBox(newBoard.get(1)) && isBox(newBoard.get(6)) && isWall(newBoard.get(2))
                      && isWall(newBoard.get(3)) && isWall(newBoard.get(8))) {
                  System.out.println("failed 5"); 
                  return true;
              }
          }
      }
      
    }
    return false;
  }
  private boolean isGoal(int[] pos) {
    return Arrays.stream(posGoals).anyMatch(goal -> Arrays.equals(goal, pos));
  }

  private boolean isWall(int[] pos) {
      return Arrays.stream(posWalls).anyMatch(wall -> Arrays.equals(wall, pos));
  }

  private boolean isBox(int[] pos) {
      return Arrays.stream(posBoxes).anyMatch(box -> Arrays.equals(box, pos));
  }


  private int heuristic(int[] posPlayer, int[][] posBoxes){
    int distance = 0;
    int[][] completes = getCompletes(posGoals, posBoxes);

    Set<String> posBoxSet = new HashSet<>();
    for (int[] box : posBoxes) {
        posBoxSet.add(box[0] + "," + box[1]); 
    }
    Set<String> posGoalSet = new HashSet<>();
    for (int[] goal : posGoals) {
        posGoalSet.add(goal[0] + "," + goal[1]); 
    }
    Set<String> completeSet = new HashSet<>();
    for (int[] complete : completes) {
        completeSet.add(complete[0] + "," + complete[1]); 
    }
   

    posBoxSet.removeAll(completeSet);
    posGoalSet.removeAll(completeSet);

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
    
    if (finalSortPosBox.length != finalSortPosGoal.length) {
      throw new IllegalArgumentException("Arrays must be of the same length");
  }

  for (int i = 0; i < finalSortPosBox.length; i++) {
      distance += Math.abs(finalSortPosBox[i][0] - finalSortPosGoal[i][0]) +
                  Math.abs(finalSortPosBox[i][1] - finalSortPosGoal[i][1]);
  }

    return distance;
  }

  private static int[][] getCompletes(int[][] posGoals, int[][] posBoxes) {
    Set<String> goalSet = new HashSet<>();
    Set<String> boxSet = new HashSet<>();

    for (int[] goal : posGoals) {
        goalSet.add(Arrays.toString(goal));
    }
    for (int[] box : posBoxes) {
        boxSet.add(Arrays.toString(box));
    }

    goalSet.retainAll(boxSet);
    
    List<int[]> completeList = new ArrayList<>();

    
    for (String goal : goalSet) {
        goal = goal.replaceAll("[\\[\\] ]", ""); 
        String[] coords = goal.split(",");
        completeList.add(new int[]{Integer.parseInt(coords[0]), Integer.parseInt(coords[1])});
    }
    //System.err.println(Arrays.deepToString(completeList.toArray(new int[0][])));
    return completeList.toArray(new int[completeList.size()][]);
  
  }

  private int cost(String currentAction) {
    int count = 0;
  
    for (int i = 0; i < currentAction.length(); i++) {
      char action = currentAction.charAt(i);
      if (Character.isLowerCase(action)) { 
        count++;
    }
    }
    
    return count;
  }

  
  private String aStarSearch(int[] beginPlayer, int[][] beginBoxes) {
    State startState = new State(beginPlayer, beginBoxes, heuristic(beginPlayer, beginBoxes));
    MyPriorityQueue<State> frontier = new MyPriorityQueue<>();
    frontier.push(startState, startState.getHCost());

    HashMap<Integer, State> explored = new HashMap<>();
    MyPriorityQueue<String> actions = new MyPriorityQueue<>();
    actions.push("", heuristic(beginPlayer, beginBoxes));

    while (!frontier.isEmpty()) {
      State currentState = frontier.pop();
      String currentAction = actions.pop();
      int playerHash = Arrays.hashCode(currentState.getPlayer());

      System.out.println("currentState: " + currentState.getPlayer()[0] + " " + currentState.getPlayer()[1]);
      System.out.println("currentAction: " + currentAction);
      

      if (isEndState(currentState.getBoxes())) {
          return actions.pop();
          
      }

      if (!explored.containsKey(playerHash)) {
        explored.put(playerHash, currentState);
        int currentCost = cost(currentAction);
      
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
            System.out.println(updatedBoxes[0][0] + " " + updatedBoxes[0][1] + "\n" + updatedBoxes[1][0] + " " + updatedBoxes[1][1] 
            + "\n" + updatedBoxes[2][0] + " " + updatedBoxes[2][1] + "\n" + updatedBoxes[3][0] + " " + updatedBoxes[3][1] + "\n" 
          );     
          */
         
          if(isFailed(updatedBoxes)){
            System.out.println("Failed");
            continue;
          }
          int heuristic = heuristic(updatedPlayer, updatedBoxes);
          State nextState = new State(updatedPlayer, updatedBoxes, heuristic + currentCost);
          System.err.println("\n chosen state: " + updatedPlayer[0] + "," + updatedPlayer[1] + "| COST: " + (heuristic + currentCost) + "| From: " + currentState.getPlayer()[0] + "," + currentState.getPlayer()[1]);
          frontier.push(nextState, nextState.getHCost());
          actions.push(currentAction + action[2], heuristic + currentCost);
         
          
        }
      }
    }
    return "";
  }


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
