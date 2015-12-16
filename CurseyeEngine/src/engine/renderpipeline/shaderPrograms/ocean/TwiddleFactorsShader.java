package engine.renderpipeline.shaderPrograms.ocean;

import engine.core.ResourceLoader;
import engine.renderpipeline.Shader;

public class TwiddleFactorsShader extends Shader{

	private static TwiddleFactorsShader instance = null;
	
	public static TwiddleFactorsShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TwiddleFactorsShader();
	    }
	      return instance;
	}
	
	protected TwiddleFactorsShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("ocean/fft/TwiddleFactors.glsl"));
		compileShader();
		
		addUniform("image");
		addUniform("N");
	}
	

	public void sendUniforms(int N)
	{
		setUniformi("N", N);
	}
}
