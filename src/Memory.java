import java.util.Arrays;

public class Memory {
    private final int SIZE_IN_BITS = 1024 * 8;
    private Bit[] memory = new Bit[SIZE_IN_BITS]; //1024 Bytes 8 bits per byte.

    Memory(){
        for(int i = 0; i < SIZE_IN_BITS; i++){
            memory[i] = new Bit(0);
        }
    }

    /* NOTE: I wasn't sure how to handle attempting to read/write to a high memory address where you would not have 32 Bits to use.
      For example trying to write to address at byte 1022. You wouldn't have enough space to write the whole Longword. I could write only
      part of the word. I could have the memory act like a wrapped buffer. For right now I'm just throwing an exception if you try to read/write
      without 32 Bits of space left. */


    public Longword read(Longword address){
        int memoryAddress = address.getSigned() * 8; //Multiply the memory address intVal by 8 to get the Bit location.
        if(SIZE_IN_BITS - memoryAddress < 32) throw new IllegalArgumentException("Need at least 8 bytes or space to write the Word!");
        Bit[] data = Arrays.copyOfRange(memory,memoryAddress,memoryAddress + Longword.WORD_SIZE);
        return new Longword(data);
    }

    public void write(Longword address, Longword value){
        int memoryAddress = address.getSigned() * 8;
        if(SIZE_IN_BITS - memoryAddress < 32) throw new IllegalArgumentException("Need at least 8 bytes or space to write the Word!");
        for(int i = 0; i < Longword.WORD_SIZE ; i++){
            memory[memoryAddress + i] = value.getBit(i);
        }
    }

    public Bit[] getMemory() {
        return memory;
    }

}
