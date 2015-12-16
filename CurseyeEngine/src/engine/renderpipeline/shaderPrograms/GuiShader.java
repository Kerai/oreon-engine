package engine.renderpipeline.shaderPrograms;

import engine.core.ResourceLoader;
import engine.math.Matrix4f;
import engine.renderpipeline.Shader;

public class GuiShader extends Shader{

private static GuiShader instance = null;
	
	public static GuiShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GuiShader();
	    }
	      return instance;
	}
	
	protected GuiShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("gui/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("gui/Fragment.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("texture");
	}
	
	public void sendUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void sendUniforms(int texture)
	{
		setUniformi("texture", texture);
	}
}
