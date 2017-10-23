package application;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MainController implements Initializable {

	private MediaPlayer mp;
	private Media song;
	@FXML MediaView mv;
	@FXML Slider sldrVol, sldrSpeed, sldrBal, sldrSeek;
	@FXML Label lblVolVal, lblSpeedVal, lblBalVal, lblSeek, lblFile;
	int totalMinutes, totalSeconds;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//***Media Player Configuration***
		String songPath = "src/media/macarena.mp3";
		File songFile = new File(songPath);
		song = new Media(songFile.toURI().toString());
		mp = new MediaPlayer(song);
		mp.setAutoPlay(true);
		mv.setMediaPlayer(mp);
		
		//***Slider Configurations***
		//Volume Slider
		sldrVol.setMin(0);
		sldrVol.setMax(100);
		sldrVol.setValue(100);
		sldrVol.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				mp.setVolume(sldrVol.getValue()/100);
				lblVolVal.setText(""+Math.round(sldrVol.getValue()));
			}
		});
		//Speed Slider
		DecimalFormat df = new DecimalFormat("#.##");
		sldrSpeed.setMin(0.5);
		sldrSpeed.setMax(2);
		sldrSpeed.setValue(1);
		sldrSpeed.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				mp.setRate(sldrSpeed.getValue());
				lblSpeedVal.setText(""+df.format(sldrSpeed.getValue())+"X");
			}
		});
		//Balance Slider
		sldrBal.setMin(-1.0);
		sldrBal.setMax(1.0);
		sldrBal.setValue(0);
		sldrBal.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				mp.setBalance(sldrBal.getValue());
				lblBalVal.setText(""+df.format(sldrBal.getValue()));
			}
		});
		//Seeking Slider
		mp.setOnReady(new Runnable() {
			@Override
			public void run() {
				sldrSeek.setMin(0);
				sldrSeek.setMax(mp.getTotalDuration().toSeconds());
				totalSeconds = (int)Math.round(mp.getTotalDuration().toSeconds())%60;
				totalMinutes = (int)Math.round(mp.getTotalDuration().toSeconds())/60;
				lblSeek.setText("0:00/"+totalMinutes+":"+totalSeconds);
				sldrSeek.setValue(0);
				sldrSeek.valueProperty().addListener(new InvalidationListener() {
					@Override
					public void invalidated(Observable arg0) {
						if(sldrSeek.isValueChanging()) {
							mp.seek(new Duration(sldrSeek.getValue()*1000));
							int seconds = (int)Math.round(sldrSeek.getValue())%60;
							int minutes = (int)Math.round(sldrSeek.getValue())/60;
							lblSeek.setText(""+minutes+":"+((seconds<10)?"0"+seconds:seconds)+
									"/"+totalMinutes+":"+((totalSeconds<10)?"0"+totalSeconds:totalSeconds));
						}
					}
				});
			}
		});
		Timer updateSeek = new Timer();
		updateSeek.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						sldrSeek.setValue(mp.getCurrentTime().toSeconds());
						int seconds = (int)Math.round(mp.getCurrentTime().toSeconds())%60;
						int minutes = (int)Math.round(mp.getCurrentTime().toSeconds())/60;
						lblSeek.setText(""+minutes+":"+((seconds<10)?"0"+seconds:seconds)+
								"/"+totalMinutes+":"+((totalSeconds<10)?"0"+totalSeconds:totalSeconds));
					}
				});
			}
		}, 0, 200);
		
		//***Filename***
		lblFile.setText(songFile.getName());
		
		
	}
	
	public void play() {
		mp.play();
	}
	
	public void pause() {
		mp.pause();
	}
	
	public void restart() {
		mp.seek(mp.getStartTime());
		mp.play();
	}

}
