//Code written by Ryan Earp for CIS 430 - Project 1
package project1;

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class Receiver {

	Packet emptyPacket = new Packet();
	Vector<Packet> packetBuffer = new Vector<Packet>();
	int mode = 0;
	
	void receiverOperations(Packet receivedData, boolean setting, boolean retransmission)
	{
		if((packetBuffer.isEmpty()) == false)
			packetBuffer.clear();
		
		System.out.println("\n\n------------------------RECEIVER MODE------------------------");
		
// First, the Receiver will verify the checksum of the received packet. If it does not match, send back a NAK packet (flag = 11111111).
		
		if(verifyChecksum(receivedData) == false)
		{
			sendNAK();
		}
		else
		{

// Second, if the received and calculated checksums match, the Receiver prints the contents of the received packet.		
			System.out.println("\nReceived Packet:");		
					
			System.out.println("\nFlag:");
			System.out.println(receivedData.getFlag());
					
			System.out.println("\nSequence Number:");
			System.out.println(receivedData.getSequenceNumber());
					
			System.out.println("\nLength:");
			System.out.println(receivedData.getLength());
					
			System.out.println("\nData:");
			System.out.println(receivedData.getData());
		
			System.out.println("\nChecksum:");
			System.out.println(receivedData.checksum);
		
// Obtain old sequence number and convert it to integer.
		
			int oldSeqNumber = Integer.parseInt(receivedData.getSequenceNumber(), 2);

// Increment sequence number, then apply converted sequence number to new packet.
			oldSeqNumber++;
			emptyPacket.updateSequenceNumber(oldSeqNumber);
		
// Select and take in ACK Mode selection, but only if it is the first iteration through this code--------------------
			if(setting == true)
			{
				while(mode <= 0 || mode > 2)
				{
					System.out.println("\nWhich mode would you like to use: \n1.) Normal Transmission \n2.) Random ACK Error Bit\nNOTE: If the receiver is currently retransmitting an ACK packet (client is set to Error Mode), this change will take effect on the next iteration of the conversation");
				
					Scanner modeSelection = new Scanner(System.in);
					mode = modeSelection.nextInt();

// If mode is not an acceptable value, allow the user to input another value-----------------------------------------		
					if(mode < 1 || mode > 2)
						System.out.println("\nPlease select a proper value (1 or 2)");
				}
			}

// If the Normal Transmission Mode is selected, the Receiver will send out a perfect ACK packet with no data and a length of 0. 
				if(mode == 1)
				{
					System.out.println("\nCreating New ACK Packet...");
				
					createPacket();
				}
// If the Error Mode is selected, the Receiver will create a new packet and flip a random bit in it.
				if(mode == 2)
				{
					createPacket();
					
					if(retransmission == false)
					{	
					
					System.out.println("\nCreating New ACK Packet (WITH ERROR!!!)... \n");
					
					flipBitError();
					}
				}
			}
		}

// Function for packet creation
	void createPacket()
	{
		
		// Set Flag to mark it as an ACK
		emptyPacket.setFlag("Receiver");
		
		// Set new Sequence Number
		emptyPacket.getSequenceNumber();
		
		// Set Length
		emptyPacket.setLength("Receiver");
		
		// Set Data (The Data field will be empty)
		emptyPacket.messageBinaryValue = "00000000";
		
		calculateChecksum(emptyPacket);
		
		System.out.println("\nSuccess! ACK Packet is ready for delivery by Server");

// Create backup Packet to store contents of current ACK, in case a retransmission is required.
		Packet storage = new Packet();
		
		storage.flagBinary = emptyPacket.flagBinary;
		storage.sequenceNumberBinary = emptyPacket.sequenceNumberBinary;
		storage.lengthBinary = emptyPacket.lengthBinary;
		storage.messageBinaryValue = emptyPacket.messageBinaryValue;
		storage.checksum = emptyPacket.checksum;
		
		packetBuffer.insertElementAt(storage, 0);
	}
	
// Function for sending a NAK packet	
	void sendNAK()
	{
		emptyPacket.flagBinary = "11111111";
		
		// Set new Sequence Number
		emptyPacket.getSequenceNumber();
		
		// Set Length
		emptyPacket.setLength("Receiver");
		
		// Set Data (The Data field will be empty)
		emptyPacket.messageBinaryValue = "00000000";
		
		System.out.println("\nSuccess! NAK Packet is ready for delivery by Server");
	}
	
	Packet getACKPacket()
	{
		return emptyPacket;
	}
	
	Packet flipBitError()
	{
// Retrieve the newly created packet and set it to be the victim of a bit flip.
		Packet victimPacket = getACKPacket();
		
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
			char[] checksumArray = {'0','0','0','0','0','0','0','0'};
			
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
			
			System.out.println("\nNow comparing: \nReceived Checksum: " + input.checksum + "\nCalculated Checksum: " + checksumVerify);
			
			if(checksumVerify.equals(input.checksum) == false)
			{
				System.out.println("\nChecksums do NOT match!!! Sending NAK packet to sender");
				return false;
			}
			else
			{
				System.out.println("\nChecksums match! Sending ACK packet to sender");
				return true;
			}
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

// Cycle through all Header bytes. Headerfield 0 = Flag. HeaderField 1 = Sequence Number. HeaderField 2 = Length. HeaderField 3 = Data.
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
							checksumArray[bit] = '0';
						else
						{
							carryOvers.pop();
							checksumArray[bit] = '1';
						}
					}
					
					if(((input.flagBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || (input.flagBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1'))
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
					
					if(((input.sequenceNumberBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || (input.sequenceNumberBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1'))
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
					
					if(((input.lengthBinary.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || (input.lengthBinary.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1'))
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
					
					if(((input.messageBinaryValue.charAt(bit) == '1') && (input.checksum.charAt(bit) == '0')) || (input.messageBinaryValue.charAt(bit) == '0') && (input.checksum.charAt(bit) == '1'))
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
	
	Packet retransmitPacket()
	{
		Packet retransmission = packetBuffer.get(0);
		
		packetBuffer.remove(emptyPacket);
		
		System.out.println("\nRetransmitting error-free ACK packet...");
	
		return retransmission;
	}
	
	}
