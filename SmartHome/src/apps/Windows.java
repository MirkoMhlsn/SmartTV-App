package apps;

import java.awt.AWTException;
import java.awt.Robot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Windows {
	
	Tab tab;
	StackPane stackPane;
	Button startWindows;
	EventHandler<ActionEvent> startAction;
	EventHandler<KeyEvent> outOfFocus;
	
	public Windows(Tab tab) {
		
		this.tab = tab;
		
		stackPane = new StackPane();
		tab.setContent(stackPane);
		
		startWindows = new Button("start up Windows");
		double buttonHeight = tab.getTabPane().getPrefHeight() * 7.0/32.0;
		double buttonWidth = buttonHeight * 3;
		startWindows.setPrefSize(buttonWidth, buttonHeight);
		startWindows.setId("windowsButton");
		stackPane.getChildren().add(startWindows);
		
		
	}
	
	public void startWindows() {
		
		startWindows.requestFocus();
		
		startAction = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				System.out.println("start Windows befehl");
				
			}
			
		};
		
		startWindows.addEventHandler(ActionEvent.ACTION, startAction);
		
		outOfFocus = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					tab.getTabPane().requestFocus();
					try {
						Robot robot = new Robot();
						robot.keyPress(java.awt.event.KeyEvent.VK_UP);
					} catch (AWTException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		};
		
		startWindows.addEventHandler(KeyEvent.KEY_PRESSED, outOfFocus);
		
	}
	
	public void stopWindows() {
		
		startWindows.removeEventHandler(ActionEvent.ACTION, startAction);
		startWindows.removeEventHandler(KeyEvent.KEY_PRESSED, outOfFocus);
		
	}

}
