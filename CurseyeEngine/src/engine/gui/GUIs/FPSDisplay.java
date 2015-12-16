package engine.gui.GUIs;

import engine.gui.GUI;
import engine.gui.GUIElement;
import engine.gui.Screen;
import engine.gui.elements.FPSPanel;

public class FPSDisplay extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[0].init();
		getScreens().add(screen0);
	}
}
