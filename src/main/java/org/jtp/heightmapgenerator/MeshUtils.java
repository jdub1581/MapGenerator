/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package org.jtp.heightmapgenerator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import org.jtp.heightmapgenerator.utils.Vertex;

/**
 *
 * @author Dub-Laptop
 */
public class MeshUtils {

    private static float minX;
    private static float minY;
    private static float maxX;
    private static float maxY;
    private final static int POINT_SIZE = 3;
    private final static int TEXCOORD_SIZE = 2;
    private final static int FACE_SIZE = 6;

    public static ListView<Point3D> getPointsListView(MeshView meshView) {
        ListView<Point3D> view = new ListView<>();
        ObservableList<Point3D> points = FXCollections.observableArrayList();
        if (meshView.getMesh() != null) {
            for (int i = 0; i < ((TriangleMesh) meshView.getMesh()).getPoints().size() - 4; i++) {
                float x, y, z;
                x = ((TriangleMesh) meshView.getMesh()).getPoints().get(i);
                y = ((TriangleMesh) meshView.getMesh()).getPoints().get(i + 1);
                z = ((TriangleMesh) meshView.getMesh()).getPoints().get(i + 2);

                points.add(new Point3D((double) x, (double) y, (double) z));

                i += 2;// increment to next X
            }
            view.setItems(points);

            view.selectionModelProperty().addListener(new ChangeListener<MultipleSelectionModel<Point3D>>() {
                @Override
                public void changed(ObservableValue<? extends MultipleSelectionModel<Point3D>> ov, MultipleSelectionModel<Point3D> t, MultipleSelectionModel<Point3D> t1) {
                    if (t1.isSelected(t1.getSelectedIndex())) {

                    }
                }
            });
            System.out.println(((TriangleMesh) meshView.getMesh()).getPoints().size() / POINT_SIZE + " :: " + points.size());

            return view;
        } else {
            return null;
        }
    }

    public static TriangleMesh createHeightMap(Image image, int pskip, float maxH, float scale) {
        minX = -(float) image.getWidth() / 2;
        maxX = (float) image.getWidth() / 2;

        minY = -(float) image.getHeight() / 2;
        maxY = (float) image.getHeight() / 2;

        int subDivX = (int) image.getWidth() / pskip;
        int subDivY = (int) image.getHeight() / pskip;

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
            float currY = (float) y / subDivY;
            double fy = (1 - currY) * minY + currY * maxY;

            for (int x = 0; x <= subDivX; x++) {
                float currX = (float) x / subDivX;
                double fx = (1 - currX) * minX + currX * maxX;

                int index = y * numDivX * pointSize + (x * pointSize);
                points[index] = (float) fx * scale;   // x
                points[index + 1] = (float) fy * scale;  // y
                // color value for pixel at point
                int rgb = ((int) image.getPixelReader().getArgb(x * pskip, y * pskip)); // a bug in here somewhere for aarray OB exception
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                points[index + 2] = -((float) ((r + g + b) / 3) / 255) * maxH; // z 

                index = y * numDivX * texCoordSize + (x * texCoordSize);
                texCoords[index] = currX;
                texCoords[index + 1] = currY;
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

    public static TriangleMesh createSphericalHeightMap(Image image, int divisions, float radius, float scale) {

        divisions = correctDivisions(divisions);

        int i = divisions / 2;

        int numPoints = divisions * (i - 1) + 2;
        int numTexCoords = (divisions + 1) * (i - 1) + divisions * 2;
        int numFaces = divisions * (i - 2) * 2 + divisions * 2;

        float fDivisions = 1.0F / divisions;

        float[] points = new float[numPoints * 3];
        float[] texCoords = new float[numTexCoords * 2];
        int[] faces = new int[numFaces * 6];

        int n = 0;
        int i1 = 0;
        int y;
        int i3;
        int i4;
        int i5;
        int x;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;

        try {
            for (y = 0; y < i - 1; y++) {
                float maybeTheta = fDivisions * (y + 1 - i / 2) * 2.0F * 3.141593F;
                float maybeX = (float) Math.sin(maybeTheta);
                float maybeY = (float) Math.cos(maybeTheta);

                float f5 = 0.5F + maybeX * 0.5F;
                for (x = 0; x < divisions; x++) {
                    double tmpTheta = fDivisions * x * 2.0F * 3.141593F;
                    float tmpX = (float) Math.sin(tmpTheta);
                    float tmpY = (float) Math.cos(tmpTheta);
                    points[(n + 0)] = (tmpX * maybeY * radius); //x
                    points[(n + 2)] = (tmpY * maybeY * radius); // y
                    points[(n + 1)] = (maybeX * radius); // z
                    texCoords[(i1 + 0)] = (1.0F - fDivisions * x);
                    texCoords[(i1 + 1)] = f5;
                    n += 3;
                    i1 += 2;
                }
                texCoords[(i1 + 0)] = 0.0F;
                texCoords[(i1 + 1)] = f5;
                i1 += 2;
                System.out.println(maybeX + " : " + maybeY + " : " + maybeTheta + " \n" + i1 + ", " + y);
            }
            points[(n + 0)] = 0.0F;
            points[(n + 1)] = (-radius);
            points[(n + 2)] = 0.0F;
            points[(n + 3)] = 0.0F;
            points[(n + 4)] = radius;
            points[(n + 5)] = 0.0F;

            n += 6;

            y = (i - 1) * divisions;

            float f2 = 0.0039063F;
            for (i3 = 0; i3 < divisions; i3++) {
                texCoords[(i1 + 0)] = (fDivisions * (0.5F + i3));
                texCoords[(i1 + 1)] = f2;
                i1 += 2;
            }
            for (i3 = 0; i3 < divisions; i3++) {
                texCoords[(i1 + 0)] = (fDivisions * (0.5F + i3));
                texCoords[(i1 + 1)] = f2;
                i1 += 2;
            }

            for (i4 = 0; i4 < i - 2; i4++) {
                for (i5 = 0; i5 < divisions; i5++) {
                    x = i4 * divisions + i5;
                    i7 = x + 1;
                    i8 = x + divisions;
                    i9 = i7 + divisions;

                    i10 = x + i4;
                    i11 = i10 + 1;
                    i12 = i10 + (divisions + 1);
                    i13 = i11 + (divisions + 1);

                    faces[(i3 + 0)] = x;
                    faces[(i3 + 1)] = i10;
                    faces[(i3 + 2)] = (i7 % divisions == 0 ? i7 - divisions : i7);
                    faces[(i3 + 3)] = i11;
                    faces[(i3 + 4)] = i8;
                    faces[(i3 + 5)] = i12;
                    i3 += 6;

                    faces[(i3 + 0)] = (i9 % divisions == 0 ? i9 - divisions : i9);
                    faces[(i3 + 1)] = i13;
                    faces[(i3 + 2)] = i8;
                    faces[(i3 + 3)] = i12;
                    faces[(i3 + 4)] = (i7 % divisions == 0 ? i7 - divisions : i7);
                    faces[(i3 + 5)] = i11;
                    i3 += 6;
                }
            }
            i4 = y;
            i5 = (i - 1) * (divisions + 1);
            for (x = 0; x < divisions; x++) {
                i7 = x;
                i8 = x + 1;
                i9 = i5 + x;
                faces[(i3 + 0)] = i4;
                faces[(i3 + 1)] = i9;
                faces[(i3 + 2)] = (i8 == divisions ? 0 : i8);
                faces[(i3 + 3)] = i8;
                faces[(i3 + 4)] = i7;
                faces[(i3 + 5)] = i7;
                i3 += 6;
            }
            i4 += 1;
            i5 += divisions;
            x = (i - 2) * divisions;
            for (i7 = 0; i7 < divisions; i7++) {
                i8 = x + i7;
                i9 = x + i7 + 1;
                i10 = i5 + i7;
                i11 = (i - 2) * (divisions + 1) + i7;
                i12 = i11 + 1;
                faces[(i3 + 0)] = i4;
                faces[(i3 + 1)] = i10;
                faces[(i3 + 2)] = i8;
                faces[(i3 + 3)] = i11;
                faces[(i3 + 4)] = (i9 % divisions == 0 ? i9 - divisions : i9);
                faces[(i3 + 5)] = i12;
                i3 += 6;
            }
        } catch (Exception e) {

        }

        TriangleMesh localTriangleMesh = new TriangleMesh();
        localTriangleMesh.getPoints().setAll(points);
        localTriangleMesh.getTexCoords().setAll(texCoords);
        localTriangleMesh.getFaces().setAll(faces);

        return localTriangleMesh;
    }

    private static int correctDivisions(int paramInt) {
        return (paramInt + 3) / 4 * 4;
    }

    /*
     Let the radius from the center of the hole to the center of the torus tube be "c", 
     and the radius of the tube be "a". 
     Then the equation in Cartesian coordinates for a torus azimuthally symmetric about the z-axis is
     (c-sqrt(x^2+y^2))^2+z^2=a^2
     and the parametric equations are
     x = (c + a * cos(v)) * cos(u)	
     y = (c + a * cos(v)) * sin(u)	
     z =  a * sin(v)	
     (for u,v in [0,2pi). 
     
     Three types of torus, known as the standard tori, are possible, 
     depending on the relative sizes of a and c. c>a corresponds to the ring torus (shown above), 
     c=a corresponds to a horn torus which is tangent to itself at the point (0, 0, 0), 
     and c<a corresponds to a self-intersecting spindle torus (Pinkall 1986). 
    */
    public static Group createToroidMesh(float lRadius,float sRadius, int divisions) {
        final Group root = new Group();
        float[] points = new float[(divisions * divisions) * POINT_SIZE],
                texCoords = new float[(divisions * divisions) * TEXCOORD_SIZE];
        int[] faces = new int[(divisions * divisions) * FACE_SIZE];
        
        int pointIndex = 0, texIndex = 0, faceIndex = 0, smoothIndex = 0;
        float divFrac = 1.0f / divisions;
        float radian, x, y, z;
        float f1 = 0.0039063F;
        
        // create points
        for(int vertIndex = 0; vertIndex < divisions; vertIndex++){
            // individual vertex
            radian = divFrac * vertIndex * 2.0f * 3.141592653589793f;
            
            for(int crossSectionIndex = 0; crossSectionIndex < divisions; crossSectionIndex++){
                // cross section vertices
                float localRadian = divFrac * crossSectionIndex * 2.0f * 3.141592653589793f;
                
                points[pointIndex] =     x = (lRadius + sRadius * ((float)Math.cos(radian))) * ((float)Math.cos(localRadian));
                points[pointIndex + 1] = y = (lRadius + sRadius * ((float)Math.cos(radian))) * ((float)Math.sin(localRadian));
                points[pointIndex + 2] = z = (sRadius * (float)Math.sin(radian));
                                
                pointIndex += 2;
                
                /**
             * points:
             * 1      3
             *  -------   texture:
             *  |\    |  1,1    1,0
             *  | \   |    -------
             *  |  \  |    |     |
             *  |   \ |    |     |
             *  |    \|    -------
             *  -------  0,1    0,0
             * 0      2
             *
             * texture[3] 0,0 maps to vertex 2
             * texture[2] 0,1 maps to vertex 0
             * texture[0] 1,1 maps to vertex 1
             * texture[1] 1,0 maps to vertex 3
             *
             * Two triangles define rectangular faces:
             * p0, t0, p1, t1, p2, t2 // First triangle of a textured rectangle
             * p0, t0, p2, t2, p3, t3 // Second triangle of a textured rectangle
             */
                if(vertIndex == 0 && crossSectionIndex == 0){
                    root.getChildren().add(new Vertex(x, y, z, Color.DARKBLUE)); //tc 0,1
                }else if(vertIndex == 0 && crossSectionIndex == 1){        
                    root.getChildren().add(new Vertex(x, y, z, Color.DARKGREEN));//tc 0,0
                }else if(vertIndex == 1 && crossSectionIndex == 0){        
                    root.getChildren().add(new Vertex(x, y, z, Color.DARKCYAN)); //tc 1,1
                }else if(vertIndex == 1 && crossSectionIndex == 1){        
                    root.getChildren().add(new Vertex(x, y, z, Color.DARKVIOLET));//tc 1,0
                }else{
                    root.getChildren().add(new Vertex(x, y, z));
                }
                //root.getChildren().add(new Vertex(x, y, z));
                //System.out.println("x: "+x+", y: "+y+", z: "+z);  
            }
        }
        
        
       /* double s, t, x, y, z, twopi = 2 * Math.PI;
        for (int i = 0; i < numc; i++) {
            for (int j = 0; j <= numt; j++) {
                for (int k = 1; k >= 0; k--) {
                    s = (i + k) % numc + 0.5;
                    t = j % numt;
                    x = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.cos(t * twopi / numt);
                    y = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.sin(t * twopi / numt);
                    z = 0.1 * Math.sin(s * twopi / numc);
                    root.getChildren().add(new Vertex(x, y, z));
                    System.out.println("x: "+x+", y: "+y+", z: "+z);
                }// k
            }// j
        }// i
        */
        
        
        /*TriangleMesh localTriangleMesh = new TriangleMesh();
         localTriangleMesh.getPoints().setAll(points);
         localTriangleMesh.getTexCoords().setAll(texCoords);
         localTriangleMesh.getFaces().setAll(faces);

         return localTriangleMesh;*/
        root.setRotationAxis(Rotate.X_AXIS);
        root.setRotate(90);
        return root;
    }

    static TriangleMesh createCylindricalMesh(int divisions, float height, float radius) {
        int numPoints = divisions * 2 + 2;
        int numTexCoords = (divisions + 1) * 4 + 1;
        int numFaces = divisions * 4;

        float f1 = 0.0039063F;

        float fDivisions = 1.0F / divisions;
        height *= 0.5F;

        float[] points = new float[numPoints * 3];
        float[] texCoords = new float[numTexCoords * 2];
        int[] faces = new int[numFaces * 6];
        int[] smoothingGroups = new int[numFaces];

        int m = 0;
        int index = 0;
        double radian;
        for (int i1 = 0; i1 < divisions; i1++) {
            radian = fDivisions * i1 * 2.0F * 3.141592653589793D;

            points[(m + 0)] = ((float) (Math.sin(radian) * radius));
            points[(m + 2)] = ((float) (Math.cos(radian) * radius));
            points[(m + 1)] = height;
            texCoords[(index + 0)] = (1.0F - fDivisions * i1);
            texCoords[(index + 1)] = (1.0F - f1);
            m += 3;
            index += 2;
        }

        texCoords[(index + 0)] = 0.0F;
        texCoords[(index + 1)] = (1.0F - f1);
        index += 2;

        for (int i1 = 0; i1 < divisions; i1++) {
            radian = fDivisions * i1 * 2.0F * 3.141592653589793D;
            points[(m + 0)] = ((float) (Math.sin(radian) * radius));
            points[(m + 2)] = ((float) (Math.cos(radian) * radius));
            points[(m + 1)] = (-height);
            texCoords[(index + 0)] = (1.0F - fDivisions * i1);
            texCoords[(index + 1)] = f1;
            m += 3;
            index += 2;
        }

        texCoords[(index + 0)] = 0.0F;
        texCoords[(index + 1)] = f1;
        index += 2;

        points[(m + 0)] = 0.0F;
        points[(m + 1)] = height;
        points[(m + 2)] = 0.0F;
        points[(m + 3)] = 0.0F;
        points[(m + 4)] = (-height);
        points[(m + 5)] = 0.0F;
        m += 6;

        for (int i1 = 0; i1 <= divisions; i1++) {
            radian = i1 < divisions ? fDivisions * i1 * 2.0F * 3.141592653589793D : 0.0D;
            texCoords[(index + 0)] = ((float) (Math.sin(radian) * 0.5D) + 0.5F);
            texCoords[(index + 1)] = ((float) (Math.cos(radian) * 0.5D) + 0.5F);
            index += 2;
        }

        for (int i1 = 0; i1 <= divisions; i1++) {
            radian = i1 < divisions ? fDivisions * i1 * 2.0F * 3.141592653589793D : 0.0D;
            texCoords[(index + 0)] = (0.5F + (float) (Math.sin(radian) * 0.5D));
            texCoords[(index + 1)] = (0.5F - (float) (Math.cos(radian) * 0.5D));
            index += 2;
        }

        texCoords[(index + 0)] = 0.5F;
        texCoords[(index + 1)] = 0.5F;
        index += 2;

        int i1 = 0;
        for (int i2 = 0; i2 < divisions; i2++) {
            int i3 = i2 + 1;
            int i4 = i2 + divisions;
            int i5 = i3 + divisions;

            faces[(i1 + 0)] = i2;
            faces[(i1 + 1)] = i2;
            faces[(i1 + 2)] = i4;
            faces[(i1 + 3)] = (i4 + 1);
            faces[(i1 + 4)] = (i3 == divisions ? 0 : i3);
            faces[(i1 + 5)] = i3;
            i1 += 6;

            faces[(i1 + 0)] = (i5 % divisions == 0 ? i5 - divisions : i5);
            faces[(i1 + 1)] = (i5 + 1);
            faces[(i1 + 2)] = (i3 == divisions ? 0 : i3);
            faces[(i1 + 3)] = i3;
            faces[(i1 + 4)] = i4;
            faces[(i1 + 5)] = (i4 + 1);
            i1 += 6;
        }

        int i2 = (divisions + 1) * 2;
        int i3 = (divisions + 1) * 4;
        int i4 = divisions * 2;
        int i6;
        int i7;
        int i8;
        for (int i5 = 0; i5 < divisions; i5++) {
            i6 = i5 + 1;
            i7 = i2 + i5;
            i8 = i7 + 1;

            faces[(i1 + 0)] = i5;
            faces[(i1 + 1)] = i7;
            faces[(i1 + 2)] = (i6 == divisions ? 0 : i6);
            faces[(i1 + 3)] = i8;
            faces[(i1 + 4)] = i4;
            faces[(i1 + 5)] = i3;
            i1 += 6;
        }

        i4 = divisions * 2 + 1;
        i2 = (divisions + 1) * 3;

        for (int i5 = 0; i5 < divisions; i5++) {
            i6 = i5 + 1 + divisions;
            i7 = i2 + i5;
            i8 = i7 + 1;

            faces[(i1 + 0)] = (i5 + divisions);
            faces[(i1 + 1)] = i7;
            faces[(i1 + 2)] = i4;
            faces[(i1 + 3)] = i3;
            faces[(i1 + 4)] = (i6 % divisions == 0 ? i6 - divisions : i6);
            faces[(i1 + 5)] = i8;
            i1 += 6;
        }
        // create smoothingGroups
        for (int i5 = 0; i5 < divisions * 2; i5++) {
            smoothingGroups[i5] = 1;
        }
        for (int i5 = divisions * 2; i5 < divisions * 4; i5++) {
            smoothingGroups[i5] = 2;
        }

        TriangleMesh localTriangleMesh = new TriangleMesh();
        localTriangleMesh.getPoints().setAll(points);
        localTriangleMesh.getTexCoords().setAll(texCoords);
        localTriangleMesh.getFaces().setAll(faces);
        localTriangleMesh.getFaceSmoothingGroups().setAll(smoothingGroups);

        System.setProperty("prism.dirtyopts", "false");

        return localTriangleMesh;
    }

}


/*  code for sphere from jfxrt.jar
 static TriangleMesh createMesh(int paramInt, float paramFloat)
 {
 paramInt = correctDivisions(paramInt);
    

 int i = paramInt / 2;
    
 int j = paramInt * (i - 1) + 2;
 int k = (paramInt + 1) * (i - 1) + paramInt * 2;
 int m = paramInt * (i - 2) * 2 + paramInt * 2;
    
 float f1 = 1.0F / paramInt;
    
 float[] arrayOfFloat1 = new float[j * 3];
 float[] arrayOfFloat2 = new float[k * 2];
 int[] arrayOfInt = new int[m * 6];
    
 int n = 0;int i1 = 0;
 for (int i2 = 0; i2 < i - 1; i2++)
 {
 f2 = f1 * (i2 + 1 - i / 2) * 2.0F * 3.141593F;
 float f3 = (float)Math.sin(f2);
 float f4 = (float)Math.cos(f2);
      
 float f5 = 0.5F + f3 * 0.5F;
 for (i6 = 0; i6 < paramInt; i6++)
 {
 double d = f1 * i6 * 2.0F * 3.141593F;
 float f6 = (float)Math.sin(d);
 float f7 = (float)Math.cos(d);
 arrayOfFloat1[(n + 0)] = (f6 * f4 * paramFloat);
 arrayOfFloat1[(n + 2)] = (f7 * f4 * paramFloat);
 arrayOfFloat1[(n + 1)] = (f3 * paramFloat);
 arrayOfFloat2[(i1 + 0)] = (1.0F - f1 * i6);
 arrayOfFloat2[(i1 + 1)] = f5;
 n += 3;
 i1 += 2;
 }
 arrayOfFloat2[(i1 + 0)] = 0.0F;
 arrayOfFloat2[(i1 + 1)] = f5;
 i1 += 2;
 }
 arrayOfFloat1[(n + 0)] = 0.0F;
 arrayOfFloat1[(n + 1)] = (-paramFloat);
 arrayOfFloat1[(n + 2)] = 0.0F;
 arrayOfFloat1[(n + 3)] = 0.0F;
 arrayOfFloat1[(n + 4)] = paramFloat;
 arrayOfFloat1[(n + 5)] = 0.0F;
 n += 6;
    
 i2 = (i - 1) * paramInt;
    
 float f2 = 0.0039063F;
 for (int i3 = 0; i3 < paramInt; i3++)
 {
 arrayOfFloat2[(i1 + 0)] = (f1 * (0.5F + i3));
 arrayOfFloat2[(i1 + 1)] = f2;
 i1 += 2;
 }
 for (i3 = 0; i3 < paramInt; i3++)
 {
 arrayOfFloat2[(i1 + 0)] = (f1 * (0.5F + i3));
 arrayOfFloat2[(i1 + 1)] = (1.0F - f2);
 i1 += 2;
 }
 i3 = 0;
 int i8;
 int i9;
 int i10;
 int i11;
 int i12;
 for (int i4 = 0; i4 < i - 2; i4++) {
 for (i5 = 0; i5 < paramInt; i5++)
 {
 i6 = i4 * paramInt + i5;
 i7 = i6 + 1;
 i8 = i6 + paramInt;
 i9 = i7 + paramInt;
        
 i10 = i6 + i4;
 i11 = i10 + 1;
 i12 = i10 + (paramInt + 1);
 int i13 = i11 + (paramInt + 1);
        

 arrayOfInt[(i3 + 0)] = i6;
 arrayOfInt[(i3 + 1)] = i10;
 arrayOfInt[(i3 + 2)] = (i7 % paramInt == 0 ? i7 - paramInt : i7);
 arrayOfInt[(i3 + 3)] = i11;
 arrayOfInt[(i3 + 4)] = i8;
 arrayOfInt[(i3 + 5)] = i12;
 i3 += 6;
        

 arrayOfInt[(i3 + 0)] = (i9 % paramInt == 0 ? i9 - paramInt : i9);
 arrayOfInt[(i3 + 1)] = i13;
 arrayOfInt[(i3 + 2)] = i8;
 arrayOfInt[(i3 + 3)] = i12;
 arrayOfInt[(i3 + 4)] = (i7 % paramInt == 0 ? i7 - paramInt : i7);
 arrayOfInt[(i3 + 5)] = i11;
 i3 += 6;
 }
 }
 i4 = i2;
 int i5 = (i - 1) * (paramInt + 1);
 for (int i6 = 0; i6 < paramInt; i6++)
 {
 i7 = i6;i8 = i6 + 1;i9 = i5 + i6;
 arrayOfInt[(i3 + 0)] = i4;
 arrayOfInt[(i3 + 1)] = i9;
 arrayOfInt[(i3 + 2)] = (i8 == paramInt ? 0 : i8);
 arrayOfInt[(i3 + 3)] = i8;
 arrayOfInt[(i3 + 4)] = i7;
 arrayOfInt[(i3 + 5)] = i7;
 i3 += 6;
 }
 i4 += 1;
 i5 += paramInt;
 i6 = (i - 2) * paramInt;
 for (int i7 = 0; i7 < paramInt; i7++)
 {
 i8 = i6 + i7;i9 = i6 + i7 + 1;i10 = i5 + i7;
 i11 = (i - 2) * (paramInt + 1) + i7;i12 = i11 + 1;
 arrayOfInt[(i3 + 0)] = i4;
 arrayOfInt[(i3 + 1)] = i10;
 arrayOfInt[(i3 + 2)] = i8;
 arrayOfInt[(i3 + 3)] = i11;
 arrayOfInt[(i3 + 4)] = (i9 % paramInt == 0 ? i9 - paramInt : i9);
 arrayOfInt[(i3 + 5)] = i12;
 i3 += 6;
 }
 TriangleMesh localTriangleMesh = new TriangleMesh(true);
 localTriangleMesh.getPoints().setAll(arrayOfFloat1);
 localTriangleMesh.getTexCoords().setAll(arrayOfFloat2);
 localTriangleMesh.getFaces().setAll(arrayOfInt);
 return localTriangleMesh;
 }
 */
