import org.spbgu.pmpu.athynia.central.matrix.Matrix;

import java.util.Random;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: A.Selivanov
 * Date: 17.06.2007
 */
public class MatrixGenerator {
    public static void main(String[] args) throws IOException {
          final int COLUMN_SIZE = 100;
        final int ROW_SIZE = 100;
        double[][] elements = new double[COLUMN_SIZE][ROW_SIZE];
        Random random = new Random();
        for (int i = 0; i < COLUMN_SIZE; i++) {
            for (int j = 0; j < ROW_SIZE; j++) {
//                elements[i][j] = random.nextDouble();
                elements[i][j] = i >= j ? 1.0 : 0.0;

            }
        }
        Matrix matrix = new Matrix(elements);
        File out = new File("matrixInverse.txt");
        if (!out.exists())
            out.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));
        writer.write(matrix.toString());
        writer.flush();
        System.out.println("matrix = " + matrix.determinate());
    }
}
