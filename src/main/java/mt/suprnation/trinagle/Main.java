package mt.suprnation.trinagle;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Throwable{

        // vars to gather time metrics
        long t0 = System.currentTimeMillis(), t1, t2;

        // the Triangle
        List<int[]> triangle = new ArrayList<>();

        // Read the input stream to the 2D array
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(reader.ready()) {
            String line = reader.readLine();
            String[] stringArray = line.split(" ");
            int[] row = new int[stringArray.length];
            for (int i=0;i<stringArray.length;i++){
                String stringNumber = stringArray[i];
                row[i] = Integer.valueOf(stringNumber);
            }
            triangle.add(row);
        }

        t1 = System.currentTimeMillis();
        // iterating from the bottom of the triangle to the top, row by row
        Object[][] prevRowData = null;
        for (int rowIndex=triangle.size()-1;rowIndex>-1;rowIndex--) {
            int[] row = triangle.get(rowIndex);
            Object[][] currentRowData = new Object[row.length][];
            if (prevRowData==null){
                // it is the first iteration, and we are at the bottom of triangle,
                // initializing the currentRowData structure
                for (int i=0;i<row.length;i++){
                    List<Integer> minimalPath = new LinkedList<>();
                    minimalPath.add(row[i]);
                    currentRowData[i] = new Object[]{row[i],minimalPath};
                }
            }else{
                // we are in the middle of the triangle,
                // filling in currentRowData structure
                for (int i=0;i<row.length;i++){
                    // index of bottom left node - i,
                    // index of bottom right node - i+1
                    if ((Integer)prevRowData[i][0]<(Integer)prevRowData[i+1][0]){
                        List<Integer> minimalPath = (List<Integer>)(prevRowData[i][1]);
                        minimalPath.add(row[i]);
                        currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i][0],minimalPath};
                    }else{
                        List<Integer> minimalPath = (List<Integer>)(prevRowData[i+1][1]);
                        minimalPath.add(row[i]);
                        currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i+1][0],minimalPath};
                    }
                }
            }
            prevRowData = currentRowData;
        }

        t2 = System.currentTimeMillis();

        System.out.println(prevRowData[0][1]);

        System.out.println("Reading input time: "+(t1-t0)/1000.+" seconds");
        System.out.println("Processing triangle time: "+(t2-t1)/1000.+" seconds");


        /**
        // ** Chapter 1 - Intuition and Abstract Thoughts **
        // Let's think clear and speak loudly.
        // We are given stream of arrays which represents rows of the triangle.
        // In case we have option and resources to store all of these rows before starting calculations.
        // In this case we will store rows as arrays and the triangle itself will be represented as 2D array of arrays.
        // Given this kind of data structure we can start to calculate and to gather minimal paths from the bottom to the top.
        // On each iteration which will process next (bottom to top) row of a triangle we will calculate the minimal path to
        // each node in this row based on the minimal paths to the nodes from the previous row.
        // At the end we will have the minimal path to the node on the very top which will be the answer.
        // Let's try to model core data structures and algorithms to have the first grasp of the task.

        // ** Chapter 2 - Modelling data structures **
        int[][] triangle= new int[0][]; // the triangle
        int rowIndex = 0; // rowIndex+1 is equal to size of the row array
        int[] row = new int[0]; // row from the triangle
        // for the previous row and as a result of processing of this row we need to have data structure
        // that stores the node values, their minimal path values and actual paths itself
        // node (minimalPathSum, LinkedList<Integer> minimalPath )
        // node (18, {7, 6, 3, 2})
        Object[][] prevRowData = new Object[0][0]; // element Object[]{11, {6,3,2}}
        Object[][] currentRowData = new Object[row.length][]; // element Object[]{18, {7,6,3,2}}

        // ** Chapter 3 - Core processing logic **
        // Logic of filling in currentRowData
        for (int i=0;i<row.length;i++){
            // index of bottom left node - i,
            // index of bottom right node - i+1
            if ((Integer)prevRowData[i][0]<(Integer)prevRowData[i+1][0]){
                currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i][0],((List<Integer>)prevRowData[i][1]).add(row[i])};
            }else{
                currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i+1][0],((List<Integer>)prevRowData[i+1][1]).add(row[i])};
            }
        }
        prevRowData = currentRowData;

        // ** Chapter 4 - Complexity estimation **
        // So we have implemented basic data structures and key code snippets for the case were all data we are able
        // to store in memory in 2D array before processing
        // complexity of this approach in terms of time is O(N^2), complexity in terms of space is O(N^2)
        // In terms of space it is also important to count size of the currentDataRow array which starts
        // from N elements end decreases by one each iteration, but its internal objects size are growing because
        // they contain minimal path to each element as a list from the bottom of the triangle.
        // So size of the currentRowData structure will be (N-rowIndex)*(rowIndex+1) <= (N^2)/4.

        // ** Chapter 5 - Is it a scalable approach? **
        // Is it possible to store big_data.txt test case in memory in 2D array for processing?
        // It contains 2000 rows, which means order of the integer numbers in the 2D array is
        // about 2000*2000 = 4 millions integers.
        // It is Ok to process it in memory, it will take no more than 4M*4=~16MiB of RAM.

        // ** Chapter 6 - Is it possible to provide better solution? **
        // One intuitive way in which we may optimize the solution it is the way of data processing (processing rows
        // of the triangle) as soon as they injected from the System.in stream, so we do not have to store all
        // the data in memory before processing.
        // In this case we need to implement top-down approach.
        // For each incoming next row we need to calculate minimal path to each node of it and thus to store whole path
        // from the top of the triangle. At the bottom we will have currentRow data structure with size of N^2 -
        // which is not better as our first approach.
        //
        // Is it possible to optimize top-down approach somehow? Maybe by using some kind of dynamic programming
        // approaches with memoization or something similar to reduce amount of memory required to store path from
        // the top of triangle for each node?
        // Yes, it is. We could apply linked objects approach in which for row element we will store only the link
        // to the only element in the upper row which is the minimal path to the top from this node. In this approach
        // we will have to model nodes of the triangle as an objects and store links between them which applies known
        // memory overhead and in worst case we have to store all nodes of the triangle (it is possible to draw
        // the whole triangle with one link connection between nodes in lower and upper rows). So the space complexity
        // will be almost the same as in our first approach.

        // ** Chapter 7 - Epilogue **
        // For now that's all thoughts I have about the task.
        // Maybe it will be useful to try to research this or similar kind of tasks in google or thematic literature
        // (like Concrete Mathematics), but for now it is important to just fulfilling the task at least in one way.
        //
        // Let's do it!
        **/

    }

}
