package com.group_finity.mascot.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javax.imageio.ImageIO;



/**
 *　画像ペアを読み込む.
 */
public class ImagePairLoader {
	
	private final Path imgPath;

	public ImagePairLoader(Path imgPath) {
		this.imgPath = imgPath;
	}

	/**
	 * 画像ペアを読み込む.
	 *
	 * 左向き画像を読み込んで、右向き画像を自動生成する.
	 *
	 * @param name 読み込みたい左向き画像.
	 * @param center 画像の中央座標.
	 * @return 読み込んだ画像ペア.
	 */
	public ImagePair load(final String name, final Point center) throws IOException {
		final URL inputImg;
		if(imgPath == null) {
			inputImg = ImagePairLoader.class.getResource("/shime/img/" + name);
		} else {
			inputImg = imgPath.resolve(name).toUri().toURL();
		}
		
		BufferedImage leftImage = ImageIO.read(inputImg);
		BufferedImage rightImage = flip(leftImage);

		return new ImagePair(new MascotImage(leftImage, center), new MascotImage(rightImage, new Point(rightImage
				.getWidth()
				- center.x, center.y)));
	}

	/**
	 * 画像を左右反転させる.
	 * @param src 左右反転したい画像
	 * @return　左右反転した
	 */
	private static BufferedImage flip(final BufferedImage src) {

		final BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < src.getHeight(); ++y) {
			for (int x = 0; x < src.getWidth(); ++x) {
				copy.setRGB(copy.getWidth() - x - 1, y, src.getRGB(x, y));
			}
		}
		return copy;
	}

}
