import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.jibble.pircbot.PircBot;


class PogChampBot extends PircBot {
	
	String username;
	String authKey;
	
	long lastMessageTime;
	long timeSincePogChamp;
	long timeAtPogChamp;
	int PogChampCount;
	int myPogChamps;
	int counter;
	int timeout = 1000 * 60 * 60; //1 hour
	long startTime = 1483891200000L;
	ArrayList<String> lastTenMessages;
	
	Random rand;
	
	PogChampBot() throws IOException {
		this.setAuthentication();
		this.loadPogChampsFromFile();
		
		lastTenMessages = new ArrayList<String>();
		lastMessageTime = System.currentTimeMillis();
		
		rand = new Random();
	}
	
	
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		
		lastMessageTime = System.currentTimeMillis();
		
		
		if (channel.equals("#krohnos")) {
			if (message.equals("!status")) {
				sendMessage(channel, "Hello! Current PogChamp count is [" + PogChampCount + "]. My count is [" + myPogChamps + "]");
			}
			else if (message.equals("!save")) {
				if (writePogChampsToFile()) {
					sendMessage(channel, "PogChamps saved! Current count is [" + PogChampCount + "]. My count is [" + myPogChamps + "]");
				}
				else {
					sendMessage(channel, "PogChamps NOT saved WutFace Current count is [" + PogChampCount + "]. My count is [" + myPogChamps + "]");
				}
			}
		}
		
		
		//if AGDQ 2017 hasn't started
		if (System.currentTimeMillis() < startTime) {
				return;
		}
		
		if (!sender.equals(this.getName())) {
			lastTenMessages.add(message);
			if (lastTenMessages.size() > 10) {
				lastTenMessages.remove(0);
			}
		}
		
		
		if (timeAtPogChamp + 8000 < lastMessageTime && shouldWePogChamp()) {
			timeAtPogChamp = System.currentTimeMillis();
			//System.out.println("\"PogChamp " + new String(new char[rand.nextInt(3)]).replace("\0", " ") + "\"");
			//sendMessage(channel, "PogChamp ");
			sendMessage(channel, "PogChamp " + new String(new char[rand.nextInt(3)]).replace("\0", " "));
			PogChampCount++;
			myPogChamps++;
			System.out.println("Time to PogChamp! [Time: " + timeAtPogChamp + "] [My Count: " + myPogChamps + "]");
		}
		
		if (message.contains("PogChamp")) {
			PogChampCount += (message.split("PogChamp", -1).length-1);	
			if (PogChampCount % 100 == 1) {
				System.out.println("Current count is [" + PogChampCount + "]. My count is [" + myPogChamps + "]");
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
		
		
		return (counter > 0);
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
		BufferedWriter fw;
		try {
			fw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			fw.write(Integer.toString(PogChampCount) + "\n");
			fw.newLine();
			fw.write(Integer.toString(myPogChamps));
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
		reader.readLine();
		myPogChamps = Integer.parseInt(reader.readLine());
		reader.close();
		
		System.out.println("PogChamps loaded! Current count is [" + PogChampCount + "]. My count is [" + myPogChamps + "]");
		
	}
	
	protected void onDisconnect() {
		
		writePogChampsToFile();
		
	}

}