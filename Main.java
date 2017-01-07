import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;



class Main {
	
	static PogChampBot bot;

	public static void main(String[] args) throws NickAlreadyInUseException, IOException, IrcException {

		bot = new PogChampBot();
		
		bot.setVerbose(false);
		bot.connect("irc.twitch.tv",6667,bot.authKey);
		bot.joinChannel("#gamesdonequick");
		
		while (true) {
			
			bot.checkActivity();
			
		}
		
		
	}
}