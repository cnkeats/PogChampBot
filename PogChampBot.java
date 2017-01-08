import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jibble.pircbot.PircBot;


class PogChampBot extends PircBot {
	
	String username;
	String authKey;
	
	long lastMessageTime;
	long timeSincePogChamp;
	long timeAtPogChamp;
	int PogChampCount;
	int counter;
	int timeout = 1000 * 60 * 60; //1 hour
	long startTime = 1483891200000L;
	ArrayList<String> lastTenMessages;
	
	PogChampBot() throws IOException {
		this.setAuthentication();
		this.loadPogChampsFromFile();
		
		lastTenMessages = new ArrayList<String>();
		lastMessageTime = System.currentTimeMillis();
		PogChampCount = 0;
	}
	
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		
		lastMessageTime = System.currentTimeMillis();
		System.out.println(lastMessageTime);
		
		
		if (channel.equals("#krohnos")) {
			sendMessage(channel, "Hello! Current PogChamp count is [" + PogChampCount + "]");
			if (message.equals("!save")) {
				if (writePogChampsToFile()) {
					sendMessage(channel, "PogChamps saved! Current count is [" + PogChampCount + "]");
				}
				else {
					sendMessage(channel, "PogChamps NOT saved WutFace Current count is [" + PogChampCount + "]");
				}
			}
		}
		
		
		//if AGDQ 2017 hasn't started
		if (System.currentTimeMillis() < startTime) {
			System.out.println("AGDQ2017 hasn't started!");
				return;
		}
		
		if (!sender.equals(this.getName())) {
			lastTenMessages.add(message);
			if (lastTenMessages.size() > 10) {
				lastTenMessages.remove(0);
			}
		}
		
		
		if (timeAtPogChamp + 30000 < lastMessageTime && shouldWePogChamp()) {
			timeAtPogChamp = System.currentTimeMillis();
			sendMessage(channel, "PogChamp");
			PogChampCount++;
		}
		
		if (message.contains("PogChamp")) {
			PogChampCount += (message.split("PogChamp", -1).length-1);	
			if (PogChampCount % 10 == 1) {
				System.out.println("Number of messages containing PogChamp: " + PogChampCount);
				writePogChampsToFile();
			}
		}
		
	}
	
	boolean shouldWePogChamp() {
		
		counter = 0;
		for (String s : lastTenMessages) {
			
			if (s.contains("PogChamp")) {
				counter++;
			}
			
		}
		
		
		return (counter > 2);
	}
	
	public void checkActivity() {
		
		//if AGDQ 2017 hasn't started
		if (System.currentTimeMillis() < 1483873200) {
			return;
		}
		
		if (System.currentTimeMillis() - lastMessageTime > timeout) {
			System.out.println("\nExiting due to lack of activity.");
			System.exit(0);
		}
		
	}
	
	boolean setAuthentication() throws IOException {
		
		File file = new File("data/IRCAuth.txt");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("IRCAuth.txt not found.");
			return false;
		}
		
		username = reader.readLine();
		authKey = reader.readLine();
		reader.close();
		
		this.setName(username);
		
		return true;
		
	}
	
	boolean writePogChampsToFile() {
		File file = new File("data/PogChampCount.txt");
		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			fw.write(Integer.toString(PogChampCount));
			System.out.println("Wrote to file successfully!");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to write to \"data/PogChampCount.txt\"");
			return false;
		}
		return true;
	}
	
	void loadPogChampsFromFile() throws IOException {
		
		File file = new File("data/PogChampCount.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));

		PogChampCount = Integer.parseInt(reader.readLine());
		reader.close();
		
		System.out.println("PogChamps loaded! Current count is [" + PogChampCount + "]");
		
	}
	
	protected void onDisconnect() {
		
		writePogChampsToFile();
		
		
	}

}