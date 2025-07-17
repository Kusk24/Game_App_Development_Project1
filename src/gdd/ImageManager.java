package gdd;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * ImageManager handles all image loading, caching, and scaling operations
 * using BufferedImage for better performance and memory management.
 */
public class ImageManager {
    
    private static ImageManager instance;
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final Map<String, BufferedImage> scaledImageCache = new HashMap<>();
    
    private ImageManager() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Get the singleton instance of ImageManager
     */
    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    /**
     * Load an image from file path and cache it
     * @param imagePath The path to the image file
     * @return BufferedImage or null if loading fails
     */
    public BufferedImage loadImage(String imagePath) {
        // Check if image is already cached
        if (imageCache.containsKey(imagePath)) {
            return imageCache.get(imagePath);
        }
        
        try {
            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);
            
            if (image != null) {
                // Cache the original image
                imageCache.put(imagePath, image);
                return image;
            } else {
                System.err.println("Failed to load image: " + imagePath);
                return createErrorImage();
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return createErrorImage();
        }
    }
    
    /**
     * Load and scale an image, with caching for the scaled version
     * @param imagePath The path to the image file
     * @param scaleFactor The scaling factor to apply
     * @return Scaled BufferedImage
     */
    public BufferedImage loadScaledImage(String imagePath, int scaleFactor) {
        String cacheKey = imagePath + "_scale_" + scaleFactor;
        
        // Check if scaled image is already cached
        if (scaledImageCache.containsKey(cacheKey)) {
            return scaledImageCache.get(cacheKey);
        }
        
        // Load the original image
        BufferedImage originalImage = loadImage(imagePath);
        if (originalImage == null) {
            return createErrorImage();
        }
        
        // Calculate new dimensions
        int newWidth = originalImage.getWidth() * scaleFactor;
        int newHeight = originalImage.getHeight() * scaleFactor;
        
        // Create scaled image
        BufferedImage scaledImage = createScaledImage(originalImage, newWidth, newHeight);
        
        // Cache the scaled image
        scaledImageCache.put(cacheKey, scaledImage);
        
        return scaledImage;
    }
    
    /**
     * Load and scale an image to specific dimensions
     * @param imagePath The path to the image file
     * @param width Target width
     * @param height Target height
     * @return Scaled BufferedImage
     */
    public BufferedImage loadScaledImage(String imagePath, int width, int height) {
        String cacheKey = imagePath + "_size_" + width + "x" + height;
        
        // Check if scaled image is already cached
        if (scaledImageCache.containsKey(cacheKey)) {
            return scaledImageCache.get(cacheKey);
        }
        
        // Load the original image
        BufferedImage originalImage = loadImage(imagePath);
        if (originalImage == null) {
            return createErrorImage();
        }
        
        // Create scaled image
        BufferedImage scaledImage = createScaledImage(originalImage, width, height);
        
        // Cache the scaled image
        scaledImageCache.put(cacheKey, scaledImage);
        
        return scaledImage;
    }
    
    /**
     * Create a high-quality scaled image using BufferedImage
     * @param originalImage The source image
     * @param width Target width
     * @param height Target height
     * @return Scaled BufferedImage
     */
    private BufferedImage createScaledImage(BufferedImage originalImage, int width, int height) {
        // Create a new BufferedImage with the target dimensions
        BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());
        
        // Get Graphics2D for high-quality rendering
        Graphics2D g2d = scaledImage.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the scaled image
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        
        return scaledImage;
    }
    
    /**
     * Create a fallback error image when loading fails
     * @return A simple red square as error indicator
     */
    private BufferedImage createErrorImage() {
        BufferedImage errorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = errorImage.createGraphics();
        g2d.setColor(java.awt.Color.RED);
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(java.awt.Color.WHITE);
        g2d.drawString("ERR", 5, 20);
        g2d.dispose();
        return errorImage;
    }
    
    /**
     * Clear all cached images to free memory
     */
    public void clearCache() {
        imageCache.clear();
        scaledImageCache.clear();
    }
    
    /**
     * Get cache statistics for debugging
     */
    public String getCacheStats() {
        return String.format("Original images cached: %d, Scaled images cached: %d", 
                            imageCache.size(), scaledImageCache.size());
    }
    
    /**
     * Preload commonly used images for better performance
     */
    public void preloadGameImages() {
        // Preload all game images at startup
        loadImage(Global.IMG_PLAYER);
        loadImage(Global.IMG_ENEMY);
        loadImage(Global.IMG_SHOT);
        loadImage(Global.IMG_SHOT2);
        loadImage(Global.IMG_SHOT3);
        loadImage(Global.IMG_SHOT4);
        loadImage(Global.IMG_EXPLOSION);
        loadImage(Global.IMG_TITLE);
        loadImage(Global.IMG_POWERUP_SPEEDUP);
        loadImage(Global.IMG_POWERUP_SHOTUP);
        
        // Preload scaled versions with the global scale factor
        loadScaledImage(Global.IMG_PLAYER, Global.SCALE_FACTOR);
        loadScaledImage(Global.IMG_ENEMY, Global.SCALE_FACTOR);
        loadScaledImage(Global.IMG_EXPLOSION, Global.SCALE_FACTOR);
        
        System.out.println("Game images preloaded. " + getCacheStats());
    }
}
