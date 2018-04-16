package tech.huit.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 图片处理缩放处理，默认生成jpg，如果是png生成png图片
 * @author huit
 *
 */
public class ImgUtil {
	private static String JPG = "jpg";
	public static final String JPG_EXT = ".jpg";
	private static String PNG = "png";

	public static String getImgType(byte[] head) {
		String type;
		byte b0 = head[0];
		byte b1 = head[1];
		byte b2 = head[2];
		byte b3 = head[3];
		byte b6 = head[6];
		byte b7 = head[7];
		byte b8 = head[8];
		byte b9 = head[9];
		if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
			type = "gif";
		} else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
			type = "png";
		} else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I' && b9 == (byte) 'F') {
			type = "jpg";
		} else if (b0 == (byte) 'B' && b1 == (byte) 'M') {
			type = "bmp";
		} else {
			type = "Unknown";
		}
		return type;
	}

	private static String getImgType(InputStream fromFile) {
		try {
			byte[] head = new byte[10];
			fromFile.mark(10);
			fromFile.read(head);
			fromFile.reset();
			return getImgType(head);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getImgType(String filePath) {
		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(filePath));
			return getImgType(fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		return filePath;
	}

	public static void main(String[] args) throws IOException {
		long beginTime = System.currentTimeMillis();
		System.out.println("Start");
		//Thumbnails.of("D:\\png.png").size(500, 500).toFile("D:\\png2.png");
		//		Thumbnails.of("D:\\jpg.jpg").sourceRegion(Positions.CENTER, 4000, 2000).size(8000, 4000).keepAspectRatio(true)
		//				.toFile("D:\\jpg2.jpg");
		//resizeImg("D:\\png.png", "D:\\png2.png", 500, 500);
		//resizeImg("D:\\bmp.bmp", "D:\\bmp2.bmp", 500, 500);

		System.out.println("OK useTie:" + (System.currentTimeMillis() - beginTime));

	}

	/**
	 * @param fromFile 输入流
	 * @param toFile 输出文件
	 * @param outputWidth 输出宽
	 * @param outputHeight 输出高
	 * @param proportion 是否等比缩放
	 * @param forceEnlarge 是否强制放大
	 * @param isCrop 
	 */
	public static void resizeImg(InputStream fromFile, String toFile, int outputWidth, int outputHeight,
			boolean proportion, boolean forceEnlarge, boolean isCrop, boolean isUpdate) {

		if (!fromFile.markSupported()) {
			InputStreamCacher cacher = new InputStreamCacher(fromFile);
			fromFile = cacher.getInputStream();
		}
		String imgType = getImgType(fromFile);
		try {
			BufferedImage srcImg = ImageIO.read(fromFile);
			int newWidth;
			int newHeight;
			if (proportion == true && !isCrop) {
				// 为等比缩放计算输出的图片宽度及高度
				double rate1 = ((double) srcImg.getWidth(null)) / (double) outputWidth;
				double rate2 = ((double) srcImg.getHeight(null)) / (double) outputHeight;
				// 根据缩放比率大的进行缩放控制
				double rate = rate1 > rate2 ? rate1 : rate2;
				if (rate < 1 || forceEnlarge) {
					newWidth = srcImg.getWidth(null);
					newHeight = srcImg.getHeight(null);
				} else {
					newWidth = (int) (((double) srcImg.getWidth(null)) / rate);
					newHeight = (int) (((double) srcImg.getHeight(null)) / rate);
				}
			} else {
				newWidth = outputWidth; // 输出的图片宽度
				newHeight = outputHeight; // 输出的图片高度
			}
			if (PNG.equals(imgType)) {
				writePng(toFile, srcImg, newWidth, newHeight, isCrop);
			} else {
				writeJpg(toFile, srcImg, newWidth, newHeight, isCrop);
			}

			if (isUpdate) {//更新删除老的切图
				deleteCutFile(toFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void resizeImg(InputStream fromFile, String toFile, int outputWidth, int outputHeight,
			boolean isUpdate) {
		resizeImg(fromFile, toFile, outputWidth, outputHeight, true, false, false, isUpdate);
	}

	public static void resizeImg(String fromFile, String toFile, int outputWidth, int outputHeight, boolean isUpdate) {
		resizeImg(fromFile, toFile, outputWidth, outputHeight, true, false, false, isUpdate);
	}

	public static void resizeImg(String fromFile, String toFile, int outputWidth, int outputHeight, boolean isCrop,
			boolean isUpdate) {
		resizeImg(fromFile, toFile, outputWidth, outputHeight, true, false, isCrop, isUpdate);
	}

	public static void resizeImg(String fromFile, String toFile, int outputWidth, int outputHeight, boolean proportion,
			boolean forceEnlarge, boolean isCrop, boolean isUpdate) {
		try {
			resizeImg(new FileInputStream(fromFile), toFile, outputWidth, outputHeight, proportion, forceEnlarge,
					isCrop, isUpdate);
		} catch (FileNotFoundException e) {

		}
	}

	private static void writePng(String toFile, BufferedImage srcImg, int newWidth, int newHeight, boolean isCrop)
			throws IOException {
		BufferedImage toImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = toImg.createGraphics();
		toImg = g.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
		g.dispose();
		g = toImg.createGraphics();
		Image from = srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_AREA_AVERAGING);
		g.drawImage(from, 0, 0, null);
		g.dispose();
		ImageIO.write(toImg, PNG, new File(toFile));
	}

	private static void writeJpg(String toFile, BufferedImage srcImg, int newWidth, int newHeight, boolean isCrop)
			throws IOException, FileNotFoundException {
		BufferedImage toImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		if (isCrop) {
			int srcw = srcImg.getWidth(null);
			int srch = srcImg.getHeight(null);

			float rateNew = (float) newWidth / newHeight;
			float rateOld = (float) srcw / srch;
			if (rateNew > rateOld) {//新的更宽，保留宽度
				srch = (int) (srch / rateNew);
			} else {
				srcw = (int) (srcw * rateNew);
			}

			Builder<?> builder = Thumbnails.of(srcImg);
			builder.sourceRegion(Positions.CENTER, srcw, srch);
			if (newWidth > srcw) {//不进行图片强制放大
				newWidth = srcw;
				newHeight = srch;
			}
			File file = new File(toFile);
			builder.outputFormat(JPG).size(newWidth, newHeight).keepAspectRatio(true).toFile(file);
		} else {
			Graphics2D g = toImg.createGraphics();
			g.setBackground(Color.WHITE);
			g.drawImage(srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();
			ImageIO.write(toImg, JPG, new File(toFile));
		}
	}

	/** 
	* 根据尺寸图片居中裁剪 
	*/
	public static void cutCenterImage(String src, String dest, int w, int h) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		int imageIndex = 0;
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2, (reader.getHeight(imageIndex) - h) / 2,
				w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "jpg", new File(dest));

	}

	public static void resizeImg(String srcImgPath, String savePath, int toWidth, int toHeight) {
		resizeImg(srcImgPath, savePath, toWidth, toHeight, false);
	}
	
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if(file.isFile()){
			file.delete();
		}
		deleteCutFile(filePath);
	}
	
	/**
	 * 生成的缩放图
	 * @param filePath
	 */
	public static void deleteCutFile(String filePath) {
		int begin = filePath.lastIndexOf("/");
		int end = filePath.lastIndexOf(".");
		final String fileNamePrefix = filePath.substring(begin + 1, end);
		File fileDir = new File(filePath.substring(0, begin));
		if(fileDir.isDirectory()){
			File[] files = fileDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith(fileNamePrefix+"-")) {
						return true;
					} else {
						return false;
					}
				}
			});
			
			for (File file : files) {
				file.delete();
			}
		}
	}
}
