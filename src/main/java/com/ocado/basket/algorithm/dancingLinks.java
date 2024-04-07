package com.ocado.basket.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class dancingLinks {

    public static ArrayList<Object[]> firstSolution(byte[][] matrix, Object[] columnNames, int numSecondaryColumn) {
        ArrayList<ArrayList<Object[]>> sol = dlx(matrix, columnNames, numSecondaryColumn, true);
        if (sol.size() == 0) throw new IllegalArgumentException("No solution possible");
        return sol.get(0);
    }

    public static ArrayList<ArrayList<Object[]>> allSolutions(byte[][] matrix, Object[] columnNames, int numSecondaryColumn) {
        ArrayList<ArrayList<Object[]>> sol = dlx(matrix, columnNames, numSecondaryColumn, false);
        if (sol.size() == 0) throw new IllegalArgumentException("No solution possible");
        return sol;
    }

    /**
     * @param matrix             byte matrix defining exact cover problem
     * @param columnNames        Objects, that define a toString method
     * @param numSecondaryColumn number of "Secondary Columns" (see Knuth's paper - pg. 18). If there are
     *                           x number of secondary columns they must be the last x columns of the matrix.
     * @param onlyFirst          True to output only first solution
     */
    static ArrayList<ArrayList<Object[]>> dlx(byte[][] matrix, Object[] columnNames, int numSecondaryColumn, boolean onlyFirst) {
        ArrayList<ArrayList<Object[]>> sol = new ArrayList<>();
        columnObject[] currentSolution = new columnObject[columnNames.length];
        columnObject root = createCircularlyLinkedLists(matrix, columnNames);
        // create secondary columns
        int i = 0;
        while (i < numSecondaryColumn) {
            createSecondaryColumn(root);
            i++;
        }
        return search(root, currentSolution, sol, onlyFirst, 0);
    }

    static ArrayList<ArrayList<Object[]>> search(columnObject root,
                                                 columnObject[] currentSolution,
                                                 ArrayList<ArrayList<Object[]>> allSolutions, boolean onlyFirst, int k) {
        if (root.right.equals(root)) {
            ArrayList<Object[]> out = new ArrayList<>();
            for (columnObject o : currentSolution) {
                if (o != null) {
                    ArrayList<Object> temp = new ArrayList<>();
                    temp.add(o.columnHead.data);
                    columnObject i = o.right;
                    while (!i.equals(o)) {
                        temp.add(i.columnHead.data);
                        i = i.right;
                    }
                    out.add(temp.toArray());
                    //out.add(new Object[]{o.i, temp.toArray()});
                }
            }
            allSolutions.add(out);
            return allSolutions;
        }

        columnObject c = smallestColumnHeuristic(root);
        coverColumn(c);
        columnObject r = c.down;
        columnObject j;
        while (!r.equals(c)) {
            j = r.right;
            currentSolution[k] = r;
            while (!j.equals(r)) {
                coverColumn(j.columnHead);
                j = j.right;
            }
            allSolutions = search(root, currentSolution, allSolutions, onlyFirst, k + 1);
            // break if onlyFirst and one solution exits
            if (onlyFirst && allSolutions.size() >= 1) {
                return allSolutions;
            }
            j = r.left;
            while (!j.equals(r)) {
                uncoverColumn(j.columnHead);
                j = j.left;
            }
            currentSolution[k] = null;
            r = r.down;
        }
        uncoverColumn(c);
        return allSolutions;
    }

    static columnObject smallestColumnHeuristic(columnObject root) {
        columnObject j = root.right;
        columnObject c = null;
        int s = Integer.MAX_VALUE;
        while (!j.equals(root)) {
            if (((listHeader) j).size < s) {
                c = j;
                s = ((listHeader) j).size;
            }
            j = j.right;
        }
        return c;
    }

    static void coverColumn(columnObject c) {
        c.left.right = c.right;
        c.right.left = c.left;
        columnObject i = c.down;
        columnObject j;
        while (!i.equals(c)) {
            j = i.right;
            while (!j.equals(i)) {
                j.down.up = j.up;
                j.up.down = j.down;
                j.columnHead.size--;
                j = j.right;
            }
            i = i.down;
        }
    }

    static void uncoverColumn(columnObject c) {
        columnObject i = c.up;
        columnObject j;
        while (!i.equals(c)) {
            j = i.left;
            while (!j.equals(i)) {
                if (j.columnHead == null) {
                    System.out.println(c);
                    System.out.println(j);
                }
                j.columnHead.size++;
                j.down.up = j;
                j.up.down = j;
                j = j.left;
            }
            i = i.up;
        }
        c.left.right = c;
        c.right.left = c;
    }

    @FunctionalInterface
    public interface linkColumnObject {
        public void link(columnObject a, columnObject b);
    }

    static final dancingLinks.linkColumnObject HORIZONTAL_LINKER = (a, b) -> {
        a.right = b;
        b.left = a;
    };
    static final dancingLinks.linkColumnObject VERTICAL_LINKER = (a, b) -> {
        a.down = b;
        b.up = a;
        b.columnHead.size++;
    };

    public static listHeader createCircularlyLinkedLists(byte[][] matrix, Object[] columnNames) {

        for (byte[] row : matrix) {
            if (IntStream.range(0, row.length).map(i -> row[i]).sum() == 0)
                throw new IllegalArgumentException("Row of all zeros in matrix");
        }

        if (columnNames.length != matrix[0].length)
            throw new IllegalArgumentException("ColumnNames array must be same width as matrix");

        // create root or "h"
        listHeader root = new listHeader(null, null, null, null, "root");

        // covert columnNames to list of columnObjects - requires java 8
        List<listHeader> topRow = Arrays.asList(columnNames)
                .stream().map(n -> new listHeader(null, null, null, null, n))
                .collect(Collectors.toList());

        // add root to front of columnObjects list
        topRow.add(0, root);

        // link top row
        linkCOsCircularly(HORIZONTAL_LINKER, topRow.toArray(new columnObject[topRow.size()]));

        // remove root from from of columnObjects list
        topRow.remove(0);

        // create columnObjects array from byte array
        columnObject[][] columnObjectMatrix = new columnObject[matrix.length + 1][columnNames.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < columnNames.length; j++) {
                if (i == 0) {
                    columnObjectMatrix[i][j] = topRow.get(j);
                }
                if (matrix[i][j] == 1) {
                    columnObject a = new columnObject(null, null, null, null, topRow.get(j));
                    a.i = i;
                    a.j = j;
                    columnObjectMatrix[i + 1][j] = a;
                }
            }
        }

        // link rows
        for (columnObject[] row : columnObjectMatrix) {
            // skip first row
            if (Arrays.equals(row, columnObjectMatrix[0]))
                continue;
            columnObject[] columnObjectsArray = Arrays.asList(row)
                    .stream()
                    .filter(n -> n != null)
                    .toArray(size -> new columnObject[size]);

            linkCOsCircularly(HORIZONTAL_LINKER, columnObjectsArray);
        }

        // link columns
        for (int j = 0; j < columnNames.length; j++) {
            final int finalJ = j; // this is clearly stupid - I don't know why IntelliJ makes me do it
            columnObject[] column = IntStream
                    .range(0, columnObjectMatrix.length)
                    .filter(i -> columnObjectMatrix[i][finalJ] != null)
                    .mapToObj(i -> columnObjectMatrix[i][finalJ])
                    .toArray(size -> new columnObject[size]);

            linkCOsCircularly(VERTICAL_LINKER, column);
        }
        return root;
    }


    /**
     * links columnObjects in a circle, horizontally or vertically based on orientation flag
     *
     * @param columnObjectLinker - function implementing linkColumnObject FunctionalInterface
     * @param columnObjects      - list of columnObjects to link
     */
    private static void linkCOsCircularly(linkColumnObject columnObjectLinker, columnObject[] columnObjects) {

        // link each pair of columnObjects
        IntStream.range(0, columnObjects.length - 1).forEach(i -> columnObjectLinker.link(columnObjects[i], columnObjects[i + 1]));

        // link two end columnObjects
        if (columnObjectLinker.equals(VERTICAL_LINKER)) {
            columnObjects[columnObjects.length - 1].down = columnObjects[0];
            columnObjects[0].up = columnObjects[columnObjects.length - 1];
        } else {
            columnObjectLinker.link(columnObjects[columnObjects.length - 1], columnObjects[0]);
        }
    }

    /**
     * @param root make last column of array secondary column
     */
    public static void createSecondaryColumn(columnObject root) {
        columnObject lastCO = root.left;

        lastCO.left.right = lastCO.right;
        lastCO.right.left = lastCO.left;

        lastCO.left = lastCO;
        lastCO.right = lastCO;
    }
}