import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.awt.*; //これが要る
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import processing.core.*;
import com.shigeodayo.ardrone.manager.*;
import com.shigeodayo.ardrone.navdata.*;
import com.shigeodayo.ardrone.utils.*;
import com.shigeodayo.ardrone.processing.*;
import com.shigeodayo.ardrone.command.*;
import com.shigeodayo.ardrone.*;
import ddf.minim.*; //Sound Library

public class VoiceControlARDrone extends PApplet {
	Minim minim;
	AudioPlayer player;
	ARDroneForP5 ardrone;
	String strings;
	String last_strings;
	public String getUrlDataParsing() throws MalformedURLException, IOException {
		Source source = new Source(new URL(
				"http://voicecontrol.heroku.com/speech/result"));
		List<Element> divList = source.getAllElements(HTMLElementName.DIV);
		Element div = divList.get(0);
		String result = String.format("%s", div.getContent().toString());
		System.out.println(result);
		return result;
	};

	public void setup() {
		frameRate(1);
		size(320, 240);
		minim = new Minim(this);
		ardrone = new ARDroneForP5("192.168.1.220");
		// AR.Droneに接続，操縦するために必要
		ardrone.connect();
		// AR.Droneからのセンサ情報を取得するために必要
		ardrone.connectNav();
		// AR.Droneからの画像情報を取得するために必要
		ardrone.connectVideo();
		// これを宣言すると上でconnectした3つが使えるようになる．
		ardrone.start();
		player = minim.loadFile("hello.mp3", 128);
		player.play();
		last_strings = "default";
	}

	public void draw() {
		background(204);
		// AR.Droneからの画像を取得
		PImage img = ardrone.getVideoImage(false);
		if (img == null)
			return;
		image(img, 0, 0);
		// AR.Droneからのセンサ情報を標準出力する．
		// ardrone.printARDroneInfo();
		// 各種センサ情報を取得する
		float pitch = ardrone.getPitch();
		float roll = ardrone.getRoll();
		float yaw = ardrone.getYaw();
		float altitude = ardrone.getAltitude();
		float[] velocity = ardrone.getVelocity();
		int battery = ardrone.getBatteryPercentage();

		String attitude = "pitch:" + pitch + "\nroll:" + roll + "\nyaw:" + yaw
				+ "\naltitude:" + altitude;
		text(attitude, 20, 85);
		String vel = "vx:" + velocity[0] + "\nvy:" + velocity[1];
		text(vel, 20, 140);
		String bat = "battery:" + battery + " %";
		text(bat, 20, 170);
		// Webアプリケーション上のメッセージをパースする
		try {
			strings = getUrlDataParsing();
			System.out.println(strings);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( strings.equals("UP") && !last_strings.equals("UP") ) {
			System.out.println("前進します");
			ardrone.forward();// 前
			player = minim.loadFile("forward.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "UP";
		} else if ( strings.equals("DOWN") && !last_strings.equals("DOWN") ) {
			System.out.println("後退します");
			ardrone.backward();// 後
			player = minim.loadFile("backward.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "DOWN";
		} else if (strings.equals("LEFT") && !last_strings.equals("LEFT")) {
			System.out.println("左に旋回します");
			ardrone.goLeft();// 左
			player = minim.loadFile("goleft.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "LEFT";
		} else if (strings.equals("RIGHT") && !last_strings.equals("RIGHT")) {
			System.out.println("右に旋回します");
			ardrone.goRight();// 右
			player = minim.loadFile("goright.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "RIGHT";
		} else if (strings.equals("SHIFT") && !last_strings.equals("SHIFT")) {
			System.out.println("離陸します");
			ardrone.takeOff();// 離陸，離陸した状態でないと移動は出来ない．
			player = minim.loadFile("takeoff.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "SHIFT";
		} else if (strings.equals("CONTROL") && !last_strings.equals("CONTROL")) {
			System.out.println("着陸します");
			ardrone.landing();// 着陸
			player = minim.loadFile("landing.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "CONTROL";
		} else if (strings.equals("s") && !last_strings.equals("s")) {
			System.out.println("緊急停止します");
			ardrone.stop();// 停止
			player = minim.loadFile("emergency_stop.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "s";
		} else if (strings.equals("r") && !last_strings.equals("r")) {
			System.out.println("右回転します");
			ardrone.spinRight(); // 右方向に回転
			player = minim.loadFile("turning_right.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "r";
		} else if (strings.equals("l") && !last_strings.equals("l")) {
			System.out.println("左回転します");
			ardrone.spinLeft();// 左方向に回転
			player = minim.loadFile("turning_left.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "l";
		} else if (strings.equals("u") && !last_strings.equals("u")) {
			System.out.println("上昇します");
			ardrone.up();// 上昇
			player = minim.loadFile("goup.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "u";
		} else if (strings.equals("d") && !last_strings.equals("d")) {
			System.out.println("下降します");
			ardrone.down();// 下降
			player = minim.loadFile("godown.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "d";
		} else if (strings.equals("1") && !last_strings.equals("1")) {
			System.out.println("前方カメラON");
			ardrone.setHorizontalCamera();
			player = minim.loadFile("frontcamera.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "1";
		} else if (strings.equals("2") && !last_strings.equals("2")) {
			System.out.println("前方カメラ&垂直カメラON");
			ardrone.setHorizontalCameraWithVertical();// 前カメラとお腹カメラ
			player = minim.loadFile("bothcamera.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "2";
		} else if (strings.equals("3") && !last_strings.equals("3")) {
			System.out.println("垂直カメラON");
			ardrone.setVerticalCamera();// お腹カメラ
			player = minim.loadFile("verticalcamera.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "3";
		} else if (strings.equals("4") && !last_strings.equals("4")) {
			System.out.println("前方カメラ&垂直カメラON");
			ardrone.setVerticalCameraWithHorizontal();// お腹カメラと前カメラ
			player = minim.loadFile("bothcamera.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "4";
		} else if (strings.equals("5") && !last_strings.equals("5")) {
			System.out.println("カメラを切り替えます");
			ardrone.toggleCamera();// 次のカメラ
			player = minim.loadFile("togglecamera.mp3", 128);
			player.play();
			strings = "0";
			last_strings = "5";
		}
		
		
	}
	// メイン関数を書くとJavaアプリケーションとして走る
	// public static void main(String args[]){
	// PApplet.main(new String[] { "--present", "HelloProcessing" });
	// }

}
