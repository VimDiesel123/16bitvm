import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler {


    // The Lexer is responsible for taking an input stream of characters and converting it to tokens
    // This Lexer only reads 1 token at a time, which is accessible publicly to be used by the parser
    private static class Lexer {

        enum TokenType {
            NUMBER,
            REGISTER,
            KEYWORD,
            EOF

        }

        static class Token {
            TokenType type;
            String bits;
        }

        /**
         * This just maps the String instruction as it would be written by the programmer to a String representation of the OPCode that the CPU will use
         */
        private static final Map<String, String> keywords = new HashMap<>();
        static {
            keywords.put("MOVE", "0001");
            keywords.put("AND", "1000");
            keywords.put("OR", "1001");
            keywords.put("XOR", "1010");
            keywords.put("NOT", "1011");
            keywords.put("LEFTSHIFT", "1100");
            keywords.put("RIGHTSHIFT", "1101");
            keywords.put("MULTIPLY", "0111");
            keywords.put("ADD", "1110");
            keywords.put("SUBTRACT", "1111");
            keywords.put("HALT", "0000");
            keywords.put("INTERRUPT", "0010");
            keywords.put("JUMP", "0011");
            keywords.put("COMPARE", "0100");
            keywords.put("BranchIfEqual", "010101");
            keywords.put("BranchIfNotEqual", "010100");
            keywords.put("BranchIfGreaterThan", "010110");
            keywords.put("BranchIfGreaterEqual", "010111");
            keywords.put("PUSH", "011000");
            keywords.put("POP", "011001");
            keywords.put("CALL", "011010");
            keywords.put("RETURN", "011011");
        }

        static Token currentToken = new Token();
        static Token lastToken = new Token();
        private static CharacterIterator it;

        /**
         * Load a stream of characters which will be converted to tokens
         * @param stream character input stream
         */
        static void LoadStream(String stream){
            it = new StringCharacterIterator(stream);
        }

        /**
         * Process the next token in the stream and store it in currentToken.
         */
        static void nextToken() {
            //set last token
            lastToken.bits = currentToken.bits;
            lastToken.type = currentToken.type;

            //clear current token
            currentToken.bits = null;
            currentToken.type = null;


            while (it.current() == ' ') {
                it.next();
            }
            if (it.current() == CharacterIterator.DONE) {
                currentToken.type = TokenType.EOF;
            }
            if (Character.isDigit(it.current()) || it.current() == '-') {
                //NUMBER
                //If the number is negative, set this flag and consume the char.
                boolean isNegative = false;
                if (it.current()== '-') {
                    isNegative = true;
                    it.next();
                }

                //"Left-fold" the characters into an int value.
                int val = 0;
                while (Character.isDigit(it.current())) {
                    val *= 10;
                    val += it.current() - '0';
                    assert (val > -126 && val < 127) : "Immediate values can only be 8 bit signed integer!";
                    it.next();
                }
                if (isNegative) val = val * -1;
                currentToken.type = TokenType.NUMBER;

                //Move takes an 8 bit immediate value, all the permutations of branch take a 10 bit immediate value. Jump will take a 10 bit value for now as well.
                //TODO: I don't think this should be the responsibility of the Lexer, but I'm just trying to get something working right now!
                int numLength = lastToken.type == TokenType.REGISTER ? 8 : 10;

                currentToken.bits = valToBitString(val, numLength);
            } else if ((it.current() == 'R' || it.current() == 'r')) {
                if(!Character.isDigit(it.next())){
                    //OOPS! token wasn't a register! (it was "RIGHTSHIFT") backtrack
                    it.previous();
                }
                //REGISTER
                int registerNum = 0;
                while (Character.isDigit(it.current())) {
                    registerNum *= 10;
                    registerNum += it.current() - '0';
                    assert (registerNum > 0 && registerNum < 17) : "There only exists registers between R1 - R16!";
                    it.next();
                }

                //registerNum--; //NOTE: we index registers starting at 0, but the programmer will start at 1 so subtract here to make up for offset.
                currentToken.type = TokenType.REGISTER;
                currentToken.bits = valToBitString(registerNum, 4);

            } if (Character.isAlphabetic(it.current())) {
                //KEYWORD
                StringBuilder instruction = new StringBuilder();
                while (Character.isAlphabetic(it.current())) {
                    instruction.append(it.current());
                    it.next();
                }
                currentToken.type = TokenType.KEYWORD;
                currentToken.bits = keywords.get(instruction.toString());

            }

        }

        /**
         * Convert an integer value to a bit string of arrayLength. Right now they should always be 4 bits long for registers & operations, 8 bits long for immediate values.
         * @param val integer value could be register number, or immediate value etc.
         * @param arrayLength length that the CPU expects the bit string to be
         * @return bitString representation of value
         */
        private static String valToBitString(int val, int arrayLength) {
            String bitString = Integer.toBinaryString(val);

            //if bitstring is longer than array length bits grab from the end
            if (bitString.length() > arrayLength) {
                bitString = bitString.substring(bitString.length() - arrayLength);
            }
            //append leading 0s if bitstring is less than arraylength bits
            else if (bitString.length() < arrayLength) {
                while (bitString.length() < arrayLength) {
                    bitString = '0' + bitString; //TODO: this is extremely inefficient but who cares right now!
                }
            }
            assert (bitString.length() == arrayLength);
            return bitString;
        }

    }

    //This is the one pass "recursive" descent parser which converts tokens generated by lexer straight to "machine code" for our CPU.
    //NOTE: I say: "recursive" , because this parser could easily be changed to handle recursive production rules but; our grammar is so simple-
    //that right now there happens to be no recursive definitions and so the Parser is more of just a decent parser.
    //I couldn't think of a ways to define the Grammar that would have recursive definitions that made things any simpler.
    //Also unlike a parser for a real language we don't generate an AST or anything we just go straight to "machine code" because our language is so simple.

    private static class Parser {

        static int lineNumber = 1;

        // ~~~~~~~ GRAMMAR ~~~~~~~ //
        // <number> ::= NUMBER
        // <register> ::= REGISTER ("R1" | "R2" | ...)
        // <operator> ::= OPERATOR ("MOVE" | "ADD" | ...)
        // <branch condition> ::= "BranchIfEqual" | "BranchIfNotEqual" | "BranchIfLessThan" | "BranchIfGreaterThan"
        // <stack operation> := "PUSH" <register> | "POP" <register> | "CALL <number> | "RETURN"
        // <instruction> ::= <operator> <register> <register> register> | "MOVE" <register> <number> | "COMPARE" <register> <register> | "INTERRUPT" <number> | "JUMP" <number> | <branch condition> <number> | <stack operation> | "HALT"
        // <program> ::= [instruction]...

        /**
         * Assemble a program instruction based off tokens produced by the Lexer. Each instruction will be in the form of a 16 length bit string, and is generated line by line
         * immediately by the parser.
         * @return A valid 16 bit Assembly instruction for our language.
         * @throws ParseError The instruction was unable to be parsed.
         */
        private static String parseInstruction() throws ParseError {
            String instruction = "";
            if (Lexer.currentToken.type != Lexer.TokenType.KEYWORD)
                throw new UnexpectedToken(lineNumber, Lexer.TokenType.KEYWORD, Lexer.currentToken.type);

            String instructionBits = Lexer.currentToken.bits;
            if(instructionBits == null)
                throw new UnknownOperation(lineNumber);

            if (instructionBits.equals(Lexer.keywords.get("MOVE"))) {

                //the current instruction is a move, which is special so handle it differently
                instruction += parseOperator() + parseRegister() + parseNumber();

            } else if (instructionBits.equals(Lexer.keywords.get("INTERRUPT"))) {
                instruction = parseOperator() + "00000000000";
                String number = parseNumber();
                instruction += number.charAt(number.length() - 1); //TODO: This is a super hacky way to construct the interrupt instruction

            } else if(instructionBits.equals(Lexer.keywords.get("JUMP"))){
                //TODO: Jump & Call should throw exceptions if you attempt to jump to a negative instruction
                instruction = parseOperator() + "00" + parseNumber();
            }
            else if(instructionBits.equals(Lexer.keywords.get("COMPARE"))){
                instruction = parseOperator() + "0000" + parseRegister() + parseRegister();
            }
            else if( instructionBits.equals(Lexer.keywords.get("BranchIfEqual")) ||
                     instructionBits.equals(Lexer.keywords.get("BranchIfNotEqual")) ||
                     instructionBits.equals(Lexer.keywords.get("BranchIfGreaterThan")) ||
                    instructionBits.equals(Lexer.keywords.get("BranchIfGreaterEqual"))
            ){
                instruction = parseOperator() + parseNumber();
            }
            else if(instructionBits.equals(Lexer.keywords.get("PUSH")) || instructionBits.equals(Lexer.keywords.get("POP"))){
                //Push and pop are of the form 0110XX00RRRR.
                instruction = parseOperator() + "000000" + parseRegister();
            }
            else if(instructionBits.equals(Lexer.keywords.get("CALL"))){
                instruction = parseOperator() + parseNumber();
            }
            else if(instructionBits.equals(Lexer.keywords.get("RETURN"))){
                //return always just 011011000000
                instruction = parseOperator() + "0000000000";
            }
            else if (instructionBits.equals(Lexer.keywords.get("HALT"))) {

                //if the instruction is to halt, just ignore whatever else may be in there.
                instruction = "0000000000000000";
                Lexer.nextToken();

            } else {

                //default case is an operator followed by 2 source registers & destination register
                instruction += parseOperator() + parseRegister() + parseRegister() + parseRegister();

            }
            return instruction;
        }

        private static String parseRegister() throws ParseError {
            if (Lexer.currentToken.type == Lexer.TokenType.REGISTER) {
                String register = Lexer.currentToken.bits;
                Lexer.nextToken();
                return register;
            } else throw new UnexpectedToken(lineNumber, Lexer.TokenType.REGISTER, Lexer.currentToken.type);
        }

        private static String parseOperator() throws ParseError {
            if (Lexer.currentToken.type == Lexer.TokenType.KEYWORD) {
                String operator = Lexer.currentToken.bits;
                Lexer.nextToken();
                return operator;
            } else throw new UnexpectedToken(lineNumber, Lexer.TokenType.KEYWORD, Lexer.currentToken.type);
        }

        private static String parseNumber() throws ParseError {
            if (Lexer.currentToken.type == Lexer.TokenType.NUMBER) {
                String number = Lexer.currentToken.bits;
                Lexer.nextToken();
                return number;
            } else throw new UnexpectedToken(lineNumber, Lexer.TokenType.NUMBER, Lexer.currentToken.type);
        }
        static class ParseError extends Exception {
            ParseError(int lineNumber) { super("SYNTAX ERROR on LINE:" + lineNumber); }
        }
        static class UnexpectedToken extends ParseError {
            Lexer.TokenType expected, got;

            UnexpectedToken(int lineNumber, Lexer.TokenType expected, Lexer.TokenType got) {
                super(lineNumber);
                this.expected = expected;
                this.got = got;
            }

            @Override
            public String toString() {
                return super.toString() + " | expected: " + expected.name() + " got: " + got.name();
            }
        }
        static class UnknownOperation extends ParseError {
            UnknownOperation(int lineNumber){
                super(lineNumber);
            }
            @Override
            public String toString() {
                return super.toString() + " Unknown Operation";
            }
        }
    }

    public static String[] assemble(String[] src) throws Parser.ParseError {
        ArrayList<String> asm = new ArrayList<>();
        //Java toArray stupidity
        String[] assembly = new String[1];

        //For each line in the source code, parse the instruction and add it to output assembly
        for(String line : src) {
            Lexer.LoadStream(line);
            Lexer.nextToken(); //kick off Lexer
            while(Lexer.currentToken.type != Lexer.TokenType.EOF){
                String instruction = Parser.parseInstruction();
                asm.add(instruction);
            }
            Parser.lineNumber++;
        }
        asm.trimToSize();
        return asm.toArray(assembly);

    }
}
