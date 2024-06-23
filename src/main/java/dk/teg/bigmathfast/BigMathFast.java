package dk.teg.bigmathfast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ar.alpertron.ecm.Ecm;
import dk.teg.bigmathfast.euler.EulerTotient;
import dk.teg.bigmathfast.fareyapproximation.BigRational;
import dk.teg.bigmathfast.fareyapproximation.FareyRationalApproxmation;
import dk.teg.bigmathfast.primes.PollardRho;

public class BigMathFast {

    public static BigDecimal PI = new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648566923460348610454326648213393607260249141273724587006606315588174881520920962829254091715364367892590360011330530548820466521384146951941511609433057270365759591953092186117381932611793105118548074462379962749567351885752724891227938183011949129833673362440656643086021394946395224737190702179860943702770539217176293176752384674818467669405132000568127145263560827785771342757789609173637178721468440901224953430146549585371050792279689258923542019956112129021960864034418159813629774771309960518707211349999998372978049951059731732816096318595024459455346908302642522308253344685035261931188171010003137838752886587533208381420617177669147303598253490428755468731159562863882353787593751957781857780532171226806613001927876611195909216420198938095257201065485863278865936153381827968230301952035301852968995773622599413891249721775283479131515574857242454150695950829533116861727855889075098381754637464939319255060400927701671139009848824012858361603563707660104710181942955596198946767837449448255379774726847104047534646208046684259069491293313677028989152104752162056966024058038150193511253382430035587640247496473263914199272604269922796782354781636009341721641219924586315030286182974555706749838505494588586926995690927210797509302955321165344987202755960236480665499119881834797753566369807426542527862551818417574672890977772793800081647060016145249192173217214772350141441973568548161361157352552133475741849468438523323907394143334547762416862518983569485562099219222184272550254256887671790494601653466804988627232791786085784383827967976681454100953883786360950680064225125205117392984896084128488626945604241965285022210661186306744278622039194945047123713786960956364371917287467764657573962413890865832645995813390478027590099465764078951269468398352595709825822620522489407726719478268482601476990902640136394437455305068203496252451749399651431429809190659250937221696461515709858387410597885959772975498930161753928468138268683868942774155991855925245953959431049972524680845987273644695848653836736222626099124608051243884390451244136549762780797715691435997700129616089441694868555848406353422072225828488648158456028506016842739452267467678895252138522549954666727823986456596116354886230577456498035593634568174324112515076069479451096596094025228879710893145669136867228748940560101503308617928680920874760917824938589009714909675985261365549781893129784821682998948722658804857564014270477555132379641451523746234364542858444795265867821051141354735739523113427166102135969536231442952484937187110145765403590279934403742007310578539062198387447808478489683321445713868751943506430218453191048481005370614680674919278191197939952061419663428754440643745123718192179998391015919561814675142691239748940907186494231961567945208095146550225231603881930142093762137855956638937787083039069792077346722182562599661501421503068038447734549202605414665925201497442850732518666002132434088190710486331734649651453905796268561005508106658796998163574736384052571459102897064140110971206280439039759515677157700420337869936007230558763176359421873125147120532928191826186125867321579198414848829164470609575270695722091756711672291098169091528017350671274858322287183520935396572512108357915136988209144421006751033467110314126711136990865851639831501970165151168517143765761835155650884909989859982387345528331635507647918535893226185489632132933089857064204675259070915481416549859461637180270981994309924488957571282890592323326097299712084433573265489382391193259746366730583604142813883032038249037589852437441702913276561809377344403070746921120191302033038019762110110044929321516084244485963766983895228684783123552658213144957685726243344189303968642624341077322697802807318915441101044682325271620105265227211166039666557309254711055785376346682065310989652691862056476931257058635662018558100729360659876486117910453348850346113657686753249441668039626579787718556084552965412665408530614344431858676975145661406800700237877659134401712749470420562230538994561314071127000407854733269939081454664645880797270826683063432858785698305235808933065757406795457163775254202114955761581400250126228594130216471550979259230990796547376125517656751357517829666454779174501129961489030463994713296210734043751895735961458901938971311179042978285647503203198691514028708085990480109412147221317947647772622414254854540332157185306142288137585043063321751829798662237172159160771669254748738986654949450114654062843366393790039769265672146385306736096571209180763832716641627488880078692560290228472104031721186082041900042296617119637792133757511495950156604963186294726547364252308177036751590673502350728354056704038674351362222477158915049530984448933309634087807693259939780541934144737744184263129860809988868741326047215695162396586457302163159819319516735381297416772947867242292465436680098067692823828068996400482435403701416314965897940924323789690706977942236250822168895738379862300159377647165122893578601588161755782973523344604281512627203734314653197777416031990665541876397929334419521541341899485444734567383162499341913181480927777103863877343177207545654532207770921201905166096280490926360197598828161332");
    static {
        PI.setScale(100000, RoundingMode.DOWN);
      }
     
    /*
     * Just an example to run the factorization from a command line.
     * Example :
     *  java -cp bigmathfast-1.0-jar-with-dependencies.jar dk.teg.bigmathfast.BigMathFast 93035149443954345347665179408833277091909532522394543659489519897196854705698057 
     *  This 70 digits will be factorized in 25 seconds. This is worst case for a 70 digits number. 
     * 
     */
    public static void main(String[] args) {

        if (!(args.length == 1 || args.length == 2)) {
            System.out.println("Input is {numberToFactor} {numberThreads (optional, default 1)}");
            System.exit(1);                               
        }
        
        int threads=1;

       
        BigInteger b= new BigInteger(args[0]);
        if (args.length ==2){
          threads=Integer.parseInt(args[1]) ;
        }
        
        long start = System.currentTimeMillis();
        System.out.println("Starting factorization of "+ b +" with #threads="+threads);
        ArrayList<BigInteger> factors = Ecm.factor(b,threads);
        System.out.println("Factorization time in millis:"+(System.currentTimeMillis()-start));
        System.out.println(factors);          
    }
    
    
    /**
     * This method will factorize an integer into prime factors. 
     * 
     * 
     * If the number has less than 22 digits, the Pollardh algorithm will be used.
     * If the number has 22 digits or more, the ECM/Siqs  algorithm will be used.
     * 
     * A number with 70 digits will be factorized in 30 seconds in worst case.
     * The complexity dependens on the second largest prime factor.
     * If the second largest prime factor is larger than 45 digits, it can take
     * many days to factorize.   
     *
     * @see https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     * @see https://www.alpertron.com.ar/ECM.HTM
     *   
     * @param b The BigInteger to be factorized
     * @return ArrayList<BigInteger> with the prime factors in sorted order. 
     *  
     */    
    public static ArrayList<BigInteger> factorize(BigInteger b){        
        if (b.toString().length() <22) {
            return PollardRho.factor(b); 
        }
        else {
          return Ecm.factor(b);                
        }       
    }
       
          /**
     * This method will factorize an integer into prime factors. 
     * 
     * 
     * If the number has less than 22 digits, the Pollardh algorithm will be used.
     * If the number has 22 digits or more, the ECM/Siqs  algorithm will be used.
     * 
     * A number with 70 digits will be factorized in 30 seconds in worst case.
     * The complexity dependens on the second largest prime factor.
     * If the second largest prime factor is larger than 45 digits, it can take
     * many days for factorize.   
     *    
     * @see https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     * @see https://www.alpertron.com.ar/ECM.HTM
     *   
     * @param b The BigInteger to be factorized
     * @param threads number of threads.
     * @return ArrayList<BigInteger> with the prime factors in sorted order. 
     *  
     */
        public static ArrayList<BigInteger> factorize(BigInteger b, int threads){        
        if (b.toString().length() <22) {
            return PollardRho.factor(b); 
        }
        else {
          return Ecm.factor(b, threads);                
        }       
    }
       
       
    
    /**
     * This method will factorize an integer into prime factors. 
     * 
     * Will use the PollardRho algorithm for small numbers (long)
     * To factorize large numbers uset the factorize(BigInteger b) method 
     * 
    
     * @see https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     *   
     * @param b The BigInteger to be factorized
     * @return ArrayList<BigInteger> with the prime factors in sorted order. 
     *  
     */    
    public static List<Long> factorize(Long b){        
             ArrayList<BigInteger> factor = PollardRho.factor(new BigInteger(""+b)); 
             List<Long>  factorsLong  =factor.stream().map( s ->  s.longValue()).collect(Collectors.toList());
             return factorsLong;             
    }
    
    /**
     * Calculate the Euler Totient (phi) for an number     
     * Running time is dependant on the factorization time of the input number
     * @see https://en.wikipedia.org/wiki/Euler%27s_totient_function    
     *   
     * The number of solutions depends on the number of divisors (or total number of prime factors).
     *      
     * @param b The BigInteger to calculate the Euler Totient
     * @return BigInteger The Euler Totient 
     *  
     */    
    public static BigInteger eulerTotient(BigInteger b) {        
        return EulerTotient.eulerTotient(b);
    }
        
    
    /**
     * Calculate the Inverse Euler Totient (invphi) for an number
     * Uses algorithm described by Hansraj Gupta and is the fastests known.
     * 
     * @see http://www.new.dli.ernet.in/rawdataupload/upload/insa/INSA_2/20005a81_22.pdf      
     * @see https://en.wikipedia.org/wiki/Euler%27s_totient_function          
     *   
     * @param b The BigInteger to calculate the Euler Totient
     * @return ArrayList<BigInteger> All numbers have Euler Totient equal to b 
     *  
     */
    public static ArrayList<BigInteger> inverseEulerTotient(BigInteger b) {        
        return EulerTotient.inverseEulerTotient(b);
    }
        
    
    /**
     * Gives the best rational approximation to a decimal number with a give maximum number of
     * digits in the denominator.  
     * The algoritm use the Farey sequence and the Stern Brocot tree- 
     *     
     * @See https://en.wikipedia.org/wiki/Farey_sequence 
     * @see https://handwiki.org/wiki/Stern%E2%80%93Brocot_tree    
     *      
     * @param bigDecimal The number to approximate 
     * @param maxDenominaterDigits maximum denominator. Maximum value is 10E100000 (100000 digits)
     * @return BigRational - a wrapper class for two BigIntegers with nominator and denominator  
     *  
     */        
    public static BigRational rationalApproxmination(BigDecimal bigDecimal, BigInteger maxDenominaterValue) {        
        return FareyRationalApproxmation.fareyApproxWithMaxDenom(bigDecimal, maxDenominaterValue);
        
    }
    
    /**
     * Gives the best rational approximation to a decimal number with a give maximum number of
     * digits in the denominator.  
     * The algoritm use the Farey sequence and the Stern Brocot tree- 
     *     
     * @See https://en.wikipedia.org/wiki/Farey_sequence 
     * @see https://handwiki.org/wiki/Stern%E2%80%93Brocot_tree    
     *      
     * @param bigDecimal The number to approximate 
     * @param maxDenominaterDigits Maximum number of digits in denominator. Max value is 10000
     * @return BigRational - a wrapper class for two BigIntegers with nominator and denominator  
     *  
     */        
    public static BigRational rationalApproxmination(BigDecimal bigDecimal, int maximumDigitsInDemominator) {        
        return FareyRationalApproxmation.fareyApproxWithMaxDenom(bigDecimal, maximumDigitsInDemominator);       
    }
    
}
