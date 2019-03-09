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

import apps.AllOtherApps;
import apps.Stoppuhr;
import apps.Windows;
import apps.YouTube;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import worker.Worker;

public class Controller implements Initializable {
	
	@FXML
	ScrollPane scrollContent;
	
	@FXML
	VBox vContentBox;
	
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
	
	Worker arbeiter = new Worker();
	ArrayList<Button> kacheln = new ArrayList<Button>();
	Tab tab[] = new Tab[arbeiter.kachelAnzahl];
	
	int currentTab = 0;
	double prevKacheln[] = {0, 0};
	double dif = 0;
	double token = 1;
	private Clip clip;
	int soundLoop = 0;
	int focusCounter = 0;
	
	double smallTabPaneHeight;
	double bigTabPaneHeight;
	
	double smallPaneFillHeight;
	double bigPaneFillHeight;
	
	double scrollPaneHeight;
	
	double hBoxWidth;
	double hBoxPadding;
	
	double metadatenHeight;
	
	YouTube youTube;
	Stoppuhr timer;
	Windows windows;
	AllOtherApps allOtherApps;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		initAppLayout();
		startAppLayout();
		
	}
	
	private void initAppLayout() {
		
		initDimensionValues();
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
	
	private void initDimensionValues() {
		
		bigTabPaneHeight = 11.0/18.0 * arbeiter.getScreenHeight();
		smallTabPaneHeight = 4.0/9.0 * arbeiter.getScreenHeight();
		
		bigPaneFillHeight = 1.0/6.0 * arbeiter.getScreenHeight();
		smallPaneFillHeight = 0;
		
		scrollPaneHeight = arbeiter.kachelDimension*4.0/15.0 + arbeiter.kachelDimension;
		
		hBoxWidth = (arbeiter.kachelDimension*2.0/15.0 + arbeiter.kachelDimension) * arbeiter.kachelAnzahl;
		
		metadatenHeight = 1.0/8.0 * arbeiter.getScreenHeight();
		
	}
	
	private void initScrollBar() {
		
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setId("scrollPaneApps");
		
		scrollContent.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollContent.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollContent.setId("scrollPaneApps");
		
		scrollContent.setPrefHeight(arbeiter.getScreenHeight());
		scrollContent.setPrefWidth(arbeiter.getScreenWidth());
		
		kacheln = arbeiter.kachelGen(arbeiter.kachelAnzahl);
		ObservableList<Button> apps = FXCollections.observableList(kacheln);
		
		vContentBox.prefWidthProperty().bind(scrollContent.widthProperty());

		double inset[] = arbeiter.getVContentBoxInsets();
		vContentBox.setPadding(new Insets(inset[1], inset[0], inset[1], inset[0]));
		
		scrollPane.prefHeightProperty().bind(vContentBox.heightProperty());
		scrollPane.prefWidthProperty().bind(vContentBox.widthProperty());
		scrollPane.setMaxHeight(scrollPaneHeight);
		
		hBox.prefHeightProperty().bind(scrollPane.heightProperty());
		hBox.setPrefWidth(hBoxWidth);
		hBox.getChildren().addAll(apps);
		hBox.setPadding(new Insets(arbeiter.hBoxInset, arbeiter.hBoxInset / 2.0, arbeiter.hBoxInset, arbeiter.hBoxInset / 2.0));
		hBox.setSpacing(arbeiter.hBoxInset);
		hBox.setId("appContainer");
		
	}
	
	private void initMetaPane(){
		
		metadaten.prefWidthProperty().bind(vContentBox.widthProperty());
		metadaten.setPrefHeight(metadatenHeight);
		
		appName = new Label();
		appName.setId("appName");
		
		spacerL = new Region();
		spacerR = new Region();
		HBox.setHgrow(spacerL, Priority.ALWAYS);
		HBox.setHgrow(spacerR, Priority.ALWAYS);
		metadaten.getChildren().addAll(spacerL, appName, spacerR);
		
		paneFill.prefWidthProperty().bind(vContentBox.widthProperty());
		paneFill.setPrefHeight(bigPaneFillHeight);
		
	}
	
	public void initTabPane() {
		
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.setPrefHeight(smallTabPaneHeight);
		tabPane.prefWidthProperty().bind(vContentBox.widthProperty());
		
		for(int i = 0; i < tab.length; i++) {
			
			tab[i] = new Tab();
			tabPane.getTabs().add(tab[i]);
			tab[i].setText("Kachel " + i);
			tab[i].setDisable(true);
			initTabContent(i);
			
		}
		
		tabPane.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				if(newValue && focusCounter == 1) {
					
					focusCounter = 0;
					
					closeTabs(currentTab);
					soundLoop = 0;
					tab[currentTab].setDisable(true);
					scrollPane.setDisable(false);
					grow();
					kacheln.get(currentTab).requestFocus();
					
				}
				else if(newValue) {
					
					focusCounter++;
					
				}
				
			}
			
		});
		
	}
	
	private void initSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		File path = new File("src\\worker\\plop.wav");
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(path);
		clip = AudioSystem.getClip();
		clip.open(inputStream);
		
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
//			scrollPane.setHvalue(index * 1.0/(arbeiter.kachelAnzahl - arbeiter.kachelSichtbar));
			smoothScroll(index * 1.0/(arbeiter.kachelAnzahl - arbeiter.kachelSichtbar));
		}
		
	}
	
	private void startTabPane(int index) {
		
		if(index == 0) {
			
			youTube.startYouTube();
			
		}
		else if(index == 7) {
			
			windows.startWindows();
			
		}
		else if(index == 9) {
			
			timer.setUpTimer();
			
		}
		else{
			
			allOtherApps.start(index);
			
		}
		
		//switch (index): case(0) -> start YT etc...
		
	}
	
	private void closeTabs(int index) {
		
		if(index == 0) {
			
			youTube.stopYouTube();
			
		}
		else if(index == 7) {
			
			windows.stopWindows();
			
		}
		else if(index == 9) {
			
			timer.stopTimer();
			
		}
		else{
			
			allOtherApps.close(index);
			
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
		KeyValue keyvalue = new KeyValue(tabPane.prefHeightProperty(), bigTabPaneHeight);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void smoothGrowTabPane() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(tabPane.prefHeightProperty(), smallTabPaneHeight);
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
		KeyValue keyvalue = new KeyValue(paneFill.prefHeightProperty(), smallPaneFillHeight);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void smoothGrowPaneFill() {
		
		Timeline timeline = new Timeline();
		KeyValue keyvalue = new KeyValue(paneFill.prefHeightProperty(), bigPaneFillHeight);
		KeyFrame keyframe = new KeyFrame(new Duration(200), keyvalue);
		timeline.getKeyFrames().add(keyframe);
		timeline.play();
		
	}
	
	private void initTabContent(int index) {
		
		if(index == 0) {
			
			youTube = new YouTube(tab[index], bigTabPaneHeight);
			
		}
		else if(index == 7) {
			
			windows = new Windows(tab[index]);
			
		}
		else if(index == 9) {
			
			timer = new Stoppuhr(tab[index]);
			
		}
		else{
			
			allOtherApps = new AllOtherApps(index, tab[index]);
			
		}
		
	}

}
