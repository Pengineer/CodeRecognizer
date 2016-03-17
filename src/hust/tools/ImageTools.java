package hust.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageTools {
	
	public static int isBlack(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {
			return 1;
		}
		return 0;
	}

	public static int isWhite(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > 100) {
			return 1;
		}
		return 0;
	}
	
	public static BufferedImage removeBlank(BufferedImage img) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		int start = 0;
		int end = 0;
		Label1: for (int y = 0; y < height; ++y) {
			int count = 0;
			for (int x = 0; x < width; ++x) {
				if (isWhite(img.getRGB(x, y)) == 1) {
					count++;
				}
				if (count >= 1) {
					start = y;
					break Label1;
				}
			}
		}
		Label2: for (int y = height - 1; y >= 0; --y) {
			int count = 0;
			for (int x = 0; x < width; ++x) {
				if (isWhite(img.getRGB(x, y)) == 1) {
					count++;
				}
				if (count >= 1) {
					end = y;
					break Label2;
				}
			}
		}
		return img.getSubimage(0, start, width, end - start + 1);
	}

	/**
	 * 去图片背景
	 * @param picFile
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage removeBackgroud(String picFile)
			throws Exception {
		BufferedImage img = ImageIO.read(new File(picFile));
		int width = img.getWidth();  
	    int height = img.getHeight();  
		BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);  
	    for(int i= 0 ; i < width ; i++){  
	        for(int j = 0 ; j < height; j++){  
	        int rgb = img.getRGB(i, j);  
	        grayImage.setRGB(i, j, rgb);  
	        }  
	    } 
		return reverse(grayImage);
	}
	
	/**
	 * 图像反色
	 * @param picFile
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage reverse(BufferedImage img)
			throws Exception {
		int width = img.getWidth();  
	    int height = img.getHeight();  
		BufferedImage reverseImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);  
	    for(int i= 0 ; i < width ; i++){  
	        for(int j = 0 ; j < height; j++){  
	        int rgb = img.getRGB(i, j);  
	        Color col = new Color(rgb);
	        int r = col.getRed();
	        int g = col.getGreen();
	        int b = col.getBlue();
	        int r_ = 255-r;
	        int g_ = 255-g;
	        int b_ = 255-b;
	        Color newCol = new Color(r_,g_,b_);
	        reverseImage.setRGB(i, j, newCol.getRGB());  
	        }  
	    } 
		return reverseImage;
	}
	
	/**
	 * 切割图片
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public static List<BufferedImage> splitImage(BufferedImage img)
			throws Exception {
		List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		int width = img.getWidth();
		int height = img.getHeight();
		List<Integer> weightlist = new ArrayList<Integer>();
		for (int x = 0; x < width; ++x) {
			int count = 0;
			for (int y = 0; y < height; ++y) {
				if (isWhite(img.getRGB(x, y)) == 1) {
					count++;
				}
			}
			weightlist.add(count);
		}
		for (int i = 1; i < weightlist.size();i++) {
			if(weightlist.get(i-1)==0 && weightlist.get(i)>0){
				int j = i;
				while (weightlist.get(j++) > 0) {

				}
				subImgs.add(removeBlank(img.getSubimage(i-1, 0,
						j-i, height)));
			}
		}
		return subImgs;
	}
}
