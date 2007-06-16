/**
 * User: vasiliy
 */
public class MatrixExperiment {
    private static double[][] matrix1 = new double[1000][1000];
    private static double[][] matrix2 = new double[1000][1000];

    public static void main(String[] args) {
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                matrix1[i][j] = Math.random() * 100;
                matrix2[i][j] = Math.random() * 100;
            }
        }
//        double[][] result = new double[matrix1.length][matrix1[0].length];
//        int t = 0;
        long time = System.currentTimeMillis();
//        for (int i = 0; i < result.length; i++) {
//            for (int j = 0; j < result[i].length; j++) {
//                double sum = 0;
//                for (int k = 0; k < result.length; k++) {
//                    t++;
//                    sum += matrix1[i][k] * matrix2[k][j];
//                }
//                result[i][j] = sum;
//            }
//        }
//        System.out.println(t);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                buf.append(Double.toString(matrix1[i][j]));
                buf.append(" ");
            }
        }
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        String numbersString = buf.toString();
        String[] numbers = numbersString.split(" ");
        double[] numb = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            String s = numbers[i];
            numb[i] = Double.parseDouble(s);
        }
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - time));
        System.out.println(buf.substring(0, 3));
//        System.out.println(buf);
    }
}
