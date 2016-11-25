/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sixpics;

import cern.colt.Arrays;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



/**
 *
 * @author Jessie Reyna
 */
public class SixPics extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
       
        
        //List of People and how many pics they have
        int imagesEach = 6;
        String[] People = 
        {
            "Alfonso_L",
            "Anton",
            "BrandonH",
            "EduardoTovar",
            "Frank",
            "Gonzalo",
            "Jessie", 
            "juanH", 
            "Luis_Garay",
            "Luis_Rod",
            "matthew",
            "RobertoAlvarez"
                
        };      
        
       BufferedImage[] BuffinImages = new BufferedImage[72];  
       //Matrix2D M2DBuffedTransposed = new Matrix2D[72];
        double[] ImageColAverage = new double [16384];        
        double[][] AllImages1D = new double[imagesEach*People.length][16384];

        AllImages1DIn2D(AllImages1D, People, imagesEach);        
        GetColumAverages(ImageColAverage ,AllImages1D);


        //PRINTING IMAGE OF ALL AVERATES
        BufferedImage outImage = ImageIo.setGrayByteImageArray2DToBufferedImage(Double1DToByte2D(ImageColAverage));
        ImageIo.writeImage(outImage, "jpg", "ALLAVERAGE.jpg");
            
        

        int n = 0;
        
        double[][] AllImagesMinusAverage = new double[imagesEach*People.length][16384];
        for (int r = 0; r < AllImages1D.length; r++)
        {
            
            for (int c = 0; c < AllImages1D[0].length; c++)
            {
                
                AllImagesMinusAverage[r][c] = AllImages1D[r][c] -ImageColAverage[r];
                //System.out.println((n)+" :"+(AllImages1D[r][c]));
                //++n;
                
            }
        }
        

        
//        for (int row = 0; row < AllImages1D.length; row++)
//            for (int col = 0; col < AllImages1D[0].length; col++)
//            {
//                
//                byte[][] grayByteData= ImageIo.getGrayByteImageArray2DFromBufferedImage(grayImage);
//               
//                for (int i = 0; i < inImages.length; i++)
//                {
//                    
//                    
//                    for(int j = 0; j < inImages.length; j++)
//                    {                                            
//                        AllImages1D[row][col] = grayByteData[j][i];
//                    }                         
//                }                
//            }
//        
         
        
        //double [][] trainMatrix = {{1, 7, 3}, {7, -4, 5}, {3, -5, 6}};
        
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("WORKED01");
                double[] myEigenValue = new double[72];
                System.out.println("WORKED02");
                CalEigen(AllImagesMinusAverage, myEigenValue);
               
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
        
    }
    
    
    public static void CalEigen(double [][] trainMatrix, double[] myEigenValue)
    {        
        //FOR EVERY ROW IN trainMatrix
        for (int w = 0; w < trainMatrix.length; w++)
        {
            
            //Create 1D array and enter Row[w] or trainMatrix
            double [] d1 = new double[trainMatrix[w].length];
            for (int i = 0; i < trainMatrix[0].length; i++)
            {
                d1[i] = trainMatrix[w][i];
            }     
            
            //Image is turned into 2D 
            double [][] array2d = conversion1Dto2D(d1, 128, 128);                    
            DoubleMatrix2D correlationMatrix = new DenseDoubleMatrix2D(array2d);             
            EigenvalueDecomposition eigenDecomp = null;
            eigenDecomp = new EigenvalueDecomposition(correlationMatrix);
            DoubleMatrix2D eigenVectorMatrix = eigenDecomp.getV();

            //CREATE DOUBLE 2D of VECTOR VALUES            
            double[][] myvectors = eigenVectorMatrix.toArray();
            DoubleMatrix1D eigenValues = eigenDecomp.getRealEigenvalues();       
            
            myEigenValue = eigenValues.toArray();
            System.out.println("<===================EFACESX--" +myEigenValue.length);
            
            
            
            
            double[][] AllImagesTransposed = TransposeMatrix(trainMatrix);

            
            byte[][] PicArray = new byte [(int)Math.sqrt(d1.length)][(int)Math.sqrt(d1.length)];
            double max = getMaxEigenValue(myEigenValue);
            for (int i = 0; i < 128; i++)
            {
                for (int j = 0; j < 128; j++)
                {
                    int z = (int)((myvectors[j][i] * 255)/max);
                    PicArray[j][i] =  (byte)z;//(z & 0xff);
                }
            } 
            //System.out.println("<===================WORKED 04--"+ w);
           
        } 
    }
    
    
//    public static void CreateImage(byte[][] PicArray)
//    {
//            BufferedImage outImage = ImageIo.setGrayByteImageArray2DToBufferedImage(PicArray);
//            
//            Matrix2D imMat = convertToNormMat(outImage);
//            
//            
//            ImageIo.writeImage(outImage, "jpg", "faces/Pic"+".jpg");    
//    
//    }
    
    public static byte[][] Double1DToByte2D(double[] ImageColAverage)
    {

        
        byte[][] byte2D = new byte[128][128];
        for(int i=0; i<128;i++)
            for(int j=0;j<128;j++)
                byte2D[i][j] = (byte)ImageColAverage[(j*128) + i] ;        
        return byte2D;     
    }
    
    public static double[] Double2DToDouble1D(double[][] D2)
    {
        double[] D1 = new double[16384];
        
        for (int i = 0; i < D2[0].length; i++)
        {
        
            D1[i]= D2[0][i];
        }
        
        return D1;
        
    }
    
    public static double getMaxEigenValue(double[] eigenValues)
    {
        double max = 0;
        for (int i = 0; i < eigenValues.length; i++)
        {
            if (eigenValues[i] > max)
            max = eigenValues[i];
        }
        
        
        return max;
    }
     public static double[][] conversion1Dto2D( double[] array, int rows, int cols )
    {
        if (array.length != (rows*cols))
         throw new IllegalArgumentException("Invalid array length");
         double[][] array2d = new double[rows][cols];
         for ( int i = 0; i < rows; i++ )
               System.arraycopy(array, (i*cols), array2d[i], 0, cols);

              return array2d;
    }
     
    public static double [][] TransposeMatrix(double[][] M)
    {
        double[][] T = new double[M[0].length][M.length];
        
        
        System.out.println("=========ORIGINAL===============");
        for (int i=0; i < M.length; i++)
        {
            for (int j=0; j <  M[0].length; j++)
            {
                M[i][j] = j+i; 
                System.out.print(M[i][j]+ ", ");
            }
            
            System.out.println("\n");
        }
        
        System.out.println("=========TRANSPOSED===============");
        
        for (int i=0; i < T.length; i++)
        {
            for (int j=0; j <  T[0].length; j++)
            {
                T[i][j] = M[j][i]; 
                System.out.print(T[i][j]+ ", ");
            }
            
            System.out.println("\n");
        }
        
        
       
        return T;
    }
    
    public static double[][] MulitplyMatrix(double[][] T, double[][] O)
    {
        double[][] A = new double[T.length][O[0].length];
        
        double t = 0;
        
        for(int i = 0;  i < A.length; i++)
        {
            for(int j = 0; j< A[0].length; j++)
            {
                t += (O[i][j] * T[i][j]);
            
            }        
        
        }

    
        return A;
    }

    private BufferedImage convertToNormMat(BufferedImage grayImage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
//    public static double[][] NormalizeAllImages(BufferedImage[] A)
//    {
//        int imWidth = A[0].getWidth();
//        int imHeight = A[0].getHeight();
//
//        int numRows = A.length;
//        int numCols = imWidth * imHeight;
//        double[][] data = new double[numRows][numCols];
//        for (int i = 0; i < numRows; i++)
//            A[i].getData().getPixels(0, 0, imWidth, imHeight, data[i]);    // one image per row
//    
//    
//    }
    
    public static void GetColumAverages(double[] ImageColAverage, double [][]AllImages1D)
    {
        for (int c = 0; c < AllImages1D[0].length; c++)
        {
            double avg = 10;
            
            for (int r = 0; r < AllImages1D.length; r++)
            {                
                avg += AllImages1D[r][c] ;                 
            }             
            //System.out.println(c+"\nCol  Average = "+ avg);
            ImageColAverage[c] = Math.abs(avg);
        }
            
        for (int i = 0 ; i < ImageColAverage.length; i++)
            ImageColAverage[i] = ImageColAverage[i]/AllImages1D.length;    
    }
    
    
    public static void AllImages1DIn2D(double[][] AllImages1D, String[] People, int imagesEach )
    {
    
        //Put all images out of the files and place them into a 2D array as 1D each
        int count = 0;
        int row = 0;
        //GO THROUGH EACH PERSON 
        for (int j = 0; j < People.length; j++)
        {               
            if (row < imagesEach*People.length) 
            //GO THROUCH EACH PICTURE OF PERSON
            for (int p = 0; p < imagesEach; p++)
            {
                //CREATE NEW GRAYIMAGE AS BYTE 
                byte[][] grayByteData = new byte[128][128];
                BufferedImage inImages = 
                ImageIo.readImage
                ("src/Pics/capturedimages/"+ People[j]+ "/saved/capturedImage"+p+".jpg");

                BufferedImage grayImage =ImageIo.toGray(inImages);   
                
                //BuffinImages[p] = grayImage;
                
                
                
                grayByteData= ImageIo.getGrayByteImageArray2DFromBufferedImage(grayImage);
                
                //TURN 2D GRAY BYTE OF EACH IMAGE INTO 1D OF 2D ONE IMAGE AT A TIME
                for (int x = 0; x < grayByteData.length; x++)
                {
                    for (int y = 0; y < grayByteData[0].length; y++)
                    {
                       AllImages1D[row][count] = (double)(grayByteData[y][x] & 0xff);
                       //System.out.println("VALUE FROM GRAYBYTE " + AllImages1D[row][count]);
                       count++;                                            
                    }
                }
                row++;
                if (count == (grayByteData.length * grayByteData[0].length))
                {
                    count = 0;
                }             
            }            
        }
        
        
    
    
    
    }
}



