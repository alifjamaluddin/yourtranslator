package com.yourtranslator.app.ocr;

public abstract class LuminanceSource {

  private final int width;
  private final int height;

  protected LuminanceSource(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Fetches one row of luminance data from the underlying platform's bitmap. Values range from
   * 0 (black) to 255 (white). Because Java does not have an unsigned byte type, callers will have
   * to bitwise and with 0xff for each value. It is preferable for implementations of this method
   * to only fetch this row rather than the whole image, since no 2D Readers may be installed and
   * getMatrix() may never be called.
   *
   * @param y The row to fetch, 0 <= y < getHeight().
   * @param row An optional preallocated array. If null or too small, it will be ignored.
   *            Always use the returned object, and ignore the .length of the array.
   * @return An array containing the luminance data.
   */
  public abstract byte[] getRow(int y, byte[] row);

  /**
   * Fetches luminance data for the underlying bitmap. Values should be fetched using:
   * int luminance = array[y * width + x] & 0xff;
   *
   * @return A row-major 2D array of luminance values. Do not use result.length as it may be
   *         larger than width * height bytes on some platforms. Do not modify the contents
   *         of the result.
   */
  public abstract byte[] getMatrix();

  /**
   * @return The width of the bitmap.
   */
  public final int getWidth() {
    return width;
  }

  /**
   * @return The height of the bitmap.
   */
  public final int getHeight() {
    return height;
  }

  /**
   * @return Whether this subclass supports cropping.
   */
  public boolean isCropSupported() {
    return true;
  }

  /**
   * Returns a new object with cropped image data. Implementations may keep a reference to the
   * original data rather than a copy. Only callable if isCropSupported() is true.
   *
   * @param left The left coordinate, 0 <= left < getWidth().
   * @param top The top coordinate, 0 <= top <= getHeight().
   * @param width The width of the rectangle to crop.
   * @param height The height of the rectangle to crop.
   * @return A cropped version of this object.
   */
  public LuminanceSource crop(int left, int top, int width, int height) {
    throw new RuntimeException("This luminance source does not support cropping.");
  }

  /**
   * @return Whether this subclass supports counter-clockwise rotation.
   */
  public boolean isRotateSupported() {
    return false;
  }

  /**
   * Returns a new object with rotated image data. Only callable if isRotateSupported() is true.
   *
   * @return A rotated version of this object.
   */
  public LuminanceSource rotateCounterClockwise() {
    throw new RuntimeException("This luminance source does not support rotation.");
  }

}
