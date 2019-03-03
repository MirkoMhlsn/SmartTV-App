package worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

public class Worker {
	
	public final int kachelAnzahl = 11;
	public double screenResolution[] = new double[2];
	public double kachelDimension;
	public int kachelSichtbar = 7;
	public double hBoxInset;
	String appNames[] = new String[kachelAnzahl];
	
	
	public Worker() {
		
		try {
			initAppNames();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initScreenResolution();
		initKachelDimension();
		initHBoxInset();
		
	}
	
	public Button kachel() {
		
		Button kachel = new Button();
		
		kachel.setFocusTraversable(true);
		kachel.setMaxHeight(kachelDimension);
		kachel.setMinHeight(kachelDimension);
		kachel.setMaxWidth(kachelDimension);
		kachel.setMinWidth(kachelDimension);
		kachel.setStyle("-fx-padding: 0");
		
		return kachel;
		
	}
	
	public ArrayList<Button> kachelGen(int anzahl){
		
		ArrayList<Button> exe = new ArrayList<Button>();
		String imagePath;
		
		for(int i = 0; i < anzahl; i++) {
			
			imagePath = "file:src/icons/" + appNames[i] + "_" + (int)kachelDimension + "x" + (int)kachelDimension + ".png";
			
			exe.add(kachel());
			if(i < appNames.length) {
				exe.get(i).setId(appNames[i]);
				exe.get(i).setGraphic(new ImageView(new Image(imagePath)));
			}else {
				exe.get(i).setId(i + "");
			}
			
		}
		
		return exe;
		
	}
	
	private void initScreenResolution() {
		
		Rectangle2D visualBounds = Screen.getPrimary().getBounds();
		screenResolution[0] = visualBounds.getHeight();
		screenResolution[1] = visualBounds.getWidth();
		
	}
	
	private void initKachelDimension() {
		
		kachelDimension = screenResolution[1] * 15.0/128.0;
		
	}
	
	private void initHBoxInset() {
		
		hBoxInset = screenResolution[1] * 1.0/64.0;
		
	}
	
	private void initAppNames() throws IOException {
		
		File appNameTXT = new File("src/icons/Apps.txt");
		FileInputStream fileInputStream = new FileInputStream(appNameTXT);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		
		int index = 0;
		String name = "";
		
		while((name = bufferedReader.readLine()) != null) {
			
			appNames[index] = name;
			index++;
			
		}
		
		bufferedReader.close();
		
	}

}
