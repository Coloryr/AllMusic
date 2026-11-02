package com.coloryr.allmusic.client.core;

import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextFrameBuffer;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.codec.HudPosObj;
import com.coloryr.allmusic.codec.HudPosType;
import com.coloryr.allmusic.codec.KtvLyricObj;
import org.apache.hc.client5.http.classic.methods.HttpGet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * AllMusic信息显示
 */
public class AllMusicHud {
    private static final String PG1 = "textures/hud/pg1.png";
    private static final String PG2 = "textures/hud/pg2.png";
    private static final String PG3 = "textures/hud/pg3.png";
    private static final String BG1 = "textures/hud/bg1.png";
    private static final String BG2 = "textures/hud/bg2.png";
    private static final String BG3 = "textures/hud/bg3.png";
    private static final String PG_OFFSET = "textures/hud/offset.txt";
    private static final String PIC_SCALE = "textures/hud/pic.txt";
    /**
     * 更新图片的链接
     */
    private final Queue<String> urlList = new ConcurrentLinkedDeque<>();
    /**
     * 更新锁，更新在子线程进行
     */
    private final Semaphore semaphore = new Semaphore(0);
    /**
     * 图片大小
     */
    private volatile int size;
    /**
     * 图片渲染
     */
    private PictureFrameBuffer picRender;
    /**
     * 新大小的图片渲染，等新图片上传时再替换，避免改大小后当前封面直接消失
     */
    private PictureFrameBuffer pendingPicRender;
    /**
     * 新渲染缓冲的大小
     */
    private int pendingSize;
    /**
     * 最后一张图片是用多大的尺寸生成的
     */
    private volatile int loadedSize;
    /**
     * 文字渲染
     */
    private final TextFrameBuffer<?> stateRender;
    private final TextFrameBuffer<?> infoRender;
    private final TextFrameBuffer<?> lyricRender;
    private final TextFrameBuffer<?> lyricTranRender;
    private final TextFrameBuffer<?> lyricKtvRender;
    private final TextureRender progress1;
    private final TextureRender progress2;
    private final TextureRender progress3;
    private final TextureRender bg1;
    private final TextureRender bg3;
    public KtvLyricObj ktv = null;
    /**
     * 图片buffer
     */
    private byte[] sourceImage;
    /**
     * 图片buffer
     */
    private byte[] rotateImage;
    //显示信息
    private String info = "";
    private String lyric = "";
    private String lyricTran = "";
    private String lyricKtv = "";
    private long allTime, nowTime;
    private HudPosObj save;
    private float lyricState = 0.0f;
    private long lyricTime = -1;
    private int lyricWidth;
    private final int pgOffset;
    private final float picScale;
    /**
     * 原始背景图，改图片大小时用来重新缩放
     */
    private final BufferedImage bg2Source;
    private volatile BufferedImage bg2;
    private boolean isRun;
    /**
     * 最后一张图片的地址，改图片大小时重新拉取
     * <p>
     * 由下载线程写入、客户端线程读取，必须 volatile
     */
    private volatile String lastPicUrl;

    /**
     * 是否有图片
     */
    private boolean haveImg;

    /**
     * 旋转角度
     */
    private int ang = 0;
    private int count = 0;
    /**
     * 是否需要更新材质
     */
    private boolean imageNeedUpload;
    /**
     * 是否需要文字
     */
    private boolean infoNeedUpdate;
    private boolean lyricNeedUpdate;
    private boolean stateNeedUpdate;

    public AllMusicHud(int size) {
        this.size = size;

        isRun = true;

        Thread thread = new Thread(this::run);
        thread.setName("allmusic_pic");
        thread.start();

        AllMusicCore.service.scheduleAtFixedRate(this::picRotateTick, 0, 1, TimeUnit.MILLISECONDS);
        AllMusicCore.service.scheduleAtFixedRate(this::lyricTick, 0, 10, TimeUnit.MILLISECONDS);
        AllMusicCore.service.scheduleAtFixedRate(this::loopTick, 0, 100, TimeUnit.MILLISECONDS);

        picRender = AllMusicCore.bridge.makePictureRender(size);

        stateRender = AllMusicCore.bridge.makeTextRender("state");
        infoRender = AllMusicCore.bridge.makeTextRender("info");
        lyricRender = AllMusicCore.bridge.makeTextRender("lyric");
        lyricTranRender = AllMusicCore.bridge.makeTextRender("lyric tran");
        lyricKtvRender = AllMusicCore.bridge.makeTextRender("lyric ktv");

        progress1 = AllMusicCore.bridge.makeTextureRender(PG1);
        progress2 = AllMusicCore.bridge.makeTextureRender(PG2);
        progress3 = AllMusicCore.bridge.makeTextureRender(PG3);
        bg1 = AllMusicCore.bridge.makeTextureRender(BG1);
        bg3 = AllMusicCore.bridge.makeTextureRender(BG3);

        String offset = AllMusicCore.bridge.readText(PG_OFFSET);
        pgOffset = Integer.parseInt(offset);

        offset = AllMusicCore.bridge.readText(PIC_SCALE);
        picScale = Float.parseFloat(offset);

        BufferedImage source = null;
        try {
            InputStream stream = AllMusicCore.bridge.readFile(BG2);
            source = ImageIO.read(stream);
            if (source != null) {
                bg2 = resizeImage(source, size, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bg2Source = source;
    }

    public void stop() {
        isRun = false;
        semaphore.release();
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_AREA_AVERAGING);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    /**
     * 时间转换
     *
     * @param time 时间
     * @return 结果
     */
    private static String tranTime(long time) {
        long m = time / 60;
        long s = time - m * 60;
        return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
    }

    /**
     * 绘制图片
     *
     * @param x   X坐标
     * @param y   Y坐标
     * @param dir 对齐方式
     */
    public static Point2f getPos(float width, float height, float x, float y, HudPosType dir) {
        if (dir == null) {
            return new Point2f(x, y);
        }

        float screenWidth = AllMusicCore.bridge.getScreenWidth();
        float screenHeight = AllMusicCore.bridge.getScreenHeight();

        float x1 = x;
        float y1 = y;

        switch (dir) {
            case TOP_CENTER:
                x1 = screenWidth / 2 - width / 2 + x;
                break;
            case TOP_RIGHT:
                x1 = screenWidth - width - x;
                break;
            case LEFT:
                y1 = screenHeight / 2 - height / 2 + y;
                break;
            case CENTER:
                x1 = screenWidth / 2 - width / 2 + x;
                y1 = screenHeight / 2 - height / 2 + y;
                break;
            case RIGHT:
                x1 = screenWidth - width - x;
                y1 = screenHeight / 2 - height / 2 + y;
                break;
            case BOTTOM_LEFT:
                y1 = screenHeight - height - y;
                break;
            case BOTTOM_CENTER:
                x1 = screenWidth / 2 - width / 2 + x;
                y1 = screenHeight - height - y;
                break;
            case BOTTOM_RIGHT:
                x1 = screenWidth - width - x;
                y1 = screenHeight - height - y;
                break;
        }

        return new Point2f(x1, y1);
    }

    /**
     * 图片旋转计数器
     */
    private void picRotateTick() {
        if (save == null) return;
        if (!save.pic.rotate) return;
        if (count < save.pic.speed) {
            count++;
            return;
        }
        count = 0;
        ang++;
        ang = ang % 360;
    }

    private void lyricTick() {
        if (save == null || ktv == null || lyricTime == -1) return;
        lyricTime += 10;
        kUpdate();
        updateKtvOffset();
    }

    private void updateKtvOffset() {
        if (ktv == null || save == null || lyricWidth <= 0) {
            lyricRender.clearKtvOffset();
            lyricKtvRender.clearKtvOffset();
            lyricTranRender.setState(0);
            return;
        }

        lyricTranRender.setState(lyricState);

        float offset = 0;
        int maxWidth = save.lyric.maxWidth;
        if (maxWidth > 0 && lyricWidth > maxWidth) {
            offset = (lyricWidth - maxWidth) * lyricState;
        }

        lyricRender.setKtvOffset(offset);
        lyricKtvRender.setKtvOffset(offset);
    }

    private void loopTick() {
        if (save == null) return;
        infoRender.tick();
    }

    /**
     * 清理显示
     */
    public void close() {
        clear();
        save = null;
    }

    public void clear() {
        haveImg = false;
        info = lyric = lyricTran = lyricKtv = "";
        allTime = nowTime = 0;

        infoNeedUpdate = true;
        lyricNeedUpdate = true;
        stateNeedUpdate = true;
    }

    public void kUpdate() {
        if (ktv == null) {
            lyricState = 0.0f;
            return;
        }

        if (lyricTime <= ktv.start) {
            lyricState = 0.0f;
            return;
        }

        if (lyricTime >= ktv.start + ktv.time) {
            lyricState = 1.0f;
            return;
        }

        float now = 0;
        for (int i = 0; i < ktv.items.size(); i++) {
            KtvLyricObj.KtvItem item = ktv.items.get(i);
            float itemp = (float) item.key.length() / ktv.charCount;
            if (lyricTime >= item.start && lyricTime < item.start + item.time) {
                float progressInChar = (float) (lyricTime - item.start) / item.time * itemp;
                lyricState = now + progressInChar;
                return;
            }

            now += itemp;
        }

        lyricState = 0.0f;
    }

    /**
     * 加载图片
     *
     * @param picUrl 加载地址
     */
    private void loadPic(String picUrl) {
        haveImg = false;
        lastPicUrl = picUrl;
        try {
            while (save == null || imageNeedUpload) {
                Thread.sleep(200);
            }
            if (!save.pic.enable) {
                return;
            }

            // 固定本次使用的尺寸，避免下载过程中被改掉，生成出一张和缓冲对不上的图
            final int size = this.size;

            HttpGet request = new HttpGet(picUrl);
            BufferedImage image = AllMusicCore.client.execute(request, (response -> {
                InputStream inputStream = response.getEntity().getContent();
                return resizeImage(ImageIO.read(inputStream), size, size);
            }));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            sourceImage = outputStream.toByteArray();

            BufferedImage formatAvatarImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = formatAvatarImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int border = (int) (size * (1 - picScale));
            Ellipse2D.Double shape = new Ellipse2D.Double(border, border, size - border * 2, size - border * 2);
            graphics.setClip(shape);
            graphics.drawImage(image, border, border, size - border * 2, size - border * 2, null);
            graphics.dispose();
            if (bg2 != null) {
                graphics = formatAvatarImage.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawImage(bg2, 0, 0, size, size, null);
                graphics.dispose();
            }

            outputStream = new ByteArrayOutputStream();
            ImageIO.write(formatAvatarImage, "png", outputStream);
            rotateImage = outputStream.toByteArray();

            request.clear();
            loadedSize = size;
            imageNeedUpload = true;
        } catch (Exception e) {
            e.printStackTrace();
            AllMusicCore.bridge.sendMessage("图片解析错误");
            haveImg = false;
        }
    }

    /**
     * 更新材质
     */
    private void updateTexture() {
        if (pendingPicRender != null) {
            if (loadedSize != pendingSize) {
                // 这张图是按旧尺寸生成的，缓冲对不上，丢掉重新拉一次
                String url = lastPicUrl;
                if (url != null && !url.isEmpty()) {
                    setImg(url);
                }
                return;
            }
            PictureFrameBuffer old = picRender;
            picRender = pendingPicRender;
            pendingPicRender = null;
            // 旧大小的贴图已经用不到了，释放掉
            if (old != null) {
                old.dispose();
            }
        }
        picRender.update(sourceImage, rotateImage);
        haveImg = true;
    }

    /**
     * 网络线程
     */
    private void run() {
        while (true) {
            try {
                if (!isRun) {
                    return;
                }
                semaphore.acquire();
                if (!isRun) {
                    return;
                }
                while (!urlList.isEmpty()) {
                    if (!isRun) {
                        return;
                    }
                    String picUrl = urlList.poll();
                    if (picUrl != null) {
                        loadPic(picUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置下一张图片
     *
     * @param picUrl 图片链接
     */
    public void setImg(String picUrl) {
        urlList.add(picUrl);
        semaphore.release();
    }

    /**
     * 设置位置信息
     *
     * @param save 位置信息
     */
    public boolean setPos(HudPosObj save) {
        if (save == null || save.lyric == null || save.state == null || save.info == null || save.pic == null) {
            return false;
        }
        this.save = save;

        infoNeedUpdate = true;
        lyricNeedUpdate = true;

        return true;
    }

    /**
     * 获取图片大小
     *
     * @return 图片大小
     */
    public int getPicSize() {
        return size;
    }

    /**
     * 是否已经收到服务端的 HUD 数据
     *
     * @return 是否收到
     */
    public boolean hasHudData() {
        return save != null;
    }

    /**
     * 当前是否有封面可以显示
     *
     * @return 是否有封面
     */
    public boolean hasImage() {
        return haveImg;
    }

    /**
     * 最后加载的封面地址
     *
     * @return 封面地址
     */
    public String getLastPicUrl() {
        return lastPicUrl;
    }

    /**
     * 客户端自检信息
     *
     * @return 每一行结果，[OK] 正常，[!!] 异常
     */
    public List<String> getTestLines() {
        List<String> list = new ArrayList<>();

        list.add(check(size > 0, "图片缓冲: " + size + "x" + size
                + (pendingPicRender != null ? "（有 " + pendingSize + " 的新缓冲待替换）" : "")));
        list.add(check(bg1.width > 0 && bg1.height > 0, "底图 bg1.png: " + bg1.width + "x" + bg1.height));
        list.add(check(bg3.width > 0 && bg3.height > 0, "唱臂 bg3.png: " + bg3.width + "x" + bg3.height));
        list.add(check(progress1.width > 0 && progress1.height > 0,
                "进度条 pg1.png: " + progress1.width + "x" + progress1.height));
        list.add(check(progress2.width > 0 && progress2.height > 0,
                "进度条 pg2.png: " + progress2.width + "x" + progress2.height));
        list.add(check(progress3.width > 0 && progress3.height > 0,
                "进度条 pg3.png: " + progress3.width + "x" + progress3.height));
        list.add(check(bg2 != null, "封面圆环 bg2.png: "
                + (bg2 == null ? "未加载" : bg2.getWidth() + "x" + bg2.getHeight())));
        list.add(check(save != null, "服务端 HUD 数据: " + (save == null ? "未收到" : "已收到")));

        if (save != null) {
            list.add("  信息 " + enable(save.info.enable) + " / 歌词 " + enable(save.lyric.enable)
                    + " / 进度 " + enable(save.state.enable) + " / 封面 " + enable(save.pic.enable));
            list.add("  封面位置: " + save.pic.x + "," + save.pic.y + " 大小 " + save.pic.size
                    + " 旋转 " + enable(save.pic.rotate));
        }

        list.add(check(haveImg, "当前封面: " + (haveImg ? "有" : "无")));

        return list;
    }

    private static String check(boolean ok, String text) {
        return (ok ? "[OK] " : "[!!] ") + text;
    }

    private static String enable(boolean value) {
        return value ? "开" : "关";
    }

    /**
     * 修改图片大小
     * <p>
     * 只在客户端本地改，不需要服务端配合。新大小的贴图缓冲会立刻建好，
     * 等新图片上传（重新拉取当前封面，或者下一首歌的图片包）时替换掉旧的
     *
     * @param size 图片大小
     * @return 是否修改成功
     */
    public boolean setPicSize(int size) {
        if (size <= 0 || size == this.size) {
            return false;
        }

        this.size = size;

        if (bg2Source != null) {
            bg2 = resizeImage(bg2Source, size, size);
        }

        if (pendingPicRender != null) {
            pendingPicRender.dispose();
        }
        pendingPicRender = AllMusicCore.bridge.makePictureRender(size);
        pendingSize = size;

        // 重新拉一次当前封面，让新大小马上生效；没有封面时就等下一首歌
        String url = lastPicUrl;
        if (url != null && !url.isEmpty()) {
            setImg(url);
        }

        return true;
    }

    /**
     * 显示更新
     */
    public void update() {
        if (save == null) return;

        // 需要更新文字渲染
        if (infoNeedUpdate) {
            if (!info.isEmpty()) {
                int offset = 0;
                String[] temp = info.split("\n");

                int height = AllMusicCore.bridge.getFontHeight() + (save.info.shadow ? 1 : 0);
                int allHeight = (height + save.info.gap) * temp.length;
                int allWidth = 0;

                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    allWidth = Math.max(allWidth, AllMusicCore.bridge.getTextWidth(item));
                }

                infoRender.resize(allWidth, allHeight);
                infoRender.use();
                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    infoRender.putText(item, offset, save.info.color, save.info.shadow);
                    offset += save.info.gap;
                }
                infoRender.unUse();
            } else {
                infoRender.clear();
            }

            infoNeedUpdate = false;
        }

        if (lyricNeedUpdate) {
            int offset = 0;
            if (!lyric.isEmpty()) {
                String[] temp = lyric.split("\n");

                int height = AllMusicCore.bridge.getFontHeight() + (save.lyric.shadow ? 1 : 0);
                int allHeight = (height + save.lyric.gap) * temp.length;
                int allWidth = 0;

                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    allWidth = Math.max(allWidth, AllMusicCore.bridge.getTextWidth(item));
                }

                lyricRender.resize(allWidth, allHeight);
                lyricRender.use();
                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    lyricRender.putText(item, offset, save.lyric.color, save.lyric.shadow);
                    offset += save.lyric.gap;
                }
                lyricRender.unUse();
            } else {
                lyricRender.clear();
            }

            if (!lyricTran.isEmpty()) {
                offset = 0;
                String[] temp = lyricTran.split("\n");

                int height = AllMusicCore.bridge.getFontHeight() + (save.lyric.shadow ? 1 : 0);
                int allHeight = (height + save.lyric.gap) * temp.length;
                int allWidth = 0;

                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    allWidth = Math.max(allWidth, AllMusicCore.bridge.getTextWidth(item));
                }

                lyricTranRender.resize(allWidth, allHeight);
                lyricTranRender.use();
                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    lyricTranRender.putText(item, offset, save.lyric.color, save.lyric.shadow);
                    offset += save.lyric.gap;
                }
                lyricTranRender.unUse();
            } else {
                lyricTranRender.clear();
            }

            if (!lyricKtv.isEmpty()) {
                offset = 0;
                String[] temp = lyricKtv.split("\n");

                int height = AllMusicCore.bridge.getFontHeight() + (save.lyric.shadow ? 1 : 0);
                int allHeight = (height + save.lyric.gap) * temp.length;
                int allWidth = 0;

                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    allWidth = Math.max(allWidth, AllMusicCore.bridge.getTextWidth(item));
                }

                lyricWidth = allWidth;

                lyricKtvRender.resize(allWidth, allHeight);
                lyricKtvRender.use();
                for (String item : temp) {
                    if (item.isEmpty()) {
                        continue;
                    }
                    lyricKtvRender.putText(item, offset, save.lyric.color, save.lyric.shadow);
                    offset += save.lyric.gap;
                }
                lyricKtvRender.unUse();
            } else {
                lyricWidth = 0;
                lyricKtvRender.clear();
            }

            lyricNeedUpdate = false;
        }

        if (stateNeedUpdate) {
            if (allTime == 0) {
                stateRender.clear();
            } else {
                String all = tranTime(allTime / 1000);
                String time = tranTime(nowTime / 1000);

                int allWidth = Math.max(AllMusicCore.bridge.getTextWidth(time), AllMusicCore.bridge.getTextWidth(all));
                int allHeight = (AllMusicCore.bridge.getFontHeight() + (save.state.shadow ? 1 : 0)) * 2;

                stateRender.resize(allWidth, allHeight);
                stateRender.use();
                stateRender.putText(all, 0, save.state.color, save.state.shadow);
                stateRender.putText(time, 10, save.state.color, save.state.shadow);
                stateRender.unUse();
            }
            stateNeedUpdate = false;
        }

        if (save.info.enable) {
            infoRender.draw(save.info.alpha, save.info.x, save.info.y, save.info.loop ? save.info.maxWidth : -1, save.info.pos);
        }
        if (save.lyric.enable) {
            lyricRender.draw(save.lyric.alpha, save.lyric.x, save.lyric.y, save.lyric.maxWidth, save.lyric.pos);
            lyricTranRender.draw(save.lyric.alpha, save.lyric.x, save.lyric.y + save.lyric.gap, save.lyric.maxWidth, save.lyric.pos);

            if (ktv != null) {
                lyricKtvRender.drawWithState(save.lyric.alpha, save.lyric.x, save.lyric.y, save.lyric.maxWidth, lyricState, save.lyric.pos);
            }
        }
        if (save.state.enable && allTime != 0) {
            int gap = save.state.gap;
            Point2f item = stateRender.getLine(1);
            Point2f item1 = stateRender.getLine(0);
            // 渲染原点
            Point2f point = getPos(item.x + item1.x + progress1.width + gap + gap,
                    item.y, save.state.x, save.state.y, save.state.pos);

            stateRender.drawLine(point.x, point.y, save.state.alpha, 1);
            stateRender.drawLine(point.x + progress1.width + gap + gap + item.x, point.y, save.state.alpha, 0);

            float x = point.x + gap + item.x;
            progress1.drawPic(x, point.y + pgOffset, save.state.alpha);
            progress2.drawPic(x, point.y + pgOffset, (float) nowTime / allTime, save.state.alpha);

            float x1 = x + (((float) nowTime / allTime) * (progress1.width)) - ((float) progress3.width / 2);

            progress3.drawPic(x1, save.state.y + pgOffset, save.state.alpha);
        }

        //需要更新材质
        if (imageNeedUpload) {
            imageNeedUpload = false;
            updateTexture();
        }
        //绘制图片
        if (save.pic.enable && haveImg) {
            if (save.pic.rotate) {
                bg1.drawPic(save.pic.x, save.pic.y, save.pic.size, save.pic.size, save.pic.pos, save.pic.alpha);
            }

            picRender.draw(save.pic.rotate, save.pic.size, save.pic.x, save.pic.y, ang, save.pic.pos, save.pic.alpha);

            if (save.pic.rotate) {
                bg3.drawPic(save.pic.x, save.pic.y, save.pic.size, save.pic.size, save.pic.pos, save.pic.alpha);
            }
        }
    }

    public void setInfo(String info) {
        this.info = info;

        infoNeedUpdate = true;
    }

    public void setLyric(String lyric, String tlyric, String ktv) {
        this.lyric = lyric;
        this.lyricTran = tlyric;
        this.lyricKtv = ktv;

        lyricState = 0.0f;

        lyricNeedUpdate = true;
    }

    public void setKtv(long time, KtvLyricObj pack2) {
        ktv = pack2;
        lyricTime = time;
        if (pack2 == null) {
            lyricRender.clearKtvOffset();
            lyricKtvRender.clearKtvOffset();
        }
    }

    public void setTime(long time, long now) {
        this.nowTime = now;
        this.allTime = time;

        stateNeedUpdate = true;
    }
}
