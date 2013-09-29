
public class Workshop2 {
	/**
	 * Implements the negative transformation.
	 *
	 * @param img the graylevel image (row major representation)
	 */
	public void negativeTransformation(byte[] img) {
		for( int i = 0; i < img.length; i++)
			img[i] = (byte)(255 - img[i] & 0xFF);
	}
	
	/**
	 * Calculates the histogram of the image.
	 * @param img the graylevel image (row major representation)
	 * @return the histogram
	 */
	public int[] histogram (byte[] img) {
		int[] returnValue = new int[256];
		
		for (int i = 0; i < img.length; i++)
			returnValue[img[i] & 0xFF]++;
		
		return returnValue;
	}
	
	/**
	 * Performes histogram equalization.
	 * @param img
	 */
	public void histogramEqualization(byte img[]) {
		
		int[] histogram = new int[256];
		double[] cdf = new double[256];
		int pixelNum = img.length;
		
		for (int i = 0; i < img.length; i++)
			histogram[img[i] & 0xFF]++;
					
		for (int j = 0; j < histogram.length; j++)
		{
			cdf[j] = (double)histogram[j] / pixelNum;
		}	
		
		for (int m = 1; m < cdf.length; m++)
			cdf[m] += cdf[m-1];
		
		for (int k = 0; k < img.length; k++)
		{
			img[k] = (byte)((int)(255*cdf[img[k] & 0xFF]));
				
		}		
	
	}
}
