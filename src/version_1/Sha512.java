package version_1;


import java.math.BigInteger;

public class Sha512 {

    /**
     * ATTRIBUTES
     */

    /*Initial values*/
    private static final long[] INITIALS = {0x6a09e667f3bcc908L,
                                            0xbb67ae8584caa73bL,
                                            0x3c6ef372fe94f82bL,
                                            0xa54ff53a5f1d36f1L,
                                            0x510e527fade682d1L,
                                            0x9b05688c2b3e6c1fL,
                                            0x1f83d9abfb41bd6bL,
                                            0x5be0cd19137e2179L};

    /*Constants for 80 rounds in sha512*/
    private static final long[] CONSTANTS  =
                                  {0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
                                   0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
                                   0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
                                   0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
                                   0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
                                   0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
                                   0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
                                   0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
                                   0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
                                   0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
                                   0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
                                   0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
                                   0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
                                   0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
                                   0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
                                   0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
                                   0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
                                   0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
                                   0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
                                   0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L};

    String originalMessage;
    String lengthMessage;
    String paddingMessage;
    String message;
    String[] messageBlock;
    String digest;

    public Sha512() {
        message = "";
        lengthMessage = "";
        paddingMessage = "";
        originalMessage = "";
        digest = "";
    }

    public void setMessage(String m) {
        reset();
        message = m;
    }

    public void reset() {
        message = "";
        lengthMessage = "";
        paddingMessage = "";
        originalMessage = "";
        digest = "";
    }

    public String getDigestInText() {
        String text = "";
        for(int i = 0; i < digest.length(); i+=16) {
            String bits = digest.substring(i, i+16);
            text += Long.toHexString(longValue(bits));
        }
        return text;
    }

    public void createDigest() {
        initialize(message);
        String[] finalDigest = new String[INITIALS.length];
        for(int countBlock = 0; countBlock < messageBlock.length; countBlock++) {
            String block = messageBlock[countBlock];
            String[] words = wordsExpansion(block);
            String[] first = finalDigest;
            //initialize first digest
            if(countBlock == 0) {
                for(int i = 0; i < finalDigest.length; i++) {
                    finalDigest[i] = pad(Long.toBinaryString(INITIALS[i]));
                }
            }

            //80 rounds
            for(int i = 0; i < words.length; i++) {
                finalDigest = round(finalDigest, longValue(words[i]), i);
            }

            //final adding
            for(int i = 0; i < finalDigest.length; i++) {
//                finalDigest[i] = pad(Long.toBinaryString(longValue(finalDigest[i]) + longValue(first[i])));
                finalDigest[i] = Long.toBinaryString(longValue(finalDigest[i]) + longValue(first[i]));
            }
        }
        for(int i = 0; i < finalDigest.length; i++) {
            digest += finalDigest[i] + "";
        }
//        digest = pad(digest);
        System.out.println(digest);
        System.out.println(digest.length());
    }

    private String[] wordsExpansion(String block) {
        String[] words = new String[80];
        for(int i = 0; i < 16; i++) {
            int start = i * 64;
            int end = start + 64;
            words[i] = block.substring(start, end);
        }
        for(int i = 16; i < words.length; i++) {
            //i-16
            long w1 = longValue(words[i-16]);

            //RotShift_1-8-7(i-15)
            long w2 = rotShift(longValue(words[i-15]), 1, 8, 7);

            //i-7
            long w3 = longValue(words[i-7]);

            //RotShift_19-61-6(i-2)
            long w4 = rotShift(longValue(words[i-2]), 19, 61, 6);

            long temp = w1 ^ w2 ^ w3 ^ w4;
            words[i] = pad(Long.toBinaryString(temp));
        }

        return words;
    }

    //preDigest: previous digest or initial digest
    //prevDigest[0] -> A    prevDigest[1] -> B    prevDigest[2] -> C    prevDigest[3] -> D
    //prevDigest[4] -> E    prevDigest[5] -> F    prevDigest[6] -> G    prevDigest[7] -> H
    public String[] round(String[] prevDigest, long word, int numOfRounds) {
        String[] result = new String[8];
        result[1] = pad(prevDigest[0]); //A -> B
        result[2] = pad(prevDigest[1]); //B -> C
        result[3] = pad(prevDigest[2]); //C -> D
        result[5] = pad(prevDigest[4]); //E -> F
        result[6] = pad(prevDigest[5]); //F -> G
        result[7] = pad(prevDigest[6]); //G -> H

        //compute Y -> E (prevDigest[4])
        //conditional(E,F,G)
        long cond = conditional(longValue(prevDigest[4]),longValue(prevDigest[5]),longValue(prevDigest[6]));
        //rotate(E)
        long rot_E = rotate(longValue(prevDigest[4]));
        //addition modulo 2^64
        //temp1 = H + cond(E,F,G) + rotate(E) + w_i + K_i
        long temp1 = longValue(prevDigest[7]) + cond + rot_E + word + CONSTANTS[numOfRounds];
        //Y = temp1 + D
        long Y = temp1 + longValue(prevDigest[3]);
        //Y -> E
        result[4] = pad(Long.toBinaryString(Y));

        //compute X -> A (prevDigest[0])
        //majority(A,B,C)
        long maj = majority(longValue(prevDigest[0]),longValue(prevDigest[1]),longValue(prevDigest[2]));
        //rotate(A)
        long rot_A = rotate(longValue(prevDigest[0]));
        //temp2 = majority(A,B,C) + rotate(A)
        long temp2 = maj + rot_A;
        //X = temp1 + temp2
        long X = temp1 + temp2;
        //X -> A
        result[0] = pad(Long.toBinaryString(X));

        return result;
    }

    /*WORD EXPANSION FUNCTIONS*/
    //RotShift_l-m-n(x) = RotR_l(x) XOR RotR_m(x) XOR ShL_n(x)
    private long rotShift(long x, long l, long m, long n) {
        long res = rotR(x,l) ^ rotR(x,m) ^ shL(x,n);
        return x;
    }
    //Rotate right x by n bits
    private long rotR(long x, long n) {
        return (((x) >>> n) | ((x) << (64 - n)));
    }
    //Shift left x by n bits
    private long shL(long x, long n) {
        return x << n;
    }
    /*END*/

    /*ROUND FUNCTIONS*/
    //MAJORITY(x,y,z) = (x AND y) XOR (y AND z) XOR (z AND x)
    private long majority(long x, long y, long z) {
        return ((x & y) ^ (y & z) ^ (z & x));
    }
    //CONDITIONAL(x,y,z) = (x AND y) XOR (NOT x AND z)
    private long conditional(long x, long y, long z) {
        return ((x & y) ^ ((~x) & z));
    }
    //ROTATE(x) = RotR_28(x) XOR RotR_34(x) XOR RotR_29(x)
    private long rotate(long x) {
        long res = rotR(x,28) ^ rotR(x,34) ^ rotR(x,29);
        return res;
    }
    /*END*/

    private long longValue(String s) {
        return new BigInteger(s,2).longValue();
    }

    private void initialize(String msg) {
        for(int i = 0; i < msg.length(); i++) {
            originalMessage += Integer.toBinaryString(msg.charAt(i));
        }

        lengthMessage = Integer.toBinaryString(originalMessage.length());
        if(lengthMessage.length() != 128) {
            int addBit = 128 - lengthMessage.length();
            String add = "";
            for(int i = 0; i < addBit; i++) {
                add += "0";
            }
            lengthMessage = add + lengthMessage;
        }

        paddingMessage = "1";
        int paddingLength = (((-originalMessage.length()-128) % 1024 + 1024) % 1024) - 1;
        for(int i = 0; i < paddingLength; i++) {
            paddingMessage += "0";
        }

        message = originalMessage + paddingMessage + lengthMessage;

        messageBlock = new String[message.length()/1024];
        for(int i = 0; i < messageBlock.length; i++) {
            int start = i*1024;
            int end = start+1024;
            messageBlock[i] = message.substring(start, end);
        }
    }

    private String pad(String input) {
        String res = "";
        int add = 0;
        if(input.length() % 8 != 0) {
            add = 8 - (input.length()%8);
        }
        for(int i = 0; i < add; i++) {
            res += "0";
        }
        res += input;
        return res;
    }
}
