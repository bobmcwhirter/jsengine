package org.dynjs.runtime;

import java.util.HashMap;
import java.util.Map;

public class PropertyDescriptor {

    private static final Map<String, Object> DEFAULTS = new HashMap<String, Object>() {
        {
            put( "Value", Types.UNDEFINED );
            put( "Set", Types.UNDEFINED );
            put( "Get", Types.UNDEFINED );
            put( "Writable", false );
            put( "Configurable", false );
            put( "Enumerable", false );
        }
    };

    private Map<String, Object> attributes = new HashMap<String, Object>();

    public static PropertyDescriptor newAccessorPropertyDescriptor(final boolean withDefaults) {
        return new PropertyDescriptor() {
            {
                if (withDefaults) {
                    set( "Value", DEFAULTS.get( "Value" ) );
                    set( "Writable", DEFAULTS.get( "Writable" ) );
                    set( "Configurable", DEFAULTS.get( "Configurable" ) );
                    set( "Enumerable", DEFAULTS.get( "Enumerable" ) );
                }
            }
        };
    }

    public static PropertyDescriptor newDataPropertyDescriptor(final boolean withDefaults) {
        return new PropertyDescriptor() {
            {
                if (withDefaults) {
                    set( "Set", DEFAULTS.get( "Set" ) );
                    set( "Get", DEFAULTS.get( "Get" ) );
                    set( "Configurable", DEFAULTS.get( "Configurable" ) );
                    set( "Enumerable", DEFAULTS.get( "Enumerable" ) );
                }
            }
        };
    }

    public PropertyDescriptor() {
    }
    
    public String toString() {
        return "[PropertyDescriptor: attributes=" + this.attributes + "]";
    }

    public boolean isWritable() {
        Object v = get( "Writable" );
        if (v == null) {
            return false;
        }
        return (Boolean) v;
    }

    public void setWritable(boolean writable) {
        set( "Writable", writable );
    }

    public boolean isConfigurable() {
        Object v = get( "Configurable" );
        if (v == null) {
            return false;
        }
        
        if ( v == Types.UNDEFINED ) {
            return false;
        }

        return (Boolean) v;
    }
    
    public boolean hasConfigurable() {
        return ( get( "Configurable" ) != null );
    }

    public void setConfigurable(boolean configurable) {
        set( "Configurable", configurable );
    }

    public boolean isEnumerable() {
        Object v = get( "Enumerable" );
        if (v == null) {
            return false;
        }

        return (Boolean) v;
    }

    public void setEnumerable(boolean enumerable) {
        set( "Enumerable", enumerable );
    }
    
    public boolean hasEnumerable() {
        return ( get( "Enumerable" ) != null );
    }

    public Object getValue() {
        return get( "Value" );
    }

    public void setValue(Object value) {
        set( "Value", value );
    }

    public Object getSetter() {
        return get( "Set" );
    }

    public void setSetter(JSFunction setter) {
        set( "Set", setter );
    }

    public Object getGetter() {
        return get( "Get" );
    }

    public void setGetter(JSFunction getter) {
        set( "Get", getter );
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public void set(String name, Object value) {
        this.attributes.put( name, value );
    }

    public Object get(String name) {
        Object v = this.attributes.get( name );
        if (v == null) {
            return Types.UNDEFINED;
        }

        return v;
    }

    public boolean isPresent(String name) {
        return this.attributes.containsKey( name );
    }

    public Object getWithDefault(String name) {
        Object v = this.attributes.get( name );
        if (v == null) {
            return DEFAULTS.get( name );
        }

        return v;
    }

    public PropertyDescriptor duplicate(String... attributes) {
        PropertyDescriptor d = new PropertyDescriptor();
        for (int i = 0; i < attributes.length; ++i) {
            d.set( attributes[i], get( attributes[i] ) );
        }
        return d;
    }

    public PropertyDescriptor duplicateWithDefaults(String... attributes) {
        PropertyDescriptor d = new PropertyDescriptor();
        for (int i = 0; i < attributes.length; ++i) {
            d.set( attributes[i], getWithDefault( attributes[i] ) );
        }
        return d;
    }

    public void copyAll(PropertyDescriptor from) {
        attributes.putAll( from.attributes );
    }

    public boolean isAccessorDescriptor() {
        // 8.10.1
        if (attributes.get( "Get" ) == null && attributes.get( "Set" ) == null) {
            return false;
        }

        return true;
    }

    public boolean isDataDescriptor() {
        // 8.10.2
        if (attributes.get( "Value" ) == null && attributes.get( "Writable" ) == null) {
            return false;
        }

        return true;
    }

    public boolean isGenericDescriptor() {
        // 8.10.3
        if (isAccessorDescriptor() == false && isDataDescriptor() == false) {
            return true;
        }

        return false;
    }

}
