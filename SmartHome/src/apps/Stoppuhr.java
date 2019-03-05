package apps;

import java.util.ArrayList;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Stoppuhr {
	
	HBox hBox;
	VBox vBox;
	VBox spacerV;
	Region spacerH;
	Region spacerVbox;
	StackPane buttonSpacer;
	TilePane tilePane;
	Tab tab;
	Button startStop;
	Button numberPad[];
	Button cancel;
	Button delete;
	Label zeit;
	
	EventHandler<KeyEvent> keyInput;
	EventHandler<ActionEvent> startAction;
	EventHandler<ActionEvent> deleteInput;
	EventHandler<ActionEvent> pauseAction;
	EventHandler<ActionEvent> cancelAction;
	EventHandler<ActionEvent> cancelInput;
	EventHandler<KeyEvent> getTabToFocus;
	ArrayList<EventHandler<ActionEvent>> numberPadAction = new ArrayList<EventHandler<ActionEvent>>();
	
	Image backspace = new Image("file:src/icons/back_space_50x50.png");
	Image cross = new Image("file:src/icons/cancel_50x50.png");
	Image play = new Image("file:src/icons/play_timer_2.png");
	Image pause = new Image("file:src/icons/pause_timer.png");
	ImageView buttonImages[] = {new ImageView(play), new ImageView(pause)};
	String buttonMessages[] = {"Start", "Stop"};
	int buttonImageIndex = 0;
	
	int setTimer[] = new int[5];
	int time;
	
	public Stoppuhr(Tab tab) {
		
		this.tab = tab;
		initTimer();
		
	}
	
	private void initTimer() {
		
		hBox = new HBox();
		vBox = new VBox();
		tilePane = new TilePane();
		tilePane.setPrefColumns(3);
		tilePane.setPrefRows(4);
		
		double tabPaneHeight = tab.getTabPane().getPrefHeight();
		double gap = tabPaneHeight * 1.0/80.0;
		double inset = tabPaneHeight * 1.0/16.0;
		double numberPadDimension = tabPaneHeight * 7.0/32.0;
		
		tilePane.setHgap(gap);
		tilePane.setVgap(gap);
		
		spacerV = new VBox();
		spacerH = new Region();
		
		spacerV.getChildren().add(hBox);
		spacerV.setAlignment(Pos.CENTER);
		tab.setContent(spacerV);
		
		initButtons(numberPadDimension);
		
		spacerH.setMaxWidth(numberPadDimension * 4.5);
		hBox.getChildren().addAll(tilePane, spacerH, vBox);
		hBox.setPadding(new Insets(0, inset*4, 0, inset));
		HBox.setHgrow(spacerH, Priority.ALWAYS);
		
		spacerVbox = new Region();
		VBox.setVgrow(spacerVbox, Priority.ALWAYS);
		
		buttonSpacer = new StackPane();
		buttonSpacer.getChildren().add(startStop);
		vBox.getChildren().addAll(zeit, spacerVbox, buttonSpacer);
		
	}
	
	private void initButtons(double numberPadDimension) {
		
		startStop = new Button();
		startStop.setPrefHeight(numberPadDimension);
		startStop.setPrefWidth(numberPadDimension * 3.0);
		zeit = new Label("0:00:00");
		zeit.setId("zeit");
		
		startStop.setGraphic(buttonImages[0]);
		startStop.setText(buttonMessages[0]);
		startStop.setId("timerStartStop");
		startStop.setDisable(true);
		
		cancel = new Button();
		cancel.setPrefSize(numberPadDimension, numberPadDimension);
		cancel.setId("numberPadButton");
		cancel.setGraphic(new ImageView(cross));
		delete = new Button();
		delete.setPrefSize(numberPadDimension, numberPadDimension);
		delete.setId("numberPadButton");
		delete.setGraphic(new ImageView(backspace));
		
		numberPad = new Button[10];
		
		for(int i = 0; i < numberPad.length; i++) {
			
			numberPad[i] = new Button();
			numberPad[i].setText(((i+1)%10) + "");
			numberPad[i].setPrefSize(numberPadDimension, numberPadDimension);
			numberPad[i].setId("numberPadButton");
			
			tilePane.getChildren().add(numberPad[i]);
			
		}
		
		tilePane.getChildren().add(cancel);
		tilePane.getChildren().add(delete);
		
	}
	
	private void startTimer() {
		
		delete.setDisable(true);
		stopTimer();
		
		ScheduledService<Integer> service = new ScheduledService<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				
				return new Task<Integer>() {

					@Override
					protected Integer call() throws Exception {
						return time--;
					}
					
				};
			
			}
			
		};
		
		service.setPeriod(Duration.seconds(1.0));
		service.start();
		
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				
				String formattedTime = String.format("%d:%02d:%02d", service.getValue()/3600, (service.getValue()%3600)/60, (service.getValue()%60));
				zeit.setText(formattedTime);
				
				if(service.getValue() == 0) {
					service.cancel();
					startStop.setDisable(true);
					startStop.setGraphic(buttonImages[0]);
					startStop.setText(buttonMessages[0]);
					startStop.removeEventHandler(ActionEvent.ACTION, pauseAction);
					cancel.removeEventHandler(ActionEvent.ACTION, cancelAction);
					System.out.println("success action set up");
					setUpTimer();
				}
				
			}
			
		});
		
		cancelAction = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				service.cancel();
				clearArray();
				startStop.setDisable(true);
				startStop.setGraphic(buttonImages[0]);
				startStop.setText(buttonMessages[0]);
				zeit.setText("0:00:00");
				cancel.removeEventHandler(ActionEvent.ACTION, this);
				startStop.removeEventHandler(ActionEvent.ACTION, pauseAction);
				System.out.println("cancel action set up");
				setUpTimer();
				
			}
			
		};
		
		cancel.addEventHandler(ActionEvent.ACTION, cancelAction);
		
		pauseAction = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				service.cancel();
				startStop.setGraphic(buttonImages[0]);
				startStop.setText(buttonMessages[0]);
				startStop.removeEventHandler(ActionEvent.ACTION, this);
				cancel.removeEventHandler(ActionEvent.ACTION, cancelAction);
				System.out.println("pause action set up");
				setUpTimer();
				
			}
			
		};
		
		startStop.addEventHandler(ActionEvent.ACTION, pauseAction);
		
	}
	
	public void setUpTimer() {
		
		delete.setDisable(false);
		
		keyInput = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(event.getText().matches("[0-9]")) {
					
					setTime((int)event.getText().charAt(0) - 48);
					
				}
				else if(event.getCode() == KeyCode.BACK_SPACE) {
					
					delTime();
					
				}
				else if(event.getCode() == KeyCode.UP) {
					
					time = 0;
					startStop.setDisable(true);
					zeit.setText("0:00:00");
					stopTimer();
					
				}
				
				zeit.setText(setTimer[4] + ":" + setTimer[3] + setTimer[2] + ":" + setTimer[1] + setTimer[0]);
				time = setTimer[4] * 3600 + (setTimer[3] * 10 + setTimer[2]) * 60 + setTimer[1] * 10 + setTimer[0];
				startStop.setDisable(false);
				
				if(time == 0) {
					
					startStop.setDisable(true);
					
				}
				
			}
			
		};
		
		tab.getTabPane().addEventHandler(KeyEvent.KEY_PRESSED, keyInput);
		
		for(int i = 0; i < numberPad.length; i++) {
			
			int index = i;
			
			numberPadAction.add(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					setTime((index + 1)%10);
					zeit.setText(setTimer[4] + ":" + setTimer[3] + setTimer[2] + ":" + setTimer[1] + setTimer[0]);
					time = setTimer[4] * 3600 + (setTimer[3] * 10 + setTimer[2]) * 60 + setTimer[1] * 10 + setTimer[0];
					startStop.setDisable(false);
					
					if(time == 0) {
						
						startStop.setDisable(true);
						
					}
					
				}
				
			});
			
			numberPad[i].addEventHandler(ActionEvent.ACTION, numberPadAction.get(i));
			numberPad[i].setDisable(false);
			
		}
		
		getTabToFocus = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					tab.getTabPane().requestFocus();
					
				}
				
			}
			
		};
		
		for(int i = 0; i < tilePane.getPrefColumns(); i++) {
			
			numberPad[i].addEventHandler(KeyEvent.KEY_PRESSED, getTabToFocus);
			
		}
		
		startAction = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				startTimer();
				startStop.setGraphic(buttonImages[1]);
				startStop.setText(buttonMessages[1]);
				
			}
			
		};
		
		startStop.addEventHandler(ActionEvent.ACTION, startAction);
		
		deleteInput = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				delTime();
				zeit.setText(setTimer[4] + ":" + setTimer[3] + setTimer[2] + ":" + setTimer[1] + setTimer[0]);
				time = setTimer[4] * 3600 + (setTimer[3] * 10 + setTimer[2]) * 60 + setTimer[1] * 10 + setTimer[0];
				startStop.setDisable(false);
				
				if(time == 0) {
					
					startStop.setDisable(true);
					
				}
				
			}
			
		};
		
		delete.addEventHandler(ActionEvent.ACTION, deleteInput);
		
		cancelInput = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				clearArray();
				zeit.setText("0:00:00");
				time = 0;
				startStop.setDisable(true);
				
			}
			
		};
		
		cancel.addEventHandler(ActionEvent.ACTION, cancelInput);
		numberPad[0].requestFocus();
		
	}
	
	private void setTime(int value) {
		
		for(int i = setTimer.length - 1; i >= 1; i--) {
			
			setTimer[i] = setTimer[i - 1];
			
			if(setTimer[1] > 5) {
				setTimer[1] = setTimer[1] - 6;
				setTimer[2]++;
			}
			if(setTimer[3] > 5) {
				setTimer[3] = setTimer[3] - 6;
				setTimer[4]++;
			}
			
		}
		setTimer[0] = value;
		
	}
	
	private void delTime() {
		
		for(int i = 0; i < setTimer.length - 1; i++) {
			
			setTimer[i] = setTimer[i + 1];
			
		}
		setTimer[setTimer.length - 1] = 0;
		
	}
	
	public void stopTimer() {
		
		clearArray();
		
		tab.getTabPane().removeEventHandler(KeyEvent.KEY_PRESSED, keyInput);
		startStop.removeEventHandler(ActionEvent.ACTION, startAction);
		delete.removeEventHandler(ActionEvent.ACTION, deleteInput);
		cancel.removeEventHandler(ActionEvent.ACTION, cancelInput);
		
		for(int i = 0; i < tilePane.getPrefColumns(); i++) {
			
			numberPad[i].removeEventHandler(KeyEvent.KEY_PRESSED, getTabToFocus);
			
		}
		
		for(int i = 0; i < numberPadAction.size(); i++) {
			
			numberPad[i].setDisable(true);
			numberPad[i].removeEventHandler(ActionEvent.ACTION, numberPadAction.get(i));
			
		}
		
		numberPadAction.clear();	
		
	}
	
	public void clearArray() {
		
		for(int i = 0; i < setTimer.length; i++) {
			setTimer[i] = 0;
		}
		
	}
	
}
