# bigmathfast
Java implementation of mathematical functions for large numbers. The implementation uses some of the fast
algorithms know. See benchmark tests below.


## Factorization
For numbers less than 22 digits the PollardRho algoritm is used. For numbers larger than 22 digits the algorithm will use ECM/Siqs.

It is the same algorithm decribed here and by same auhor: https://www.alpertron.com.ar/ECM.HTM

Usage:

BigMathFast.factorize(BigInteger b)

## Euler Totient (phi) and inverse Euler Totient (invphi)
The inverse euler totient uses the algorithm described by HansRaj Gupta: https://insa.nic.in/writereaddata/UpLoadedFiles/IJPAM/20005a81_22.pdf

Usage:

BigMathFast.inverseEulerTotient(BigInteger b)

BigMathFast.eulerTotient(BigInteger b)


## Factorization benchmark for worst case numbers (*)

| Number of digits  | bigmathfast(ECM) | Math Wolfram      |
| ------------------| ------------- |----------------------|
| 30                |  80 millis     |                      |
| 40                |  290 millis    |                      |
| 50                |  1.2 sec      |                      |
| 60                |  4,7 sec      |                      |
| 70                |  25 sec       |  21 minutes          |
| 80                |  6 min 30 sec  |  12 hour 30 minutes  |
| 90                |  1 hour 16 minutes    |   6 days 16 hours             |
| 100               |  ???         |      ???             |


(*) Numbers from benchmark table:


30 digits
147275865199119510385557165977 =
26573469154506 * 554221446747557

40 digits:
1468859383233401953850079471177142403357 = 
4344062700916566703 * 33813033659101719361

50 digits: 
8924060181263144762913076834769824195165519271249 =
1580680038114991309325617 * 5645709420045157326539297

60 digits: 
57006543036882955477733064155963100765859988504898777062311 =
135153797414605589804898502907 * 421790168884462740869075554373

70 digits:
2008366610044614145105509426936481148630631765118331491742083502567441 =
229 * 13296624793876881897048465625547 * 659577900793976541082703745871501207

80 digits:
93035149443954345347665179408833277091909532522394543659489519897196854705698057 = 
9365079368113765900517013922746586856483 * 9934261717067827536371100301835771377379

90 digits:
235619162309580984868967318620943039846576548536713751373304739395055583551615448989006587 =
477116622855714229032892479353541386967943093 * 493839767936224340101985740267792644210443759

