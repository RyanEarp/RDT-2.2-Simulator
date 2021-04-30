//Code written by Ryan Earp for CIS 430 - Project 1
package project1;

import java.util.Scanner;

public class Packet {

	Integer flag = 0;
	String flagBinary = "";
	
	Integer sequenceNumber = -1;
	String sequenceNumberBinary = "";
	
	Integer length = 0;
	String lengthBinary = "";
	
	String message = "";
	String messageBinaryValue = "00000000";
	
	String checksum = "00000000";

// Set message that the sender wishes to have in his message. If the message is over 20 characters, the Sender is asked again.
// If input is under 20, then convert it into binary data.
	String setMessage()
	{
		
		int mode = 1;
		
		while(mode == 1)
		{
			System.out.println("What message would you like to send? (20 ASCII Character Maximum)");
			Scanner messageInput = new Scanner(System.in);
			message = messageInput.nextLine();
		
			if(message.length() > 20)
			{
				System.out.println("Error: Message is too long. Please try again.");
			}
			else
			{
				mode = 0;
			}
		}
		
		byte [] messageBinaryArray = message.getBytes();
		
		StringBuilder binaryMessage = new StringBuilder();
		  for (byte a : messageBinaryArray)
		  {
		     int value = a;
		     for (int x = 0; x < 8; x++)
		     {
		        binaryMessage.append((value & 128) == 0 ? 0 : 1);
		        value <<= 1;
		     }
		     binaryMessage.append(' ');
		  } 
		
		  messageBinaryValue = binaryMessage.toString();
		  return messageBinaryValue;
	}

// Set the flag numeric value depending on the role of the caller, then convert it into Binary.
	String setFlag(String role)
	{
		if(role == "Sender")
		{
			flag = 2;
			flagBinary = Integer.toBinaryString(flag);
			flagBinary = stretchBinary(flagBinary);
			
			return flagBinary;
		}
		
		else
		{
			flag = 1;
			flagBinary = Integer.toBinaryString(flag);
			flagBinary = stretchBinary(flagBinary);
			
			return flagBinary;
		}
		
	}
	
// Increment Sequence Number, convert the new value into Binary, and stretch the binary to 8 bits in length.
	String updateSequenceNumber(int seq)
	{
		sequenceNumber = seq;
		sequenceNumberBinary = Integer.toBinaryString(sequenceNumber);
		sequenceNumberBinary = stretchBinary(sequenceNumberBinary);
		return sequenceNumberBinary;
	}

// Depending on the role of the caller, set the numeric value of the length of the message, then convert it into binary and stretch it to 8 bits.
	String setLength(String role)
	{
		if(role == "Sender")
		{
			length = 4 + message.length();
			lengthBinary = Integer.toBinaryString(length);
			lengthBinary = stretchBinary(lengthBinary);
			return lengthBinary;
		}
		else
		{
			length = 0;
			lengthBinary = "00000000";
			return lengthBinary;
		}
	}
	
// Take in any binary value and add 0's until it has 8 numbers in it.
	String stretchBinary(String binary)
	{
		while(binary.length() < 8)
		{
			String temp = "0";
			binary = temp + binary;
		}
		return binary;
	}
	
	String getFlag()
	{
		return flagBinary;
	}
	
	String getSequenceNumber()
	{
		return sequenceNumberBinary;
	}
	
	String getLength()
	{
		return lengthBinary;
	}
	
	String getData()
	{
		return messageBinaryValue;
	}
	
}
