package apps;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class YouTube {
	
	Tab tab;
	ScrollPane scrollVertical;
	Button metaButtons[] = new Button[3];
	HBox hMetaBox;
	Region metaSpacer;
	VBox vBox;
	ScrollPane scrollHorizontal[] = new ScrollPane[2];
	VBox vVideoBox[] = new VBox[2];
	HBox hPictureBox[] = new HBox[2];
	HBox hTitleBox[] = new HBox[2];
	Button videos[][] = new Button[2][7];
	Label title[][] = new Label[2][7];
	
	Label recommended;
	
	double pictureHeight;
	double titleHeight;
	double videosWidth;
	double hPictureBoxInset;
	double vBoxInset;
	double metaButtonHeight;
	double metaButtonWidth;
	
	EventHandler<KeyEvent> returnToApps;
	
	public YouTube(Tab tab, double bigTabHeight) {
		
		this.tab = tab;
		
		hPictureBoxInset = 1.0/80.0 * tab.getTabPane().getPrefHeight();
		vBoxInset = 1.0/32.0 * tab.getTabPane().getPrefHeight();
		pictureHeight = 27.0/80.0 * tab.getTabPane().getPrefHeight();
		
		titleHeight = 3.0/16.0 * tab.getTabPane().getPrefHeight();
		videosWidth = 3.0/5.0 * tab.getTabPane().getPrefHeight();
		metaButtonHeight = 5.0/32.0 * tab.getTabPane().getPrefHeight();
		
		scrollVertical = new ScrollPane();
		scrollVertical.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollVertical.setVbarPolicy(ScrollBarPolicy.NEVER);
		tab.setContent(scrollVertical);
		
		vBox = new VBox();
		vBox.prefWidthProperty().bind(tab.getTabPane().widthProperty());
		vBox.setPrefHeight(bigTabHeight);
		vBox.setStyle("-fx-background-color: #24262e");
		scrollVertical.setContent(vBox);
		
		hMetaBox = new HBox();
		
		recommended = new Label("Recommended");
		recommended.setId("recommended");
		
		metaSpacer = new Region();
		HBox.setHgrow(metaSpacer, Priority.ALWAYS);
		
		hMetaBox.getChildren().addAll(recommended, metaSpacer);
		hMetaBox.setSpacing(hPictureBoxInset);
		
		for(int i = 0; i < metaButtons.length; i++) {
			
			metaButtons[i] = new Button();
			metaButtons[i].setPrefHeight(metaButtonHeight);
			metaButtons[i].setPrefWidth(videosWidth);
			metaButtons[i].setId("metaButtonsYT");
			hMetaBox.getChildren().add(metaButtons[i]);
			
		}
		
		metaButtons[0].setText("open App");
		metaButtons[1].setText("Search");
		metaButtons[1].setGraphic(new ImageView(new Image("file:src/icons/search.png")));
		metaButtons[2].setText("Playlists");
		
		vBox.getChildren().add(hMetaBox);
		
		hPictureBox[0] = new HBox();
		
		for(int y = 0; y < scrollHorizontal.length; y++) {
			
			scrollHorizontal[y] = new ScrollPane();
			scrollHorizontal[y].setPrefHeight(11.0 / 20.0 * tab.getTabPane().getPrefHeight());
			scrollHorizontal[y].setHbarPolicy(ScrollBarPolicy.NEVER);
			scrollHorizontal[y].setVbarPolicy(ScrollBarPolicy.NEVER);
			scrollHorizontal[y].setStyle("-fx-padding: 0");
			
			hPictureBox[y] = new HBox();
			hTitleBox[y] = new HBox();
			vVideoBox[y] = new VBox();
			
			for(int z = 0; z < videos[y].length; z++) {
				
				videos[y][z] = new Button("video: " + z);
				videos[y][z].setPrefSize(videosWidth, pictureHeight);
				videos[y][z].setId("videos");
				
				title[y][z] = new Label("Video Title: " + z);
				title[y][z].setPrefSize(videosWidth, titleHeight);
				title[y][z].wrapTextProperty().setValue(true);
				
				hPictureBox[y].getChildren().add(videos[y][z]);
				hTitleBox[y].getChildren().add(title[y][z]);
				
			}
			
			hPictureBox[y].setPadding(new Insets(hPictureBoxInset));
			hPictureBox[y].setSpacing(hPictureBoxInset);
			
			hTitleBox[y].setPadding(new Insets(hPictureBoxInset));
			hTitleBox[y].setSpacing(hPictureBoxInset);
			
			vVideoBox[y].getChildren().addAll(hPictureBox[y], hTitleBox[y]);
			
			scrollHorizontal[y].setContent(vVideoBox[y]);
			vBox.getChildren().add(scrollHorizontal[y]);
			
		}
		
		vBox.setPadding(new Insets(0, vBoxInset, vBoxInset, vBoxInset));
		vBox.setSpacing(vBoxInset);
		
	}
	
	public void startYouTube() {
		
		metaButtons[0].requestFocus();
		
		returnToApps = new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					tab.getTabPane().requestFocus();
					
				}
				
			}
			
		};
		
		for(int i = 0; i < metaButtons.length; i++) {
			
			metaButtons[i].addEventHandler(KeyEvent.KEY_PRESSED, returnToApps);
			
		}
		
		for(int i = 0; i < scrollHorizontal.length; i++) {
			
			int index = i;
			
			videos[i][4].focusedProperty().addListener(e -> {
				
				smoothScroll(scrollHorizontal[index], 1.0);
				
			});
			
		}
		
		for(int i = 0; i < scrollHorizontal.length; i++) {
			
			int index = i;
			
			videos[i][2].focusedProperty().addListener(e -> {
				
				smoothScroll(scrollHorizontal[index], 0.0);
				
			});
			
		}
		
	}
	
	private void smoothScroll(ScrollPane scrollPane, double value) {
		
		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(scrollPane.hvalueProperty(), value);
		KeyFrame keyFrame = new KeyFrame(new Duration(200), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
		
	}
	
	public void stopYouTube(){
		
		for(int i = 0; i < metaButtons.length; i++) {
			
			metaButtons[i].removeEventHandler(KeyEvent.KEY_PRESSED, returnToApps);
			
		}
		
	}

}
