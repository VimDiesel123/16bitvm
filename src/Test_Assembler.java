public class Test_Assembler {

    public static void runTests() {
        try {
            //System.out.println();
            //testProgram1();
            //System.out.println();
            //equalityTestProgram(10, 10);
            int[] vec1 = {2,3};
            int[] vec2 = {5,6};
            dotProductTest(vec1, vec2);
            System.out.println();
            System.out.println("Assembler passed all tests!");
        }
        catch (Exception e){
            System.out.print(e.toString());
        }
    }

    private static void testProgram1() throws Exception{
        String[] source = {
                "MOVE R1 4",
                "MOVE R2 1",
                "MOVE R5 -123",
                "NOT R1 R2 R3",
                "ADD R1 R2 R3",
                "SUBTRACT R1 R2 R3",
                "MULTIPLY R3 R5 R4",
                "XOR R2 R4 R15",
                "OR R1 R2 R3",
                "NOT R5 R4 R7",
                "LEFTSHIFT R1 R2 R4",
                "RIGHTSHIFT R1 R2 R4",
                "INTERRUPT 1",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    /**
     * Simple "useful" program in this assembly language, Compares values of x & y. If x == y register 3 will be all 0's & register 4 will be all 1's.
     * @param x val1
     * @param y val2
     * @throws Exception Syntax error
     */
    private static void equalityTestProgram(int x, int y) throws Exception{
        String val1 = Integer.toString(x);
        String val2 = Integer.toString(y);
        String[] source = {
                "MOVE R1 " + val1,
                "MOVE R2 " + val2,
                "XOR R1 R2 R3",
                "NOT R3 R16 R4",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    /**
     * Test some arithmetic by taking the dot product of two vec2's. The result is stored in register 7.
     * @param vec1 a vector of length 2
     * @param vec2 a vector of length 2
     * @throws Exception Syntax Error
     */
    private static void dotProductTest(int[] vec1, int[] vec2) throws Exception{
        int v1x = vec1[0];
        int v1y = vec1[1];
        int v2x = vec2[0];
        int v2y = vec2[1];
        String[] source = {
                "MOVE R1 " + v1x,
                "MOVE R2 " + v1y,
                "MOVE R3 " + v2x,
                "MOVE R4 " + v2y,
                "MULTIPLY R1 R3 R5",
                "MULTIPLY R2 R4 R6",
                "ADD R5 R6 R7",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }
}
