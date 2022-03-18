import java.util.Arrays;
import java.util.Stack;

public class Computer {

    //TODO: @Cleanup. Do More factoring and handling of edge cases. A lot of these member variables can be refactored or removed. Functions can be simplified & split into more sensible pieces.

    public static final int REGISTER_COUNT = 16;

    private Bit halted;
    private Memory memory = new Memory();
    private Longword PC =  new Longword(0);
    private Longword[] registers = new Longword[REGISTER_COUNT];
    private Longword currentInstruction;
    private Longword op1, op2, result;
    private Longword sourceReg1, sourceReg2, destReg;
    private Bit[] OPCode;

    //Compare flags
    private Bit greaterThan = new Bit(0);
    private Bit equalTo = new Bit(0);

    //Branch condition bits
    private Bit inequalityCondition;
    private Bit equalityCondition;

    private boolean signedBranchDestination = false;

    private Longword stackPointer;

    /*The 4th to 8th bits in our instruction tell us the first source register so this mask let's us retrieve the intVal*/
    private final Longword SECOND_4_BITS_MASK = new Longword( 0x0F000000);
    /*Second source register bits*/
    private final Longword THIRD_4_BITS_MASK = new Longword( 0x00F00000);
    /*Bits that determine the destination register */
    private final Longword FINAL_4_BITS_MASK = new Longword(0x000F0000);
    /*Bits which will encode the immediate intVal for a move instruction */
    private final Longword IMMEDIATE_VAL_BITMASK = new Longword(0x00FF0000);

    private final Longword BRANCH_DEST_BITMASK = new Longword(0x03FF0000);

    private final int SOURCE1_RIGHT_SHIFT = 24;
    private final int SOURCE2_RIGHT_SHIFT = 20;
    private final int IMMEDIATE_VAL_RIGHT_SHIFT = 16;

    /*Special CPU Instructions not handled by the ALU */
    private final Bit[] HALTOP = {new Bit(0), new Bit(0), new Bit(0), new Bit(0)};
    private final Bit[] MOV = {new Bit(0), new Bit(0), new Bit(0), new Bit(1)};
    private final Bit[] INTERRUPT = {new Bit(0), new Bit(0), new Bit(1), new Bit(0)};
    private final Bit[] JUMP = {new Bit(0), new Bit(0), new Bit(1), new Bit(1)};
    private final Bit[] COMPARE = {new Bit(0), new Bit(1), new Bit(0), new Bit(0)};
    private final Bit[] BRANCH = {new Bit(0), new Bit(1), new Bit(0), new Bit(1)};
    private final Bit[] STACKOP = {new Bit(0), new Bit(1), new Bit(1), new Bit(0)};

    enum Operator{
        HALT,
        MOVE,
        JUMP,
        INTERRUPT,
        COMPARE,
        BRANCH,
        PUSH,
        POP,
        CALL,
        RETURN,
        NORMAL
    };

    private Operator currentOP = Operator.NORMAL;

    //Did the branch condition pass? Is the execution path going to branch?
    private boolean branching = false;

    Computer(){
        for(int i = 0; i < REGISTER_COUNT; i++){
            registers[i] = new Longword(0);
        }
        this.halted = new Bit(1); //Computer not running by default.
        this.stackPointer = new Longword(1020);
    }

    public void run(){
        halted.toggle();
        while(halted.getValue() != 1){
            fetch();
            decode();
            execute();
            store();
            //Reset special OP flags
            branching = signedBranchDestination = false;
            currentOP = Operator.NORMAL;
        }
    }

    public void test(Longword instruction){
        memory.write(PC,instruction);
    }

    private void fetch(){
        /* Fetch current instruction in memory */
        currentInstruction = memory.read(PC);

        /* Increment PC by size of instruction 2 bytes (16 bits) */
        PC = RippleAdder.add(PC, new Longword(2));
    }
    private void decode(){
        //TODO: @CLEANUP This is getting a bit messy! Clean this up if you get time!

        /* Each instruction is 16 bits & will come in the form: 4 Bit OPCode, 4 Bit Source Register, 4 Bit Source Register, 4 Bit Destination Register
           EXCEPT: HALT, MOV, INTERRUPT, JUMP, COMPARE, & BRANCH are special cases!
         */
        OPCode = new Bit[]{ currentInstruction.getBit(0), currentInstruction.getBit(1), currentInstruction.getBit(2), currentInstruction.getBit(3)};
        /* If OPCode is Halt than don't compute the result, the other parts of instruction are meaningless */
        if(Arrays.equals((OPCode), HALTOP)){
            halted.set();
            currentOP = Operator.HALT;
        }
        else if(Arrays.equals(OPCode, INTERRUPT)){
            currentOP = Operator.INTERRUPT;
        }
        /* Move is a special case with different encoding so handle it differently */
        else if(Arrays.equals(OPCode, MOV)){
            currentOP = Operator.MOVE;
            //The only register you care about with move is the destination register
            destReg = currentInstruction.and(SECOND_4_BITS_MASK).rightShift(SOURCE1_RIGHT_SHIFT);

            //Move doesn't do an ALU operation it just takes an 8 Bit immediate intVal
            result = currentInstruction.and(IMMEDIATE_VAL_BITMASK).rightShift(IMMEDIATE_VAL_RIGHT_SHIFT);

            //If the sign bit of the 8 bit immediate intVal is set, extend that sign through the rest of the result by filling it with 1s
            if(result.getBit(24).getValue() == 1){
                for(int i = 0; i < 24; i++){
                    result.setBit(i, new Bit(1));
                }
            }

        }
        else if(Arrays.equals(OPCode, JUMP)){
            currentOP = Operator.JUMP;
            result = currentInstruction.and(IMMEDIATE_VAL_BITMASK).rightShift(IMMEDIATE_VAL_RIGHT_SHIFT);
        }
        else if(Arrays.equals(OPCode, STACKOP)){
            decodeStackOperation();
        }
        else if(Arrays.equals(OPCode, COMPARE)){
            currentOP = Operator.COMPARE;
            //Compare comes in the form 0100 0000 REG1 REG2. So in this case the 2 source registers are 0x00F0 and 0x00FF
            sourceReg1 = currentInstruction.and(THIRD_4_BITS_MASK).rightShift(SOURCE2_RIGHT_SHIFT);
            sourceReg2 = currentInstruction.and(FINAL_4_BITS_MASK).rightShift(16);
            //The work a compare actually does is a subtraction, so set the OPCode to subtract now so execute sends this to the ALU
            OPCode = new Bit[]{new Bit(1), new Bit(1), new Bit(1), new Bit(1)};

            op1 = registers[sourceReg1.getSigned() - 1];
            if(sourceReg2.getSigned() != 0){
                op2 = registers[sourceReg2.getSigned() - 1];
            }
        }
        else if(Arrays.equals(OPCode, BRANCH)){
            currentOP = Operator.BRANCH;
            //These are the parts of the branch instruction that tell us whether to branch on <, >, =, or !=.
            final int INEQUALITY_BIT_POS = 4;
            final int EQUALITY_BIT_POS = 5;
            final int SIGN_BIT_POS = 6;

            inequalityCondition = currentInstruction.getBit(INEQUALITY_BIT_POS);
            equalityCondition = currentInstruction.getBit(EQUALITY_BIT_POS);
            signedBranchDestination = currentInstruction.getBit(SIGN_BIT_POS).getValue() == 1;
        }
        else {
            /* Decode the instruction according to the normal encoding rules */

            /*To get the source register, apply bitmask to the instruction and shift to the right so bits are the lowest order*/
            sourceReg1 = currentInstruction.and(SECOND_4_BITS_MASK).rightShift(SOURCE1_RIGHT_SHIFT);
            sourceReg2 = currentInstruction.and(THIRD_4_BITS_MASK).rightShift(SOURCE2_RIGHT_SHIFT);
            /*To get the destination register apply bitmask and shift right to put bits in the lowest order*/
            destReg = currentInstruction.and(FINAL_4_BITS_MASK).rightShift(16);

            //TODO: For Some instructions like NOT, there is only 1 source register and so the 2nd one ignored. We subtract 1 from the source register value to index the correct element in the array
            //TODO: This is an ugly hack right now to deal with the fact that for unary operations the 2nd source register is set to 0, so that we don't go out of bounds of the register array!
            op1 = registers[sourceReg1.getSigned() - 1];
            if(sourceReg2.getSigned() != 0){
                op2 = registers[sourceReg2.getSigned() - 1];
            }

        }
    }
    private void decodeStackOperation(){
        if(currentInstruction.getBit(4).getValue() == 1 && currentInstruction.getBit(5).getValue() == 1){
            currentOP = Operator.RETURN;
            //Return instruction
            //nothing to decode with a return instruction!
        }
        else if(currentInstruction.getBit(4).getValue() == 1){
            currentOP = Operator.CALL;
            //Call instruction
            result = currentInstruction.and(IMMEDIATE_VAL_BITMASK).rightShift(IMMEDIATE_VAL_RIGHT_SHIFT);
        }
        else if(currentInstruction.getBit(5).getValue() == 1){
            //Pop instruction
            currentOP = Operator.POP;
            destReg = currentInstruction.and(FINAL_4_BITS_MASK).rightShift(16);
        }
        else{
            //Push
            currentOP = Operator.PUSH;
            sourceReg1 = currentInstruction.and(FINAL_4_BITS_MASK).rightShift(16);

        }
    }
    private void execute(){
        //If the computer was halted or the current instruction is a special instruction like move or jump there is nothing for the ALU to execute.
        switch (currentOP){
            case HALT: case MOVE: case JUMP: case PUSH: case POP: case CALL: {
                return;
            }
            case BRANCH:{
                //TODO: This logic could certainly be terser, but this was easier for me to read and understand.
                if(inequalityCondition.getValue() == 1 && equalityCondition.getValue() == 1){
                    //greater than or equal
                    if(greaterThan.getValue() == 1 || equalTo.getValue() == 1){
                        branching = true;
                    }
                }
                else if(inequalityCondition.getValue() == 1 && equalityCondition.getValue() == 0){
                    //greater than
                    if(greaterThan.getValue() == 1 && equalTo.getValue() == 0){
                        branching = true;
                    }
                }
                else if(inequalityCondition.getValue() == 0 && equalityCondition.getValue() == 1){
                    //equal to
                    if(greaterThan.getValue() == 0 && equalTo.getValue() == 1){
                        branching = true;
                    }
                }
                else if(inequalityCondition.getValue() == 0 && equalityCondition.getValue() == 0){
                    //not equal
                    if(equalTo.getValue() == 0){
                        branching = true;
                    }
                }
                if(!branching){
                    return;
                }
                result = currentInstruction.and(BRANCH_DEST_BITMASK).rightShift(16);
                int index = 0;
                if(signedBranchDestination){
                    //If the number is signed extend the sign bit through
                    while(result.getBit(index).getValue() == 0){
                        result.setBit(index, new Bit(1));
                        index++;
                    }
                }
                break;
            }
            case INTERRUPT:{
                //If the lowest bit of the interrupt instruction is set, that means print all 1024 bytes of memory to the screen!
                if(currentInstruction.getBit(15).getValue() == 1){
                    printMemory();
                }
                //If the lowest bit of the interrupt instruction isn't set, that means print all the registers
                else {
                    printRegisters();
                }
                break;
            }
            case NORMAL: case COMPARE:{
                result = ALU.doOp(OPCode, op1, op2);
                break;
            }
        }

    }
    private void store(){
        switch (currentOP) {
            //If the instruction was to halt don't store the intVal it's invalid
            //If instruction was interrupt there is nothing to store
            case HALT: case INTERRUPT: {
                return;
            }
            case JUMP: case BRANCH: case COMPARE:{
                storeBranchOperators();
                break;
            }
            case PUSH: case POP: case CALL: case RETURN:{
                storeStackOperators();
                break;
            }
            default:{
                //If it wasn't a special instruction just store the result in the destination register.
                registers[destReg.getSigned() - 1] = result;
                break;
            }
        }
    }

    private void storeStackOperators(){
        switch (currentOP) {
            case PUSH: {
                memory.write(stackPointer, registers[sourceReg1.getSigned() - 1]);
                System.out.println("Pushing " + registers[sourceReg1.getSigned() - 1].getSigned() + " Onto the stack!");
                stackPointer = RippleAdder.subtract(stackPointer, new Longword(4));
                break;
            }
            case POP: {
                stackPointer = RippleAdder.add(stackPointer, new Longword(4));
                Longword val = memory.read(stackPointer);
                int registerNum = destReg.getSigned();
                System.out.println("Popping " + val.getSigned() + " Off the stack into register: " + registerNum);
                registers[destReg.getSigned() - 1] = val;
                break;

            }
            case CALL: {
                //push the next instruction onto the stack & update stack pointer
                memory.write(stackPointer, PC);
                stackPointer = RippleAdder.subtract(stackPointer, new Longword(4));
                //System.out.println("Pushing instruction address: " + PC.getSigned()/2 + " onto the stack");
                //move PC to the address given by the call instruction
                PC = new Longword(result.getSigned() * 2); // * 2 because there are 2 bytes per instruction!
                //System.out.println("Calling function starting at instruction: " + PC.getSigned()/2);

                break;
            }
            case RETURN: {
                stackPointer = RippleAdder.add(stackPointer, new Longword(4));
                PC = memory.read(stackPointer);
                System.out.println("Returning to instruction at location:" + PC.getSigned() / 2);
                break;
            }
        }

    }

    private void storeBranchOperators(){
        switch (currentOP) {
            case JUMP: {
                System.out.println("JUMPING TO INSTRUCTION: " + result.getSigned());
                //If performing a jump, set program counter to correct position and exit.
                //TODO: There is no constructor for Longword that takes a long so I can't used .getUnsigned() although the instruction number is never negative!
                PC = new Longword(result.getSigned() * 2); // * 2 because there are 2 bytes per instruction!
                break;
            }
            case BRANCH: {
                if (branching) {
                    //if it is a branch instruction and the condition passed change the program counter by whatever number was in the branch instruction
                    PC = RippleAdder.add(PC, Multiplier.multiply(result, new Longword(2)));
                }
                break;
            }
            case COMPARE: {
                if (result.getSigned() == 0) {
                    equalTo.set();
                } else if (result.getSigned() != 0) {
                    equalTo.clear();
                    if (result.getBit(0).getValue() == 1)
                        greaterThan.clear();
                    else if (result.getBit(0).getValue() == 0)
                        greaterThan.set();
                }
                break;
            }
        }
    }

    public void preload(String[] program){
        int instructionIndex = 0;
        Longword packedInstruction, firstHalf, secondHalf;

        /*
           To load a program you need to load a series of 16 bit instructions into memory.
           To do this, load the first and second instructions into two separate words.
           Next left shift the first word by 16 bits so that the instruction is in the top half of the word.
           Finally combine the two instructions into one word by ORing the two halves, and write this to memory.
           Increment the instruction index each time.
         */

        for(int i = 0; i < program.length; i += 2){
            firstHalf = new Longword(program[i]);

            //If there is an odd number of instructions, just set the second-half to zero
            if( i + 1 < program.length)
                secondHalf = new Longword(program[i + 1]);
            else
                secondHalf = new Longword();

            firstHalf = firstHalf.leftShift(16);
            packedInstruction = firstHalf.or(secondHalf);
            //Each packed instruction is 4 bytes long, so we should write the next instruction 4 bytes ahead of the last one
            memory.write(new Longword(instructionIndex++ * 4), packedInstruction);
        }


    }
    //Helper function to print memory when we get an interrupt instruction.
    private void printMemory(){
        int bitIndex = 0;
        for(Bit bit : memory.getMemory()){
            if(bitIndex >= 64 * 8)
                return;
            else {
                System.out.print(bit);
                bitIndex++;
                if(bitIndex % 4 == 0) //Put a space between every 4 bits to make instructions clearer
                    System.out.print(" ");
                if(bitIndex % Longword.WORD_SIZE == 0 && !(bitIndex % (Longword.WORD_SIZE * 4) == 0) ) //Separate each word with a comma
                    System.out.print(", ");
                if(bitIndex % (Longword.WORD_SIZE * 4) == 0 ) //Start a new line ever 4 words
                    System.out.println();
            }

        }
    }
    //Helper function to print registers when we get an interrupt instruction.
    private void printRegisters(){
        int registerIndex = 1;
        for(Longword register : registers){
            if(register == null)
                continue;
            System.out.println("Register:" + registerIndex++ + " Value:" + register.toString());
        }
    }

}
