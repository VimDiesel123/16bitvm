public class Multiplier {

    public static Longword multiply (Longword a, Longword b){
       Longword multiplicand = new Longword();
       Longword product = new Longword();
       /* Initialize values of multiplicand and product */
       for(int i = Longword.WORD_SIZE - 1; i >= 0; i--){
           multiplicand.setBit(i,a.getBit(i));
       }
       //System.out.println(multiplicand);

       /*Now do the actual multiplication*/
        for(int i = Longword.WORD_SIZE - 1; i >=0; i--){
            if(b.getBit(i).getValue() == 0){
                multiplicand = multiplicand.leftShift(1);
                continue;
            }
            else if(b.getBit(i).getValue() == 1){
                product = (RippleAdder.add(product,multiplicand));
            }
            multiplicand = multiplicand.leftShift(1);

        }

       return product;



    }
}


