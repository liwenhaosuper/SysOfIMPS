package com.imps.media.video.core;
/**
 * 
 * @author liwenhaosuper
 *
 */
public abstract class IVideoEventListener {
    /**
     * 
     * @param paramLong
     */
	  public abstract void onAcceptedVideo(long paramLong);
     /**
      * 
      * @param paramLong
      */
	  public abstract void onCancelRequest(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onChannelReady(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onCloseVideo(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onFriendOffline(long paramLong);
      /**
       * 
       * @param paramLong1
       * @param paramLong2
       */
	  public abstract void onNetworkBufferFull(long paramLong1, long paramLong2);
  /**
   * 
   * @param paramLong1
   * @param paramLong2
   */
	  public abstract void onNetworkError(long paramLong1, long paramLong2);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onNetworkTimeOut(long paramLong);

	  public abstract void onOperationTimeOut(long paramLong);
      /**
       * 
       * @param paramLong
       * @param paramArrayOfByte
       * @param paramInt
       */
	  public abstract void onRecvVideoData(long paramLong, byte[] paramArrayOfByte, int paramInt);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onRejectVideo(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onRequestVideo(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onUnknownReasonClosed(long paramLong);
      /**
       * 
       * @param paramLong
       */
	  public abstract void onVideoConflict(long paramLong);
}
