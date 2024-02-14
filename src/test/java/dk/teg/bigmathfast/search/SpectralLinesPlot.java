package dk.teg.bigmathfast.search;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;


public class SpectralLinesPlot {

    public final static int width =800;
    public final static int height = 600;

    private final static BigInteger B1= new BigInteger("1");
    private final static BigInteger B2= new BigInteger("2");
    private final static BigInteger B4= new BigInteger("4");
    
    public static void main(String[] args) throws Exception {

        ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
        
        for (int i=1;i<10;i++) {
            factors.add(new BigInteger("5"));
            factors.add(new BigInteger("13"));
            factors.add(new BigInteger("29"));
        }        
        for (int i =3;i<factors.size();i++) {
         BigInteger number=multiplyList(factors.subList(0, i));
         BufferedImage spectralLinesPlot = SpectralLinesPlot(number,false);
         String fileName="E:\\studie\\bigmathfast\\target\\"+i+"_"+number.toString() +".png";
         File f = new File(fileName);         
         ImageIO.write(spectralLinesPlot, "PNG", f);
         System.out.println("wrote file:"+fileName);
        }
        
        
        
    }


    public static BufferedImage SpectralLinesPlot(BigInteger number , boolean showImage) {

        ArrayList<NumberExpressedInSumOfSquares> allAPofSquares = SquareUtil.getAllAPofSquares(number);       
        System.out.println("number of aps:"+allAPofSquares.size()); 


        TreeSet<BigInteger> diffValues= new TreeSet<BigInteger>(); 

        for (NumberExpressedInSumOfSquares b:allAPofSquares) {
            Tuppel3SquaresInAPBigNumber apBigNumber = b.getAPBigNumber();       
            diffValues.add(apBigNumber.getDifference());                       
        }

        //Find minimum and maximum.
        BigInteger smallest = diffValues.first();
        BigInteger highest = diffValues.last();
        System.out.println("smallest:"+smallest);
        System.out.println("highest:"+highest);
        //map [smallest,highest] to [-1,1]

        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(allAPofSquares);

        TreeSet<BigInteger> bestDiffs = new TreeSet<BigInteger>();
        
        for (NumberExpressedInSumOfSquares n:best3MatchAps.getAps()) {
            Tuppel3SquaresInAPBigNumber apBigNumber = n.getAPBigNumber();
            bestDiffs.add(apBigNumber.getDifference());                 
        }
        
        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
        // into integer pixels
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D ig2 = bi.createGraphics();

        ig2.setBackground(Color.WHITE);
        ig2.clearRect(0, 0, width, height);
        
        Font font = new Font("Arial", Font.BOLD, 18);
        ig2.setFont(font);
        ig2.setColor(Color.BLACK);

        //over spectral lines
        ig2.drawString("n="+number, 100, 40);
        ig2.drawString("factors="+BigMathFast.factorize(number), 100, 80);
        ig2.drawString("#AP="+allAPofSquares.size(), 100, 120);
        ig2.drawString("#Quality="+SquareUtil.calculateQuality(best3MatchAps.getDifference(), number), 100, 160);
        //Below spectral lines

        Tuppel3SquaresInAPBigNumber bestDiff1 = best3MatchAps.getAps().get(0).getAPBigNumber();        
        Tuppel3SquaresInAPBigNumber bestDiff2 = best3MatchAps.getAps().get(1).getAPBigNumber();        
        Tuppel3SquaresInAPBigNumber bestDiff3 = best3MatchAps.getAps().get(2).getAPBigNumber();

        
        font = new Font("Arial", Font.BOLD, 12);
        ig2.setFont(font);
        ig2.drawString("AP1="+bestDiff1,100,420);
        ig2.drawString("diff1="+bestDiff1.getDifference(),200,445);                
        ig2.drawString("AP2="+bestDiff2,100,470);
        ig2.drawString("diff2="+bestDiff2.getDifference(),200,495);
        ig2.drawString("AP3="+bestDiff3,100,520);
        ig2.drawString("diff3="+bestDiff3.getDifference(),200,545);
        
        ig2.drawString("diff="+best3MatchAps.getDifference(),100,570);

        for (BigInteger diff : diffValues) {
            Color color =  bestDiffs.contains(diff) ?  Color.red : Color.black;
            plotVerticalLine(ig2, map2Double(diff ,smallest,highest), color);
        }


        if(showImage) {
            displayImage(bi);        

        }

        return bi;

    }


    //Map into (0,1 interval)
    public static double map2Double(BigInteger value, BigInteger minimum, BigInteger maximum) {

        BigInteger span=maximum.subtract(minimum);
        BigInteger diff=value.subtract(minimum);

        BigDecimal bigD = new BigDecimal("1");            
        BigDecimal d = (bigD.multiply(new BigDecimal(diff))).divide(new BigDecimal(span),6,RoundingMode.CEILING);                                         
        
        return d.doubleValue(); //between 0 and 1                                 
    }


    /**
     * coordinate between 0 and 1
     * 
     */
    private static void plotVerticalLine(Graphics2D ig2,double coordinate, Color color) {
        // (0,0,0,0) is top corner
        //ig2.drawLine(400,200,500,600);
        //Scale x
        int x= (int) (coordinate*(width-200) )+100; //100 pixels to  each side
        ig2.setPaint(color); 
        ig2.drawLine(x,400,x,200);

    }
    public static void displayImage(final BufferedImage image) {
        displayImage("", image);
    }

    public static void displayImage(final String windowTitle,
            final BufferedImage image) {
        new JFrame(windowTitle) {
            {
                final JLabel label = new JLabel("", new ImageIcon(image), 0);
                add(label);
                pack();
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setVisible(true);
            }
        };
    }
    
  public static BigInteger multiplyList(List<BigInteger> list) {
        
        BigInteger b =  B1;
        
        for (BigInteger f : list) {
           b=b.multiply(f);
        }
        return b; 
        }
    
}
