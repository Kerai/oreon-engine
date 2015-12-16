package engine.gameObject.components;

import engine.core.Camera;
import engine.models.data.Material;
import engine.models.data.Particle;
import engine.renderpipeline.RenderingConfig;
import engine.renderpipeline.Shader;
import engine.renderpipeline.data.ParticleSystemVAO;
import engine.renderpipeline.shaderPrograms.particles.ParticleShader;

public class ParticleRenderer extends Renderer{

	private ParticleSystemVAO vao;
	private int delete;
	private int countdown;
	private long timeMillis;
	
	// TODO full rework
	
	public ParticleRenderer(Shader shader, RenderingConfig config, Material material)
	{
		super(config, shader);
		this.vao = new ParticleSystemVAO();
	}
	
	public void init(Particle[] particles)
	{
		vao.init(particles);
	}

	public void render()
	{
		getShader().execute();
		getShader().sendUniforms(getTransform().getWorldMatrix(), Camera.getInstance().getViewProjectionMatrix(), getTransform().getModelViewProjectionMatrix());
		getShader().sendUniforms(((Model) getParent().getComponents().get("Model")).getMaterial());
		getConfig().enable();
		vao.draw();
		getConfig().disable();
	}
	
	public void update()
	{
		ParticleShader.getInstance().execute();
		
		if (delete == 1 && (System.currentTimeMillis() - timeMillis) > countdown)
		{
			vao.shutdown();
			this.getParent().getComponents().remove("Renderer");
		}
		else
		{
			ParticleShader.getInstance().sendUniforms(this.delete);
			vao.updateParticles();
		}
	}
	

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}
}
