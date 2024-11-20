/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralring.example.ring.minimal;

import com.coralblocks.coralring.ring.NonBlockingRingConsumer;
import com.coralblocks.coralring.ring.RingConsumer;

public class MinimalNonBlockingConsumer {
	
	private final static String FILENAME = MinimalBlockingProducer.FILENAME;
	
	public static void main(String[] args) {
		
		final int messagesToSend = 10;
		
		final RingConsumer<MutableLong> ringConsumer = new NonBlockingRingConsumer<MutableLong>(MutableLong.getMaxSize(), MutableLong.class, FILENAME); // default size is 1024
		
		boolean isRunning = true;
		
		while(isRunning) {
			
			long avail = ringConsumer.availableToPoll(); // read available batches as fast as possible
			
			if (avail == 0) continue; // busy spin
			
			if (avail == -1) throw new RuntimeException("The consumer fell behind! (ring wrapped)");
			
			for(long i = 0; i < avail; i++) {
				
				MutableLong ml = ringConsumer.poll();
				
				if (ml == null) throw new RuntimeException("The consumer tripped over the producer! (checksum failed)");
				
				System.out.print(ml.get());
				
				if (ml.get() == messagesToSend - 1) isRunning = false; // done receiving all messages
			}
			
			ringConsumer.donePolling(); // don't forget to notify producer
		}
		
		ringConsumer.close(true);
		
		System.out.println();
		
		// OUTPUT: 0123456789
	}
}
