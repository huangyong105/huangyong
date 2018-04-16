package zlib.set;

import java.util.HashMap;
import java.util.Map;

public final class MapThreadLocal extends ThreadLocal
{
  private static final ThreadLocal instance = new MapThreadLocal();

  public static Map getMap()
  {
    return ((Map)(Map)instance.get());
  }

  protected Map initialValue()
  {
    return new HashMap();
  }
}
