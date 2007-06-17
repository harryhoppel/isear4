package org.spbgu.pmpu.athynia.central.matrix;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.text.NumberFormat;
import java.text.ParseException;

public class Matrix {
    private static final Logger LOG = Logger.getLogger(Matrix.class);
    public static final String DELIMETERS = " ,;/><?\'\"\t\n\r!@#$%^&*()=|\\[]:";
    private Jama.Matrix matrix;
    private int size;


    public Matrix(double[][] elements) {
        matrix = new Jama.Matrix(elements);
        size = matrix.getColumnDimension();
    }

    public Matrix(File matrixFile) {
        try {
            FileInputStream fis = new FileInputStream(matrixFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETERS);
            size = Integer.parseInt(tokenizer.nextToken());

            matrix = new Jama.Matrix(size, size);

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                tokenizer = new StringTokenizer(line, DELIMETERS);
                for (int j = 0; tokenizer.hasMoreTokens(); j++) {
                    matrix.set(i, j, Double.parseDouble(tokenizer.nextToken()));
                }
            }
        } catch (IOException e) {
            assert matrixFile != null;
            LOG.error("Error while reading file: " + matrixFile.getAbsolutePath(), e);
        }
    }

    public Matrix(String stringMatrix) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(stringMatrix));
            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMETERS);
            size = Integer.parseInt(tokenizer.nextToken());
            matrix = new Jama.Matrix(size, size);

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                tokenizer = new StringTokenizer(line, DELIMETERS);
                for (int j = 0; tokenizer.hasMoreTokens(); j++) {
                    matrix.set(i, j, Double.parseDouble(tokenizer.nextToken()));
                }
            }
        } catch (IOException e) {
            LOG.error("Error while reading string: ", e);
        }
    }

    public Matrix(int size) {
        matrix = new Jama.Matrix(size, size);
        this.size = size;
    }

    public Matrix() {
        this(0);
    }

    public Matrix (Jama.Matrix matrix) {
        this.matrix = matrix;
        size = matrix.getColumnDimension();
    }

    public Matrix copy() {
        return new Matrix(matrix.copy());
    }

    public String toString() {
        //todo
        StringBuffer buf = new StringBuffer();
        int size = size();
        buf.append(size);
        buf.append('\n');
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buf.append(getDouble(matrix.getArray()[i][j])).append(" ");
            }
            buf.append('\n');
        }
        return buf.toString();
    }

    public double[][] getValues() {
        return matrix.getArray();
    }

    public double determinate() {
        return getDouble(matrix.det());
    }

    public int size() {
        return size;
    }

    public void setElement(int i, int j, double value) {
        matrix.set(i, j, value);
    }

    public Vector getRow(int index) {
        //todo
        double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = matrix.getArray()[i][index];
        }
        return new Vector(result);
    }

    public Vector getColumn(int index) {
        return new Vector(matrix.getArray()[index]);
    }

    public Matrix transpose() {
        return new Matrix(matrix.transpose());
    }

    public void setSize(int size) {
        this.size = size;
        if (size == 0 || matrix.getArray().length != size || matrix.getArray()[0].length != size) {
            matrix = new Jama.Matrix(size, size);
        }
    }

    public Matrix invert() {
        return new Matrix(matrix.inverse());
    }

    public Matrix invert2() {
        Matrix result = new Matrix(size);
        double det = this.determinate();
        System.out.println("det = " + det);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double minor = getMinorDet(i, j);
                result.setElement(j, i, getDouble(minor / det));
            }
        }
        return result;
    }

    public static double getDouble(double d) {
        NumberFormat format = NumberFormat.getInstance();
        Number number = null;
        try {
            number = format.parse(format.format(d));
        } catch (ParseException e) {
            LOG.warn("unknown double format", e);
        }
        assert number != null;
        double result = number.doubleValue();
        return result == 0.0 ? Math.abs(result) : result;
    }

    public boolean equals(Object obj) {
        Matrix matrix = (Matrix) obj;
        boolean result = size == matrix.size();
        if (result) {
            result = matrix.getMatrix().equals(matrix.getMatrix());
        }
        return result;
    }

    public double getMinorDet(int i, int j){
        double sgn = Math.pow(-1, i + j);
        int[] columnIndexes = new int[size - 1];
        int[] rowIndexes = new int[size - 1];
        for (int k = 0; k < size - 1; k++) {
            if (i != k) columnIndexes[k] = k;
            if (j != k) rowIndexes[k] = k;
        }
        Jama.Matrix minor = matrix.getMatrix(columnIndexes, rowIndexes);
        return getDouble(sgn * minor.det());
    }

    private Jama.Matrix getMatrix() {
        return matrix;
    }
}