//Code written by Ryan Earp for CIS 430 - Project 1
package project1;

import java.util.Scanner;

public class main {
	
	public static void main(String args[]) {

// This value tells us if the program is still running. If changed, the program will terminate.
		int mode = 1;
// This value tells us if the conversation is in its first iteration or not.
		boolean first = true;

// This value tells us if a packet from the sender is a retransmission.
		boolean retransmissionMode = false;
		
// Create new sender and receiver		
		Sender client = new Sender();
		Receiver receiver = new Receiver();
		
		while(mode == 1)
		{
		
// Start Client sender process
		client.senderOperations(first);

// Deliver packet from sender to receiver
		receiver.receiverOperations(client.getPacket(), first, retransmissionMode);
		
// Deliver packet from receiver to sender. If the sender receives a NAK packet, it will inform the server by providing it with a 1, asking it to retransmit.	
		
		if(client.receiveACK(receiver.getACKPacket()) == 1)
		{
			retransmissionMode = true;
			receiver.receiverOperations(client.retransmitPacket(), first, retransmissionMode);
			retransmissionMode = false;
			client.receiveACK(receiver.retransmitPacket());
		}

// Ask the user if they want to continue the conversation
		System.out.println("\nWould you like to continue this conversation?\n1. Yes\n2. No");
		Scanner modeSelect = new Scanner(System.in);
		mode = modeSelect.nextInt();
		first = false;
		}
		if(mode == 2)
			System.out.println("Thank You! Goodbye!");
	}
	
}
