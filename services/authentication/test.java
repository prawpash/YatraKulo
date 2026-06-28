import java.util.Base64;
public class test {
    public static void main(String[] args) {
        try { Base64.getDecoder().decode("${RSA_PUBLIC_KEY}"); } catch(Exception e) { System.out.println(e.getMessage()); }
    }
}
