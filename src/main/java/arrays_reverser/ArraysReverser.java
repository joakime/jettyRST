package arrays_reverser;

import java.util.*;
import java.util.stream.Collectors;

public class ArraysReverser {
  static List<List<Integer>> reverse(List<List<Integer>> sequences) {
    sequences.stream().forEach(Collections::reverse);
    return sequences;
  }
}
