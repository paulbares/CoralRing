package com.coralblocks.coralring.example.memory;

import com.coralblocks.coralring.memory.Memory;
import com.coralblocks.coralring.memory.SharedMemory;

public class SharedMemoryConsumer {
	
	public static void main(String[] args) {
		
		final String filename = "shared-pipe.mmap";
		
		Memory memory = new SharedMemory(filename); // size will be taken from file
		final long address = memory.getPointer();
		
		final int headerOffset = 4;
		int producerIndex = -1;
		int lastIndexRead = -1;
		
		OUTER: while(true) { // busy spin

			producerIndex = memory.getIntVolatile(address);
			
			if (producerIndex > lastIndexRead) {
			
				int availableToRead = producerIndex - lastIndexRead;
				
				for(int i = 0; i < availableToRead; i++) {
					
					long offset = headerOffset + (lastIndexRead + i + 1) * 4;
					
					int value = memory.getInt(address + offset);

					if (value == -1) break OUTER;

					if (value != 0) System.out.print(",");
					
					System.out.print(value);
				}
				
				lastIndexRead = producerIndex;
			}
		}
		
		memory.release(true); // don't delete the file, consumer may still be reading it
		
		System.out.println("\nConsumer DONE!");
	}
}