package p000;

import java.io.Serializable;

/* JADX INFO: loaded from: /home/ubuntu/apk_source_work/raw/classes.dex */
public class javalangEnum implements Comparable, Serializable {
    private final String name;
    private final int ordinal;

    public final String name() {
        return this.name;
    }

    public final int ordinal() {
        return this.ordinal;
    }

    protected javalangEnum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public String toString() {
        return this.name;
    }

    public final boolean equals(Object other) {
        return this == other;
    }

    public final int hashCode() {
        return System.identityHashCode(this);
    }

    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public int compareTo(javalangEnum o) {
        return compareTo((Object) o);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        javalangEnum other = (javalangEnum) o;
        if (getDeclaringClass() != other.getDeclaringClass()) {
            throw new ClassCastException();
        }
        return this.ordinal - other.ordinal;
    }

    public final Class getDeclaringClass() {
        Class<?> cls = getClass();
        Class<? super Object> superclass = cls.getSuperclass();
        try {
            return superclass == Class.forName("javalangEnum") ? cls : superclass;
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static javalangEnum valueOf(Class enumType, String name) {
        try {
            if (enumType.getSuperclass() != Class.forName("javalangEnum")) {
                throw new IllegalArgumentException(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("No enum const ").append(enumType).toString()).append(".").toString()).append(name).toString());
            }
            try {
                Class<?>[] clsArr = new Class[1];
                try {
                    clsArr[0] = Class.forName("java.lang.String");
                    return (javalangEnum) enumType.getMethod("valueOf", clsArr).invoke(null, name);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            } catch (Throwable th) {
                throw new IllegalArgumentException(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("No enum const ").append(enumType).toString()).append(".").toString()).append(name).toString());
            }
        } catch (ClassNotFoundException e2) {
            throw new NoClassDefFoundError(e2.getMessage());
        }
    }
}
