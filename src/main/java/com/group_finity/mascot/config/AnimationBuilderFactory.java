package com.group_finity.mascot.config;

import java.io.IOException;

import com.group_finity.mascot.image.ImagePairLoader;

public class AnimationBuilderFactory {
	
	private final ImagePairLoader imagePairLoader;
	
	public AnimationBuilderFactory(Configuration configuration) {
		imagePairLoader = new ImagePairLoader(configuration.shimejiImgFolder);
	}

	
	public AnimationBuilder create(Entry animationNode) throws IOException {
		return new AnimationBuilder(imagePairLoader, animationNode);
	}
}
