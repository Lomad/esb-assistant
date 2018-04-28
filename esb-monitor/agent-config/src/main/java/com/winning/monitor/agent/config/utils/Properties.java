package com.winning.monitor.agent.config.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class Properties {

    public Properties() {
    }

    public static Properties.StringPropertyAccessor forString() {
        return new Properties.StringPropertyAccessor();
    }

    public interface PropertyProvider {
        Object getProperty(String var1);
    }

    public static class SystemPropertyProvider implements Properties.PropertyProvider {
        private boolean m_properties;
        private boolean m_env;
        private String m_name;

        public SystemPropertyProvider(boolean properties, boolean env) {
            this.m_properties = properties;
            this.m_env = env;
        }

        @Override
        public Object getProperty(String name) {
            String value = null;
            if (this.m_name != null) {
                name = this.m_name;
            }

            if (value == null && this.m_properties) {
                value = System.getProperty(name);
            }

            if (value == null && this.m_env) {
                value = System.getenv(name);
            }

            return value;
        }

        public Properties.SystemPropertyProvider setName(String name) {
            this.m_name = name;
            return this;
        }
    }

    public static class StringPropertyAccessor extends Properties.PropertyAccessor<String> {
        public StringPropertyAccessor() {
        }

        @Override
        public String getProperty(String name, String defaultValue) {
            Object value = name == null ? null : this.getProperty(name);
            return value == null ? defaultValue : value.toString();
        }
    }

    public abstract static class PropertyAccessor<T> {
        private List<PropertyProvider> m_providers = new ArrayList();

        public PropertyAccessor() {
        }

        public Properties.PropertyAccessor<T> fromEnv() {
            return this.fromEnv((String) null);
        }

        public Properties.PropertyAccessor<T> fromEnv(String name) {
            this.m_providers.add((new Properties.SystemPropertyProvider(false, true)).setName(name));
            return this;
        }

        public Properties.PropertyAccessor<T> fromMap(Map<String, T> map) {
            return this.fromMap(map, (String) null);
        }

        public Properties.PropertyAccessor<T> fromMap(Map<String, T> map, String name) {
            this.m_providers.add((new Properties.MapPropertyProvider(map)).setName(name));
            return this;
        }

        public Properties.PropertyAccessor<T> fromSystem() {
            return this.fromSystem((String) null);
        }

        public Properties.PropertyAccessor<T> fromSystem(String name) {
            this.m_providers.add((new Properties.SystemPropertyProvider(true, false)).setName(name));
            return this;
        }

        protected Object getProperty(String name) {
            Object value = null;
            Iterator var3 = this.m_providers.iterator();

            while (var3.hasNext()) {
                Properties.PropertyProvider provider = (Properties.PropertyProvider) var3.next();
                value = provider.getProperty(name);
                if (value != null) {
                    break;
                }
            }

            return value;
        }

        public abstract T getProperty(String var1, T var2);
    }

    public static class MapPropertyProvider<T> implements Properties.PropertyProvider {
        private String m_name;
        private Map<String, T> m_map;

        public MapPropertyProvider(Map<String, T> map) {
            this.m_map = map;
        }

        @Override
        public Object getProperty(String name) {
            Object value = null;
            if (this.m_name != null) {
                name = this.m_name;
            }

            if (value == null && this.m_map != null) {
                value = this.m_map.get(name);
            }

            return value;
        }

        public Properties.MapPropertyProvider<T> setName(String name) {
            this.m_name = name;
            return this;
        }
    }
}
