package scripts.VeloxRockCrabs;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import javax.imageio.ImageIO;

import static org.tribot.api.General.println;
import static org.tribot.api.General.sleep;

// To be in line for Version 0.20!
@ScriptManifest(authors={"Dibes"}, category="Combat", name="Velox Crabs v0.19", description="<h1 style='text-align:center;color:black;'>Velox Crabs v0.19</h1>" +
        "<br> <h3 style='text-align:center'>This script is open source, and is not allowed to be used for resale</h3>")
public class VeloxRockCrabs extends Script implements Painting {

    public static boolean stop = false;
    public static int foodId, runeIds, eatLevel = 9;
    private RSTile bankTile = new RSTile(2725, 3491);
    private int[] crabIds = {1266, 1265, 1267, 1268};

    public static Boolean isWaitingForRockMove = false;
    private int startXpAtt, startXpStr, startXpDef, startXpRange, startXpMage, startXpHealth; //arrowId;
    private final Color color1 = new Color(255, 255, 255);
    private final Font font1 = new Font("Arial", Font.BOLD, 10);
    private final Image img1 = getImage("http://i.imgur.com/sJQZLHl.png");
    private long startTime;
    public static Boolean foundTarget = false;
    public static RSNPC crab = null;
    public static Boolean isWest = false;
    public static Boolean isRandomHandler = false;

    private Boolean findInCombatCrab = false;

    public static String Version = "0.19";

    @Override
    public void run() {

        Thread gui = new Thread(new VeloxCrabsGUI());
        gui.start();

        //Run GUI - Get foodId
        //Sleep for GUI
        //Setup initial stats and stuff
        while (VeloxCrabsGUI.isGui) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        gui.interrupt();

        startXpAtt = Skills.getXP("ATTACK");
        startXpStr = Skills.getXP("STRENGTH");
        startXpDef = Skills.getXP("DEFENCE");
        startXpRange = Skills.getXP("RANGE");
        startXpMage = Skills.getXP("MAGIC");
        startXpHealth = Skills.getXP("HITPOINTS");
        startTime = System.currentTimeMillis();

        Mouse.setSpeed(250);
        // Create threads for failsafes and eating here
        Thread eating = new Thread(new VeloxCrabsEating());
        Thread failsafe = new Thread(new VeloxCrabsFailsafe());
        Thread randomHandling = new Thread(new VeloxRandomHandler());
        Thread idleFix = new Thread(new VeloxIdleFix());

        failsafe.start();
        randomHandling.start();
        idleFix.start();
        // To stop set each thread's loop to 0
        println("Starting Velox Crabs! :)");
        while(!stop){
            if (Player.getPosition().getX() <= 2696 && !Player.getRSPlayer().isInCombat() && !VeloxRockCrabs.isWest) {
                walkPath(Walking.generateStraightPath(VeloxRockCrabs.whichSide()));
            }
            // Failsafe for eating and such
            if(!isRandomHandler && Skills.getCurrentLevel("Hitpoints") > eatLevel) {
                if (eating.isAlive()) {
                    eating.stop();
                }
                loop();
            } else {
                eating.run();
            }
            if (!failsafe.isAlive()) {
                failsafe.start();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public int loop(){
        if(atCrabs()){
            if (hasFood()) {
                //attackCrabs();
            } else {
                println("Walking to bank");
                walkPath(whichSideToBank());
            }
        }else if (hasFood()) {
            //walkPath(toWestCrabs);
        }

        if(atBank()){
            bank();
        }else{

        }

        return 1;
    }

    public boolean hasFood(){
        if(Inventory.getCount(foodId) > 0){
            return true;
        }
        return false;
    }

    public void attackCrabs() {
        RSNPC[] crabs = null;
        if (!foundTarget) {
            crabs = NPCs.findNearest(crabIds);
            for (RSNPC testCrab : crabs) {
                crabs = NPCs.sortByDistance(Player.getPosition(), crabs);
                if (!testCrab.isInCombat() && testCrab.getInteractingCharacter() == null && !findInCombatCrab) {
                    if (isWest) {
                        if (testCrab.getPosition().getX() < 2689) {
                            crab = testCrab;
                            foundTarget = true;
                        }
                    } else {
                        if (testCrab.getPosition().getX() > 2689) {
                            crab = testCrab;
                            foundTarget = true;
                        }
                    }
                    break;
                }
            }
        }

        if (crab != null && Player.getRSPlayer().getInteractingCharacter() == null && crab.isValid() && !crab.isInCombat() && !findInCombatCrab) {
            if (crab.isOnScreen() && !crab.isInCombat() && Player.getAnimation() == -1) {
                if (crab.getName().equals("Rock Crab") && Player.getAnimation() == -1 && crab.getInteractingCharacter() == null && crab.isValid()) {
                    crab.click("Attack");
                } else if (crab.getName().equals("Rocks")) {
                    crab.click("Walk here");
                    int Timer = 0;
                    while (Player.getRSPlayer().getAnimation() == -1 && !Player.isMoving()) {
                        isWaitingForRockMove = true;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        Timer += 100;
                        println(Timer);
                        if (Timer == 1000) {
                            println("Walking south");
                            Walking.generateStraightPath(new RSTile(2673, 3712));
                            if (walkPath(whichSideSafe())) {
                                println("Walking back to crabs");
                                walkPath(whichSideFromBank());
                            };
                            break;
                        }
                    }
                    if (Player.getPosition().distanceTo(crab.getPosition()) < 3) {
                        crab.click("Attack");
                    }
                    isWaitingForRockMove = false;
                }
            } else if (Player.getAnimation() == -1) {
                Walking.walkTo(crab.getPosition());
            }
        } else if (crab != null) {
            if (!Player.getRSPlayer().isInCombat()) {
                foundTarget = false;
            } else {
                RSNPC attackingMeCrab = NPCs.sortByDistance(Player.getPosition(), NPCs.findNearest("Rock Crab", "Hobgoblin", "Warrior"))[0];

                if (attackingMeCrab.isInteractingWithMe()) {
                    attackingMeCrab.click("Attack");
                }

                if (!attackingMeCrab.isValid()) {
                    foundTarget = false;
                }
            }
        }
    }

    public boolean atBank(){
        if(Player.getPosition().distanceTo(bankTile) < 10) {
            return true;
        }
        return false;
    }

    public boolean atCrabs(){
        if(Player.getPosition().distanceTo(whichSide()) < 18) {
            return true;
        }
        return false;
    }

    public boolean walkPath(RSTile[] path){
        for(int i=closestTile(path);i<path.length;i++){
            Point click = Projection.tileToMinimap(path[i]);
            if(Projection.isInMinimap(click)){
                Mouse.click(click, 1);
                long t = System.currentTimeMillis();
                if(i != path.length-1){
                    while(!Projection.isInMinimap(Projection.tileToMinimap(path[i+1])) && Timing.timeFromMark(t) < 5000){
                        sleep(50);
                    }
                    if(!Projection.isInMinimap(Projection.tileToMinimap(path[i+1]))){
                        Walking.blindWalkTo(path[i+1]);
                    }
                } else {
                    while(distance(Player.getPosition(), path[i]) > 1 && Timing.timeFromMark(t) < 5000){
                        sleep(50);
                    }
                    while(Player.isMoving()){
                        sleep(50);
                    }
                }
            } else {
                println("Searching for path...");
                Walking.blindWalkTo(path[i]);
                long t = System.currentTimeMillis();
                while(distance(Player.getPosition(), path[i]) > 1 && Timing.timeFromMark(t) < 5000){
                    sleep(50);
                }
                while(Player.isMoving()){
                    sleep(50);
                }
            }
        }
        return true;
    }

    public int closestTile(RSTile[] path){
        for(int i=path.length-1; i>=0; i--){
            if(Projection.tileToMinimap(path[i]).getX() != -1){
                println("closest point: " + i);
                return i;
            }
        }
        return 0;
    }

    public void bank(){
        if(Inventory.getAll().length > 0){ //Inventory has stuff
            if(Banking.isBankScreenOpen()){
                //Banking.depositAllExcept(arrowId);
                Banking.depositAllExcept(foodId);
            }else{
                Banking.openBankBooth();
            }
        }else{ //Nothing in our inventory - let's get our food!
            if(Banking.isBankScreenOpen()){ //Our banks open - Let's get our food
                if(Banking.find(foodId).length > 0){ //Food is in bank - Let's get it
                    println("Finding Food");
                    Banking.withdraw(General.random(30, 100), foodId); //Got our food
                    sleep(300, 500);
                }else{ //No food in bank - Stopping Script
                    println("Out of food - Stopping VeloxCrabs");
                    stop = true;
                }
            }else{ //Bank Closed - Open it!
                Banking.openBankBooth();
            }
        }
        if (Inventory.getCount(foodId) > 0) {
            walkPath(whichSideFromBank());
        }
    }

    public boolean walkTo(RSTile destination) {

        if (PathFinding.aStarWalk(destination)) {
            return true;
        }

        return true;
    }

    public int distance(RSTile p1, RSTile p2){
        return (int) Math.round(Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(),  2)));
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    public static RSTile whichSide() {

        RSTile returnTiles = null;

        if (isWest) {
            returnTiles = new RSTile(2675, 3715);
        } else if (!isWest) {
            returnTiles = new RSTile(2709, 3716);
        }


        return returnTiles;
    }

    public static RSTile[] whichSideFromBank() {

        RSTile[] returnTiles = null;

        if (isWest) {
            returnTiles = new RSTile[]{
                    new RSTile(2725, 3486),
                    new RSTile(2719, 3486),
                    new RSTile(2713, 3500),
                    new RSTile(2702, 3509),
                    new RSTile(2689, 3513),
                    new RSTile(2683, 3525),
                    new RSTile(2681, 3541),
                    new RSTile(2674, 3551),
                    new RSTile(2659, 3572),
                    new RSTile(2654, 3581),
                    new RSTile(2654, 3595),
                    new RSTile(2653, 3607),
                    new RSTile(2658, 3619),
                    new RSTile(2659, 3633),
                    new RSTile(2662, 3645),
                    new RSTile(2662, 3657),
                    new RSTile(2670, 3668),
                    new RSTile(2671, 3681),
                    new RSTile(2673, 3694),
                    new RSTile(2674, 3708)
            };
        } else {
            returnTiles = new RSTile[]{
                    new RSTile(2725, 3491),
                    new RSTile(2725, 3486),
                    new RSTile(2719, 3486),
                    new RSTile(2713, 3500),
                    new RSTile(2702, 3509),
                    new RSTile(2689, 3513),
                    new RSTile(2683, 3525),
                    new RSTile(2681, 3541),
                    new RSTile(2674, 3551),
                    new RSTile(2659, 3572),
                    new RSTile(2654, 3581),
                    new RSTile(2654, 3595),
                    new RSTile(2656 ,3607),
                    new RSTile(2671 ,3613),
                    new RSTile(2697 ,3614),
                    new RSTile(2703 ,3633),
                    new RSTile(2700, 3641),
                    new RSTile(2703, 3663),
                    new RSTile(2703, 3663),
                    new RSTile(2703, 3663),
                    new RSTile(2706, 3684),
                    new RSTile(2706, 3703),
                    new RSTile(2706, 3703),
                    new RSTile(2709, 3719)
            };
        }


        return returnTiles;
    }

    public static RSTile[] whichSideToBank() {

        RSTile[] returnTiles = null;

        if (isWest) {
            returnTiles = new RSTile[]{
                    new RSTile(2674, 3708),
                    new RSTile(2673, 3694),
                    new RSTile(2671, 3681),
                    new RSTile(2670, 3668),
                    new RSTile(2662, 3657),
                    new RSTile(2662, 3645),
                    new RSTile(2659, 3633),
                    new RSTile(2658, 3619),
                    new RSTile(2653, 3607),
                    new RSTile(2654, 3595),
                    new RSTile(2654, 3581),
                    new RSTile(2659, 3572),
                    new RSTile(2674, 3551),
                    new RSTile(2681, 3541),
                    new RSTile(2683, 3525),
                    new RSTile(2689, 3513),
                    new RSTile(2702, 3509),
                    new RSTile(2713, 3500),
                    new RSTile(2719, 3486),
                    new RSTile(2725, 3486),
                    new RSTile(2725, 3491)
            };
        } else {
            returnTiles = new RSTile[]{
                    new RSTile(2706, 3703),
                    new RSTile(2706, 3684),
                    new RSTile(2703, 3663),
                    new RSTile(2700, 3641),
                    new RSTile(2703 ,3633),
                    new RSTile(2697 ,3614),
                    new RSTile(2671 ,3613),
                    new RSTile(2656 ,3607),
                    new RSTile(2654, 3595),
                    new RSTile(2654, 3581),
                    new RSTile(2659, 3572),
                    new RSTile(2674, 3551),
                    new RSTile(2681, 3541),
                    new RSTile(2683, 3525),
                    new RSTile(2689, 3513),
                    new RSTile(2702, 3509),
                    new RSTile(2713, 3500),
                    new RSTile(2719, 3486),
                    new RSTile(2725, 3486),
                    new RSTile(2725, 3491),

            };
        }


        return returnTiles;
    }

    public static RSTile[] whichSideSafe() {

        RSTile[] returnTiles = null;

        if (isWest) {
            returnTiles = new RSTile[]{
                    new RSTile(2673, 3712),
                    new RSTile(2674, 3698),
                    new RSTile(2673, 3684)
            };
        } else {
            returnTiles = new RSTile[] {
                    new RSTile(2706, 3703),
                    new RSTile(2706, 3694),
                    new RSTile(2703, 3686)
            };
        }


        return returnTiles;
    }

    public void onPaint(Graphics g1) {

        if (!VeloxCrabsGUI.isGui) {
            Graphics2D g = (Graphics2D)g1;
            g.drawImage(img1, 6, 282, null); //72 up
            g.setFont(font1);
            g.setColor(color1);
            g.drawString("" + (Skills.getXP("ATTACK") - startXpAtt), 231, 368);
            g.drawString("" + (Skills.getXP("STRENGTH") - startXpStr), 231, 382);
            g.drawString("" + (Skills.getXP("DEFENCE") - startXpDef), 231, 397);
            g.drawString("" + toString(System.currentTimeMillis() - startTime), 180, 462);
            g.drawString("" + (Skills.getXP("HITPOINTS") - startXpHealth), 231, 411);
            g.drawString("" + (Skills.getXP("RANGE") - startXpRange), 231, 425);
            g.drawString("" + (Skills.getXP("MAGIC") - startXpMage), 231, 440);
            g.drawString("" + VeloxCrabsFailsafe.Timer, 360, 462);
            if (crab != null && crab.isValid() && foundTarget) {
                try {
                    drawTileNpc(crab.getPosition(), new Color(0, 0, 255, 30), g);

                } catch (RuntimeException e) {
                    println("Error displaying painted tile.");
                }
            }

            if (Player.getRSPlayer().getInteractingCharacter() != null) {
                try {
                    drawTileNpc(Player.getRSPlayer().getInteractingCharacter().getPosition(), new Color(255, 0, 0, 30), g);
                } catch (RuntimeException e) {
                    println("Error displaying interacting NPC");
                }
            }
        }

    }


    private void drawTileNpc(RSTile loc, Color c, Graphics g) {
        if (loc != null) {
            g.setColor(c);
            g.fillPolygon(Projection.getTileBoundsPoly(loc, 1));
        }
    }

    public String toString(final Long ms){
        long s = ms/1000;
        long sec = s % 60;
        long min = (s % 3600) / 60;
        long hrs = s / 3600;

        return hrs + ":" + min + ":" + sec;

    }


}

class VeloxCrabsEating implements Runnable {

    @Override
    public void run() {
        sleep(100);
        while (Skills.getCurrentLevel("Hitpoints") < VeloxRockCrabs.eatLevel) {
            if(Skills.getCurrentLevel("Hitpoints") < VeloxRockCrabs.eatLevel && Inventory.getCount(VeloxRockCrabs.foodId) > 0) {
                RSItem[] food = Inventory.find(VeloxRockCrabs.foodId);
                food[0].click("Eat");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (Skills.getCurrentLevel("Hitpoints") > VeloxRockCrabs.eatLevel) {
                return;
            }
        }
    }
}

class VeloxRandomHandler implements Runnable {

    private int loop = 1;
    private int[] randomNPCs = {2463, 2464, 2465, 2466, 2467, 2468, 6827, 6828, 411, 408};
    RSNPC[] randomNPC;
    RSNPC evilChicken;

    @Override
    public void run() {
        while (loop > 0) {
            randomNPC = NPCs.findNearest(randomNPCs);

            if (randomNPC.length > 0 && randomNPC[0].getPosition().distanceTo(Player.getPosition()) < 3) {
                VeloxRockCrabs.isRandomHandler = true;
                evilChicken = NPCs.sortByDistance(Player.getRSPlayer().getPosition(), randomNPC)[0];
                println("Random Handler");
                Walking.generateStraightPath(new RSTile(2673, 3712));
                if (walkPath(VeloxRockCrabs.whichSideSafe())) {
                    VeloxRockCrabs.isRandomHandler = false;
                    walkPath(VeloxRockCrabs.whichSideFromBank());
                }

            } else {

            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public boolean walkPath(RSTile[] path){
        for(int i=closestTile(path);i<path.length;i++){
            Point click = Projection.tileToMinimap(path[i]);
            if(Projection.isInMinimap(click)){
                Mouse.click(click, 1);
                long t = System.currentTimeMillis();
                if(i != path.length-1){
                    while(!Projection.isInMinimap(Projection.tileToMinimap(path[i+1])) && Timing.timeFromMark(t) < 5000){
                        sleep(50);
                    }
                    if(!Projection.isInMinimap(Projection.tileToMinimap(path[i+1]))){
                        Walking.blindWalkTo(path[i+1]);
                    }
                } else {
                    while(distance(Player.getPosition(), path[i]) > 1 && Timing.timeFromMark(t) < 5000){
                        sleep(50);
                    }
                    while(Player.isMoving()){
                        sleep(50);
                    }
                }
            } else {
                Walking.blindWalkTo(path[i]);
                long t = System.currentTimeMillis();
                while(distance(Player.getPosition(), path[i]) > 1 && Timing.timeFromMark(t) < 5000){
                    sleep(50);
                }
                while(Player.isMoving()){
                    sleep(50);
                }
            }
        }
        return true;
    }

    public int closestTile(RSTile[] path){
        for(int i=path.length-1; i>=0; i--){
            if(Projection.tileToMinimap(path[i]).getX() != -1){
                return i;
            }
        }
        return 0;
    }

    public int distance(RSTile p1, RSTile p2){
        return (int) Math.round(Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(),  2)));
    }
}

class VeloxIdleFix implements Runnable {

    public static int loop = 1;

    @Override
    public void run() {

        while(loop > 0) {
            if (Player.getRSPlayer().getAnimation() == -1) {
                turnCamera();
            }

            try {
                Thread.sleep(General.random(2000, 17000));
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    private void turnCamera() {
        Camera.setCameraRotation(General.random(0, 360));
    }
}

class VeloxCrabsFailsafe implements Runnable {

    public static int loop = 1;
    VeloxRockCrabs test = new VeloxRockCrabs();
    public static int Timer;
    private Boolean isDoneFighting = false;

    @Override
    public void run() {
        while (loop > 0) {
            if (Player.getRSPlayer().getAnimation() != -1) {
                Timer = 0;
            }
            while (!Player.isMoving() && Player.getAnimation() == -1 && !VeloxRockCrabs.isWaitingForRockMove) {
                sleep(10);
                Timer += 10;
                if (Timer >= 10) {
                    isDoneFighting = true;
                }
                //System.out.println("Inactivity Time: " + Timer);
                if (Timer >= 20 || isDoneFighting) {
                    VeloxRockCrabs.foundTarget = false;
                    ResetTimer();
                    test.attackCrabs();
                    isDoneFighting = false;
                    break;
                }
            }
            if (VeloxRockCrabs.crab == null) {
                VeloxRockCrabs.foundTarget = false;
                test.attackCrabs();
            }
        }
    }

    private void ResetTimer() {
        Timer = 0;
    }
}
