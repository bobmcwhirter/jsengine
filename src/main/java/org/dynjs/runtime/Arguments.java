package org.dynjs.runtime;

import org.dynjs.exception.TypeError;

public class Arguments extends DynObject {

    private JSObject map;

    public Arguments() {
        setClassName( "Arguments" );
        this.map = new DynObject();
    }

    public void setParameterMap(JSObject map) {
        this.map = map;
    }

    public JSObject getParameterMap() {
        return this.map;
    }

    @Override
    public Object get(ExecutionContext context, String name) {
        // 10.6 [[Get]]
        Object d = this.map.getOwnProperty( context, name );
        if (d == Types.UNDEFINED) {
            Object v = super.get( context, name );
            if (name.equals( "caller" ) && (v instanceof JSFunction) && ((JSFunction) v).isStrict()) {
                throw new TypeError();
            }
            return v;
        }
        return this.map.get( context, name );
    }

    @Override
    public Object getOwnProperty(ExecutionContext context, String name) {
        // 10.6 [[GetOwnProperty]]
        Object d = super.getOwnProperty( context, name );
        if (d == Types.UNDEFINED) {
            return d;
        }

        d = this.map.getOwnProperty( context, name );
        if (d != Types.UNDEFINED) {
            PropertyDescriptor desc = (PropertyDescriptor) d;
            desc.setValue( this.map.get( context, name ) );
        }

        return d;
    }

    @Override
    public boolean defineOwnProperty(ExecutionContext context, String name, PropertyDescriptor desc, boolean shouldThrow) {
        // 10.6 [[DefineOwnProperty]]
        boolean allowed = super.defineOwnProperty( context, name, desc, false );
        if (!allowed) {
            return reject( shouldThrow );
        }

        if (this.map.getOwnProperty( context, name ) != Types.UNDEFINED) {
            if (desc.isAccessorDescriptor()) {
                this.map.delete( context, name, false );
            } else {
                if (desc.isPresent( "Value" )) {
                    this.map.put( context, name, desc.getValue(), shouldThrow );
                }
                if (desc.isPresent( "Writable" ) && !desc.isWritable()) {
                    this.map.delete( context, name, false );
                }
            }

        }

        return true;
    }

    @Override
    public boolean delete(ExecutionContext context, String name, boolean shouldThrow) {
        // 10.6 [[Delete]]
        boolean result = super.delete( context, name, shouldThrow );
        if (result && (this.map.getOwnProperty( context, name ) != Types.UNDEFINED)) {
            this.map.delete( context, name, false );
        }
        return result;
    }

}
