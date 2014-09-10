This is Oskari Leppäaho's master's thesis project. The object of 
the project is to develop a StarCraft BroodWar bot for small scale 
combat (SSC) scenarios using potential fields to control the bot 
and genetic programming to evolve the potential field functions.

Installation
----------------------------------

1. If you don't own it, buy the StarCraft BroodWar game 
(http://us.battle.net/en/games/classic).
2. Install StarCraft BroodWar and patch it to the latest version 
(1.61.1) 
(https://us.battle.net/support/en/article/classic-game-patches).
3. Install BWAPI 3.7.4 by running the install.exe in BWAPI folder.
4. Replace the bwapi.ini file in bwapi-data folder in StarCraft 
installation folder with the bwapi.ini in the package.
5. Copy the EvolutionMap.scm to Maps/custom folder in StarCraft 
installation folder.
6. Import the ThesisAIProject to Eclipse. (For running the bot you 
might need to start Eclipse as an admiminstrator.
7. Ensure that dependencies are set correctly
	-bwmirror_v1_0.jar in lib folder
	-ecj source provided in the root of the main zip file
8. Ensure that the project uses 32 bit jre.

You are now ready to run the project.


Testing BroodWar connection
----------------------------------
You can run the bot without evolution to test that it connects to 
BroodWar as expected.

1. Run thesis.bot.Controller in eclipse. (You might have to start 
Eclipse as an adiminstrator.)
2. Run Chaoslauncher.exe in BWAPI 3.7.4/Chaoslauncher folder (You 
might have to run it as an administrator).
3. Make sure that BWAPI (1.16.1) RELEASE plugin is checked. (You 
might want to also check W-MODE 1.02 to run StarCraft in a windowed 
mode.)
4. In the drop-down menu at bottom of the window select Starcraft 
1.16.1
5. Press Start.
6. BWAPI should automatically navigate the menu and start a game on 
the EvolutionMap.scm map (as defined in the bwapi.ini file).


Running the evolution
----------------------------------
If everything went fine so far, you are finally ready to run the 
actual evolution.

1. The bot AI client uses Java RMI to connect to the evolution. 
First you have to run the rmiregistry.exe from your jdk bin folder. 
Keep it running until you are finished with the bot.
2. Run the Chaoslauncher - MultiInstance.exe in BWAPI 
3.7.4/Chaoslauncher folder. (You might have to run it as an 
administrator).
3. Determine how many instances of StarCraft you want to run 
concurrently by modifying the evalthreads parameter in 
src/thesis/evolution/thesis.params to your liking. Default is 8.
4. Run ec.Evolve with the command line argument "-file 
src/thesis/evolution/thesis.params".
5. Run the thesis.rmi.RemoteBotStarter with command line argument 
that is the index of the current concurrent StarCraft instance 
starting from "0".
6. Return to the Chaoslauncher window.
7. Make sure that BWAPI (1.16.1) RELEASE plugin is checked. (You 
might want to also check W-MODE 1.02 to run StarCraft in a windowed 
mode.)
8. In the dropdown menu at bottom of the window select Starcraft 
Multi-Instance.
9. Press Start.
10. Go to ec.Evolve command line and press Enter.
11. Repeat from step 5 until you have as many StarCraft instances 
running as you determined in step 3. 
12. The evolution is now running.

The results will appear in out.stat file in ThesisAIProject folder.

There are also some other evolution parameters that can be changed 
in the thesis.params file.
 