/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.heightmapgenerator;

import javafx.scene.image.Image;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Dub-Laptop
 */
public class MeshUtils {
    private static float minX ;
    private static float minY ;
    private static float maxX ;
    private static float maxY ; 
  
    public static TriangleMesh buildTriangleMesh(Image image, int pskip, float maxH, float scale) {
        minX = -(float)image.getWidth() / 2;
        maxX = (float)image.getWidth() / 2;
        
        minY = -(float)image.getHeight() / 2;
        maxY = (float)image.getHeight() / 2;
        
        int subDivX = (int)image.getWidth() / pskip;
        int subDivY = (int)image.getHeight() / pskip;
        
        final int pointSize = 3;
        final int texCoordSize = 2;
        // 3 point indices and 3 texCoord indices per triangle
        final int faceSize = 6;
        int numDivX = subDivX + 1;
        int numVerts = (subDivY + 1) * numDivX;
        float points[] = new float[numVerts * pointSize];
        float texCoords[] = new float[numVerts * texCoordSize];
        int faceCount = subDivX * subDivY * 2;
        int faces[] = new int[faceCount * faceSize];
        
  
        // Create points and texCoords
        for (int y = 0; y <= subDivY; y++) {
            float dy = (float) y / subDivY;
            double fy = (1 - dy) * minY + dy * maxY;
  
            for (int x = 0; x <= subDivX; x++) {
                float dx = (float) x / subDivX;
                double fx = (1 - dx) * minX + dx * maxX;
  
                int index = y * numDivX * pointSize + (x * pointSize);
                points[index] = (float) fx * scale;   // x
                points[index + 1] = (float) fy * scale;  // y
                // color value for pixel at point
                int rgb = ((int)image.getPixelReader().getArgb(x * pskip, y * pskip)); // a bug in here somewhere for aarray OB exception
                int r = (rgb >> 16) & 0xFF; int g = (rgb >> 8) & 0xFF; int b = rgb & 0xFF;
                points[index + 2] = -((float)((r+g+b)/3) / 255) * maxH; // z 
                index = y * numDivX * texCoordSize + (x * texCoordSize);
                texCoords[index] = dx;
                texCoords[index + 1] = dy;
            }
        }
  
        // Create faces
        for (int y = 0; y < subDivY; y++) {
            for (int x = 0; x < subDivX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                int tc00 = y * numDivX + x;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + numDivX;
                int tc11 = tc10 + 1;
  
                int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
                faces[index + 0] = p00;
                faces[index + 1] = tc00;
                faces[index + 2] = p10;
                faces[index + 3] = tc10;
                faces[index + 4] = p11;
                faces[index + 5] = tc11;
  
                index += faceSize;
                faces[index + 0] = p11;
                faces[index + 1] = tc11;
                faces[index + 2] = p01;
                faces[index + 3] = tc01;
                faces[index + 4] = p00;
                faces[index + 5] = tc00;
            }
        }
        //to do
        //int smoothingGroups[] = new int[faces.length / faceSize];
  
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(faces);
        //mesh.getFaceSmoothingGroups().addAll(smoothingGroups);
        
        return mesh;
    }
}
