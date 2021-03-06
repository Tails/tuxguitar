package org.herac.tuxguitar.gui.tools.browser;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.herac.tuxguitar.gui.editors.chord.ChordSelector;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserConnection {

  private TGBrowser browser;
  private TGBrowserConnectionHandler handler;
  private boolean locked;

  public TGBrowserConnection(TGBrowserConnectionHandler handler) {
    this.handler = handler;
  }
  

  /** The Logger for this class. */
  public static final transient Logger LOG = Logger
      .getLogger(TGBrowserConnection.class);
  

  public void cd(final int callId, final TGBrowserElement element) {
    if (!isLocked()) {
      this.lock();
      new Thread(new Runnable() {
        public void run() {
          try {
            if (isOpen()) {
              getBrowser().cdElement(element);
              notifyCd(callId);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public void cdRoot(final int callId) {
    if (!isLocked()) {
      this.lock();
      new Thread(new Runnable() {
        public void run() {
          try {
            if (isOpen()) {
              getBrowser().cdRoot();
              notifyCd(callId);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public void cdUp(final int callId) {
    if (!isLocked()) {
      this.lock();
      new Thread(new Runnable() {
        public void run() {
          try {
            if (isOpen()) {
              getBrowser().cdUp();
              notifyCd(callId);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public void close(final int callId) {
    if (!isLocked()) {
      this.lock();
      final TGBrowser browser = getBrowser();
      this.browser = null;
      new Thread(new Runnable() {
        public void run() {
          try {
            if (browser != null) {
              browser.close();
              notifyClosed(callId);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public TGBrowser getBrowser() {
    return this.browser;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public boolean isOpen() {
    return (getBrowser() != null);
  }

  public void listElements(final int callId) {
    if (!isLocked()) {
      this.lock();
      new Thread(new Runnable() {
        public void run() {
          try {
            if (isOpen()) {
              List<TGBrowserElement> elements = getBrowser().listElements();
              notifyElements(callId, elements);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  protected void lock() {
    this.locked = true;
    this.handler.notifyLockStatusChanged();
  }

  public void notifyCd(final int callId) {
    this.handler.notifyCd(callId);
  }

  public void notifyClosed(final int callId) {
    this.handler.notifyClosed(callId);
  }

  public void notifyElements(final int callId, List<TGBrowserElement> elements) {
    this.handler.notifyElements(callId, elements);
  }

  public void notifyError(final int callId, Throwable throwable) {
    this.handler.notifyError(callId, throwable);
  }

  public void notifyOpened(final int callId) {
    this.handler.notifyOpened(callId);
  }

  public void notifyStream(final int callId, InputStream stream,
      TGBrowserElement element) {
    this.handler.notifyStream(callId, stream, element);
  }

  public void open(final int callId, final TGBrowser browser) {
    if (!isLocked()) {
      this.close(callId);
      this.lock();
      this.browser = browser;
      new Thread(new Runnable() {
        public void run() {
          try {
            if (getBrowser() != null) {
              getBrowser().open();
              notifyOpened(callId);
            } else {
              notifyClosed(callId);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public void openStream(final int callId, final TGBrowserElement element) {
    if (!isLocked()) {
      this.lock();
      new Thread(new Runnable() {
        public void run() {
          try {
            if (element == null) {
              release();
              return;
            }
            if (element.isFolder()) {
              release();
              cd(callId, element);
            } else {
              InputStream stream = element.getInputStream();
              notifyStream(callId, stream, element);
            }
          } catch (TGBrowserException e) {
            notifyError(callId, e);
            LOG.error(e);
          }
        }
      }).start();
    }
  }

  public void release() {
    this.unlock();
  }

  protected void unlock() {
    this.locked = false;
    this.handler.notifyLockStatusChanged();
  }
}
