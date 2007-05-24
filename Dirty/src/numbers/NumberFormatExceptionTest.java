package numbers;

/**
 * User: vasiliy
 */
public class NumberFormatExceptionTest {
    public static void main(String[] args) {
        String s = "44446\u0000";
        System.out.println(Integer.parseInt(s));
    }
}
