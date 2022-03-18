public class Test_Longword {

    //Example words to test methods on:
    private static final Longword TEST_ZERO = new Longword(0);
    private static final Longword TEST_INT_MAX = new Longword(Integer.MAX_VALUE);
    private static final Longword TEST_INT_MIN = new Longword(Integer.MIN_VALUE);
    private static final Longword TEST_INT_256 = new Longword(256);

    static void runTests(){
        try{
            testEquals(); //Test Equals first because it is used to test other methods.
            testGetBit();
            testSetBit();
            testAnd();
            testOr();
            testXor();
            testNot();
            testRightShift();
            testLeftShift();
            testToString();
            testGetUnsigned();
            testGetSigned();
            testCopy();
            testSet();
            testStringConstructor();
            System.out.println("Longword passed all tests!");
        }
        catch (Exception exception){
            System.out.println(exception.toString());
        }
    }

    private static void testEquals() throws Exception{
        Longword test =  new Longword();
        test.set(256);
        if(!TEST_INT_256.equals(test)) throw new Exception("Equals FAILED! for Longword 256!");
        test.set(Integer.MAX_VALUE);
        if(!test.equals(TEST_INT_MAX)) throw new Exception("Equals FAILED! for Longword Integer Max!");
    }


    private static void  testGetBit() throws Exception{
        if(TEST_ZERO.getBit(0).getValue() != 0) throw new Exception( "GetBit FAILED on test word 1! EXPECTED: 0 ACTUAL: " + TEST_ZERO.getBit(0).getValue());
        if(TEST_ZERO.getBit(16).getValue() != 0) throw new Exception( "GetBit FAILED on test word 1! EXPECTED: 0 ACTUAL: " + TEST_ZERO.getBit(16).getValue());

        if(TEST_INT_MAX.getBit(0).getValue() != 0) throw new Exception( "GetBit FAILED on test word 2! EXPECTED: 0 ACTUAL: " + TEST_INT_MAX.getBit(0).getValue());
        if(TEST_INT_MAX.getBit(24).getValue() != 1) throw new Exception( "GetBit FAILED on test word 2! EXPECTED: 1 ACTUAL: " + TEST_INT_MAX.getBit(24).getValue());

        if(TEST_INT_MIN.getBit(0).getValue() != 1) throw new Exception( "GetBit FAILED on test word 3! EXPECTED: 1 ACTUAL: " + TEST_INT_MIN.getBit(0).getValue());
        if(TEST_INT_MIN.getBit(25).getValue() != 0) throw new Exception( "GetBit FAILED on test word 3! EXPECTED: 0 ACTUAL: " + TEST_INT_MIN.getBit(25).getValue());

        if(TEST_INT_256.getBit(31).getValue() != 0) throw new Exception( "GetBit FAILED on test word 4! EXPECTED: 1 ACTUAL: " + TEST_INT_256.getBit(31).getValue());
        if(TEST_INT_256.getBit(23).getValue() != 1) throw new Exception( "GetBit FAILED on test word 4! EXPECTED: 1 ACTUAL: " + TEST_INT_256.getBit(23).getValue());
        if(TEST_INT_256.getBit(0).getValue() != 0) throw new Exception( "GetBit FAILED on test word 4! EXPECTED: 1 ACTUAL: " + TEST_INT_256.getBit(0).getValue());

    }
    private static void testSetBit() throws Exception{
        TEST_ZERO.setBit(0,new Bit(1));
        if(TEST_ZERO.getBit(0).getValue() != 1) throw new Exception("SetBit to 1 FAILED");
        TEST_ZERO.setBit(0,new Bit(0));
        if(TEST_ZERO.getBit(0).getValue() != 0) throw new Exception("SetBit to 0 FAILED");

    }

    private static void testAnd() throws Exception{
        //All bits in TEST_ZERO are 0, so and-ing it with anything will result in a word of all 0 bits.
        if(!(TEST_ZERO.and(TEST_INT_MAX).equals(new Longword(0))))throw new Exception("AND with 0 word FAILED!");
        //All bits in TEST_INT_MAX except for sign bit are 1, so and-ing with a positive word should result in the same word.
        if(!(TEST_INT_MAX.and(TEST_INT_256).equals(TEST_INT_256))) throw new Exception("AND Integer Max with 256 FAILED!");
        //And-ing any word with itself should result in itself.
        if(!(TEST_ZERO.and(TEST_ZERO).equals(TEST_ZERO))) throw new Exception("AND with same Longword FAILED!");
    }
    private static void testOr() throws Exception{
        //Or-ing with 0 should simply return the original word.
        if(!(TEST_INT_MAX.or(TEST_ZERO).equals(TEST_INT_MAX)))throw new Exception("OR with 0 word FAILED!");
        //Or-ing these two should result in the sign bit being set and the 9th bit being set, which is a intVal of -2147483392.
        if(!(TEST_INT_MIN.or(TEST_INT_256).equals(new Longword(-2147483392)))) throw new Exception("OR INTEGER_MIN with 256 FAILED!");
        //Or-ing these two words should result in all bits being set which in 2's compliment representation would be a intVal of -1.
        if(!(TEST_INT_MAX.or(TEST_INT_MIN).equals(new Longword(-1)))) throw new Exception("OR Integer Max with Integer Min FAILED!");
    }
    private static void testXor() throws Exception{
        //Xor-ing with 0 should simply return the original word.
        if(!(TEST_INT_MAX.xor(TEST_ZERO).equals(TEST_INT_MAX)))throw new Exception("XOR with 0 word FAILED!");
        //Xor-ing Integer Max with 256 should result in every bit except sign bit and the 9th bit set, which is a intVal of 2147483391.
        if(!(TEST_INT_MAX.xor(TEST_INT_256).equals(new Longword(2147483391)))) throw new Exception("XOR INTEGER_MAX with 256 FAILED!");

    }
    private static void testNot()throws Exception{
        //Not-ing the 0 word should result in every bit set which has a intVal of -1.
        if(!(TEST_ZERO.not().equals(new Longword(-1))))throw new Exception("NOT 0 word FAILED!");
        //Not-ing Integer Max should give you Integer Min.
        if(!(TEST_INT_MAX.not().equals(TEST_INT_MIN)))throw new Exception("NOT Integer Max FAILED!");
        //Not-ing 256 should flip all bits resulting in a new word with a intVal of -257.
        if(!(TEST_INT_256.not().equals(new Longword(-257))))throw new Exception("NOT 256 FAILED!");
    }
    private static void testRightShift() throws Exception{
        //Right-Shifting 256 1 time should divide by 2 resulting in a word with a intVal of 128.
        if(!(TEST_INT_256.rightShift(1).equals(new Longword(128)))) throw new Exception("RIGHT-SHIFT 256 once FAILED!");
        //Right-Shifting Integer max 31 times should result in all 1 bits being lost and the intVal of the new word being 0.
        if(!(TEST_INT_MAX.rightShift(31).equals(TEST_ZERO))) throw new Exception("RIGHT-SHIFT Integer Max 31 times FAILED!");
        //Right-Shifting a word twice should result in a word with 1/4th its intVal
        if(!(TEST_INT_MAX.rightShift(2).equals(new Longword(Integer.MAX_VALUE/4)))) throw new Exception("Right Shift Integer Max 2 times FAILED!");
    }
    private static void testLeftShift() throws Exception{
        //Left-Shifting 256 1 time should multiply by 2 resulting in a word with a intVal of 512.
        if(!(TEST_INT_256.leftShift(1).equals(new Longword(512)))) throw new Exception("LEFT-SHIFT 256 once FAILED!");
        //Left-Shifting Integer max 31 times should result in the only remaining set bit being the sign bit, meaning its resulting intVal will be Integer Min.
        if(!(TEST_INT_MAX.leftShift(31).equals(TEST_INT_MIN))) throw new Exception("LEFT-SHIFT Integer Max 31 times FAILED!");
        //Left-Shifting Integer min once should result in the sign bit being lost and all bits being 0.
        if(!(TEST_INT_MIN.leftShift(1).equals(TEST_ZERO))) throw new Exception("Right Shift Integer Min 1 time FAILED!");
    }
    private static void testToString() throws Exception{
        if(!(TEST_INT_MAX.toString().equals("0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1"))) throw new Exception("Longword To String FAILED!");
    }
    private static void testGetUnsigned()throws Exception{
        if(TEST_INT_256.getUnsigned() != 256) throw new Exception("getUnsinged for INT_256 FAILED!");
        //If read as an unsigned 32 bit intVal, Integer Min would have a intVal of 2147483648.
        if(TEST_INT_MIN.getUnsigned() != 2147483648L) throw new Exception("getUnsinged for INT_MIN FAILED!");
        if(TEST_INT_MAX.getUnsigned() != Integer.MAX_VALUE) throw new Exception("getUnsinged for INT_MAX FAILED!");
        //Or-ing together Integer Min and Integer Max will result in all bits being set. The unsigned intVal of this 32 bit number should be 4294967295.
        if(TEST_INT_MAX.or(TEST_INT_MIN).getUnsigned() != 4294967295L) throw new Exception("getUnsinged for INT_MAX OR INT_MIN FAILED!");
    }
    private static void testGetSigned() throws Exception{
        if(TEST_INT_256.getSigned() != 256) throw new Exception("getSinged for INT_256 FAILED!");
        if(TEST_INT_MIN.getSigned() != Integer.MIN_VALUE) throw new Exception("getSinged for INT_MIN FAILED!");
        if(TEST_INT_MAX.getSigned() != Integer.MAX_VALUE) throw new Exception("getSinged for INT_MAX FAILED!");
    }
    private static void testCopy() throws Exception{
        Longword test = new Longword();
        test.copy(TEST_INT_256);
        if(!(test.equals(TEST_INT_256))) throw new Exception("Copy Int 256 FAILED!");
        test.copy(TEST_ZERO);
        if(!test.equals(TEST_ZERO)) throw new Exception("Copy Zero FAILED!");
    }
    private static void testSet()throws Exception{
        Longword test = new Longword();
        test.set(256);
        if(!test.equals(TEST_INT_256)) throw new Exception("Set FAILED for 256!");
        test.set(0);
        if(!test.equals(TEST_ZERO)) throw new Exception("Set FAILED for 0!");
    }

    private static void testStringConstructor() throws Exception{
        //TODO: test String constructor better
        Longword test = new Longword("00100000000000010000000000000000");
        if(test.getSigned() != 536936448) throw new Exception("Longword String constructor failed!");
    }
}
