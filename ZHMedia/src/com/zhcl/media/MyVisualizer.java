package com.zhcl.media;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import com.zh.uitls.L;

import android.annotation.SuppressLint;
import android.media.audiofx.Visualizer;
import android.util.Log;

@SuppressLint("NewApi")
public class MyVisualizer implements Visualizer.OnDataCaptureListener {
	private static final String tag = MyVisualizer.class.getSimpleName();
	
	public interface IResultListener{
		public void Result(int num, String data);
	} 
	IResultListener mIResultListener;
	
	public void setIResultListener(IResultListener mIResultListener){
		this.mIResultListener = mIResultListener;
	}
	
	/**
	 * 在范围1024内取16个点（以44.1khz为参照）
	 */
	private static final int m16Points1024[] = { 2, 6, 10, 14, // 86hz, 258hz,
																// 430hz, 603hz,
			20, 24, 32, 42, // 861hz, 1khz, 1.4khz, 1.8khz,
			46, 56, 70, 92, // 2khz, 2.4khz, 3khz, 4khz,
			116, 140, 186, 232 // 5khz, 6khz, 8khz, 10khz
	};
	/**
	 * 在范围2048内取16个点（以44.1khz为参照）
	 */
	private static final int m16Points2048[] = { 4, 10, 20, 28, // 86hz, 215hz,
																// 430hz, 603hz,
			38, 46, 66, 84, // 818hz, 1khz, 1.4khz, 1.8khz,
			92, 112, 140, 186, // 2khz, 2.4khz, 3khz, 4khz,
			232, 278, 372, 464 // 5khz, 6khz, 8khz, 10khz
	};
	private static final int m16PointArrays[][] = { m16Points1024,
			m16Points2048, };

	/**
	 * 在范围1024内取32个点（以44.1khz为参照）
	 */
	private static final int m32Points1024[] = { 2, 4, 6, 8, 10, 12, 14, 16, // 86hz,
																				// 172hz,
																				// 258hz,
																				// 345hz,
																				// 430hz,
																				// 516hz,
																				// 603hz,
																				// 689hz
			18, 20, 22, 24, 28, 32, 38, 42, // 775hz, 861hz, 947hz, 1khz,
											// 1.2khz, 1.4khz, 1.6khz, 1.8khz,
			46, 52, 56, 60, 66, 70, 80, 92, // 2khz, 2.2khz, 2.4khz, 2.6khz,
											// 2.8khz, 3khz, 3.5khz, 4khz,
			116, 140, 162, 186, 232, 278, 326, 372 // 5khz, 6khz, 7khz, 8khz,
													// 10khz, 12khz, 14khz,
													// 16khz
	};

	/**
	 * 在范围2048内取32个点（以44.1khz为参照）
	 */
	private static final int m32Points2048[] = { 4, 6, 8, 10, 12, 14, 16, 20, // 86hz,
																				// 129hz,
																				// 172hz,
																				// 215hz,
																				// 258hz,
																				// 301hz,
																				// 345hz,
																				// 430hz,
			24, 28, 38, 46, 56, 66, 74, 84, // 516hz, 603hz, 818hz, 1khz,
											// 1.2khz, 1.4khz, 1.6khz, 1.8khz,
			92, 102, 112, 120, 130, 140, 162, 186, // 2khz, 2.2khz, 2.4khz,
													// 2.6khz, 2.8khz, 3khz,
													// 3.5khz, 4khz,
			232, 278, 326, 372, 464, 558, 650, 744 // 5khz, 6khz, 7khz, 8khz,
													// 10khz, 12khz, 14khz,
													// 16khz
	};
	private static final int m32PointArrays[][] = { m32Points1024,
			m32Points2048, };

	private double mFloatFfts[] = null;
	private int mIntFfts[] = null;
	private int mFftPoints[] = null;
	private boolean mFloatResult = false;
	private boolean mParamSet = false;
	private Visualizer mVisualizer = null;
	private static HashMap<Integer, int[][]> mResultSampleTable;

	static {
		mResultSampleTable = new HashMap<Integer, int[][]>();
		mResultSampleTable.put(32, m32PointArrays);
		mResultSampleTable.put(16, m16PointArrays);
	}

	public MyVisualizer() {}
	
	/**
	 * 重置频谱初始化数据
	 * @param audio_session
	 * @param waveform
	 * @param fft
	 * @param useFloat
	 */
	public void reset(int audio_session, boolean waveform, boolean fft, boolean useFloat){
		if(mVisualizer != null){
			Stop();
			mVisualizer.release();
		}
		if(audio_session == -1){
			return ;
		}
		mVisualizer = new Visualizer(audio_session);
		Stop();
		mVisualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate() / 2/* 4000 */, waveform, fft);
		mFloatResult = useFloat;
	}

	public boolean SetCaptureInfo(int sample_size, int result_size) {
		if(mVisualizer == null){
			return false;
		}
		mVisualizer.setCaptureSize(sample_size);
		if (mResultSampleTable.containsKey(result_size)) {
			int pointArrays[][] = mResultSampleTable.get(result_size);
			if ((sample_size % 1024) == 0) {
				int pos = sample_size / 1024 - 1;
				if (pos >= 0 && pos < pointArrays.length) {
					mFftPoints = pointArrays[pos];
					if (mFloatResult) {
						mFloatFfts = new double[result_size];
					} else {
						mIntFfts = new int[result_size];
					}
					mParamSet = true;
					return true;
				}
			}
		}
		return false;
	}

	public void Start() {
		if(mVisualizer != null){
			L.e(tag, "Start");
			mVisualizer.setEnabled(true);
		}
		
	}

	@SuppressLint("NewApi")
	public void Stop() {
		if(mVisualizer != null){
			L.e(tag, "Stop");
			mVisualizer.setEnabled(false);
		}
	}

	@SuppressLint("NewApi")
	public double[] GetFloatFfts() {
		if (mVisualizer.getEnabled()) {
			synchronized (mVisualizer) {
				return Arrays.copyOf(mFloatFfts, mFloatFfts.length);
			}
		}
		return null;
	}

	public int[] GetIntFfts() {
		if (mVisualizer.getEnabled()) {
			synchronized (mVisualizer) {
				return Arrays.copyOf(mIntFfts, mIntFfts.length);
			}
		}
		return null;
	}

	/**
	 * 监听回调
	 * 最大值128
	 */
	@Override
	public void onFftDataCapture(Visualizer visualizer, byte[] ffts, int rate) {
		// TODO Auto-generated method stub
//		String data = String.format(Locale.CHINESE, "update FFT(%d):", rate);
		String data = "";//String.format(Locale.CHINESE, "update FFT(%d):", rate);
		for (int i = 0; i < mFftPoints.length; i++) {
			if (mFloatResult) {
				double rpart = ffts[mFftPoints[i]], ipart = ffts[mFftPoints[i] + 1];
				double value = Math.sqrt(rpart * rpart + ipart * ipart);
				mFloatFfts[i] = value;
				data += String.format(" %.2f", value);
			} else {
				mIntFfts[i] = Math.abs(ffts[mFftPoints[i]]);
				data += String.format(" %d", Math.abs(ffts[mFftPoints[i]]));
			}
		}
		if(mIResultListener != null){
			mIResultListener.Result(16, data);
		}
//		Log.e("FFT", data);
	}

	/**
	 * 目前未使用
	 */
	@Override
	public void onWaveFormDataCapture(Visualizer arg0, byte[] waves, int rate) {
		// TODO Auto-generated method stub
		String data = String.format(Locale.CHINESE, "update WAV(%d):", rate);
		for (byte value : waves) {
			data += " " + String.format("%d", value);
		}
		Log.d("WAV", data);
	}

}
