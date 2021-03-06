/*
 * Created on 17-dic-2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.duration;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;

/**
 * @author julian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeDivisionTypeAction extends Action {
  private static final TGDivisionType DEFAULT_DIVISION_TYPE = new TGDivisionType(
      3, 2);

  public static final String NAME = "action.note.duration.change-division-type";

  private static final TGDivisionType NO_TUPLET = new TGDivisionType(1, 1);

  public ChangeDivisionTypeAction() {
    super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING
        | KEY_BINDING_AVAILABLE);
  }

  @Override
  protected int execute(TypedEvent e) {
    // comienza el undoable
    UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();

    boolean isKeyEvent = false;
    if (e instanceof KeyEvent) {
      isKeyEvent = true;
    }
    if (!isKeyEvent) {
      TGDivisionType divisionType = DEFAULT_DIVISION_TYPE;
      if (e.widget.getData() != null
          && e.widget.getData() instanceof TGDivisionType) {
        divisionType = (TGDivisionType) e.widget.getData();
      }

      if (getSelectedDuration().getDivision().isEqual(divisionType)) {
        setDivisionType(NO_TUPLET);
      } else {
        setDivisionType(divisionType);
      }
    } else {
      if (getSelectedDuration().getDivision().isEqual(TGDivisionType.NORMAL)) {
        setDivisionType(DEFAULT_DIVISION_TYPE);
      } else {
        setDivisionType(NO_TUPLET);
      }
    }
    setDurations();

    // termia el undoable
    addUndoableEdit(undoable.endUndo());

    return 0;
  }

  public TGDuration getSelectedDuration() {
    return getEditor().getTablature().getCaret().getDuration();
  }

  private void setDivisionType(TGDivisionType divisionType) {
    getSelectedDuration().setDivision(divisionType);
  }

  private void setDurations() {
    Caret caret = getEditor().getTablature().getCaret();
    caret.changeDuration(getSelectedDuration().clone());
    TuxGuitar.instance().getFileHistory().setUnsavedFile();
    fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
  }
}
