package version_1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter message: ");
        String msg = sc.nextLine();

        Sha512 s = new Sha512();
        s.setMessage(msg);
        s.createDigest();

        System.out.println("Digest: ");
        String digest = s.getDigest();
        System.out.println(digest);

        sc.close();
    }
}
