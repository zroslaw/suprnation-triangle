import java.io.*;
import java.util.*;

public class MinTrianglePath {

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
                    Object[] minimalPathNode = new Object[]{row[i],null};
                    currentRowData[i] = new Object[]{row[i],minimalPathNode};
                }
            }else{
                // we are in the middle of the triangle,
                // filling in currentRowData structure
                for (int i=0;i<row.length;i++){
                    // index of bottom left node - i,
                    // index of bottom right node - i+1
                    if ((Integer)prevRowData[i][0]<(Integer)prevRowData[i+1][0]){
                        Object[] minimalPathNode = new Object[]{row[i],prevRowData[i][1]};
                        currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i][0],minimalPathNode};
                    }else{
                        Object[] minimalPathNode = new Object[]{row[i],prevRowData[i+1][1]};
                        currentRowData[i]=new Object[]{row[i]+(Integer)prevRowData[i+1][0],minimalPathNode};
                    }
                }
            }
            prevRowData = currentRowData;
        }

        // Work is done! Perfect!
        t2 = System.currentTimeMillis();

        // Prepare the output messages
        StringBuffer pathSumString = new StringBuffer();
        Object[] minimalPathNode = (Object[])prevRowData[0][1];
        while (minimalPathNode[1]!=null){
            pathSumString.append(minimalPathNode[0]+" + ");
            minimalPathNode = (Object[])minimalPathNode[1];
        }
        pathSumString.setLength(pathSumString.length() - 3);
        System.out.println("Minimal path is: "+ pathSumString+" = "+prevRowData[0][0]);

        // Print some metrics
        System.out.println("Reading input time: "+(t1-t0)/1000.+" seconds");
        System.out.println("Processing triangle time: "+(t2-t1)/1000.+" seconds");

        // Thank you!

    }
}
