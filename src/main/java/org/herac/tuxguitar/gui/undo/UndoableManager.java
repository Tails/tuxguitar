/*
 * Created on 08-ago-2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.undo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author julian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class UndoableManager {
  private static final int LIMIT = 100;
  private final List<UndoableEdit> edits = new ArrayList<UndoableEdit>();
  private int indexOfNextAdd = 0;

  public synchronized void addEdit(UndoableEdit anEdit) {
    checkForUnused();
    checkForLimit();
    this.edits.add(this.indexOfNextAdd, anEdit);
    this.indexOfNextAdd++;
  }

  public synchronized boolean canRedo() {
    boolean canRedo = false;
    UndoableEdit edit = editToBeRedone();
    if (edit != null) {
      canRedo = edit.canRedo();
    }
    return canRedo;
  }

  public synchronized boolean canUndo() {
    boolean canUndo = false;
    UndoableEdit edit = editToBeUndone();
    if (edit != null) {
      canUndo = edit.canUndo();
    }
    return canUndo;
  }

  private void checkForLimit() {
    while (this.edits.size() >= LIMIT) {
      UndoableEdit edit = (UndoableEdit) this.edits.get(0);
      remove(edit);
      this.indexOfNextAdd--;
    }
  }

  private void checkForUnused() {
    while (this.edits.size() > this.indexOfNextAdd) {
      UndoableEdit edit = (UndoableEdit) this.edits.get(this.indexOfNextAdd);
      remove(edit);
    }
  }

  public void discardAllEdits() {
    this.indexOfNextAdd = 0;
    this.edits.clear();
  }

  private UndoableEdit editToBeRedone() {
    int index = this.indexOfNextAdd;
    if (index >= 0 && index < this.edits.size()) {
      return this.edits.get(index);
    }
    return null;
  }

  private UndoableEdit editToBeUndone() {
    int index = this.indexOfNextAdd - 1;
    if (index >= 0 && index < this.edits.size()) {
      return this.edits.get(index);
    }
    return null;
  }

  public synchronized void redo() throws CannotRedoException {
    UndoableEdit edit = editToBeRedone();
    if (edit == null) {
      throw new CannotRedoException();
    }
    try {
      edit.redo();
    } catch (Throwable throwable) {
      throw new CannotRedoException();
    }
    this.indexOfNextAdd++;
  }

  private void remove(UndoableEdit edit) {
    this.edits.remove(edit);
  }

  public synchronized void undo() throws CannotUndoException {
    UndoableEdit edit = editToBeUndone();
    if (edit == null) {
      throw new CannotUndoException();
    }
    try {
      edit.undo();
    } catch (Throwable throwable) {
      throw new CannotUndoException(throwable);
    }
    this.indexOfNextAdd--;
  }

}
