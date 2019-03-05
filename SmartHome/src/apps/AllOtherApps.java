package apps;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class AllOtherApps {
	
	static Button otherAppsButtons[] = new Button[11];
	static Tab tab[] = new Tab[11];
	StackPane stackPane[] = new StackPane[11];
	EventHandler<KeyEvent> tabPaneToFocus;
	
	public AllOtherApps(int appIndex, Tab tab) {
		
		AllOtherApps.tab[appIndex] = tab;
		otherAppsButtons[appIndex] = new Button("Button number: " + appIndex);
		otherAppsButtons[appIndex].setId("otherAppsButtons");
		
		stackPane[appIndex] = new StackPane();
		AllOtherApps.tab[appIndex].setContent(stackPane[appIndex]);
		stackPane[appIndex].getChildren().add(otherAppsButtons[appIndex]);
		
	}
	
	public void start(int index) {
		
		otherAppsButtons[index].requestFocus();
		
		tabPaneToFocus = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					tab[index].getTabPane().requestFocus();
					
				}
				
			}
			
		};
		
		otherAppsButtons[index].addEventHandler(KeyEvent.KEY_PRESSED, tabPaneToFocus);
		
	}
	
	public void close(int index) {
		
		otherAppsButtons[index].removeEventHandler(KeyEvent.KEY_PRESSED, tabPaneToFocus);
		
	}

}
