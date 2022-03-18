import java.util.Random;

public class Test_Memory {

    static void runTests(){
        try {
            testMemory();
            System.out.println("Memory passed all test!");
        }
        catch (Exception exception){
            System.out.println(exception.toString());
        }

    }

    private static void testMemory() throws Exception{
        Random rand = new Random();
        Memory memory = new Memory();
        /* Create random address values between 0-1020 and random 32 Bit values to store. Write them to that spot in memory.
           Next read the intVal at the memory address and compare with initial integer intVal to make sure it works. */
        for(int i = 0; i < 10000; i++){
            // Our computer has 1024 bytes of memory. We need at least 4 bytes to be able to store a 32 bit intVal, so we limit the address range to between 0-1020.
            int testAddress = rand.nextInt(1021);
            int testValue = rand.nextInt();
            Longword address = new Longword(testAddress);
            Longword value = new Longword(testValue);
            memory.write(address,value);
            if(memory.read(address).getSigned() != testValue) throw new Exception("Memory test failed to read: " + testValue + "From: " + testAddress);
        }
    }
}
