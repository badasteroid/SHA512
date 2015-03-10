package version_1;

public class Main {

    public static void main(String[] args) {
        Sha512 s = new Sha512();
        s.setMessage("password");
        s.createDigest();
        String digest = s.getDigestInText();
        System.out.println(digest);
    }
}
