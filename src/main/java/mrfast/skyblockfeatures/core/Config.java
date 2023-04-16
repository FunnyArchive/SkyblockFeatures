package mrfast.skyblockfeatures.core;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.*;

import java.awt.Color;
import java.io.File;

public class Config extends Vigilant {
    @Property(
            type = PropertyType.TEXT,
            name = "Hypixel API Key",
            description = "Your Hypixel API key\nObtained by running §a/api new",
            category = "General",
            subcategory = "API"
    )
    public String apiKey = "";

    @Property(
            type = PropertyType.SLIDER,
            name = "Times Game Restarted",
            description = "",
            category = "General",
            subcategory = "Reparty",
            hidden = true,
            max = 100000
    )
    public int timeStartedUp = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "First Launch",
            description = "Used to see if the user is a new user of skyblockfeatures.",
            category = "General",
            subcategory = "Other",
            hidden = true
    )
    public boolean firstLaunch = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Potion Effects",
            description = "Hide the potion effects inside your inventory while on skyblock",
            category = "General",
            subcategory = "Other"
    )
    public boolean hidepotion = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Red Numbers From Sidebar",
            description = "Hide the red numbers from the sidebar",
            category = "General",
            subcategory = "Sidebar"
    )
    public boolean hideRedNumbers = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Draw Text With Shadow",
            description = "Draws the text on the sidebar with a shadow",
            category = "General",
            subcategory = "Sidebar"
    )
    public boolean drawTextWithShadow = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Hypixel From sidebar",
            description = "Hide the www.hypixel.net the sidebar bottom",
            category = "General",
            subcategory = "Sidebar"
    )
    public boolean hideHypixelSidebar = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto-Accept Reparty",
            description = "Automatically accepts reparty invites",
            category = "General",
            subcategory = "Reparty"
    )
    public boolean autoReparty = false;

    @Property(
            type = PropertyType.SLIDER,
            name = "Auto-Accept Reparty Timeout",
            description = "Timeout in seconds for accepting a reparty invite",
            category = "General",
            subcategory = "Reparty",
            max = 120
    )
    public int autoRepartyTimeout = 60;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dungeon Blocks",
            description = "Highlights important blocks in Dungeons.",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean dungeonBlocks = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Join Dungeon",
            description = "Auto joins the dungeon when the party is full. §cWarning Use At Own Risk",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean autoJoinDungeon = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Box Shadow Assasins",
            description = "Draws a box around invisible shadow assasins when their sword is visible.",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean boxShadowAssasins = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Shadow Assassin Notify",
            description = "Notify when there is a nearby shadow assasin thats invisible.",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean shadowAssassinNotify = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Quick Close Chest",
            description = "Press any key or click to close secret chest screen",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean quickCloseChest = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Doors",
            description = "Highlights wither door and blood doors",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean highlightDoors = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Blaze Solver",
            description = "Highlights the correct blazes to shoot.",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean blazeSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Quiz Solver",
            description = "Highlights the correct answer.",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean quizSolver = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Tic-Tac-Toe Solver",
        description = "Highlights where to go in Tic-Tac-Toe puzzle.",
        category = "§1§rDungeons",
        subcategory = "Solvers"
    )
    public boolean TicTacToeSolver = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Ice Path Solver",
        description = "Highlights the path for the silverfish to follow",
        category = "§1§rDungeons",
        subcategory = "Solvers"
    )
    public boolean IcePathSolver = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Boulder Solver",
        description = "Highlights the buttons to press",
        category = "§1§rDungeons",
        subcategory = "Solvers"
    )
    public boolean BoulderSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Waterboard Solver",
            description = "Highlights levers to flip to solve waterboard",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean waterboard = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Teleport Pad Solver",
            description = "Highlights teleport pads that you have stepped on",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean teleportPadSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Icefill Solver",
            description = "Solves ice fill puzzle",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean IceFillSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Creeper Solver",
            description = "Highlights the lanterns to shoot in Creeper puzzle.",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean creeperSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "3 Weirdos Solver",
            description = "Highlights the correct chest to open.",
            category = "§1§rDungeons",
            subcategory = "Solvers"
    )
    public boolean ThreeWeirdosSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crypt Display",
            description = "Big count of how many crypts have been killed",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean cryptCount = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Blessings Viewer",
            description = "Displays the current blessings in a dungeons",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean blessingViewer = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Bats",
            description = "Draws a box around bats to make bats easier to find",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean highlightBats = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Gifts",
            description = "Highlights with a yellow box of where gifts are at the Jerry's workshop.",
            category = "Render",
	    subcategory = "Highlights"
    )
    public boolean presentWaypoints = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glacial Cave Ice Treasure Detector",
            description = "Highlights ice treasures in the wall when inside the Glacial Cave",
            category = "Render",
	    subcategory = "Glacial Cave"
    )
    public boolean icecaveHighlight = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glacial Cave Ice Treasure Tracker",
            description = "Tracks the items you get from ice treasures",
            category = "Mining",
	    subcategory = "Trackers"
    )
    public boolean IceTreasureTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Ender Node Tracker",
            description = "Tracks the items you get from ender nodes",
            category = "Mining",
	    subcategory = "Trackers"
    )
    public boolean EnderNodeTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Day Tracker",
            description = "Tracks the day in the Crystal Hollows",
            category = "Mining",
	    subcategory = "Trackers"
    )
    public boolean dayTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Ender Nodes",
            description = "Highlights the sparkly blocks in the end",
            category = "Mining",
	    subcategory = "End Island"
    )
    public boolean highlightEnderNodes = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Through the walls",
            description = "Makes the Ender Node Highlight go through walls. §cWarning Use At Own Risk",
            category = "Mining",
	    subcategory = "End Island"
    )
    public boolean highlightEnderNodesWalls = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Detector Through Walls",
            description = "§cWarning Use At Own Risk",
            category = "Render",
	    subcategory = "Glacial Cave"
    )
    public boolean icecaveHighlightWalls = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dungeon Chest Profit",
            description = "Shows the estimated profit for items from chests in dungeons.",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean dungeonChestProfit = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Health and Mana",
            description = "Hides Health and Mana from action bar",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean hidethings = false;

     @Property(
            type = PropertyType.SWITCH,
            name = "Health Display",
            description = "Moveable health display",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean HealthDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Speed Display",
            description = "Moveable Speed display",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean SpeedDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Effective Health Display",
            description = "Moveable Effective Health display",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean EffectiveHealthDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mana Display",
            description = "Moveable mana",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean ManaDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Defence Display",
            description = "Moveable defence display",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean DefenceDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Secrets Display",
            description = "Moveable Secrets display",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean SecretsDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Revive Stone Names",
            description = "Shows names next to the heads on the Revive Stone menu.",
            category = "§1§rDungeons",
            subcategory = "Quality of Life"
    )
    public boolean reviveStoneNames = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Spirit Leap Names",
            description = "Shows names next to the heads on the Spirit Leap menu.",
            category = "§1§rDungeons",
            subcategory = "Quality of Life"
    )
    public boolean spiritLeapNames = false;

     @Property(
            type = PropertyType.SWITCH,
            name = "Better Party Finder",
            description = "Highlight parties you can't join with red",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean betterpartys = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Correct Livid",
            description = "Highlights the incorrect livid",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean highlightCorrectLivid = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Fairy Soul Helper",
            description = "Highlights nearby fairy souls using waypoints",
            category = "Helpers",
            subcategory = "General"
    )
    public boolean fairy = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Bazaar Flipping Helper",
            description = "Highlights bazaar items that will give you profit by selling to NPC",
            category = "Helpers",
            subcategory = "General"
    )
    public boolean bazaarFlip = false;
    
    @Property(
        type = PropertyType.SWITCH,
        name = "Relic Helper",
        description = "Highlights relics in the §cSpider's Den§r using waypoints",
        category = "Helpers",
        subcategory = "General"
    )
    public boolean spiderRelicHelper = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Armor Bar",
            description = "Hide the armor icons above health bar",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean armorbar = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Hunger Bar",
            description = "Hide the food icons above hotbar",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean hungerbar = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Health Hearts",
            description = "Hide the health icons above health bar",
            category = "General",
            subcategory = "Health & Mana Bars"
    )
    public boolean healthsbar = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Player Nametags",
            description = "Stops player's nametags from renderering",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hidePlayerNametags = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Normal Fullbright",
            description = "Normal classic full bright everywhere",
            category = "Render",
            subcategory = "Fullbright"
    )
    public boolean fullbright = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dynamic Fullbright",
            description = "Turns on Fullbright in §aCrystal Hollows§r,§aYour Island§r,§aDungeons",
            category = "Render",
            subcategory = "Fullbright"
    )
    public boolean DynamicFullbright = false;

    @Property(
            type = PropertyType.SLIDER,
            name = "Dynamic Fullbright Disabled Value",
            description = "Value of brightness to set when in the certain locations",
            category = "Render",
            subcategory = "Fullbright",
            max = 100,
            min = 1
    )
    public int DynamicFullbrightDisabled = 100;

    @Property(
            type = PropertyType.SLIDER,
            name = "Dynamic Fullbright Enabled Value",
            description = "Value of brightness to set when everywhere else",
            category = "Render",
            subcategory = "Fullbright",
            max = 100,
            min = 1
    )
    public int DynamicFullbrightElsewhere = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide All Nametags",
            description = "Stops all nametags from renderering",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideAllNametags = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide players near NPC's",
        description = "Bye bye players",
        category = "Render",
        subcategory = "Hide Things"
    )
    public boolean hidePlayersNearNPC = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Arrows",
        description = "Stops arrows from being rendered.",
        category = "Render",
        subcategory = "Hide Things"
    )
    public boolean hideArrows = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Fishing Helper",
            description = "Displays a box of where the fish could be and the radius of it to the bobber",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean fishthing = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Bait Display",
            description = "Displays the current bait and amount in your Fishing Bag",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean baitCounter = false;
    
    @Property(
            type = PropertyType.SWITCH,
            name = "Display Tree Capitator Cooldown",
            description = "Displays the cooldown for the treecapitator",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean treecapitatorCooldown = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Display Conjuring Cooldown",
            description = "Displays the cooldown for the Conjuring",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean ConjuringCooldown = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Onscreen Clock",
            description = "Display a clock",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean clock = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Jerry Timer",
            description = "Shows the cooldown for spawning jerry's",
            category = "§1§rEvents",
            subcategory = "Mayor Jerry"
    )
    public boolean jerry = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Jerry Mode",
            description = "JERRYYY",
            category = "General",
            subcategory = "Jerry Mode",
            hidden = true
    )
    public boolean jerryMode = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Use Smooth Font",
            description = "Uses a smoother font to render text. §cRequires restart",
            category = "§1§rFun",
            subcategory = "Gui"
    )
    public boolean customFont = false;

    @Property(type = PropertyType.COLOR,name = "Gui Lines",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color guiLines = new Color(0x808080);

    @Property(type = PropertyType.COLOR,name = "Selected Category Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color selectedCategory = new Color(0x02A9EA);

    @Property(type = PropertyType.COLOR,name = "Hovered Category Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color hoveredCategory = new Color(0x2CC8F7);

    @Property(type = PropertyType.COLOR,name = "Default Category Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color defaultCategory = new Color(0xFFFFFF);

    @Property(type = PropertyType.COLOR,name = "Feature Box Outline",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color featureBoxOutline = new Color(0xa9a9a9);

    @Property(type = PropertyType.COLOR,name = "Feature Description Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color featureDescription = new Color(0xbbbbbb);

    @Property(type = PropertyType.COLOR,name = "Main Box Background",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color mainBackground = new Color(25,25,25,200);

    @Property(type = PropertyType.COLOR,name = "Search Box Background",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color searchBoxBackground = new Color(120,120,120,60);

    @Property(type = PropertyType.COLOR,name = "Button Background",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color editGuiUnhovered = new Color(0,0,0,50);
    
    @Property(type = PropertyType.COLOR,name = "Button Hover Background",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color editGuiHovered = new Color(0,0,0,75);

    @Property(type = PropertyType.COLOR,name = "Edit Gui Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color editGuiText = new Color(0xFFFFFF);

    @Property(type = PropertyType.COLOR,name = "Title Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color titleColor = new Color(0x00FFFF);

    @Property(type = PropertyType.COLOR,name = "Version Text",description = "",category = "§1§rGui",subcategory = "Colors")
    public Color versionColor = new Color(0xFFFFFF);

    @Property(
            type = PropertyType.SWITCH,
            name = "Player Disguiser",
            description = "Disguises players as different things",
            category = "§1§rFun",
            subcategory = "Player"
    )
    public boolean playerDiguiser = false;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Disguise Players As",
            category = "§1§rFun",
            subcategory = "Player",
            options = {"Cow","Pig","Sheep","Zombie","Jerry","Enderman","Giant","Baby Player","Monki"}
    )
    public int DisguisePlayersAs = 0;

    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Player Cape",
            category = "§1§rFun",
            description = "Paste a image url to give yourself a cape!\n§aEx. https://i.imgur.com/wHk1W6X.png",
            subcategory = "Player"
    )
    public String playerCapeURL = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Diana Mythological Helper",
            description = "Draw an extended line of where the Mythological burrow could be",
            category = "§1§rEvents",
            subcategory = "Diana"
    )
    public boolean MythologicalHelper = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crop Counter",
            description = "Shows the amount of crops on the hoe your holding",
            category = "§1§rFarming",
            subcategory = "Quality of Life"
    )
    public boolean Counter = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Trevor The Trapper Helper",
            description = "Shows the biome and location of the hunted mob",
            category = "Helpers",
            subcategory = "General"
    )
    public boolean trevorHelper = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "School work reminder",
            description = "Remindes you every 30 minutes to do schoolwork",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean SchoolworkReminder = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "1.7 Animations",
            description = "",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean oldAnimations = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Far Entitys in hub",
            description = "",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean HideFarEntity = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Damage Tint",
            description = "Makes your screen get more red the lower in health you are",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean damagetint = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "NameTags",
            description = "Render better nametags in dungeons",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean NameTags = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Score Estimate",
            description = "Shows an estimate for the score of the dungeon run",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean ScoreCalculation = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Trash",
            description = "Draws a red box around items that just fill up your inventory. \nExample §aDreadlord Sword§r, §aMachine Gun Bow",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean highlightTrash = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dungeon Map",
            description = "Render a moveable dungeon map on screen",
            category = "§1§rDungeons",
            subcategory = "Dungeon Map"
    )
    public boolean dungeonMap = false;

    @Property(
            type = PropertyType.SLIDER,
            name = "Dungeon Map Head Scale",
            description = "Scale the size of the heads on the dungeon map §3(Percent)",
            category = "§1§rDungeons",
            subcategory = "Dungeon Map",
            min = 50,
            max = 150
    )
    public int dungeonMapHeadScale = 100;

    @Property(
            type = PropertyType.SWITCH,
            name = "Center Player on Dungeon Map",
            description = "Locks your player to the center of the dungeon map",
            category = "§1§rDungeons",
            subcategory = "Dungeon Map"
    )
    public boolean dungeonMapCenter = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Rotate Dungeon Map",
            description = "Rotates dungeon map based on your rotation",
            category = "§1§rDungeons",
            subcategory = "Dungeon Map"
    )
    public boolean dungeonMapRotate = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dungeon Map Outline Heads",
            description = "Adds an outline the the player heads on the dungeon map",
            category = "§1§rDungeons",
            subcategory = "Dungeon Map"
    )
    public boolean dungeonMapOutlineHeads = true;

     @Property(
            type = PropertyType.SWITCH,
            name = "Quick Start",
            description = "Sends a chat message at the end of a dungeon that can be used to reparty or warp out of a dungeon",
            category = "§1§rDungeons",
            subcategory = "Miscellaneous"
    )
    public boolean quickStart = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glowing Dungeon Teammates!",
            description = "Make your teamates glow based on there class in dungeons.",
            category = "General",
            subcategory = "1.9 Glow Effect"
    )
    public boolean glowingPlayers = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glowing Players",
            description = "Make visible players anywhere glow",
            category = "General",
            subcategory = "1.9 Glow Effect"
    )
    public boolean playeresp = false;


    @Property(
            type = PropertyType.SWITCH,
            name = "Party Glow!",
            description = "Makes your party members glow blue!",
            category = "General",
            subcategory = "1.9 Glow Effect"
    )
    public boolean glowingParty = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Glowing Items!",
            description = "Make items glow depending on rarity",
            category = "General",
            subcategory = "1.9 Glow Effect"
    )
    public boolean glowingItems = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hidden Jerry Alert",
            description = "Displays an alert when you find a hidden Jerry.",
            category = "§1§rEvents",
            subcategory = "Mayor Jerry"
    )
    public boolean hiddenJerryAlert = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Treasure Chest Solver",
            description = "Highlights the particles to look at when opening a treasure chest.",
            category = "Mining",
            subcategory = "Solvers"
    )
    public boolean treasureChestSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automaton Loot Tracker",
            description = "Tracks the loot from Automatons. Starts after a Automaton is killed",
            category = "Mining",
            subcategory = "Trackers"
    )
    public boolean AutomatonTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Gemstone Tracker",
            description = "Tracks the stats from mining gemstones like Coins per hour",
            category = "Mining",
            subcategory = "Trackers"
    )
    public boolean gemstoneTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Ghost Tracker",
            description = "Tracks the loot gained from killing Ghosts",
            category = "Mining",
            subcategory = "Trackers"
    )
    public boolean ghostTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Powder Mining Tracker",
            description = "Tracks the stats from mining gemstones like Coins per hour",
            category = "Mining",
            subcategory = "Trackers"
    )
    public boolean PowderTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Commissions Tracker",
            description = "Tracks your progress on commissions",
            category = "Mining",
            subcategory = "Quality of Life"
    )
    public boolean CommisionsTracker = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Placed Cobblestone",
            description = "Highlights the cobblestone you place in crystal hollows",
            category = "Mining",
            subcategory = "Quality of Life"
    )
    public boolean highlightCobblestone = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crystal Hollows Map",
            description = "Show a map of the crystal hollows",
            category = "Mining",
            subcategory = "Quality of Life"
    )
    public boolean CrystalHollowsMap = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Crystal Hollows Map Heads",
            description = "Show a heads instead of a marker on the crystal hollows map",
            category = "Mining",
            subcategory = "Quality of Life"
    )
    public boolean CrystalHollowsMapHeads = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Dwarven Mines Map",
            description = "Show a map of the dwarven map",
            category = "Mining",
            subcategory = "Quality of Life"
    )
    public boolean dwarvenMinesMap = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Puzzler Solver",
            description = "Shows which block to mine for Puzzler.",
            category = "Mining",
            subcategory = "Solvers"
    )
    public boolean puzzlerSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mines of Divan Metal Detector Solver",
            description = "Shows where the treasure chest is in the Mines of Divan",
            category = "Mining",
            subcategory = "Solvers"
    )
    public boolean MetalDetectorSolver = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show NPC Sell Price",
            description = "Shows the NPC Sell Price on certain items.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showNPCSellPrice = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Skyblock Item ID",
            description = "Shows an items skyblock ID in the lore.",
            category = "Miscellaneous",
            subcategory = "Items"
    )
    public boolean showSkyblockID = false;
    
    @Property(
            type = PropertyType.SWITCH,
            name = "TNT Timer",
            description = "Shows the time till tnt exploads",
            category = "Miscellaneous",
            subcategory = "Items"
    )
    public boolean tntTimer = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Prehistoric Egg Distance Counter",
            description = "Shows the blocks walked on the prehistoric egg item",
            category = "Miscellaneous",
            subcategory = "Items"
    )
    public boolean egg = false;

     @Property(
            type = PropertyType.SWITCH,
            name = "Show teleport overlay",
            description = "Highlights the block that your teleporting to with Aspect of the End or Aspect of the Void",
            category = "Miscellaneous",
            subcategory = "Items"
    )
    public boolean teleportDestination = false;

     @Property(
            type = PropertyType.SWITCH,
            name = "Timestamps",
            description = "Add Chat Timestamps to Messages",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean timestamps = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Enchanting Solvers",
            description = "Solvers for ultrasequencer and chronomotron",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean enchantingSolvers = false;
    

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide White Square",
            description = "Hide the hover highlight Square in inventories",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean hideWhiteSquare = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Zealot Spawn Areas & Spawn Timer",
            description = "Draws where zealots spawn and when zealots will spawn. (this includes bruisers)",
            category = "§1§rFarming",
            subcategory = "Quality of Life"
    )
    public boolean showZealotSpawns = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Garden Visitor Overlay",
            description = "Shows the extra information inside the Garden Visitor Gui.",
            category = "§1§rFarming",
            subcategory = "Garden"
    )
    public boolean GardenVisitorOverlay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Blocks to Destroy Overlay",
            description = "Shows the blocks needed to destroy when clearing a plot in the garden.",
            category = "§1§rFarming",
            subcategory = "Garden"
    )
    public boolean GardenBlocksToRemove = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Make Zealots Glow",
        description = "Applys the 1.9 glow effect to zealots",
        category = "§1§rFarming",
        subcategory = "Quality of Life"
    )
    public boolean glowingZealots = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Highlight Glowing Mushrooms",
        description = "Highlights glowing mushrooms in the Glowing Mushroom Cave",
        category = "§1§rFarming",
        subcategory = "Quality of Life"
    )
    public boolean highlightMushrooms = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "1.12 Crop Hitbox",
        description = "Applys full sized hitbox for crops",
        category = "§1§rFarming",
        subcategory = "Quality of Life"
    )
    public boolean cropBox = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Air Display",
            description = "Prevents the game from rendering the air bubbles while underwater.",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean hideAirDisplay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Fire on Entities",
            description = "Prevents the game from rendering fire on burning entities.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideEntityFire = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Jerry Rune",
            description = "Prevents the game from rendering the items spawned by the Jerry rune.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideJerryRune = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Lightning",
            description = "Prevents all lightning from rendering.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideLightning = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Mob Death Particles",
            description = "Hides the smoke particles created when mobs die.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideDeathParticles = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Geyser Particles",
            description = "Hides the annoying particles in the §6Blazing Volcano.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideGeyserParticles = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Geyser Box",
            description = "Creates a box of where the geyser area is in the §6Blazing Volcano",
            category = "Render",
            subcategory = "Highlights"
    )
    public boolean geyserBoundingBox = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "No Fire",
            description = "Removes first-person fire overlay when you are burning.",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean noFire = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "No Hurtcam",
            description = "Removes the screen shake when you are hurt.",
            category = "Miscellaneous",
            subcategory = "Quality of Life"
    )
    public boolean noHurtcam = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Lowest BIN Price",
            description = "Shows the lowest Buy It Now price for various items in Skyblock.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showLowestBINPrice = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Price Paid",
            description = "Shows the price you bought an item for.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showPricePaid = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Bazaar Price",
            description = "Shows the bazaar price for various items in Skyblock.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showBazaarPrice = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Sales Per Day",
            description = "Shows the sales per day for various items in Skyblock.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showSalesPerDay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Estimated Price",
            description = "Shows the estimated price for various items in Skyblock. Calculates using things like enchants and stars",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showEstimatedPrice = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Average BIN Price",
            description = "Shows the average Buy It Now price for various items in Skyblock.",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showAvgLowestBINPrice = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Helpful Auction Guis",
            description = "Shows the extra information about your own and others auctions.",
            category = "Helpers",
            subcategory = "Auction Utils"
    )
    public boolean auctionGuis = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Condense Item Price Info",
            description = "Only shows the things like Average BIN, Lowest BIN, Sales/Day when the Shift key is held",
            category = "Miscellaneous",
            subcategory = "Item Price Info"
    )
    public boolean showPriceInfoOnShift = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Minion Overlay",
            description = "Shows the extra information inside the minion gui.",
            category = "Miscellaneous",
            subcategory = "Overlay"
    )
    public boolean minionOverlay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Trade Gui",
            description = "Shows the extra information inside the trade gui.",
            category = "Miscellaneous",
            subcategory = "Overlay"
    )
    public boolean tradeOverlay = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Missing Accessories",
            description = "Shows a list of what talismans your missing when in your accessory bag",
            category = "Miscellaneous",
            subcategory = "Overlay"
    )
    public boolean showMissingAccessories = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Extra Profile Info",
            description = "Shows a a players networth,discord,weight, and skill avg when you right click on someone",
            category = "Miscellaneous",
            subcategory = "Overlay"
    )
    public boolean extraProfileInfo = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Auctions For Flipping",
            description = "Highlights auctions that have 100,000 profit or more.",
            category = "Helpers",
            subcategory = "Auction Utils"
    )
    public boolean highlightAuctionProfit = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Highlight Losing Auctions Red",
            description = "Highlights auctions that you arent winning",
            category = "Helpers",
            subcategory = "Auction Utils"
    )
    public boolean highlightlosingAuction = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "BIN Flipper",
            description = "Shows you BIN that have a flip value of more than your margin.\n§cDo not put 100% trust in the mod, it can and probably will make mistakes.",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public boolean autoAuctionFlip = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Include Auction Flips",
        description = "Check auctions for flips",
        category = "§1§rBIN Flipper",
        subcategory = "BIN Flipper Settings"
    )
    public boolean autoFlipAuction = true;
    
    @Property(
        type = PropertyType.SWITCH,
        name = "Include BIN Flips",
        description = "Check BIN for flips §c(Risky)",
        category = "§1§rBIN Flipper",
        subcategory = "BIN Flipper Settings"
    )
    public boolean autoFlipBIN = true;

    @Property(
            type = PropertyType.TEXT,
            name = "Profit Margin",
            description = "The minimum amount of profit for an auction to be shown to you. §3(Numbers Only)",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public String autoAuctionFlipMargin = "100000";
    
    @Property(
            type = PropertyType.TEXT,
            name = "Minimum Volume",
            description = "The minimum amount of sales per day for an auction to be shown to you. §3(Numbers Only)",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public String autoAuctionFlipMinVolume = "1";
 
    @Property(
            type = PropertyType.TEXT,
            name = "Minimum Flip Percent",
            description = "The minimum percent of profit from an auction to be shown to you. §3(Numbers Only)",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public String autoAuctionFlipMinPercent = "5";

    @Property(
            type = PropertyType.SWITCH,
            name = "Make Purse Max Amount",
            description = "Make the amount of money you can spend on an auction equal to your purse.",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public boolean autoAuctionFlipSetPurse = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Change Item Estimation",
        description = "Include stars and enchants into item value estimation.",
        category = "§1§rBIN Flipper",
        subcategory = "BIN Flipper Settings"
    )
    public boolean autoFlipAddEnchAndStar = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Refresh Countdown",
            description = "Show the countdown till refreshing.",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public boolean autoAuctionFlipCounter = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Open",
            description = "Opens up the bid menu for the item with the highest profit. \n§cThis is slower than holding down key",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public boolean autoAuctionFlipOpen = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Easy Auction Buying",
            description = "By spam clicking you will auto buy/bid the item from that is currently viewed.",
            category = "§1§rBIN Flipper",
            subcategory = "BIN Flipper Settings"
    )
    public boolean autoAuctionFlipEasyBuy = false;

//   Auto Auction Filters

   @Property(
            type = PropertyType.CHECKBOX,
            name = "Average BIN Safety Guard",
            description = "Filters out auctions that are going over there average bin value",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutManip = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Filter Out Pets",
            description = "Filters out pets from Auto Flipper",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutPets = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Filter Out Skins",
            description = "Filters out minion skins, armor skins, and pet skins from Auto Flipper",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutSkins = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Filter Out Furniture",
            description = "Filters out furniture from Auto Flipper",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutFurniture = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Filter Out Dyes",
            description = "Filters out dyes from Auto Flipper",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutDyes = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Filter Out Runes",
            description = "Filters out runes from Auto Flipper",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public boolean autoAuctionFilterOutRunes = false;

    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Blacklist",
            description = "Filters out any blacklisted items. Seperate with §a;§r.§aExample: 'bonemerang;stick'",
            category = "§1§rBIN Flipper",
            subcategory = "§1§rBIN Flipper Filter"
    )
    public String autoAuctionBlacklist = "bonemerang;soldier;jungle pick;";

    @Property(
            type = PropertyType.SWITCH,
            name = "Favorite Pets",
            description = "Highlights Favorite Pets",
            category = "General",
            subcategory = "Pets"
    )
    public boolean FavoritePets = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Granda Wolf Pet Combo Timer",
            description = "Shows time until your combo expires on the Grandma Wolf Pet",
            category = "General",
            subcategory = "Pets"
    )
    public boolean GrandmaWolfTimer = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Fishing Hooks",
            description = "Hides fishing hooks from other players",
            category = "Render",
            subcategory = "Hide Things"
    )
    public boolean hideFishingHooks = false;

     @Property(
            type = PropertyType.SWITCH,
            name = "Ad Blocker",
            description = "Hides auction/lowballing advertisments in chat",
            category = "General",
            subcategory = "Other"
    )
    public boolean hideAdvertisments = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Small Items",
            description = "Makes the items you hold smaller",
            category = "General",
            subcategory = "Other"
    )
    public boolean smallItems = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Auto Party Chat",
            description = "Auto sends §a/chat p§r after joining a party §cWarning Use At Own Risk",
            category = "General",
            subcategory = "Other"
    )
    public boolean autoPartyChat = false;

    @Property(
            type = PropertyType.SLIDER,
            name = "X",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int armX = 0;

     @Property(
            type = PropertyType.SLIDER,
            name = "Y",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int armY = 0;

     @Property(
            type = PropertyType.SLIDER,
            name = "Z",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int armZ = 0;
    
    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf5Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf5Second = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf10Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf10Second = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf15Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf15Second = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf20Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf20Second = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf25Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf25Second = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "gMaWolf30Second",
            description = "",
            category = "General",
            subcategory = "Item Fov",
            hidden = true
    )
    public int gMaWolf30Second = 0;
    public static File file = new File("./config/skyblockfeatures/config.toml");
    public Config() {
        super(file);
        initialize();
    }
}
