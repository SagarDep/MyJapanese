package pri.weiqiang.encryption;

import java.io.IOException;
import java.nio.ByteBuffer;

import pri.weiqiang.myjapanese.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

/**
 * @author  54wall 
 * @date 创建时间：2016-5-24 下午4:40:10
 * @version 1.0 
 * http://www.oschina.net/code/snippet_1997485_47999
 */
public class MediaCoder2 {
	Context context;
	private final String TAG = "MediaCodeSample";
    /** 用来解码 */
    private MediaCodec mMediaCodec;
    /** 用来读取音频文件 */
    private MediaExtractor extractor;
    private MediaFormat format;
    private String mime = null;
    private int sampleRate = 0, channels = 0, bitrate = 0;
    private long presentationTimeUs = 0, duration = 0;
    public void decode(String url)
    {
 
        extractor = new MediaExtractor();
        // 根据路径获取源文件
        try
        {
        	/*54wall2016.5.24 17.28 依然不起作用*/
			AssetFileDescriptor sampleFD =  context.getResources().openRawResourceFd(R.raw.ding);
			extractor.setDataSource(sampleFD.getFileDescriptor(),
                    sampleFD.getStartOffset(), sampleFD.getLength());
        	/*old*/
//            extractor.setDataSource(url);
        } catch (Exception e)
        {
            Log.e(TAG, " 设置文件路径错误" + e.getMessage());
        }
        try
        {
            // 音频文件信息
            format = extractor.getTrackFormat(0);
            mime = format.getString(MediaFormat.KEY_MIME);
            sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            // 声道个数：单声道或双声道
            channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            // if duration is 0, we are probably playing a live stream
            duration = format.getLong(MediaFormat.KEY_DURATION);
            // System.out.println("歌曲总时间秒:"+duration/1000000);
            bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
        } catch (Exception e)
        {
            Log.e(TAG, "音频文件信息读取出错：" + e.getMessage());
            // 不要退出，下面进行判断
        }
        Log.d(TAG, "Track info: mime:" + mime + " 采样率sampleRate:" + sampleRate + " channels:" + channels + " bitrate:"
                + bitrate + " duration:" + duration);
        // 检查是否为音频文件
        if (format == null || !mime.startsWith("audio/"))
        {
            Log.e(TAG, "不是音频文件 end !");
            return;
        }
        // 实例化一个指定类型的解码器,提供数据输出
        // Instantiate an encoder supporting output data of the given mime type
        try {
			mMediaCodec = MediaCodec.createDecoderByType(mime);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 
        if (mMediaCodec == null)
        {
            Log.e(TAG, "创建解码器失败！");
            return;
        }
        mMediaCodec.configure(format, null, null, 0);
 
        mMediaCodec.start();
        // 用来存放目标文件的数据
        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        // 解码后的数据
        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
        // 设置声道类型:AudioFormat.CHANNEL_OUT_MONO单声道，AudioFormat.CHANNEL_OUT_STEREO双声道
        int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        Log.i(TAG, "channelConfiguration=" + channelConfiguration);
        extractor.selectTrack(0);
        // ==========开始解码=============
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        final long kTimeOutUs = 10;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        while (!sawOutputEOS)
        {
            try
            {
                if (!sawInputEOS)
                {
                    int inputBufIndex = mMediaCodec.dequeueInputBuffer(kTimeOutUs);
                    if (inputBufIndex >= 0)
                    {
                        ByteBuffer dstBuf = inputBuffers[inputBufIndex];
 
                        int sampleSize = extractor.readSampleData(dstBuf, 0);
                        if (sampleSize < 0)
                        {
                            Log.d(TAG, "saw input EOS. Stopping playback");
                            sawInputEOS = true;
                            sampleSize = 0;
                        } else
                        {
                            presentationTimeUs = extractor.getSampleTime();
                        }
 
                        mMediaCodec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs,
                                sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
 
                        if (!sawInputEOS)
                        {
                            extractor.advance();
                        }
 
                    } else
                    {
                        Log.e(TAG, "inputBufIndex " + inputBufIndex);
                    }
                } // !sawInputEOS
 
                // decode to PCM and push it to the AudioTrack player
                int res = mMediaCodec.dequeueOutputBuffer(info, kTimeOutUs);
 
                if (res >= 0)
                {
                    int outputBufIndex = res;
                    ByteBuffer buf = outputBuffers[outputBufIndex];
                    final byte[] chunk = new byte[info.size];
                    buf.get(chunk);
                    buf.clear();
                    if (chunk.length > 0)
                    {
 
                        // chunk解码后的音频流
                        // TODO:处理...
                    }
                    mMediaCodec.releaseOutputBuffer(outputBufIndex, false);
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        Log.d(TAG, "saw output EOS.");
                        sawOutputEOS = true;
                    }
 
                } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                {
                    outputBuffers = mMediaCodec.getOutputBuffers();
                    Log.w(TAG, "[AudioDecoder]output buffers have changed.");
                } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                    MediaFormat oformat = mMediaCodec.getOutputFormat();
                    Log.w(TAG, "[AudioDecoder]output format has changed to " + oformat);
                } else
                {
                    Log.w(TAG, "[AudioDecoder] dequeueOutputBuffer returned " + res);
                }
 
            } catch (RuntimeException e)
            {
                Log.e(TAG, "[decodeMP3] error:" + e.getMessage());
            }
        }
        // =================================================================================
        if (mMediaCodec != null)
        {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        if (extractor != null)
        {
            extractor.release();
            extractor = null;
        }
        // clear source and the other globals
        duration = 0;
        mime = null;
        sampleRate = 0;
        channels = 0;
        bitrate = 0;
        presentationTimeUs = 0;
        duration = 0;
    }
}
