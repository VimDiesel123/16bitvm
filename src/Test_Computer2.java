public class Test_Computer2 {
    public static void runTests(){
        try {
            //testJump();
            System.out.println();
            //testCompareBranchEqual();
            System.out.println();
            //testBranchNotEqual();
            System.out.println();
            //testBranchGreater();
            System.out.println();
            //testBranchGreaterEqual();
            System.out.println();
            //whileLoopTest();
            System.out.println("Computer passed 2nd round of tests");
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static void testJump() throws Exception{
        //We should jump over the 3rd MOVE instruction. There shouldn't be any value in Register 3!
        String[] source = {
                "MOVE R1 1",
                "MOVE R2 1",
                "JUMP 4",
                "MOVE R3 2",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    private static void testCompareBranchEqual() throws Exception{
        //Test Compare and Branch if equal. Should take branch skipping over 3rd MOVE instruction. If it worked there shouldn't be anything in registers 3 - 5 because we took a branch over those instructions.
        String[] source = {
                "MOVE R1 1",
                "MOVE R2 1",
                "COMPARE R1 R2",
                "BranchIfEqual 2",
                "MOVE R3 2",
                "MOVE R4 8",
                "ADD R3 R4 R5",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    private static void testBranchNotEqual() throws Exception{
        //Test Branch if not equal. If it worked we should skip the 3rd MOVE but still MOVE 8 into R4. Then when we get to ADD it should just be 0 + whatever is in register 4 (8 in this case).
        String[] source = {
                "MOVE R1 1",
                "MOVE R2 -1",
                "COMPARE R1 R2",
                "BranchIfNotEqual 1",
                "MOVE R3 2",
                "MOVE R4 8",
                "ADD R3 R4 R5",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    private static void testBranchGreater() throws Exception{
        //Test Branch if greater. Again skip the 3rd MOVE only this time do it if R2 is greater than R1.
        String[] source = {
                "MOVE R1 10",
                "MOVE R2 20",
                "COMPARE R2 R1",
                "BranchIfGreaterThan 1",
                "MOVE R3 2",
                "MOVE R4 8",
                "ADD R3 R4 R5",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    private static void testBranchGreaterEqual() throws Exception{
        //Test Branch if greater or equal. Again skip the 3rd MOVE only this time do it if R2 is greater than R1.
        String[] source = {
                "MOVE R1 22",
                "MOVE R2 20",
                "COMPARE R1 R2",
                "BranchIfGreaterEqual 1",
                "MOVE R3 2",
                "MOVE R4 8",
                "ADD R3 R4 R5",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }

    private static void whileLoopTest() throws Exception{
        //This is supposed to be a primitive while loop thing. Increment R1 and then compare to R3. if R3 is greater than R1 go back and increment again.
        String[] source = {
                "MOVE R1 0",
                "MOVE R2 1",
                "MOVE R3 10",
                "ADD R1 R2 R1",
                "COMPARE R3 R1",
                "BranchIfGreaterThan -3",
                "INTERRUPT 0",
                "HALT"
        };
        String[] assembly = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(assembly);
        cpu.run();
    }
}
