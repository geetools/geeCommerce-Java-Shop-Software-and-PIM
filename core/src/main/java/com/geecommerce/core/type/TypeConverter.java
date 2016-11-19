package com.geecommerce.core.type;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Value;
import com.geecommerce.core.util.DateTimes;

public class TypeConverter {
    public static final <T> T convert(Class<T> type, Object object) {
        return convert(type, null, object);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> T convert(Class<T> type, Type genericType, Object object) {
        Object ret = null;

        if (BigDecimal.class == type) {
            ret = asBigDecimal(object);
        } else if (BigInteger.class == type) {
            ret = asBigInteger(object);
        } else if (Boolean.class == type || boolean.class == type) {
            ret = asBoolean(object);
        } else if (byte[].class == type) {
            ret = asBytes(object);
        } else if (Date.class == type) {
            ret = asDate(object);
        } else if (Double.class == type || double.class == type) {
            ret = asDouble(object);
        } else if (type.isEnum()) {
            ret = asEnum((Class<Enum>) type, object);
        } else if (Float.class == type || float.class == type) {
            ret = asFloat(object);
        } else if (Id.class == type) {
            ret = asId(object);
        } else if (Integer.class == type || int.class == type) {
            ret = asInteger(object);
        } else if (Long.class == type || long.class == type) {
            ret = asLong(object);
        } else if (Short.class == type || short.class == type) {
            ret = asShort(object);
        } else if (String.class == type) {
            ret = asString(object);
        } else if (UUID.class == type) {
            ret = asUUID(object);
        } else if (ContextObject.class == type) {
            ret = asContextObject(object);
        } else if (List.class == type) {
            List<Class<?>> genTypes = Reflect.getGenericType(genericType);

            Class<?> genType = genTypes != null && genTypes.size() > 0 ? genTypes.get(0) : null;

            if (genType != null) {
                if (genType == Id.class) {
                    ret = asIdList(object);
                } else if (genType == ContextObject.class) {
                    ret = asListOfContextObjects(object);
                } else if (genType.isEnum()) {
                    ret = asListOfEnums((Class<Enum>) genType, object);
                } else {
                    ret = asList(object);
                }
            } else {
                ret = asList(object);
            }
        } else if (Set.class == type) {
            List<Class<?>> genTypes = Reflect.getGenericType(genericType);

            Class<?> genType = genTypes != null && genTypes.size() > 0 ? genTypes.get(0) : null;

            if (genType != null) {
                if (genType == Id.class) {
                    ret = asIdSet(object);
                } else if (genType.isEnum()) {
                    ret = asSetOfEnums((Class<Enum>) genType, object);
                } else {
                    ret = asSet(object);
                }
            } else {
                ret = asSet(object);
            }
        } else if (Map.class == type) {
            List<Class<?>> genTypes = Reflect.getGenericType(genericType);

            if (genTypes != null && genTypes.size() > 1) {
                Class<?> keyGenType = genTypes.get(0);
                Class<?> valGenType = genTypes.get(1);

                if (valGenType == List.class) {
                    if (genTypes.size() == 3) {
                        Class<?> listGenType = genTypes.get(2);

                        if (listGenType == Id.class) {
                            ret = asIdListsInMap(object);
                        }
                    } else if (genTypes.size() == 2 && keyGenType == Object.class && valGenType == Object.class) {
                        ret = asObjectMap(object);
                    }
                }
            }

            if (ret == null) {
                ret = asMap(object);
            }
        }

        return (T) ret;
    }

    public static final byte[] asBytes(Object object) {
        if (isNull(object))
            return null;

        Object value = asRaw(object);

        if (value instanceof byte[]) {
            return (byte[]) value;
        } else if (value instanceof String) {
            return ((String) value).getBytes();
        }

        return null;
    }

    public static final Id asId(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Id) {
            return (Id) object;
        } else if (object instanceof Value) {
            return new Id(((Value) object).getLong());
        } else if (object instanceof Number) {
            return new Id((Number) object);
        } else if (object instanceof String) {
            return Id.parseId((String) object);
        }

        return null;
    }

    public static final String asString(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Value) {
            Object value = ((Value) object).getRaw();

            if (value instanceof byte[]) {
                try {
                    return new String((byte[]) value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return String.valueOf(value);
            }
        } else if (object instanceof byte[]) {
            try {
                return new String((byte[]) object, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return String.valueOf(object);
        }
    }

    public static final Integer asInteger(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Value) {
            return ((Value) object).getInteger();
        } else if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return Integer.parseInt((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Integer.class.getName());
        }
    }

    public static final Long asLong(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Long) {
            return (Long) object;
        } else if (object instanceof Value) {
            return ((Value) object).getLong();
        } else if (object instanceof Number) {
            return ((Number) object).longValue();
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return Long.parseLong((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Long.class.getName());
        }
    }

    public static final Double asDouble(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Double) {
            return (Double) object;
        } else if (object instanceof Value) {
            return ((Value) object).getDouble();
        } else if (object instanceof Number) {
            return ((Number) object).doubleValue();
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return Double.parseDouble(((String) object).replace(',', '.'));
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Double.class.getName());
        }
    }

    public static final Float asFloat(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Float) {
            return (Float) object;
        } else if (object instanceof Value) {
            return ((Value) object).getFloat();
        } else if (object instanceof Number) {
            return ((Number) object).floatValue();
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return Float.parseFloat(((String) object).replace(',', '.'));
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Float.class.getName());
        }
    }

    public static final Short asShort(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Short) {
            return (Short) object;
        } else if (object instanceof Value) {
            return ((Value) object).getShort();
        } else if (object instanceof Number) {
            return ((Number) object).shortValue();
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return Short.parseShort((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Short.class.getName());
        }
    }

    public static final Boolean asBoolean(Object object) {
        if (isNull(object))
            return Boolean.FALSE;

        if (object instanceof Value) {
            return ((Value) object).getBoolean();
        } else if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof Number) {
            int n = ((Number) object).intValue();

            if (n != 0 && n != 1) {
                throw new IllegalArgumentException("If boolean is stored as an int then it must be '0' or '1'");
            }

            return n == 1 ? true : false;
        } else if (object instanceof String) {
            return Boolean.parseBoolean((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Boolean.class.getName());
        }
    }

    public static final BigDecimal asBigDecimal(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        } else if (object instanceof Value) {
            return ((Value) object).getBigDecimal();
        } else if (object instanceof Number) {
            return new BigDecimal(((Number) object).doubleValue());
        } else if (object instanceof String) {
            return new BigDecimal(((String) object).replace(',', '.'));
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + BigDecimal.class.getName());
        }
    }

    public static final BigInteger asBigInteger(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof BigInteger) {
            return (BigInteger) object;
        } else if (object instanceof Value) {
            return ((Value) object).getBigInteger();
        } else if (object instanceof Number) {
            return new BigInteger(String.valueOf((Number) object));
        } else if (object instanceof String) {
            return new BigInteger((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + BigInteger.class.getName());
        }
    }

    public static final Character asCharacter(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getChar();
        } else if (object instanceof Character) {
            return (Character) object;
        } else if (object instanceof Number) {
            int n = ((Number) object).intValue();

            return (char) n;
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Character.class.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public static final <E extends Enum<E>> E asEnum(Class<E> enumType, Object object) {
        if (isNull(object))
            return null;

        if (object.getClass().isEnum()) {
            return (E) object;
        } else if (object instanceof Value) {
            return ((Value) object).getEnum(enumType);
        } else if (object instanceof String) {
            try {
                Method m = enumType.getDeclaredMethod("fromString", String.class);
                return (E) m.invoke(null, (String) object);
            } catch (NoSuchMethodException e) {
                return Enum.valueOf(enumType, (String) object);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else if (object instanceof Integer) {
            try {
                Method m = enumType.getDeclaredMethod("fromId", int.class);
                return (E) m.invoke(null, (Integer) object);
            } catch (NoSuchMethodException e) {
                // Try finding by ordinal() if fromId() does not exist.
                E[] enums = enumType.getEnumConstants();

                for (E constant : enums) {
                    if (constant.ordinal() == (Integer) object) {
                        return constant;
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return null;
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + UUID.class.getName());
        }
    }

    @SuppressWarnings("rawtypes")
    public static <E extends Enum<E>> List<E> asListOfEnums(Class<E> enumType, Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Collection) {
            List<E> returnList = new ArrayList<>();

            for (Object o : (Collection) object) {
                E enumVal = asEnum(enumType, o);
                returnList.add(enumVal);
            }

            return returnList;
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    public static <E extends Enum<E>> Set<E> asSetOfEnums(Class<E> enumType, Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Collection) {
            Set<E> returnSet = new HashSet<>();

            for (Object o : (Collection) object) {
                E enumVal = asEnum(enumType, o);
                returnSet.add(enumVal);
            }

            return returnSet;
        }

        return null;
    }

    public static final UUID asUUID(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getUUID();
        } else if (object instanceof String) {
            return UUID.fromString((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + UUID.class.getName());
        }
    }

    public static final <T> ContextObject<T> asContextObject(Object object) {
        return asContextObject(object, false);
    }

    @SuppressWarnings({ "unchecked" })
    public static final <T> ContextObject<T> asContextObject(Object object, boolean containsIdList) {
        if (isNull(object))
            return null;

        ContextObject<T> co = null;

        // Value given is not a context object.
//        if (!(object instanceof List && !((List) object).isEmpty() && Map.class.isAssignableFrom(((List) object).get(0).getClass()))) {
//            if (object instanceof Collection) {
//                if (containsIdList) {
//                    co = (ContextObject<T>) ContextObjects.global(asIdList(object));
//                } else {
//                    co = (ContextObject<T>) ContextObjects.global(asList(object));
//                }
//            } else if (object instanceof Map) {
//                co = (ContextObject<T>) ContextObjects.global(asMap(object));
//            } else {
//                co = (ContextObject<T>) ContextObjects.global(asRaw(object));
//            }
//
//            System.out.println("--------- CONVETED PLAIN VALUE: " + object + " - to contextobject - " + co);
//
//            return co;
//        }

        if (object instanceof List) {
            List<Map<String, Object>> listOfMaps = (List<Map<String, Object>>) object;

            if (listOfMaps != null && listOfMaps.size() > 0) {
                // TODO: Temporary hack for backwards compatibility!!
//                if (listOfMaps.get(0) != null && listOfMaps.get(0) instanceof List) {
//                    listOfMaps = (List<Map<String, Object>>) listOfMaps.get(0);
//                }

                co = new ContextObject<>();

                for (Map<String, Object> innerMap : listOfMaps) {
                    if (innerMap != null && innerMap.size() > 0) {
                        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

                        if (innerMap.get(ContextObject.MERCHANT) != null)
                            returnMap.put(ContextObject.MERCHANT, asId(innerMap.get(ContextObject.MERCHANT)));

                        if (innerMap.get(ContextObject.STORE) != null)
                            returnMap.put(ContextObject.STORE, asId(innerMap.get(ContextObject.STORE)));

                        if (innerMap.get(ContextObject.LANGUAGE) != null)
                            returnMap.put(ContextObject.LANGUAGE, asString(innerMap.get(ContextObject.LANGUAGE)));

                        if (innerMap.get(ContextObject.COUNTRY) != null)
                            returnMap.put(ContextObject.COUNTRY, asString(innerMap.get(ContextObject.COUNTRY)));

                        if (innerMap.get(ContextObject.VIEW) != null)
                            returnMap.put(ContextObject.VIEW, asLong(innerMap.get(ContextObject.VIEW)));

                        if (innerMap.get(ContextObject.REQUEST_CONTEXT) != null)
                            returnMap.put(ContextObject.REQUEST_CONTEXT, asId(innerMap.get(ContextObject.REQUEST_CONTEXT)));

                        if (innerMap.get(ContextObject.VALUE) != null) {
                            Object val = innerMap.get(ContextObject.VALUE);

                            if (val instanceof Collection) {
                                if (containsIdList) {
                                    returnMap.put(ContextObject.VALUE, asIdList(val));
                                } else {
                                    returnMap.put(ContextObject.VALUE, asList(val));
                                }
                            } else {
                                returnMap.put(ContextObject.VALUE, asRaw(innerMap.get(ContextObject.VALUE)));
                            }
                        }

                        co.add(returnMap);
                    }
                }
            }

            return co;
        }

        return null;
    }

    @SuppressWarnings({ "unchecked" })
    public static final <T> List<ContextObject<?>> asListOfContextObjects(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof List) {
            List<ContextObject<?>> returnList = new ArrayList<>();

            for (List<Map<String, Value>> mapsInList : (List<List<Map<String, Value>>>) object) {
                ContextObject<?> co = asContextObject(mapsInList);
                returnList.add(co);
            }

            return returnList;
        }

        return null;
    }

    public static final String asASCII(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getString();
        } else if (object instanceof String) {
            return (String) object;
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + String.class.getName());
        }
    }

    public static final Date asDate(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getDate();
        } else if (object instanceof Date) {
            return (Date) object;
        } else if (object instanceof Number) {
            long n = ((Number) object).longValue();

            return new Date(n);
        } else if (object instanceof String) {
            if ("".equals(object.toString().trim()))
                return null;

            return DateTimes.parseDate((String) object);
        } else {
            throw new IllegalArgumentException("Unable to convert object of type " + object.getClass().getName() + " to " + Date.class.getName());
        }
    }

    @SuppressWarnings("rawtypes")
    public static final Map<Object, Object> asObjectMap(Object object) {
        if (isNull(object))
            return null;

        Map fromMap = (Map) object;
        Map<Object, Object> toMap = new LinkedHashMap<>();

        for (Object k : fromMap.keySet()) {
            toMap.put(k, asRaw(fromMap.get(k)));
        }

        return toMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <K, V> Map<K, V> asMap(Object object) {
        if (isNull(object))
            return null;

        Map fromMap = (Map) object;
        Map<K, V> toMap = new LinkedHashMap<>();

        for (Object k : fromMap.keySet()) {
            Object v = fromMap.get(k);

            if (v instanceof List) {
                toMap.put((K) k, (V) asList(v));
            } else if (v instanceof Set) {
                toMap.put((K) k, (V) asSet(v));
            } else if (v instanceof Map) {
                toMap.put((K) k, (V) asMap(v));
            } else {
                toMap.put((K) k, (V) asRaw(v));
            }
        }

        return toMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <K, V> Map<K, V> asIdListsInMap(Object object) {
        if (isNull(object))
            return null;

        Map fromMap = (Map) object;
        Map<K, V> toMap = new LinkedHashMap<>();

        for (Object k : fromMap.keySet()) {
            toMap.put((K) k, (V) asIdList(fromMap.get(k)));
        }

        return toMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <K, V> Map<K, V> asIdSetsInMap(Object object) {
        if (isNull(object))
            return null;

        Map fromMap = (Map) object;
        Map<K, V> toMap = new LinkedHashMap<>();

        for (Object k : fromMap.keySet()) {
            toMap.put((K) k, (V) asIdSet(fromMap.get(k)));
        }

        return toMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <T> List<T> asList(Object object) {
        if (isNull(object))
            return null;

        List<T> toList = new ArrayList<>();

        if (object instanceof Collection) {
            Collection fromCol = (Collection) object;

            for (Object o : fromCol) {
                if (o instanceof Map) {
                    toList.add((T) asMap(o));
                } else if (o instanceof List) {
                    toList.add((T) asList(o));
                } else if (o instanceof Set) {
                    toList.add((T) asSet(o));
                } else {
                    toList.add((T) asRaw(o));
                }
            }
        } else if (object instanceof Map) {
            Map fromMap = (Map) object;

            for (Object k : fromMap.keySet()) {
                Object v = fromMap.get(k);

                if (v instanceof Map) {
                    toList.add((T) asMap(v));
                } else if (v instanceof List) {
                    toList.add((T) asList(v));
                } else if (v instanceof Set) {
                    toList.add((T) asSet(v));
                } else {
                    toList.add((T) asRaw(v));
                }
            }
        } else if (object instanceof Object[]) {
            Object[] fromArray = (Object[]) object;

            for (Object o : fromArray) {
                if (o instanceof Map) {
                    toList.add((T) asMap(o));
                } else if (o instanceof List) {
                    toList.add((T) asList(o));
                } else if (o instanceof Set) {
                    toList.add((T) asSet(o));
                } else {
                    toList.add((T) asRaw(o));
                }
            }
        }

        return toList;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final <T> Set<T> asSet(Object object) {
        if (isNull(object))
            return null;

        Set<T> toSet = new HashSet<>();

        if (object instanceof Collection) {
            Collection fromCol = (Collection) object;

            for (Object o : fromCol) {
                if (o instanceof Map) {
                    toSet.add((T) asMap(o));
                } else if (o instanceof List) {
                    toSet.add((T) asList(o));
                } else if (o instanceof Set) {
                    toSet.add((T) asSet(o));
                } else {
                    toSet.add((T) asRaw(o));
                }
            }
        } else if (object instanceof Map) {
            Map fromMap = (Map) object;

            for (Object k : fromMap.keySet()) {
                Object v = fromMap.get(k);

                if (v instanceof Map) {
                    toSet.add((T) asMap(v));
                } else if (v instanceof List) {
                    toSet.add((T) asList(v));
                } else if (v instanceof Set) {
                    toSet.add((T) asSet(v));
                } else {
                    toSet.add((T) asRaw(v));
                }
            }
        } else if (object instanceof Object[]) {
            Object[] fromArray = (Object[]) object;

            for (Object o : fromArray) {
                if (o instanceof Map) {
                    toSet.add((T) asMap(o));
                } else if (o instanceof List) {
                    toSet.add((T) asList(o));
                } else if (o instanceof Set) {
                    toSet.add((T) asSet(o));
                } else {
                    toSet.add((T) asRaw(o));
                }
            }
        }

        return toSet;
    }

    @SuppressWarnings({ "rawtypes" })
    public static final List<Id> asIdList(Object object) {
        if (isNull(object))
            return null;

        List<Id> toList = new ArrayList<>();

        if (object instanceof Collection) {
            Collection fromCol = (Collection) object;

            for (Object o : fromCol) {
                toList.add(asId(o));
            }
        } else if (object instanceof Object[]) {
            Object[] fromArray = (Object[]) object;

            for (Object o : fromArray) {
                toList.add(asId(o));
            }
        } else if (object instanceof Map) {
            Map fromMap = (Map) object;

            for (Object k : fromMap.keySet()) {
                toList.add(asId(fromMap.get(k)));
            }
        } else if (object instanceof Id || object instanceof Number || object instanceof String) {
            toList.add(asId(object));
        }

        return toList;
    }

    @SuppressWarnings({ "rawtypes" })
    public static final Set<Id> asIdSet(Object object) {
        if (isNull(object))
            return null;

        Set<Id> toSet = new HashSet<>();

        if (object instanceof Collection) {
            Collection fromCol = (Collection) object;

            for (Object o : fromCol) {
                toSet.add(asId(o));
            }
        } else if (object instanceof Map) {
            Map fromMap = (Map) object;

            for (Object k : fromMap.keySet()) {
                toSet.add(asId(fromMap.get(k)));
            }
        } else if (object instanceof Id || object instanceof Number || object instanceof String) {
            toSet.add(asId(object));
        }

        return toSet;
    }

    public static final Object asRaw(Object object) {
        if (isNull(object))
            return null;

        if (object instanceof Value) {
            return ((Value) object).getRaw();
        } else {
            return object;
        }
    }

    protected static final boolean isNull(Object object) {
        if (object == null)
            return true;

        if (object instanceof Value && ((Value) object).getRaw() == null)
            return true;

        return false;
    }
}
