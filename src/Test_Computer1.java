public class Test_Computer1 {

    static void runTests(){
        testProgram1();
        testProgram2();
        testProgram3();
        testProgram4();
        System.out.print("Computer \"passed\" all tests!");

    }

    private static void testProgram1(){
        Computer computer = new Computer();
        //This program should just add 64 and 32 together, putting the results in register 3, and print the registers + memory at the end.
        String[] program = {
                //Move 64 into register 1
                "0001000101000000",
                //Move 32 into register 2
                "0001001000100000",
                //Add register 1 to register 2 put results in register 3
                "1110000100100011",
                //Print registers using 0 Interrupt
                "0010000000000000",
                //Print entire memory to ensure program is stored correctly.
                //"0010000000000001",
                //Halt
                "0000000000000000"
        };
        computer.preload(program);
        computer.run();
        System.out.println();
    }

    private static void testProgram2(){
        Computer computer = new Computer();
        //This program should add 64 and NEGATIVE 32, putting the results in register 3, and print the registers + memory at the end.
        String[] program = {
                //Move 64 into register 1
                "0001000101000000",
                //Move -32 into register 2
                "0001001011100000",
                //Add register 1 to register 2 put results in register 3
                "1110000100100011",
                //Print registers using 0 Interrupt
                "0010000000000000",
                //Print entire memory to ensure program is stored correctly.
                //"0010000000000001",
                //Halt
                "0000000000000000"
        };
        computer.preload(program);
        computer.run();
        System.out.println();
    }

    private static void testProgram3(){
        Computer computer = new Computer();
        //This program should multiply 64 and 32, putting the results in register 3, and print the registers + memory at the end.
        String[] program = {
                //Move 64 into register 1
                "0001000101000000",
                //Move 32 into register 2
                "0001001000100000",
                //Multiply register 1 to register 2 put results in register 3
                "0111000100100011",
                //Print registers using 0 Interrupt
                "0010000000000000",
                //Print entire memory to ensure program is stored correctly.
                //"0010000000000001",
                //Halt
                "0000000000000000"
        };
        computer.preload(program);
        computer.run();
        System.out.println();
    }

    private static void testProgram4(){
        Computer computer = new Computer();
        //This program should multiply 64 and NEGATIVE 32, putting the results in register 3, and print the registers + memory at the end.
        String[] program = {
                //Move 64 into register 1
                "0001000101000000",
                //Move 32 into register 2
                "0001001011100000",
                //Multiply register 1 to register 2 put results in register 3
                "0111000100100011",
                //Print registers using 0 Interrupt
                "0010000000000000",
                //Print entire memory to ensure program is stored correctly.
                "0010000000000001",
                //Halt
                "0000000000000000"
        };
        computer.preload(program);
        computer.run();
    }

}
