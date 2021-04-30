//Code written by Ryan Earp for CIS 430 - Project 1
package project1;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.util.Random;

public class Sender {

// Create new empty packet for utilization in sending.
	Packet newPacket = new Packet();
	int seqNumber = 0;
	
// Vector to store packets before transmission.
	Vector<Packet> packetBuffer = new Vector<Packet>();
	
	int mode = 0;
	
	 void senderOperations(boolean setting)
	{
		System.out.println("\n------------------------SENDER PROGRAM------------------------\n");
		
// Select and take in Delivery Mode, but only if its the first iteration of this code--------------------------------------------	
		if(setting == true)
		{
			while(mode <= 0 || mode > 2)
			{
				System.out.println("Which mode would you like to use: \n 1.) Normal Transmission \n 2.) Random Error Bit");
		
				Scanner modeSelection = new Scanner(System.in);
				mode = modeSelection.nextInt();

// If mode is not an acceptable value, allow the user to input another value-----------------------------------------		
				if(mode < 1 || mode > 2)
					System.out.println("Please select a proper value (1 or 2)");
			}
		}

// If normal transmission is selected, create a new data packet.
		if(mode == 1)
		{
			System.out.println("\nCreating New Packet... \n ");
			
			createPacket();
		}
		
// If Error Mode is selected, create a new packet and flip a random bit in it.
		if(mode == 2)
		{
			System.out.println("\nCreating New Packet (WITH ERROR!!!)... \n");
			
			createPacket();
			
			flipBitError();
		}
	}

// Function for receiving ACK packets from the original packet's recipient
	int receiveACK(Packet ackPacket)
	{
		System.out.println("\n\n------------------------SENDER PROGRAM------------------------\n");
		
// If ACK is not a NAK, compare checksums, print out the ACK packet contents, and remove the packet from the sender's buffer.
		if(ackPacket.flagBinary.equals("11111111") == false)
		{
			System.out.println("\nServer successfully received an ACK packet from the receiver: ");
			
			seqNumber = Integer.parseInt(ackPacket.getSequenceNumber(), 2);
			seqNumber++;
		
			System.out.println("\nReceived ACK packet:");
		
			System.out.println("\nFlag:");
			System.out.println(ackPacket.getFlag());
		
			System.out.println("\nSequence Number:");
			System.out.println(ackPacket.getSequenceNumber());
		
			System.out.println("\nLength:");
			System.out.println(ackPacket.getLength());
		
			System.out.println("\nData:");
			System.out.println(ackPacket.getData());
			
// If the checksums match, print out the message of the packet in the buffer, remove it from the buffer, and tell the server that no retransmission is required.		
			if(verifyChecksum(ackPacket) == true)
				{
					System.out.println("\nMessage of Buffered Packet: " + newPacket.message);
					packetBuffer.remove(newPacket);
					return 0;
				}
			else
			{
// If the checksums do not match, tell the server that a retransmission is required.				
				return 1;
			}
			
		}
// If a NAK packet is received, tell the server that a retransmission is required.
			System.out.println("\nNAK Packet received. Beginning retransmission...");
			return 1;
	}

// Create a packet by setting the message and header values individually
	void createPacket()
	{ 
		newPacket.setMessage();
		
		newPacket.setFlag("Sender");
		
		newPacket.updateSequenceNumber(seqNumber);
		
		newPacket.setLength("Sender");
		
		calculateChecksum(newPacket);
		
// Add the packet to a storage buffer for retransmission
		Packet storage = new Packet();
		
		storage.flagBinary = newPacket.flagBinary;
		storage.sequenceNumberBinary = newPacket.sequenceNumberBinary;
		storage.lengthBinary = newPacket.lengthBinary;
		storage.messageBinaryValue = newPacket.messageBinaryValue;
		storage.checksum = newPacket.checksum;
		
		packetBuffer.insertElementAt(storage, 0);
		
		System.out.println("\nSuccess!Packet is ready for delivery by Server");
	}
// If the client receives a scrambled ACK or NAK packet back, an unscrambled packet will be pulled from the packet buffer and retransmitted.	
	Packet retransmitPacket()
	{
		Packet retransmission = packetBuffer.get(0);
		
		packetBuffer.remove(newPacket);
		
		System.out.println("\nRetransmitting error-free packet...");
	
		return retransmission;
	}

// Allows the main function, acting as the Server, to retrieve the sender's created packet.
	Packet getPacket()
	{
		return newPacket;
	}
	
	Packet flipBitError()
	{
		
// Retrieve the newly created packet and set it to be the victim of a bit flip.
		Packet victimPacket = getPacket();
		
// Generate a random number and store it for use in selecting a random header section to flip a bit in
		Random headerValueSelector = new Random();
		int randomHeaderSection = headerValueSelector.nextInt(4);

// Generate a random number and store it for use in selecting a random bit position to flip
		Random randomBitSelector = new Random();
		int randomBitPosition = randomBitSelector.nextInt(8);

// Depending on the randomly chosen header field and bit position, a random bit in the header will flip.
		switch(randomHeaderSection) {

// If 0 is randomly selected, flip a bit in the FLAG field
		case 0:
			System.out.println("\nFlipping bit in FLAG");
			char[] flagBinaryArray = victimPacket.flagBinary.toCharArray();
			if(flagBinaryArray[randomBitPosition] == '0')
				flagBinaryArray[randomBitPosition] = '1';
			
			else 
				flagBinaryArray[randomBitPosition] = '0';
			
			System.out.println("\nOld FLAG Byte Value: ");
			System.out.print(victimPacket.flagBinary);
			
			victimPacket.flagBinary = String.valueOf(flagBinaryArray);
			System.out.println("\nNew FLAG Byte Value: ");
			System.out.print(victimPacket.flagBinary);
			
			break;
// If 1 is randomly selected, flip a bit in the SEQUENCE NUMBER field	
		case 1:
			System.out.println("\nFlipping bit in SEQUENCE NUMBER");
			char[] seqBinaryArray = victimPacket.sequenceNumberBinary.toCharArray();
			
			if(seqBinaryArray[randomBitPosition] == '0')
				seqBinaryArray[randomBitPosition] = '1';
			
			else
				seqBinaryArray[randomBitPosition] = '0';
			
			System.out.println("\nOld SEQUENCE NUMBER Byte Value: ");
			System.out.print(victimPacket.sequenceNumberBinary);
			
			victimPacket.sequenceNumberBinary = String.valueOf(seqBinaryArray);
			System.out.println("\nNew SEQUENCE NUMBER Byte Value: ");
			System.out.print(victimPacket.sequenceNumberBinary);
			
			break;
// If 2 is randomly selected, flip a bit in the LENGTH field			
		case 2:
			System.out.println("\nFlipping bit in LENGTH");
			char[] lengthBinaryArray = victimPacket.lengthBinary.toCharArray();
			if(lengthBinaryArray[randomBitPosition] == '0')
				lengthBinaryArray[randomBitPosition] = '1';
			
			else
				lengthBinaryArray[randomBitPosition] = '0';
			
			System.out.print("\nOld LENGTH Byte Value: ");
			System.out.print(victimPacket.lengthBinary);
			
			victimPacket.lengthBinary = String.valueOf(lengthBinaryArray);
			System.out.println("\nNew LENGTH Byte Value: ");
			System.out.print(victimPacket.lengthBinary);
			
			break;
// If 3 is randomly selected, flip a bit in the DATA field		
		case 3:
			System.out.println("\nFlipping bit in DATA");
			char[] dataBinaryArray = victimPacket.messageBinaryValue.toCharArray();
			
			if(dataBinaryArray[randomBitPosition] == '0')
				dataBinaryArray[randomBitPosition] = '1';
			
			else
				dataBinaryArray[randomBitPosition] = '0';
			
			System.out.println("\nOld DATA Byte Value: ");
			System.out.print(victimPacket.messageBinaryValue);
			
			victimPacket.messageBinaryValue = String.valueOf(dataBinaryArray);
			System.out.println("\nNew DATA Byte Value: ");
			System.out.print(victimPacket.messageBinaryValue);
			break;
		}

// Return the modified packet
		return victimPacket;
	}
	
	void calculateChecksum(Packet input)
	{
// Create new buffer to hold carry over 1's.
		Stack<String> carryOvers = new Stack<String>();
		
		input.flagBinary = input.getFlag();
		input.sequenceNumberBinary = input.getSequenceNumber();
		input.lengthBinary = input.getLength();
		input.messageBinaryValue = input.getData();
		
		input.checksum = "00000000";
		
// Convert String to Char array for easier access to individual bits
		char[] checksumArray = {'0', '0','0','0','0','0','0','0'};

// Cycle through all Header bytes. HeaderField 0 = Flag. HeaderField 1 = Sequence Number. HeaderField 2 = Length. Headerfield 3 = Data 
		for(int headerField = 0; headerField <= 3; headerField++)
		{
// Cycle through all bit positions of that byte
			for(int bit = 7; bit >= 0; bit--)
			{
				switch(headerField) {
// Case 0 = Checksum + Flag				
				case 0:
					if ((input.flagBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
					
					if(((input.flagBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || ((input.flagBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1')))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '1';
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
					
					if((input.flagBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					input.checksum = String.valueOf(checksumArray);
						
					break;
				
// Case 1 = Checksum + Sequence Number				
				case 1:
					if ((input.sequenceNumberBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
					
					if(((input.sequenceNumberBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || ((input.sequenceNumberBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1')))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
					
					if((input.sequenceNumberBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					input.checksum = String.valueOf(checksumArray);
						
					break;
// Case 2 = checksum + Length				
				case 2:
					if ((input.lengthBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
					
					if(((input.lengthBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || ((input.lengthBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1')))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
					
					if((input.lengthBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
				
					input.checksum = String.valueOf(checksumArray);
					
					break;
					
// Case 3 = checksum + Data				
				case 3:
					if ((input.messageBinaryValue.charAt(bit) == '0') && (input.checksum.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
					
					if(((input.messageBinaryValue.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || ((input.messageBinaryValue.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1')))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
					
					if((input.messageBinaryValue.charAt(bit) == '1') && (input.checksum.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
				
					input.checksum = String.valueOf(checksumArray);
					
					break;
					
				}

			}
		}
		input.checksum = String.valueOf(checksumArray);
	}
	
	boolean verifyChecksum(Packet input)
	{
// Create new buffer to hold carry over 1's.
		Stack<String> carryOvers = new Stack<String>();
			
		input.flagBinary = input.getFlag();
		input.sequenceNumberBinary = input.getSequenceNumber();
		input.lengthBinary = input.getLength();
		input.messageBinaryValue = input.getData();
		
		String checksumVerify = "00000000";
			
// Convert String to Char array for easier access to individual bits
			char[] checksumArray = {'0', '0','0','0','0','0','0','0'};
			
// Cycle through all Header bytes. HeaderField 0 = Flag. HeaderField 1 = Sequence Number. HeaderField 2 = Length. HeaderField 3 = Data.
			for(int headerField = 0; headerField <= 3; headerField++)
			{
// Cycle through all bit positions of that byte
			for(int bit = 7; bit >= 0; bit--)
			{
				switch(headerField) {
// Case 0 = Checksum + Flag				
				case 0:
					if ((input.flagBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
						
					if(((input.flagBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '0')) || (input.flagBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
								checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
						
					if((input.flagBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					checksumVerify = String.valueOf(checksumArray);
							
					break;
					
// Case 1 = Checksum + Sequence Number				
				case 1:
					if ((input.sequenceNumberBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
						
					if(((input.sequenceNumberBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '0')) || (input.sequenceNumberBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
						
					if((input.sequenceNumberBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					checksumVerify = String.valueOf(checksumArray);
							
					break;
// Case 2 = checksum + Length				
				case 2:
					if ((input.lengthBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
						
					if(((input.lengthBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '0')) || (input.lengthBinary.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
						
					if((input.lengthBinary.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					
					checksumVerify = String.valueOf(checksumArray);
						
					break;
						
// Case 3 = checksum + Data				
				case 3:
					if ((input.messageBinaryValue.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '0'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
						
					if(((input.messageBinaryValue.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '0')) || (input.messageBinaryValue.charAt(bit) == '0') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
							checksumArray[bit] = '1';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
					}
						
					if((input.messageBinaryValue.charAt(bit) == '1') && (checksumVerify.charAt(bit) == '1'))
					{
						if(carryOvers.isEmpty())
						{
							checksumArray[bit] = '0';
							carryOvers.push("1");
						}
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
							carryOvers.push("1");
						}
					}
					
					checksumVerify = String.valueOf(checksumArray);
						
					break;
						
					}
				}
			}
			checksumVerify = String.valueOf(checksumArray);
			
			System.out.println("\nNow comparing: \nReceived ACK Checksum: " + input.checksum + "\nCalculated ACK Checksum: " + checksumVerify);
			
			if(checksumVerify.equals(input.checksum) == false)
			{
				System.out.println("\nACK packet checksums do not match! Retransmitting...");
				return false;
			}
			else
			{
				System.out.println("ACK packet checksums MATCH! Accepting ACK");
				return true;
			}
		}
}
