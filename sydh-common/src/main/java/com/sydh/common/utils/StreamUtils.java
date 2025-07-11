/*     */ package com.sydh.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamUtils {
    public static <E> List<E> filter(Collection<E> collection, Predicate<E> function) {
        return (List)(CollUtil.isEmpty(collection) ? CollUtil.newArrayList(new Object[0]) : (List)collection.stream().filter(function).collect(Collectors.toList()));
    }

    public static <E> E findFirst(Collection<E> collection, Predicate<E> function) {
        return CollUtil.isEmpty(collection) ? null : collection.stream().filter(function).findFirst().orElse((E) null);
    }

    public static <E> Optional<E> findAny(Collection<E> collection, Predicate<E> function) {
        return CollUtil.isEmpty(collection) ? Optional.empty() : collection.stream().filter(function).findAny();
    }

    public static <E> String join(Collection<E> collection, Function<E, String> function) {
        return join(collection, function, ",");
    }

    public static <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        return CollUtil.isEmpty(collection) ? "" : (String)collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }

    public static <E> List<E> sorted(Collection<E> collection, Comparator<E> comparing) {
        return (List)(CollUtil.isEmpty(collection) ? CollUtil.newArrayList(new Object[0]) : (List)collection.stream().filter(Objects::nonNull).sorted(comparing).collect(Collectors.toList()));
    }

    public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
        return (Map)(CollUtil.isEmpty(collection) ? MapUtil.newHashMap() : (Map)collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(key, Function.identity(), (var0, var1) -> {
            return var0;
        })));
    }

    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        return (Map)(CollUtil.isEmpty(collection) ? MapUtil.newHashMap() : (Map)collection.stream().filter(Objects::nonNull).collect(Collectors.toMap(key, value, (var0, var1) -> {
            return var0;
        })));
    }

    public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
        return (Map)(CollUtil.isEmpty(collection) ? MapUtil.newHashMap() : (Map)collection.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(key, LinkedHashMap::new, Collectors.toList())));
    }

    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
        return (Map)(CollUtil.isEmpty(collection) ? MapUtil.newHashMap() : (Map)collection.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(key1, LinkedHashMap::new, Collectors.groupingBy(key2, LinkedHashMap::new, Collectors.toList()))));
    }

    public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
        return (Map)(!CollUtil.isEmpty(collection) && key1 != null && key2 != null ? (Map)collection.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(key1, LinkedHashMap::new, Collectors.toMap(key2, Function.identity(), (var0, var1) -> {
            return var0;
        }))) : MapUtil.newHashMap());
    }

    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        return (List)(CollUtil.isEmpty(collection) ? CollUtil.newArrayList(new Object[0]) : (List)collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        return (Set)(!CollUtil.isEmpty(collection) && function != null ? (Set)collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet()) : CollUtil.newHashSet(new Object[0]));
    }

    public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
        if (MapUtil.isEmpty((Map)map1) && MapUtil.isEmpty((Map)map2)) {
            return MapUtil.newHashMap();
        } else {
            if (MapUtil.isEmpty((Map)map1)) {
                map1 = MapUtil.newHashMap();
            } else if (MapUtil.isEmpty((Map)map2)) {
                map2 = MapUtil.newHashMap();
            }

            HashSet var3 = new HashSet();
            var3.addAll(((Map)map1).keySet());
            var3.addAll(((Map)map2).keySet());
            HashMap var4 = new HashMap();
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
                Object var6 = var5.next();
                Object var7 = ((Map)map1).get(var6);
                Object var8 = ((Map)map2).get(var6);
                Object var9 = merge.apply((X) var7, (Y) var8);
                if (var9 != null) {
                    var4.put(var6, var9);
                }
            }

            return var4;
        }
    }

    private StreamUtils() {
    }
}
