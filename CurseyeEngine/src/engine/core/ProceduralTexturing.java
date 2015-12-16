package engine.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import engine.math.Vec2f;
import engine.models.data.Mesh;

public class ProceduralTexturing{

		public static void sphere(Mesh mesh)
		{	
			for(int i=0; i<mesh.getVertices().length; i++)
			{
				if (mesh.getVertices()[i].getTextureCoord().getX() != 0.001f)
				mesh.getVertices()[i].getTextureCoord().setX((float) (0.5 + (Math.atan2(mesh.getVertices()[i].getPos().getZ(), mesh.getVertices()[i].getPos().getX()))/(2*Math.PI)));
				mesh.getVertices()[i].getTextureCoord().setY((float) (0.5 - (Math.asin(mesh.getVertices()[i].getPos().getY())/Math.PI)));
			}
		}
		
		public static void dome(Mesh mesh)
		{
			for(int i=0; i<mesh.getVertices().length; i++)
			{
				mesh.getVertices()[i].setTextureCoord(new Vec2f((mesh.getVertices()[i].getPos().getX()+1)*0.5f, (mesh.getVertices()[i].getPos().getZ()+1)*0.5f));
			}
		}
		
		public static void noiseTexture()
		{
			int resolution = 256;
			
			BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster raster = image.getRaster();
			File output = new File("./" + "Noise" + ".jpg");
			
			Random rnd = new Random();
			
			
			for(int i= 0; i<resolution; i++)
			{
				for(int j=0; j<resolution; j++)
				{	
					raster.setSample(j, i, 0, (byte) rnd.nextInt(255));
				}
			}
			try {
				ImageIO.write(image, "jpg", output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public static void mapCoordsToTextureFormat(Vertex[] vertices, Texture texture){
			
			float div = texture.getHeight()/texture.getWidth();
			
			for(Vertex vertex : vertices){
				vertex.getTextureCoord().mul(div);
			}
		}
}
