package game;

import fi.iki.elonen.NanoHTTPD;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameServer extends NanoHTTPD {
    private ShipComponent ship;
    private List<PirateShip> pirates = new ArrayList<>();
    private SeaMonsterGroup monsters;
    private OceanMap oceanMap = OceanMap.getInstance();

    public GameServer() throws IOException {
        // Starts the server and sets up the game.
        super(8080);
        initializeGame();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Server running on http://localhost:8080");
    }

    private void initializeGame() {
        // Initializes the game with a ship, pirates, monsters, and whirlpools.
        Random rand = new Random();
        oceanMap.reset();
        ship = new Ship();

        pirates.clear();
        PirateShipFactory chaseFactory = new ChasePirateFactory();
        PirateShipFactory patrolFactory = new PatrolPirateFactory();
        pirates.add(chaseFactory.createPirateShip((Ship) ship, rand.nextInt(20), rand.nextInt(20)));
        pirates.add(patrolFactory.createPirateShip((Ship) ship, rand.nextInt(20), rand.nextInt(20)));

        monsters = new SeaMonsterGroup(0, 10, 0, 10);
        monsters.add(new SeaMonster(rand.nextInt(11), rand.nextInt(11), 0, 10, 0, 10));
        monsters.add(new SeaMonster(rand.nextInt(11), rand.nextInt(11), 0, 10, 0, 10));

        List<int[]> whirlpoolPositions = new ArrayList<>();
        Whirlpool whirlpoolA = null, whirlpoolB = null;
        while (whirlpoolPositions.size() < 2) {
            int x = rand.nextInt(oceanMap.getDimension());
            int y = rand.nextInt(oceanMap.getDimension());
            boolean positionTaken = oceanMap.getOceanCell()[x][y] || (x == 10 && y == 10);
            for (int[] pos : whirlpoolPositions) {
                if (pos[0] == x && pos[1] == y) {
                    positionTaken = true;
                    break;
                }
            }
            int[] treasureLoc = oceanMap.getTreasureLocation();
            if (x == treasureLoc[0] && y == treasureLoc[1]) {
                positionTaken = true;
            }
            if (!positionTaken) {
                whirlpoolPositions.add(new int[]{x, y});
                Whirlpool whirlpool = new Whirlpool(x, y, monsters);
                monsters.add(whirlpool);
                if (whirlpoolPositions.size() == 1) {
                    whirlpoolA = whirlpool;
                } else {
                    whirlpoolB = whirlpool;
                }
            }
        }
        if (whirlpoolA != null && whirlpoolB != null) {
            whirlpoolA.setExitWhirlpool(whirlpoolB);
            whirlpoolB.setExitWhirlpool(whirlpoolA);
        }
    }

    private int[] findValidPosition(Random rand) {
        // Finds a random, valid spot for placing new elements.
        int x, y;
        boolean positionTaken;
        do {
            x = rand.nextInt(oceanMap.getDimension());
            y = rand.nextInt(oceanMap.getDimension());
            positionTaken = oceanMap.getOceanCell()[x][y];
            int[] treasureLoc = oceanMap.getTreasureLocation();
            if (x == treasureLoc[0] && y == treasureLoc[1]) positionTaken = true;
            for (PirateShip pirate : pirates) {
                int[] pirateLoc = pirate.getLocation();
                if (x == pirateLoc[0] && y == pirateLoc[1]) positionTaken = true;
            }
            for (SeaMonsterComponent monster : monsters.getMonsters()) {
                int[] monsterLoc = monster.getLocation();
                if (x == monsterLoc[0] && y == monsterLoc[1]) positionTaken = true;
            }
            for (SeaMonsterComponent whirlpool : monsters.getWhirlpools()) {
                int[] whirlpoolLoc = whirlpool.getLocation();
                if (x == whirlpoolLoc[0] && y == whirlpoolLoc[1]) positionTaken = true;
            }
            int[] shipLoc = ship.getCurrentLocation();
            if (x == shipLoc[0] && y == shipLoc[1]) positionTaken = true;
        } while (positionTaken);
        return new int[]{x, y};
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Handles HTTP requests to manage game actions like movement and state updates.
        String uri = session.getUri();
        if (uri.equals("/state")) {
            return getGameState();
        } else if (uri.equals("/move")) {
            String direction = session.getParms().get("direction");
            moveShip(direction);
            monsters.move();
            applyTeleportationEffects();
            checkShipContainer();
            updateFreezeStatus();
            return getGameState();
        } else if (uri.equals("/use-freeze")) {
            String action = session.getParms().get("action");
            if ("activate".equals(action)) {
                activateFreeze();
            }
            return getGameState();
        } else if (uri.equals("/change-strategy")) {
            String pirateIndexStr = session.getParms().get("pirateIndex");
            String strategyType = session.getParms().get("strategy");
            return changePirateStrategy(pirateIndexStr, strategyType);
        } else if (uri.equals("/reset") && session.getMethod() == Method.POST) {
            initializeGame();
            return newFixedLengthResponse("Game reset");
        } else if (uri.equals("/add-element")) {
            String elementType = session.getParms().get("type");
            return addElement(elementType);
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }

    private Response addElement(String elementType) {
        // Adds new elements like pirates or monsters to the game.
        Random rand = new Random();
        int[] position = findValidPosition(rand);

        switch (elementType) {
            case "ChasePirate":
                PirateShipFactory chaseFactory = new ChasePirateFactory();
                PirateShip chasePirate = chaseFactory.createPirateShip((Ship) ship, position[0], position[1]);
                pirates.add(chasePirate);
                System.out.println("Added Chase Pirate Ship at (" + position[0] + "," + position[1] + ")");
                break;
            case "PatrolPirate":
                PirateShipFactory patrolFactory = new PatrolPirateFactory();
                PirateShip patrolPirate = patrolFactory.createPirateShip((Ship) ship, position[0], position[1]);
                pirates.add(patrolPirate);
                System.out.println("Added Patrol Pirate Ship at (" + position[0] + "," + position[1] + ")");
                break;
            case "SeaMonster":
                monsters.add(new SeaMonster(position[0], position[1], 0, 10, 0, 10));
                System.out.println("Added Sea Monster at (" + position[0] + "," + position[1] + ")");
                break;
            case "Island":
                oceanMap.getOceanCell()[position[0]][position[1]] = true;
                System.out.println("Added Island at (" + position[0] + "," + position[1] + ")");
                break;
            case "Whirlpool":
                Whirlpool newWhirlpool = new Whirlpool(position[0], position[1], monsters);
                monsters.add(newWhirlpool);
                List<SeaMonsterComponent> whirlpools = monsters.getWhirlpools();
                if (whirlpools.size() > 1) {
                    Whirlpool previousWhirlpool = (Whirlpool) whirlpools.get(whirlpools.size() - 2);
                    newWhirlpool.setExitWhirlpool(previousWhirlpool);
                    previousWhirlpool.setExitWhirlpool(newWhirlpool);
                }
                System.out.println("Added Whirlpool at (" + position[0] + "," + position[1] + ")");
                break;
            default:
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid element type");
        }
        return newFixedLengthResponse("Added " + elementType + " at (" + position[0] + "," + position[1] + ")");
    }

    private Response changePirateStrategy(String pirateIndexStr, String strategyType) {
        // Updates a pirate ship’s movement strategy.
        try {
            int pirateIndex = Integer.parseInt(pirateIndexStr);
            if (pirateIndex < 0 || pirateIndex >= pirates.size()) {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid pirate index");
            }

            PirateShip pirate = pirates.get(pirateIndex);
            MovementStrategy newStrategy;
            if ("Chase".equalsIgnoreCase(strategyType)) {
                newStrategy = new ChaseStrategy();
            } else if ("Patrol".equalsIgnoreCase(strategyType)) {
                newStrategy = new PatrolStrategy();
            } else {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid strategy type. Use 'Chase' or 'Patrol'");
            }

            pirate.setStrategy(newStrategy);
            System.out.println("Pirate " + pirateIndex + " strategy changed to " + strategyType);
            return newFixedLengthResponse("Strategy changed to " + strategyType);
        } catch (NumberFormatException e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid pirate index format");
        }
    }

    private void activateFreeze() {
        // Applies the freeze ability to the ship.
        if (!(ship instanceof FreezeShipDecorator)) {
            ship = new FreezeShipDecorator(ship, pirates);
        }
        ((FreezeShipDecorator) ship).useFreeze();
    }

    private void updateFreezeStatus() {
        // Updates the freeze duration for affected pirates.
        if (ship instanceof FreezeShipDecorator) {
            ((FreezeShipDecorator) ship).updateFreeze();
        }
    }

    private void applyTeleportationEffects() {
        // Handles ship teleportation through whirlpools.
        List<SeaMonsterComponent> whirlpools = monsters.getWhirlpools();
        boolean hasTeleported = false;
        int[] shipLocBefore = ship.getCurrentLocation();
        System.out.println("CC Ship position before whirlpool check: (" + shipLocBefore[0] + "," + shipLocBefore[1] + ")");
        for (SeaMonsterComponent component : whirlpools) {
            if (hasTeleported) {
                System.out.println("Ship has already teleported in this move, skipping further whirlpool checks.");
                break;
            }
            if (component instanceof WhirlpoolComponent) {
                int[] whirlpoolLoc = component.getLocation();
                System.out.println("Checking whirlpool at (" + whirlpoolLoc[0] + "," + whirlpoolLoc[1] + ")");
                ((WhirlpoolComponent) component).applyEffect(ship);
                int[] shipLocAfterCheck = ship.getCurrentLocation();
                if (shipLocBefore[0] != shipLocAfterCheck[0] || shipLocBefore[1] != shipLocAfterCheck[1]) {
                    hasTeleported = true;
                    System.out.println("CC Ship position updated to (" + shipLocAfterCheck[0] + "," + shipLocAfterCheck[1] + ") after whirlpool teleportation");
                }
            }
        }
    }

    private void checkShipContainer() {
        // Checks if the ship is in the monster group’s territory.
        int[] shipLoc = ship.getCurrentLocation();
        if (monsters.contains(shipLoc[0], shipLoc[1])) {
            System.out.println("Ship is in the monster group's territory (top-left quadrant)!");
        } else {
            System.out.println("Ship is outside the monster group's territory.");
        }
    }

    private Response getGameState() {
        // Sends the current game state to the client.
        JSONObject state = new JSONObject();
        state.put("ship", new JSONArray(ship.getCurrentLocation()));
        JSONArray pirateArray = new JSONArray();
        for (PirateShip pirate : pirates) {
            pirateArray.put(new JSONArray(pirate.getLocation()));
        }
        state.put("pirates", pirateArray);
        JSONArray monsterArray = new JSONArray();
        for (SeaMonsterComponent monster : monsters.getMonsters()) {
            monsterArray.put(new JSONArray(monster.getLocation()));
        }
        state.put("monsters", monsterArray);
        state.put("treasure", new JSONArray(oceanMap.getTreasureLocation()));
        state.put("islands", getIslandArray());
        JSONArray whirlpoolArray = new JSONArray();
        for (SeaMonsterComponent whirlpool : monsters.getWhirlpools()) {
            whirlpoolArray.put(new JSONArray(whirlpool.getLocation()));
        }
        state.put("whirlpools", whirlpoolArray);
        state.put("gameOver", checkGameOver());
        state.put("freezeUses", ship instanceof FreezeShipDecorator ? ((FreezeShipDecorator) ship).getFreezeUses() : 2);
        return newFixedLengthResponse(state.toString());
    }

    private JSONArray getIslandArray() {
        // Converts island positions into a JSON array.
        JSONArray islands = new JSONArray();
        boolean[][] grid = oceanMap.getOceanCell();
        for (int i = 0; i < oceanMap.getDimension(); i++) {
            for (int j = 0; j < oceanMap.getDimension(); j++) {
                if (grid[i][j]) {
                    islands.put(new JSONArray(new int[]{i, j}));
                }
            }
        }
        return islands;
    }

    private void moveShip(String direction) {
        // Moves the ship based on the input direction.
        int[] loc = ship.getCurrentLocation();
        int newX = loc[0], newY = loc[1];
        switch (direction) {
            case "right": newX++; break;
            case "left": newX--; break;
            case "up": newY--; break;
            case "down": newY++; break;
        }
        ship.setLocation(newX, newY);
    }

    private String checkGameOver() {
        // Checks if the game has ended (win or lose).
        int[] shipLoc = ship.getCurrentLocation();
        int[] treasureLoc = oceanMap.getTreasureLocation();
        if (shipLoc[0] == treasureLoc[0] && shipLoc[1] == treasureLoc[1]) {
            return "win";
        }
        for (PirateShip pirate : pirates) {
            int[] pirateLoc = pirate.getLocation();
            if (shipLoc[0] == pirateLoc[0] && shipLoc[1] == pirateLoc[1]) {
                return "lose";
            }
        }
        for (SeaMonsterComponent monster : monsters.getMonsters()) {
            int[] monsterLoc = monster.getLocation();
            if (shipLoc[0] == monsterLoc[0] && shipLoc[1] == monsterLoc[1]) {
                return "lose";
            }
        }
        return "ongoing";
    }

    public static void main(String[] args) {
        // Starts the game server.
        try {
            new GameServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}