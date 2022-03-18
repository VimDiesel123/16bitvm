import java.util.Random;

public class Test_Multiplier {

    static void runTests(){
        try {
            testMultiply();
            System.out.println("Multiplier passed all tests!");
        }catch (Exception exception){
            System.out.println(exception.toString());
        }

    }

    private static void testMultiply() throws Exception{
        Random random = new Random();
        /* Test our Multiplier by multiplying two random ints and comparing the results to regular multiplication. Do this 100 times. */
        for(int i = 0; i < 100; i++){
            int num1 = random.nextInt();
            int num2 = random.nextInt();
            final Longword test1 = new Longword(num1);
            final Longword test2 = new Longword(num2);
            Longword product = Multiplier.multiply(test1,test2);
            if(product.getSigned() != num1 * num2) throw new Exception("Error multiplying: " + num1 + " with " + num2);
        }

    }
}
