package org.spbgu.pmpu.athynia.central.matrix;

import org.apache.log4j.Logger;

import java.lang.*;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.StringReader;
import java.util.StringTokenizer;

public class Matrix {
    private static final Logger LOG = Logger.getLogger(Matrix.class);
    public static final String DELIMETERS = " ,;/><?\'\"\t\n\r!@#$%^&*()=|\\[]:";

    private double[][] elements;
    private int size;

    public Matrix(double[][] elements) {
        this.elements = elements;
        size = elements.length;
    }

    public Matrix(File matrixFile) {
        try {
            FileInputStream fis = new FileInputStream(matrixFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETERS);
            size = Integer.parseInt(tokenizer.nextToken());
            elements = new double[size][size];

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                tokenizer = new StringTokenizer(line, DELIMETERS);
                for (int j = 0; tokenizer.hasMoreTokens(); j++) {
                    elements[i][j] = Double.parseDouble(tokenizer.nextToken());
                }
            }
        } catch (IOException e) {
            assert matrixFile != null;
            LOG.error("Error while reading file: " + matrixFile.getAbsolutePath(), e);
        }
    }

    public Matrix(String stringMatrix){
        try {
            BufferedReader reader = new BufferedReader(new StringReader(stringMatrix));
            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETERS);
            size = Integer.parseInt(tokenizer.nextToken());
            elements = new double[size][size];

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                tokenizer = new StringTokenizer(line, DELIMETERS);
                for (int j = 0; tokenizer.hasMoreTokens(); j++) {
                    elements[i][j] = Double.parseDouble(tokenizer.nextToken());
                }
            }
        } catch (IOException e) {
            LOG.error("Error while reading string: ", e);
        }
    }

    public Matrix(int size) {
        this.size = size;
        elements = new double[size][size];
    }

    public Matrix copy() {
        double[][] result = new double[size][size];
        for ( int i =0; i < size; i++){
            System.arraycopy(elements[i], 0, result[i], 0, size);
        }
        return new Matrix(result);
    }

    public Matrix invert() {
        double result[][] = new double[size][size];
        double b[][] = new double[size][size];
        int index[] = new int[size];
        for (int i = 0; i < size; ++i) b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < size - 1; ++i)
            for (int j = i + 1; j < size; ++j)
                for (int k = 0; k < size; ++k)
                    b[index[j]][k]
                        -= elements[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < size; ++i) {
            result[size - 1][i] = b[index[size - 1]][i] / elements[index[size - 1]][size - 1];
            for (int j = size - 2; j >= 0; --j) {
                result[j][i] = b[index[j]][i];
                for (int k = j + 1; k < size; ++k) {
                    result[j][i] -= elements[index[j]][k] * result[k][i];
                }
                result[j][i] /= elements[index[j]][j];
            }
        }
        return new Matrix(result);
    }

    private void gaussian(int index[]) {
        double c[] = new double[size];

        // Initialize the index
        for (int i = 0; i < size; ++i) index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < size; ++i) {
            double c1 = 0;
            for (int j = 0; j < size; ++j) {
                double c0 = Math.abs(elements[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < size - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < size; ++i) {
                double pi0 = Math.abs(elements[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < size; ++i) {
                double pj = elements[index[i]][j] / elements[index[j]][j];

                // Record pivoting ratios below the diagonal
                elements[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < size; ++l)
                    elements[index[i]][l] -= pj * elements[index[j]][l];
            }
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(size);
        buf.append('\n');
        for(int i = 0; i < size; i++){
            for(int j =0; j < size; j++){
                buf.append(elements[i][j]).append(" ");
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    public double[][] getValues() {
        return elements;
    }

    public double determinate() {
        int index[] = new int[size];
        gaussian(index);
        double d = 1;
        for (int i = 0; i < size; ++i) d = d * elements[index[i]][i];
        int sgn = 1;
        for (int i = 0; i < size; ++i) {
            if (i != index[i]) {
                sgn = -sgn;
                int j = index[i];
                index[i] = index[j];
                index[j] = j;
            }
        }
        return sgn * d;
    }

    public int size() {
        return size;
    }

    public void setElement(int i, int j, double value) {
        elements[i][j] = value;
    }

    public Vector getRow(int index) {
        double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[i][index];
        }
        return new Vector(result);
    }

    public Vector getColumn(int index) {
        return new Vector(elements[index]);
    }
}