/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.torod.db.postgresql.converters.array;

import com.torodb.torod.db.postgresql.converters.jsonb.NullValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.ArrayValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.DateValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.TimeValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.LongValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.StringValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.DateTimeValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.BooleanValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.IntegerValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.jsonb.DoubleValueToJsonbConverter;
import com.torodb.torod.db.postgresql.converters.ValueConverter;
import com.google.common.collect.Maps;
import com.torodb.torod.core.exceptions.ToroImplementationException;
import com.torodb.torod.core.subdocument.BasicType;
import com.torodb.torod.core.subdocument.values.*;
import com.torodb.torod.db.postgresql.converters.jsonb.*;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 *
 */
public class ValueToArrayConverterProvider {

    private final Map<BasicType, ValueConverter> converters;
    private final ValueConverter<JsonArray, ArrayValue> arrayConverter;
    private final ValueConverter<Boolean, BooleanValue> booleanConverter;
    private final ValueConverter<String, DateValue> dateConverter;
    private final ValueConverter<String, DateTimeValue> dateTimeConverter;
    private final DoubleValueToJsonbConverter doubleConverter;
    private final ValueConverter<Number, IntegerValue> integerConverter;
    private final ValueConverter<Number, LongValue> longConverter;
    private final ValueConverter<Void, NullValue> nullConverter;
    private final ValueConverter<String, StringValue> stringConverter;
    private final ValueConverter<String, TimeValue> timeConverter;
    private final ValueConverter<String, TwelveBytesValue> twelveBytesConverter;
    private final ValueConverter<String, PatternValue> posixPatterConverter;

    private ValueToArrayConverterProvider() {
        arrayConverter = new ArrayValueToJsonbConverter();
        booleanConverter = new BooleanValueToJsonbConverter();
        dateConverter = new DateValueToJsonbConverter();
        dateTimeConverter = new DateTimeValueToJsonbConverter();
        doubleConverter = new DoubleValueToJsonbConverter();
        integerConverter = new IntegerValueToJsonbConverter();
        longConverter = new LongValueToJsonbConverter();
        nullConverter = new NullValueToJsonbConverter();
        stringConverter = new StringValueToJsonbConverter();
        timeConverter = new TimeValueToJsonbConverter();
        twelveBytesConverter = new TwelveBytesToArrayConverter();
        posixPatterConverter = new PosixPatternValueToJsonbConverter();

        converters = Maps.newEnumMap(BasicType.class);
        converters.put(BasicType.ARRAY, arrayConverter);
        converters.put(BasicType.BOOLEAN, booleanConverter);
        converters.put(BasicType.DATE, dateConverter);
        converters.put(BasicType.DATETIME, dateTimeConverter);
        converters.put(BasicType.DOUBLE, doubleConverter);
        converters.put(BasicType.INTEGER, integerConverter);
        converters.put(BasicType.LONG, longConverter);
        converters.put(BasicType.NULL, nullConverter);
        converters.put(BasicType.STRING, stringConverter);
        converters.put(BasicType.TIME, timeConverter);
        converters.put(BasicType.TWELVE_BYTES, twelveBytesConverter);
        converters.put(BasicType.PATTERN, posixPatterConverter);
    }

    public static ValueToArrayConverterProvider getInstance() {
        return ToArrayConverterHolder.INSTANCE;
    }

    @Nonnull
    public ValueConverter getConverter(BasicType valueType) {
        ValueConverter converter = converters.get(valueType);
        if (converter == null) {
            throw new AssertionError("There is no converter that converts "
                    + "elements of type " + valueType);
        }
        return converter;
    }

    @Nonnull
    public Value<?> convertFromJson(JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
            case ARRAY:
                assert jsonValue instanceof JsonArray;
                return arrayConverter.toValue((JsonArray) jsonValue);
            case TRUE:
                return booleanConverter.toValue(true);
            case FALSE:
                return booleanConverter.toValue(false);
            case NULL:
                return nullConverter.toValue(null);
            case NUMBER:
                assert jsonValue instanceof JsonNumber;
                JsonNumber number = (JsonNumber) jsonValue;
                if (number.isIntegral()) {
                    try {
                        long l = number.longValueExact();
                        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                            return longConverter.toValue(number.longValueExact());
                        }
                        return integerConverter.toValue(number.intValueExact());
                    }
                    catch (ArithmeticException ex) {
                        throw new ToroImplementationException(
                                "Unexpected integral value. " + number + " is "
                                + "bigger than long values"
                        );
                    }
                }
                return doubleConverter.toValue(number.doubleValue());
            case STRING:
                assert jsonValue instanceof JsonString;
                return stringConverter.toValue(((JsonString) jsonValue).getString());
            case OBJECT:
            default:
                throw new IllegalArgumentException("Instances of '"
                        + jsonValue.getClass() + "' like '" + jsonValue
                        + "' are not supported");
        }
    }

    public ValueConverter<JsonArray, ArrayValue> getArrayConverter() {
        return arrayConverter;
    }

    public ValueConverter<Boolean, BooleanValue> getBooleanConverter() {
        return booleanConverter;
    }

    public ValueConverter<String, DateValue> getDateConverter() {
        return dateConverter;
    }

    public ValueConverter<String, DateTimeValue> getDateTimeConverter() {
        return dateTimeConverter;
    }

    public DoubleValueToJsonbConverter getDoubleConverter() {
        return doubleConverter;
    }

    public ValueConverter<Number, IntegerValue> getIntegerConverter() {
        return integerConverter;
    }

    public ValueConverter<Number, LongValue> getLongConverter() {
        return longConverter;
    }

    public ValueConverter<Void, NullValue> getNullConverter() {
        return nullConverter;
    }

    public ValueConverter<String, StringValue> getStringConverter() {
        return stringConverter;
    }

    public ValueConverter<String, TimeValue> getTimeConverter() {
        return timeConverter;
    }

    public ValueConverter<String, TwelveBytesValue> getTwelveBytesConverter() {
        return twelveBytesConverter;
    }
    
    public ValueConverter<String, PatternValue> getPosixConverter() {
        return posixPatterConverter;
    }

    private static class ToArrayConverterHolder {

        private static final ValueToArrayConverterProvider INSTANCE
                = new ValueToArrayConverterProvider();
    }

    //@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD")
    private Object readResolve() {
        return ValueToArrayConverterProvider.getInstance();
    }
}
