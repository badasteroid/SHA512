package version_2;

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
    String message;
    String[] messageBlock;
    String digest;

    Sha512() {
        originalMessage = "";
        message = "";
        messageBlock = new String[1];
        digest = "";
    }
    public void setMessage(String m) {
        reset();
        originalMessage = m;
    }

    public String getMessage() {
        String text = "";
        for(int i = 0; i < digest.length(); i+=16) {
            String bits = digest.substring(i, i+16);
            text += Long.toHexString(Long.parseLong(bits,2));
        }
        return text;
    }

    public void createDigest() {
        initialize();

        String[] initialDigest = new String[INITIALS.length];
        for(int i = 0; i < initialDigest.length; i++) {
            initialDigest[i] = pad(Long.toBinaryString(INITIALS[i]));
        }
        wordExpansion(messageBlock[0]);
//        digest = message;
    }

    private String[] wordExpansion(String block) {
        String[] words = new String[80];
        for(int i = 0; i < 16; i++) {
            int start = i * 64;
            int end = start + 64;
            words[i] = block.substring(start, end);
        }

        for(int i = 16; i < words.length; i++) {
            //i-16
            String temp1 = words[i-16];

            //RotShift_1-8-7(W_i-15)
            String temp2 = rotShift(words[i-15], 1, 8, 7);

            //i-7
            String temp3 = words[i-7];

            //RotShift_19-61-6(W_i-2)
            String temp4 = rotShift(words[i-2], 19, 61, 6);

            //words = temp1 xor temp2 xor temp3 xor temp4
            words[i] = xor(xor(xor(temp1,temp2),temp3),temp4);
        }

        for(int i = 0; i < 80; i++) {
            System.out.println(words[i]);
        }
        return words;
    }

    public String[] round(String[] prevDigest, String words, String constants) {
        String[] result = new String[8];
        result[1] = pad(prevDigest[0]); //A -> B
        result[2] = pad(prevDigest[1]); //B -> C
        result[3] = pad(prevDigest[2]); //C -> D
        result[5] = pad(prevDigest[4]); //E -> F
        result[6] = pad(prevDigest[5]); //F -> G
        result[7] = pad(prevDigest[6]); //G -> H

        //compute Y -> E
        String condEFG = conditional(prevDigest[4], prevDigest[5], prevDigest[6]);
        String rotateE = rotate(prevDigest[4]);
        String tempYAdd = addition(words, words);

        return result;
    }
    
    //RotShift_l-m-n(x)
    private String rotShift(String x, int l, int m, int n) {
        String rotRL = rotR(x,l);
        String rotRM = rotR(x,m);
        String shLN = shL(x,n);
        String res = xor(xor(rotRL,rotRM),shLN);
        return res;
    }

    private String rotR(String x, int bits) {
        String res = "";
        char[] ch = new char[x.length()];
        for(int i = 0; i < x.length(); i++) {
            int idx = i+bits;
            if(idx >= x.length()) {
                idx %= x.length();
            }
            ch[idx] = x.charAt(i);
        }
        res = new String(ch);
        return res;
    }

    private String shL(String x, int bits) {
        String res = x;
        for(int i = 0; i < bits; i++) {
            res += "0";
        }
        return res;
    }

    private String xor(String a, String b) {
        String res = "";
        if(a.length() != b.length()) {
            res = a;
        } else {
            for(int i = 0; i < a.length(); i++) {
                int temp1 = a.charAt(i)-48;
                int temp2 = b.charAt(i)-48;
                int xor = temp1 ^ temp2;
                res += xor;
            }
        }
        return res;
    }

    public String addition(String a, String b) {
        String res = "";
        if(a.length() != b.length()) {
            res = a;
        } else {
            int carry = 0;
            for(int i = a.length()-1; i >= 0; i--) {
                int a1 = a.charAt(i)-48;
                int b1 = b.charAt(i)-48;
                int add = (a1 + b1) >= 2 ? (a1+b1)-2 : (a1 + b1);
                add = carry + add >= 2 ? (carry+add)-2 : (carry+add);
                carry = (carry+a1+b1) / 2;
                res = add + res;
            }
            res = carry + res;
            res = res.substring(1, res.length());
        }
        return res;
    }

    public String majority(String x, String y, String z) {
        String res = "";
        for(int i = 0; i < x.length(); i++) {
            int x1 = x.charAt(i)-48;
            int y1 = y.charAt(i)-48;
            int z1 = z.charAt(i)-48;
            int maj = (x1 & y1) ^ (y1 & z1) ^ (z1 & x1);
            res += maj;
        }
        return res;
    }

    public String conditional(String x, String y, String z) {
        String res = "";
        for(int i = 0; i < x.length(); i++) {
            int x1 = x.charAt(i)-48;
            int y1 = y.charAt(i)-48;
            int z1 = z.charAt(i)-48;
            int cond = (x1 & y1) ^ (NOT(x1) & z1);
            res += cond;
        }
        return res;
    }

    public String rotate(String x) {
        String res = xor(xor(rotR(x, 28),rotR(x, 34)),rotR(x,39));
        return res;
    }

    public String and(String a, String b) {
        String res = "";
        if(a.length() != b.length()) {
            res = a;
        } else {
            for(int i = 0; i < a.length(); i++) {
                int temp1 = a.charAt(i)-48;
                int temp2 = b.charAt(i)-48;
                int and = temp1 & temp2;
                res += and + "";
            }
        }
        return res;
    }

    public String not(String a) {
        String res = "";
        for(int i = 0; i < a.length(); i++) {
            res += a.charAt(i) == '0' ? "1" : "0";
        }
        return res;
    }

    public int NOT(int a) {
        int res = a == 0 ? 1 : 0;
        return res;
    }

    private void reset() {
        originalMessage = "";
        message = "";
        messageBlock = new String[1];
        digest = "";
    }

    private void initialize() {
        String tempMessage = "";
        for(int i = 0; i < originalMessage.length(); i++) {
            tempMessage += Integer.toBinaryString(originalMessage.charAt(i));
        }

        String lengthMessage = Integer.toBinaryString(originalMessage.length());
        if(lengthMessage.length() != 128) {
            int addBit = 128 - lengthMessage.length();
            String add = "";
            for(int i = 0; i < addBit; i++) {
                add += "0";
            }
            lengthMessage = add + lengthMessage;
        }

        String paddingMessage = "1";
        int paddingLength = (((-tempMessage.length()-128) % 1024 + 1024) % 1024) - 1;
        for(int i = 0; i < paddingLength; i++) {
            paddingMessage += "0";
        }

        message = tempMessage + paddingMessage + lengthMessage;

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
