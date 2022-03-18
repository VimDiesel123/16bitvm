public class Test_Bit {

    static void runTests(){
        try{
            testGetValue(); //NOTE: test getValue() first because it is called by other test methods!
            testSet();
            testToggle();
            testClear();
            testAnd();
            testOr();
            testXor();
            testNot();
            testToString();
            System.out.println("Bit passed all tests!");
        } catch (Exception exception){
            System.out.println(exception.toString());
        }
    }

    /**
     * The getValue() should return the same intVal passed to the constructor, because we didn't do anything to it.
     * @throws Exception Bit getValue() returned incorrect val.
     */
    private static void testGetValue()throws Exception{
        if(new Bit(0).getValue() != 0) throw new Exception("getValue() FAILED!");
        if(new Bit(1).getValue() != 1) throw new Exception("getValue() FAILED!");
    }

    /**
     * Test both the set() and set(int intVal) methods. If either fail to change the Bit's intVal throw exception.
     * @throws Exception Bit intVal failed to set.
     */
    private static void testSet() throws Exception{
        Bit testBit = new Bit(0);
        testBit.set();
        if(testBit.getValue() != 1) throw new Exception("Bit set FAILED!");
        testBit.set(0);
        if(testBit.getValue() != 0) throw new Exception("Set Bit to 0 FAILED!");
        testBit.set(1);
        if(testBit.getValue() != 1) throw new Exception("Set Bit to 1 FAILED!");
    }

    /**
     * If the Bit is set to 0 toggle should change it to a 1. If the Bit is set to 1, toggle should change it to a 0.
     * @throws Exception Bit toggle failed.
     */
    private static void testToggle()throws Exception{
        Bit testBit = new Bit(0);
        testBit.toggle();
        if(testBit.getValue() != 1) throw new Exception("Bit toggle 0 to 1 FAILED!");
        testBit.toggle();
        if(testBit.getValue() != 0) throw new Exception("Bit toggle 1 to 0 FAILED!");
    }

    /**
     * Bit clear() should set the intVal of the bit to 0. Also test clear a bit who's intVal is already 0 to be safe.
     * @throws Exception Bit clear failed.
     */
    private static void testClear()throws Exception{
        Bit testBit = new Bit(1);
        testBit.clear();
        if(testBit.getValue() != 0) throw new Exception("Bit clear FAILED!");
        testBit = new Bit(0);
        testBit.clear();
        if(testBit.getValue() != 0) throw new Exception("Bit clear FAILED!");
    }

    /**
     * Bit and() should return a Bit who's intVal is 1 if and only if the original bit AND bit being passed to and() both have a intVal of 1.
     * @throws Exception Bit AND failed.
     */
    private static void testAnd() throws Exception{
        if(new Bit(0).and(new Bit(0)).getValue() != 0) throw new Exception("0 AND 0 FAILED!");
        if(new Bit(1).and(new Bit(0)).getValue() != 0) throw new Exception("1 AND 0 FAILED!");
        if(new Bit(0).and(new Bit(1)).getValue() != 0) throw new Exception("0 AND 1 FAILED!");
        if(new Bit(1).and(new Bit(1)).getValue() != 1) throw new Exception("1 AND 1 FAILED!");

    }

    /**
     * Bit or() should return a Bit who's intVal is 1 if and only if either the original bit OR the bit being passed to or() have a intVal of 1.
     * @throws Exception Bit OR failed.
     */
    private static void testOr() throws Exception{
        if(new Bit(0).or(new Bit(0)).getValue() != 0) throw new Exception("0 OR 0 FAILED!");
        if(new Bit(1).or(new Bit(0)).getValue() != 1) throw new Exception("1 OR 0 FAILED!");
        if(new Bit(0).or(new Bit(1)).getValue() != 1) throw new Exception("0 OR 1 FAILED!");
        if(new Bit(1).or(new Bit(1)).getValue() != 1) throw new Exception("1 OR 1 FAILED!");
    }

    /**
     * Bit xor() should return a Bit who's intVal is 1 if and only if either the original bit OR the bit being passed to or() have a intVal of 1, but not both.
     * @throws Exception Bit XOR failed.
     */
    private static void testXor()throws Exception{
        if(new Bit(0).xor(new Bit(0)).getValue() != 0) throw new Exception("0 XOR 0 FAILED!");
        if(new Bit(1).xor(new Bit(0)).getValue() != 1) throw new Exception("1 XOR 0 FAILED!");
        if(new Bit(0).xor(new Bit(1)).getValue() != 1) throw new Exception("0 XOR 1 FAILED!");
        if(new Bit(1).xor(new Bit(1)).getValue() != 0) throw new Exception("1 XOR 1 FAILED!");
    }

    /**
     * If the Bit's intVal is 0, not() should return a bit who's intVal is 1. If the Bit's intVal is 1, not() should return a bit who's intVal is 0.
     * @throws Exception Bit NOT failed.
     */
    private static void testNot() throws Exception{
        if(new Bit(0).not().getValue() != 1) throw new Exception("NOT 0 FAILED!");
        if(new Bit(1).not().getValue() != 0) throw new Exception("NOT 0 FAILED!");
    }

    /**
     * Bit toString() should return string representation the intVal of the bit. 1 or 0.
     * @throws Exception Bit toString failed.
     */
    private static void testToString() throws Exception{
        if(!(new Bit(0).toString().equals("0"))) throw new Exception("To String 0 FAILED!");
        if(!(new Bit(1).toString().equals("1"))) throw new Exception("To String 1 FAILED!");
    }

}
