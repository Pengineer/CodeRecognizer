package hust.recognize;

import hust.tools.ImageTools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;


public class CaptchaRecognizer {
	
	private static Map<BufferedImage, String> trainMap = null;
	private static int index = 0;

	/**
	 * 下载待训练图片
	 * @param num 下载数
	 * @param storagePath 下载存储路径
	 */
	public static void downloadImage(int num, String storagePath) {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = null;
		for (int i = 0; i < num; i++) {
			getMethod = new GetMethod("http://csdc.info/smdb/rand.action");
			try {
				// 执行getMethod
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: " + getMethod.getStatusLine());
				}
				// 读取内容
				String picName = storagePath +  i + ".jpg";
				InputStream inputStream = getMethod.getResponseBodyAsStream();
				OutputStream outStream = new FileOutputStream(picName);
				IOUtils.copy(inputStream, outStream);
				outStream.close();
				System.out.println(i + "OK!");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 释放连接
				getMethod.releaseConnection();
			}
		}
	}
	
	/**
	 * 训练数据样本
	 * @throws Exception
	 */
	public static void trainData() throws Exception {
		String picPath = System.getProperty("user.dir") + "\\image";
		String trainPath = System.getProperty("user.dir") + "\\train";
		File dir = new File(picPath);
		File[] files = dir.listFiles();
		for (File file : files) {
			BufferedImage img = ImageTools.removeBackgroud(picPath + "\\" + file.getName());
			List<BufferedImage> listImg = ImageTools.splitImage(img);
			if (listImg.size() == 4) {
				for (int j = 0; j < listImg.size(); ++j) {
					ImageIO.write(listImg.get(j), "JPG", new File(trainPath + "\\" + file.getName().charAt(j) + "-" + (index++) + ".jpg"));
				}
			}
		}
		System.out.println("train over!");
	}
	
	/**
	 * 加载训练样本
	 * @return
	 * @throws Exception
	 */
	public static Map<BufferedImage, String> loadTrainData() throws Exception {
		if (trainMap == null) {
			Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
			File dir = new File(System.getProperty("user.dir") + "\\train");
			File[] files = dir.listFiles();
			for (File file : files) {
				map.put(ImageIO.read(file), file.getName().charAt(0) + "");
			}
			trainMap = map;
		}
		return trainMap;
	}
	
	/**
	 * 获取单个字符
	 * @param img
	 * @param map
	 * @return
	 */
	public static String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> map) {
		String result = "";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (BufferedImage bi : map.keySet()) {
			int count = 0;
			int widthmin = width < bi.getWidth() ? width : bi.getWidth();
			int heightmin = height < bi.getHeight() ? height : bi.getHeight();
			Label1: for (int x = 0; x < widthmin; ++x) {
				for (int y = 0; y < heightmin; ++y) {
					if (ImageTools.isWhite(img.getRGB(x, y)) != ImageTools.isWhite(bi.getRGB(x, y))) {
						count++;
						if (count >= min)
							break Label1;
					}
				}
			}
			if (count < min) {
				min = count;
				result = map.get(bi);
			}
		}
		return result;
	}
	/**
	 * 识别验证码文件中的字符
	 * @param file
	 * @return
	 * @throws Exception 
	 */
	public static String recognize(String input)  {
		String result = null;
		try {
			BufferedImage img = ImageTools.removeBackgroud(input);
			List<BufferedImage> listImg = ImageTools.splitImage(img);
			Map<BufferedImage, String> map = loadTrainData();
			result = "";
			for (BufferedImage bi : listImg) {
				result += getSingleCharOcr(bi, map);
			}
		} catch (Exception e) {
			return "0000";
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.dir"));
	}
}
