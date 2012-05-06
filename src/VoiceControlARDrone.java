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

			if (strings == "UP") {
				ardrone.forward();// 前
				player = minim.loadFile("forward.mp3", 128);
				player.play();
			} else if (strings == "DOWN") {
				ardrone.backward();// 後
				player = minim.loadFile("backward.mp3", 128);
				player.play();
			} else if (strings == "LEFT") {
				ardrone.goLeft();// 左
				player = minim.loadFile("goleft.mp3", 128);
				player.play();
			} else if (strings == "RIGHT") {
				ardrone.goRight();// 右
				player = minim.loadFile("goright.mp3", 128);
				player.play();
			} else if (strings == "SHIFT") {
				ardrone.takeOff();// 離陸，離陸した状態でないと移動は出来ない．
				player = minim.loadFile("takeoff.mp3", 128);
				player.play();
			} else if (strings == "CONTROL") {
				ardrone.landing();// 着陸
				player = minim.loadFile("landing.mp3", 128);
				player.play();
			} else if (strings == "s") {
				ardrone.stop();// 停止
				player = minim.loadFile("emergency_stop.mp3", 128);
				player.play();
			} else if (strings == "r") {
				ardrone.spinRight(); // 右方向に回転
				player = minim.loadFile("turning_right.mp3", 128);
				player.play();
			} else if (strings == "l") {
				ardrone.spinLeft();// 左方向に回転
				player = minim.loadFile("turning_left.mp3", 128);
				player.play();
			} else if (strings == "u") {
				ardrone.up();// 上昇
				player = minim.loadFile("goup.mp3", 128);
				player.play();
			} else if (strings == "d") {
				ardrone.down();// 下降
				player = minim.loadFile("godown.mp3", 128);
				player.play();
			} else if (strings == "1") {
				ardrone.setHorizontalCamera();
				player = minim.loadFile("frontcamera.mp3", 128);
				player.play();
			} else if (strings == "2") {
				ardrone.setHorizontalCameraWithVertical();// 前カメラとお腹カメラ
				player = minim.loadFile("bothcamera.mp3", 128);
				player.play();
			} else if (strings == "3") {
				ardrone.setVerticalCamera();// お腹カメラ
				player = minim.loadFile("verticalcamera.mp3", 128);
				player.play();
			} else if (strings == "4") {
				ardrone.setVerticalCameraWithHorizontal();// お腹カメラと前カメラ
				player = minim.loadFile("bothcamera.mp3", 128);
				player.play();
			} else if (strings == "5") {
				ardrone.toggleCamera();// 次のカメラ
				player = minim.loadFile("togglecamera.mp3", 128);
				player.play();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// メイン関数を書くとJavaアプリケーションとして走る
	// public static void main(String args[]){
	// PApplet.main(new String[] { "--present", "HelloProcessing" });
	// }

}
