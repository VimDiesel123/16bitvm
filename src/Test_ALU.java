import java.util.Random;

public class Test_ALU {
    private static final Bit _1_BIT = new Bit(1);
    private static final Bit _0_BIT = new Bit(0);
    private static final Longword TEST_ZERO = new Longword(0);
    private static Random rand = new Random();

    /**
     * NOTE: For all of these tests I am checking the output of our ALU against the output of the Java defined bitwise and arithmetic operations. For example: ALU.ADD(num1,num2) == num1 + num2 etc.
     * For each test I am generating 10 random integers and performing the operation with the ALU, checking that output vs the output of the Java implemented operations
     * and throwing an exception if they return a different result.
     */

    static void runTests(){
        try {
            testOpAnd();
            testOpOr();
            testOpXor();
            testOpNot();
            testOpLeftShift();
            testOpRightShift();
            testOpAdd();
            testOpSubtract();
            testOpMultiply();
            System.out.println("ALU passed all tests!");
        }
        catch (Exception exception){
            System.out.println(exception.toString());
        }

    }
    private static void testOpAnd() throws Exception{
        final Bit[] AND = { _1_BIT,_0_BIT,_0_BIT,_0_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(AND,new Longword(test1),new Longword(test2)).getSigned() != (test1 & test2))) throw new Exception("ALU failed to AND: " + test1 + "with " + test2);
        }

    }

    private static void testOpOr() throws Exception{
        final Bit[] OR = { _1_BIT,_0_BIT,_0_BIT,_1_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(OR,new Longword(test1),new Longword(test2)).getSigned() != (test1 | test2))) throw new Exception("ALU failed to OR: " + test1 + "with " + test2);
        }
    }

    private static void testOpXor() throws Exception{
        final Bit[] XOR = { _1_BIT,_0_BIT,_1_BIT,_0_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(XOR,new Longword(test1),new Longword(test2)).getSigned() != (test1 ^ test2))) throw new Exception("ALU failed to XOR: " + test1 + "with " + test2);
        }
    }

    private static void testOpNot() throws Exception{
        final Bit[] NOT = { _1_BIT,_0_BIT,_1_BIT,_1_BIT};
        for(int i = 0; i < 10; i++){
            int test = rand.nextInt();
            if((ALU.doOp(NOT,new Longword(test),TEST_ZERO).getSigned() != ~test)) throw new Exception("ALU failed to NOT: " + test); //ALU NOT operator just ignores the second Longword, so it doesn't matter what we use.
        }
    }

    private static void testOpLeftShift() throws Exception{
        final Bit[] LEFT_SHIFT = { _1_BIT,_1_BIT,_0_BIT,_0_BIT};
        for(int i = 0; i < 10; i++) {
            int test = rand.nextInt();
            int leftShift = rand.nextInt(32); // Since the ALU only looks at the first 4 bits for left shift, The most we can left shift by is 15. So generate a random int between 0-15.
            if((ALU.doOp(LEFT_SHIFT,new Longword(test),new Longword(leftShift)).getSigned() != test << leftShift)) throw new Exception("ALU failed to LEFT_SHIFT: " + test + "by " + leftShift);
        }

    }
    private static void testOpRightShift() throws Exception{
        final Bit[] RIGHT_SHIFT = { _1_BIT,_1_BIT,_0_BIT,_1_BIT};
        for(int i = 0; i < 10; i++) {
            int test = rand.nextInt();
            int rightShift = rand.nextInt(32); // Technically, we could just let this be any random int because we ignore the upper 28 bits; but I restricted it to between 0-15 to make debugging easier.
            if((ALU.doOp(RIGHT_SHIFT,new Longword(test),new Longword(rightShift)).getSigned() != test >> rightShift)) throw new Exception("ALU failed to RIGHT_SHIFT: " + test + "by " + rightShift);
        }

    }
    private static void testOpAdd() throws Exception{
        final Bit[] ADD = { _1_BIT,_1_BIT,_1_BIT,_0_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(ADD,new Longword(test1),new Longword(test2)).getSigned() != test1 + test2)) throw new Exception("ALU failed to ADD: " + test1 + "and " + test2);
        }

    }
    private static void testOpSubtract() throws Exception{
        final Bit[] SUBTRACT = { _1_BIT,_1_BIT,_1_BIT,_1_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(SUBTRACT,new Longword(test1),new Longword(test2)).getSigned() != test1 - test2)) throw new Exception("ALU failed to SUBTRACT: " + test1 + "and " + test2);
        }

    }
    private static void testOpMultiply() throws Exception{
        final Bit[] MULTIPLY = { _0_BIT,_1_BIT,_1_BIT,_1_BIT};
        for(int i = 0; i < 10; i++) {
            int test1 = rand.nextInt();
            int test2 = rand.nextInt();
            if((ALU.doOp(MULTIPLY,new Longword(test1),new Longword(test2)).getSigned() != test1 * test2)) throw new Exception("ALU failed to MULTIPLY: " + test1 + "and " + test2);
        }

    }

}
