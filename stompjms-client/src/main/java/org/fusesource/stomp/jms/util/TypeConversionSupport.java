/**
 * Copyright (C) 2010-2011, FuseSource Corp.  All rights reserved.
 *
 *     http://fusesource.com
 *
 * The software in this package is published under the terms of the
 * CDDL license a copy of which has been included with this distribution
 * in the license.txt file.
 */

package org.fusesource.stomp.jms.util;

import org.fusesource.stomp.jms.StompJmsConnection;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.StompJmsQueue;

import java.util.Date;
import java.util.HashMap;

public final class TypeConversionSupport {

    static class ConversionKey {
        final Class from;
        final Class to;
        final int hashCode;

        public ConversionKey(Class from, Class to) {
            this.from = from;
            this.to = to;
            this.hashCode = from.hashCode() ^ (to.hashCode() << 1);
        }

        public boolean equals(Object o) {
            ConversionKey x = (ConversionKey) o;
            return x.from == from && x.to == to;
        }

        public int hashCode() {
            return hashCode;
        }
    }

    interface Converter {
        Object convert(StompJmsConnection connection, Object value);
    }

    private static final HashMap<ConversionKey, Converter> CONVERSION_MAP = new HashMap<ConversionKey, Converter>();

    static {
        Converter toStringConverter = new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return value.toString();
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Boolean.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Byte.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Integer.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Long.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Float.class, String.class), toStringConverter);
        CONVERSION_MAP.put(new ConversionKey(Double.class, String.class), toStringConverter);

        CONVERSION_MAP.put(new ConversionKey(String.class, Boolean.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Boolean.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Byte.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Byte.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Short.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Short.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Integer.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Integer.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Long.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Long.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Float.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Float.valueOf((String) value);
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, Double.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Double.valueOf((String) value);
            }
        });

        Converter longConverter = new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Long.valueOf(((Number) value).longValue());
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Byte.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Integer.class, Long.class), longConverter);
        CONVERSION_MAP.put(new ConversionKey(Date.class, Long.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Long.valueOf(((Date) value).getTime());
            }
        });

        Converter intConverter = new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Integer.valueOf(((Number) value).intValue());
            }
        };
        CONVERSION_MAP.put(new ConversionKey(Byte.class, Integer.class), intConverter);
        CONVERSION_MAP.put(new ConversionKey(Short.class, Integer.class), intConverter);

        CONVERSION_MAP.put(new ConversionKey(Byte.class, Short.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return Short.valueOf(((Number) value).shortValue());
            }
        });

        CONVERSION_MAP.put(new ConversionKey(Float.class, Double.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return new Double(((Number) value).doubleValue());
            }
        });
        CONVERSION_MAP.put(new ConversionKey(String.class, StompJmsDestination.class), new Converter() {
            public Object convert(StompJmsConnection connection, Object value) {
                return new StompJmsQueue(connection, value.toString());
            }
        });
    }

    private TypeConversionSupport() {
    }

    public static Object convert(StompJmsConnection connection, Object value, Class clazz) {

        assert value != null && clazz != null;

        if (value.getClass() == clazz) {
            return value;
        }

        Converter c = CONVERSION_MAP.get(new ConversionKey(value.getClass(), clazz));
        if (c == null) {
            return null;
        }
        return c.convert(connection, value);

    }

}
