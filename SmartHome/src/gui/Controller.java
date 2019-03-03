package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import apps.Stoppuhr;
import apps.Windows;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import worker.Worker;

public class Controller implements Initializable {
	
	@FXML
	AnchorPane anchorPane;
	
	@FXML
	ScrollPane scrollContent;
	
	@FXML
	VBox vBox;
	
	@FXML
	HBox metadaten;
	
	@FXML
	ScrollPane scrollPane;
	
	@FXML
	TabPane tabPane;
	
	@FXML
	Pane paneFill;
	
	@FXML
	HBox hBox;
	
	Label appName;
	Region spacerL;
	Region spacerR;
	ArrayList<Button> kacheln = new ArrayList<Button>();
	Worker arbeiter = new Worker();
	Tab tab[] = new Tab[arbeiter.kachelAnzahl];
	int currentTab = 0;
	double prevKacheln[] = {0, 0};
	double dif = 0;
	double token = 1;
	private Clip clip;
	int soundLoop = 0;
	
	Stoppuhr timer;
	Windows windows;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		initAppLayout();
		startAppLayout();
		
	}
	
	private void initAppLayout() {
		
		initScrollBar();
		initMetaPane();
		initTabPane();
		
		try {
			initSound();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println("SoundFile error");
			e.printStackTrace();
		}
		
	}
	
	public void initScrollBar() {
		
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		scrollContent.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollContent.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		scrollContent.prefHeightProperty().bind(anchorPane.heightProperty());
		scrollContent.prefWidthProperty().bind(anchorPane.widthProperty());
		
		kacheln = arbeiter.kachelGen(arbeiter.kachelAnzahl);
		ObservableList<Button> apps = FXCollections.observableList(kacheln);
		
		vBox.prefWidthProperty().bind(scrollContent.widthProperty());

		double inset[] = getScreenDependentInsets();
		vBox.setPadding(new Insets(inset[1], inset[0], inset[1], inset[0]));
		
		scrollPane.prefHeightProperty().bind(vBox.heightProperty());
		scrollPane.prefWidthProperty().bind(vBox.widthProperty());
		scrollPane.setMaxHeight(arbeiter.kachelDimension + 40);
		
		hBox.prefHeightProperty().bind(scrollPane.heightProperty());
		hBox.setPrefWidth((arbeiter.kachelDimension + 20) * arbeiter.kachelAnzahl);
		hBox.getChildren().addAll(apps);
		hBox.setPadding(new Insets(arbeiter.hBoxInset, arbeiter.hBoxInset / 2.0, arbeiter.hBoxInset, arbeiter.hBoxInset / 2.0));
		hBox.setSpacing(arbeiter.hBoxInset);
		
	}
	
	private void initMetaPane(){
		
		metadaten.prefWidthProperty().bind(vBox.widthProperty());
		metadaten.setPrefHeight(1.0/8.0 * arbeiter.screenResolution[0]);
		
		appName = new Label();
		spacerL = new Region();
		spacerR = new Region();
		HBox.setHgrow(spacerL, Priority.ALWAYS);
		HBox.setHgrow(spacerR, Priority.ALWAYS);
		metadaten.getChildren().addAll(spacerL, appName, spacerR);
		
		paneFill.prefWidthProperty().bind(vBox.widthProperty());
		paneFill.setPrefHeight(1.0/6.0 * arbeiter.screenResolution[0]);
		
	}
	
	public void initTabPane() {
		
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.setPrefHeight(arbeiter.screenResolution[0] * 4.0/9.0);
		tabPane.prefWidthProperty().bind(vBox.widthProperty());
		
		for(int i = 0; i < tab.length; i++) {
			
			tab[i] = new Tab();
			tabPane.getTabs().add(tab[i]);
			tab[i].setText("Kachel " + i);
			tab[i].setDisable(true);
			initTabContent(i);
			
		}
		
	}
	
	private void initSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		File path = new File("src\\worker\\plop.wav");
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(path);
		clip = AudioSystem.getClip();
		clip.open(inputStream);
		
	}
	
	private double[] getScreenDependentInsets() {
		
		double returnValues[] = new double[2];
		returnValues[0] = (9.0 / 256.0 * arbeiter.screenResolution[1]);
		returnValues[1] = (1.0 / 12.0 * arbeiter.screenResolution[0]);
		
		return returnValues;
		
	}
	
	private void startAppLayout() {
		
		for(int i = 0; i < arbeiter.kachelAnzahl; i++) {
			
			int tabIndex = i;
			
			kacheln.get(i).setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					
					kacheln.get(tabIndex).requestFocus();
					
				}
				
			});
			
			kacheln.get(i).focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					
					if(newValue) {
						clip.loop(soundLoop);
						soundLoop = 1;
						tabPane.getSelectionModel().select(tabIndex);
						appName.setText(kacheln.get(tabIndex).getId());
						tab[currentTab].setDisable(true);
						autoScroll(tabIndex);
					}
					
				}
				
			});
			
			kacheln.get(i).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					tab[tabIndex].setDisable(false);
					currentTab = tabIndex;
					scrollPane.setDisable(true);
					shrink();
					startTabPane(tabIndex);
					
				}
				
			});
			
			kacheln.get(i).setOnKeyPressed(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					
					if(event.getCode() == KeyCode.DOWN) {
						
						tab[tabIndex].setDisable(false);
						currentTab = tabIndex;
						scrollPane.setDisable(true);
						shrink();
						startTabPane(tabIndex);
						
					}
					
				}
				
			});
			
		}
		
	}
	
	private void autoScroll(double index) {
		
		if((int)index !=(int)prevKacheln[1]) {
			dif = (index - prevKacheln[0])/2.0;
			prevKacheln[0] = prevKacheln[1];
			prevKacheln[1] = index;
			index = index - 3 - dif;
			dif = 0;
			smoothScroll(index * 1.0/(arbeiter.kachelAnzahl - arbeiter.kachelSichtbar));
		}
		
	}
	
	private void startTabPane(int index) {
		
		tabPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
			
			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					closeTabs(index);
					soundLoop = 0;
					tab[index].setDisable(true);
					scrollPane.setDisable(false);
					grow();
					kacheln.get(index).requestFocus();
					
				}
				
			}
			
		});
		
		if(index == 7) {
			
			windows.startWindows();
			
		}
		else if(index == 9) {
			
			timer.setUpTimer();
			
		}
		
	}
	
	private void closeTabs(int index) {
		
		if(index == 7) {
			
			windows.stopWindows();
			
		}
		else if(index == 9) {
			
			timer.stopTimer();
			
		}
		
	}
	
	private void smoothScroll(double keyValue){
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(scrollPane.hvalueProperty(), keyValue);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void shrink() {
		
//		paneFill.setPrefHeight(0.0);
//		tabPane.setPrefHeight(arbeiter.screenResolution[0] * 1.0/18.0);
//		scrollContent.setVvalue(1.0);
		smoothShrinkPaneFill();
		smoothShrinkScrollContent();
		smoothShrinkTabPane();
		
	}
	
	private void grow() {
		
//		paneFill.setPrefHeight(arbeiter.screenResolution[0] * 1.0/6.0);
//		tabPane.setPrefHeight(arbeiter.screenResolution[0] * 4.0/9.0);
//		scrollContent.setHvalue(0.0);
		smoothGrowPaneFill();
		smoothGrowScrollContent();
		smoothGrowTabPane();
		
	}
	
	private void smoothShrinkTabPane() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(tabPane.prefHeightProperty(), arbeiter.screenResolution[0] * 11.0/18.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void smoothGrowTabPane() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(tabPane.prefHeightProperty(), arbeiter.screenResolution[0] * 4.0/9.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void smoothShrinkScrollContent() {

		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(scrollContent.vvalueProperty(), 1.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();

	}
	
	private void smoothGrowScrollContent() {

		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(scrollContent.vvalueProperty(), 0.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();

	}
	
	private void smoothShrinkPaneFill() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(paneFill.prefHeightProperty(), 0.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void smoothGrowPaneFill() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(paneFill.prefHeightProperty(), arbeiter.screenResolution[0] * 1.0/6.0);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void initTabContent(int index) {
		
		if(index == 7) {
			windows = new Windows(tab[index]);
		}
		else if(index == 9) {
			timer = new Stoppuhr(tab[index]);
		}
		
	}

}
