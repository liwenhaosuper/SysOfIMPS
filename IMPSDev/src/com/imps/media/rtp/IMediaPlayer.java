/***********************************************************************
 * Module:  IMediaPlayer.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class IMediaPlayer
 ***********************************************************************/
package com.imps.media.rtp;


/** @pdOid 20f26e05-e3b0-45fc-94af-457db3bcf04d */
public class IMediaPlayer {
   /** @pdRoleInfo migr=no name=IMediaEventListener assc=association1 coll=java.util.Collection impl=java.util.HashSet mult=1..* */
   public java.util.Collection<IMediaEventListener> iMediaEventListener;
   
   
   /** @pdGenerated default getter */
   public java.util.Collection<IMediaEventListener> getIMediaEventListener() {
      if (iMediaEventListener == null)
         iMediaEventListener = new java.util.HashSet<IMediaEventListener>();
      return iMediaEventListener;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorIMediaEventListener() {
      if (iMediaEventListener == null)
         iMediaEventListener = new java.util.HashSet<IMediaEventListener>();
      return iMediaEventListener.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newIMediaEventListener */
   public void setIMediaEventListener(java.util.Collection<IMediaEventListener> newIMediaEventListener) {
      removeAllIMediaEventListener();
      for (java.util.Iterator iter = newIMediaEventListener.iterator(); iter.hasNext();)
         addIMediaEventListener((IMediaEventListener)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newIMediaEventListener */
   public void addIMediaEventListener(IMediaEventListener newIMediaEventListener) {
      if (newIMediaEventListener == null)
         return;
      if (this.iMediaEventListener == null)
         this.iMediaEventListener = new java.util.HashSet<IMediaEventListener>();
      if (!this.iMediaEventListener.contains(newIMediaEventListener))
         this.iMediaEventListener.add(newIMediaEventListener);
   }
   
   /** @pdGenerated default remove
     * @param oldIMediaEventListener */
   public void removeIMediaEventListener(IMediaEventListener oldIMediaEventListener) {
      if (oldIMediaEventListener == null)
         return;
      if (this.iMediaEventListener != null)
         if (this.iMediaEventListener.contains(oldIMediaEventListener))
            this.iMediaEventListener.remove(oldIMediaEventListener);
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllIMediaEventListener() {
      if (iMediaEventListener != null)
         iMediaEventListener.clear();
   }

}