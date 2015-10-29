package Moteur;

import java.util.Comparator;
import java.util.Map;

public class MapValueComparator<K, V> implements Comparator<K>{

	  private final Map<K, V> mapToSort;
	  private final Comparator<V> valueComparator;

	  public MapValueComparator(Map<K, V> mapToSort, Comparator<V> valueComparator){
	     this.mapToSort = mapToSort;
	  this.valueComparator = valueComparator;
	  }

	   @Override
	  public int compare(K key1, K key2) {
	  return valueComparator.compare(mapToSort.get(key1), mapToSort.get(key2));
	}
	}