package simulations.objLoader;
import org.lwjgl.input.Keyboard;

import engine.core.Input;
import engine.gameObject.GameObject;
import engine.gameObject.components.MeshRenderer;
import engine.gameObject.components.Model;
import engine.gameObject.components.Renderer;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.obj.OBJLoader;
import engine.renderer.glass.GlassRenderer;
import engine.renderpipeline.configs.AlphaBlending;
import engine.renderpipeline.configs.CullFaceDisable;
import engine.renderpipeline.data.MeshVAO;

public class OBJ extends GameObject{

	public OBJ(){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(20f,20f,20f);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("nanosuit");
		int size = 0;
		for (Model model : models){
			size += model.getMesh().getVertices().length;
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			//Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
			meshBuffer.addData(model.getMesh());
			MeshRenderer renderer = null;
			if(model.getMaterial() == null){
				Material material = new Material();
				material.setColor(new Vec3f(0.2f,0.2f,0.2f));
				material.setName("zero");
				model.setMaterial(material);
			}

			if (model.getMaterial().getName().equals("glass"))
				renderer = new MeshRenderer(meshBuffer, engine.renderpipeline.shaderPrograms.materials.Glass.getInstance(), new AlphaBlending(0));
			else if (model.getMaterial().getNormalmap() != null)
				renderer = new MeshRenderer(meshBuffer, engine.renderpipeline.shaderPrograms.lighting.phong.Bumpy.getInstance(), new CullFaceDisable());
			else if (model.getMaterial().getDiffusemap() != null)
				renderer = new MeshRenderer(meshBuffer, engine.renderpipeline.shaderPrograms.lighting.phong.Textured.getInstance(), new CullFaceDisable());	
			else
				renderer = new MeshRenderer(meshBuffer, engine.renderpipeline.shaderPrograms.lighting.phong.RGBA.getInstance(), new CullFaceDisable());	

			object.addComponent("Model", model);
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		System.out.println((size * 32.0f)/1000000f + " mb");
	}
	
	public void update(){
		super.update();
		
		if (Input.getHoldingKeys().contains(Keyboard.KEY_G))
		{
			for(GameObject gameobject : this.getChildren()){
				((Renderer) gameobject.getComponent("Renderer")).setShader(engine.renderpipeline.shaderPrograms.basic.Grid.getInstance());
			}
		}
		else {
			for(GameObject gameobject : this.getChildren()){
				if (((Model) gameobject.getComponent("Model")).getMaterial().getName().equals("glass"))
					((Renderer) gameobject.getComponent("Renderer")).setShader(engine.renderpipeline.shaderPrograms.materials.Glass.getInstance());	
				else if (((Model) gameobject.getComponent("Model")).getMaterial().getNormalmap() != null)
					((Renderer) gameobject.getComponent("Renderer")).setShader(engine.renderpipeline.shaderPrograms.lighting.phong.Bumpy.getInstance());
				else if (((Model) gameobject.getComponent("Model")).getMaterial().getDiffusemap() != null)
					((Renderer) gameobject.getComponent("Renderer")).setShader(engine.renderpipeline.shaderPrograms.lighting.phong.Textured.getInstance());
				else
					((Renderer) gameobject.getComponent("Renderer")).setShader(engine.renderpipeline.shaderPrograms.lighting.phong.RGBA.getInstance());			
			}
		}
		
		for(GameObject child: getChildren()){
			if (((Model) child.getComponent("Model")).getMaterial().getName().equals("glass")){
				GlassRenderer.getInstance().addChild(child);
			}
		}
		
	}
	
	public void render(){
		for(GameObject child: getChildren()){
			if (!((Model) child.getComponent("Model")).getMaterial().getName().equals("glass"))
				child.render();
		}
	}
	
}