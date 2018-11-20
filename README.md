# SortVisualizer

Visualizes sorting algorithms. Supported algorithms:

- BogoSort
- BubbleSort
- CombSort
- HeapSort
- InsertionSort
- MergeSort
- MergeSort (Natural)
- MergeSort (Extended Runs)
- MergeSort (Iterative)
- MergeSort (Iterative2)
- QuickSort
- QuickSort (Iterative)
- RadixSort (LSD)
- RadixSort (MSD)
- SelectionSort
- ShakerSort
- ShakerSort (Duplicates)
- ShellSort
- SlowSort
- SmoothSort


## Dependencies

- Java 11
- `$JAVA_HOME` set to JDK home

## Program Arguments

None or two integers: `numValues`, `upperBound`


## Building and Launching w/o Maven

Build and launch:

```
./bin/build.sh && ./bin/launch
```

### Direct launching
```
# Example: launch directly
java --module-path mods:libs --module kn.uni.dbis.cs.sorting

# Example: launch with arguments
java --module-path mods:libs --module kn.uni.dbis.cs.sorting 128 100

# Example: launch with alternative main class
java --module-path mods:libs --module kn.uni.dbis.cs.sorting/kn.uni.dbis.cs.sorting.gui.Main
```

### Modules?

- Java modules introduced in Java 9.

Inspect module:
```
java --module-path mods --describe-module kn.uni.dbis.cs.sorting

java --module-path mods --list-modules
```

List dependencies:
```
jdeps -summary -recursive --module-path mods:libs --module kn.uni.dbis.cs.sorting
```
