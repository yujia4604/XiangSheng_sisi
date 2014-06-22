package yujia.xiangsheng;

import yujia.model.StreamingMediaPlayer;
import yujia.model.Works;
import yujia.util.Logger;
import yujia.util.MyStringUtil;
import yujia.util.MyUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayActivity extends Activity {

	private StreamingMediaPlayer player;
	private TextView nameView;
	private TextView totalPlayTime;
	private int clickCount_down;
	private int clickCount_up;
	private TextView msgView;

	public TextView getNameView() {
		return nameView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_layout);
		ImageButton playBtn = (ImageButton) findViewById(R.id.btn_online_play);
		ImageButton stopBtn = (ImageButton) findViewById(R.id.stopbutton);
		ImageButton previousBtn = (ImageButton) findViewById(R.id.btn_previous);
		ImageButton nextBtn = (ImageButton) findViewById(R.id.btn_next);

		TextView currentPlayTime = (TextView) findViewById(R.id.currentPlayTimeView);
		msgView = (TextView) findViewById(R.id.msgView);

		totalPlayTime = (TextView) findViewById(R.id.totalPlayTimeView);
		nameView = (TextView) findViewById(R.id.nameView);
		SeekBar progressBar = (SeekBar) findViewById(R.id.music_progress);
		String filename = getIntent().getStringExtra("name");
		if (G.isFirstBoot) {
			G.isFirstBoot = false;
			MyUtil.makeToast(this, "双击音量键可切换曲目~~", true);
		}

		if (filename == null) {// 播放在在线文件
			Works works = (Works) getIntent().getSerializableExtra("works");
			Logger.i(" get serializable extra " + works);
			player = new StreamingMediaPlayer(this, currentPlayTime,
					progressBar, works, playBtn, previousBtn, nextBtn);
			int sec = works.getLength();

			String totalTime = MyStringUtil.getFormatTime(sec * 1000);
			Logger.i(" total sec = " + sec + " format string  = " + totalTime);
			totalPlayTime.setText(totalTime);
			nameView.setText(works.getName());
		} else {// 播放本地文件
			player = new StreamingMediaPlayer(this, currentPlayTime, playBtn,
					progressBar, filename);
			nameView.setText(filename);

		}

		previousBtn.setOnClickListener(player);
		nextBtn.setOnClickListener(player);
		stopBtn.setOnClickListener(player);

	}

	public TextView getMsgView() {
		return msgView;
	}

	public TextView getTotalPlayTime() {
		return totalPlayTime;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (player != null)
			player.onDestroy();
		player = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		long down = event.getDownTime();
		long eventtime = event.getEventTime();
		if (down != eventtime)
			return super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Logger.i(" volume down called");
			clickCount_down++;
			clickCount_up = 0;
			if (clickCount_down >= 2) {
				player.playNext();
				clickCount_down = 0;
			}
			return true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			Logger.i(" volume up called");
			clickCount_down = 0;
			clickCount_up++;
			if (clickCount_up >= 2) {
				player.playPrevious();
				clickCount_up = 0;
			}
			return true;

		} else {

			return super.onKeyDown(keyCode, event);

		}

	}

}
