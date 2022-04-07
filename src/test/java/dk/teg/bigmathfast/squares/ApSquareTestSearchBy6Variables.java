package dk.teg.bigmathfast.squares;

import java.math.BigInteger;

public class ApSquareTestSearchBy6Variables {

    private final static BigInteger B2= new BigInteger("2");
    private final static BigInteger B4= new BigInteger("4");
    public static void main(String[] args) {
        
        
        //[(391,1105,1513:1068144), (1057,1105,1151:103776), (221,1105,1547:1172184)]
        BigInteger m = new BigInteger("391");
        BigInteger n = new BigInteger("1513");
        BigInteger r = new BigInteger("1057");
        BigInteger s = new BigInteger("1151");
        BigInteger u = new BigInteger("221");
        BigInteger v = new BigInteger("1547");
        
        BigInteger minDiff = calculateDiff(m, n, r, s, u, v);
        System.out.println(minDiff);
                
        
        
    }
    
    // Must be true that m²+n²=r²+s²=u²+v²
    public static BigInteger calculateDiff(BigInteger m, BigInteger n, BigInteger r, BigInteger s, BigInteger u, BigInteger v) {
        
        
        BigInteger m2=m.multiply(m);
        BigInteger n2=n.multiply(n);        
        BigInteger middle2 = (m2.add(n2)).divide(B2).abs();
        
        BigInteger r2=r.multiply(r);
        //BigInteger s2=s.multiply(s);
        BigInteger u2=u.multiply(u);
       // BigInteger v2=v.multiply(v);
        
        
        BigInteger b = middle2.subtract(m2).abs();        
        BigInteger c = middle2.subtract(r2).abs();
        BigInteger d = middle2.subtract(u2).abs();
        
        
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        
        BigInteger bc_d= (b.add(c).subtract(d)).abs();
        BigInteger bd_c= (b.add(d).subtract(c)).abs();
        BigInteger cd_b= (c.add(d).subtract(b)).abs();
        //return minimum of above 3.
        
        BigInteger minimum = bc_d.min(bd_c);
        minimum = minimum.min(cd_b);
        
        System.out.println("ratio:" +middle2.divide(minimum));
        return minimum;
         
        
    }
}


