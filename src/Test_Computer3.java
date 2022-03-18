public class Test_Computer3 {
    public static void runTests(){
        try{
            testPushPop();
            testCallReturn();
            testStackAdd();
            testStackDotProduct();
            System.out.println("Computer stack operations passed tests!");
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

    }

    private static void testPushPop() throws Exception{
        //test push and pop by pushing some values from registers onto the stack and then pop them off into different registers
        String[] source = {
                //main
                "MOVE R1 1",
                "MOVE R2 2",
                "MOVE R3 3",
                "MOVE R4 4",
                "PUSH R1",
                "PUSH R2",
                "PUSH R3",
                "PUSH R4",
                "POP R15",
                "POP R14",
                "POP R13",
                "POP R12",
                "INTERRUPT 0",
                "HALT",

        };
        String[] asm = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(asm);
        cpu.run();

    }

    private static void testCallReturn() throws Exception{
        //test call & return by calling a couple functions and returning out of them into main
        //should start with 1, add increment it in the first function, double it in the second function and pop the final value into register 4 at the end.
        String[] source = {
                //main
                "MOVE R1 1",
                "PUSH R1",
                "CALL 6",
                "POP R1",
                "INTERRUPT 0",
                "HALT",
                //func 1
                "POP R15", //6
                "POP R2",
                "ADD R1 R2 R3",
                "PUSH R3",
                "CALL 13",
                "PUSH R15",
                "RETURN",
                //func 2
                "POP R14", //13
                "POP R5",
                "MOVE R6 2",
                "MULTIPLY R5 R6 R7",
                "PUSH R7",
                "PUSH R14",
                "RETURN"

        };
        String[] asm = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(asm);
        cpu.run();

    }

    private static void testStackAdd() throws Exception{
        //Test stack with the add program like from the assignment doc.
        //The sum should be popped off the stack and put into register 1 if it worked right.
        String[] source = {
                //main
                "MOVE R1 3",
                "MOVE R2 4",
                "PUSH R1",
                "PUSH R2",
                "CALL 8",
                "POP R1",
                "INTERRUPT 0",
                "HALT",
                //add
                "POP R15",
                "POP R1",
                "POP R2",
                "ADD R1 R2 R3",
                "PUSH R3",
                "PUSH R15",
                "RETURN"
        };
        String[] asm = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(asm);
        cpu.run();
    }

    private static void testStackDotProduct() throws Exception{
        //Test stack with a little program that takes the dot product of 2 vec2's
        //The dot product should be popped off the stack into register 1 if it worked right.
        String[] source = {
                //main
                "MOVE R1 3", //the 2 vec2's would look like <3,4> & <5,8>
                "MOVE R2 4",
                "MOVE R3 5",
                "MOVE R4 8",
                "PUSH R1",
                "PUSH R2",
                "PUSH R3",
                "PUSH R4",
                "CALL 12",
                "POP R1",
                "INTERRUPT 0", // the answer in this case is 47 and it should be in Register 1.
                "HALT",
                //function that might look like dotVec(vec1,vec2) in C
                "POP R15",
                "POP R1",
                "POP R2",
                "POP R3",
                "POP R4",
                "MULTIPLY R1 R3 R5",
                "MULTIPLY R2 R4 R6",
                "ADD R5 R6 R7",
                "PUSH R7",
                "PUSH R15",
                "RETURN"
        };
        String[] asm = Assembler.assemble(source);
        Computer cpu = new Computer();
        cpu.preload(asm);
        cpu.run();
    }





}
