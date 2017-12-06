// DefineInterface.aidl
package com.schaffer.base;
import com.schaffer.base.IMyAidlInterface;
import com.schaffer.base.test.Book;
// Declare any non-default types here with import statements

interface DefineInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
     void myInterface(in IMyAidlInterface inter);

     List<Book> getList();

     void setBinderDeath(IBinder binder);
}
