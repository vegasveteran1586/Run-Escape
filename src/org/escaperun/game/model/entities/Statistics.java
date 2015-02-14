package org.escaperun.game.model.entities;

import org.escaperun.game.model.items.EquipableItem;

import java.util.*;

public class Statistics{

    protected Map<StatEnum, Integer> statsmap = new HashMap<StatEnum, Integer>();
    protected Map<StatEnum, Integer> currentstats = new HashMap<StatEnum, Integer>(); //ONLY USED FOR AVATAR/ENTITY FOR TEMPORARY STATS. Do not use for Items; instead, use statsmap var.

    //Constructor for Entity-related Statistics
    public Statistics(Occupation occupation, int numoflives) {
        this.initializeSM();//Initialize values of all possibilities.

        statsmap.put(StatEnum.STRENGTH, occupation.getStrength());//get initial STR stat from whatever occupation was
        statsmap.put(StatEnum.AGILITY, occupation.getAgility());//get initial AGI stat from whatever occupation was
        statsmap.put(StatEnum.INTELLECT, occupation.getIntelligence());//get initial INT stat from whatever occupation was
        statsmap.put(StatEnum.HARDINESS, occupation.getHardiness());//get initial HARD stat from whatever occupation was
        statsmap.put(StatEnum.MOVEMENT, occupation.getMovement());//get initial MOV stat from whatever occupation was
        statsmap.put(StatEnum.NUMOFLIVES, numoflives);//NumberOfLives is arg passed in (usu. 3 for avi, 1 for else)
        getLevel();//Derived level initially from started EXP (0).
        getOffensiveRate();//Calculate OR from the method provided initially.
        getDefensiveRate();//Calculate DR from the method provided initially.
        getArmorRate();//Calculate AR from the method provided initially.
        getMaxHP(); //Calculate HP from the method provided initially.
        getMaxMP(); //Calculate MP from the method provided intiially.

        currentstats.putAll(statsmap); //Initialize currentstats with a "copy" of our base stats.
        currentstats.put(StatEnum.CURRENTHP, statsmap.get(StatEnum.MAXHP)); //Current HP = Max HP
        currentstats.put(StatEnum.CURRENTMP, statsmap.get(StatEnum.MAXMP)); //Current MP = Max MP
    }

    //Constructor for weapon/armor-related Statistics... Utilizes a Map in order to find what stats it has.
    public Statistics(Map<StatEnum, Integer> itemstats) {
        this.initializeSM();//Initialize values of all possibilities.

        statsmap.putAll(itemstats); //Move those values from the item's generated Map to its Statistics object.
    }

    public Statistics(){
        this.initializeSM();//Intiailize all things (except currentHP/MP) to Zero.
    }

    //Initializes the Map "statsmap" to have all zero values for the possible Enumeration types. Used in constructor methods.
    private void initializeSM(){
        statsmap.put(StatEnum.STRENGTH, 0);//get initial STR stat
        statsmap.put(StatEnum.AGILITY, 0);//get initial AGI stat
        statsmap.put(StatEnum.INTELLECT, 0);//get initial INT stat
        statsmap.put(StatEnum.HARDINESS, 0);//get initial HARD stat
        statsmap.put(StatEnum.MOVEMENT, 0);//get initial MOV stat
        statsmap.put(StatEnum.NUMOFLIVES, 0);//NumberOfLives
        statsmap.put(StatEnum.LEVEL, 0);//Derived level initially from started EXP (0).
        statsmap.put(StatEnum.EXP, 0);//EXP always starts at level zero (or one if we choose to do it that way).
//        statsmap.put(StatEnum.TEMPSTR, 0);//Temporary STR = 0 at start.
//        statsmap.put(StatEnum.TEMPAGI, 0);//Temporary AGI = 0 at start.
//        statsmap.put(StatEnum.TEMPINT, 0);//Temporary INT = 0 at start.
//        statsmap.put(StatEnum.TEMPMOV, 0);//Temporary MOV = 0 at start.
//        statsmap.put(StatEnum.TEMPHAR,0);//Temporary HAR = 0 at start.
        statsmap.put(StatEnum.MAXHP, 0);//store initial val of MaxHP
        statsmap.put(StatEnum.MAXMP, 0);//store initial val of MaxMP
//        statsmap.put(StatEnum.CURRENTHP, 0);//store initial val of CurrentHP // Don't want current values to plague currentstats in updateStats() (e.g., rewrite to MaxHP or 0 val)
//        statsmap.put(StatEnum.CURRENTMP, 0);//store initial val of CurrentMP // Don't want current values to plague currentstats in updateStats() (e.g., rewrite to MaxHP or 0 val)
        statsmap.put(StatEnum.OFFENSERATE, 0);//store initial val of OR
        statsmap.put(StatEnum.DEFENSERATE, 0);//store initial val of DR
        statsmap.put(StatEnum.ARMORRATE, 0);//store initial val of AR
    }

    protected void updateStats(Equipment equipment){
        currentstats.putAll(statsmap); //Reset our currentstats object to have default values.
        Collection<EquipableItem> equipitems = equipment.getEquipment().values();
        Iterator<EquipableItem> iterator = equipitems.iterator();
        while(iterator.hasNext()){
            addStats(iterator.next().getStats());
        }
    }

    //Boolean is for return from "isGameOver()" method. If game is over (num of lives is 0), returns true from it. else false
    protected boolean takeDamage(int damage){
        int newHP = (currentstats.get(StatEnum.CURRENTHP) - damage);
        if(newHP > 0){//If we're still alive.
            currentstats.put(StatEnum.CURRENTHP, newHP);
            return false;
        }
        else{
            currentstats.put(StatEnum.CURRENTHP, 0);
            System.out.println("WE ARE DEAD!!!");
            return isGameOver();
        }
    }

    //Method that is (as of now) called only by takeDamage if our currentHP drops to or below zero.
    protected boolean isGameOver(){
        statsmap.put(StatEnum.NUMOFLIVES, statsmap.get(StatEnum.NUMOFLIVES)-1);
        if(statsmap.get(StatEnum.NUMOFLIVES) == 0) {
            System.out.println("GAME OVER!");
            return true;
        }
        else return false;
    }

    protected void healDamage(int healz){
        if(currentstats.get(StatEnum.CURRENTHP) + healz > currentstats.get(StatEnum.MAXHP))
            currentstats.put(StatEnum.CURRENTHP, currentstats.get(StatEnum.MAXHP));
        else currentstats.put(StatEnum.CURRENTHP, currentstats.get(StatEnum.CURRENTHP)+healz);
    }

    protected void useMana(int mana){
        //TODO: Implement MP-related methods
    }

    protected void addStats(Statistics itemstat) {
        Set<Map.Entry<StatEnum, Integer>> entries = itemstat.statsmap.entrySet();
        Iterator<Map.Entry<StatEnum, Integer>> iterator = entries.iterator();

        while(iterator.hasNext())
        {
            Map.Entry<StatEnum, Integer> entry = iterator.next();
            currentstats.put(entry.getKey(), (entry.getValue() + this.currentstats.get(entry.getKey())));
            //Above statement: Put the new value at a certain key got from the entry, the current value found in our statsmap + the new value found in the entry.
        }
    }

    protected int getLevel() {
        int before = statsmap.get(StatEnum.LEVEL);
        statsmap.put(StatEnum.LEVEL, 1 + (statsmap.get(StatEnum.EXP) / 10));//Simple EXP->LVL formula for now; will model it more later;
        //TODO: Put in special functionality if a check determines that calculated level and level stored are different, aka Avatar leveled.
        if(before != statsmap.get(StatEnum.LEVEL) && before > 0)
            System.out.println("ENTITY LEVELED UP!!!");
        return statsmap.get(StatEnum.LEVEL); //Return the newly updated (if at all) value of level calculated.
    }

    protected int getMaxHP(){
        statsmap.put(StatEnum.MAXHP, getLevel() + statsmap.get(StatEnum.HARDINESS));
        //Formula for MaxHP: Hardiness + Level
        return statsmap.get(StatEnum.MAXHP);
    }

    protected int getMaxMP(){
        statsmap.put(StatEnum.MAXHP, getLevel() + statsmap.get(StatEnum.INTELLECT));
        //Formula for MaxHP: Intellect + Level
        return statsmap.get(StatEnum.MAXMP);
    }

    protected int getOffensiveRate(){
        statsmap.put(StatEnum.OFFENSERATE, (statsmap.get(StatEnum.STRENGTH)+getLevel()));
        //Formula for OR: Strength + TempSTR + (derived) Level
        return statsmap.get(StatEnum.OFFENSERATE);
    }

    protected int getDefensiveRate(){
        statsmap.put(StatEnum.DEFENSERATE, (statsmap.get(StatEnum.AGILITY)+getLevel()));
        //Formula for DR: Agility + TempAGI + (derived) Level
        return statsmap.get(StatEnum.DEFENSERATE);
    }

    protected int getArmorRate(){
        statsmap.put(StatEnum.ARMORRATE, (statsmap.get(StatEnum.HARDINESS)));
        //Formula for AR: Hardiness + TempHAR
        return statsmap.get(StatEnum.ARMORRATE);
    }

    //Unnecessary getter method removed. -Jeff

    protected void setStat(StatEnum se, int valueofchange)
    {
        statsmap.put(se, valueofchange);
    }

    public String leveltoString() {
        String stat = "Level: " + currentstats.get(StatEnum.LEVEL);
        return stat;
    }

    public String exptoString() {
        String stat = "Exp:" + currentstats.get(StatEnum.EXP);
        return stat;
    }

    public String livestoString() {
        String stat = "Lives: " + currentstats.get(StatEnum.NUMOFLIVES);
        return stat;
    }

    public String healthtoString() {
        String stat = "Health: " + currentstats.get(StatEnum.CURRENTHP) + "/" + currentstats.get(StatEnum.MAXHP);
        return stat;
    }

    public String manatoString() {
        String stat = "Mana: " + currentstats.get(StatEnum.CURRENTMP) + "/" + currentstats.get(StatEnum.MAXMP);
        return stat;
    }

    public String offensetoString() {
        String stat = "Offense: " + currentstats.get(StatEnum.OFFENSERATE);
        return stat;
    }

    public String defensetoString() {
        String stat = "Defense: " + currentstats.get(StatEnum.DEFENSERATE);
        return stat;
    }

    public String armourtoString() {
        String stat = "Armour: " + currentstats.get(StatEnum.ARMORRATE);
        return stat;
    }

    public String strengthtoString() {
        String stat = "Strength: " + currentstats.get(StatEnum.STRENGTH);
        return stat;
    }

    public String agilitytoString() {
        String stat = "Agility: " + currentstats.get(StatEnum.AGILITY);
        return stat;
    }

    public String intellecttoString() {
        String stat = "Intellect: " + currentstats.get(StatEnum.INTELLECT);
        return stat;
    }

    public String hardinesstoString() {
        String stat = "Hardiness: " + currentstats.get(StatEnum.HARDINESS);
        return stat;
    }

    public String movementtoString() {
        String stat ="Movement: " + currentstats.get(StatEnum.MOVEMENT);
        return stat;
    }
}

