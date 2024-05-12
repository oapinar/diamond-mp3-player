package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	@FXML
	private AnchorPane pane;
	@FXML
	private Label songLabel;
	@FXML
	private ProgressBar songProgressBar;
	@FXML
	private Button resetButton, previousButton, playButton, pauseButton, nextButton;
	@FXML
	private ComboBox<String> speedComboBox;
	@FXML
	private Slider volumeSlider;

	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	
	private ArrayList<File> songs;
	
	private int songNumber;
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
	
	private Timer timer;
	private TimerTask timerTask;
	private boolean running;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		songs = new ArrayList<>();
		directory = new File("music");
		files = directory.listFiles();
		if(files != null) {
			for(File file: files) {
				songs.add(file);
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());
		
		for(int i = 0; i < speeds.length; i++) {
			speedComboBox.getItems().add(Integer.toString(speeds[i]) + "%");
		}
		speedComboBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
			}
			
		});
		
		songProgressBar.setStyle("-fx-accent: lime");
	}
	
	public void resetMusicPlayer() {
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0.0));
	}
	
	public void previousSong() {
		if(songNumber > 0) {
			songNumber--;
			
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playSong();
		} else {
			songNumber = songs.size() - 1;
			
			pauseSong();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playSong();
		}
	}
	
	public void pauseSong() {
		cancelTimer();
		mediaPlayer.pause();
	}
	
	public void playSong() {
		beginTimer();
		changeSpeed(null);
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
		mediaPlayer.play();
	}
	
	public void nextSong() {
		if(songNumber < songs.size() - 1) {
			songNumber++;
			
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playSong();
		} else {
			songNumber = 0;
			
			pauseSong();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			playSong();
		}
	}
	
	public void changeSpeed(ActionEvent event) {
		if(speedComboBox.getValue() == null) {
			mediaPlayer.setRate(1);
		} else {
			//mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue().substring(0, speedComboBox.getValue().indexOf("%"))) * 0.01);
		}
		
	}
	
	public void beginTimer() {
		timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				System.out.println(current/end);
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					cancelTimer();
				}
			}
			
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}
	
	public void cancelTimer() {
		running = false;
		timer.cancel();
	}
}