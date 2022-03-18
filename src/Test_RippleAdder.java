import java.util.Random;

public class Test_RippleAdder {

    static void runTests(){
        try{
            testAdd();
            testSubtract();
            System.out.println("Ripple Adder passed all tests!");
        }
        catch (Exception exception){
            System.out.println(exception.toString());
        }
    }

    private static void testAdd() throws Exception{
        Random random = new Random();
        /* Test our ripple adder by adding two random numbers and comparing the results to regular addition. Do this 100 times. */
        for(int i = 0; i < 100; i++){
            int num1 = random.nextInt();
            int num2 = random.nextInt();
            final Longword test1 = new Longword(num1);
            final Longword test2 = new Longword(num2);
            Longword sum = RippleAdder.add(test1,test2);
            if(sum.getSigned() != num1 + num2) throw new Exception("Error adding: " + num1 + " with " + num2);
        }

    }

    private static void testSubtract() throws Exception{
        Random random = new Random();
        /* Test our ripple subtractor by subtracting two random numbers and comparing the results to regular subtraction. Do this 100 times. */
        for(int i = 0; i < 100; i++){
            int num1 = random.nextInt();
            int num2 = random.nextInt();
            final Longword test1 = new Longword(num1);
            final Longword test2 = new Longword(num2);
            Longword result = RippleAdder.subtract(test1,test2);
            if(result.getSigned() != num1 - num2) throw new Exception("Error subtracting: " + num1 + " with " + num2);
        }

    }

}
