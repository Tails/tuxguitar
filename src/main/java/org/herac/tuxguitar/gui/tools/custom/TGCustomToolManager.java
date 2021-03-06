package org.herac.tuxguitar.gui.tools.custom;

import java.util.ArrayList;
import java.util.List;

public class TGCustomToolManager {

  private static TGCustomToolManager instance;

  public static TGCustomToolManager instance() {
    if (instance == null) {
      instance = new TGCustomToolManager();
    }
    return instance;
  }

  private List<TGCustomTool> tools;

  public TGCustomToolManager() {
    this.tools = new ArrayList<TGCustomTool>();
  }

  public void addCustomTool(TGCustomTool tool) {
    this.tools.add(tool);
  }

  public List<TGCustomTool> getCustomTools() {
    return this.tools;
  }

  public void removeCustomTool(TGCustomTool tool) {
    this.tools.remove(tool);
  }

}
