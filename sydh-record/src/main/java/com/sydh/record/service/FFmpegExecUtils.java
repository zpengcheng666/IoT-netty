package com.sydh.record.service;

import com.sydh.record.dto.UserSettings;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FFmpegExecUtils implements InitializingBean{
    private final static Logger logger = LoggerFactory.getLogger(FFmpegExecUtils.class);
    @Autowired
    private UserSettings userSettings;

    private FFprobe ffprobe;
    private FFmpeg ffmpeg;

    public FFprobe getFfprobe() {
        return ffprobe;
    }

    public FFmpeg getFfmpeg() {
        return ffmpeg;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String ffmpegPath = userSettings.getFfmpeg();
        String ffprobePath = userSettings.getFfprobe();
        this.ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        logger.info("录像程序启动成功。 \n{}\n{} ", this.ffmpeg.version(), this.ffprobe.version());
    }

    public interface VideoHandEndCallBack {
        void run(String status, double percentage, String result);
    }

    @Async
    public void mergeOrCutFile(List<File> fils, File dest,  String destFileName, VideoHandEndCallBack callBack){

        if (fils == null || fils.size() == 0 || ffmpeg == null || ffprobe == null || dest== null || !dest.exists()){
            callBack.run("error", 0.0, null);
            return;
        }

        File tempFile = new File(dest.getAbsolutePath());
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        String fileListName = tempFile.getAbsolutePath() + File.separator + "fileList";
        double durationAll = 0.0;
        try {
            BufferedWriter bw =new BufferedWriter(new FileWriter(fileListName));
            for (File file : fils) {
                String[] split = file.getName().split("-");
                if (split.length != 3) continue;
                String durationStr = split[2].replace(".mp4", "");
                Double duration = Double.parseDouble(durationStr)/1000;
                bw.write("file " + file.getAbsolutePath());
                bw.newLine();
                durationAll += duration;
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            callBack.run("error", 0.0, null);
        }
        String recordFileResultPath = dest.getAbsolutePath() + File.separator + destFileName + ".mp4";
        long startTime = System.currentTimeMillis();
        FFmpegBuilder builder = new FFmpegBuilder()

                .setFormat("concat")
                .overrideOutputFiles(true)
                .setInput(fileListName) // Or filename
                .addExtraArgs("-safe", "0")
                .addExtraArgs("-threads", userSettings.getThreads() + "")
                .addOutput(recordFileResultPath)
                .setVideoCodec("copy")
                .setAudioCodec("aac")
                .setFormat("mp4")
                .done();

        double finalDurationAll = durationAll;
        FFmpegJob job = executor.createJob(builder, (Progress progress) -> {
            final double duration_ns = finalDurationAll * TimeUnit.SECONDS.toNanos(1);
            double percentage = progress.out_time_ns / duration_ns;

            if (progress.status.equals(Progress.Status.END)){
                callBack.run(progress.status.name(), percentage, recordFileResultPath);
            }else {
                callBack.run(progress.status.name(), percentage, null);
            }

        });
        job.run();
    }

    public long duration(File file) throws IOException {
        FFmpegProbeResult in = ffprobe.probe(file.getAbsolutePath());
        double duration = in.getFormat().duration * 1000;
        long durationLong = new Double(duration).longValue();
        return durationLong;
    }

}
